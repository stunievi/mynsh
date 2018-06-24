package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.form.SystemVarEditRequest;
import com.beeasy.hzback.modules.system.service.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api
@RequestMapping(value = "/api/system/var")
@RestController
public class SystemVariableController {
    @Autowired
    SystemService systemService;

    @ApiOperation(value = "设置系统变量")
    @RequestMapping(value = "/set", method = RequestMethod.POST)
    public Result set(
            @Valid @RequestBody SystemVarEditRequest request
            ){
        return Result.finish(systemService.set(request.getKey(), request.getValue(), true));
    }

    @ApiOperation(value = "得到系统变量")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result get(@RequestParam String key){
        return Result.ok(systemService.get(key));
    }

    @ApiOperation(value = "删除系统变量")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Result delete(@RequestParam String key){
        return Result.ok(systemService.delete(key));
    }

}
