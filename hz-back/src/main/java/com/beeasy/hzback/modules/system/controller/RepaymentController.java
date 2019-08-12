package com.beeasy.hzback.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.RepayAcc;
import com.beeasy.hzback.entity.RepayLoan;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 还款
@RestController
@RequestMapping("/api/repayment")
public class RepaymentController {

    @Autowired
    SQLManager sqlManager;

    // 还款余额不足 - 客户列表
    @RequestMapping(value = "warn/cusList", method = RequestMethod.GET)
    public Result warnCusList(
            @RequestParam Map<String, Object> params
    ){
        params.put("uid", AuthFilter.getUid());
        return Result.ok(U.beetlPageQuery("accloan.repay_cus_list", JSONObject.class, params));
    }

    // 还款余额不足客户 - 账户列表
    @RequestMapping(value = "warn/account", method = RequestMethod.GET)
    public Result warnCusAccList(
            @RequestParam("cusId") String cusId
    ){
        AuthFilter.getUid();

        return Result.ok(
                sqlManager.select("accloan.repay_acct_info", JSONObject.class, C.newMap("cusId", cusId))
        );
    }

    // 还款余额不足客户账户关联台账
    @RequestMapping(value = "warn/linkLoan", method = RequestMethod.GET)
    public Result warnCusAccLinkLoan(
            @RequestParam("accountId") String accountId
    ){
        AuthFilter.getUid();
        return Result.ok(
                sqlManager.select("accloan.repay_loan_acct_info", JSONObject.class, C.newMap("repaymentAccount", accountId))
        );
    }

}
