package com.beeasy.loadqcc.utils;

import com.alibaba.fastjson.JSON;
import com.beeasy.mscommon.util.OkHttpUtil;
import com.google.common.base.Joiner;
import org.osgl.util.C;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.Map;

public class QccUtil {

    // 应用AppKey
    private static final String AppKey = "797d933687f441ad9098dda4f3eabba2";
    // 密钥
    private static final String SecretKey = "ED0F558DF66A184E26504B1DD9DAD1A3";

    // 请求的http header
    private static Map setHeaderInfo(){
        String Timespan = String.valueOf(new Date().getTime()/1000);
        String str = AppKey.concat(Timespan).concat(SecretKey);
        String token = DigestUtils.md5DigestAsHex(str.getBytes()).toUpperCase();
        Map header = C.newMap(
                "Token",token
                ,"Timespan", Timespan
        );
        return header;
    }

    // get请求数据
    public static String getData(String url, Map queries){
        // 补充key，企查查请求格式
        queries.put("key", AppKey);
        Map header = setHeaderInfo();
        try{
            return OkHttpUtil.getForHeader(url, queries, header);
        }catch (Exception e){
            String dataQueries = Joiner.on("&").withKeyValueSeparator("=").join(queries);
            String fullLink = url + "?" + dataQueries;
            return JSON.toJSONString(C.newMap(
               "Status", "500",
                    "Message", "获取信息时发生服务器发生异常",
                    "Result", fullLink
            ));
        }
    }
}
