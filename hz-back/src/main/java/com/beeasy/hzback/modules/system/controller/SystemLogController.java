package com.beeasy.hzback.modules.system.controller;


import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.dao.ISystemLogDao;
import com.beeasy.hzback.modules.system.form.Pager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "系统日志API", description = "用于系统日志的查看, 需要管理员权限")
@RestController
@Slf4j
@RequestMapping("/api/system_log")
public class SystemLogController {

    @Autowired
    ISystemLogDao systemLogDao;

    @ApiOperation(value = "获得系统日志列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "需要查找的用户名")
    })
    @GetMapping
    public Result list(
            Pager pager,
            String userName,
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        if(!StringUtils.isEmpty(userName)){
            return Result.ok(systemLogDao.findAllByUserName(userName,pageable));
        }
        return Result.ok(systemLogDao.findAll(pageable));
    }
}
