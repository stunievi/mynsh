package com.beeasy.hzback.core.util;

import com.beeasy.hzback.modules.system.service.SystemLogService;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;

public class Log {

    public static void log(String content, int number) {
        try {
            if (number == 3) {
                U.getBean(SystemLogService.class).writeLog(AuthFilter.getUid(), content + "下载excel表格", null);
            } else {
                U.getBean(SystemLogService.class).writeLog(AuthFilter.getUid(), content, null);
            }
        } catch (Exception e) {
        }
    }

}
