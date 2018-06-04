package com.beeasy.hzback.core.config;

import com.google.common.collect.ImmutableMap;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;
import java.util.Map;

@Configuration
public class ProxyConfig {
    @Bean
    public Servlet baiduProxyServlet(){
        return new ProxyServlet();
    }

    @Bean
    public ServletRegistrationBean proxyServletRegistration(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(baiduProxyServlet(), "/open/filecloud/*");
        Map<String, String> params = ImmutableMap.of(
                "targetUri", "http://192.168.31.55",
                "log", "true");
        registrationBean.setInitParameters(params);
        return registrationBean;
    }
}
