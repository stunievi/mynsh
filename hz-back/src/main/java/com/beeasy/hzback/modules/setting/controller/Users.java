package com.beeasy.hzback.modules.setting.controller;

import com.beeasy.hzback.helper.Result;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/setting/user")
public class Users {

    @Autowired
    IUserDao userDao;

    @Autowired
    UserService userService;

    @GetMapping("/list")
    public String userList(Model model,@PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable){
        Page<User> userList = userDao.findAll(pageable);
        model.addAttribute("list",userList);
        return "setting/user_list";
    }


    @GetMapping("/add")
    public String userAdd(Model model){
        return "setting/user_edit";
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    @ResponseBody
    public Result userAdd(@Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return Result.error(bindingResult.getAllErrors());
        }
        //禁止添加同名用户
        User sameUser = userDao.findByUsername(user.getUsername());
        if(sameUser != null){
            return Result.error("禁止添加同名用户");
        }
        boolean flag = userService.add(user);
        return flag ? Result.ok() : Result.error("添加失败");
    }




}
