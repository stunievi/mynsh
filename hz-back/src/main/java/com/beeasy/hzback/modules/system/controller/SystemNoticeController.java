package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.service.SystemNoticeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notice")
public class SystemNoticeController{

    @Autowired
    SystemNoticeService noticeService;


    @ApiOperation(value = "得到通知列表")
    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public Result getNoticeList(
            SystemNoticeService.SearchRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(noticeService.getMessageList(Utils.getCurrentUserId(), request, pageable));
    }


    @ApiOperation(value = "更新已读通知")
    @RequestMapping(value = "/updateRead", method = RequestMethod.GET)
    public Result updateRead(
            @RequestParam String id
    ){
        return Result.ok(noticeService.readNotice(Utils.getCurrentUserId(), Utils.convertIdsToList(id)));
    }
}