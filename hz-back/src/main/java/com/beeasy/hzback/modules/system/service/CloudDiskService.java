package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.Message;
import com.beeasy.hzback.modules.system.entity.SystemFile;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CloudDiskService implements ICloudDiskService {


    @Value("${upload.path}")
    private String AREA_DIR;//excel临时存储文件夹


    @Autowired
    IUploadFileTempDao uploadFileTempDao;
    @Autowired
    UserService userService;
    @Autowired
    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
    @Autowired
    ICloudShareDao cloudShareDao;
    @Autowired
    ISystemFileDao systemFileDao;
    @Autowired
    MessageService messageService;
    @Autowired
    IMessageDao messageDao;

//    public String createUploadSign(long uid, DirType type, long dirId, String fileName) {
//        Optional<CloudDirectoryIndex> target = findDirectory(uid, type, dirId);
//        if (!target.isPresent()) {
//            return "";
//        }
//        CloudDirectoryIndex cloudDirectoryIndex = target.get();
//        Optional<UploadFileTemp> optionalUploadFileTemp = uploadFileTempDao.findFirstByDirectoryIndexAndFileName(cloudDirectoryIndex, fileName);
//        if(!optionalUploadFileTemp.isPresent()){
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
//            String today = sdf.format(new Date());
//            UploadFileTemp temp1 = new UploadFileTemp();
//            temp1.setDirectoryIndex(cloudDirectoryIndex);
//            temp1.setFileName(fileName);
//            String uuid = UUID.randomUUID().toString();
//            temp1.setUuid(uuid);
//            temp1.setFilePath(AREA_DIR + File.separator + today + File.separator + uuid);
//            return (uploadFileTempDao.save(temp1)).getUuid();
//        }
//        return optionalUploadFileTemp.get().getUuid();
//    }


    /**
     * 上传文件
     * @param uid
     * @param type
     * @param pid
     * @param file
     * @return
     */
    public Optional<CloudDirectoryIndex> uploadFile(long uid, DirType type, long pid, MultipartFile file){
        CloudDirectoryIndex parent = null;
       if(pid != 0){
           parent = cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndId(type,uid,pid).orElse(null);
       }
       SystemFile systemFile = new SystemFile();
       systemFile.setFileName(file.getOriginalFilename());
       systemFile.setType(SystemFile.Type.CLOUDDISK);
        try {
            systemFile.setFile(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        systemFile = systemFileDao.save(systemFile);

        CloudDirectoryIndex cloudDirectoryIndex = new CloudDirectoryIndex();
        cloudDirectoryIndex.setParent(parent);
        cloudDirectoryIndex.setFile(systemFile);
        cloudDirectoryIndex.setDir(false);
        cloudDirectoryIndex.setType(type);
        cloudDirectoryIndex.setLinkId(uid);
        cloudDirectoryIndex.setDirName(file.getOriginalFilename());
        return Optional.ofNullable(cloudDirectoryIndexDao.save(cloudDirectoryIndex));
    }

//    public Optional<CloudFileIndex> uploadFile(long uid, DirType type, long dirId, MultipartFile file){
//        return findDirectory(uid,type,dirId)
//            .map(directoryIndex -> {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
//                String today = sdf.format(new Date());
//
//                Path path = Paths.get(AREA_DIR + File.separator + today);
//                if (Files.notExists(path)) {
//                    try {
//                        Files.createDirectory(path);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                }
//
//                RandomAccessFile raFile = null;
//                BufferedInputStream inputStream = null;
//                CloudFileIndex fileIndex = new CloudFileIndex();
//                try {
//                    String uuid = UUID.randomUUID().toString();
//                    String filePath = AREA_DIR + File.separator + today + File.separator + uuid;
//                    File dirFile = new File(filePath);
//                    //以读写的方式打开目标文件
//                    raFile = new RandomAccessFile(dirFile, "rw");
//                    raFile.seek(raFile.length());
//                    inputStream = new BufferedInputStream(file.getInputStream());
//                    byte[] buf = new byte[1024];
//                    int length = 0;
//                    while ((length = inputStream.read(buf)) != -1) {
//                        raFile.write(buf, 0, length);
//                    }
//
//                    fileIndex.setDirectoryIndex(directoryIndex);
//                    fileIndex.setFilePath(filePath);
//                    fileIndex.setFileName(file.getName());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (inputStream != null) {
//                            inputStream.close();
//                        }
//                        if (raFile != null) {
//                            raFile.close();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if(null != fileIndex.getFilePath()){
//                    return cloudFileIndexDao.save(fileIndex);
//                }
//
//                return null;
//            });
//    }

//    public boolean uploadFile(String sign, MultipartFile file) {
//        return uploadFileTempDao.findFirstByUuid(sign).map(uploadFileTemp -> {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
//            String today = sdf.format(new Date());
//
//            Path path = Paths.get(AREA_DIR + File.separator + today);
//            if (Files.notExists(path)) {
//                try {
//                    Files.createDirectory(path);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return false;
//                }
//            }
//
//            //合并文件
//            RandomAccessFile raFile = null;
//            BufferedInputStream inputStream = null;
//            try {
//                File dirFile = new File(uploadFileTemp.getFilePath());
//                //以读写的方式打开目标文件
//                raFile = new RandomAccessFile(dirFile, "rw");
//                raFile.seek(raFile.length());
//                inputStream = new BufferedInputStream(file.getInputStream());
//                byte[] buf = new byte[1024];
//                int length = 0;
//                while ((length = inputStream.read(buf)) != -1) {
//                    raFile.write(buf, 0, length);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                    if (raFile != null) {
//                        raFile.close();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            return true;
//
//        }).orElse(false);
//    }

//    public String finishUpload(String uuid) {
//        return uploadFileTempDao.findFirstByUuid(uuid)
//                .map(uploadFileTemp -> {
//                    //检查文件是否存在
//                    CloudFileIndex index = cloudFileIndexDao
//                            .findFirstByDirectoryIndexAndFileName(uploadFileTemp.getDirectoryIndex(), uploadFileTemp.getFileName())
//                            .orElse(new CloudFileIndex());
//                    index.setDirectoryIndex(uploadFileTemp.getDirectoryIndex());
//                    index.setFileName(uploadFileTemp.getFileName());
//                    index.setFilePath(uploadFileTemp.getFilePath());
//                    cloudFileIndexDao.save(index);
//                    uploadFileTempDao.delete(uploadFileTemp);
//                    return index.getFileName();
//                }).orElse("");
//    }

    public List<CloudDirectoryIndex> getDirectories(long uid, DirType type, long dirId) {
        return findDirectory(uid, type, dirId)
                .map(directoryIndex -> cloudDirectoryIndexDao.findAllByParent(directoryIndex))
                .orElse(new ArrayList<CloudDirectoryIndex>());
    }

    public List<CloudDirectoryIndex> getDirectories(long uid, DirType type){
        return cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndParent(type,uid,null)
                .map(directoryIndex -> getDirectories(uid,type,directoryIndex.getId()))
                .orElse(new ArrayList<>());
    }

//    public List<CloudFileIndex> getFiles(long uid, DirType type, long dirId) {
//        return findDirectory(uid, type, dirId)
//                .map(directoryIndex -> cloudFileIndexDao.findAllByDirectoryIndex(directoryIndex))
//                .orElse(new ArrayList<>());
//    }

//    public List<CloudFileIndex>


    /**
     * 创建文件夹
     * @param uid
     * @param type
     * @param dirId
     * @param dirName
     * @return
     */
    public Result<CloudDirectoryIndex> createDirectory(long uid, DirType type, long dirId, String dirName) {
        CloudDirectoryIndex parent = null;
        if(StringUtils.isEmpty(dirName)){
            return Result.error("文件夹名不能为空");
        }
        //禁止创建同名
        if(dirId > 0){
            Optional<CloudDirectoryIndex> optional = findDirectory(uid, type, dirId);
            if (!optional.isPresent()) {
                return Result.error("找不到父文件夹");
            }
            parent = optional.get();
        }
        if(cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndParentAndDirName(DirType.USER,uid,parent,dirName).isPresent()){
            return Result.error("无法创建同名文件夹");
        }
        String finalFolderName1 = dirName;
        CloudDirectoryIndex finalParent = parent;
        CloudDirectoryIndex cloudDirectoryIndex = new CloudDirectoryIndex();
        cloudDirectoryIndex.setDirName(finalFolderName1);
        cloudDirectoryIndex.setType(type);
        cloudDirectoryIndex.setLinkId(uid);
        cloudDirectoryIndex.setDir(true);
        cloudDirectoryIndex.setParent(finalParent);
        return Result.ok(cloudDirectoryIndexDao.save(cloudDirectoryIndex));
    }

    /**
     * 重命名文件
     * @param uid
     * @param type
     * @param dirId
     * @param newName
     * @return
     */
    public String renameDirectory(long uid, DirType type, long dirId, String newName) {
        Optional<CloudDirectoryIndex> optional = findDirectory(uid, type, dirId)
                .map(directoryIndex -> {
                    //如果是文件,不改文件拓展名
                    String finalNewName;
                    if(directoryIndex.isDir()){
                        finalNewName = newName;
                    }
                    else{
                        int pointIndex = directoryIndex.getDirName().lastIndexOf(".");
                        if(pointIndex > -1){
                            String ext = directoryIndex.getDirName().substring(pointIndex,directoryIndex.getDirName().length() - 1);
                            finalNewName = newName + "." + ext;
                        }
                        else{
                            finalNewName = newName;
                        }
                    }
                    Optional<CloudDirectoryIndex> optionalCloudDirectoryIndex = cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndParentAndDirName(DirType.USER, uid,directoryIndex.getParent(), finalNewName);
                    if (optionalCloudDirectoryIndex.isPresent()) {
                        return null;
                    }
                    directoryIndex.setDirName(finalNewName);
                    return cloudDirectoryIndexDao.save(directoryIndex);
                });
        return optional.isPresent() ? optional.get().getDirName() : "";
    }

//    public boolean renameFile(long uid, DirType type, long oldId, String newName) {
//        return findFile(uid, type, oldId)
//                .filter(cloudFileIndex -> {
//                    //检查是否有同名文件
//                    if (cloudFileIndexDao.findFirstByDirectoryIndexAndFileName(cloudFileIndex.getDirectoryIndex(), newName).isPresent()) {
//                        return false;
//                    }
//                    cloudFileIndex.setFileName(newName);
//                    cloudFileIndexDao.save(cloudFileIndex);
//                    return true;
//                })
//                .isPresent();
//    }

//    public Set<Long> moveFiles(long uid, DirType dirType, Set<Long> fileIds, long targetDirId) {
//        CloudDirectoryIndex directoryIndex = findDirectory(uid, dirType, targetDirId).orElse(null);
//        Set<Long> set = new HashSet<>();
//        if (null == directoryIndex) return set;
//        fileIds.forEach(fileId -> {
//            findFile(uid, dirType, fileId)
//                    .ifPresent(fileIndex -> {
//                        CloudFileIndex ret = cloudFileIndexDao.findFirstByDirectoryIndexAndFileName(directoryIndex, fileIndex.getFileName()).orElseGet(() -> {
//                            CloudFileIndex fileIndex1 = new CloudFileIndex();
//                            fileIndex1.setFileName(fileIndex.getFileName());
//                            fileIndex1.setFilePath(fileIndex.getFilePath());
//                            return fileIndex1;
//                        });
//                        ret.setDirectoryIndex(directoryIndex);
//                        cloudFileIndexDao.save(ret);
//                        set.add(ret.getId());
//                    });
//        });
//        return set;
//    }

    public boolean moveDirectory(long uid, String dirName, String newPath) {
        return false;
    }

//    public Set<Long> deleteFile(long uid, DirType type, Set<Long> fileIds) {
//        Set<Long> ret = new HashSet<>();
//        fileIds.forEach(id -> {
//            findFile(uid, type, id).ifPresent(cloudFileIndex -> {
//                cloudFileIndexDao.delete(cloudFileIndex);
//                ret.add(id);
//            });
//        });
//        return ret;
//    }

    /**
     * 删除文件
     * @param uid
     * @param type
     * @param dirIds
     * @return
     */
    public boolean deleteDirectory(long uid, DirType type, List<Long> dirIds) {
        for (Long dirId : dirIds) {
           cloudDirectoryIndexDao.deleteByTypeAndLinkIdAndId(type,uid,dirId);
        }
        return true;
    }

    /**
     * 分享, 直接将文件作为消息转存给用户
     * @param fromUid
     * @param toUid
     * @param type
     * @param fileIds 需要发送的文件ID列表
     */
    public List<Long> sendFileToUser(long fromUid, long toUid, long linkId, DirType type, List<Long> fileIds){
        List<Long> result = new ArrayList<>();
        fileIds.stream().forEach(fileId -> {
            CloudDirectoryIndex cloudDirectoryIndex = findDirectory(linkId, type, fileId).orElse(null);
            if (null == cloudDirectoryIndex) {
                return;
            }
            SystemFile systemFile = cloudDirectoryIndex.getFile();
            if (null == systemFile) {
                return;
            }
            messageService.sendMessage(fromUid, toUid, "")
                    .ifPresent(message -> {
                        message.setType(Message.Type.FILE);
                        message.setContent(systemFile.getFileName());
                        message.setLinkId(systemFile.getId());
                        messageDao.save(message);
                        result.add(fileId);
                    });
        });
        return result;
    }


//    public Optional<CloudShare> shareTo(long fromUid, long toUid, DirType type, long fileId) {
//        if (fromUid == toUid) {
//            return Optional.empty();
//        }
//        return findDirectory(fromUid, type, fileId)
//                .map(cloudFileIndex -> {
//                    Optional<User> toUser = userService.findUser(toUid);
//                    CloudShare cloudShare = cloudShareDao.findFirstByToUserAndFileIndex(toUser.get(), cloudFileIndex).orElseGet(() -> {
//                        CloudShare cloudShare1 = new CloudShare();
//                        cloudShare1.setFileIndex(cloudFileIndex);
//                        cloudShare1.setToUser(toUser.get());
//                        return cloudShareDao.save(cloudShare1);
//                    });
//                    return cloudShare;
//                });
//    }
//
//
//
//    public Page<CloudShare> getShareFiles(long uid, Pageable pageable) {
//        return userService.findUser(uid)
//                .map(user -> {
//                    return cloudShareDao.findAllByToUser(user, pageable);
//                }).orElse(new PageImpl<CloudShare>(new ArrayList<>(), pageable, 0));
//    }
//
//    public boolean deleteShareFiles(long uid, Set<Long> shareIds) {
//        return userService.findUser(uid)
//                .filter(user -> {
//                    cloudShareDao.deleteAllByToUserAndIdIn(user, shareIds);
//                    return true;
//                }).isPresent();
//    }

//    public boolean saveShareFiles(long uid, DirType type, long targetDirId, Set<Long> shareIds) {
//        return findDirectory(uid, type, targetDirId)
//                .filter(directoryIndex -> {
//                    cloudShareDao.findAllByToUserIdAndIdIn(uid, shareIds).forEach(cloudShare -> {
//                        CloudFileIndex fileIndex = cloudShare.getFileIndex();
//                        fileIndex.setId(null);
//                        fileIndex.setDirectoryIndex(directoryIndex);
//                        cloudFileIndexDao.save(fileIndex);
//                    });
//                    return true;
//                }).isPresent();
//
//    }

//    public Set<CloudFileIndex> saveCommonFiles(long uid, long targetDirId, Set<Long> fileIds) {
//        Set<CloudFileIndex> set = new HashSet<>();
//        findDirectory(uid, DirType.USER, targetDirId)
//                .ifPresent(directoryIndex -> {
//                    fileIds.forEach(id -> {
//                        findFile(0, DirType.COMMON, id)
//                                .ifPresent(fileIndex -> {
//                                    //检查是否有同名文件
//                                    Optional<CloudFileIndex> optional = cloudFileIndexDao.findFirstByDirectoryIndexAndFileName(directoryIndex, fileIndex.getFileName());
//                                    if (optional.isPresent()) {
//                                        return;
//                                    }
//                                    fileIndex.setDirectoryIndex(directoryIndex);
//                                    fileIndex.setId(null);
//                                    set.add(cloudFileIndexDao.save(fileIndex));
//                                });
//                    });
//                });
//        return set;
//    }

//    public void downloadFile(long uid, DirType type, long fileId) {
//        findFile(uid, type, fileId)
//                .ifPresent(cloudFileIndex -> {
//                    HttpServletResponse res = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//                    res.setHeader("content-type", "application/octet-stream");
//                    res.setContentType("application/octet-stream");
//                    res.setHeader("Content-Disposition", "attachment;filename=" + cloudFileIndex.getFileName());
//                    byte[] buff = new byte[1024];
//                    BufferedInputStream bis = null;
//                    OutputStream os = null;
//                    try {
//                        os = res.getOutputStream();
//                        bis = new BufferedInputStream(new FileInputStream(new File(cloudFileIndex.getFilePath())));
//                        int i = bis.read(buff);
//                        while (i != -1) {
//                            os.write(buff, 0, buff.length);
//                            os.flush();
//                            i = bis.read(buff);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        if (bis != null) {
//                            try {
//                                bis.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//
//    }

//    public Optional<CloudFileIndex> findFile(long uid, DirType type, long fileId) {
//        CloudFileIndex fileIndex = cloudFileIndexDao.findOne(fileId);
//        if (null == fileIndex) {
//            return Optional.empty();
//        }
//        if (!fileIndex.getDirectoryIndex().getType().equals(type) || !fileIndex.getDirectoryIndex().getLinkId().equals(uid)) {
//            return Optional.empty();
//        }
//        return Optional.of(fileIndex);
//    }

    public Optional<CloudDirectoryIndex> findDirectory(long uid, DirType type, long dirId) {
        return cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndId(type, uid, dirId);
    }
}

