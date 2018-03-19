package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.zed.Zed;
import com.beeasy.hzback.core.helper.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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
        try {
            RandomAccessFile raf = new RandomAccessFile("/Users/bin/work/configlist.yaml","rw");
            String line ;
            StringBuffer sb = new StringBuffer();
            while((line = raf.readLine()) != null){
                sb.append(new String(line.getBytes("iso8859-1")));
                sb.append("\n");
            }
            raf.close();
            String str = sb.toString();
            return Result.ok(str);

        } catch (IOException e) {
        }
        return Result.error();

    }
}
