package com.beeasy.hzback.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.log.NotSaveLog;
import com.beeasy.hzback.modules.system.service.ImService;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.mscommon.entity.BeetlPager;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.hzback.view.DepartmentUser;
import com.beeasy.mscommon.util.U;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Api(tags = "消息API")
@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    ImService imService;
    @Autowired
    SQLManager sqlManager;

    @RequestMapping(value = "/sendString", method = RequestMethod.POST)
    public Result sendStringMessage(
        @RequestParam Long toUid
        , String content
        , @RequestParam(required = false) MultipartFile file
    ) {
        return Result.ok(imService.sendMessage(Utils.getCurrentUserId(),toUid, content, file ));
    }

    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public Result getList(){
        return    Result.ok(sqlManager.select("im.查询未读消息", JSONObject.class, C.newMap("uid", AuthFilter.getUid())) );
    }

    @RequestMapping(value = "/chatlog/getList", method = RequestMethod.GET)
    public Result getChatLogList(
        @RequestParam long toUid
        , BeetlPager beetlPager
    ){
        return Result.ok(imService.getChatLogList(AuthFilter.getUid(),toUid, beetlPager));
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Object sendFile(
        @RequestParam MultipartFile file
    ){
        return imService.uploadFile(Utils.getCurrentUserId(), file);
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public Result readMessages(
        @RequestParam Long[] toUids
    ){
        imService.readMessages(AuthFilter.getUid(), Arrays.asList(toUids));
        return Result.ok();
    }

    @RequestMapping(value = "/user/getList", method = RequestMethod.GET)
    public Result getUserList(
        BeetlPager beetlPager
//         @PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(
            Utils.beetlPageQuery("im.查找用户", DepartmentUser.class, C.newMap(), beetlPager)
        );
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

//    @RequestMapping(value = "/readMessages", method = RequestMethod.POST)
//    public Result<List<ReadMessageResponse>> readMessages(
//            @RequestBody Long[] toUids
//    ) {
//        return Result.ok(messageService.userReadMessage(Utils.getCurrentUserId(), toUids));
//    }

    @ApiOperation(value = "获取未读消息人列表")
    @GetMapping("/getUnreadUserList")
    public Result getUnreadUserList() {
        //todo:
        return Result.ok();
    }

    @NotSaveLog
    @RequestMapping(value = "/sendFile", method = RequestMethod.POST)
    public JSONObject sendFile(
            @RequestParam Long toUid,
            @RequestParam Long fileId
    ) throws IOException {
        JSONObject object = new JSONObject();
        return object;
//        return Result.finish(
//                messageService.sendMessage(Utils.getCurrentUserId(), toUid, fileId)
//        );
    }


    @GetMapping("/userRecentMessage")
    public Result getUserRecentMessages(
            Long messageId,
            Long userId
    ) {
        return Result.ok(
            //todo:
//                messageService.getUserRecentMessages(Utils.getCurrentUserId(), userId, messageId).stream()
//                        .map(item -> {
//                            return messageService.applyDownload(Utils.getCurrentUserId(), item);
////                            JSONObject object = (JSONObject) JSON.toJSON(item);
////                            object.put("token", messageService.applyDownload(Utils.getCurrentUserId(), item));
////                            return object;
//                        })
//                        .collect(Collectors.toList())
        );
    }




    /*** 消息相关 ***/
    @Autowired
    NoticeService2 noticeService;


    @ApiOperation(value = "得到通知列表")
    @RequestMapping(value = "/notice/getList", method = RequestMethod.GET)
    public Result getNoticeList(
        @RequestParam Map<String,Object> params
    ) {
        params.put("uid", AuthFilter.getUid());
        return Result.ok(
            U.beetlPageQuery("im.查询系统通知", JSONObject.class, params)
        );
    }


    @ApiOperation(value = "更新已读通知")
    @RequestMapping(value = "/updateRead", method = RequestMethod.GET)
    public Result updateRead(
    ) {
        noticeService.readNotice(Utils.getCurrentUserId());
        return Result.ok();
    }


}
