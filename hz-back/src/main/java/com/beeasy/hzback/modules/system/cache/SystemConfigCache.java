package com.beeasy.hzback.modules.system.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@CacheConfig(cacheNames = "system_config")
@Component
public class SystemConfigCache {


    @Value("${spring.datasource.driver-class-name}")
    String dbDriver;

    public static final String DEMO_CACHE_NAME = "system_config";

//    @Cacheable(value = DEMO_CACHE_NAME, key = "'workflow'")
    public String getWorkflowString() throws IOException {
//        String filePath = "classpath:config/workflow.yml";
        ClassPathResource resource = new ClassPathResource("config/workflow.yml");
        List<String> codes = IOUtils.readLines(resource.getInputStream());
        return String.join("\n", codes);
    }

    public Map getCreateUserString() throws IOException {
        ClassPathResource resource = new ClassPathResource("config/create_user.yml");
        List<String> codes = IOUtils.readLines(resource.getInputStream());
        String str = String.join("\n", codes);
        Yaml yaml = new Yaml();
        Object o = yaml.load(str);
        return (Map) o;
    }

    public Object getWorkflowConfig() {
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

    public JSONObject getMenus() {
        try {
            String str = getMenuString();
            return JSON.parseObject(str);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Cacheable(value = DEMO_CACHE_NAME, key = "'behavior.js'")
    public String getBehaviorString() throws IOException {
        ClassPathResource resource = new ClassPathResource("config/behavior.js");
        List<String> codes = IOUtils.readLines(resource.getInputStream());
        return String.join("\n", codes);
//        return Utils.readFile("classpath:config/behavior.js");
    }

    @Cacheable(value = DEMO_CACHE_NAME, key = "'menu.json'")
    public String getMenuString() throws IOException {
        ClassPathResource resource = new ClassPathResource("config/menu.json");
        List<String> codes = IOUtils.readLines(resource.getInputStream());
        return String.join("\n", codes);
    }

    @Cacheable(value = DEMO_CACHE_NAME, key = "'cross.html'")
    public String getCorssHtml() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/cross.html");
        List<String> codes = IOUtils.readLines(resource.getInputStream());
        return String.join("\n", codes);
    }
//    public String getBehaviorLibrary(){
//        try {
//            return getBehaviorString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

//    @Cacheable(value = DEMO_CACHE_NAME, key = "'full_method_permission'")
//    public Map<String, Map> getFullMethodPermission() {
//        try {
//            Yaml yaml = new Yaml();
//            String str = Utils.readFile("classpath:config/method_permission.yml");
//            Object o = yaml.load(str);
//            return (Map<String, Map>) o;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new HashMap<>();
//    }


//    public List<String> getSqlViews() {
//        String prefix = "";
//        switch (dbDriver) {
//            case "com.mysql.jdbc.Driver":
//                prefix = "mysql";
//                break;
//
//            case "com.ibm.db2.jcc.DB2Driver":
//                prefix = "db2";
//                break;
//        }
//        String[] files = {"t_global_permission_center", "t_workflow_dealer"};
//        String finalPrefix = prefix;
//        return (List<String>) Arrays.stream(files)
//                .map(file -> {
//                    ClassPathResource resource = new ClassPathResource(String.format("sql_views/%s/%s.sql", finalPrefix, file));
//                    try {
//                        List<String> codes = IOUtils.readLines(resource.getInputStream());
//                        return String.join(" ", codes);
//                    } catch (IOException e) {
////                        e.printStackTrace();
//                        return "";
//                    }
//                })
//                .map(item -> (String) item)
//                .filter(StringUtils::isNotEmpty)
//                .collect(Collectors.toList());
//    }

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
