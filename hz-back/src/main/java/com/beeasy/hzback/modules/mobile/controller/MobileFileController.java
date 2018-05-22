package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.ICloudDirectoryIndexDao;
import com.beeasy.hzback.modules.system.dao.IMessageDao;
import com.beeasy.hzback.modules.system.dao.ISystemFileDao;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.SystemFile;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/api/mobile")
public class MobileFileController {

    @Autowired
    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
    @Autowired
    ISystemFileDao systemFileDao;
    @Autowired
    IMessageDao messageDao;

    @GetMapping("/clouddisk/user/{id}")
    public ResponseEntity<byte[]> getCloudFile(@PathVariable Long id) throws IOException {
        //检查这个文件是不是属于你
        CloudDirectoryIndex cloudDirectoryIndex = cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndId(ICloudDiskService.DirType.USER, Utils.getCurrentUserId(),id).orElse(null);
        if(null == cloudDirectoryIndex){
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
        SystemFile file = cloudDirectoryIndex.getFile();
//        SystemFile file = systemFileDao.findFirstByIdAndType(id, SystemFile.Type.MESSAGE).orElse(null);
//        if(null == file){
//            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
//        }
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<byte[]>(file.getFile(), headers, HttpStatus.OK);
    }

    @ApiOperation(value = "用户头像下载")
    @GetMapping("/download/face/{id}")
    public ResponseEntity<byte[]> getFace(@PathVariable String id) throws IOException{
        SystemFile file = systemFileDao.findFirstByIdAndType(Long.valueOf(id), SystemFile.Type.FACE).orElse(null);
        if(null == file){
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<byte[]>(file.getFile(), headers, HttpStatus.OK);
    }

    @ApiOperation(value = "聊天文件下载")
    @GetMapping("/download/message/{id}")
    public ResponseEntity<byte[]> getMessageFile(@PathVariable Long id) throws IOException {
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
        return new ResponseEntity<byte[]>(file.getFile(), headers, HttpStatus.OK);
    }
}
