package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.util.SqlUtils;
import com.beeasy.hzback.modules.system.entity.MessageTemplate;
import com.beeasy.hzback.modules.system.form.GlobalPermissionEditRequest;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.log.NotSaveLog;
import com.beeasy.hzback.modules.system.service.SystemService;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "系统API")
@RequestMapping(value = "/api/system")
@RestController
public class SystemController  {
    @Autowired
    UserService userService;
    @Autowired
    SystemService systemService;

    /************ 授权 *************/

    @ApiOperation(value = "增加全局授权")
    @RequestMapping(value = "/permission/set", method = RequestMethod.POST)
    public Result addGlobalPermission(
            @Valid @RequestBody GlobalPermissionEditRequest request
            ){
        return Result.ok(userService.addGlobalPermission(request.getType(),request.getObjectId(), request.getUserType(), request.getLinkIds(),null == request.getObject() ? request.getArray() : request.getObject()));
    }

    @ApiOperation(value = "删除全局授权")
    @RequestMapping(value = "/permission/delete", method = RequestMethod.GET)
    public Result deleteGlobalPermission(
            @RequestParam String id
    ){
        return Result.ok(userService.deleteGlobalPermission(Utils.convertIds(id)));
    }


    @RequestMapping(value = "/information", method = RequestMethod.GET)
    public String getSystemInfo(){
        return systemService.getSystemInfo();
    }


    /********** 消息模板 ************/
    @ApiOperation(value = "添加消息模板")
    @RequestMapping(value = "/messageTemplate/add", method = RequestMethod.POST)
    public Result addMessageTemplate(
            @RequestBody @Validated(value = SystemService.MessageTemplateRequest.add.class)SystemService.MessageTemplateRequest request
            ){
        return Result.ok(systemService.addMessageTemplate(request));
    }

    @ApiOperation(value = "编辑消息模板")
    @RequestMapping(value = "/messageTemplate/edit", method = RequestMethod.POST)
    public Result editMessageTemplate(
            @Validated(value = SystemService.MessageTemplateRequest.edit.class) @RequestBody SystemService.MessageTemplateRequest request
    ){
        return Result.finish(systemService.editMessageTemplate(request));
    }


    @ApiOperation(value = "批量删除消息模板")
    @RequestMapping(value = "/messageTemplate/delete", method = RequestMethod.GET)
    public Result deleteMessageTemplate(
            @RequestParam String id
    ){
        return Result.ok(systemService.deleteMessageTemplates(Utils.convertIdsToList(id)));
    }

    @ApiOperation(value = "获得消息模板列表")
    @RequestMapping(value = "/messageTemplate/list", method = RequestMethod.GET)
    public Result getMessageTemplateList(
            SystemService.MessageTemplateSearchRequest request,
            Pager pagers,
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(systemService.getMessageTemplateList(request,pageable));
    }

    @ApiOperation(value = "通过ID得到消息详情")
    @RequestMapping(value = "/messageTemplate/getById", method = RequestMethod.GET)
    public Result getMessageTemplateById(
            @RequestParam long id
    ){
        return Result.finish(systemService.getMessageTemplateById(id));
    }

    /**********测试***********/
    //该接口只有管理员可以调用
    @Autowired
    EntityManager entityManager;
    @Autowired
    SqlUtils sqlUtils;
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public Result query(@RequestBody String sql){
        if(!userService.isSu(Utils.getCurrentUserId())){
            return Result.error();
        }
        return Result.ok(sqlUtils.query(sql));
    }

    @NotSaveLog
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public Result execute(@RequestBody String sql){
        if(!userService.isSu(Utils.getCurrentUserId())){
            return Result.error();
        }
        return Result.ok(sqlUtils.execute(sql));
    }


}
