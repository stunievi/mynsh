package com.beeasy.zed;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class ZedController {

    @Autowired
    ZedService zedService;

    @PostMapping("/zed")
    public Result doPost(
            @RequestBody JSONObject body
            ){
        return Result.ok(zedService.select(body));
    }
}
