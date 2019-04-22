package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.modules.system.service.QccHistLogService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auto")
public class QccHistLogController {

    @Autowired
    private QccHistLogService qccHistLogService;

    @RequestMapping(value = "/test1", method = RequestMethod.GET)
    @ResponseBody
    public Result uploadFace(
    ) throws IOException {
        qccHistLogService.saveQccHisLog();
        return Result.ok();
    }
}
