package com.beeasy.loadqcc.utils;

import com.beeasy.mscommon.util.OkHttpUtil;
import org.osgl.util.C;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.Map;

public class QccUtil {

    // 应用AppKey
    private static final String AppKey = "0cc43cbebcca42d9b68441c32cc254f2";
    // 密钥
    private static final String SecretKey = "6825D0914B8333B45F2E36D71A10E85F";

    // 获取请求的http header
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
        queries.put("key", AppKey);
        Map header = setHeaderInfo();
        return OkHttpUtil.getForHeader(url, queries, header);
    }
}
