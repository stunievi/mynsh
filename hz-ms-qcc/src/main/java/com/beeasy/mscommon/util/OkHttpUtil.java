package com.beeasy.mscommon.util;

import com.beeasy.mscommon.RestException;
import okhttp3.*;
//import org.apache.commons.lang3.exception.ExceptionUtils;
import okio.BufferedSink;
import org.osgl.util.IO;
import org.osgl.util.S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OkHttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);
    private static OkHttpClient okHttpClient = null;

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String get(String url, Map<String, String> queries) {
        String responseBody = "";
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
        Request request = new Request.Builder()
                .url(sb.toString())
                .build();
        try (
                Response response = okHttpClient().newCall(request).execute();
                ResponseBody body = response.body();
        ) {
            if (response.isSuccessful()) {
                responseBody = body.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    /**
     * post
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        String responseBody = "";
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        try (
                Response response = okHttpClient().newCall(request).execute();
                ResponseBody body = response.body();
        ) {
            if (response.isSuccessful()) {
                responseBody = body.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
//            logger.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        }
        return responseBody;
    }

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String getForHeader(String url, Map<String, String> queries, Map<String, String> headers) {
        String responseBody = "";
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
        Request.Builder builder = new Request.Builder();
        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder
//            new Request.Builder()
//            .addHeader("key", "value")
                .url(sb.toString())
                .build();
        try (
                Response response = okHttpClient().newCall(request).execute();
                ResponseBody body = response.body();
        ) {
            if (response.isSuccessful()) {
                responseBody = body.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (S.empty(responseBody)) {
            throw new RestException("网络请求错误");
        }
        return responseBody;
    }


    public static String getForHeader2(String url, Map<String, String> queries, Map<String, String> headers) {
        String responseBody = "";
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
        Request.Builder builder = new Request.Builder();
        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder
//            new Request.Builder()
//            .addHeader("key", "value")
                .url(sb.toString())
                .build();
        try (
                Response response = okHttpClient().newCall(request).execute();
                ResponseBody body = response.body();
        ) {
            if (response.isSuccessful()) {
                responseBody= response.headers().toMultimap().get("set-cookie").stream()
                        .map(s -> s.replace("; Path=/",""))
                        //.map(s -> s.replace(";\\s*Path=/",""))
                        .sorted(new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                if(o1.contains("JSESSIONID")) return -1;
                                else if(o2.contains("linkapp")) return 1;
                                return 0;
                            }
                        })
                        .collect(Collectors.joining("; "));
                //responseBody = body.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (S.empty(responseBody)) {
            throw new RestException("网络请求错误");
        }
        return responseBody;
    }

    public static String postFile(String url, Map<String, Object> params, Map<String, String> headers) {
        MultipartBody.Builder mBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof File) {
                RequestBody fileBody = RequestBody.create(MediaType.parse(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE), (File) entry.getValue());
                mBuilder.addFormDataPart(entry.getKey(), ((File) entry.getValue()).getName(), fileBody);
            } else if (entry.getValue() instanceof FakeFile) {
                FakeFile fakeFile = (FakeFile) entry.getValue();
                RequestBody fileBody = new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE);
                    }

                    @Override
                    public void writeTo(BufferedSink bufferedSink) throws IOException {
                        int bufferSize = 20480;
                        byte[] buffer = new byte[bufferSize];
                        int len = -1;
                        while ((len = fakeFile.getInputStream().read(buffer)) > -1) {
                            bufferedSink.write(buffer, 0, len);
                        }
                    }
                };
//                RequestBody fileBody = RequestBody.create(MediaType.parse(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE), fakeFile.());
                mBuilder.addFormDataPart(entry.getKey(), ((FakeFile) entry.getValue()).getFileName(), fileBody);
            } else {
                mBuilder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        Request.Builder builder = new Request.Builder();
        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder
                .url(url)
                .post(mBuilder.build())
                .build();
        String responseBody = null;
        OkHttpClient okHttpClient = okHttpClient();
        try (
                Response response = okHttpClient.newCall(request).execute();
                ResponseBody body = response.body();
        ) {
            if (response.isSuccessful()) {
                responseBody = body.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (S.empty(responseBody)) {
            throw new RestException("网络请求错误");
        }
        return responseBody;
    }


    public static byte[] download(final String url, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder();
        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        byte[] bytes = null;
        try (
                Response response = client.newCall(request).execute();
                ResponseBody body = response.body();
                InputStream is = body.byteStream();
        ) {
            bytes = IO.readContent(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null == bytes) {
            bytes = new byte[0];
        }
        return bytes;
    }


    public static OkHttpClient okHttpClient() {
        if (null == okHttpClient) {
            okHttpClient = new OkHttpClient.Builder()
//                .connectionPool(pool())
                    //.sslSocketFactory(sslSocketFactory(), x509TrustManager())
                    .retryOnConnectionFailure(true)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }

    private static ConnectionPool pool() {
        return new ConnectionPool(2000, 5, TimeUnit.MINUTES);
    }


    public static class FakeFile {
        String fileName;
        InputStream inputStream;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public FakeFile(String fileName, InputStream inputStream) {
            this.fileName = fileName;
            this.inputStream = inputStream;
        }

        //        byte[] bytes;
    }
}

