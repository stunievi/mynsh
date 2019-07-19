package com.beeasy.hzback.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.SysVar;
import com.beeasy.hzback.entity.SystemFile;
import com.beeasy.hzback.modules.system.service.FileService;
import com.beeasy.hzback.modules.system.service.LFSService;
import com.beeasy.hzcloud.control.CFileController;
import com.beeasy.hzcloud.entity.CFile;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RequestMapping("/api/file")
@Controller
@Transactional
public class FileController {


    @Autowired
    SQLManager      sqlManager;
    @Autowired
    LFSService      lfsService;
    @Autowired
    FileService     fileService;
    @Autowired
    CFileController cFileController;

    @Value("${filecloud.username}")
    String wfUsername;
    @Value("${filecloud.password}")
    String wfPassword;
    @Value("${filecloud.wfPid}")
    String wfPid;

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    private String getStorageEngine() {
        JSONObject object = new JSONObject();
        object.put("key", "file_storage_engine");
        JSONObject map = (JSONObject) new SysVar().getConfig(sqlManager, object);
        String storage = map.getString("file_storage_engine");
        if (S.empty(storage)) storage = "linkapp";
        return storage;
    }

    @RequestMapping(value = "/uploadFace", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadFace(
        @RequestParam MultipartFile file
    ) throws IOException {
        fileService.uploadFace(AuthFilter.getUid(), file);
        return Result.ok();
    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String upload(
        @RequestParam MultipartFile file,
        String name,
        String tags,
        @RequestParam SystemFile.Type type
        , String dir
        , String uuid,
        Long uid
    ) {
        if (uid == null) {
            uid = AuthFilter.getUid();
        }
        switch (getStorageEngine()) {
            case "linkapp":
                try {
                    String sessionId = lfsService.login(wfUsername, wfPassword);
                    if (type.equals(SystemFile.Type.MESSAGE)) {
                        dir = sdf.format(new Date());
                        name = S.uuid() + "." + S.fileExtension(file.getOriginalFilename());
                    }
                    String pid = lfsService.createDir(sessionId, wfPid, dir);
                    String fid = lfsService.uploadFile(sessionId, pid, name, file);
                    //修改tag
                    if (S.notEmpty(tags)) {
                        lfsService.editTags(sessionId, fid, tags);
                    }
                    return JSON.toJSONString(Result.ok(
                            C.newMap(
                                    "id", fid
                                    , "name", name
                                    , "tags", tags
                                    , "creator", uid
                                    , "uuid", uuid
                                    , "sname", file.getOriginalFilename()
                            )
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.error(uuid).toJson();
                }


            case "self":
                JSONObject object = new JSONObject();
                AuthFilter.setUid(1L);
                object.put("path", "/" + sdf.format(new Date()));
                CFile pdir = (CFile) new CFile().mkdir(sqlManager, object);
                Result<CFile> result = cFileController.upload(file, pdir.getId(), 1L);
                if(!result.isSuccess()){
                    return Result.error(uuid).toJson();
                }
                CFile cFile = (CFile) result.getData();
                return JSON.toJSONString(Result.ok(
                    C.newMap(
                        "id", cFile.getId()
                        , "name", name
                        , "tags", tags
                        , "creator", uid
                        , "uuid", uuid
                        , "sname", file.getOriginalFilename()
                    )
                ));

        }

        throw new RestException();

    }


    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result delete(
        @RequestParam long fileId,
        @RequestParam String token
    ) {
        SystemFile sFile = sqlManager.single(SystemFile.class, fileId);
        if ($.isNotNull(sFile)) {
            sqlManager.lambdaQuery(SystemFile.class)
                .andEq(SystemFile::getId, fileId)
                .andEq(SystemFile::getCreatorId, AuthFilter.getUid())
                .delete();
            try {
                Files.delete(Paths.get(sFile.getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.ok();
    }

    @RequestMapping(value = "/rename", method = RequestMethod.GET)
    @ResponseBody
    public Result modify(
        @RequestParam String fid,
        String name
    ) {
        try {
            String sessionId = lfsService.login(wfUsername, wfPassword);
            lfsService.rename(sessionId, fid, name);
            return Result.ok();
        } catch (Exception e) {
            return Result.error("修改文件名失败");
        }
    }

    @RequestMapping(value = "/retags", method = RequestMethod.GET)
    @ResponseBody
    public Result modifyTags(
        @RequestParam String fid,
        String tags
    ) {
        try {
            String sessionId = lfsService.login(wfUsername, wfPassword);
            lfsService.editTags(sessionId, fid, tags);
            return Result.ok();
        } catch (Exception e) {
            return Result.error("修改文件名失败");
        }
    }


    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(
        @RequestParam String fid
        , @RequestParam String name,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        switch (getStorageEngine()){
            case "linkapp":
//                HttpHeaders headers = new HttpHeaders();
                try {
                    String sessionId = lfsService.login(wfUsername, wfPassword);
                    byte[] bytes = lfsService.downloadFile(sessionId, fid);
                    String ext = S.fileExtension(name);
                    makeHeader(response, ext, name);
                    response.addHeader("Content-Length", bytes.length + "");
                    try(
                        OutputStream os = response.getOutputStream();
                        ){
                       os.write(bytes);
                       response.flushBuffer();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;

            case "self":
                cFileController.download(Long.parseLong(fid), request, response);
                return;
        }

        throw new RestException("下载失败");
    }


    private void makeHeader(HttpServletResponse response, String ext, String fileName){
        switch (ext.toLowerCase()) {
            case "jpg":
            case "jpeg":
            case "bmp":
                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                break;

            case "png":
                response.setContentType(MediaType.IMAGE_PNG_VALUE);
                break;

            case "gif":
                response.setContentType(MediaType.IMAGE_GIF_VALUE);
                break;

            default:
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        }
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RestException("下载失败");
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=utf-8''" + fileName);
    }

    private void makeHeader(HttpHeaders headers, String ext, String fileName) {
        switch (ext.toLowerCase()) {
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
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RestException("下载失败");
        }
        headers.set("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=utf-8''" + fileName);
    }


}
