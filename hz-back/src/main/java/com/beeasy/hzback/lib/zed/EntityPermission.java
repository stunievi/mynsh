package com.beeasy.hzback.lib.zed;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class EntityPermission {

    private Class entityClass;
    @Getter
    private boolean get = false;
    @Getter
    private boolean post = false;
    @Getter
    private boolean put = false;
    @Getter
    private boolean delete = false;

    @Getter
    private Set<String> getReturnFields = new HashSet<>();

    EntityPermission(Class clz){
        entityClass = clz;
    }


    public EntityPermission allowGet(){
        get = true;
        return this;
    }

    public EntityPermission allowPost(){
        post = true;
        return this;
    }
    public EntityPermission allowPut(){
        put = true;
        return this;
    }
    public EntityPermission allowDelete(){
        delete = true;
        return this;
    }

    public EntityPermission addGetCondition(){

        return this;
    }

    /**
     * 限制返回字段
     */
    public EntityPermission setGetReturnFields(Set<String> fields){
        getReturnFields = fields;
        return this;
    }
    public EntityPermission setGetReturnFields(List<String> fields){
        getReturnFields = new LinkedHashSet<>(fields);
        return this;
    }
    public EntityPermission setGetReturnFields(String[] fields){
        return setGetReturnFields(Arrays.asList(fields));
    }




}
