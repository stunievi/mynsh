package com.beeasy.hzback.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
