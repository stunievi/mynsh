package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.zed.Zed;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "通用API", description = "通用API测试类")
@RestController
@RequestMapping("/api")
public class ZedController {

    @Autowired
    Zed zed;

    @Autowired
    SystemConfigCache cache;

    @ApiOperation(value = "通用查询API", notes = "需要校验身份授权")
    @PostMapping
    public Result zed(String request){
        try {
            Map<?,?> result = zed.parse(request,"");
            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error();
    }


    @ApiOperation(value = "系统设置", notes = "得到系统的设置列表")
    @GetMapping("/config")
    public synchronized Result getConfig(){
        return Result.ok(cache.getWorkflowConfig());
    }


}
