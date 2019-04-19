package com.beeasy.zed;

//import cn.hutool.json.JSON;
//import cn.hutool.json.JSONNull;
//import cn.hutool.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
}
