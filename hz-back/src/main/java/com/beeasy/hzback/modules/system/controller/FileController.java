package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.IDownloadFileTokenDao;
import com.beeasy.hzback.modules.system.dao.IMessageDao;
import com.beeasy.hzback.modules.system.dao.ISystemFileDao;
import com.beeasy.hzback.modules.system.entity.DownloadFileToken;
import com.beeasy.hzback.modules.system.entity.SystemFile;
import com.sun.imageio.plugins.jpeg.JPEG;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Optional;

@Api(tags = "系统内文件API")
@Controller
@RequestMapping
public class FileController {

    @Autowired
    SystemConfigCache cache;
    @Autowired
    ISystemFileDao systemFileDao;
    @Autowired
    IMessageDao messageDao;
    @Autowired
    IDownloadFileTokenDao fileTokenDao;

    @ApiOperation(value = "获取头像")
    @GetMapping("/open/face/{id}")
    public ResponseEntity<byte[]> getFace(@PathVariable String id) throws IOException{
        SystemFile file = systemFileDao.findFirstByIdAndType(Long.valueOf(id), SystemFile.Type.FACE).orElse(null);
        if(null == file){
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
        HttpHeaders headers = new HttpHeaders();
        if(file.getExt().equalsIgnoreCase("jpg") || file.getExt().equalsIgnoreCase("jpeg")){
            headers.setContentType(MediaType.IMAGE_JPEG);
        }
        else if(file.getExt().equalsIgnoreCase("png")){
            headers.setContentType(MediaType.IMAGE_PNG);
        }
        return new ResponseEntity<byte[]>(file.getBytes(), headers, HttpStatus.OK);
    }


    @GetMapping("/api/download/message/{id}")
    public ResponseEntity<byte[]> getMessageFile(@PathVariable Long id) throws IOException{
        //检查这个文件是不是属于你
        Optional optional = messageDao.findContainsFileMessage(Utils.getCurrentUserId(),id);
        if(!optional.isPresent()){
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
        SystemFile file = systemFileDao.findFirstByIdAndType(id, SystemFile.Type.MESSAGE).orElse(null);
        if(null == file){
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<byte[]>(file.getBytes(), headers, HttpStatus.OK);
    }


    @ApiOperation(value = "通过令牌下载文件")
    @RequestMapping(value = "/open/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getMessageFile(@RequestParam String token) throws IOException{
        DownloadFileToken downloadFileToken = fileTokenDao.findTopByTokenAndExprTimeGreaterThan(token,new Date()).orElse(null);
        if(null == downloadFileToken){
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
        SystemFile file = systemFileDao.findById(downloadFileToken.getFileId()).orElse(null);
        if(null == file){
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
        HttpHeaders headers = new HttpHeaders();
        switch (file.getExt().toLowerCase()){
            case "jpg":
            case "jpeg":
            case "bmp":
                headers.setContentType(MediaType.IMAGE_JPEG);
                break;

            case "png":
                headers.setContentType(MediaType.IMAGE_PNG);
                break;

            case "gif":
                headers.setContentType(MediaType.IMAGE_GIF);
                break;

            default:
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        String fileName = URLEncoder.encode(file.getFileName(),"UTF-8");
        headers.set("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=utf-8''" + fileName);
        return new ResponseEntity<byte[]>(file.getBytes(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/open/cross.html",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<byte[]> cross(){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<byte[]>(cache.getCorssHtml().getBytes(), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
    }


}

