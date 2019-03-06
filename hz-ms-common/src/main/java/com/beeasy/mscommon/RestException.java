package com.beeasy.mscommon;

import lombok.Getter;
import org.osgl.util.S;

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

    public RestException(String message, Object ...fmts){
        super();
        this.simpleMessage = S.fmt(message,fmts);
    }


    @Override
    public RestException get() {
        return this;
    }
}
