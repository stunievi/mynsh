package com.beeasy.zed;

//import cn.hutool.json.JSON;
//import cn.hutool.json.JSONNull;
//import cn.hutool.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

public class Utils {
    public static JSONObject newJsonObject(Object ...objects){
        JSONObject object = new JSONObject();
        for(short i = 0; i < objects.length; i+=2){
            object.put((String) objects[i], objects[i+1]);
        }
        return object;
    }

    public static Object jsonGetByPath(JSON json, String path){
        String[] paths = path.split("\\.");
        Object obj = json;
        for (String s : paths) {
            if(obj instanceof JSONObject){
                if(((JSONObject) obj).containsKey(s)){
                    obj = ((JSONObject) obj).get(s);
                } else {
                    return null;
                }
            } else if(obj instanceof JSONArray){
                try{
                    int n = Integer.parseInt(s);
                    if(((JSONArray) obj).size() >= n){
                        return null;
                    }
                    obj = ((JSONArray) obj).get(n);
                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        }

        return obj;

//    public static boolean isJsonNull(JSON json){
//        return null != json && !json.getClass().equals(JSONNull.class);
//    }
//    public static boolean isNotJsonNull(JSON json){
//        return !isJsonNull(json);
    }

    public static void unzipFile(File src, File dest) throws IOException {
        try(
            FileInputStream fis = new FileInputStream(src);
            ZipInputStream zip = new ZipInputStream(fis);
            FileOutputStream fos = new FileOutputStream(dest);
        ) {
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    fos.write(buf, 0, num);
                }
            }
        }
    }

}
