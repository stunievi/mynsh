package com.beeasy.hzback.lib.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.lib.zed.metadata.ICheckPermission;
import com.beeasy.hzback.lib.zed.metadata.IPermission;
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

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
import java.util.*;


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
    static Map<String, Class<?>> entityMap = new HashMap<>();

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
        JSONObject obj = JSON.parseObject(json);
        if (obj == null) {
            throw new Exception();
        }
        return parseSingle(obj);
    }

    
    public Map<?,?> parseSingle(JSONObject obj) throws Exception {
        //删除被影响的字段
        String method = obj.getString(METHOD);
        obj.remove(METHOD);

        if (method == null) {
            method = "get";
        }
        method = method.trim().toLowerCase();

        Map<?,?> result = null;

        switch (method) {
            case GET:
                result = this.parseGet(obj);
                log.info(JSON.toJSONString(result));
                return (result);

            case POST:
                result = parsePost(obj);
                log.info(JSON.toJSONString(result));
                return (result);

            case PUT:
                result = parsePut(obj);
                log.info(JSON.toJSONString(result));
                return (result);

            case DELETE:
                result = parseDelete(obj);
                log.info(JSON.toJSONString(result));
                return (result);

        }

        throw  new Exception();
    }

    public Result parse(String json,Object token){
        JSONObject obj = JSON.parseObject(json);
        if (obj == null) {
            return Result.error();
        }
        try{
            return Result.ok(parseSingle(obj));
        }
        catch (Exception e){
            return Result.error();
        }
    }

    public Result parse(String json) throws Exception {
        return parse(json,"su");
    }

    public void addRole(String roleName, ICheckPermission checkFunc, IPermission permissionFunc){

    }


    public static void register(Class<?> clz) {

    }

    public static void addRole() {

    }

    public Map<String, Object> parseGet(JSONObject obj) throws Exception {
        return sqlUtil.select(obj);
    }


    public Map<String,Boolean> parseDelete(JSONObject obj) throws Exception{
        return sqlUtil.delete(obj);
    }


    public Map<String,Boolean> parsePut(JSONObject obj) throws Exception{
        return sqlUtil.put(obj);
    }


    public Map<String,Object> parsePost(JSONObject obj) throws Exception {
        return sqlUtil.post(obj);
    }

    @Transactional
    public Object test(User u){
        Object o = null;
        o = entityManager.merge(u);
        return o;
    }
}
