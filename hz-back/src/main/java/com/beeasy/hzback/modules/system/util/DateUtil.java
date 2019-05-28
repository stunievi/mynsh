package com.beeasy.hzback.modules.system.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class DateUtil {

    /**
     * 计算两个日期相差天数
     */
    public static Long betweenDay(LocalDate startDate, LocalDate endDate){
        long days = startDate.until(endDate, ChronoUnit.DAYS);
        return days;
    }

    /**
     * Date 转 LocalDate
     */
    public static LocalDate dateToLocalDate(Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }
}
