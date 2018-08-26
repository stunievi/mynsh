package com.beeasy.hzdata.utils;

import org.osgl.util.C;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    /***
     * 驼峰命名转为下划线命名
     *
     * @param para
     *        驼峰命名的字符串
     */
    public static String HumpToUnderline(String para){
        StringBuilder sb=new StringBuilder(para);
        int temp=0;//定位
        for(int i=0;i<para.length();i++){
            if(Character.isUpperCase(para.charAt(i))){
                sb.insert(i+temp, "_");
                temp+=1;
            }
        }
        return sb.toString().toUpperCase();
    }

    public static Map<String,Object> underLineMap(Map<String,Object> map){
        Object[] objects = new Object[map.size() * 2];
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            objects[i * 2] = HumpToUnderline(entry.getKey());
            objects[i * 2 + 1] = entry.getValue();
            i++;
        }
        return C.newMap(objects);
    }

    public static List<Map<String,Object>> underLineList(List<Map> list){
        return list.stream().map(m -> underLineMap((Map<String,Object>)m)).collect(Collectors.toList());
    }
}
