package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.system.dao.ISystemLogDao;
import com.beeasy.hzback.modules.system.entity.SystemLog;
import com.beeasy.hzback.modules.system.entity_kt.User;
import com.beeasy.hzback.modules.system.log.NotSaveLog;
import com.beeasy.hzback.modules.system.service_kt.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@Service
@Transactional
public class SystemLogService {
    @Autowired
    UserService userService;
    @Autowired
    ISystemLogDao systemLogDao;

    @Async
    public void handleLog(final long uid, JoinPoint joinPoint, Object res){
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

        Api api = (Api) targetClass.getAnnotation(Api.class);
        if(null == api){
            return;
        }

        Method[] methods = targetClass.getMethods();
        Object[] arguments = joinPoint.getArgs();
        String operationName = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs!=null&&clazzs.length == arguments.length){
                    //禁止记录log
                    if(method.getAnnotation(NotSaveLog.class) != null){
                        continue;
                    }
                    if(method.getAnnotation(ApiOperation.class) != null){
                        operationName = method.getAnnotation(ApiOperation.class).value();
                        break;
                    }
                }
            }
        }

        if(!StringUtils.isEmpty(operationName)){
            if(api.tags().length > 0){
                writeLog(uid, api.tags()[0], operationName, arguments);
            }
        }
    }

    public void writeLog(final long uid, final String className, final String actionName, final Object[] arguments){
        try{
            User user = userService.findUser(uid);
            //写日志
            SystemLog systemLog = new SystemLog();
            systemLog.setUserId(uid);
            systemLog.setController(className);
            systemLog.setUserName(user.getTrueName());
            systemLog.setMethod(actionName);
            systemLog.setParams(arguments);
            systemLogDao.save(systemLog);
        }
        catch (Exception e){

        }
    }
}
