package com.beeasy.hzback.modules.system.controller;

import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@Transactional
@RestController
@RequestMapping("/api/sys")
public class SystemNoticeController {
    @Autowired
    private SQLManager sqlManager;

    /**
     * 消息全部标位已读
     */
    @RequestMapping(value = "/readAll", method = RequestMethod.GET)
    public Result toRead(@RequestParam Long[] listId) throws Exception{
        Long uid = AuthFilter.getUid();
        if(null != uid){
            for (Long id : listId ){
                sqlManager.update("system.标为已读", C.newMap("uid",uid,"id",id));
            }
//            sqlManager.update("system.标为已读", C.newMap("uid",uid));
        }
        return Result.ok();
    }

    /**
     * 删除全部已读消息
     */
    @RequestMapping(value = "/deleteAll", method = RequestMethod.GET)
    public Result toDelete() throws Exception{
        Long uid = AuthFilter.getUid();
        if(null != uid){
            sqlManager.update("system.删除已读", C.newMap("uid",uid));
        }
        return Result.ok();
    }
}
