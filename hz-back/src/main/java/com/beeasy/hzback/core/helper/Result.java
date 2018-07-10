package com.beeasy.hzback.core.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Result <T> {
    protected boolean success;
    protected String errMessage = "";
    protected T data;

    public T orElse(T elseValue){
        return isSuccess() ? getData() : elseValue;
    }

    public T orElseGet(ElseGetFunction<T> func){
        return isSuccess() ? getData() : func.apply();
    }

    public interface ElseGetFunction<T>{
        T apply();
    }

    public Result<T> map(){ return this;}
    public Result<T> flatMap(){ return this;}

    protected Result(){}

    public static Result ok(){
        Result result = new Result();
        result.setSuccess(true);
        return result;
    }
    public static Result ok(Object item){
        Result result = ok();
        result.setData(item);
        return result;
    }


    public static Result error(Set<String> errMsgs){
        Result result = error();
        result.setErrMessage(StringUtils.join(errMsgs.toArray(),","));
        return result;
    }
    public static Result error(String ...errMsgs){
        return error(new HashSet<>(Arrays.asList(errMsgs)));
    }
    public static Result error(BindingResult item){
        Result result = error();
            Set<String> stringSet = ((BindingResult) item).getAllErrors().stream().map(i -> i.getDefaultMessage()).collect(Collectors.toSet());
            return error(stringSet);
    }
    public static Result error(){
        Result result = new Result();
        result.setSuccess(false);
        return result;
    }

    public static Result finish(String msg){
        if(StringUtils.isEmpty(msg)){
            return Result.ok();
        }
        return Result.error(msg);
    }
    public static Result finish(boolean b){
        return b ? ok() : error();
    }
    public static Result finish(Optional optional){
        return optional.isPresent() ? ok(optional.get()) : error();
    }
    public static Result finish(boolean flag, Object object){
        return flag ? ok(object) : error();
    }


    @Data
    @AllArgsConstructor
    public static abstract class Entry{
        protected Class clz;
        protected List<String> fields;
        public Entry(Class clz, String ...fields){
            this.clz = clz;
            this.fields = Arrays.asList(fields);
        }
    }

    @Data
    public static class DisallowEntry extends Entry{
        public DisallowEntry(Class clz, String ...fields) {
            super(clz,fields);
        }
    }

    @Data
    public static class AllowEntry extends Entry{
        public AllowEntry(Class clz, String ...fields) {
            super(clz,fields);
        }
    }

    public String toJson(){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setHeader("content-type","application/json");
        return JSON.toJSONString(this);
    }

    public String toJson(Entry...entries) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        if(null == response){
            return "";
        }
        response.setHeader("content-type","application/json");
        PropertyFilter propertyFilter = (source, name, value) -> {
            for (Entry entry : entries) {
                if(source.getClass().equals(entry.getClz()) && entry.getFields().contains(name)){
                    return entry instanceof AllowEntry;
                }
            }
            return true;
        };
        return JSON.toJSONString(this,propertyFilter);
    }

    private String encode(String str){
        return Utils.getCurrentUserPrivateKey().map(key -> {
            try {
                return RSAUtils.privateEncrypt(str, RSAUtils.getPrivateKey(key));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
            return null;
        }).orElse(Result.error().toJson());
    }

    public String toMobile(){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setHeader("content-type","application/json");
        String str = JSON.toJSONString(this);
//        return str;
        return encode(str);
    }
    public String toMobile(DisallowEntry...entries){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setHeader("content-type","application/json");
        PropertyFilter propertyFilter = (source, name, value) -> {
            for (DisallowEntry entry : entries) {
                if(source.getClass().equals(entry.getClz()) && entry.getFields().contains(name)){
                    return false;
                }
            }
            return true;
        };
        String str =  JSON.toJSONString(this,propertyFilter);
//        return str;
        return encode(str);
    }

//    public static String okJson(Object obj){
//        return okJson(obj,new DisallowEntry[0]);
//    }
    public static String okJson(Object obj, DisallowEntry...entries){
        return Result.ok(obj).toJson(entries);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
