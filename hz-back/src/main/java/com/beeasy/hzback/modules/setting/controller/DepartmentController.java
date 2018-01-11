package com.beeasy.hzback.modules.setting.controller;

import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
@RequestMapping("/admin/setting/department")
public class DepartmentController {

    static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    DepartmentService departmentService;

    @GetMapping("/list")
    public String list(Model model){
        try {
            Set<Department> set = departmentService.listAsTree();
            model.addAttribute("tree",mapper.writeValueAsString(set));
        } catch (JsonProcessingException e) {
            model.addAttribute("tree","[]");
            e.printStackTrace();
        }
        return "setting/department_list";
    }


}
