package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.entity.SysVar;
import com.beeasy.mscommon.Result;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.IO;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RequestMapping("/api/lab")
@RestController
public class LabController {

    @Autowired
    SQLManager sqlManager;

    @RequestMapping(value = "/update")
    public String update(
        @RequestParam MultipartFile file
        ){
        try {
            File temp = File.createTempFile("update","");
            IO.copy(file.getInputStream(), new FileOutputStream(temp));
            //更新前端
            if(file.getOriginalFilename().endsWith(".zip")){
                //查询前端
                SysVar sysVar = sqlManager.lambdaQuery(SysVar.class)
                    .andEq(SysVar::getVarName, "html_path")
                    .single();
                if(null == sysVar){
                    throw new IOException();
                }
                Project p = new Project();
                Expand e = new Expand();
                e.setProject(p);
                e.setSrc(temp);
                e.setOverwrite(true);
                e.setDest(new File(sysVar.getVarValue()));
//                e.setEncoding("utf-8");
                e.execute();
            }
            else if(S.eq(file.getOriginalFilename(), "hz-back.war")){
                SysVar sysVar = sqlManager.lambdaQuery(SysVar.class)
                    .andEq(SysVar::getVarName, "hz_back_path")
                    .single();
                if(null == sysVar){
                    throw new IOException();
                }
                String war = sysVar.getVarValue();
                String path = temp.getAbsolutePath();
                Runtime.getRuntime().exec(S.fmt("mv -f %s %s/webapps/hz-back.war", path, war));
                Runtime.getRuntime().exec(S.fmt("%s/bin/shutdown.sh && sleep 1 && %s/bin/startup.sh", war, war));
            }

            //clean
            temp.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return "<script>alert('系统已更新, 可能需要数分钟重启时间, 请稍后再操作')</script>";
    }
}
