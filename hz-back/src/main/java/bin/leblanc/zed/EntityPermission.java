package bin.leblanc.zed;

import bin.leblanc.zed.metadata.IGetWhereLimit;
import lombok.Getter;

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

    @Getter
    private Set<String> requiredWhereFields = new HashSet<>();

    @Getter
    private Set<String> uniqueWhereFields = new HashSet<>();

    @Getter
    private IGetWhereLimit getWhereLimit = null;
    @Getter
    private IGetWhereLimit putWhereLimit = null;
    @Getter
    private IGetWhereLimit deleteWhereLimit = null;


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

    /**
     * 限制提交的condition字段（必须提交的字段）
     */
    public EntityPermission setRequiredWhereFields(Set<String> fields){
        requiredWhereFields = fields;
        return this;
    }
    public EntityPermission setRequiredWhereFields(List<String> fields){
        requiredWhereFields = new HashSet<>(fields);
        return this;
    }
    public EntityPermission setRequiredWhereFields(String[] fields){
        return setRequiredWhereFields(Arrays.asList(fields));
    }

    /**
     * 限制提交的condition字段（只能提交的字段）
     */
    public EntityPermission setUniqueWhereFields(Set<String> fields){
        uniqueWhereFields = fields;
        return this;
    }
    public EntityPermission setUniqueWhereFields(List<String> fields){
        uniqueWhereFields = new HashSet<>(fields);
        return this;
    }
    public EntityPermission setUniqueWhereFields(String[] fields){
        setUniqueWhereFields(Arrays.asList(fields));
        return this;
    }

    /**
     * 限制GET提交条件
     */
    public EntityPermission setGetWhereLimit(IGetWhereLimit whereLimit){
        this.getWhereLimit = whereLimit;
        return this;
    }


    /**
     * 限制PUT DELETE id条件
     */
    public EntityPermission setPutIdLimit(IGetWhereLimit whereLimit){
        putWhereLimit = whereLimit;
        return this;
    }

    public EntityPermission setDeleteLimit(IGetWhereLimit whereLimit){
        deleteWhereLimit = whereLimit;
        return this;
    }



}
