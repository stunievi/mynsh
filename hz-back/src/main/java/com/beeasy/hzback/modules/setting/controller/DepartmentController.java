package com.beeasy.hzback.modules.setting.controller;

import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.lib.zed.Result;
import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/setting/department")
public class DepartmentController {

    static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    IDepartmentDao departmentDao;
    @Autowired
    DepartmentService departmentService;

    @GetMapping("/list")
    public String list(Model model){
        try {
            model.addAttribute("tree", JSON.toJSONString(departmentService.listAsTree()));
        } catch (Exception e) {
            model.addAttribute("tree","[]");
            e.printStackTrace();
        }
        return "setting/department_list";
    }

    @GetMapping("/add")
    public String add(Model model,Integer parentId){
        if(parentId == null) parentId = 1;
        Department department = departmentDao.findOne(parentId);
        if(department == null){
            return "redirect:list";
        }
        model.addAttribute("parent_id",department.getId());
        model.addAttribute("parent_name",department.getName());
        model.addAttribute("item",new Department());
        return "setting/department_edit";
    }

    @GetMapping("/edit")
    public String edit(Model model,Integer id){
        if(id == null){
            return "redirect:list";
        }
        Department department = departmentDao.findOne(id);
        if(department == null){
            return "redirect:list";
        }
        model.addAttribute("parent_id",department.getParent().getId());
        model.addAttribute("parent_name",department.getParent().getName());
        model.addAttribute("item",department);
        return "setting/department_edit";
    }

    @PostMapping("/add")
    @ResponseBody
    public Result add(@Valid Department department, BindingResult bindingResult, Integer parent_id){
        if(bindingResult.hasErrors()){
            return Result.error(bindingResult.getAllErrors());
        }
        boolean success = departmentService.add(department,parent_id);
        return success ? Result.ok() : Result.error("添加失败");
    }

    @PostMapping("/edit")
    @ResponseBody
    public Result edit(@Valid Department department,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return Result.error(bindingResult);
        }
        boolean success = departmentService.edit(department);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ResponseBody
    public Result delete(Integer id){
        Department department = departmentDao.findOne(id);
        if(department == null){
            return Result.ok();
        }
        departmentDao.delete(id);
        return Result.ok();
    }

}
