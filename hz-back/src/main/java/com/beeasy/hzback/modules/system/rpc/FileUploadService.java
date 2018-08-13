package com.beeasy.hzback.modules.system.rpc;


import com.beeasy.hzback.modules.cloud.config.FeignConfig;
import com.beeasy.hzback.modules.cloud.response.CloudBaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "service-upload",configuration = FeignConfig.class, fallback = FileUploadServiceFallback.class)
public interface FileUploadService {
    @RequestMapping(value = "/service/upload/uploadFile",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFiles(@RequestPart("filedata") MultipartFile filedata);

}
