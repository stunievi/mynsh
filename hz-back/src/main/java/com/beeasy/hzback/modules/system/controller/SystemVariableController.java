package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.ISystemVariableDao;
import com.beeasy.hzback.modules.system.entity.SystemVariable;
import com.beeasy.hzback.modules.system.form.SystemVarEditRequest;
import com.beeasy.hzback.modules.system.log.SaveLog;
import com.beeasy.hzback.modules.system.service.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "系统变量API")
@RequestMapping(value = "/api/system/var")
@RestController
public class SystemVariableController {
    @Autowired
    SystemService systemService;
    @Autowired
    ISystemVariableDao systemVariableDao;

    @SaveLog(value = "设置系统变量")
    @ApiOperation(value = "设置系统变量")
    @RequestMapping(value = "/set", method = RequestMethod.POST)
    public Result set(
            @Valid @RequestBody Map<String,String> request
            ){
        return Result.finish(systemService.set(request));
    }

    @ApiOperation(value = "得到系统变量")
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public Result get(
            @RequestBody String[] keys
    ){

        return Result.ok(systemService.get(keys));
    }

    @ApiOperation(value = "得到所有的系统变量")
    @RequestMapping(value = "/getall", method = RequestMethod.GET)
    public Result get(
    ){
        return Result.ok(systemVariableDao.findAll().stream().collect(Collectors.toMap(SystemVariable::getVarName, SystemVariable::getVarValue)));
    }

    @ApiOperation(value = "删除系统变量")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Result delete(@RequestParam String key){
        return Result.ok(systemService.delete(Utils.convertToList(key,String.class)));
    }

}
