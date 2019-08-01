package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class Config {
    public int port;
    public boolean dev = false;
    public DataSourceConfig datasource;
    public String mqserver;
    public WorkMode workmode;
    public LogConfig log;
    public String file;

    public static class DataSourceConfig{
        public String url;
        public String username;
        public String password;
        public String driver;
    }

    public static class LogConfig{
        public String source;
        public String unzip;
        public String sql;
        public String blob;
    }

    public static Config config;

    static {
        String content;
        File file = new File("./config.json");
        try (
            FileReader reader = new FileReader(file);
        ){
            content = IoUtil.read(reader);
        } catch (IOException e) {
            ClassPathResource resource = new ClassPathResource("config.json");
            content = IoUtil.read(resource.getStream(), CharsetUtil.UTF_8);
        }
        config = JSON.parseObject(content, Config.class);
    }


    public enum WorkMode{
        ALL,
        DECONSTRUCT,
        SEARCH
    }

}
