package com.beeasy.tool;

import okhttp3.*;
//import org.apache.commons.lang3.exception.ExceptionUtils;
import okio.BufferedSink;
import org.osgl.util.C;
import org.osgl.util.IO;
import org.osgl.util.S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OkHttpUtil{
    public static final MediaType MJSON = MediaType.parse("application/json; charset=utf-8");

//    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);
    private static OkHttpClient okHttpClient = null;

    /**
     * get
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String get(String url, Map<String, String> queries) throws IOException {
        return request("get", url,queries, C.newMap(), null);
    }

    /**
     * get
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String request(String method, String url, Map<String, String> queries, Map<String,String> headers, String json) throws IOException {
        String responseBody = "";
        //query
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        //header
        Request.Builder builder = new Request.Builder();
        if(null != headers){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(),entry.getValue());
            }
        }
        //body
        RequestBody requestBody = null;
        if(S.notEmpty(json)){
            requestBody = RequestBody.create(MJSON, json);
        }
        else{

        }
        //type
        switch (method.toLowerCase()){
            case "get":
                break;
            case "post":
                builder.post(requestBody);
                break;
            case "put":
                builder.put(requestBody);
                break;
            case "delete":
                builder.delete();
                break;
        }

        Request request = builder
            .url(sb.toString())
            .build();
        try (
            Response response = okHttpClient().newCall(request).execute();
            ResponseBody body = response.body();
        ){
            if (response.isSuccessful()) {
                responseBody = body.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (S.empty(responseBody)) {
            throw new IOException("网络请求错误");
        }
        return responseBody;
    }


    public static OkHttpClient okHttpClient(){
        if(null == okHttpClient){
            okHttpClient =   new OkHttpClient.Builder()
//                .connectionPool(pool())
                //.sslSocketFactory(sslSocketFactory(), x509TrustManager())
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300,TimeUnit.SECONDS)
                .build();
        }
        return okHttpClient;
    }

    private static ConnectionPool pool() {
        return new ConnectionPool(2000, 5, TimeUnit.MINUTES);
    }
}



