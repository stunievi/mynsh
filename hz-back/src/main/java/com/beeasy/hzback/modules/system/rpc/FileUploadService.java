package com.beeasy.hzback.modules.system.rpc;


import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.modules.cloud.config.FeignConfig;
import com.beeasy.hzback.modules.cloud.response.CloudBaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "service-upload", configuration = FeignConfig.class, fallback = FileUploadServiceFallback.class)
public interface FileUploadService {
    @RequestMapping(value = "/service/upload/uploadFile",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFiles(@RequestPart("filedata") MultipartFile filedata);

    @RequestMapping(value = "/service/upload/uploadFileJSON",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    String uploadFiles(@RequestBody JSONObject object);

}
