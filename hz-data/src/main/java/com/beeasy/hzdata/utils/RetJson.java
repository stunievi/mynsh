package com.beeasy.hzdata.utils;


import act.util.SimpleBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import org.osgl.mvc.result.RenderJSON;

import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class RetJson<T> implements SimpleBean {
    public boolean success;
    public String errMessage = "";
    public T data;

    public interface ElseGetFunction<T> {
        T apply();
    }

    public RetJson<T> map() {
        return this;
    }

    public RetJson<T> flatMap() {
        return this;
    }

    protected RetJson() {
    }

    public static RetJson ok() {
        RetJson retJson = new RetJson();
        retJson.success = true;
        return retJson;
    }

    public static RetJson ok(Object item) {
        RetJson retJson = ok();
        retJson.data = (item);
        return retJson;
    }


    public static RetJson error(Set<String> errMsgs) {
        RetJson retJson = error();
        retJson.errMessage = String.join(",", errMsgs);
//        RetJson.setErrMessage(StringUtils.join(errMsgs.toArray(), ","));
        return retJson;
    }

    public static RetJson error(String... errMsgs) {
        return error(new HashSet<>(Arrays.asList(errMsgs)));
    }

//    public static RetJson error(BindingRetJson item) {
//        RetJson RetJson = error();
//        Set<String> stringSet = ((BindingRetJson) item).getAllErrors().stream().map(i -> i.getDefaultMessage()).collect(Collectors.toSet());
//        return error(stringSet);
//    }

    public static RetJson error() {
        RetJson retJson = new RetJson();
        retJson.success = false;
        return retJson;
    }

    public static RetJson finish(final String msg) {
        if(msg.isEmpty()){
            return RetJson.ok();
        }
//        if (StringUtils.isEmpty(msg)) {
//            return RetJson.ok();
//        }
        return RetJson.error(msg);
    }

    public static RetJson finish(boolean b) {
        return b ? ok() : error();
    }

    public static RetJson finish(Optional optional) {
        return optional.isPresent() ? ok(optional.get()) : error();
    }

    public static RetJson finish(boolean flag, Object object) {
        return flag ? ok(object) : error();
    }


    public static abstract class Entry {
        public Class clz;
        public List<String> fields;

        public Entry(Class clz, String... fields) {
            this.clz = clz;
            this.fields = Arrays.asList(fields);
        }
    }

    public static class DisallowEntry extends Entry {
        public DisallowEntry(Class clz, String... fields) {
            super(clz, fields);
        }
    }

    public static class AllowEntry extends Entry {
        public AllowEntry(Class clz, String... fields) {
            super(clz, fields);
        }
    }

    public String toJson() {
//        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//        response.setHeader("content-type", "application/json");
        return JSON.toJSONString(this);
    }

    public String toJson(Entry... entries) {
//        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//        if (null == response) {
//            return "";
//        }
//        response.setHeader("content-type", "application/json");
        PropertyFilter propertyFilter = (source, name, value) -> {
            for (Entry entry : entries) {
                if (source.getClass().equals(entry.clz) && entry.fields.contains(name)) {
                    return entry instanceof AllowEntry;
                }
            }
            return true;
        };
        return JSON.toJSONString(this, propertyFilter);
    }

    public RenderJSON toResult(){
        return RenderJSON.of(this);
    }

    private String encode(String str) {
        return "";
//        return Utils.getCurrentUserPrivateKey().map(key -> {
//            try {
//                return RSAUtils.privateEncrypt(str, RSAUtils.getPrivateKey(key));
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (InvalidKeySpecException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }).orElse(RetJson.error().toJson());
    }

    public String toMobile() {
        return "";
//        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//        response.setHeader("content-type", "application/json");
//        String str = JSON.toJSONString(this);
////        return str;
//        return encode(str);
    }

    public String toMobile(DisallowEntry... entries) {
        return "";
//        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//        response.setHeader("content-type", "application/json");
//        PropertyFilter propertyFilter = (source, name, value) -> {
//            for (DisallowEntry entry : entries) {
//                if (source.getClass().equals(entry.getClz()) && entry.getFields().contains(name)) {
//                    return false;
//                }
//            }
//            return true;
//        };
//        String str = JSON.toJSONString(this, propertyFilter);
////        return str;
//        return encode(str);
    }

    //    public static String okJson(Object obj){
//        return okJson(obj,new DisallowEntry[0]);
//    }
    public static String okJson(Object obj, DisallowEntry... entries) {
        return RetJson.ok(obj).toJson(entries);
    }


    @Override
    public String toString() {
        return toJson();
    }
}

