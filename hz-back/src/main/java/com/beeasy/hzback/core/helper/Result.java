package com.beeasy.hzback.core.helper;

import lombok.Data;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

@Data
public class Result <T> {
    protected boolean success;
    protected T message;


    protected Result(boolean success,T item) {
        this.success = success;
        this.message = item;
    }

    protected Result(boolean success) {
        this.success = success;
    }

    public static Result ok(){
        return new Result(true);
    }
    public static Result ok(Object item){
        return new Result(true,item);
    }

    public static Result error(Object item){
        if(item instanceof BindingResult){
            item = ((BindingResult) item).getAllErrors().stream().map(i -> i.getDefaultMessage()).collect(Collectors.toSet());
        }
        return new Result(false,item);
    }
    public static Result error(){
        return error(null);
    }
}
