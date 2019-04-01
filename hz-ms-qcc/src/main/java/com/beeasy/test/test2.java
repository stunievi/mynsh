package com.beeasy.test;

import com.alibaba.fastjson.JSON;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test2 {

    public static void main(String[] args) {
        Map data = JSON.parseObject("{\"ShowPic1\":{\"ShowPic3\":[{\"TestParam\":\"test\",\"TestParam2\":[{\"Apple\":\"苹果\"}]}]},\"BeautifulWorld\":\"\",\"WidthHeightLower\":\"\"}");
        System.out.println(formatData(data));

    }

    public static Map formatData(
            Map<String, Object> formatData
    ){
        Map tempData = new HashMap();
        for(Map.Entry<String, Object> entry : formatData.entrySet()){
            String key = entry.getKey();
            Object val = entry.getValue();

            String formatKey = camelToUnderline(key);

            if(val instanceof List){
                tempData.put(formatKey, formatData((List<Map>) val));
            }else if(val instanceof Map){
                tempData.put(formatKey, formatData((Map) val));
            }else {
                tempData.put(formatKey, val);
            }
        }
        return tempData;
    }
    public static List formatData(
            List<Map> formatData
    ){
        ArrayList<Map> dataList = new ArrayList();
        formatData.forEach(item->
                dataList.add(formatData(item)));
        return dataList;
    }

    public static final char UNDERLINE = '_';

    /**
     * 驼峰格式字符串转换为下划线格式字符串
     *
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线格式字符串转换为驼峰格式字符串
     *
     * @param param
     * @return
     */
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线格式字符串转换为驼峰格式字符串2
     *
     * @param param
     * @return
     */
    public static String underlineToCamel2(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile("_").matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }


}
