package com.beeasy.hzback.controller;



import com.beeasy.hzback.dao.IUserDao;
import com.beeasy.hzback.entity.Admin;
import com.beeasy.hzback.entity.User;
import com.beeasy.hzback.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {



    @Autowired
    private IUserDao userDao;

    @RequestMapping("/")
    public String hello(){
        return "redirect:guichu";
    }


    @RequestMapping("/guichu")
    public User guchu(){
        return userDao.findOne(1L);
    }
}
