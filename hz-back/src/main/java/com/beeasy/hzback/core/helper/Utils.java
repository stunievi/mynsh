package com.beeasy.hzback.core.helper;

//import bin.leblanc.faker.Faker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.mscommon.entity.BeetlPager;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.apache.commons.io.FileUtils;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

@Deprecated
@Component
public class Utils {

    @Autowired
    UserService userService;


    public static PageQuery convertPageable(Pageable pageable){
        PageQuery pageQuery = new PageQuery(pageable.getPageNumber() + 1, pageable.getPageSize());
        return pageQuery;
    }

    public static PageQuery convertPageable(Pageable pageable, Object params) {
        PageQuery pageQuery = convertPageable(pageable);
        pageQuery.setParas(params);
        return pageQuery;
    }

    public static PageQuery convertPageable(BeetlPager beetlPager, Object params){
        PageQuery pageQuery = new PageQuery(beetlPager.getPage(), beetlPager.getSize());
        return pageQuery;
    }

    public static Page convertPage(PageQuery pageQuery, Pageable pageable){
        return new PageImpl(pageQuery.getList(), pageable, pageQuery.getTotalRow());
    }

    public static <T> Page<T> beetlPageQuery(String sqlId, Class<T> clz, Object params, Pageable pageable){
        SQLManager sqlManager = U.getSQLManager();
        PageQuery pageQuery = convertPageable(pageable, params);
        sqlManager.pageQuery(sqlId, clz, pageQuery);
        return Utils.convertPage(pageQuery, pageable);
    }

    public static <T> PageQuery<T> beetlPageQuery(String sqlId, Class<T> clz, Object params, BeetlPager beetlPager){
        SQLManager sqlManager = U.getSQLManager();
        PageQuery pageQuery = new PageQuery(beetlPager.getPage(), beetlPager.getSize());
        pageQuery.setParas(params);
        sqlManager.pageQuery(sqlId, clz, pageQuery);
        return pageQuery;
    }

    public static Long toLong(Object object){
        return Long.valueOf(String.valueOf(object));
    }

    public static String readFile(String filePath) throws IOException {
        File f;
        if (filePath.startsWith("classpath")) {
            f = ResourceUtils.getFile(filePath);
        } else {
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

    public static Optional<String> getCurrentUserPrivateKey() {
        //todo
//        IUserDao userDao = SpringContextUtils.getBean(IUserDao.class);
//        List list = userDao.getPrivateKey(Utils.getCurrentUserId());
//        if (list.size() > 0) {
//            return Optional.of(String.valueOf(list.get(0)));
//        }
//        return Optional.empty();
        return null;
    }

    public static Optional<String> getCurrentUserPublicKey() {
        //todo
//        IUserDao userDao = SpringContextUtils.getBean(IUserDao.class);
//        List list = userDao.getPublicKey(Utils.getCurrentUserId());
//        if (list.size() > 0) {
//            return Optional.of(String.valueOf(list.get(0)));
//        }
//        return Optional.empty();
        return null;
    }

    public static String getExt(String fileName) {
        Pattern p = Pattern.compile("([^\\.]+)$");
        Matcher m = p.matcher(fileName);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }


    public static Long[] convertIds(String ids) {
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

    public static <T> List<T> convertToList(String str, Class<T> clz) {
        Object object = JSON.toJSON(str.trim().split(","));
        if (object instanceof JSONArray) {
            return ((JSONArray) object).toJavaList(clz);
        }
        return new ArrayList<>();
    }

    public static List<String> convertLongToString(Collection<Long> list) {
        return list.stream().map(item -> item + "").collect(Collectors.toList());
    }

//    public static User getCurrentUser() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return user;
//    }

    public static Long getCurrentUserId() {
        return AuthFilter.getUid();
//        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
//        return (Long) request.getSession().getAttribute(AuthFilter.Uid);
//        RequestContextHolder
//        try {
//
////            return (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        } catch (Exception e) {
//            return 0;
//        }
    }

    public static List<String> splitByComma(String str) {
        return Arrays.asList((str).split(","))
                .stream()
                .map(item -> item.trim())
                .collect(Collectors.toList());
    }



    @Autowired
    RedisTemplate redisTemplate;

    public boolean lock(String key, int exprTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            result = operations.setIfAbsent(key, 1);
            //十秒超时
            redisTemplate.expire(key, exprTime, TimeUnit.SECONDS);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isLocking(String key) {
        return redisTemplate.opsForValue().get(key) != null;
    }

    public void unlock(String key) {
        redisTemplate.delete(key);
    }

    public boolean isLockingOrLockFailed(String key, int exprTime) {
        if (isLocking(key)) return false;
        return !lock(key, exprTime);
    }





}
