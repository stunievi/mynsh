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
import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


@Slf4j
public class Zed {

    @Autowired
    EntityManager entityManager;

    static boolean init = false;

    static Map<String,Class<?>> entityMap = new HashMap<>();

    public void init(){
        if(init){
            return;
        }
        Set<String> clzs = ScanPackageUtil.findPackageAnnotationClass("com.beeasy", Entity.class);
        try{
            for(String className : clzs){
                Class<?> clz = Class.forName(className);
                entityMap.put(clz.getSimpleName(),clz);
            }

            init = true;
        }
        catch (Exception e){

        }
    }

    public void parse(String json) throws Exception {
            JSONObject obj = JSON.parseObject(json);
            if(obj == null){
                throw new Exception();
            }
            String method = obj.getString("method");
            if(method == null){
                method = "get";
            }
            method = method.trim().toLowerCase();
            switch (method){
                case "get":
                    this.parseGet(obj);
                    break;
            }
    }


    public static void register(Class<?> clz){

    }

    public static void addRole(){

    }

    private void parseGet(JSONObject obj){
        for(String key : obj.keySet()){
              log.info(key);
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        SpringContextUtils.getContext().
    }
}
