//package com.beeasy.mscommon;
//
//public class DataSourceContextHolder {
//
//    private static final ThreadLocal<String> contextHolder  = new ThreadLocal<String>();
//
//    /**
//     * 使用setDataSourceType设置当前的
//     * @param dataSourceType
//     */
//    public static void setDataSourceType(String dataSourceType) {
//        contextHolder.set(dataSourceType);
//    }
//
//    public static String getDataSourceType() {
//        return contextHolder.get();
//    }
//
//    public static void clearDataSourceType() {
//        contextHolder.remove();
//    }
//
//}
