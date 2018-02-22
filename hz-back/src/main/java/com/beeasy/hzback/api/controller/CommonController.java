package com.beeasy.hzback.api.controller;


import com.beeasy.hzback.core.config.AdminMenuConfig;
import bin.leblanc.zed.Result;
import bin.leblanc.zed.Zed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class CommonController {

    @Autowired
    AdminMenuConfig adminMenuConfig;

    @RequestMapping("/menu")
    public Result menu(){
        return Result.ok(adminMenuConfig.getAdminMenu());
    }


    @Autowired
    Zed zed;

    @RequestMapping("/aa")
    public Result menu2(
            HttpServletRequest request,
            @RequestBody String body
    ){
//        request.get
            log.info(body);
//        log.info();
        try {
            return (zed).parse(body);
        } catch (Exception e) {
            return Result.error();
        }
//        request.get
//        return Result.ok();
    }
}
