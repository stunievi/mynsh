package com.beeasy.loadqcc.controller;

import com.beeasy.loadqcc.service.GetQccService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qcc")
public class QccDataController {

    @Autowired
    GetQccService getQccService;

    @GetMapping(value = "/getAllQccData")
    Result ECI_GetDetailsByName(
           @RequestParam("keyword") String keyword
    ){
        getQccService.loadAllData(keyword);
        return Result.ok(keyword);
    }

}
