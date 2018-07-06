package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.ISystemLogDao;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.hzback.modules.system.entity.SystemLog;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.log.NotSaveLog;
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
