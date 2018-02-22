package com.beeasy.hzback.modules.setting.controller;


import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.dao.IRoleDao;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
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
@RequestMapping("/admin/setting/role")
public class RoleController {
    @Autowired
    IDepartmentDao departmentDao;
    @Autowired
    DepartmentService departmentService;

    @Autowired
    IRoleDao roleDao;


    @GetMapping("/list")
    public String list(Model model){
        try {
            model.addAttribute("tree", JSON.toJSONString(departmentService.listAsTree()));
        } catch (Exception e) {
            model.addAttribute("tree","[]");
        };
        return "setting/role_list";
    }

    /**
     * 编辑操作
     * @param role
     * @param bindingResult
     * @param departmentId
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public Result edit(
            @Valid Role role,
            BindingResult bindingResult,
            Integer departmentId
            ){
        if(bindingResult.hasErrors()){
            return Result.error(bindingResult);
        }
        Role result = roleDao.editByDepartmentId(role,departmentId);
        return result != null ? Result.ok(result) : Result.error();
    }

    /**
     * 删除操作
     * 角色不能被简单的删除，因为可能还有用户存在这个角色，具体如何处理再议
     */
}
