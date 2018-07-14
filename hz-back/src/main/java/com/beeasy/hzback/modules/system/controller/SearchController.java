package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import com.beeasy.hzback.modules.system.form.GlobalPermissionEditRequest;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.service.DataSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.rmi.CORBA.Util;
import javax.validation.Valid;
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
        return Result.ok(searchService.searchPrivateClient(Utils.getCurrentUserId(), request, pageable));
    }


    @ApiOperation(value = "查询单独客户的基本资料")
    @RequestMapping(value = "/searchClientBase", method = RequestMethod.GET)
    public Result searchClientBase(
            @RequestParam String CUS_ID
    ){
        return Result.ok(searchService.searchClientBase(CUS_ID));
    }


    @ApiOperation(value = "贷款台账查询")
    @RequestMapping(value = "/searchAccloan", method = RequestMethod.GET)
    public Result searchAccLoan(
            DataSearchService.AccloanRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchAccLoan(Utils.getCurrentUserId(), request,pageable));
    }

    @ApiOperation(value = "贷款资料查询")
    @RequestMapping(value = "/searchAccloanData", method = RequestMethod.GET)
    public Result searchAccLoanData(
            DataSearchService.AccloanRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchAccLoan(Utils.getCurrentUserId(), request,pageable));
    }

    @ApiOperation(value = "高管信息查询")
    @RequestMapping(value = "/searchCusComManager", method = RequestMethod.GET)
    public Result searchCusComManager(
            @RequestParam String CUS_ID,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchCusComManager(Utils.getCurrentUserId(), CUS_ID,pageable));
    }

    @ApiOperation(value = "联系信息查询")
    @RequestMapping(value = "/searchComAddr", method = RequestMethod.GET)
    public Result searchComAddr(
            @RequestParam String CUS_ID,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchComAddr(Utils.getCurrentUserId(), CUS_ID,pageable));
    }

    @ApiOperation(value = "个人收入情况查询")
    @RequestMapping(value = "/searchCusInDiv", method = RequestMethod.GET)
    public Result searchCusInDiv(
            @RequestParam String CUS_ID,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchCusInDiv(Utils.getCurrentUserId(), CUS_ID,pageable));
    }

    @ApiOperation(value = "贷款合同列表查询")
    @RequestMapping(value = "/searchCrtLoan", method = RequestMethod.GET)
    public Result searchCrtLoan(
            @RequestParam String CONT_NO,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchCrtLoan(Utils.getCurrentUserId(), CONT_NO,pageable));
    }

    @ApiOperation(value = "担保合同列表查询")
    @RequestMapping(value = "/searchGrtGuar", method = RequestMethod.GET)
    public Result searchGrtGuar(
            @RequestParam String CONT_NO,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchGrtGuar(Utils.getCurrentUserId(), CONT_NO,pageable));
    }

    @ApiOperation(value = "贷款台账-基本信息")
    @RequestMapping(value = "/searchACC_LOAN", method = RequestMethod.GET)
    public Result searchACC_LOAN(
            @RequestParam String BILL_NO
    ){
        return Result.ok(searchService.searchACC_LOAN(Utils.getCurrentUserId(), BILL_NO));
    }

    @ApiOperation(value = "对公客户-基本信息")
    @RequestMapping(value = "/searchCUS_COM", method = RequestMethod.GET)
    public Result searchCUS_COM(
            @RequestParam String CUS_ID
    ){
        return Result.ok(searchService.searchCUS_COM(Utils.getCurrentUserId(), CUS_ID));
    }

    @ApiOperation(value = "对私客户-基本信息")
    @RequestMapping(value = "/searchCUS_INDIV", method = RequestMethod.GET)
    public Result searchCUS_INDIV(
            @RequestParam String CUS_ID
    ){
        return Result.ok(searchService.searchCUS_INDIV(Utils.getCurrentUserId(), CUS_ID));
    }

    @ApiOperation(value = "抵押物明细列表查询")
    @RequestMapping(value = "/searchGRTGBasicInfo", method = RequestMethod.GET)
    public Result searchGRTGBasicInfo(
            @RequestParam String CONT_NO,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchGRTGBasicInfo(Utils.getCurrentUserId(), CONT_NO,pageable));
    }


    @ApiOperation(value = "设置数据查询权限")
    @RequestMapping(value = "/setPermission", method = RequestMethod.POST)
    public Result setConditionPermission(
            @Valid @RequestBody GlobalPermissionEditRequest request){
        return Result.ok(searchService.setPermissions(request));
    }



//    @ApiOperation(value = "设置数据查询的结果约束")
//    @RequestMapping(value = "/setResultPermission", method = RequestMethod.POST)
//    public Result setResultPermission(@Valid @RequestBody GlobalPermissionEditRequest request){
//        return Result.ok(searchService.setPermissions(request);
//    }

    @ApiOperation(value = "授权查询")
    @RequestMapping(value = "/getPermissions", method = RequestMethod.GET)
    public Result getPermissions(
            @RequestParam GlobalPermission.Type type,
            @RequestParam GlobalPermission.UserType userType,
            @RequestParam long linkId
    ){
        return Result.ok(searchService.getPermission(type,userType,linkId));
    }


    /****** 报表 *******/
    @ApiOperation(value = "预期应收本金（到期）")
    @RequestMapping(value = "/sheet/searchYQYSBJDQ", method = RequestMethod.GET)
    public Result searchYQYSBJDQ(
             DataSearchService.YQYSBJDQRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchYQYSBJDQ(request,pageable));
    }
    @ApiOperation(value = "预期应收利息")
    @RequestMapping(value = "/sheet/searchYQYSLX", method = RequestMethod.GET)
    public Result searchYQYSLX(
            @Valid DataSearchService.YQYSLXRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(searchService.searchYQYSLX(request,pageable));
    }

    @ApiOperation(value = "逾期应收本金")
    @RequestMapping(value = "/sheet/searchYuQYSBJ", method = RequestMethod.GET)
    public Result searchYuQYSBJ(
            DataSearchService.YuQYSBJRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return Result.ok(searchService.searchYuQYSBJ(request, pageable));
    }

    @ApiOperation(value = "逾期应收利息")
    @RequestMapping(value = "/sheet/searchYuQYSLX", method = RequestMethod.GET)
    public Result searchYQYSLX(
            DataSearchService.YuQYSLXRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return Result.ok(searchService.searchYuQYSLX(request, pageable));
    }
}
