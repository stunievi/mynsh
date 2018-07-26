package com.beeasy.hzback.core.exception;


import lombok.Getter;

public class RestException extends RuntimeException{

    @Getter
    protected String simpleMessage = "";

    public RestException(){
        super();
    }
    public RestException(String simpleMessage) {
        super();
        this.simpleMessage = simpleMessage;
    }


}
