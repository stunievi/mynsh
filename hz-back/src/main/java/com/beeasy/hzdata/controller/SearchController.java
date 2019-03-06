package com.beeasy.hzdata.controller;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzdata.service.SearchService;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.entity.BeetlPager;
import com.beeasy.hzback.entity.LoanManager;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;


@Transactional
@RestController
@RequestMapping("/api")
@Slf4j
public class SearchController {

//    @Autowired
//    BackRpc backRpc;

    @Autowired
    SearchService searchService;

    @Autowired
    SQLManager sqlManager;

    @RequestMapping(value = "/search/accloan/{no}", method = RequestMethod.GET)
    public Result search(
            @PathVariable String no
            , @RequestParam Map<String, Object> params
    ) {

        final long uid = AuthFilter.getUid();
        return Result.ok(searchService.search(no,params, uid, false));
    }


    @RequestMapping(value = "/search/loanrm/add")
    public Result addRm(
        @Validated(value = ValidGroup.Add.class) LoanManager loanManager
        ){
        loanManager.setLastModify(new Date());
        sqlManager.insert(loanManager,true);
        return Result.ok(loanManager);
    }

    @RequestMapping(value = "/search/loanrm/edit")
    public Result editRm(
        @Validated(value = ValidGroup.Edit.class) LoanManager loanManager
    ){
        loanManager.setLastModify(new Date());
        sqlManager.updateById(loanManager);
        return Result.ok(loanManager);
    }

    @RequestMapping(value = "/search/loanrm/set")
    public Result setRM(
        @Validated(value = ValidGroup.Edit.class) LoanManager loanManager
    ){
        loanManager.setLastModify(new Date());
        int row = sqlManager.updateById(loanManager);
        if(row == 0){
            sqlManager.insert(loanManager);
        }
        return Result.ok(loanManager);
    }

    @RequestMapping(value = "/search/loanrm/getOne")
    public Result getLoanRM(
        @RequestParam String loanAccount
    ){
        return Result.ok(sqlManager.selectSingle("system.查询实际控制人", C.newMap("loanAccount", loanAccount), JSONObject.class));
    }

    @RequestMapping(value = "/search/loanrm/getList")
    public Result getLoanRMList(
        @RequestParam Map<String,Object> params
    ){
        return Result.ok(
            sqlManager.select("system.查询实际控制人列表", JSONObject.class, params)
        );
    }

    @RequestMapping(value = "/search/loanrm/delete")
    public Result deleteLoanRM(
        @RequestParam Long[] loanAccount
    ){
        sqlManager.lambdaQuery(LoanManager.class)
            .andIn(LoanManager::getLoanAccount, Arrays.asList(loanAccount))
            .delete();
        return Result.ok();
    }

}

