package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.mobile.request.StringMessageRequest;
import com.beeasy.hzback.modules.mobile.response.ReadMessageResponse;
import com.beeasy.hzback.modules.mobile.response.UnreadMessageResponse;
import com.beeasy.hzback.modules.system.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/mobile/message")
public class MobileMessageController {
    @Autowired
    MessageService messageService;

    @PostMapping("/sendString")
    public String sendStringMessage(
            @Valid @RequestBody StringMessageRequest request
            ){
        return Result.finish(messageService.sendMessage(Utils.getCurrentUserId(),request.getToUid(),request.getContent(),request.getUuid())).toMobile();
    }

    @PostMapping("/sendFile")
    public String sendFile(
            @RequestParam Long toUid,
            @RequestParam MultipartFile file
    ) throws IOException {
        return Result.finish(messageService.sendMessage(Utils.getCurrentUserId(),toUid,file)).toMobile();
    }

    @GetMapping("/userRecentMessage")
    public Result getUserRecentMessages(
            Long messageId,
            Long userId
    ){
        return Result.ok(messageService.getUserRecentMessages(Utils.getCurrentUserId(),userId,messageId));
    }

    @PostMapping("/userUnreadNums")
    public Result<List<UnreadMessageResponse>> getUnreadMessages(
            @RequestBody Long[] toUids
            ){
        return Result.ok(messageService.getUnreadNums(Utils.getCurrentUserId(),toUids));
    }

    @PostMapping("/readMessages")
    public Result<List<ReadMessageResponse>> readMessages(
            @RequestBody Long[] toUids
    ){
        return Result.ok(messageService.userReadMessage(Utils.getCurrentUserId(),toUids));
    }

}
