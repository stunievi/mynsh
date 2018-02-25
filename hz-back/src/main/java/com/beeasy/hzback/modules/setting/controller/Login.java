package com.beeasy.hzback.modules.setting.controller;

import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.core.util.CrUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class Login {


    @GetMapping("/login")
    public Object login(){
//        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
//        if(currentUser.isAuthenticated()){
//            return "redirect:/admin";
//        }
        ModelAndView ret = new ModelAndView("login");
        return ret;
    }


    @PostMapping("/login")
    public String login(@Valid User user, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "login";
        }
        String username = user.getUsername();
        String password = user.getPassword();
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
            return "redirect:/admin";
        }
        try{
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), password);
            currentUser.login(token);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(currentUser.isAuthenticated()){
            return "redirect:/admin/index.html";
        }
        return "login";
    }

}
