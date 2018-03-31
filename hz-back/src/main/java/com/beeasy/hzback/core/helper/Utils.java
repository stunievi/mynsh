package com.beeasy.hzback.core.helper;

import com.beeasy.hzback.modules.setting.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static User getCurrentUser(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }

    public static List<String> splitByComma(String str){
        return Arrays.asList((str).split(","))
                .stream()
                .map(item -> item.trim())
                .collect(Collectors.toList());
    }
}
