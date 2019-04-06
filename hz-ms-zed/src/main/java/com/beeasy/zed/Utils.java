package com.beeasy.zed;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;

public class Utils {
    public static JSONObject newJsonObject(Object ...objects){
        JSONObject object = new JSONObject();
        for(short i = 0; i < objects.length; i+=2){
            object.put((String) objects[i], objects[i+1]);
        }
        return object;
    }

    public static boolean isJsonNull(JSON json){
        return null != json && !json.getClass().equals(JSONNull.class);
    }
    public static boolean isNotJsonNull(JSON json){
        return !isJsonNull(json);
    }
}
