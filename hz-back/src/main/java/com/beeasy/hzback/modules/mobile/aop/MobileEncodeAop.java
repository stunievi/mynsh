package com.beeasy.hzback.modules.mobile.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MobileEncodeAop {
    @Pointcut(value = "execution(* com.beeasy.hzback.modules.mobile.controller..*(..)) ")
    public void point(){}

    @Around(value ="point()")
    public Object twiceAsOld(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {


        Object result = null;
        result = joinPoint.proceed();

//        if(result instanceof Result){
//            ((Result) result).setData(Utils.md5(String.valueOf(((Result) result).getData())));
//            return result;
//        }
        return result;
//        try {
//            result = joinPoint.proceed();
//            if(result instanceof Boolean){
//                result = result.equals(true) ? Result.ok() : Result.error();
//            }
//        } catch (Throwable throwable) {
//            if(throwable instanceof RestException){
//                return Result.error(((RestException) throwable).getSimpleMessage());
//            }
//            else {
//                throwable.printStackTrace();
//            }
//        }
//        return result;
    }
}
