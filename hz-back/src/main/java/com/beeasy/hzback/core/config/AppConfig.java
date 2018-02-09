package com.beeasy.hzback.core.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.stereotype.Component;
import javax.servlet.Filter;

@Configuration
@Component
public class AppConfig {
   public static String TEXT_FILED_TYPE = "long varchar";
}