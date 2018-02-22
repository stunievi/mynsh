package com.beeasy.hzback.modules.workflow.controller;

import com.alibaba.fastjson.JSON;
import bin.leblanc.zed.Result;
import com.beeasy.hzback.modules.setting.dao.IWorkFlowDao;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.entity.WorkFlow;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/admin/workflow/task")
public class TaskController {

    @Autowired
    IWorkFlowDao workFlowDao;

    static String failedUrl = "";


    @GetMapping("/{id}")
    public String index(
        Model model,
        @PathVariable(value = "id") Integer id
    ){
        //检查该任务是否存在
        if(id == null){
            return failedUrl;
        }
        WorkFlow workFlow = workFlowDao.findOne(id);
        if(workFlow == null){
            return failedUrl;
        }
        //检查该任务是否属于我
        org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if(!workFlow.getDepartment().hasUser(user)){
            return failedUrl;
        }

        model.addAttribute("workflow", JSON.toJSONString(workFlow));
        model.addAttribute("user",JSON.toJSONString(user));
        return "workflow/list";
    }

    @GetMapping("/add/{id}")
    public String add(
            Model model,
            @PathVariable(value = "id") Integer id
    ){
        //检查该任务是否存在
        if(id == null){
            return failedUrl;
        }
        WorkFlow workFlow = workFlowDao.findOne(id);
        if(workFlow == null){
            return failedUrl;
        }
        //检查该任务是否属于我
        org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if(!workFlow.getDepartment().hasUser(user)){
            return failedUrl;
        }
        model.addAttribute("workflow", JSON.toJSONString(workFlow));
        model.addAttribute("user",JSON.toJSONString(user));
        return "workflow/add";
    }


    /**
     * 启动新的工作流实体业务流程
     * @return
     */
    @PostMapping("/pub")
    @ResponseBody
    public Result pub(
            Integer workflowId,
            String version,
            String content
    ){
        if(workflowId == null){
            return Result.error();
        }
        WorkFlow workFlow = workFlowDao.findOne(workflowId);
        if(workFlow == null){
            return Result.error();
        }
        //检查该任务是否属于我
        org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if(!workFlow.getDepartment().hasUser(user)){
            return Result.error();
        }

        //TODO
        return Result.ok();

    }

}
