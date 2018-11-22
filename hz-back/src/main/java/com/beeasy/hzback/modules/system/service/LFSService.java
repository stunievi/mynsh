package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.util.OkHttpUtil;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class LFSService {

    @Value("${lfs.server}")
    String baseUrl;


    private static final String apiLogin = "";

    public String login(String username, String password){
        String str = OkHttpUtil.getForHeader(buildUrl("/apiLogin.action"), C.newMap("username",username,"password",password), null);
        JSONObject object = JSON.parseObject(str);
        String sessionId = object.getString("sessionId");
        return sessionId;
    }

    public String createDir(String sessionId, String pid, String name){
        String str = OkHttpUtil.getForHeader(buildUrl(S.fmt("/disk/createFolder.action",pid,name)), C.newMap(
            "pid",pid
            ,"name",name
        ), buildCookie(sessionId));
        JSONObject object = JSON.parseObject(str);
        return object.getString("id");
    }

    public String uploadFile(String sessionId, String pid, String fileName, MultipartFile multipartFile) throws IOException {
        OkHttpUtil.postFile(buildUrl("/disk/uploadFiles.action"), C.newMap(
            "pid",pid,
            "filedata", new OkHttpUtil.FakeFile(fileName,multipartFile.getInputStream())
        ),buildCookie(sessionId));
        //检索上传的文件
        String str = OkHttpUtil.getForHeader(buildUrl(S.fmt("/disk/getFiles.action")),C.newMap("pathId", pid, "pageSize", 1000), buildCookie(sessionId));
        JSONObject object = JSON.parseObject(str);
        return object.getJSONArray("rows").stream()
            .map(item -> (JSONObject) item)
            .filter(item -> item.getBoolean("isFile") && S.eq(item.getString("name"), fileName))
            .findFirst()
            .map(item -> item.getString("autoId"))
            .orElse(null);
//        return object.getString("id");
    }

    public void editTags(String sessionId, String fid, String tags){
        OkHttpUtil.getForHeader(buildUrl("/disk/editTags.action"), C.newMap("id",fid, "tags", tags), buildCookie(sessionId));
    }
    public void rename(String sessionId, String fid, String name){
        OkHttpUtil.getForHeader(buildUrl("/disk/renameFile.action"), C.newMap("id",fid, "name", name), buildCookie(sessionId));
    }

    public byte[] downloadFile(String sessionId, String fid){
        return OkHttpUtil.download(buildUrl("/disk/download.action?id=" + fid), buildCookie(sessionId));
    }


    private String buildUrl(String url){
        return baseUrl + url;
    }

    private Map buildCookie(String sessionId){
         return C.newMap("Cookie",S.fmt("JSESSIONID=%s",sessionId));
    }
}
