package com.beeasy.hzback.modules.system.cache;

import com.beeasy.hzback.core.helper.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CacheConfig(cacheNames = "system_config")
@Component
public class SystemConfigCache {

    public static final String DEMO_CACHE_NAME = "system_config";

    @Cacheable(value = DEMO_CACHE_NAME, key = "'workflow'")
    public String getWorkflowString() throws IOException {
//        String filePath = "classpath:config/workflow.yml";
        ClassPathResource resource = new ClassPathResource("config/workflow.yml");
        List<String> codes = IOUtils.readLines(resource.getInputStream());
        return String.join("\n",codes);
    }

    public Map getCreateUserString() throws IOException {
        ClassPathResource resource = new ClassPathResource("config/create_user.yml");
        List<String> codes = IOUtils.readLines(resource.getInputStream());
        String str = String.join("\n",codes);
        Yaml yaml = new Yaml();
        Object o = yaml.load(str);
        return (Map) o;
    }

    public Object getWorkflowConfig(){
        try {
            String str = getWorkflowString();
            Yaml yaml = new Yaml();
            Object o = yaml.load(str);
            return o;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Cacheable(value = DEMO_CACHE_NAME, key = "'behavior.js'")
    public String getBehaviorString() throws IOException {
        ClassPathResource resource = new ClassPathResource("config/behavior.js");
        List<String> codes = IOUtils.readLines(resource.getInputStream());
        return String.join("\n",codes);
//        return Utils.readFile("classpath:config/behavior.js");
    }

//    public String getBehaviorLibrary(){
//        try {
//            return getBehaviorString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    @Cacheable(value = DEMO_CACHE_NAME, key = "'full_method_permission'")
    public Map<String,Map> getFullMethodPermission(){
        try {
            Yaml yaml = new Yaml();
            String str = Utils.readFile("classpath:config/method_permission.yml");
            Object o = yaml.load(str);
            return (Map<String, Map>) o;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

//    @Cacheable(value = DEMO_CACHE_NAME, fieldName = "'full_menu'")
//    public String getFullMenuString() throws IOException {
//        String filePath = "classpath:config/menu.json";
//        String menu = Utils.readFile(filePath);
//        return menu;
//    }
//
//    public JSONArray getFullMenu(){
//        try {
//            JSONArray arr = JSON.parseArray(getFullMenuString());
//            return arr;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
