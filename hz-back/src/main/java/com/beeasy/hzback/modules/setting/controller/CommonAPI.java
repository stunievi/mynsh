//package com.beeasy.hzback.modules.setting.controller;
//
//import com.beeasy.hzback.core.helper.Result;
//import com.beeasy.hzback.modules.system.entity.User;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.subject.Subject;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.validation.Valid;
//
//
//@RestController
//@RequestMapping("/api")
//public class CommonAPI {
//
//    /**
//     * 检查是否登录
//     * @return
//     */
//    @RequestMapping("/checkLogin")
//    public Result checkLogin(){
//        Subject subject = SecurityUtils.getSubject();
//        if(subject.isAuthenticated()){
//            User user = (User) subject.getPrincipal();
//            return Result.ok(user);
//        }
//        else{
//            return Result.error();
//        }
//    }
//
//    /**
//     *
//     * @return
//     */
//    @RequestMapping("/login")
//    public Result login(
//            @Valid User user,
//            BindingResult bindingResult
//    ){
//        if(bindingResult.hasErrors()){
//            return Result.error(
//                bindingResult.getAllErrors().stream().map(item -> item.toString()).toArray()
//            );
//        }
//        Subject subject = SecurityUtils.getSubject();
//        if(subject.isAuthenticated()){
//            User u = (User) subject.getPrincipal();
//            return Result.ok(u);
//        }
//        try{
//            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
//            subject.login(token);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            return Result.error();
//        }
//
//        if(subject.isAuthenticated()){
//            User u = (User) subject.getPrincipal();
//            return Result.ok(u);
//        }
//        return Result.error();
//    }
//
//
//
//}
