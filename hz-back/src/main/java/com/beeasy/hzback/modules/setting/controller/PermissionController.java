package com.beeasy.hzback.modules.setting.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/setting/permission")
public class PermissionController {
    @Autowired
    IUserDao userDao;

    @Autowired
    UserService userService;

    static String failedUrl = "redirect:list";

    @GetMapping("/list")
    public String userList(Model model, @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable){
        Page<User> userList = userDao.findAll(pageable);
        model.addAttribute("list",userList);
        return "setting/permission_list";
    }

    @GetMapping("/edit")
    public String edit(Model model,Integer id){
        if(id == null){
            return failedUrl;
        }
        User user = userDao.findOne(id);
        if(user == null){
            return failedUrl;
        }
        
        return "setting/permission_edit";
    }

}
