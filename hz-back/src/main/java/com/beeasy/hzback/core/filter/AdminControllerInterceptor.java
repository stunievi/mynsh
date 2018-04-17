//package com.beeasy.hzback.core.filter;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.beeasy.hzback.core.config.AdminMenuConfig;
//import com.beeasy.hzback.core.security.SecurityUser;
//import com.beeasy.hzback.modules.setting.dao.IRoleDao;
//import com.beeasy.hzback.modules.system.dao.IUserDao;
//import com.beeasy.hzback.modules.setting.entity.Role;
//import com.beeasy.hzback.modules.system.entity.User;
//import com.beeasy.hzback.modules.setting.entity.WorkFlow;
//import org.apache.shiro.SecurityUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pager;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.ui.Model;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.*;
//
//@Aspect
//@Component
//public class AdminControllerInterceptor  {
//
//    @Autowired
//    private AdminMenuConfig adminMenu;
//
//    @Autowired
//    private IUserDao userDao;
//
//    @AfterReturning(value = "execution(* com.beeasy.hzback.modules.*.controller..*(..)) && @annotation(org.springframework.web.bind.annotation.GetMapping)",returning = "keys")
//    public void doAfterReturningAdvice1(JoinPoint joinPoint, Object keys){
//        Object[] os = joinPoint.getArgs();
//        Model model = null;
//        for(Object o : os){
//            if(o instanceof Model){
//                model = (Model)o;
//                break;
//            }
//        }
//        if(model == null){
//            return;
//        }
//        //根据权限处理掉不需要的菜单选项
////        org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
////        User user = (User) subject.getPrincipal();
//
//        //增加自己配置的工作流菜单
//        SecurityUser su = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        List<WorkFlow> workFlowList = userDao.getUserWorkFlows(su.getUser());
//
//        //将菜单列表进行深拷贝（每一个人的菜单项实际上是不同的）
//        TypeReference<List<AdminMenuConfig.AdminMenuItem>> type = new TypeReference<List<AdminMenuConfig.AdminMenuItem>>(){};
//        List<AdminMenuConfig.AdminMenuItem> menu = JSON.parseObject(JSON.toJSONString(adminMenu.getAdminMenu()),type);
//        AdminMenuConfig.AdminMenuItem target = (AdminMenuConfig.AdminMenuItem) menu.stream().filter(item -> item.getTitle().equals("工作台")).toArray()[0];
//        List<AdminMenuConfig.AdminMenuItem> children = target.getChildren();
//        for(WorkFlow workFlow : workFlowList){
//            AdminMenuConfig.AdminMenuItem item = new AdminMenuConfig.AdminMenuItem();
//            item.setChildren(new ArrayList<>());
//            item.setTitle(workFlow.getNodeName());
//            item.setHref("/admin/workflow/task/" + workFlow.getId());
//            children.add(item);
//        }
//        model.addAttribute("adminMenu",menu);
//
//        //处理分页
//        if(model.containsAttribute("list")){
//            Pager<?> p = (Pager<?>) model.asMap().get("list");
//            model.addAttribute("totalPage",p.getTotalPages());
//            model.addAttribute("currentPage",p.getNumber());
//        }
//        else{
//            model.addAttribute("totalPage",0);
//            model.addAttribute("currentPage",0);
//        }
//    }
//}
