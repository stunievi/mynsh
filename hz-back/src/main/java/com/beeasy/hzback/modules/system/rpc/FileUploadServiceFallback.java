package com.beeasy.hzback.modules.system.rpc;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadServiceFallback implements FileUploadService{

    @Override
    public String uploadFiles(MultipartFile filedata) {
        return "";
    }
}
