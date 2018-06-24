package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.form.ModifyPasswordRequest;
import com.beeasy.hzback.modules.system.form.ProfileEditRequest;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/user")
@RestController
public class WebUserController  {
    @Autowired
    UserService userService;

    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
    public Result modifyPassword(
            @Valid @RequestBody ModifyPasswordRequest request
    ){
        return userService.modifyPassword(Utils.getCurrentUserId(), request.getOldPassword(), request.getNewPassword());
    }

    @ApiOperation(value = "修改个人资料")
    @RequestMapping(value = "/modifyProfile", method = RequestMethod.POST)
    public Result modifyProfile(
            @Valid @RequestBody ProfileEditRequest request
            ){
        return userService.modifyProfile(Utils.getCurrentUserId(), request);
    }



}
