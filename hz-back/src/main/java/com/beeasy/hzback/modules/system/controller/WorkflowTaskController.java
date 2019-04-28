package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.modules.system.task.WorkflowTaskService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/workflow/task")
public class WorkflowTaskController {
    @Autowired
    private WorkflowTaskService workflowTask;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Result uploadFace(
    ) throws IOException {
        workflowTask.workflowTask();
        return Result.ok();
    }
}
