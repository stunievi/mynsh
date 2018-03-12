package com.beeasy.hzback.core.helper;

import com.beeasy.hzback.modules.setting.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {
    public static User getCurrentUser(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }
}
