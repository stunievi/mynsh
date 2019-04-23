package com.beeasy.easyshop.core;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Config {
    public static Config config;
    public int port;
    public Map<String,Db> db;
    public Set<String> hotswap;
    public LinkedHashSet<String> route;

    static {
        try (
            FileInputStream fis = new FileInputStream("config.json");
            ){
            String str = IoUtil.read(fis).toString(StandardCharsets.UTF_8);
            config = JSON.parseObject(str, Config.class);

            if (config.route != null) {
                for (String s : config.route) {
                    String[] route = s.split("\\s*->\\s*");
                    HttpServerHandler.ctrls.put(route[0], route[1]);
                }
            }
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
