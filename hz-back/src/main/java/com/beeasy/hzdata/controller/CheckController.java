package com.beeasy.hzdata.controller;


import com.beeasy.hzdata.service.CheckService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check")
public class CheckController {

    @Autowired
    CheckService checkService;

    @RequestMapping("/clear-logs")
    public Result clearLogs(){
       checkService.clearTriggerLogs();
       return Result.ok();
    }

    @RequestMapping("/check-rule")
    public Result checkRule(
            String n
    ){
        checkService.checkMany(n);
        return Result.ok();
    }

    @RequestMapping("/generate-task")
    public Result generateTask(){
        checkService.generateAutoTask(System.out);
        return Result.ok();
    }


}
