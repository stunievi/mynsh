package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Resources;
import org.beetl.sql.core.SQLReady;
import org.osgl.util.Charsets;
import org.osgl.util.E;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import static com.beeasy.zed.DBService.sqlManager;

public class Version {

    public double versionName;
    public String description;
    public static Version version;

    static {
        String content = "";
        try {
            ClassPathResource resource = new ClassPathResource("version.json");
            content = IoUtil.read(resource.getStream(), CharsetUtil.UTF_8);
        } catch (Exception e) {
         e.printStackTrace();
        }
        version = JSON.parseObject(content, Version.class);
    }

    public static String convertProvince(String code){
        try{
            return App.concurrentMapWordCounts.getOrDefault(code, "");
        } catch (Exception e){
            return "";
        }
    }

    public static void update(String tabName) {
        //解决sql问题
        String sql = "select inner_company_name,province from  "+ tabName;
        List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
        for (int i = 0; i < list.size(); i++) {
            if (null != list.get(i).getString("province") && null != App.concurrentMapWordCounts.get(list.get(i).getString("province")) && !App.concurrentMapWordCounts.get(list.get(i).getString("province")).equals("")) {
                String updateSql = "update " + tabName + " set province = '" + App.concurrentMapWordCounts.get(list.get(i).getString("province")) + "'  where  inner_company_name = '" + list.get(i).getString("innerCompanyName") + "'";
                sqlManager.executeUpdate(new SQLReady(updateSql));
            }
        }
    }

//    public static void updateFayuan() {
//        //解决sql问题
//        String sql = "select name,province from  QCC_DETAILS";
//        List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
//        for (int i = 0; i < list.size(); i++) {
//            if (null != list.get(i).getString("province") && null != App.concurrentMapWordCounts.get(list.get(i).getString("province")) && !App.concurrentMapWordCounts.get(list.get(i).getString("province")).equals("")) {
//                String updateSql = "update QCC_DETAILS set province = '" + App.concurrentMapWordCounts.get(list.get(i).getString("province")) + "'  where  name = '" + list.get(i).getString("name") + "'";
//                sqlManager.executeUpdate(new SQLReady(updateSql));
//            }
//        }
//    }

}
