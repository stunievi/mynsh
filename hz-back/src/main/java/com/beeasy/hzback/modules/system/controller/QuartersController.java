package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.IDepartmentDao;
import com.beeasy.hzback.modules.system.dao.IQuartersDao;
import com.beeasy.common.entity.Quarters;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.request.QuartersAddRequest;
import com.beeasy.hzback.modules.system.request.QuartersEditRequest;
import com.beeasy.hzback.modules.system.service.QuartersService;
import com.beeasy.hzback.modules.system.service.UserService;
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
    QuartersService quartersService;
    @Autowired
    IQuartersDao quartersDao;
    @Autowired
    UserService userService;

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
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable
    ) {
        if (departmentId != null) {
            return Result.ok(quartersDao.findAllByDepartment_Id(departmentId, pageable));
        }
        return Result.ok(quartersDao.findAll(pageable));
    }

    @ApiOperation(value = "添加岗位", notes = "添加一个岗位")
    @PostMapping("")
    public Object add(
            @Valid QuartersAddRequest add,
            BindingResult bindingResult
    ) throws RestException {

        return Result.ok(userService.createQuarters(add));
    }

    /**
     * 编辑需要验证当前符合岗位的人
     * 禁止修改所属部门
     *
     * @return
     */
    @ApiOperation(value = "编辑岗位", notes = "编辑一个岗位")
    @PostMapping("/edit")
    public Result edit(
            @Valid QuartersEditRequest edit
    ) {
        /*
         * @gotomars
         * */
        return Result.ok(userService.editQuarters(edit));
    }


    /**
     * 删除, 需要验证这个岗位是否还有人
     *
     * @return
     */
    @ApiOperation(value = "删除岗位")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Result delete(
            @RequestParam String id
    ) {
        return Result.ok(userService.deleteQuarters(Utils.convertIdsToList(id)));
    }

}
