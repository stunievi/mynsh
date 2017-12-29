package com.beeasy.hzback.controller;



import com.beeasy.hzback.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {



//    @Autowired
//    private IUserDao userDao;
//
    @RequestMapping("/")
    public String hello(){
        return "12234SADFD52";
    }
//
//
    @RequestMapping("/guichu")
    public User guchu(){
        return new User();
    }
}
