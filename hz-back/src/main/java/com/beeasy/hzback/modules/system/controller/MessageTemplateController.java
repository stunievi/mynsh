//package com.beeasy.hzback.modules.system.controller;
//
//import com.beeasy.hzback.core.helper.Result;
//import com.beeasy.hzback.modules.system.dao.IMessageTemplateDao;
//import com.beeasy.hzback.modules.system.form.Pager;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Api(tags = "消息模板接口", description = "需要管理员权限")
//@RestController
//@RequestMapping("/api/message_template")
//public class MessageTemplateController {
//
//    @Autowired
//    IMessageTemplateDao messageTemplateDao;
//
//    /**
//     * 不分页, 一次性列出所有模板
//      * @return
//     */
//    @ApiOperation(value = "消息模板列表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", value = "模板名")
//    })
//    @GetMapping
//    public Result list(
//            String name,
//            Pager pager,
//            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        if(!StringUtils.isEmpty(name)){
//            return Result.ok(messageTemplateDao.findAllByName(name,pageable));
//        }
//        return Result.ok(messageTemplateDao.findAll(pageable));
//    }
//
//    @ApiOperation(value = "消息规则列表")
//    @GetMapping("/rules_list")
//    public Result rulesList(){
//        return Result.error();
//    }
//}
