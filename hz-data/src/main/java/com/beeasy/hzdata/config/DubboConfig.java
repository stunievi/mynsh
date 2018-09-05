package com.beeasy.hzdata.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath:provider.xml")
@Configuration
public class DubboConfig {
}
