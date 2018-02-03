package com.beeasy.hzback.modules.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.modules.setting.dao.IWorkFlowDao;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.setting.entity.WorkFlow;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.Subject;


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
        if(!user.isBelongToDepartment(workFlow.getDepartment())){
            return failedUrl;
        }
        model.addAttribute("workflow", JSON.toJSONString(workFlow));
        model.addAttribute("user",JSON.toJSONString(user));
        return "workflow/list";
    }

}
