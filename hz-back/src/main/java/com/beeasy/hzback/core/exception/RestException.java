package com.beeasy.hzback.core.exception;


import lombok.Getter;

public class RestException extends RuntimeException {

    @Getter
    private String simpleMessage;

    public RestException(String simpleMessage) {
        super();
        this.simpleMessage = simpleMessage;
    }




}
