package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.modules.system.service.LoanManagerService;

import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class LoanManagerController {
    @Autowired
    LoanManagerService loanManagerService;

    @RequestMapping(value = "/loanManager/test1", method = RequestMethod.GET)
    public Result a() {
        loanManagerService.sendTaskRule1();
        loanManagerService.sendTaskRule2();
        return Result.ok();
    }
}
