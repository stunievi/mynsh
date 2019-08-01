package com.beeasy.hzreport.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LiUtils {
    public static long getDistanceDays(String date) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        long days = 0;
        try {
            Date time = df.parse(date);//String转Date
            Date now = new Date();//获取当前时间
            long time1 = time.getTime();
            long time2 = now.getTime();
            long diff = time1 - time2;
            days = diff / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;//正数表示在当前时间之后，负数表示在当前时间之前
    }
}
