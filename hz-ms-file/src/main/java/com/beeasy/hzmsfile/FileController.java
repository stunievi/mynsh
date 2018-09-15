package com.beeasy.hzmsfile;

import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.entity.SystemFile;
import com.beeasy.mscommon.entity.User;
import com.beeasy.mscommon.entity.UserProfile;
import com.beeasy.mscommon.entity.UserToken;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.IO;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequestMapping("/file")
@Controller
@Transactional
public class FileController {

    @Autowired
    SQLManager sqlManager;

    @Value("${uploads.type}")
    String UPLOAD_TYPE;

    @Value("${uploads.path}")
    String UPLOAD_PATH;



    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Result upload(
            @RequestParam MultipartFile file,
            @RequestParam String token,
            String fileName,
            String tags,
            SystemFile.Type type
    ) {
        User user = checkAuth(token);
        Path path;
        try {
            path = prepareDir();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestException("生成目录失败");
        }
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        if(S.empty(fileName)){
            fileName = file.getOriginalFilename();
        }
        File targetFile = new File(path.toAbsolutePath().toString() + File.separator + UUID.randomUUID().toString() + "." + ext);
        try (
                InputStream is = file.getInputStream();
                FileOutputStream fos = new FileOutputStream(targetFile);
        ) {
            IOUtils.copyLarge(is, fos);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestException("保存文件错误");
        }


        //save entity
        SystemFile systemFile = new SystemFile();
        systemFile.setExt(ext);
        systemFile.setFilePath(targetFile.getAbsolutePath());
        systemFile.setLastModifyTime(new Date());
        systemFile.setFileName(fileName);
        systemFile.setStorageDriver(SystemFile.Driver.NATIVE);
        systemFile.setCreatorId(user.getId());
        systemFile.setCreatorName(user.getTrueName());
        systemFile.setTags(tags);
        systemFile.setType(type);

        //file type
        if($.isNull(type)){
            type = SystemFile.Type.TEMP;
        }
        if(type.equals(SystemFile.Type.FACE)){
            List<String> limit = C.list("png","jpg","jpeg");
            if(!limit.contains(systemFile.getExt().toLowerCase())){
                throw new RestException("只允许jpg/png/jpeg类型的图片");
            }
        }

        //make download token
        systemFile.setToken(S.uuid());
        systemFile.setExprTime(new Date(System.currentTimeMillis() + 3600000));
        sqlManager.insert(systemFile, true);

        //update user face
        if(systemFile.getId() > 0 && systemFile.getType().equals(SystemFile.Type.FACE)){
            UserProfile up = (UserProfile) user.get("profile");
            if($.isNotNull(up)){
                up.setFaceId(systemFile.getId());
                sqlManager.updateById(up);
            }
        }
        return Result.ok(systemFile);
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result delete(
            @RequestParam long fileId,
            @RequestParam String token
    ){
        User user = checkAuth(token);
        SystemFile sFile = sqlManager.single(SystemFile.class, fileId);
        if($.isNotNull(sFile)){
            sqlManager.lambdaQuery(SystemFile.class)
                    .andEq(SystemFile::getId, fileId)
                    .andEq(SystemFile::getCreatorId, user.getId())
                    .delete();
            try {
                Files.delete(Paths.get(sFile.getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.ok();
    }

    @RequestMapping(value = "/rename", method = RequestMethod.POST)
    @ResponseBody
    public Result modify(
            @RequestParam long fileId,
            @RequestParam String token,
            String fileName
    ){
        User user = checkAuth(token);
        SystemFile sFile = sqlManager.single(SystemFile.class, fileId);
        if($.isNull(sFile)){
            return Result.error("修改失败");
        }
        sFile.setFileName(S.fmt("%s.%s",fileName,sFile.getExt()));
        sqlManager.updateById(sFile);
        return Result.ok(sFile);
    }

    @RequestMapping(value = "/retags", method = RequestMethod.POST)
    @ResponseBody
    public Result modifyTags(
            @RequestParam long fileId,
            @RequestParam String token,
            String tags
    ){
        User user = checkAuth(token);
        SystemFile sFile = sqlManager.single(SystemFile.class, fileId);
        if($.isNull(sFile)){
            return Result.error("修改失败");
        }
        sFile.setTags(tags);
        sqlManager.updateById(sFile);
        return Result.ok(sFile);
    }


    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(
            @RequestParam long fileId,
            String fileToken
    ){
        SystemFile file = sqlManager.single(SystemFile.class, fileId);
        boolean allow = false;
        if(!Files.exists(Paths.get(file.getFilePath()))){
            allow = false;
        }
        //if this file is face file, ignore token
        if(file.getType().equals(SystemFile.Type.FACE) || file.getType().equals(SystemFile.Type.DEFALUT_FACE)){
            allow = true;
        }
        else if(
                file.getToken().equals(fileToken) &&
                $.isNotNull(file.getExprTime()) &&
                file.getExprTime().after(new Date())
        ){
            allow = true;
        }
        if(!allow){
            return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
        }
        HttpHeaders headers = new HttpHeaders();
        switch (file.getExt().toLowerCase()) {
            case "jpg":
            case "jpeg":
            case "bmp":
                headers.setContentType(MediaType.IMAGE_JPEG);
                break;

            case "png":
                headers.setContentType(MediaType.IMAGE_PNG);
                break;

            case "gif":
                headers.setContentType(MediaType.IMAGE_GIF);
                break;

            default:
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        String fileName = null;
        try {
            fileName = URLEncoder.encode(file.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RestException("下载失败");
        }
        headers.set("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=utf-8''" + fileName);
        return new ResponseEntity<byte[]>(IO.readContent(new File(file.getFilePath())), headers, HttpStatus.OK);
    }



    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private Path prepareDir() throws IOException {
        Path path;
        if (!Files.exists(path = Paths.get(UPLOAD_PATH))) {
            Files.createDirectory(path);
        }
        //create
        if (!Files.exists(path = Paths.get(UPLOAD_PATH + File.separator + sdf.format(new Date())))) {
            Files.createDirectory(path);
        }
        return path;
    }


    private User checkAuth(String token) {
        RestException err = new RestException("用户信息错误");
        if (S.blank(token)) {
            throw err;
        }
        long uid = 0;
        if (S.isIntOrLong(token)) {
            uid = Long.valueOf(token);
        }
        else{
            List<UserToken> list = sqlManager.lambdaQuery(UserToken.class)
                    .andEq(UserToken::getToken, token)
                    .andGreat(UserToken::getExprTime, new Date())
                    .select();
            if(C.empty(list)){
                throw err;
            }
            uid = list.get(0).getUserId();
        }
        return sqlManager.unique(User.class, uid);
    }


}
