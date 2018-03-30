package com.beeasy.hzback.modules.system.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

@CacheConfig(cacheNames = "system_config")
@Component
public class SystemConfigCache {

    @Cacheable(key = "'config'")
    public Object getConfig(){
        try {
            Reader r = new FileReader("/Users/bin/work/configlist.yaml");
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
