package com.beeasy.hzback.core.exception;

import com.beeasy.hzback.core.helper.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler{
    
    //运行时异常
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result runtimeExceptionHandler(RuntimeException runtimeException) {
        log.info(runtimeException.getMessage());
        return ReturnFormat.retParam(1000, null);
    }

    //空指针异常
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Result nullPointerExceptionHandler(NullPointerException ex) {
        ex.printStackTrace();
        return ReturnFormat.retParam(1001, null);
    }
    //类型转换异常
    @ExceptionHandler(ClassCastException.class)
    @ResponseBody
    public Result classCastExceptionHandler(ClassCastException ex) {
        ex.printStackTrace();
        return ReturnFormat.retParam(1002, null);
    }

    //IO异常
    @ExceptionHandler(IOException.class)
    @ResponseBody
    public Result iOExceptionHandler(IOException ex) {
        ex.printStackTrace();
        return ReturnFormat.retParam(1003, null);
    }
    //未知方法异常
    @ExceptionHandler(NoSuchMethodException.class)
    @ResponseBody
    public Result noSuchMethodExceptionHandler(NoSuchMethodException ex) {
        ex.printStackTrace();
        return ReturnFormat.retParam(1004, null);
    }

    //数组越界异常
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    public Result indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException ex) {
        ex.printStackTrace();
        return ReturnFormat.retParam(1005, null);
    }
    //400错误
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public Result requestNotReadable(HttpMessageNotReadableException ex){
        System.out.println("400..requestNotReadable");
        ex.printStackTrace();
        return ReturnFormat.retParam(400, null);
    }
    //400错误
    @ExceptionHandler({TypeMismatchException.class})
    @ResponseBody
    public Result requestTypeMismatch(TypeMismatchException ex){
        System.out.println("400..TypeMismatchException");
        ex.printStackTrace();
        return ReturnFormat.retParam(400, null);
    }
    //400错误
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    public Result requestMissingServletRequest(MissingServletRequestParameterException ex){
        System.out.println("400..MissingServletRequest");
        ex.printStackTrace();
        return ReturnFormat.retParam(400, null);
    }
    //405错误
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public Result request405(){
        System.out.println("405...");
        return ReturnFormat.retParam(405, null);
    }
    //406错误
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseBody
    public Result request406(){
        System.out.println("404...");
        return ReturnFormat.retParam(406, null);
    }
    //500错误
    @ExceptionHandler({ConversionNotSupportedException.class,HttpMessageNotWritableException.class})
    @ResponseBody
    public Result server500(RuntimeException runtimeException){
        System.out.println("500...");
        return ReturnFormat.retParam(406, null);
    }
    
    
    static class ReturnFormat{
        public static Result retParam(int code, Object param){
            return Result.error();   
        }
    }


}
