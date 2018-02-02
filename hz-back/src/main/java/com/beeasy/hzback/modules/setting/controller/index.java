package com.beeasy.hzback.modules.setting.controller;


import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("/admin")
public class index {

    @RequestMapping("/")
    public String index(){
        return "redirect:/admin/index.html";
    }

    @GetMapping("/index.html")
    public String indexHTML(Model model){
        return "index";
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
