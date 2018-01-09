package com.beeasy.hzback.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class Result {
    private boolean success;
    private Object message;


    private Result(boolean success,Object item) {
        this.success = success;
        this.message = item;
    }

    private Result(boolean success) {
        this.success = success;
    }

    public static Result ok(){
        return new Result(true);
    }
    public static Result ok(Object item){
        return new Result(true,item);
    }

    public static Result error(Object item){
        return new Result(false,item);
    }
}
