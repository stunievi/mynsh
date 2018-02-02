package com.beeasy.hzback.core.filter;

import com.beeasy.hzback.core.config.AdminMenuConfig;
import com.beeasy.hzback.modules.setting.dao.IRoleDao;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.entity.WorkFlow;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        Set<Role> roles = user.getRoles();
        Set<WorkFlow> workFlows = new HashSet<>();
//        for(Role role : roles){
//            workFlows.addAll(role.getDepartment().getWorkFlows());
//        }
//        Object fuck = SecurityUtils.getSubject().getPrincipal();
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        request.getSession().getAttribute("fuck");
        List<AdminMenuConfig.AdminMenuItem> adminMenuItem = adminMenu.getAdminMenu();
//        Subject subject = Subject.getSubject()
        model.addAttribute("adminMenu",adminMenuItem);
        //处理分页
        if(model.containsAttribute("list")){
            Page<?> p = (Page<?>) model.asMap().get("list");
            model.addAttribute("totalPage",p.getTotalPages());
            model.addAttribute("currentPage",p.getNumber());
        }
        else{
            model.addAttribute("totalPage",0);
            model.addAttribute("currentPage",0);
        }
    }
}
