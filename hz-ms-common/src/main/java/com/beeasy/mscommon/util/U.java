package com.beeasy.mscommon.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.entity.BeetlPager;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.ext.SnowflakeIDWorker;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class U {
    private static SQLManager sqlManager;
    private static Validator validator;
    private static SnowflakeIDWorker snowflakeIDWorker;

    public static String sGet(Map map, String key){
        Object object = map.get(key);
        if($.isNull(object) || !(object instanceof String) || S.empty((String)object)){
            return "";
        }
        return (String) object;
    }

    public static int sGet(JSONObject object, String key, int dft){
        Integer integer = object.getInteger(key);
        return null == integer ? dft : integer;
    }

    public static String sGet(JSONObject object, String key, String dft){
        String str = object.getString(key);
        return S.empty(str) ? dft : str;
    }
    public static String sGet(JSONObject object, String key){
        String str = object.getString(key);
        return S.empty(str) ? "" : str;
    }


    public static String stGet(Map map, String key){
        return sGet(map,key).trim();
    }


    public static SQLManager getSQLManager(){
        if(null == sqlManager){
            sqlManager = getContext().getBean(SQLManager.class);
        }
        return sqlManager;
    }

    public static Validator getValidator(){
        if(null == validator){
            validator = getBean(Validator.class);
        }
        return validator;
    }

    public static SnowflakeIDWorker getSnowflakeIDWorker(){
        if(null == snowflakeIDWorker){
            snowflakeIDWorker = getBean(SnowflakeIDWorker.class);
        }
        return snowflakeIDWorker;
    }

    public static HttpServletRequest getRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }


    public static ApplicationContext getContext(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
        return ctx;
    }

    public static <T> T getBean(Class<T> clz){
        return getContext().getBean(clz);
    }

    public static <T> PageQuery<T> beetlPageQuery(String sqlId, Class<T> clz, Object params, BeetlPager beetlPager){
        SQLManager sqlManager = getContext().getBean(SQLManager.class);
        PageQuery pageQuery = new PageQuery(beetlPager.getPage(), beetlPager.getSize());
        pageQuery.setParas(params);
        sqlManager.pageQuery(sqlId, clz, pageQuery);
        return pageQuery;
    }

    public static <T> PageQuery<T> beetlPageQuery(String sqlId, Class<T> clz, Object params){
        BeetlPager beetlPager = $.map(params).to(BeetlPager.class);
        return beetlPageQuery(sqlId,clz,params,beetlPager);
    }

    /**
     *
     * @param sqlId
     * @param params
     * @return
     */
    public static boolean assertFromSql(String sqlId, Map<String,Object> params){
        JSONObject object = getSQLManager().selectSingle(sqlId, params, JSONObject.class);
        return null != object && object.getInteger("1") > 0;
    }

    public static <T> T parseFormJSON(String json, Class<T> clz){
        String rjson = (String) JSON.parse(json);
        return JSON.parseObject(rjson, clz);
    }


    public static <T> List<T> toList(String ids, Class<T> clz){
        List list = C.newList();
        for (String s : ids.split(",")) {
            try{
                if(clz.equals(Long.class)){
                    list.add(Long.parseLong(s.trim()));
                }
                else if(clz.equals(String.class)){
                    list.add((T) s);
                }
            }
            catch (Exception e){
            }
        }
        return list;
    }

    /**
     * 将逗号拼接的参数转换为对应的list
     *
     * @param ids
     * @return
     */
    public static List<Long> toIdList(String ids) {
        return toList(ids,Long.class);
    }


    /**
     * 驼峰转下划线
     * @param param
     * @return
     */
    public static String camelToUnderline(String param){
        if (param==null||"".equals(param.trim())){
            return "";
        }
        int len=param.length();
        StringBuilder sb=new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c=param.charAt(i);
            if (Character.isUpperCase(c)){
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }


}
