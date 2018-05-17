//package com.beeasy.hzback.modules.system.controller;
//
//import com.beeasy.hzback.core.helper.Result;
//import com.beeasy.hzback.modules.system.service.CloudDiskService;
//import com.beeasy.hzback.modules.system.service.ICloudDiskService;
//import io.swagger.annotations.Api;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//@Api(tags = "共享文件柜API", description = "所有API参数同个人文件柜相同,不再赘述")
//@RestController
//@RequestMapping("/api/clouddisk/common")
//public class CommonCloudController {
//    @Autowired
//    CloudDiskService cloudDiskService;
//
//
//    @PostMapping("/createDir")
//    public Result createDir(Long dirId, String dirName){
//        return Result.finish(
//               cloudDiskService.createDirectory(0, ICloudDiskService.DirType.COMMON, dirId,dirName).isPresent()
//        );
//    }
//
//    @DeleteMapping("/deleteDir")
//    public Result deleteDir(Long dirId){
//        return Result.finish(
//            cloudDiskService.deleteDirectory(0, ICloudDiskService.DirType.COMMON,dirId)
//        );
//    }
//
//    @PutMapping("/renameFile")
//    public Object renameFile(Long oldFileId, String newFileName){
//        return cloudDiskService.renameFile(0, ICloudDiskService.DirType.COMMON,oldFileId,newFileName);
//    }
//
//    @DeleteMapping("/deleteFiles")
//    public Result deleteFile(Set<Long> fileIds){
//        return Result.ok(cloudDiskService.deleteFile(0, ICloudDiskService.DirType.COMMON,fileIds));
//    }
//
//    @PutMapping("/renameDir")
//    public Object renameDir(Long dirId, String newDirName) {
//        return cloudDiskService.renameDirectory(0, ICloudDiskService.DirType.COMMON, dirId, newDirName);
//    }
//
//
//    @GetMapping("/files")
//    public Result listFolders(Long dirId){
//        Map<String,List> files = new HashMap<>();
//        files.put("dirs",cloudDiskService.getDirectories(0, ICloudDiskService.DirType.COMMON,dirId));
//        files.put("files",cloudDiskService.getFiles(0, ICloudDiskService.DirType.COMMON,dirId));
//        return Result.ok(files);
//
//    }
//
//    @PostMapping("/file/upload/apply")
//    public Result apply(Long dirId, String fileName){
//        //fileName 前面为路径, 最后为文件名
//        return Result.ok(cloudDiskService.createUploadSign(0, ICloudDiskService.DirType.COMMON,dirId,fileName));
//    }
//    @PostMapping("/file/upload/start")
//    public Object upload(@RequestParam MultipartFile file, String uuid) {
//        //获取文件名
//        return cloudDiskService.uploadFile(uuid,file);
//    }
//
//    @PostMapping("/file/upload/finish")
//    public Result finish(String uuid) {
//        return Result.ok(cloudDiskService.finishUpload(uuid));
//    }
//
//
//    @PutMapping("/moveFiles")
//    public Result<Set<Long>> moveFiles(Long targetDirId, Set<Long> fileIds){
//        return Result.ok(cloudDiskService.moveFiles(0, ICloudDiskService.DirType.COMMON,fileIds,targetDirId));
//    }
//
////    @PostMapping("/saveShareFiles")
////    public Result saveShareFiles(
////            Set<Long> shareIds,
////            long targetDirId
////    ){
////        return Result.finish(
////                cloudDiskService.saveShareFiles(0, ICloudDiskService.DirType.COMMON,targetDirId,shareIds)
////        );
////    }
//}
