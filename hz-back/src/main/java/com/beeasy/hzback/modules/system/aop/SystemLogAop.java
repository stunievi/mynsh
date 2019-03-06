package com.beeasy.hzback.modules.system.aop;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.service.SystemLogService;
import com.beeasy.hzback.modules.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统日志切面, 只记录登录的用户访问的情况
 */
@Aspect
@Component
@Slf4j
public class SystemLogAop {

    @Autowired
    UserService userService;
    @Autowired
    SystemLogService systemLogService;

    @Pointcut(value = "execution(* com.beeasy.hzback.modules.*.controller..*(..)) ")
    public void point() {
    }

    @AfterReturning(value = "point()", returning = "res")
    public void log(JoinPoint joinPoint, Object res) {
        Long uid = Utils.getCurrentUserId();
        if(null == uid){
            return;
        }
        systemLogService.handleLog(uid, joinPoint, res);

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
