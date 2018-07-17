package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.mobile.request.StringMessageRequest;
import com.beeasy.hzback.modules.mobile.response.ReadMessageResponse;
import com.beeasy.hzback.modules.system.dao.IMessageReadDao;
import com.beeasy.hzback.modules.system.form.MessageAdd;
import com.beeasy.hzback.modules.system.log.NotSaveLog;
import com.beeasy.hzback.modules.system.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Api(tags = "消息API")
@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    MessageService messageService;

    @Autowired
    IMessageReadDao messageReadDao;

    @PostMapping("/sendString")
    public String sendStringMessage(
            @Valid @RequestBody StringMessageRequest request
    ){
        return Result.okJson(messageService.sendMessage(Utils.getCurrentUserId(),request.getToUid(),request.getContent(),request.getUuid()));
    }

//    @ApiOperation(value = "消息列表")
//    @GetMapping("/messages")
//    public Result<Page<Message>> getMessages(
//            @Valid MessageSearch search,
//            BindingResult bindingResult,
//            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
//            ){
//        return Result.ok(messageService.getMessages(Utils.getCurrentUserId(),search.getRead(),pageable));
//    }

//    @ApiOperation(value = "更新已读")
//    @PutMapping("/message/read")
//    public Result readMessage(
//             Set<Long> messageIds
//    ){
//        messageService.readMessage(Utils.getCurrentUserId(),messageIds);
//        return Result.ok();
//    }

    @RequestMapping(value = "/readMessages", method = RequestMethod.POST)
    public Result<List<ReadMessageResponse>> readMessages(
            @RequestBody Long[] toUids
    ){
        return Result.ok(messageService.userReadMessage(Utils.getCurrentUserId(),toUids));
    }

    @ApiOperation(value = "获取未读消息人列表")
    @GetMapping("/getUnreadUserList")
    public Result getUnreadUserList(){
        return  Result.ok(messageReadDao.findAllByUser_IdAndUnreadNumGreaterThan(Utils.getCurrentUserId(), 0));
    }

    @NotSaveLog
    @PostMapping("/sendFile")
    public String sendFile(
            @RequestParam Long toUid,
            @RequestParam MultipartFile file
    ) throws IOException {
        return Result.okJson(messageService.sendMessage(Utils.getCurrentUserId(),toUid,file));
    }


    @GetMapping("/userRecentMessage")
    public Result getUserRecentMessages(
            Long messageId,
            Long userId
    ){
        return Result.ok(messageService.getUserRecentMessages(Utils.getCurrentUserId(),userId,messageId));
    }

}
