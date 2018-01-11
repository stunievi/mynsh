package com.beeasy.hzback.modules.setting.controller;

import com.beeasy.hzback.modules.setting.service.DepartmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/setting/department")
public class DepartmentController {

    static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    DepartmentService departmentService;

    @GetMapping("/list")
    public String list(Model model){
        try {
            model.addAttribute("tree",mapper.writeValueAsString(departmentService.listAsTree()));
        } catch (JsonProcessingException e) {
            model.addAttribute("tree","[]");
            e.printStackTrace();
        }
        return "setting/department_list";
    }


}
