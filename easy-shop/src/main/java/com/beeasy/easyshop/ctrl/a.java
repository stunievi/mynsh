package com.beeasy.easyshop.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.easyshop.core.Cookie;
import com.beeasy.easyshop.core.MultipartFile;
import org.beetl.sql.core.SQLReady;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.beeasy.easyshop.core.DBService.sqlManager;
public class a {

    public static class d{
        public int c;
    }

    public String test(
        d query,
        int c,
        int[] d
    ) {
        List<JSONObject> list = sqlManager.execute(new SQLReady("select count(1) from ra_member "), JSONObject.class);
        return "source word " + new b().word;
    }

    public String upload(
        JSON body,
//        JSONArray params,
        MultipartFile file
    ){
        try {
            file.transferTo(new File("d:/cubi.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String cookieAdd(
        Cookie cookie
    ){
        cookie.set("cubi","123l");
        return "";
    }

    public String cookieDelete(
        Cookie cookie
    ){
        cookie.delete("cubi");
        return "";
    }
}




