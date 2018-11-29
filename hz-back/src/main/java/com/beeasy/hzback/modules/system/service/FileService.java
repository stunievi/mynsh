package com.beeasy.hzback.modules.system.service;

import com.beeasy.mscommon.RestException;
import com.beeasy.hzback.entity.SystemFile;
import com.beeasy.hzback.entity.User;
import com.beeasy.mscommon.filter.AuthFilter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.osgl.util.Img;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class FileService {
    @Autowired
    SQLManager sqlManager;

    @Value("${lfs.wfPid}")
    String wfPid;
    @Value("${lfs.wfUsername}")
    String wfUsername;
    @Value("${lfs.wfPassword}")
    String wfPassword;

    @Value("${uploads.type}")
    String UPLOAD_TYPE;
    @Value("${uploads.path}")
    String UPLOAD_PATH;
    @Value("${uploads.avatar}")
    String AVATAR_PATH;


    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


    /**
     * upload file
     * save on the local disk
     *
     * @param file
     * @param type
     * @param fileName
     * @param tags
     * @param dirName
     * @return
     */
    public Object uploadFile(MultipartFile file, SystemFile.Type type, String fileName, String tags,  String dirName, boolean save) throws IOException {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        if(S.empty(fileName)){
            fileName = file.getOriginalFilename();
        }
        User user = sqlManager.unique(User.class, AuthFilter.getUid());
        String mainDir = sdf.format(new Date());
        File dir = new File(UPLOAD_PATH, mainDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String realFileName = S.uuid() + "." + ext;
        File targetFile = new File(dir, realFileName);
        try (
            InputStream is = file.getInputStream();
            FileOutputStream fos = new FileOutputStream(targetFile);
        ) {
            IOUtils.copyLarge(is, fos);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestException("保存文件错误");
        }
        if(save){
            //save entity
            SystemFile systemFile = new SystemFile();
            systemFile.setExt(ext);
            systemFile.setFilePath(mainDir + "/" + fileName);
            systemFile.setLastModifyTime(new Date());
            systemFile.setFileName(fileName);
            systemFile.setStorageDriver(SystemFile.Driver.NATIVE);
            systemFile.setCreatorId(user.getId());
            systemFile.setCreatorName(user.getTrueName());
            systemFile.setTags(tags);
            systemFile.setType(type);
            systemFile.setToken(S.uuid());
            sqlManager.insert(systemFile,true);

            return systemFile;
        }
        else{
            return mainDir + "/" + realFileName;
        }

    }

    public boolean uploadFace(long uid, MultipartFile file) throws IOException {
        File dir = new File(AVATAR_PATH);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String exten = S.fileExtension(file.getOriginalFilename()).toLowerCase();
        if(S.blank(exten)){
            exten = S.fileExtension(file.getName().toLowerCase());
        }

        List<String> limit = C.list("png","jpg","jpeg", "gif");
        if(!limit.contains(exten)){
            throw new RestException("只允许jpg/png/jpeg类型的图片");
        }

        Img.source(file.getInputStream())
            .resize(152,152)
            .writeTo(new File(dir,uid + ".jpg"));

        return true;

//        String fileName = S.uuid() + "." + exten;
//        IO.copy(
//            file.getInputStream()
//            , new FileOutputStream(
//            );
//        sqlManager.lambdaQuery(User.class)
//            .andEq(User::getId, AuthFilter.getUid())
//            .updateSelective(C.newMap(
//                "avatar", fileName
//            ));
//        return fileName;
    }


    private File prepareDir() throws IOException {
        File dir = new File(UPLOAD_PATH, sdf.format(new Date()));
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

}
