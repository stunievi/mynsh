package com.beeasy.hzbpm.exception;

public class BpmException extends RuntimeException{
    public String error = "";

    public BpmException(String message) {
        super(message);
        error = message;
    }
}
