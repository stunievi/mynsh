package com.beeasy.loadqcc.utils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonConvertUtils {

    //标准驼峰和不标准驼峰转下划线
    public static String convert1(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        return convert1(jsonObject).toJSONString();
    }
    private static JSONObject convert1(JSONObject input) {
        JSONObject result = new JSONObject();
        Set<Entry<String, Object>> entrySet = input.entrySet();
        for (Iterator<Entry<String, Object>> it = entrySet.iterator(); it.hasNext();) {
            Entry<String, Object> entry = it.next();
            Object obj = entry.getValue();
            String key = entry.getKey();
            String[] ss = key.split("(?<!^)(?=[A-Z])");
            int len = ss==null?0:ss.length;
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<len;i++) {
                sb.append(toLowerCaseFirstOne(ss[i]));
                if(i<len-1) {
                    sb.append("_");
                }
            }
            if(obj instanceof JSONObject) {
                result.put(sb.toString(), convert1((JSONObject)obj));
            }else if(obj instanceof JSONArray) {
                result.put(sb.toString(), convert1((JSONArray)obj));
            }else {
                result.put(sb.toString(), entry.getValue());
            }


        }
        return result;
    }
    private static JSONArray convert1(JSONArray input) {
        JSONArray result = new JSONArray();
        for(int i=0;i<input.size();i++) {
            result.add(convert1((JSONObject)input.get(i)));
        }
        return result;
    }


    //不标准驼峰转标准驼峰
    public static String convert2(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        return convert2(jsonObject).toJSONString();
    }
    private static JSONObject convert2(JSONObject input) {
        JSONObject result = new JSONObject();
        Set<Entry<String, Object>> entrySet = input.entrySet();
        for (Iterator<Entry<String, Object>> it = entrySet.iterator(); it.hasNext();) {
            Entry<String, Object> entry = it.next();
            Object obj = entry.getValue();
            String key = entry.getKey();
            StringBuffer sb=new StringBuffer();
            sb.append(toLowerCaseFirstOne(key));

            if(obj instanceof JSONObject) {
                result.put(sb.toString(), convert2((JSONObject)obj));
            }else if(obj instanceof JSONArray) {
                result.put(sb.toString(), convert2((JSONArray)obj));
            }else {
                result.put(sb.toString(), entry.getValue());
            }
        }
        return result;
    }
    private static JSONArray convert2(JSONArray input) {
        JSONArray result = new JSONArray();
        for(int i=0;i<input.size();i++) {
            result.add(convert2((JSONObject)input.get(i)));
        }
        return result;
    }
    //标准驼峰和不标准驼峰转下划线和不标准驼峰转标准驼峰要使用转换小写的方法
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    //标准驼峰转不标准驼峰
    public static String convert3(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        return convert3(jsonObject).toJSONString();
    }
    private static JSONObject convert3(JSONObject input) {
        JSONObject result = new JSONObject();
        Set<Entry<String, Object>> entrySet = input.entrySet();
        for (Iterator<Entry<String, Object>> it = entrySet.iterator(); it.hasNext();) {
            Entry<String, Object> entry = it.next();
            Object obj = entry.getValue();
            String key = entry.getKey();
//			String[] ss = key.split("(?<!^)(?=[A-Z])");
//			int len = ss==null?0:ss.length;
            StringBuffer sb=new StringBuffer();
            sb.append(toUpperCaseFirstOne(key));
//			for(int i=0;i<len;i++) {
//				sb.append(toLowerCaseFirstOne(ss[i]));
//				if(i<len-1) {
//					sb.append("_");
//				}
//			}

//			System.out.println("obj class:"+obj.getClass().getSimpleName());
            if(obj instanceof JSONObject) {
                result.put(sb.toString(), convert3((JSONObject)obj));
            }else if(obj instanceof JSONArray) {
                result.put(sb.toString(), convert3((JSONArray)obj));
            }else {
                result.put(sb.toString(), entry.getValue());
            }


        }
        return result;
    }
    private static JSONArray convert3(JSONArray input) {
        JSONArray result = new JSONArray();
        for(int i=0;i<input.size();i++) {
            result.add(convert3((JSONObject)input.get(i)));
        }
        return result;
    }
    //标准驼峰转不标准驼峰要使用转换大写的方法
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    //下滑线转标准驼峰
    public final static void convert(Object json) {
        if (json instanceof JSONArray) {
            JSONArray arr = (JSONArray) json;
            for (Object obj : arr) {
                convert(obj);
            }
        } else if (json instanceof JSONObject) {
            JSONObject jo = (JSONObject) json;
            Set<String> keys = jo.keySet();
            String[] array = keys.toArray(new String[keys.size()]);
            for (String key : array) {
                Object value = jo.get(key);
                String[] key_strs = key.split("_");
                if (key_strs.length > 1) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < key_strs.length; i++) {
                        String ks = key_strs[i];
                        if (!"".equals(ks)) {
                            if (i == 0) {
                                sb.append(ks);
                            } else {
                                int c = ks.charAt(0);
                                if (c >= 97 && c <= 122) {
                                    int v = c - 32;
                                    sb.append((char) v);
                                    if (ks.length() > 1) {
                                        sb.append(ks.substring(1));
                                    }
                                } else {
                                    sb.append(ks);
                                }
                            }
                        }
                    }
                    jo.remove(key);
                    jo.put(sb.toString(), value);
                }
                convert(value);
            }
        }
    }
    public final static Object convert(String json) {
        Object obj = JSON.parse(json);
        convert(obj);
        return obj;
    }


    //下滑线转不标准驼峰
    public final static void convert4(Object json) {
        if (json instanceof JSONArray) {
            JSONArray arr = (JSONArray) json;
            for (Object obj : arr) {
                convert4(obj);
            }
        } else if (json instanceof JSONObject) {
            JSONObject jo = (JSONObject) json;
            Set<String> keys = jo.keySet();
            String[] array = keys.toArray(new String[keys.size()]);
            for (String key : array) {
                Object value = jo.get(key);
                String[] key_strs = key.split("_");
                if (key_strs.length > 1) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < key_strs.length; i++) {
                        String ks = key_strs[i];
                        if (!"".equals(ks)) {
                            if (i == 0) {
                                sb.append(toUpperCaseFirstOne(ks));
                            } else {
                                int c = ks.charAt(0);
                                if (c >= 97 && c <= 122) {
                                    int v = c - 32;
                                    sb.append((char) v);
                                    if (ks.length() > 1) {
                                        sb.append(ks.substring(1));
                                    }
                                } else {
                                    sb.append(ks);
                                }
                            }
                        }
                    }
                    jo.remove(key);
                    jo.put(sb.toString(), value);
                }
                convert4(value);
            }
        }
    }
    public final static Object convert4(String json) {
        Object obj = JSON.parse(json);
        convert4(obj);
        return obj;
    }
}