package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.zed.Zed;
import com.beeasy.hzback.core.helper.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ZedController {

    @Autowired
    Zed zed;

    @PostMapping("/")
    public Result zed(String request){
        try {
            Map<?,?> result = zed.parse(request,"");
            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error();
    }


    @RequestMapping("/config")
    public synchronized Result getConfig(){
        String str = null;
        try {
            InputStream is = new FileInputStream("/Users/bin/work/configlist.yaml");
            StringBuffer sb = new StringBuffer();
            byte[] bytes = new byte[1024];
            int len;
            while((len = is.read(bytes)) != -1){
                sb.append(new String(bytes,0,len));
                sb.append("\n");
            }
            str = sb.toString();
        } catch (IOException e) {
        }
        return Result.ok(str);
    }
}
