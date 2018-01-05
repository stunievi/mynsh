package com.beeasy.hzback.modules.setting.controller;



import com.beeasy.hzback.modules.setting.entity.User;
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
