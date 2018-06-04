package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/error")
@RestController
public class ErrorController {

    @RequestMapping("/")
    public Result error(){
        return Result.error();
    }
}
