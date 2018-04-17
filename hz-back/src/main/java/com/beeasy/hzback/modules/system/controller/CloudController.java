package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.service.CloudDiskService;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "个人文件柜API")
@RestController
@RequestMapping("/api/clouddisk")
public class CloudController {


    @Autowired
    CloudDiskService cloudDiskService;


    @ApiOperation(value = "申请上传令牌", notes = "大文件采取切片上传, 在上传文件的时候, 只有附加了合法的令牌才可以上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dirId",value = "上传的文件夹ID"),
            @ApiImplicitParam(name = "fileName", value = "保存在文件柜中的文件名", required = true)
    })
    @PostMapping("/file/upload/apply")
    public Result apply(Long dirId, String fileName){
        //fileName 前面为路径, 最后为文件名

        User user = Utils.getCurrentUser();
        return Result.ok(cloudDiskService.createUploadSign(user.getId(), ICloudDiskService.DirType.USER,dirId,fileName));
    }


    @ApiOperation(value = "切片上传该文件", notes = "大文件必须采用切片上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "上传文件二进制数据流",required = true),
            @ApiImplicitParam(name = "uuid", value = "上传令牌", required = true)
    })
    @PostMapping("/file/upload/start")
    public Object upload(@RequestParam MultipartFile file, String uuid) throws IOException, CannotFindEntityException {
        //获取文件名
        return cloudDiskService.uploadFile(uuid,file);
    }

    @ApiOperation(value = "上传完毕")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid",value = "上传令牌", required = true )
    })
    @PostMapping("/file/upload/finish")
    public Result finish(String uuid) throws CannotFindEntityException, IOException {
        return Result.ok(cloudDiskService.finishUpload(uuid));
    }

    @ApiOperation(value = "文件列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dirId", value = "文件夹ID",required = true)
    })
    @GetMapping("/files")
    public Result listFolders(Long dirId){
        Map<String,List> files = new HashMap<>();
        files.put("dirs",cloudDiskService.getDirectories(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,dirId));
        files.put("files",cloudDiskService.getFiles(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,dirId));
        return Result.ok(files);
    }

    @ApiOperation(value = "创建文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dirId",value = "父文件夹ID",required = true),
            @ApiImplicitParam(name = "dirName",value = "文件夹名",required = true)
    })
    @PostMapping("/createDir")
    public Result createFolder(Long dirId, String dirName){
        Optional<CloudDirectoryIndex> optionalCloudDirectoryIndex = cloudDiskService.createDirectory(Utils.getCurrentUser().getId(), ICloudDiskService.DirType.USER,dirId,dirName);
        return optionalCloudDirectoryIndex.isPresent() ? Result.ok() : Result.error();
    }

    @ApiOperation(value = "删除文件夹", notes = "会同时删除该文件夹下所有的文件, 根文件夹不允许删除")
    @ApiImplicitParams({
           @ApiImplicitParam(name = "dirId", value = "文件夹ID",required = true)
    })
    @DeleteMapping("/deleteDir")
    public Result deleteFolder(Long dirId){
        boolean success = cloudDiskService.deleteDirectory(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,dirId);
        return success ? Result.ok() : Result.error();
    }

    @ApiOperation(value = "重命名文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dirId", value = "旧文件夹ID",required = true),
            @ApiImplicitParam(name = "newDirName",value = "改成的新文件夹名",required = true)
    })
    @PutMapping("/renameDir")
    public Object renameDir(Long dirId, String newDirName){
        return cloudDiskService.renameDirectory(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,dirId,newDirName);
    }


    @ApiOperation(value = "重命名文件")
    @ApiImplicitParams({
           @ApiImplicitParam(name = "oldFileId",value = "旧文件ID",required = true),
           @ApiImplicitParam(name = "newFileName",value = "新文件名",required = true)
    })
    @PutMapping("/renameFile")
    public Object renameFile(Long oldFileId, String newFileName){
        return cloudDiskService.renameFile(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,oldFileId,newFileName);
    }

    @ApiOperation(value = "移动文件")
    @PutMapping("/moveFiles")
    public Result<Set<Long>> moveFiles(Long targetDirId, Set<Long> fileIds){
        return Result.ok(cloudDiskService.moveFiles(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,fileIds,targetDirId));
    }

    @ApiOperation(value = "删除文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileIds",value = "需要删除的文件ID",allowMultiple = true, required = true)
    })
    @DeleteMapping("/deleteFiles")
    public Object deleteFile(Set<Long> fileIds){
        return cloudDiskService.deleteFile(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,fileIds);
    }

    @ApiOperation(value = "分享文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "toUid", value = "分享给用户的ID", required = true),
            @ApiImplicitParam(name = "fileId", value = "文件ID", required = true)
    })
    @PostMapping("/shareFile")
    public Result shareFile(Long toUid, Long fileId){
        return Result.ok(cloudDiskService.shareTo(Utils.getCurrentUserId(),toUid, ICloudDiskService.DirType.USER,fileId).orElse(null));
    }

    @ApiOperation(value = "收到的分享文件列表")
    @GetMapping("/shareFiles")
    public Result getShareFiles(){
        PageRequest pageable = new PageRequest(0,100);
        return Result.ok(cloudDiskService.getShareFiles(Utils.getCurrentUserId(),pageable));
    }

    @ApiOperation(value = "删除分享记录", notes = "只能删除我的分享里面的内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shareIds",allowMultiple = true,required = true)
    })
    @DeleteMapping("/deleteShareFiles")
    public Result deleteShareFile(
            Set<Long> shareIds
    ){
        cloudDiskService.deleteShareFiles(Utils.getCurrentUserId(),shareIds);
        return Result.ok();
    }

    @ApiOperation(value = "转存分享文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shareIds",value = "分享文件的ID数组", allowMultiple = true,required = true),
            @ApiImplicitParam(name = "targetDirId", value = "转存的目录ID, 需要确实存在该目录",required = true)
    })
    @PostMapping("/saveShareFiles")
    public Result saveShareFiles(
            Set<Long> shareIds,
            long targetDirId
    ){
        return Result.finish(
                cloudDiskService.saveShareFiles(Utils.getCurrentUserId(), ICloudDiskService.DirType.USER,targetDirId,shareIds)
        );
    }

    @ApiOperation(value = "转存文件柜中的文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetDirId", value = "转存目录",required = true),
            @ApiImplicitParam(name = "fileIds",value = "文件数组", required = true, allowMultiple = true)
    })
    @PostMapping("/saveCommonFiles")
    public Result saveCommonFiles(Long targetDirId, Set<Long> fileIds){
        return Result.ok(
                cloudDiskService.saveCommonFiles(Utils.getCurrentUserId(),targetDirId,fileIds)
                .stream()
                .map(item -> item.getId())
                .collect(Collectors.toSet())
        );
    }


}
