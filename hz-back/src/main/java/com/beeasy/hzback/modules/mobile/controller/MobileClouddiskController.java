package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.ICloudDirectoryIndexDao;
import com.beeasy.hzback.modules.system.dao.ICloudFileTagDao;
import com.beeasy.hzback.modules.system.dao.ICloudShareDao;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.service.CloudDiskService;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import com.beeasy.hzback.modules.system.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RequestMapping("/api/mobile/clouddisk")
@RestController
public class MobileClouddiskController {

    @Autowired
    ICloudFileTagDao tagDao;
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
    @GetMapping("/{type}/files/{pid}")
    public String allFiles(
            @PathVariable String type,
            @PathVariable Long pid
    ){
        if(type.equals("user")){
            return cloudDiskService.getFiles(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER, pid).toMobile();
        }
        else{
            return cloudDiskService.getFiles(0, ICloudDiskService.DirType.COMMON, pid).toMobile();
        }
    }

    @GetMapping("/{type}/createDir/{pid}/{dirName}")
    public String createDir(
            @PathVariable String type,
            @PathVariable Long pid,
            @PathVariable String dirName
    ){
        if(type.equals("user")){
            return cloudDiskService.createDirectory(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,pid,dirName).toMobile(new Result.DisallowEntry(CloudDirectoryIndex.class,"children","parent"));
        }
        else{
            if(!checkAuth(Utils.getCurrentUserId())){
                return Result.error().toJson();
            }
            return cloudDiskService.createDirectory(0, ICloudDiskService.DirType.COMMON,pid,dirName).toMobile(new Result.DisallowEntry(CloudDirectoryIndex.class,"children","parent"));
        }
    }

    @PostMapping("/{type}/removeDir")
    public String removeDir(
            @PathVariable String type,
            @RequestBody List<Long> rids
    ){
        if(type.equals("user")){
            return cloudDiskService.deleteDirectory(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,rids).toMobile();
        }
        else{
            if(!checkAuth(Utils.getCurrentUserId())){
                return Result.error("没有权限").toJson();
            }
            return cloudDiskService.deleteDirectory(0, ICloudDiskService.DirType.COMMON,rids).toMobile();
        }
    }

    @GetMapping("/type/renameDir/{id}/{newName}")
    public String renameDir(
            @PathVariable String type,
            @PathVariable Long id,
            @PathVariable String newName
    ){

        if(type.equals("user")) {
            return cloudDiskService.renameDirectory(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,id,newName).toMobile();
        }
        else {
            if(!checkAuth(Utils.getCurrentUserId())){
                return Result.error("没有权限").toMobile();
            }
            return cloudDiskService.renameDirectory(0, ICloudDiskService.DirType.COMMON,id,newName).toMobile();
        }
    }


    @ApiOperation(value = "上传文件")
    @PostMapping("/{type}/uploadFile/{pid}")
    public String uploadFile(
            @PathVariable String type,
            @RequestParam MultipartFile file,
            @PathVariable  Long pid
            ){
        if(type.equals("user")){
            return (cloudDiskService.uploadFile(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,pid,file)).toMobile();
        }
        else{
            if(!checkAuth(Utils.getCurrentUserId())){
                return Result.error("没有权限").toJson();
            }
            return (cloudDiskService.uploadFile(0, ICloudDiskService.DirType.COMMON,pid,file)).toMobile();
        }
    }


    @GetMapping("/{type}/shareFile/{id}/{toUid}")
    public String shareFile(
            @PathVariable String type,
            @PathVariable Long id,
            @PathVariable Long toUid
    ){
        if(type.equals("user")){
            return Result.ok(cloudDiskService.sendFileToUser(Utils.getCurrentUserId(),toUid, Utils.getCurrentUserId(), ICloudDiskService.DirType.USER, Collections.singletonList(id))).toMobile();
        }
        else{
            //分享共享文件柜不需要权限
            return Result.ok(cloudDiskService.sendFileToUser(Utils.getCurrentUserId(),toUid,0 , ICloudDiskService.DirType.COMMON, Collections.singletonList(id))).toMobile();
        }
//        return Result.finish(cloudDiskService.shareTo(Utils.getCurrentUserId(),toUid, ICloudDiskService.DirType.USER,id));
    }

//    @ApiOperation(value = "得到一个文件的标签")
//    @GetMapping("/user/tag/{fileId}")
//    public String getFileTags(@PathVariable Long fileId){
//        return Result.ok(tagDao.findAllByIndex_Id(fileId)).toMobile();
//    }
//
//    @Deprecated
//    @ApiOperation(value = "添加文件标签")
//    @PostMapping("/user/tag/add")
//    public String addFileTag(
//            @RequestParam Long fileId,
//            @RequestParam String tag
//    ){
//        return cloudDiskService.addFileTag(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,fileId,tag).toMobile();
//    }
//
//    @Deprecated
//    @ApiOperation(value = "删除文件标签")
//    @PostMapping("/user/tag/remove")
//    public String removeFileTag(
//            @RequestParam Long fileId,
//            @RequestParam String tag
//    ){
//        return Result.finish(cloudDiskService.removeFileTag(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,fileId,tag)).toMobile();
//    }

    @ApiOperation(value = "设置文件标签")
    @PostMapping("/{type}/tag/{id}")
    public String setTags(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestBody List<String> tags
    ){
        if(type.equals("user")){
            return (cloudDiskService.setFileTags(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER, id, tags)).toMobile();
        }
        else{
            if(!checkAuth(Utils.getCurrentUserId())){
                return Result.error().toMobile();
            }
            return (cloudDiskService.setFileTags(0, ICloudDiskService.DirType.COMMON, id , tags)).toMobile();
        }
    }

//    @ApiOperation(value = "设置一个文件的标签")
//    @PostMapping("/user/bytes/setTags")
//    public Result setFileTags(
//            @RequestParam Long fileId,
//            @RequestBody List<String> tags
//    ){
//        return Result.finish(cloudDiskService.setFileTags(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,fileId, tags));
//    }


    /** 共享文件柜 **/
    private boolean checkAuth(long uid){
        return false;
//        return userService.findUser(uid).filter(user -> {
//            return user.getExternalPermissions().stream().anyMatch(p -> p.getPermission().equals(UserExternalPermission.Permission.COMMON_CLOUD_DISK));
//        }).isPresent();
    }



//    @GetMapping("/common/all")
//    public String allCommonFiles(){
//        //无权限 所有人都可以看到
//        return Result.ok(cloudDirectoryIndexDao.findAllByTypeAndLinkIdAndParentOrderByDirDesc(ICloudDiskService.DirType.COMMON,0,null)).toMobile(
//                new Result.DisallowEntry(CloudDirectoryIndex.class,"parent")
//        );
//    }

//    @PostMapping("/common/createDir")
//    public String createCommonDir(
//            @RequestParam Long pid,
//            @RequestParam String dirName
//    ){
//        if(!checkAuth(Utils.getCurrentUserId())){
//            return Result.error("没有权限!").toJson();
//        }
//        if(null == pid){
//            pid = 0L;
//        }
//    }

//    @PostMapping("/common/removeDir")
//    public String removeCommonDir(
//            @RequestBody List<Long> rids
//    ){
//    }
//
//    @PostMapping("/common/renameDir")
//    public String renameCommonDir(
//            @RequestParam Long id,
//            @RequestParam String newName
//    ){
//
//    }


//    @PostMapping("/common/uploadFile")
//    public String uploadCommonFile(
//            @RequestParam MultipartFile file,
//            @RequestParam Long id
//    ){
//    }


//    @PostMapping("/common/shareFile")
//    public String shareCommonFile(
//            @RequestParam Long id,
//            @RequestParam Long toUid
//    ){
//        if(!checkAuth(Utils.getCurrentUserId())){
//            return Result.error("没有权限").toMobile();
//        }
//        return Result.ok(cloudDiskService.sendFileToUser(Utils.getCurrentUserId(),toUid,0 , ICloudDiskService.DirType.COMMON, Collections.singletonList(id))).toMobile();
//    }


//    @ApiOperation(value = "得到一个文件的标签")
//    @GetMapping("/common/tag/{fileId}")
//    public String getCommonFileTags(@PathVariable Long fileId){
//        return Result.ok(tagDao.findAllByIndex_Id(fileId)).toMobile();
//    }
//
//    @ApiOperation(value = "添加文件标签")
//    @PostMapping("/common/tag/add")
//    public String addCommonFileTag(
//            @RequestParam Long fileId,
//            @RequestParam String tag
//    ){
//        return cloudDiskService.addFileTag(0, ICloudDiskService.DirType.COMMON,fileId,tag).toMobile();
//    }
//
//    @ApiOperation(value = "删除文件标签")
//    @PostMapping("/common/tag/remove")
//    public String removeCommonFileTag(
//            @RequestParam Long fileId,
//            @RequestParam String tag
//    ){
//        return Result.finish(cloudDiskService.removeFileTag(0, ICloudDiskService.DirType.COMMON,fileId,tag)).toMobile();
//    }
}
