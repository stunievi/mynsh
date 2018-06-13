package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.ICloudDirectoryIndexDao;
import com.beeasy.hzback.modules.system.dao.ICloudShareDao;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.UserExternalPermission;
import com.beeasy.hzback.modules.system.service.CloudDiskService;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Api
@RequestMapping("/api/clouddisk")
@RestController
class ClouddiskController {

    @Autowired
    CloudDiskService cloudDiskService;
    @Autowired
    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
    @Autowired
    ICloudShareDao cloudShareDao;
    @Autowired
    UserService userService;

//    @GetMapping("/files")
//    public Result getFiles(){
//        return Result.ok(cloudDiskService.getDirectories(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER));
//    }
//
    /** 个人文件柜 **/
    @GetMapping("/user/all")
    public String allFiles(
        @RequestParam Long pid
    ){
        if(0 == pid){
            return Result.okJson(cloudDirectoryIndexDao.findAllByTypeAndLinkIdAndParent(ICloudDiskService.DirType.USER,Utils.getCurrentUserId(), null),
                    new Result.Entry(CloudDirectoryIndex.class,"parent", "children")
            );
        }else {
            return Result.okJson(cloudDirectoryIndexDao.findAllByTypeAndLinkIdAndParent_Id(ICloudDiskService.DirType.USER,Utils.getCurrentUserId(), pid),
                    new Result.Entry(CloudDirectoryIndex.class,"parent", "children")
            );
        }

    }

    @PostMapping("/user/createDir")
    public String createDir(
            @RequestParam Long pid,
            @RequestParam String dirName
    ){
        if(null == pid){
            pid = 0L;
        }
        return cloudDiskService.createDirectory(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,pid,dirName).toJson(new Result.Entry(CloudDirectoryIndex.class,"children","parent"));
    }

    @PostMapping("/user/removeDir")
    public Result removeDir(
            @RequestBody List<Long> rids
    ){
        cloudDiskService.deleteDirectory(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER, rids);
        return Result.ok();
    }

//    @PostMapping("/user/renameDir")
//    public Result renameDir(
//            @RequestParam Long id,
//            @RequestParam String newName
//    ){
//        String fileName = cloudDiskService.renameDirectory(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,id,newName);
//        if(fileName.isEmpty()){
//            return Result.error("修改失败");
//        }
//        else{
//            return Result.ok(fileName);
//        }
//    }


//    @PostMapping("/user/uploadFile")
//    public Result uploadFile(
//            @RequestParam MultipartFile file,
//            @RequestParam  Long dirId
//    ){
//        return Result.finish(cloudDiskService.uploadFile(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,dirId,file));
//    }


    @PostMapping("/user/shareFile")
    public Result shareFile(
            @RequestParam Long id,
            @RequestParam List<Long> toUids
    ){
        //自己还原
        return Result.error();
//        return Result.ok(cloudDiskService.sendFileToUsers(Utils.getCurrentUserId(),toUids, Utils.getCurrentUserId(), ICloudDiskService.DirType.USER, Collections.singletonList(id)));
//        return Result.finish(cloudDiskService.shareTo(Utils.getCurrentUserId(),toUid, ICloudDiskService.DirType.USER,id));
    }

//    @PostMapping("/user/putFileTags")
//    public Result putFileTags(
//            @RequestParam Long fileId,
//            @RequestParam List<String> tags
//    ){
//        return Result.finish(cloudDiskService.setFileTags(Utils.getCurrentUserId(),ICloudDiskService.DirType.USER,fileId,tags));
//    }


    /** 共享文件柜 **/
    private boolean checkAuth(long uid){
        return userService.findUser(uid).filter(user -> {
            return user.getExternalPermissions().stream().anyMatch(p -> p.getPermission().equals(UserExternalPermission.Permission.COMMON_CLOUD_DISK));
        }).isPresent();
    }

    @GetMapping("/common/all")
    public String allCommonFiles(
            @RequestParam Long pid
    ){
        if(0 == pid){
            return Result.okJson(cloudDirectoryIndexDao.findAllByTypeAndLinkIdAndParent(ICloudDiskService.DirType.COMMON, 0, null),
                    new Result.Entry(CloudDirectoryIndex.class,"parent")
            );
        }else {
            return Result.okJson(cloudDirectoryIndexDao.findAllByTypeAndLinkIdAndParent_Id(ICloudDiskService.DirType.COMMON, 0, pid),
                    new Result.Entry(CloudDirectoryIndex.class,"parent")
            );
        }
    }

    @PostMapping("/common/createDir")
    public String createCommonDir(
            @RequestParam Long pid,
            @RequestParam String dirName
    ){
        if(!checkAuth(Utils.getCurrentUserId())){
            return Result.error("没有权限!").toJson();
        }
        if(null == pid){
            pid = 0L;
        }
        return cloudDiskService.createDirectory(0, ICloudDiskService.DirType.COMMON,pid,dirName).toJson(new Result.Entry(CloudDirectoryIndex.class,"children","parent"));
    }

    @PostMapping("/common/removeDir")
    public Result removeCommonDir(
            @RequestBody List<Long> rids
    ){
        if(!checkAuth(Utils.getCurrentUserId())){
            return Result.error("没有权限");
        }
        cloudDiskService.deleteDirectory(0, ICloudDiskService.DirType.COMMON,rids);
        return Result.ok();
    }

//    @PostMapping("/common/renameDir")
//    public Result renameCommonDir(
//            @RequestParam Long id,
//            @RequestParam String newName
//    ){
//        if(!checkAuth(Utils.getCurrentUserId())){
//            return Result.error("没有权限");
//        }
//        String fileName = cloudDiskService.renameDirectory(0, ICloudDiskService.DirType.COMMON,id,newName);
//        if(fileName.isEmpty()){
//            return Result.error("修改失败");
//        }
//        else{
//            return Result.ok(fileName);
//        }
//    }


//    @PostMapping("/common/uploadFile")
//    public Result uploadCommonFile(
//            @RequestParam MultipartFile file,
//            @RequestParam Long dirId
//    ){
//        if(!checkAuth(Utils.getCurrentUserId())){
//            return Result.error("没有权限");
//        }
//        return Result.finish(cloudDiskService.uploadFile(0, ICloudDiskService.DirType.COMMON,dirId,file));
//    }


    @PostMapping("/common/shareFile")
    public Result shareCommonFile(
            @RequestParam Long id,
            @RequestParam Long toUid
    ){
        if(!checkAuth(Utils.getCurrentUserId())){
            return Result.error("没有权限");
        }
        return Result.ok(cloudDiskService.sendFileToUser(Utils.getCurrentUserId(),toUid,0 , ICloudDiskService.DirType.COMMON, Collections.singletonList(id)));
    }

//    @PostMapping("/common/putFileTags")
//    public Result putCommonFileTags(
//            @RequestParam Long fileId,
//            @RequestParam List<String> tags
//    ){
//        return Result.finish(cloudDiskService.setFileTags(0,ICloudDiskService.DirType.COMMON,fileId,tags));
//    }


}
