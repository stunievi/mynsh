package com.beeasy.hzback.lib.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;


@Slf4j
public class Zed {

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
            case "get":
                Map<?,?> result = this.parseGet(obj);
                log.info(JSON.toJSONString(result));
                return Result.ok(result);
        }

        return Result.error();
    }


    public static void register(Class<?> clz) {

    }

    public static void addRole() {

    }

    public Map<String,Object> parseGet(JSONObject obj) throws Exception {


        sqlUtil.select(obj);
        if(!!true){
            return new HashMap<>();
        }
        Set<String> entityKeys = obj.keySet();
        Map<String,Object> map = new HashMap<>();
        for (String entityKey : entityKeys) {
            boolean multipul = false;
            //单独查询
            if(entityKey.indexOf("[]") > -1){
                multipul = true;
                entityKey = entityKey.replace("[]","");
            }
            //多个查询
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

//            CriteriaUpdate<?> update = cb.createCriteriaUpdate(clz);
//            update.
            CriteriaQuery<?> query = sqlUtil.buildWhere(clz,item);
//                    cb.createQuery(clz);



//            Root<?> root = query.from(clz);
//            List<Predicate> predicateList = new ArrayList<>();
            //检查是否存在这个字段




//            query.select(root.get("id"));
//            query.where(predicates);

            TypedQuery<?> q = entityManager.createQuery(query);
            q.setFirstResult(100);
            q.setMaxResults(10);
            List<?> result = q.getResultList();

            if(multipul){

            }
            else{

            }


            map.put(entityKey,result);
            log.info(result.size() + "");
        }

        return map;
//        cb.and()
//        SpringContextUtils.getContext().
    }
}
