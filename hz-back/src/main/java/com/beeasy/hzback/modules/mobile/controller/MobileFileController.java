package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.ICloudDirectoryIndexDao;
import com.beeasy.hzback.modules.system.dao.ISystemFileDao;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.SystemFile;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/api/mobile")
public class MobileFileController {

    @Autowired
    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
    @Autowired
    ISystemFileDao systemFileDao;

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

}
