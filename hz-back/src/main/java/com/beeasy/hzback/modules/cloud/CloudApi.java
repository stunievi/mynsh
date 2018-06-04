package com.beeasy.hzback.modules.cloud;

import com.beeasy.hzback.modules.cloud.config.FeignConfig;
import com.beeasy.hzback.modules.cloud.response.LoginResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cloud-service", url = "${filecloud.address}",configuration = FeignConfig.class)
public interface CloudApi {
    @RequestMapping(method = RequestMethod.GET, value = "/apiLogin.action")
    LoginResponse login(@RequestParam("username") String username, @RequestParam("password") String password);

//    @RequestMapping(method = RequestMethod.GET, value = "/disk/getFiles.action?page=1&pageSize=999")
//    LoginResponse getFiles(@RequestParam("pathId") long pathId);


    //是否登录

    //创建工作目录

    //上传文件

    //下载文件
}
