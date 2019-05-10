package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.modules.system.service.QccHistLogService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/api/auto")
public class QccHistLogController {

    @Autowired
    private QccHistLogService qccHistLogService;

    @RequestMapping(value = "/test1", method = RequestMethod.GET)
    public void uploadFace(HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("text/plain; charset=utf-8");
        try {
            OutputStream os = response.getOutputStream();
            qccHistLogService.os = os;
            qccHistLogService.saveQccHisLog();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            qccHistLogService.os = System.out;
        }

    }
    @RequestMapping(value = "/deleteHistLog", method = RequestMethod.GET)
    public synchronized void deleteTable(HttpServletResponse response, @RequestParam String cusName) throws IOException{
        response.setStatus(200);
        response.setContentType("text/plain; charset=utf-8");
        try {
            OutputStream os = response.getOutputStream();
            QccHistLogService.os = os;
            qccHistLogService.deleteQccHistLog(cusName);

            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            QccHistLogService.os = System.out;
        }
    }
}
