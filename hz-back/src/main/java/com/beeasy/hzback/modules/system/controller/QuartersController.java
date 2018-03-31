package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.classtranslate.Transformer;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.setting.dao.IDepartmentDao;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.system.dao.IQuartersDao;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.form.QuartersAdd;
import com.beeasy.hzback.modules.system.form.QuartersEdit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "岗位API", description = "需要管理员权限")
@RestController
@RequestMapping("/api/quarters")
public class QuartersController {

    @Autowired
    IQuartersDao quartersDao;

    @Autowired
    IDepartmentDao departmentDao;

    @GetMapping("")
    @ApiOperation(value = "岗位列表", notes = "查找岗位列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", value = "部门ID")
    })
    public Result<Page<Quarters>> list(
            Pager pager,
            Integer departmentId,
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable
    ){
        if(departmentId != null){
            return Result.ok(quartersDao.findAllByDepartment_Id(departmentId,pageable));
        }
        return Result.ok(quartersDao.findAll(pageable));
    }

    @ApiOperation(value = "添加岗位", notes = "添加一个岗位")
    @PostMapping("")
    public Result add(
            @Valid QuartersAdd add,
            BindingResult bindingResult
            ){
        Department department = departmentDao.findOne(add.getDepartmentId());
        if(department == null) return Result.error("所属部门填写错误");
        Quarters quarters = Transformer.transform(add,Quarters.class);
        quarters.setDepartment(department);
        Quarters ret = quartersDao.save(quarters);
        return ret.getId() > 0 ? Result.ok() : Result.error("添加失败");
    }

    /**
     * 编辑需要验证当前符合岗位的人
     * 禁止修改所属部门
     * @return
     */
    @ApiOperation(value = "编辑岗位", notes = "编辑一个岗位")
    @PutMapping("")
    public Result edit(
            @Valid QuartersEdit edit,
            BindingResult bindingResult
    ){
        Quarters quarters = quartersDao.findOne(edit.getId());
        if(quarters == null) return Result.error("岗位ID不对");
        quarters = Transformer.transform(edit,quarters);
        quartersDao.save(quarters);
        return Result.ok();
    }


    /**
     * 删除, 需要验证这个岗位是否还有人
     * @return
     */
    public Result delete(){

        return Result.error();
    }

}
