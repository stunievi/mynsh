package com.beeasy.hzback.lib.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;


@Slf4j
public class Zed {

    @Autowired
    EntityManager entityManager;

    static boolean init = false;

    static Map<String, Class<?>> entityMap = new HashMap<>();

    public void init() {
        if (init) {
            return;
        }
        Set<String> clzs = ScanPackageUtil.findPackageAnnotationClass("com.beeasy", Entity.class);
        try {
            for (String className : clzs) {
                Class<?> clz = Class.forName(className);
                entityMap.put(clz.getSimpleName(), clz);

                log.info(clz.getSimpleName());
            }


            init = true;
        } catch (Exception e) {

        }
    }

    public void parse(String json) throws Exception {
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
            case "get":
                this.parseGet(obj);
                break;
        }
    }


    public static void register(Class<?> clz) {

    }

    public static void addRole() {

    }

    public void parseGet(JSONObject obj) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        Set<String> entityKeys = obj.keySet();
        for (String entityKey : entityKeys) {
            log.info(entityKey);
            //不存在该表的情况直接略过
            if (!entityMap.containsKey(entityKey)) {
                log.info("no entity " + entityKey);
                continue;
            }
            //得到需要查询的表
            Class<?> clz = entityMap.get(entityKey);
            //得到需要查询的字段
            JSONObject item = obj.getJSONObject(entityKey);
            Set<String> keys = item.keySet();
            log.info(keys.toString());

            CriteriaQuery<?> query = cb.createQuery(clz);


            Root<?> root = query.from(clz);
            List<Predicate> predicateList = new ArrayList<>();
            //检查是否存在这个字段
            for (String fieldName : keys) {
                try {
                    Field field = clz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Path<?> path = root.get(fieldName);
//                    String s = item.getString(fieldName);
//                    Integer i = item.getInteger(fieldName);
//                    boolean b = item.get
                    predicateList.add(
                            cb.equal(path,item.getString(fieldName))
                    );

                    log.info("has this field");
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

            }



            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
//            query.select(root.get("id"));
            query.where(predicates);

            TypedQuery<?> q = entityManager.createQuery(query);
            List<?> result = q.getResultList();

            log.info(result.size() + "");

        }
//        cb.and()
//        SpringContextUtils.getContext().
    }
}
