package com.beeasy.hzback.lib.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
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

    final static String GET = "get";
    final static String POST = "post";
    final static String PUT = "put";
    final static String DELETE = "delete";

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

    public Result parse(String json) throws Exception {
        JSONObject obj = JSON.parseObject(json);
        if (obj == null) {
            throw new Exception();
        }
        String method = obj.getString("method");
        if (method == null) {
            method = "get";
        }
        method = method.trim().toLowerCase();
        switch (method) {
            case GET:
                Map<?, ?> result = this.parseGet(obj);
                log.info(JSON.toJSONString(result));
                return Result.ok(result);

            case POST:
                break;

            case PUT:
                break;

            case DELETE:
                break;


        }

        return Result.error();
    }


    public static void register(Class<?> clz) {

    }

    public static void addRole() {

    }

    public Map<String, Object> parseGet(JSONObject obj) throws Exception {
        return sqlUtil.select(obj);
    }


    public void parseDelete() {

    }
}
