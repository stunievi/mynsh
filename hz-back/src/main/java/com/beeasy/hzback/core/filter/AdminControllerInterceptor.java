package com.beeasy.hzback.core.filter;

import com.beeasy.hzback.core.config.AdminMenuConfig;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

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
