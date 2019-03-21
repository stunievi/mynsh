package com.beeasy.hzqcc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

    @GetMapping(value = "/ECI_GetDetailsByName")
    Result ECI_GetDetailsByName(
            @RequestParam("keyWord") String keyWord
    ){

        return Result.ok(
                qccService.ECI_GetBasicDetailsByName(keyWord)
        );
    }

    // 裁判文书列表
    @GetMapping(value = "/JudgeDoc_SearchJudgmentDoc")
    Result JudgeDoc_SearchJudgmentDoc(
            @RequestParam() Map param
    ){
       JSONObject object = (JSONObject) JSON.toJSON(param);
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
    // 开庭公告查询
    @GetMapping(value = "/CourtAnno_SearchCourtNotice")
    Result CourtAnno_SearchCourtNotice(
            @RequestParam() Map param
    ){
        return Result.ok(
                qccService.CourtAnno_SearchCourtNotice(param)
        );
    }
    // 开庭公告详情查询
    @GetMapping(value = "/CourtAnno_GetCourtNoticeInfo")
    Result CourtAnno_GetCourtNoticeInfo(
            @RequestParam("id") String Id
    ){

        return Result.ok(
                qccService.CourtAnno_GetCourtNoticeInfo(Id)
        );
    }

    // 历史工商信息
    @GetMapping(value = "/History/GetHistorytEci")
    Result History_GetHistorytEci(
            @RequestParam("keyWord") String keyWord
    ){

        return Result.ok(
                qccService.History_GetHistorytEci(keyWord)
        );
    }


}
