package com.beeasy.hzback.core.helper;

import bin.leblanc.faker.Faker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.form.UserAdd;
import com.beeasy.hzback.modules.system.service.UserService;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class Utils {

    @Autowired
    UserService userService;
    @Autowired
    IUserDao userDao;

    public User createFaker(){
        UserAdd userAdd = new UserAdd();
        userAdd.setPhone(Faker.getPhone());
        userAdd.setTrueName(Faker.getTrueName());
        userAdd.setUsername(Faker.getName());
        userAdd.setPassword("2");
        userAdd.setBaned(false);
        Result<User> r = userService.createUser(userAdd);
        return r.orElse(null);
    }

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

    public static Optional<String> getCurrentUserPrivateKey(){
        IUserDao userDao = SpringContextUtils.getBean(IUserDao.class);
        List list = userDao.getPrivateKey(Utils.getCurrentUserId());
        if(list.size() > 0){
            return Optional.of(String.valueOf(list.get(0)));
        }
        return Optional.empty();
    }
    public static Optional<String> getCurrentUserPublicKey(){
        IUserDao userDao = SpringContextUtils.getBean(IUserDao.class);
        List list = userDao.getPublicKey(Utils.getCurrentUserId());
        if(list.size() > 0){
            return Optional.of(String.valueOf(list.get(0)));
        }
        return Optional.empty();
    }

    public static String getExt(String fileName){
        Pattern p = Pattern.compile("([^\\.]+)$");
        Matcher m = p.matcher(fileName);
        if(m.find()){
            return m.group(1);
        }
        return "";
    }


    public static Long[] convertIds(String ids){
        List<Long> list = convertIdsToList(ids);
        Long[] arr = new Long[list.size()];
        return list.toArray(arr);
    }

    public static List<Long> convertIdsToList(String ids) {
        List<Long> list = new ArrayList<>();
        for (String s : ids.split(",")) {
            try {
                list.add(Long.valueOf(s.trim()));
            } catch (Exception e) {

            }
        }
        return list;
    }

    public static <T> List<T> convertToList(String str, Class<T> clz){
        Object object = JSON.toJSON(str.trim().split(","));
        if(object instanceof JSONArray){
            return ((JSONArray) object).toJavaList(clz);
        }
        return new ArrayList<>();
    }

        public static List<String> convertLongToString(Collection<Long> list){
        return list.stream().map(item -> item + "").collect(Collectors.toList());
    }

//    public static User getCurrentUser() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return user;
//    }

    public static long getCurrentUserId(){
        try{
            return (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        catch (Exception e){
            return 0;
        }
    }

    public static List<String> splitByComma(String str) {
        return Arrays.asList((str).split(","))
                .stream()
                .map(item -> item.trim())
                .collect(Collectors.toList());
    }

    public static <T>  Result validate(T t){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(t);
        if(violations.size() > 0){
            List<String> arr = new ArrayList<>();
            violations.forEach(tConstraintViolation -> {
                arr.add(tConstraintViolation.getMessage());
            });
            return Result.error(String.join(",",arr));
        }
        return Result.ok();
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
