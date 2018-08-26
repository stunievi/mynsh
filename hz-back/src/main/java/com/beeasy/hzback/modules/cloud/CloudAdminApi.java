package com.beeasy.hzback.modules.cloud;

import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.modules.cloud.response.CloudBaseResponse;
import com.beeasy.hzback.modules.cloud.response.LoginResponse;
import feign.*;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FeignClient(name = "cloud-admin-service", url = "${filecloud.address}", configuration = CloudAdminApi.Config.class)
public interface CloudAdminApi {

//    @RequestMapping(value = "/system/userAdd.action", method = RequestMethod.POST,
//            produces = MediaType.TEXT_HTML_VALUE,
//            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    String adminCreateUser(@RequestParam("") Map map);

    @RequestMapping(value = "/checkOnline.action", method = RequestMethod.GET)
    CloudBaseResponse checkOnline();

    @RequestMapping(method = RequestMethod.GET, value = "/apiLogin.action")
    LoginResponse login(@RequestParam("username") String username, @RequestParam("password") String password);

    @RequestMapping(value = "/ajax/ajaxAddUser.action?roleId=3&departmentId=1&lastName=t&firstName=t", method = RequestMethod.GET)
    CloudBaseResponse createUser(@RequestParam("username") String username);

    class Config {
        public static String cookie = "";

        public synchronized static String getCookie() {
            return cookie;
        }

        public synchronized static void setCookie(String cookie) {
            Config.cookie = cookie;
        }

        @Bean
        RequestInterceptor getRI() {
            return new RequestInterceptor() {
                @Override
                public void apply(RequestTemplate requestTemplate) {
                    synchronized (cookie) {
                        if (!StringUtils.isEmpty(cookie)) {
                            requestTemplate.header("cookie", cookie);
                        }
                    }
                }
            };
        }

        @Bean
        public Decoder getDecoder() {
            return new Decoder() {
                @Override
                public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
                    if (response.status() == 404) return Util.emptyValueOf(type);
                    if (response.body() == null) return null;
                    Reader reader = response.body().asReader();
                    if (!reader.markSupported()) {
                        reader = new BufferedReader(reader, 1);
                    }
                    char[] bytes = new char[1024];
                    int len = -1;
                    StringBuffer sb = new StringBuffer();
                    while ((len = reader.read(bytes)) > -1) {
                        sb.append(bytes, 0, len);
                    }
                    String str = sb.toString();
                    Object obj = JSON.parseObject(str, type);
                    if (obj instanceof CloudBaseResponse) {
                        List<String> cookies = (List<String>) response.headers().getOrDefault("set-cookie", new ArrayList<>());
                        ((CloudBaseResponse) obj).setResponseCookies(cookies);
                    }
                    return obj;
                }
            };
        }
    }
}
