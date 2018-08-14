package com.beeasy.hzback.modules.system.service;

import lombok.Data;

public interface ICloudDiskService {
    static enum Type {
        FILE,
        DIRECTORY
    }

    ;

    enum DirType {
        USER(0, "USER"),
        COMMON(1, "COMMON");

        private int value;
        private String name;

        DirType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String toString() {
            return name;
        }
    }

    @Data
    static class CloudFile {
        String path = "/";
        String fileName = "";
    }

}
