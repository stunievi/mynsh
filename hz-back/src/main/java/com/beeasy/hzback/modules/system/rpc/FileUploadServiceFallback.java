package com.beeasy.hzback.modules.system.rpc;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadServiceFallback implements FileUploadService {

    @Override
    public String uploadFiles(MultipartFile filedata) {
        return "";
    }

    @Override
    public String uploadFiles(JSONObject object) {
        return "";
    }

}
