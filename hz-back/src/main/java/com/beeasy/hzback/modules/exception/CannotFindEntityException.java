package com.beeasy.hzback.modules.exception;

import com.beeasy.hzback.core.exception.RestException;

public class CannotFindEntityException extends RestException {

    public CannotFindEntityException(Class clz, Object id){
        super();
        simpleMessage = "没有找到" + clz.getName() + " " +  String.valueOf(id);
    }
}
