package com.beeasy.hzback.modules.setting.controller;

import com.beeasy.hzback.modules.setting.dao.IWorkDao;
import com.beeasy.hzback.modules.setting.dao.IWorkFlowDao;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/setting/workflow")
public class WorkFlowController {

    @Autowired
    IWorkFlowDao workFlowDao;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    IWorkDao workDao;

    static ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/list")
    public String list(
            Model model,
           @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){
        model.addAttribute("list",workFlowDao.findAll(pageable));
        return "setting/workflow_list";
    }


    /**
     * 新增，需查出所有的部门
     * @param model
     * @return
     */
    @GetMapping("/add")
    public String add(
            Model model
    ){
        try {
            model.addAttribute("departments",objectMapper.writeValueAsString(departmentService.listAsTree()));
            //列出所有业务模型
            model.addAttribute("works",objectMapper.writeValueAsString(workDao.findAll()));
        } catch (JsonProcessingException e) {
            model.addAttribute("departments","[]");
            model.addAttribute("works","[]");
            e.printStackTrace();
        }
        return "setting/workflow_edit";
    }
}
