package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import com.beeasy.hzback.modules.system.form.GlobalPermissionEditRequest;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.service.DataSearchService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RequestMapping("/api/data")
@RestController
public class SearchController {
    @Autowired
    DataSearchService searchService;

    @ApiOperation(value = "对公客户资料查询")
    @RequestMapping(value = "/searchPublicClient", method = RequestMethod.GET)
    public Result searchPublicClient(
            DataSearchService.PublicClientRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchPublicClient(Utils.getCurrentUserId(), request, pageable));
    }


    @ApiOperation(value = "对私客户资料查询")
    @RequestMapping(value = "/searchPrivateClient", method = RequestMethod.GET)
    public Result searchPrivateClient(
            DataSearchService.PrivateClientRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchPrivateClient(request, pageable));
    }


    @ApiOperation(value = "贷款台账查询")
    @RequestMapping(value = "/searchAccloan", method = RequestMethod.GET)
    public Result searchAccLoan(
            DataSearchService.AccloanRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchAccLoan(request,pageable));
    }

    @ApiOperation(value = "贷款资料查询")
    @RequestMapping(value = "/searchAccloanData", method = RequestMethod.GET)
    public Result searchAccLoanData(
            DataSearchService.AccloanRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchAccLoan(request,pageable));
    }

    @ApiOperation(value = "设置数据查询约束权限")
    @RequestMapping(value = "/setConditionPermission", method = RequestMethod.POST)
    public Result setConditionPermission(GlobalPermissionEditRequest[] requests){
        return Result.ok(searchService.setPermissions(requests, GlobalPermission.Type.DATA_SEARCH_CONDITION));
    }

    @ApiOperation(value = "设置数据查询的结果约束")
    @RequestMapping(value = "/setResultPermission", method = RequestMethod.POST)
    public Result setResultPermission(GlobalPermissionEditRequest[] requests){
        return Result.ok(searchService.setPermissions(requests, GlobalPermission.Type.DATA_SEARCH_RESULT));
    }

    @ApiOperation(value = "授权查询")
    @RequestMapping(value = "/getPermissions", method = RequestMethod.GET)
    public Result getPermissions(
            @RequestParam  String type
    ){
        return Result.ok(searchService.getPermissions(Utils.convertToList(type, GlobalPermission.Type.class)));
    }


}
