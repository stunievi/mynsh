package com.beeasy.hzback.modules.system.cache;

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
}
