package com.beeasy.hzback.core.helper;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
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

    public static Result finish(boolean b){
        return b ? ok() : error();
    }
}
