package com.beeasy.hzback.core.helper;

import com.beeasy.hzback.modules.setting.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static String readFile(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        File f;
        if(filePath.startsWith("classpath")){
            f = ResourceUtils.getFile(filePath);
        }
        else{
            f = new File(filePath);
        }
        Long fileLength = f.length();
        byte[] filecontent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(f);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(filecontent,"utf8");
    }
    public static Reader getReader(String filePath) throws FileNotFoundException {
        if (filePath.startsWith("classpath")) {
            return new FileReader(ResourceUtils.getFile(filePath));
        } else {
            return new FileReader(filePath);
        }
    }

    public static User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }

    public static List<String> splitByComma(String str) {
        return Arrays.asList((str).split(","))
                .stream()
                .map(item -> item.trim())
                .collect(Collectors.toList());
    }
}
