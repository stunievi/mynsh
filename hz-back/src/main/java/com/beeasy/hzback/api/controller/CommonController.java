package com.beeasy.hzback.api.controller;


import com.beeasy.hzback.core.config.AdminMenuConfig;
import com.beeasy.hzback.core.helper.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class CommonController {

    @Autowired
    AdminMenuConfig adminMenuConfig;

    @RequestMapping("/menu")
    public Result menu(){
        return Result.ok(adminMenuConfig.getAdminMenu());
    }
}
