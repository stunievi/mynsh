package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.zed.Zed;
import com.beeasy.hzback.core.helper.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ZedController {

    @Autowired
    Zed zed;

    @PostMapping("/")
    public Result zed(String request){
        try {
            Map<?,?> result = zed.parse(request,"");
            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error();
    }
}
