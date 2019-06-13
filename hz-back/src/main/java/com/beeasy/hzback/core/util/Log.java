package com.beeasy.hzback.core.util;

import com.beeasy.hzback.entity.SystemFile;
import com.beeasy.hzback.modules.system.controller.FileController;
import com.beeasy.hzback.modules.system.service.SystemLogService;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.mockito.Mock;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
    public static void log(String content, Object ...args){
        try{
            U.getBean(SystemLogService.class)
                    .writeLog(AuthFilter.getUid(), S.fmt(content, args), null);
        }
        catch (Exception e){
        }

    }

}
