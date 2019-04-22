package com.beeasy.easyshop.core;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class Config {
    public static Config config;
    public int port;
    public String watch;
    public Map<String,Db> db;
    public Set<String> hotswap;

    static {
        try (
            FileInputStream fis = new FileInputStream("config.json");
            ){
            String str = IoUtil.read(fis).toString(StandardCharsets.UTF_8);
            config = JSON.parseObject(str, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Db{
        public String url;
        public String driver;
        public String username;
        public String password;
    }
}
