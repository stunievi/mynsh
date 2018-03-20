package bin.leblanc.zed;

import bin.leblanc.zed.proxy.MethodFile;
import bin.leblanc.zed.proxy.ZedProxyHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import bin.leblanc.zed.exception.NoMethodException;
import bin.leblanc.zed.exception.NoPermissionException;
import bin.leblanc.zed.metadata.ICheckPermission;
import bin.leblanc.zed.metadata.IPermission;
import bin.leblanc.zed.metadata.RoleEntity;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.YamlJsonParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.io.*;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class Zed {

    public final static String GET = "get";
    public final static String POST = "post";
    public final static String PUT = "put";
    public final static String DELETE = "delete";
    final static String METHOD = "method";

    @Autowired
    EntityManager entityManager;

    @Autowired
    SQLUtil sqlUtil;

    static boolean init = false;

    @Setter
    @Getter
    protected Map<String, Class<?>> entityMap = new HashMap<>();

    @Getter
    protected Map<String,RolePermission> roleMap = new HashMap<>();

    @Getter
    protected List<ICheckPermission> roleHandlers = new ArrayList<>();


    public void init() {
        if (init) {
            return;
        }
//        Set<String> clzs = ScanPackageUtil.findPackageAnnotationClass("com.beeasy", Entity.class);
        Set<EntityType<?>> set = entityManager.getMetamodel().getEntities();
        set.forEach(entityType -> {
            EntityTypeImpl entity = (EntityTypeImpl) entityType;
            entityMap.put(entity.getName(), entity.getBindableJavaType());
        });

        init = true;
    }

    public Map<?,?> parseSingle(String json) throws Exception {
        return parseSingle(json,"SU");
    }

    public Map<?,?> parseSingle(String json,Object token) throws Exception {
        JSONObject obj = JSON.parseObject(json);
        if (obj == null) {
            throw new Exception();
        }
        return parseSingle(obj,token);
    }

    public Map<?,?> parseSingle(JSONObject obj) throws Exception {
        return parseSingle(obj,"SU");
    }

    public Map<?,?> parseSingle(JSONObject obj,Object token) throws Exception {
        //删除被影响的字段
        String method = obj.getString(METHOD);
        obj.remove(METHOD);

        if (method == null) {
            method = GET;
        }
        method = method.trim().toLowerCase();

        Map<?,?> result;
        Set<RoleEntity> roleEntities = authRole(token);

        //过滤掉不存在的权限
        String finalMethod = method;
        roleEntities = roleEntities.stream().filter(roleEntity ->{
            return !roleEntity.getRolePermission().getDisallowMap().containsKey(finalMethod);
        }).collect(Collectors.toSet());

        if(roleEntities.size() == 0){
            throw new NoPermissionException();
        }

        switch (method) {
            case GET:
                result = this.parseGet(obj,roleEntities);
                log.info(JSON.toJSONString(result));
                return (result);

            case POST:
                result = parsePost(obj,roleEntities);
                log.info(JSON.toJSONString(result));
                return (result);

            case PUT:
                result = parsePut(obj,roleEntities);
                log.info(JSON.toJSONString(result));
                return (result);

            case DELETE:
                result = parseDelete(obj,roleEntities);
                log.info(JSON.toJSONString(result));
                return (result);

        }

        throw  new NoMethodException();
    }

    public Map<?,?> parse(JSONObject json) throws Exception{
        return parseSingle(json,"SU");
    }

    public Map<?,?> parse(JSONObject json, Object token) throws Exception{
        return parseSingle(json,token);
    }

    public Map<?,?> parse(String json, Object token) throws Exception{
        JSONObject obj = JSON.parseObject(json);
        return parseSingle(obj,token);
    }

    public Map<?,?> parse(String json) throws Exception {
        return parse(json,"SU");
    }

    public void addRole(String roleName, IPermission permissionFunc){
        //禁止重复注册
        if(roleMap.containsKey(roleName)){
            return;
        }
        editRole(roleName,permissionFunc);
    }

    public void editRole(String roleName, IPermission permissionFunc){
        RolePermission rolePermission = new RolePermission();
        permissionFunc.call(rolePermission);
        rolePermission.setPermission(permissionFunc);
        rolePermission.setRoleName(roleName);
        this.roleMap.put(roleName,rolePermission);
    }

    /**
     * 增加授权信息
     * @param checkPermission
     */
    public int addRoleHandler(ICheckPermission checkPermission){
        roleHandlers.add(checkPermission);
        return roleHandlers.size() - 1;
    }

    /**
     * 在实际系统运行中需要动态授权，所以可以更改自己的授权方式
     */
    public boolean modifyRoleHandler(int index,ICheckPermission checkPermission){
        ICheckPermission roleHandler = roleHandlers.get(index);
        //如果没有就不能更改
        if(roleHandler == null){
            return false;
        }
        roleHandlers.set(index,checkPermission);
        return true;
    }


    protected Set<RoleEntity> authRole(Object token){
        //验证权限
        Set<RoleEntity> set = new LinkedHashSet<>();
        roleHandlers.forEach(roleHandler -> {
            String roleName = roleHandler.call(token);
            //如果是null 表示略过这个权限
            if(roleName == null){
                return;
            }
            //添加到权限组
            RoleEntity entity = new RoleEntity(token,roleMap.get(roleName));
            set.add(entity);
        });
        //至少保证有一个合法的权限
        if(set.size() == 0){
            RoleEntity entity = new RoleEntity(null,roleMap.get(RolePermission.UNKNOWN));
            set.add(entity);
        }
        return set;
    }

    public Map<String, Object> parseGet(JSONObject obj,Set<RoleEntity> roleEntities) throws Exception {
        return sqlUtil.select(obj,roleEntities);
    }


    public Map<String,Boolean> parseDelete(JSONObject obj,Set<RoleEntity> roleEntities) throws Exception{
        return sqlUtil.delete(obj,roleEntities);
    }


    public Map<String,Boolean> parsePut(JSONObject obj,Set<RoleEntity> roleEntities) throws Exception{
        return sqlUtil.put(obj,roleEntities);
    }


    public Map<String,Object> parsePost(JSONObject obj,Set<RoleEntity> roleEntities) throws Exception {
        return sqlUtil.post(obj,roleEntities);
    }


    /**
     * 需要打开 -parameters 参数！！！！！！！！！！
     * @param yaml
     * @param inter
     * @param <T>
     * @return
     */
    public synchronized static <T> T createProxy(String yaml, Class<T> inter){
        String template = null;
        Map<String,Object> obj = null;
        readFile: try {
            if(yaml == null){
                break readFile;
            }
            @Cleanup InputStream is = new FileInputStream(yaml);
            byte[] bytes = new byte[1024];
            int len;
            StringBuffer sb = new StringBuffer();
            while((len = is.read(bytes)) != -1){
                sb.append(new String(bytes,0,len));
            }
            template = sb.toString();
            Yaml parser = new Yaml();
            obj = (Map<String, Object>) parser.load(template);
        } catch (IOException e) {
            e.printStackTrace();
            obj = new HashMap<>();
        }
        return createProxy(obj,inter);
    }

    public static <T> T createProxy(Map template, Class<T> inter){
        ZedProxyHandler handler = new ZedProxyHandler(template);
        Class<T>[] inters = new Class[]{inter};
        return (T) Proxy.newProxyInstance(inter.getClassLoader(),inters,handler);
    }

    public static <T> T createProxy(Class<T> inter){
        MethodFile annotation = inter.getDeclaredAnnotation(MethodFile.class);
        String fileName = null;
        if(annotation != null){
            fileName = annotation.value();
        }
        return createProxy(fileName,inter);
    }

    @Transactional
    public Object test(User u){
        Object o = null;
        o = entityManager.merge(u);
        return o;
    }
}
