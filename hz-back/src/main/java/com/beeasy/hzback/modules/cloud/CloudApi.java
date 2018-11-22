package com.beeasy.hzback.modules.cloud;

import com.beeasy.hzback.modules.cloud.config.FeignConfig;
import com.beeasy.hzback.modules.cloud.response.CloudBaseResponse;
import com.beeasy.hzback.modules.cloud.response.CreateDirResponse;
import com.beeasy.hzback.modules.cloud.response.GetFilesResponse;
import com.beeasy.hzback.modules.cloud.response.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "cloud-service", url = "http://${filecloud.address}",configuration = FeignConfig.class)
public interface CloudApi {
    @RequestMapping(method = RequestMethod.GET, value = "/apiLogin.action")
    LoginResponse login(@RequestParam("username") String username, @RequestParam("password") String password);

//    @RequestMapping(method = RequestMethod.GET, value = "/disk/getFiles.action?page=1&pageSize=999")
//    LoginResponse getFiles(@RequestParam("pathId") long pathId);


    //是否登录
    @RequestMapping(value = "/checkOnline.action", method = RequestMethod.GET)
    CloudBaseResponse checkOnline();

    //创建工作目录
    @RequestMapping(value = "/disk/createFolder.action", method = RequestMethod.GET)
    CreateDirResponse createUserDir(@RequestParam("pid") long pid, @RequestParam("name") String name);

    //获得文件目录
    @RequestMapping(value = "/disk/getFiles.action", method = RequestMethod.GET)
    GetFilesResponse getFiles(@RequestParam("pathId") long pathId);

    //删除文件
    @RequestMapping(value = "/disk/deleteFiles.action", method = RequestMethod.GET)
    CloudBaseResponse deleteFiles(@RequestParam("id") long id);

    @RequestMapping(value = "/disk/deleteFiles.action", method = RequestMethod.GET)
    CloudBaseResponse deleteFiles(@RequestParam("id") String id);

    //重命名
    @RequestMapping(value = "/disk/renameFile.action", method = RequestMethod.GET)
    CloudBaseResponse renameFile(@RequestParam("id") long id, @RequestParam("name") String name);

    //设置标签
    @RequestMapping(value = "/disk/editTags.action", method = RequestMethod.GET)
    CloudBaseResponse editTags(@RequestParam("id") long id, @RequestParam("tags") String tags);

    //上传文件
    @RequestMapping(value = "/disk/uploadFiles.action", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CloudBaseResponse uploadFiles(@RequestParam("pid") long pid, @RequestPart("filedata") MultipartFile filedata);


    //全文检索
    @RequestMapping(value = "/disk/fileSearch.action", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    GetFilesResponse fileSearch(@RequestParam("keywords") String keywords);


    //下载文件


}
