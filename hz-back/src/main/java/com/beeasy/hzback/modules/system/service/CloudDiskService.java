//package com.beeasy.hzback.modules.system.service;
//
//import com.beeasy.hzback.core.helper.Result;
//import com.beeasy.hzback.modules.cloud.CloudService;
//import com.beeasy.hzback.modules.cloud.response.CloudBaseResponse;
//import com.beeasy.hzback.modules.cloud.response.CreateDirResponse;
//import com.beeasy.hzback.modules.system.dao.*;
//import com.beeasy.common.entity.CloudDirectoryIndex;
//import com.beeasy.common.entity.FileCloudIndex;
//import com.beeasy.common.entity.Message;
//import com.beeasy.common.entity.SystemFile;
//import com.beeasy.hzback.modules.system.service.UserService;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class CloudDiskService implements ICloudDiskService {
//
//
////    @Value("${upload.path}")
////    private String AREA_DIR;//excel临时存储文件夹
//    @Value("${filecloud.enable}")
//    private boolean fileCloudEnabled;
//
//    @Value("${filecloud.username}")
//    private String cloudUserName;
//    @Value("${filecloud.password}")
//    private String cloudPassword;
//
//    @Autowired
//    CloudService cloudService;
//    @Autowired
//    IFileCloudIndexDao fileCloudIndexDao;
//
//    @Autowired
//    ICloudFileTagDao tagDao;
////    @Autowired
////    IUploadFileTempDao uploadFileTempDao;
//    @Autowired
//    UserService userService;
//    @Autowired
//    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
//    @Autowired
//    ICloudShareDao cloudShareDao;
//    @Autowired
//    ISystemFileDao systemFileDao;
//    @Autowired
//    MessageService messageService;
//    @Autowired
//    IMessageDao messageDao;
//
////    public String createUploadSign(long uid, DirType type, long dirId, String fileName) {
////        Optional<CloudDirectoryIndex> target = findDirectory(uid, type, dirId);
////        if (!target.isPresent()) {
////            return "";
////        }
////        CloudDirectoryIndex cloudDirectoryIndex = target.get();
////        Optional<UploadFileTemp> optionalUploadFileTemp = uploadFileTempDao.findFirstByDirectoryIndexAndFileName(cloudDirectoryIndex, fileName);
////        if(!optionalUploadFileTemp.isPresent()){
////            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
////            String today = sdf.format(new Date());
////            UploadFileTemp temp1 = new UploadFileTemp();
////            temp1.setDirectoryIndex(cloudDirectoryIndex);
////            temp1.setFileName(fileName);
////            String uuid = UUID.randomUUID().toString();
////            temp1.setUuid(uuid);
////            temp1.setFilePath(AREA_DIR + File.separator + today + File.separator + uuid);
////            return (uploadFileTempDao.save(temp1)).getUuid();
////        }
////        return optionalUploadFileTemp.get().getUuid();
////    }
//
//    /**
//     * 增加文件标签
//     * @param uid
//     * @param type
//     * @param fileId
//     * @param tag
//     * @return
//     */
////    public Result addFileTag(long uid, DirType type, long fileId, String tag){
////        CloudDirectoryIndex file = findFile(uid,type,fileId).orElse(null);
////        if(null == file){
////            return Result.error();
////        }
////        //查找是否有同名tag
////        CloudFileTag same = tagDao.findFirstByIndexAndTag(file,tag).orElse(null);
////        if(null != same){
////            return Result.error("已经有同名标签");
////        }
////        CloudFileTag cloudFileTag = new CloudFileTag();
////        cloudFileTag.setTag(tag);
////        cloudFileTag.setIndex(file);
////        return Result.ok(tagDao.save(cloudFileTag));
////    }
////
////    /**
////     * 删除文件标签
////     * @param uid
////     * @param type
////     * @param fileId
////     * @param tag
////     * @return
////     */
////    public boolean removeFileTag(long uid, DirType type, long fileId, String tag){
////        CloudDirectoryIndex file = findFile(uid,type,fileId).orElse(null);
////        if(null == file){
////            return false;
////        }
////        tagDao.deleteByIndexAndTag(file,tag);
////        return true;
////    }
//
//    /**
//     * 设置文件标签
//     * @param uid
//     * @param type
//     * @param fileId
//     * @param tags
//     * @return
//     */
//    public Result setFileTags(long uid, DirType type, long fileId, List<String> tags){
//        if(isFileCloudEnabled()){
//            long workId = prepareFileCloud(type, uid);
//            if(checkFile(type,uid,workId,fileId)){
//                return cloudService.editTags(fileId, tags);
//            }
//            return Result.error();
//        }
//        else{
//            //清空tag
//            CloudDirectoryIndex file = findDirectory(uid,type,fileId).orElse(null);
//            if(null == file || file.isDir()){
//                return Result.error();
//            }
//            file.setTags(tags);
//            cloudDirectoryIndexDao.save(file);
//            return Result.ok();
//        }
//    }
//
//    /**
//     * 上传文件
//     * @param uid
//     * @param type
//     * @param pid
//     * @param file
//     * @return
//     */
//    public Result uploadFile(long uid, DirType type, long pid, MultipartFile file){
//        if(isFileCloudEnabled()){
//            long wid = prepareFileCloud(type,uid);
//            //根目录
//            if(0 == pid){
//                pid = wid;
//            }
//            if((wid == pid && wid > 0) || checkFile(type,uid,wid,pid)){
//                Result<CloudBaseResponse> result = cloudService.uploadFiles(pid,file);
////                if(result.isSuccess()){
////                    FileCloudIndex index = new FileCloudIndex();
////                    index.setDir(false);
////                    index.setFileId(result.getData().getId());
////                    index.setLinkId(uid);
////                    index.setType(type);
////                    fileCloudIndexDao.save(index);
////                }
//                return result;
//            }
//            return Result.error();
//        }
//        else{
//            CloudDirectoryIndex parent = null;
//            if(pid != 0){
//                parent = cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndId(type,uid,pid).orElse(null);
//            }
//            SystemFile systemFile = new SystemFile();
//            systemFile.setFileName(file.getOriginalFilename());
//            systemFile.setType(SystemFile.Type.CLOUDDISK);
//            try {
//                systemFile.setBytes(file.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            systemFile = systemFileDao.save(systemFile);
//
//            CloudDirectoryIndex cloudDirectoryIndex = new CloudDirectoryIndex();
//            cloudDirectoryIndex.setParentId(null == parent ? null : parent.getId());
//            cloudDirectoryIndex.setFileId(systemFile.getId());
//            cloudDirectoryIndex.setDir(false);
//            cloudDirectoryIndex.setType(type);
//            cloudDirectoryIndex.setLinkId(uid);
//            cloudDirectoryIndex.setDirName(file.getOriginalFilename());
//            return Result.finish(Optional.ofNullable(cloudDirectoryIndexDao.save(cloudDirectoryIndex)));
//        }
//    }
//
////    public Result fileSearch(String keyword){
////
////    }
//
////    public Optional<CloudFileIndex> uploadFile(long uid, DirType type, long dirId, MultipartFile bytes){
////        return findDirectory(uid,type,dirId)
////            .map(directoryIndex -> {
////                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
////                String today = sdf.format(new Date());
////
////                Path path = Paths.get(AREA_DIR + File.separator + today);
////                if (Files.notExists(path)) {
////                    try {
////                        Files.createDirectory(path);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                        return null;
////                    }
////                }
////
////                RandomAccessFile raFile = null;
////                BufferedInputStream inputStream = null;
////                CloudFileIndex fileIndex = new CloudFileIndex();
////                try {
////                    String uuid = UUID.randomUUID().toString();
////                    String filePath = AREA_DIR + File.separator + today + File.separator + uuid;
////                    File dirFile = new File(filePath);
////                    //以读写的方式打开目标文件
////                    raFile = new RandomAccessFile(dirFile, "rw");
////                    raFile.seek(raFile.length());
////                    inputStream = new BufferedInputStream(bytes.getInputStream());
////                    byte[] buf = new byte[1024];
////                    int length = 0;
////                    while ((length = inputStream.read(buf)) != -1) {
////                        raFile.write(buf, 0, length);
////                    }
////
////                    fileIndex.setDirectoryIndex(directoryIndex);
////                    fileIndex.setFilePath(filePath);
////                    fileIndex.setFileName(bytes.getName());
////
////                } catch (Exception e) {
////                    e.printStackTrace();
////                } finally {
////                    try {
////                        if (inputStream != null) {
////                            inputStream.close();
////                        }
////                        if (raFile != null) {
////                            raFile.close();
////                        }
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                }
////
////                if(null != fileIndex.getFilePath()){
////                    return cloudFileIndexDao.save(fileIndex);
////                }
////
////                return null;
////            });
////    }
//
////    public boolean uploadFile(String sign, MultipartFile bytes) {
////        return uploadFileTempDao.findFirstByUuid(sign).map(uploadFileTemp -> {
////            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
////            String today = sdf.format(new Date());
////
////            Path path = Paths.get(AREA_DIR + File.separator + today);
////            if (Files.notExists(path)) {
////                try {
////                    Files.createDirectory(path);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                    return false;
////                }
////            }
////
////            //合并文件
////            RandomAccessFile raFile = null;
////            BufferedInputStream inputStream = null;
////            try {
////                File dirFile = new File(uploadFileTemp.getFilePath());
////                //以读写的方式打开目标文件
////                raFile = new RandomAccessFile(dirFile, "rw");
////                raFile.seek(raFile.length());
////                inputStream = new BufferedInputStream(bytes.getInputStream());
////                byte[] buf = new byte[1024];
////                int length = 0;
////                while ((length = inputStream.read(buf)) != -1) {
////                    raFile.write(buf, 0, length);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            } finally {
////                try {
////                    if (inputStream != null) {
////                        inputStream.close();
////                    }
////                    if (raFile != null) {
////                        raFile.close();
////                    }
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
////            return true;
////
////        }).orElse(false);
////    }
//
////    public String finishUpload(String uuid) {
////        return uploadFileTempDao.findFirstByUuid(uuid)
////                .map(uploadFileTemp -> {
////                    //检查文件是否存在
////                    CloudFileIndex index = cloudFileIndexDao
////                            .findFirstByDirectoryIndexAndFileName(uploadFileTemp.getDirectoryIndex(), uploadFileTemp.getFileName())
////                            .orElse(new CloudFileIndex());
////                    index.setDirectoryIndex(uploadFileTemp.getDirectoryIndex());
////                    index.setFileName(uploadFileTemp.getFileName());
////                    index.setFilePath(uploadFileTemp.getFilePath());
////                    cloudFileIndexDao.save(index);
////                    uploadFileTempDao.delete(uploadFileTemp);
////                    return index.getFileName();
////                }).orElse("");
////    }
//
//    public List<CloudDirectoryIndex> getDirectories(long uid, DirType type, long dirId) {
//        return findDirectory(uid, type, dirId)
//                .map(directoryIndex -> cloudDirectoryIndexDao.findAllByParent(directoryIndex))
//                .orElse(new ArrayList<CloudDirectoryIndex>());
//    }
//
//    public List<CloudDirectoryIndex> getDirectories(long uid, DirType type){
//        return cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndParent(type,uid,null)
//                .map(directoryIndex -> getDirectories(uid,type,directoryIndex.getId()))
//                .orElse(new ArrayList<>());
//    }
//
////    public List<CloudFileIndex> getFiles(long uid, DirType type, long dirId) {
////        return findDirectory(uid, type, dirId)
////                .map(directoryIndex -> cloudFileIndexDao.findAllByDirectoryIndex(directoryIndex))
////                .orElse(new ArrayList<>());
////    }
//
////    public List<CloudFileIndex>
//
//
//    public Result getFiles(long uid, DirType type, long pid){
//        if(isFileCloudEnabled()){
//            long workId = prepareFileCloud(type,uid);
//            if(0 == pid && workId > 0){
//                pid = workId;
//                return cloudService.getFiles(pid);
//            }
//            return Result.error();
//        }
//        else{
//            return Result.error();
//        }
//    }
//
//    /**
//     * 创建文件夹
//     * @param uid
//     * @param type
//     * @param dirId
//     * @param dirName
//     * @return
//     */
//    public Result createDirectory(long uid, DirType type, long dirId, String dirName) {
//        CloudDirectoryIndex parent = null;
//        if(isFileCloudEnabled()){
//            long workIdrId = prepareFileCloud(type,uid);
//            if(dirId == 0){
//                dirId = workIdrId;
//            }
//            if((dirId == workIdrId && workIdrId > 0) || checkFile(type,uid,workIdrId,dirId)){
//                Result<CreateDirResponse> result = cloudService.createUserDir(dirId, dirName);
//                if(result.isSuccess()){
//                    FileCloudIndex index = new FileCloudIndex();
//                    index.setType(type);
//                    index.setLinkId(uid);
//                    index.setFileId(result.getData().getId());
//                    index.setDir(true);
//                    fileCloudIndexDao.save(index);
//                }
//                return result;
//            }
//            return Result.error();
//        }
//        else{
//            if(StringUtils.isEmpty(dirName)){
//                return Result.error("文件夹名不能为空");
//            }
//            //禁止创建同名
//            if(dirId > 0){
//                Optional<CloudDirectoryIndex> optional = findDirectory(uid, type, dirId);
//                if (!optional.isPresent()) {
//                    return Result.error("找不到父文件夹");
//                }
//                parent = optional.get();
//            }
//            if(cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndParentAndDirName(DirType.USER,uid,parent,dirName).isPresent()){
//                return Result.error("无法创建同名文件夹");
//            }
//            String finalFolderName1 = dirName;
//            CloudDirectoryIndex finalParent = parent;
//            CloudDirectoryIndex cloudDirectoryIndex = new CloudDirectoryIndex();
//            cloudDirectoryIndex.setDirName(finalFolderName1);
//            cloudDirectoryIndex.setType(type);
//            cloudDirectoryIndex.setLinkId(uid);
//            cloudDirectoryIndex.setDir(true);
//            cloudDirectoryIndex.setParentId(null == finalParent ? null : finalParent.getId());
//            return Result.ok(cloudDirectoryIndexDao.save(cloudDirectoryIndex));
//        }
//    }
//
//    /**
//     * 重命名文件
//     * @param uid
//     * @param type
//     * @param dirId
//     * @param newName
//     * @return
//     */
//    public Result renameDirectory(long uid, DirType type, long dirId, String newName) {
//        if(isFileCloudEnabled()){
//            long workId = prepareFileCloud(type, uid);
//            //禁止重命名主目录
//            if(checkFile(type,uid,workId,dirId) && workId != dirId){
//                return cloudService.renameFile(dirId,newName);
//            }
//            return Result.error();
//        }
//        else{
//            Optional<CloudDirectoryIndex> optional = findDirectory(uid, type, dirId)
//                    .map(directoryIndex -> {
//                        //如果是文件,不改文件拓展名
//                        String finalNewName;
//                        if(directoryIndex.isDir()){
//                            finalNewName = newName;
//                        }
//                        else{
//                            int pointIndex = directoryIndex.getDirName().lastIndexOf(".");
//                            if(pointIndex > -1){
//                                String ext = directoryIndex.getDirName().substring(pointIndex,directoryIndex.getDirName().length() - 1);
//                                finalNewName = newName + "." + ext;
//                            }
//                            else{
//                                finalNewName = newName;
//                            }
//                        }
//                        Optional<CloudDirectoryIndex> optionalCloudDirectoryIndex = cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndParentAndDirName(DirType.USER, uid,directoryIndex.getParent(), finalNewName);
//                        if (optionalCloudDirectoryIndex.isPresent()) {
//                            return null;
//                        }
//                        directoryIndex.setDirName(finalNewName);
//                        return cloudDirectoryIndexDao.save(directoryIndex);
//                    });
//            return Result.finish(optional);
//        }
//    }
//
////    public boolean renameFile(long uid, DirType type, long oldId, String newName) {
////        return findFile(uid, type, oldId)
////                .filter(cloudFileIndex -> {
////                    //检查是否有同名文件
////                    if (cloudFileIndexDao.findFirstByDirectoryIndexAndFileName(cloudFileIndex.getDirectoryIndex(), newName).isPresent()) {
////                        return false;
////                    }
////                    cloudFileIndex.setFileName(newName);
////                    cloudFileIndexDao.save(cloudFileIndex);
////                    return true;
////                })
////                .isPresent();
////    }
//
////    public Set<Long> moveFiles(long uid, DirType dirType, Set<Long> fileIds, long targetDirId) {
////        CloudDirectoryIndex directoryIndex = findDirectory(uid, dirType, targetDirId).orElse(null);
////        Set<Long> set = new HashSet<>();
////        if (null == directoryIndex) return set;
////        fileIds.forEach(fileId -> {
////            findFile(uid, dirType, fileId)
////                    .ifPresent(fileIndex -> {
////                        CloudFileIndex ret = cloudFileIndexDao.findFirstByDirectoryIndexAndFileName(directoryIndex, fileIndex.getFileName()).orElseGet(() -> {
////                            CloudFileIndex fileIndex1 = new CloudFileIndex();
////                            fileIndex1.setFileName(fileIndex.getFileName());
////                            fileIndex1.setFilePath(fileIndex.getFilePath());
////                            return fileIndex1;
////                        });
////                        ret.setDirectoryIndex(directoryIndex);
////                        cloudFileIndexDao.save(ret);
////                        set.add(ret.getId());
////                    });
////        });
////        return set;
////    }
//
//    public boolean moveDirectory(long uid, String dirName, String newPath) {
//        return false;
//    }
//
////    public Set<Long> deleteFile(long uid, DirType type, Set<Long> fileIds) {
////        Set<Long> ret = new HashSet<>();
////        fileIds.forEach(id -> {
////            findFile(uid, type, id).ifPresent(cloudFileIndex -> {
////                cloudFileIndexDao.delete(cloudFileIndex);
////                ret.add(id);
////            });
////        });
////        return ret;
////    }
//
//    /**
//     * 删除文件
//     * @param uid
//     * @param type
//     * @param dirIds
//     * @return
//     */
//    public Result deleteDirectory(long uid, DirType type, List<Long> dirIds) {
//        if(isFileCloudEnabled()){
//            long workId = prepareFileCloud(type,uid);
//            dirIds = dirIds.stream().filter(id -> checkFile(type,uid,workId,id)).collect(Collectors.toList());
//            if(dirIds.size() > 0){
//                Result result = cloudService.deleteFiles(dirIds);
//                if(result.isSuccess()){
//                    fileCloudIndexDao.deleteByTypeAndLinkIdAndFileIdIn(type,uid, dirIds);
//                }
//                return result;
//            }
//            return Result.error();
//        }
//        else{
//            for (Long dirId : dirIds) {
//                cloudDirectoryIndexDao.deleteByTypeAndLinkIdAndId(type,uid,dirId);
//            }
//            return Result.ok();
//        }
//    }
//
//    /**
//     * 分享, 直接将文件作为消息转存给用户
//     * @param fromUid
//     * @param toUid
//     * @param type
//     * @param fileIds 需要发送的文件ID列表
//     */
//    public List<Long> sendFileToUser(long fromUid, long toUid, long linkId, DirType type, List<Long> fileIds){
//        List<Long> result = new ArrayList<>();
//        fileIds.stream().forEach(fileId -> {
//            CloudDirectoryIndex cloudDirectoryIndex = findDirectory(linkId, type, fileId).orElse(null);
//            if (null == cloudDirectoryIndex) {
//                return;
//            }
//            //文件云
//            if(isFileCloudEnabled()){
//
//            }
//            else{
//                SystemFile systemFile = systemFileDao.findById(cloudDirectoryIndex.getFileId()).orElse(null);
//                if (null == systemFile) {
//                    return;
//                }
//                messageService.sendMessage(fromUid, toUid, "")
//                        .ifPresent(message -> {
//                            message.setType(Message.Type.FILE);
//                            message.setContent(systemFile.getFileName());
//                            message.setLinkId(systemFile.getId());
//                            messageDao.save(message);
//                            result.add(fileId);
//                        });
//            }
//        });
//        return result;
//    }
//
//
//    public ResponseEntity<byte[]> downloadFile(long uid, DirType type, Long linkId, Long id){
//        //检查这个文件是不是属于你
//        CloudDirectoryIndex cloudDirectoryIndex = cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndId(type,linkId, id).orElse(null);
//        if(null == cloudDirectoryIndex){
//            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        if(isFileCloudEnabled()){
//
//        }
//        else{
//            SystemFile systemFile = systemFileDao.findById(id).orElse(null);
//            if(null != systemFile){
//                return new ResponseEntity<byte[]>(systemFile.getBytes(), headers, HttpStatus.OK);
//            }
//        }
//        return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
//
//    }
////    public Optional<CloudShare> shareTo(long fromUid, long toUid, DirType type, long fileId) {
////        if (fromUid == toUid) {
////            return Optional.empty();
////        }
////        return findDirectory(fromUid, type, fileId)
////                .map(cloudFileIndex -> {
////                    Optional<User> toUser = userService.findUser(toUid);
////                    CloudShare cloudShare = cloudShareDao.findFirstByToUserAndFileIndex(toUser.get(), cloudFileIndex).orElseGet(() -> {
////                        CloudShare cloudShare1 = new CloudShare();
////                        cloudShare1.setFileIndex(cloudFileIndex);
////                        cloudShare1.setToUser(toUser.get());
////                        return cloudShareDao.save(cloudShare1);
////                    });
////                    return cloudShare;
////                });
////    }
////
////
////
////    public Page<CloudShare> getShareFiles(long uid, Pageable pageable) {
////        return userService.findUser(uid)
////                .map(user -> {
////                    return cloudShareDao.findAllByToUser(user, pageable);
////                }).orElse(new PageImpl<CloudShare>(new ArrayList<>(), pageable, 0));
////    }
////
////    public boolean deleteShareFiles(long uid, Set<Long> shareIds) {
////        return userService.findUser(uid)
////                .filter(user -> {
////                    cloudShareDao.deleteAllByToUserAndIdIn(user, shareIds);
////                    return true;
////                }).isPresent();
////    }
//
////    public boolean saveShareFiles(long uid, DirType type, long targetDirId, Set<Long> shareIds) {
////        return findDirectory(uid, type, targetDirId)
////                .filter(directoryIndex -> {
////                    cloudShareDao.findAllByToUserIdAndIdIn(uid, shareIds).forEach(cloudShare -> {
////                        CloudFileIndex fileIndex = cloudShare.getFileIndex();
////                        fileIndex.setId(null);
////                        fileIndex.setDirectoryIndex(directoryIndex);
////                        cloudFileIndexDao.save(fileIndex);
////                    });
////                    return true;
////                }).isPresent();
////
////    }
//
////    public Set<CloudFileIndex> saveCommonFiles(long uid, long targetDirId, Set<Long> fileIds) {
////        Set<CloudFileIndex> set = new HashSet<>();
////        findDirectory(uid, DirType.USER, targetDirId)
////                .ifPresent(directoryIndex -> {
////                    fileIds.forEach(id -> {
////                        findFile(0, DirType.COMMON, id)
////                                .ifPresent(fileIndex -> {
////                                    //检查是否有同名文件
////                                    Optional<CloudFileIndex> optional = cloudFileIndexDao.findFirstByDirectoryIndexAndFileName(directoryIndex, fileIndex.getFileName());
////                                    if (optional.isPresent()) {
////                                        return;
////                                    }
////                                    fileIndex.setDirectoryIndex(directoryIndex);
////                                    fileIndex.setId(null);
////                                    set.add(cloudFileIndexDao.save(fileIndex));
////                                });
////                    });
////                });
////        return set;
////    }
//
////    public void downloadFile(long uid, DirType type, long fileId) {
////        findFile(uid, type, fileId)
////                .ifPresent(cloudFileIndex -> {
////                    HttpServletResponse res = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
////                    res.setHeader("content-type", "application/octet-stream");
////                    res.setContentType("application/octet-stream");
////                    res.setHeader("Content-Disposition", "attachment;filename=" + cloudFileIndex.getFileName());
////                    byte[] buff = new byte[1024];
////                    BufferedInputStream bis = null;
////                    OutputStream os = null;
////                    try {
////                        os = res.getOutputStream();
////                        bis = new BufferedInputStream(new FileInputStream(new File(cloudFileIndex.getFilePath())));
////                        int i = bis.read(buff);
////                        while (i != -1) {
////                            os.write(buff, 0, buff.length);
////                            os.flush();
////                            i = bis.read(buff);
////                        }
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    } finally {
////                        if (bis != null) {
////                            try {
////                                bis.close();
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    }
////                });
////
////    }
//
////    public Optional<CloudFileIndex> findFile(long uid, DirType type, long fileId) {
////        CloudFileIndex fileIndex = cloudFileIndexDao.findOne(fileId);
////        if (null == fileIndex) {
////            return Optional.empty();
////        }
////        if (!fileIndex.getDirectoryIndex().getType().equals(type) || !fileIndex.getDirectoryIndex().getLinkId().equals(uid)) {
////            return Optional.empty();
////        }
////        return Optional.of(fileIndex);
////    }
//
//    public Optional<CloudDirectoryIndex> findDirectory(long uid, DirType type, long dirId) {
//        return cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndId(type, uid, dirId);
//    }
//
//    public Optional<CloudDirectoryIndex> findFile(long uid, DirType type, long dirId){
//        return findDirectory(uid,type,dirId).filter(file -> !file.isDir());
//    }
//
//    private boolean isFileCloudEnabled(){
//        return fileCloudEnabled;
//    }
//
//
//    public long prepareFileCloud(DirType type, long uid){
//        //检查是否登录
//        boolean flag = cloudService.checkOnline();
//        if(!flag){
//            flag = cloudService.login(cloudUserName,cloudPassword);
//        }
//        if(!flag){
//            return 0;
//        }
//        Result<CreateDirResponse> result = cloudService.createUserDir(0,type.toString() + "_" +  uid + "");
//        return result.isSuccess() ? result.getData().getId() : 0;
//    }
//
//    public boolean checkFile(DirType type, long uid, long workId, long fileId){
//        //目录存在
//        return workId > 0 &&
//                fileId > 0;
//                //属于我
////                fileCloudIndexDao.countByTypeAndLinkIdAndFileId(type,uid,fileId) > 0;
//    }
//}
//
