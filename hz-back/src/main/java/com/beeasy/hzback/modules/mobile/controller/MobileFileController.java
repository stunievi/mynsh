package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.IMessageDao;
import com.beeasy.hzback.modules.system.dao.ISystemFileDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowNodeFileDao;
import com.beeasy.common.entity.SystemFile;
import com.beeasy.common.entity.WorkflowNodeFile;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import com.beeasy.hzback.modules.system.service.WorkflowService;
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
    ISystemFileDao systemFileDao;
    @Autowired
    IMessageDao messageDao;
    @Autowired
    IWorkflowNodeFileDao nodeFileDao;
    @Autowired
    WorkflowService workflowService;

    public ResponseEntity<byte[]> output(SystemFile file) {
        if (null == file) {
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
        HttpHeaders headers = new HttpHeaders();
        if (file.getType().equals(SystemFile.Type.FACE)) {
            headers.setContentType(MediaType.IMAGE_JPEG);
        }
        return new ResponseEntity<byte[]>(file.getBytes(), headers, HttpStatus.OK);
    }


    @ApiOperation(value = "用户头像下载")
    @GetMapping("/download/face/{id}")
    public ResponseEntity<byte[]> getFace(@PathVariable String id) throws IOException {
        SystemFile file = systemFileDao.findFirstByIdAndType(Long.valueOf(id), SystemFile.Type.FACE).orElse(null);
        return output(file);
    }

    @ApiOperation(value = "聊天文件下载")
    @GetMapping("/download/message/{id}")
    public ResponseEntity<byte[]> getMessageFile(@PathVariable Long id) throws IOException {
        //检查这个文件是不是属于你
        Optional optional = messageDao.findContainsFileMessage(Utils.getCurrentUserId(), id);
        if (!optional.isPresent()) {
            return output(null);
        }
        SystemFile file = systemFileDao.findFirstByIdAndType(id, SystemFile.Type.MESSAGE).orElse(null);
        return output(file);
    }


    @ApiOperation(value = "工作流节点文件下载")
    @GetMapping("/download/workflow/node/file/{id}")
    public ResponseEntity<byte[]> getWorkflowNodeFile(@PathVariable Long id) {
        WorkflowNodeFile nodeFile = workflowService.findNodeFile(id).orElse(null);
        if (null == nodeFile) {
            return output(null);
        }
        if (null != nodeFile.getFileId() && nodeFile.getFileId() > 0) {
            SystemFile systemFile = systemFileDao.findById(nodeFile.getFileId()).orElse(null);
            return output(systemFile);
        }
        return output(null);
    }
}
