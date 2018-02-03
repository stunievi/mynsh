package com.beeasy.hzback.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@ConfigurationProperties()
@Data
public class AdminMenuConfig {
    private List<AdminMenuItem> adminMenu;


    @Data
    public static class AdminMenuItem implements Cloneable{
        private String title;
        private String href;
        private List<AdminMenuItem> children = new ArrayList<>();

    }


}
