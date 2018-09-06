package com.beeasy.mscommon;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.PostConstruct;

@ImportResource(value = {"classpath:beetlsql.xml"})
@Configuration
public class StarterAutoConfigure {
}
