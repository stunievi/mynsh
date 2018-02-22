package com.beeasy.hzback.modules.setting.controller;

import com.alibaba.fastjson.JSON;
import bin.leblanc.zed.Result;
import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.dao.IWorkDao;
import com.beeasy.hzback.modules.setting.dao.IWorkFlowDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/setting/workflow")
public class WorkFlowController {

    @Autowired
    IWorkFlowDao workFlowDao;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    IDepartmentDao departmentDao;

    @Autowired
    IWorkDao workDao;


    @GetMapping("/list")
    public String list(
            Model model,
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("list", workFlowDao.findAll(pageable));
        return "setting/workflow_list";
    }


    /**
     * 新增，需查出所有的部门
     *
     * @param model
     * @return
     */
    @GetMapping("/add")
    public String add(
            Model model
    ) {
        try {
            //列出所有业务模型
            model.addAttribute("works", JSON.toJSONString(workDao.findAll()));
            model.addAttribute("departments", JSON.toJSONString(departmentService.listAsTree()));

        } catch (Exception e) {
            model.addAttribute("departments", "[]");
            model.addAttribute("works", "[]");
            e.printStackTrace();
        }
        return "setting/workflow_edit";
    }


    /**
     * 查询该部门所有的人员
     */
    @PostMapping("/department_users")
    @ResponseBody
    public Result searchDepartmentUsers(
            Integer departmentId
    ) {
        //得到该部门所有的角色
        if (departmentId == null) {
            return Result.error();
        }
        Department department = departmentDao.findOne(departmentId);
        if (department == null) {
            return Result.error();
        }

        return Result.ok(department.getUsers());
    }


    @PostMapping("/add")
    @ResponseBody
    public Result add(
            Integer departmentId,
            Integer workId,
            String workflowNodeList,
            Double version
    ) {
        workFlowDao.deployWork(departmentId, workId, workflowNodeList,version);
        return Result.ok();
    }
}
