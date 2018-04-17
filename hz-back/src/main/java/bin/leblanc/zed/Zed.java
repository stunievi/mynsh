package bin.leblanc.zed;

import bin.leblanc.zed.exception.NoMethodException;
import bin.leblanc.zed.exception.NoPermissionException;
import bin.leblanc.zed.metadata.IPermission;
import bin.leblanc.zed.metadata.RoleEntity;
import bin.leblanc.zed.proxy.MethodFile;
import bin.leblanc.zed.proxy.ZedProxyHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.modules.system.entity.User;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Function;


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
    protected List<Function<Object,String>> roleHandlers = new ArrayList<>();


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
        RoleEntity roleEntity = authRole(token);

        //过滤掉不存在的权限
        if(roleEntity.getRolePermission().getDisallowMap().containsKey(method)){
            throw new NoPermissionException();
        }

        switch (method) {
            case GET:
                result = this.parseGet(obj,roleEntity);
                log.info(JSON.toJSONString(result));
                return (result);

//            case POST:
//                result = parsePost(obj,roleEntities);
//                log.info(JSON.toJSONString(result));
//                return (result);
//
//            case PUT:
//                result = parsePut(obj,roleEntities);
//                log.info(JSON.toJSONString(result));
//                return (result);
//
//            case DELETE:
//                result = parseDelete(obj,roleEntities);
//                log.info(JSON.toJSONString(result));
//                return (result);

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
    public void addRoleHandler(Function<Object,String> checkPermission){
        roleHandlers.add(checkPermission);
    }

    /**
     * 在实际系统运行中需要动态授权，所以可以更改自己的授权方式
     */
//    public boolean modifyRoleHandler(int index,ICheckPermission checkPermission){
//        ICheckPermission roleHandler = roleHandlers.get(index);
//        //如果没有就不能更改
//        if(roleHandler == null){
//            return false;
//        }
//        roleHandlers.set(index,checkPermission);
//        return true;
//    }


    protected RoleEntity authRole(Object token){
        for (Function<Object, String> roleHandler : roleHandlers) {
            String roleName = roleHandler.apply(token);
            if(roleName != null){
                return new RoleEntity(token,roleMap.get(roleName));
            }
        }
        return new RoleEntity(null,roleMap.get(RolePermission.UNKNOWN));
    }

    public Map<String, Object> parseGet(JSONObject obj,RoleEntity roleEntity) throws Exception {
        return sqlUtil.select(obj,roleEntity);
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
        Map<String,Object> obj = null;
        readFile: try {
            if(yaml == null){
                break readFile;
            }
            File file;
            if(yaml.contains("classpath")){
                file = ResourceUtils.getFile(yaml);
            }
            else{
                file = new File(yaml);
            }
            @Cleanup Reader reader = new FileReader(file);
//            @Cleanup InputStream is = new FileInputStream(file);
//            byte[] bytes = new byte[1024];
//            int len;
//            StringBuffer sb = new StringBuffer();
//            while((len = is.read(bytes)) != -1){
//                sb.append(new String(bytes,0,len));
//            }
//            template = sb.toString();
            Yaml parser = new Yaml();
            obj = (Map<String, Object>) parser.load(reader);
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
