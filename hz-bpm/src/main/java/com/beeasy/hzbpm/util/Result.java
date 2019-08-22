package com.beeasy.hzbpm.util;

public class Result {
    public boolean success = false;
    public String errMessage = "";
    public Object data;


    public static Result ok(Object data){
        Result ret = new Result();
        ret.success = true;
        ret.data = data;
        return ret;
    }

    public static Result ok(){
        return ok(null);
    }

    public static Result error(){
        return error(null);
    }

    public static Result error(String msg){
        Result ret = new Result();
        ret.success = false;
        ret.data = null;
        ret.errMessage = msg;
        return ret;
    }
}
