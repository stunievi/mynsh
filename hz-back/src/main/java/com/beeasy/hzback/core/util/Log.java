package com.beeasy.hzback.core.util;

import com.beeasy.hzback.modules.system.service.SystemLogService;
import com.beeasy.mscommon.util.U;

public class Log {

    public static void log(long uid, String content){
        try{
            U.getBean(SystemLogService.class)
                .writeLog(uid, content, null);
        }
        catch (Exception e){
        }
    }
}
