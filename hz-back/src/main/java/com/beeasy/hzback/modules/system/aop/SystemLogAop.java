package com.beeasy.hzback.modules.system.aop;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.ISystemLogDao;
import com.beeasy.hzback.modules.system.entity.SystemLog;
import com.beeasy.hzback.modules.system.entity.SystemTextLog;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.log.SaveLog;
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
@Transactional
public class SystemLogAop {

    @Autowired
    ISystemLogDao systemLogDao;
    @Autowired
    UserService userService;

    @Pointcut(value = "execution(* com.beeasy.hzback.modules.*.controller..*(..)) ")
    public void point(){}

    @AfterReturning(value ="point()", returning = "res")
    public void log(JoinPoint joinPoint, Object res){
        handleLog(joinPoint,res);

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

    @Async
    public void handleLog(JoinPoint joinPoint, Object res){
        if(null == res){
            return;
        }
        Class targetClass = null;
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        try {
            targetClass = Class.forName(targetName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method[] methods = targetClass.getMethods();
        Object[] arguments = joinPoint.getArgs();
        String operationName = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs!=null&&clazzs.length == arguments.length&&method.getAnnotation(ApiOperation.class)!=null) {
                    operationName = method.getAnnotation(ApiOperation.class).value();
                    break;
                }
            }
        }
        if(!StringUtils.isEmpty(operationName)){
            writeLog(Utils.getCurrentUserId(), operationName, arguments);
        }
    }

    public void writeLog(long uid, String actionName, Object[] arguments){
        User user = userService.findUser(uid).orElse(null);
        if(null != user){
            //写日志
            SystemLog systemLog = new SystemLog();
            systemLog.setUserId(uid);
            systemLog.setUserName(user.getTrueName());
            systemLog.setMethod(actionName);
            systemLog.setParams(arguments);
            systemLogDao.save(systemLog);
        }
    }
}
