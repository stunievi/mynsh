package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.service.DepartmentService;
import com.beeasy.hzback.modules.system.form.DepartmentAdd;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.internal.xjc.reader.xmlschema.BindRed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.ws.RespectBinding;

@Api(tags = "部门API", description = "部分接口不需要管理员权限")
@RestController
public class DepartmentController {

    @Autowired
    IDepartmentDao departmentDao;
    @Autowired
    DepartmentService departmentService;


    @ApiOperation(value = "部门列表", notes = "获得所有部门列表，开放API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "部门名称, 如果传递了这个属性, 那么会无视parentId进行查找"),
            @ApiImplicitParam(name = "parentId", value = "父部门ID,如果想获得顶层,请传0")
    })
    @GetMapping("/open/department")
    public Result list(
            String name,
            Integer parentId
    ){
        if(parentId == null) parentId = 0;
        //name比parent优先
        if(!StringUtils.isEmpty(name)){
            return Result.ok(departmentDao.findAllByName(name));
        }
        if(parentId == 0){
            return Result.ok(departmentDao.findAllByParent(null));
        }
        return Result.ok(departmentDao.findAllByParentId(parentId));
    }


    @ApiOperation(value = "添加部门", notes = "添加一个新部门,需要管理员权限")
    @PostMapping("/api/department")
    public Result add(
            @Valid DepartmentAdd departmentAdd,
            BindingResult bindingResult
    ){
        Department same = departmentDao.findByName(departmentAdd.getName());
        if(same != null)
            return Result.error("已有同名部门");

        Department department = Transformer.transform(departmentAdd,Department.class);
        Department result = departmentDao.save(department);

        return result.getId() > 0 ? Result.ok() : Result.error("添加失败");

    }


    /**
     * 不能随意删除,需要验证已有的工作人员/工作流
     * @param departmentId
     * @return
     */
    @ApiOperation(value = "删除部门", notes = "删除部门,需要管理员权限")
    @DeleteMapping("/api/department")
    public Result del(
            Integer departmentId
    ){
        return Result.error();
    }

    /**
     * 编辑时如果需要修改父类,那么需要验证已有的工作人员/工作流
     * @return
     */
    @ApiOperation(value = "编辑部门资料", notes = "编辑部门, 需要管理员权限")
    @PutMapping("/api/department")
    public Result edit(){
        return Result.error();
    }






//    @GetMapping("/list")
//    public String list(Model model){
//        try {
//            model.addAttribute("tree", JSON.toJSONString(departmentService.listAsTree()));
//        } catch (Exception e) {
//            model.addAttribute("tree","[]");
//            e.printStackTrace();
//        }
//        return "setting/department_list";
//    }
//
//    @GetMapping("/add")
//    public String add(Model model,Integer parentId){
//        if(parentId == null) parentId = 1;
//        Department department = departmentDao.findOne(parentId);
//        if(department == null){
//            return "redirect:list";
//        }
//        model.addAttribute("parent_id",department.getId());
//        model.addAttribute("parent_name",department.getName());
//        model.addAttribute("item",new Department());
//        return "setting/department_edit";
//    }
//
//    @GetMapping("/edit")
//    public String edit(Model model,Integer id){
//        if(id == null){
//            return "redirect:list";
//        }
//        Department department = departmentDao.findOne(id);
//        if(department == null){
//            return "redirect:list";
//        }
//        model.addAttribute("parent_id",department.getParent().getId());
//        model.addAttribute("parent_name",department.getParent().getName());
//        model.addAttribute("item",department);
//        return "setting/department_edit";
//    }
//
//    @PostMapping("/add")
//    @ResponseBody
//    public Result add(@Valid Department department, BindingResult bindingResult, Integer parent_id){
//        if(bindingResult.hasErrors()){
//            return Result.error(bindingResult.getAllErrors());
//        }
//        boolean success = departmentService.add(department,parent_id);
//        return success ? Result.ok() : Result.error("添加失败");
//    }
//
//    @PostMapping("/edit")
//    @ResponseBody
//    public Result edit(@Valid Department department,BindingResult bindingResult){
//        if(bindingResult.hasErrors()){
//            return Result.error(bindingResult);
//        }
//        boolean success = departmentService.edit(department);
//        return Result.ok();
//    }
//
//    @PostMapping("/delete")
//    @ResponseBody
//    public Result delete(Integer id){
//        Department department = departmentDao.findOne(id);
//        if(department == null){
//            return Result.ok();
//        }
//        departmentDao.delete(id);
//        return Result.ok();
//    }

}
