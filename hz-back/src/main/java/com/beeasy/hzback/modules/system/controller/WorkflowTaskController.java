package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.modules.system.task.WorkflowTaskService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowTaskController {
    @Autowired
    private WorkflowTaskService workflowTask;

    @RequestMapping(value="/task",method = RequestMethod.GET)
    @ResponseBody
    public Result uploadFace(
    ) throws IOException {
        workflowTask.workflowTask();
        return Result.ok();
    }

    @RequestMapping(value="/sendMsg",method = RequestMethod.GET)
    public Result sendMsg(@RequestParam Long id) throws IOException {
        workflowTask.sendUrge(id);
        return Result.ok();
    }
}
