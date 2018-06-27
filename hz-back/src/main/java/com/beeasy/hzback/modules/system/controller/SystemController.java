package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.form.GlobalPermissionEditRequest;
import com.beeasy.hzback.modules.system.service.SystemService;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.Valid;
import java.util.Arrays;
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


    /**********测试***********/
    @Autowired
    EntityManager entityManager;
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public Result query(@RequestBody String sql){
        Query query =entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return Result.ok(
                query
                .getResultList());
    }
}
