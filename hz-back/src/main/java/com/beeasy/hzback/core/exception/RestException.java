package com.beeasy.hzback.core.exception;


import lombok.Getter;

import java.util.function.Supplier;

public class RestException extends RuntimeException implements Supplier<RestException> {

    @Getter
    protected String simpleMessage = "";

    public RestException() {
        super();
    }

    public RestException(String simpleMessage) {
        super();
        this.simpleMessage = simpleMessage;
    }


    @Override
    public RestException get() {
        return this;
    }
}
