package com.beeasy.hzback.filter;

import com.beeasy.hzback.config.AdminMenuConfig;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class AdminControllerInterceptor  {

    @Autowired
    private AdminMenuConfig adminMenu;

    @AfterReturning(value = "execution(* com.beeasy.hzback.modules.*.controller..*(..)) && @annotation(org.springframework.web.bind.annotation.GetMapping)",returning = "keys")
    public void doAfterReturningAdvice1(JoinPoint joinPoint, Object keys){
        Object[] os = joinPoint.getArgs();
        Model model = null;
        for(Object o : os){
            if(o instanceof Model){
                model = (Model)o;
                break;
            }
        }
        if(model == null){
            return;
        }
        //根据权限处理掉不需要的菜单选项
        model.addAttribute("adminMenu",adminMenu.getAdminMenu());
    }
}
