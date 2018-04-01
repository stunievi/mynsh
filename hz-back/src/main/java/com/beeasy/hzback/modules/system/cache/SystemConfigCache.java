package com.beeasy.hzback.modules.system.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

@CacheConfig(cacheNames = "system_config")
@Component
public class SystemConfigCache {

    @Cacheable(key = "'config'")
    public Object getConfig(){
        try {
            String filePath = "classpath:workflow/config.yaml";
            File file = ResourceUtils.getFile(filePath);
            Reader r = new FileReader(file);
            Yaml yaml = new Yaml();
            Object o = yaml.load(r);
            r.close();
            return o;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Cacheable(key = "'full_menu'")
    public JSONArray getFullMenu() throws IOException {
        String filePath = "classpath:config/menu.json";
        File file = ResourceUtils.getFile(filePath);
        Long fileLength = file.length();
        byte[] filecontent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String menu = new String(filecontent, "utf8");
            JSONArray arr = JSON.parseArray(menu);
            return arr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }
}
