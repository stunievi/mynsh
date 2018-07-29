package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.form.DepartmentAdd;
import com.beeasy.hzback.modules.system.form.DepartmentEdit;
import com.beeasy.hzback.modules.system.service.DepartmentService;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "部门API", description = "部分接口不需要管理员权限")
@RestController
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;
    @Autowired
    UserService userService;

    @Autowired
    IDepartmentDao departmentDao;
//    @Autowired
//    DepartmentService2 departmentService;


    @ApiOperation(value = "部门列表", notes = "获得所有部门列表，开放API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "部门名称, 如果传递了这个属性, 那么会无视parentId进行查找"),
            @ApiImplicitParam(name = "parentId", value = "父部门ID,如果想获得顶层,请传0")
    })
    @GetMapping("/open/department")
    public Result list(
            String name,
            Long parentId
    ){
        //name比parent优先
        return Result.ok(departmentService.findDepartments(name,parentId));
    }


    @ApiOperation(value = "添加部门", notes = "添加一个新部门,需要管理员权限")
    @PostMapping("/api/department")
    public Result add(
            @Valid DepartmentAdd departmentAdd
    ){
        return Result.ok(userService.createDepartment(departmentAdd));
    }


    /**
     * 不能随意删除,需要验证已有的工作人员/工作流
     * @param departmentId
     * @return
     */
    @ApiOperation(value = "删除部门", notes = "删除部门,需要管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", value = "部门ID", required = true)
    })
    @DeleteMapping("/api/department")
    public Result del(
            @RequestParam Long departmentId
    ){
        return Result.finish(userService.deleteDepartment(departmentId));
    }

    /**
     * 编辑时如果需要修改父类,那么需要验证已有的工作人员/工作流
     * @return
     */
    @ApiOperation(value = "编辑部门资料", notes = "编辑部门, 需要管理员权限")
    @PutMapping("/api/department")
    public Result edit(
            @Valid DepartmentEdit edit
            ){
        return Result.ok(userService.editDepartment(edit));
    }



    @GetMapping("/alldepartment")
    public Result alllist(){
        return Result.ok(departmentDao.findAll());
    }

}
