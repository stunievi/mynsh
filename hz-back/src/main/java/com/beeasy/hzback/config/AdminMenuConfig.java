package com.beeasy.hzback.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties()
@Data
public class AdminMenuConfig {
    private AdminMenuItem[] adminMenu;

    @Data
    public static class AdminMenuItem{
        private String title;
        private String href;
        private AdminMenuItem[] children;
    }
}
