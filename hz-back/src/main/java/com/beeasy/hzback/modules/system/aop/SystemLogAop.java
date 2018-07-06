package com.beeasy.hzback.modules.system.aop;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.ISystemLogDao;
import com.beeasy.hzback.modules.system.entity.SystemLog;
import com.beeasy.hzback.modules.system.entity.SystemTextLog;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.log.NotSaveLog;
import com.beeasy.hzback.modules.system.log.SaveLog;
import com.beeasy.hzback.modules.system.service.SystemLogService;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.Joinpoint;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 系统日志切面, 只记录登录的用户访问的情况
 */
@Aspect
@Component
@Slf4j
public class SystemLogAop {

    @Autowired
    ISystemLogDao systemLogDao;
    @Autowired
    UserService userService;
    @Autowired
    SystemLogService systemLogService;

    @Pointcut(value = "execution(* com.beeasy.hzback.modules.*.controller..*(..)) ")
    public void point(){}

    @AfterReturning(value ="point()", returning = "res")
    public void log(JoinPoint joinPoint, Object res){
        systemLogService.handleLog(joinPoint,res);

//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        SaveLog saveLog = methodSignature.getMethod().getAnnotation(SaveLog.class);
//        if(null == saveLog){
//            return;
//        }
//        SystemTextLog textLog = new SystemTextLog();
//        textLog.setUserId(Utils.getCurrentUserId());
//        textLog.setType(saveLog.type());



//        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if(o == null) return;
//        if(o instanceof String && o.equals("anonymousUser")) return;
//
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        systemLogAsync.saveLog((User) o,request,methodSignature);
    }


}
