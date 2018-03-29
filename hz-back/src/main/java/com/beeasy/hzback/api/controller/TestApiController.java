package com.beeasy.hzback.api.controller;

import com.beeasy.hzback.core.helper.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/api-docs")
public class TestApiController {

    @RequestMapping("/")
    public Result test(){
        return Result.ok();
    }
}
