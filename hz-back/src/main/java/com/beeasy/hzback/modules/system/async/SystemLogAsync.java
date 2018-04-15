package com.beeasy.hzback.modules.system.async;

import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.dao.ISystemLogDao;
import com.beeasy.hzback.modules.system.entity.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
@Transactional
public class SystemLogAsync {
    @Autowired
    ISystemLogDao systemLogDao;

    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    @Async("taskExecutor")
    public void saveLog(User user, HttpServletRequest request, MethodSignature methodSignature) {
        String className = methodSignature.getDeclaringTypeName();
        className = className.substring(className.lastIndexOf(".") + 1);
        className += "." + methodSignature.getMethod().getName();

        SystemLog log = new SystemLog();
        log.setIp(getIpAddr(request));
        log.setUserName(user.getUsername());
        log.setMethod(className);
        log.setParams((request.getParameterMap()));
        systemLogDao.save(log);
    }
}
