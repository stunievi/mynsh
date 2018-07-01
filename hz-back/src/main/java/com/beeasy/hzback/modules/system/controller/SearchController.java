package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.service.DataSearchService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
        return Result.ok(searchService.searchPublicClient(request, pageable));
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


}
