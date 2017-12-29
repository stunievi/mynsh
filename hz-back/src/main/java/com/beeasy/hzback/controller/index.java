package com.beeasy.hzback.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class index {

    @RequestMapping("/fuck")
    public ModelAndView index(){
        ModelAndView result = new ModelAndView("index");
        return result;
    }

    @RequestMapping("/ri")
    public String test(){
        return "fuck";
    }

//    @RequestMapping(value="/login",method= RequestMethod.POST)
//    public String login(@Valid User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "login";
//        }
//
//        String username = user.getUsername();
//        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
//        User currentUser = (User) SecurityUtils.getSubject();
//
//
//        return username;
//    }
}
