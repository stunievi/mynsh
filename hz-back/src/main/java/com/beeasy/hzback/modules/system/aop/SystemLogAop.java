package com.beeasy.hzback.modules.system.aop;

import com.beeasy.hzback.modules.system.async.SystemLogAsync;
import com.beeasy.hzback.modules.system.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统日志切面, 只记录登录的用户访问的情况
 */
@Aspect
@Component
@Slf4j
@Transactional
public class SystemLogAop {

    @Autowired
    SystemLogAsync systemLogAsync;


    @After(value ="execution(* com.beeasy.hzback.modules.*.controller..*(..)) ")
    public void log(JoinPoint joinPoint){
        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(o == null) return;
        if(o instanceof String && o.equals("anonymousUser")) return;

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        systemLogAsync.saveLog((User) o,request,methodSignature);
    }
}
