package com.beeasy.hzback.core.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
