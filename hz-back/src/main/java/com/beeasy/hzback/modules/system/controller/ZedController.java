package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.zed.Zed;
import com.beeasy.hzback.core.helper.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

@Api(tags = "通用API", description = "通用API测试类")
@RestController
@RequestMapping("/api")
public class ZedController {

    @Autowired
    Zed zed;

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
        try {
            Reader r = new FileReader("/Users/bin/work/configlist.yaml");
            Yaml yaml = new Yaml();
            Object o = yaml.load(r);
            r.close();
            return Result.ok(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error();
    }
}
