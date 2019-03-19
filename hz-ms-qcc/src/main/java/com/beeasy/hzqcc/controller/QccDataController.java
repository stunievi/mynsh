package com.beeasy.hzqcc.controller;

import com.beeasy.hzqcc.service.QccService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/qcc")
public class QccDataController {

    @Autowired
    QccService qccService;

    @GetMapping(value = "/ECI/GetDetailsByName")
    Result ECI_GetDetailsByName(
            String keyword
    ){
        qccService.ECI_GetDetailsByName(keyword);
        return Result.ok("t");
    }

    // 裁判文书列表
    @GetMapping(value = "/JudgeDoc_SearchJudgmentDoc")
    Result JudgeDoc_SearchJudgmentDoc(
            @RequestParam() Map param
    ){
        return Result.ok(
                qccService.JudgeDoc_SearchJudgmentDoc(param)
        );
    }

    // 裁判文书详情
    @GetMapping(value = "/JudgeDoc_GetJudgementDetail")
    Result JudgeDoc_GetJudgementDetail(
            @RequestParam("id") String Id
    ){

        return Result.ok(
                qccService.JudgeDoc_GetJudgementDetail(Id)
        );
    }
}
