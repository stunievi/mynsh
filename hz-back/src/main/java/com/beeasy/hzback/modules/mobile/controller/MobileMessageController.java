package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.mobile.request.StringMessageRequest;
import com.beeasy.hzback.modules.system.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/mobile/message")
public class MobileMessageController {
    @Autowired
    MessageService messageService;

    @PostMapping("/sendString")
    public Result sendStringMessage(
            @Valid @RequestBody StringMessageRequest request
            ){
        return Result.finish(messageService.sendMessage(Utils.getCurrentUserId(),request.getToUid(),request.getContent()));
    }

    @PostMapping("/sendFile")
    public Result sendFile(
            @RequestParam Long toUid,
            @RequestParam MultipartFile file
    ) throws IOException {
        return Result.finish(messageService.sendMessage(Utils.getCurrentUserId(),toUid,file.getBytes()));
    }

    @GetMapping("/userRecentMessage")
    public Result getUserRecentMessages(
            Long messageId,
            Long userId
    ){
        return Result.ok(messageService.getUserRecentMessages(Utils.getCurrentUserId(),userId,messageId));
    }

}
