package com.beeasy.test;


import com.beeasy.hzreport.filter.AuthFilter;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.entity.SystemFile;
import com.beeasy.mscommon.entity.User;
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




    public boolean uploadFace(long uid, MultipartFile file) throws IOException {
        File dir = new File(AVATAR_PATH);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String exten = S.fileExtension(file.getOriginalFilename()).toLowerCase();

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