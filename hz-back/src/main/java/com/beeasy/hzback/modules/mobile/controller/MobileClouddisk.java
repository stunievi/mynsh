package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.service.CloudDiskService;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/mobile")
@RestController
public class MobileClouddisk {

    @Autowired
    CloudDiskService cloudDiskService;

    @GetMapping("/files")
    public Result getFiles(){
        return Result.ok(cloudDiskService.getDirectories(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER));
    }
}
