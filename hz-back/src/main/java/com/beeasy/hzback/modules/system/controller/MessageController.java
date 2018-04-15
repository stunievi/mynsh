package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.message.entity.Message;
import com.beeasy.hzback.modules.system.form.MessageAdd;
import com.beeasy.hzback.modules.system.form.MessageSearch;
import com.beeasy.hzback.modules.system.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.validation.Valid;
import java.util.Set;

@Api(tags = "消息API")
public class MessageController {
    @Autowired
    MessageService messageService;

    @ApiOperation(value = "发送消息")
    @PostMapping("/message")
    public Result  sendMessage(
            @Valid MessageAdd add,
            BindingResult bindingResult
    ){
        messageService.sendMessage(Utils.getCurrentUserId(),add.getToUserIds(),add.getTitle(),add.getContent(),add.getFiles());
        return  Result.ok();
    }

    @ApiOperation(value = "消息列表")
    @GetMapping("/messages")
    public Result<Page<Message>> getMessages(
            @Valid MessageSearch search,
            BindingResult bindingResult,
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
            ){
        return Result.ok(messageService.getMessages(Utils.getCurrentUserId(),search.getRead(),pageable));
    }

    @ApiOperation(value = "更新已读")
    @PutMapping("/message/read")
    public Result readMessage(
             Set<Long> messageIds
    ){
        messageService.readMessage(Utils.getCurrentUserId(),messageIds);
        return Result.ok();
    }





}
