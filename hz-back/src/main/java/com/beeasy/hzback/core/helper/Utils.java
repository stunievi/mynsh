package com.beeasy.hzback.core.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.system.entity.User;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class Utils {

    public static String readFile(String filePath) throws IOException {
        File f;
        if(filePath.startsWith("classpath")){
            f = ResourceUtils.getFile(filePath);
        }
        else{
            f = new File(filePath);
        }
        return FileUtils.readFileToString(f);
//        FileUtils.
//        Long fileLength = f.length();
//        byte[] filecontent = new byte[fileLength.intValue()];
//        try {
//            FileInputStream in = new FileInputStream(f);
//            in.read(filecontent);
//            in.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return new String(filecontent,"utf8");
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

    public static long getCurrentUserId(){
        return getCurrentUser().getId();
    }

    public static List<String> splitByComma(String str) {
        return Arrays.asList((str).split(","))
                .stream()
                .map(item -> item.trim())
                .collect(Collectors.toList());
    }

    public static <T>  void validate(T t) throws RestException{
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(t);
        if(violations.size() > 0){
            JSONArray arr = new JSONArray();
            violations.forEach(tConstraintViolation -> {
                arr.add(tConstraintViolation.getMessage());
            });
            throw new RestException(JSON.toJSONString(arr));
        }
    }



    @Autowired
    RedisTemplate redisTemplate;

    public boolean lock(String key, int exprTime){
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            result = operations.setIfAbsent(key, 1);
            //十秒超时
            redisTemplate.expire(key,exprTime, TimeUnit.SECONDS);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public boolean isLocking(String key){
        return redisTemplate.opsForValue().get(key) != null;
    }
    public void unlock(String key){
        redisTemplate.delete(key);
    }
    public boolean isLockingOrLockFailed(String key, int exprTime){
        if(isLocking(key)) return false;
        return !lock(key,exprTime);
    }


}
