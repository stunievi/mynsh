package com.beeasy.hzback.lib.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.lib.zed.exception.NoMethodException;
import com.beeasy.hzback.lib.zed.exception.NoPermissionException;
import com.beeasy.hzback.lib.zed.metadata.ICheckPermission;
import com.beeasy.hzback.lib.zed.metadata.IPermission;
import com.beeasy.hzback.lib.zed.metadata.RoleEntity;
import com.beeasy.hzback.lib.zed.metadata.RoleMap;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
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
    protected static Map<String, Class<?>> entityMap = new HashMap<>();

    @Getter
    protected static Map<String,RolePermission> roleMap = new HashMap<>();


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

    public Result parse(String json,Object token){
        JSONObject obj = JSON.parseObject(json);
        if (obj == null) {
            return Result.error();
        }
        try{
            return Result.ok(parseSingle(obj,token));
        }
        catch (NoMethodException e){
            return Result.error("error method");
        }
        catch (NoPermissionException e){
            return Result.error("permission error");
        }
        catch (Exception e){
            return Result.error();
        }
    }

    public Result parse(String json) throws Exception {
        return parse(json,"SU");
    }

    public void addRole(String roleName, ICheckPermission checkFunc, IPermission permissionFunc){
        //禁止重复注册
        if(roleMap.containsKey(roleName)){
            return;
        }
        RolePermission rolePermission = new RolePermission();
        permissionFunc.func(rolePermission);
        rolePermission.setCheckPermission(checkFunc);
        rolePermission.setPermission(permissionFunc);
        rolePermission.setRoleName(roleName);
        this.roleMap.put(roleName,rolePermission);
    }


    protected Set<RoleEntity> authRole(Object token){
        //验证权限
        Set<RoleEntity> set = new LinkedHashSet<>();
        roleMap.forEach((roleName,rolePermission) -> {
            if(rolePermission.getCheckPermission().check(token)){
                RoleEntity entity = new RoleEntity();
                entity.setRolePermission(rolePermission);
                entity.setToken(token);
                set.add(entity);
            }
        });

        return set;
    }

    public Map<String, Object> parseGet(JSONObject obj,Set<RoleEntity> roleEntities) throws Exception {
        return sqlUtil.select(obj,roleEntities);
    }


    public Map<String,Boolean> parseDelete(JSONObject obj,Set<RoleEntity> roleEntities) throws Exception{
        return sqlUtil.delete(obj);
    }


    public Map<String,Boolean> parsePut(JSONObject obj,Set<RoleEntity> roleEntities) throws Exception{
        return sqlUtil.put(obj);
    }


    public Map<String,Object> parsePost(JSONObject obj,Set<RoleEntity> roleEntities) throws Exception {
        return sqlUtil.post(obj);
    }

    @Transactional
    public Object test(User u){
        Object o = null;
        o = entityManager.merge(u);
        return o;
    }
}
