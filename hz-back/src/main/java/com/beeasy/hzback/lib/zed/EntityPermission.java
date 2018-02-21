package com.beeasy.hzback.lib.zed;

import lombok.Getter;

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


}
