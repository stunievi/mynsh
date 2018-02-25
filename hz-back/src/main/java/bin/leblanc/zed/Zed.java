package bin.leblanc.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import bin.leblanc.zed.exception.NoMethodException;
import bin.leblanc.zed.exception.NoPermissionException;
import bin.leblanc.zed.metadata.ICheckPermission;
import bin.leblanc.zed.metadata.IPermission;
import bin.leblanc.zed.metadata.RoleEntity;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
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
    protected Set<ICheckPermission> roleHandlers = new LinkedHashSet();


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
    public void addRoleHandler(ICheckPermission checkPermission){
        roleHandlers.add(checkPermission);
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

    @Transactional
    public Object test(User u){
        Object o = null;
        o = entityManager.merge(u);
        return o;
    }
}
