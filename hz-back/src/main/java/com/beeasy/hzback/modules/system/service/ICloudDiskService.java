package com.beeasy.hzback.modules.system.service;

import lombok.Data;

public interface ICloudDiskService {
    static enum Type{
        FILE,
        DIRECTORY
    };

    enum DirType {
        USER,
        COMMON
    }

    @Data
    static class CloudFile {
        String path = "/";
        String fileName = "";
    }

}
