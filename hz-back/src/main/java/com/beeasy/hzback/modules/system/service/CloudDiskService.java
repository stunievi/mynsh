package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.dao.ICloudDirectoryIndexDao;
import com.beeasy.hzback.modules.system.dao.ICloudFileIndexDao;
import com.beeasy.hzback.modules.system.dao.ICloudShareDao;
import com.beeasy.hzback.modules.system.dao.IUploadFileTempDao;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.CloudFileIndex;
import com.beeasy.hzback.modules.system.entity.CloudShare;
import com.beeasy.hzback.modules.system.entity.UploadFileTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

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
    ICloudFileIndexDao cloudFileIndexDao;
    @Autowired
    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
    @Autowired
    ICloudShareDao cloudShareDao;

    public String createUploadSign(long uid, DirType type, long dirId, String fileName) {
        Optional<CloudDirectoryIndex> target = findDirectory(uid, type, dirId);
        if (!target.isPresent()) {
            return "";
        }
        CloudDirectoryIndex cloudDirectoryIndex = target.get();
        Optional<UploadFileTemp> optionalUploadFileTemp = uploadFileTempDao.findFirstByDirectoryIndexAndFileName(cloudDirectoryIndex, fileName);
        if(!optionalUploadFileTemp.isPresent()){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
            String today = sdf.format(new Date());
            UploadFileTemp temp1 = new UploadFileTemp();
            temp1.setDirectoryIndex(cloudDirectoryIndex);
            temp1.setFileName(fileName);
            String uuid = UUID.randomUUID().toString();
            temp1.setUuid(uuid);
            temp1.setFilePath(AREA_DIR + File.separator + today + File.separator + uuid);
            return (uploadFileTempDao.save(temp1)).getUuid();
        }
        return optionalUploadFileTemp.get().getUuid();
    }


    public boolean uploadFile(String sign, MultipartFile file) {
        return uploadFileTempDao.findFirstByUuid(sign).map(uploadFileTemp -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
            String today = sdf.format(new Date());

            Path path = Paths.get(AREA_DIR + File.separator + today);
            if (Files.notExists(path)) {
                try {
                    Files.createDirectory(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            //合并文件
            RandomAccessFile raFile = null;
            BufferedInputStream inputStream = null;
            try {
                File dirFile = new File(uploadFileTemp.getFilePath());
                //以读写的方式打开目标文件
                raFile = new RandomAccessFile(dirFile, "rw");
                raFile.seek(raFile.length());
                inputStream = new BufferedInputStream(file.getInputStream());
                byte[] buf = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(buf)) != -1) {
                    raFile.write(buf, 0, length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (raFile != null) {
                        raFile.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;

        }).orElse(false);
    }

    public String finishUpload(String uuid) {
        return uploadFileTempDao.findFirstByUuid(uuid)
                .map(uploadFileTemp -> {
                    //检查文件是否存在
                    CloudFileIndex index = cloudFileIndexDao
                            .findFirstByDirectoryIndexAndFileName(uploadFileTemp.getDirectoryIndex(), uploadFileTemp.getFileName())
                            .orElse(new CloudFileIndex());
                    index.setDirectoryIndex(uploadFileTemp.getDirectoryIndex());
                    index.setFileName(uploadFileTemp.getFileName());
                    index.setFilePath(uploadFileTemp.getFilePath());
                    cloudFileIndexDao.save(index);
                    uploadFileTempDao.delete(uploadFileTemp);
                    return index.getFileName();
                }).orElse("");
    }

    public List<CloudDirectoryIndex> getDirectories(long uid, DirType type, long dirId) {
        return findDirectory(uid, type, dirId)
                .map(directoryIndex -> cloudDirectoryIndexDao.findAllByParent(directoryIndex))
                .orElse(new ArrayList<CloudDirectoryIndex>());
    }

    public List<CloudFileIndex> getFiles(long uid, DirType type, long dirId) {
        return findDirectory(uid, type, dirId)
                .map(directoryIndex -> cloudFileIndexDao.findAllByDirectoryIndex(directoryIndex))
                .orElse(new ArrayList<>());
    }

    public Optional<CloudDirectoryIndex> createDirectory(long uid, DirType type, long dirId, String dirName) {
        Optional<CloudDirectoryIndex> optional = findDirectory(uid, type, dirId);
        if (optional.isPresent()) {
            return Optional.empty();
        }
        String finalFolderName1 = dirName;
        return userService.findUser(uid)
                .map(user -> {
                    CloudDirectoryIndex cloudDirectoryIndex = new CloudDirectoryIndex();
                    cloudDirectoryIndex.setDirName(finalFolderName1);
                    cloudDirectoryIndex.setType(type);
                    cloudDirectoryIndex.setLinkId(uid);
                    return cloudDirectoryIndexDao.save(cloudDirectoryIndex);
                });
    }

    public boolean renameDirectory(long uid, DirType type, long dirId, String newName) {
        return findDirectory(uid, type, dirId)
                .map(directoryIndex -> {
                    String finalNewName = (newName);
                    Optional<CloudDirectoryIndex> optionalCloudDirectoryIndex = cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndDirName(DirType.USER, uid, finalNewName);
                    if (optionalCloudDirectoryIndex.isPresent()) {
                        return false;
                    }
                    directoryIndex.setDirName(finalNewName);
                    return cloudDirectoryIndexDao.save(directoryIndex);
                }).isPresent();

    }

    public boolean renameFile(long uid, DirType type, long oldId, String newName) {
        return findFile(uid, type, oldId)
                .filter(cloudFileIndex -> {
                    //检查是否有同名文件
                    if (cloudFileIndexDao.findFirstByDirectoryIndexAndFileName(cloudFileIndex.getDirectoryIndex(), newName).isPresent()) {
                        return false;
                    }
                    cloudFileIndex.setFileName(newName);
                    cloudFileIndexDao.save(cloudFileIndex);
                    return true;
                })
                .isPresent();
    }

    public Set<Long> moveFiles(long uid, DirType dirType, Set<Long> fileIds, long targetDirId) {
        CloudDirectoryIndex directoryIndex = findDirectory(uid, dirType, targetDirId).orElse(null);
        Set<Long> set = new HashSet<>();
        if (null == directoryIndex) return set;
        fileIds.forEach(fileId -> {
            findFile(uid, dirType, fileId)
                    .ifPresent(fileIndex -> {
                        CloudFileIndex ret = cloudFileIndexDao.findFirstByDirectoryIndexAndFileName(directoryIndex, fileIndex.getFileName()).orElseGet(() -> {
                            CloudFileIndex fileIndex1 = new CloudFileIndex();
                            fileIndex1.setFileName(fileIndex.getFileName());
                            fileIndex1.setFilePath(fileIndex.getFilePath());
                            return fileIndex1;
                        });
                        ret.setDirectoryIndex(directoryIndex);
                        cloudFileIndexDao.save(ret);
                        set.add(ret.getId());
                    });
        });
        return set;
    }

    public boolean moveDirectory(long uid, String dirName, String newPath) {
        return false;
    }

    public Set<Long> deleteFile(long uid, DirType type, Set<Long> fileIds) {
        Set<Long> ret = new HashSet<>();
        fileIds.forEach(id -> {
            findFile(uid, type, id).ifPresent(cloudFileIndex -> {
                cloudFileIndexDao.delete(cloudFileIndex);
                ret.add(id);
            });
        });
        return ret;
    }

    public boolean deleteDirectory(long uid, DirType type, long dirId) {
        //删除该文件夹下所有的文件
        return findDirectory(uid, type, dirId)
                .filter(cloudFileIndex -> {
                    //根目录禁止删除!!
                    if (cloudFileIndex.getDirName().equals("/")) {
                        return false;
                    }
                    cloudDirectoryIndexDao.delete(cloudFileIndex);
                    return true;
                })
                .isPresent();
    }


    public Optional<CloudShare> shareTo(long fromUid, long toUid, DirType type, long fileId) {
        if (fromUid == toUid) {
            return Optional.empty();
        }
        return findFile(fromUid, type, fileId)
                .map(cloudFileIndex -> {
                    Optional<User> toUser = userService.findUser(toUid);
                    CloudShare cloudShare = cloudShareDao.findFirstByToUserAndFileIndex(toUser.get(), cloudFileIndex).orElseGet(() -> {
                        CloudShare cloudShare1 = new CloudShare();
                        cloudShare1.setFileIndex(cloudFileIndex);
                        cloudShare1.setToUser(toUser.get());
                        return cloudShareDao.save(cloudShare1);
                    });
                    return cloudShare;
                });
    }

    public Page<CloudShare> getShareFiles(long uid, Pageable pageable) {
        return userService.findUser(uid)
                .map(user -> {
                    return cloudShareDao.findAllByToUser(user, pageable);
                }).orElse(new PageImpl<CloudShare>(new ArrayList<>(), pageable, 0));
    }

    public boolean deleteShareFiles(long uid, Set<Long> shareIds) {
        return userService.findUser(uid)
                .filter(user -> {
                    cloudShareDao.deleteAllByToUserAndIdIn(user, shareIds);
                    return true;
                }).isPresent();
    }

    public boolean saveShareFiles(long uid, DirType type, long targetDirId, Set<Long> shareIds) {
        return findDirectory(uid, type, targetDirId)
                .filter(directoryIndex -> {
                    cloudShareDao.findAllByToUserIdAndIdIn(uid, shareIds).forEach(cloudShare -> {
                        CloudFileIndex fileIndex = cloudShare.getFileIndex();
                        fileIndex.setId(null);
                        fileIndex.setDirectoryIndex(directoryIndex);
                        cloudFileIndexDao.save(fileIndex);
                    });
                    return true;
                }).isPresent();

    }

    public Set<CloudFileIndex> saveCommonFiles(long uid, long targetDirId, Set<Long> fileIds) {
        Set<CloudFileIndex> set = new HashSet<>();
        findDirectory(uid, DirType.USER, targetDirId)
                .ifPresent(directoryIndex -> {
                    fileIds.forEach(id -> {
                        findFile(0, DirType.COMMON, id)
                                .ifPresent(fileIndex -> {
                                    //检查是否有同名文件
                                    Optional<CloudFileIndex> optional = cloudFileIndexDao.findFirstByDirectoryIndexAndFileName(directoryIndex, fileIndex.getFileName());
                                    if (optional.isPresent()) {
                                        return;
                                    }
                                    fileIndex.setDirectoryIndex(directoryIndex);
                                    fileIndex.setId(null);
                                    set.add(cloudFileIndexDao.save(fileIndex));
                                });
                    });
                });
        return set;
    }

    public void downloadFile(long uid, DirType type, long fileId) {
        findFile(uid, type, fileId)
                .ifPresent(cloudFileIndex -> {
                    HttpServletResponse res = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                    res.setHeader("content-type", "application/octet-stream");
                    res.setContentType("application/octet-stream");
                    res.setHeader("Content-Disposition", "attachment;filename=" + cloudFileIndex.getFileName());
                    byte[] buff = new byte[1024];
                    BufferedInputStream bis = null;
                    OutputStream os = null;
                    try {
                        os = res.getOutputStream();
                        bis = new BufferedInputStream(new FileInputStream(new File(cloudFileIndex.getFilePath())));
                        int i = bis.read(buff);
                        while (i != -1) {
                            os.write(buff, 0, buff.length);
                            os.flush();
                            i = bis.read(buff);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (bis != null) {
                            try {
                                bis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }

    public Optional<CloudFileIndex> findFile(long uid, DirType type, long fileId) {
        CloudFileIndex fileIndex = cloudFileIndexDao.findOne(fileId);
        if (null == fileIndex) {
            return Optional.empty();
        }
        if (!fileIndex.getDirectoryIndex().getType().equals(type) || !fileIndex.getDirectoryIndex().getLinkId().equals(uid)) {
            return Optional.empty();
        }
        return Optional.of(fileIndex);
    }

    public Optional<CloudDirectoryIndex> findDirectory(long uid, DirType type, long dirId) {
        if (dirId == 0) {
            return cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndDirName(type, uid, "/");
        } else {
            return cloudDirectoryIndexDao.findFirstByTypeAndLinkIdAndId(type, uid, dirId);
        }
    }
}

