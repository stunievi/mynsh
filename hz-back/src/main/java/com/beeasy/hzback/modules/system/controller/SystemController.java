package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.form.GlobalPermissionEditRequest;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping(value = "/api/system")
@RestController
public class SystemController  {
    @Autowired
    UserService userService;

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
        return Result.ok(userService.deleteGlobalPermission((Long[]) Utils.convertIds(id).toArray()));
    }



}
