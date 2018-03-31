package com.beeasy.hzback.core.exception;


import lombok.Getter;

public class RestException extends Exception {

    @Getter
    private String simpleMessage;

    public RestException(String simpleMessage) {
        super();
        this.simpleMessage = simpleMessage;
    }




}
