package com.beeasy.hzback.core.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.awt.print.Pageable;

public class JSONConverter implements AttributeConverter<Object,String> {

    public static final DBDRIVER DRIVER = DBDRIVER.DB2;
    public static final String type = "LONGTEXT";
    public static final String blobType = "LONGBLOB";
//    public static String fuck = "";
//    static {
//        switch (DRIVER){
//            case DB2:
//                fuck = "fuck";
////                type = "CLOB";
////                blobType = "BLOB(16M)";
//                break;
//            case MYSQL:
////                type = "LONGTEXT";
////                blobType = "LONGBLOB";
//                break;
//        }
//    }

//    public static final String type = "CLOB";
//    public static final String blobType = "BLOB(16M)";
//
    public enum DBDRIVER{
        DB2,
        MYSQL
    }


    public static String getPagedSql(String sql, PageRequest pageable){
        switch (DRIVER){
            case DB2:
                return sql + String.format(" LIMIT %d OFFSET %d", pageable.getOffset(), pageable.getPageSize());

            case MYSQL:
                return sql + String.format(" LIMIT %d, %d", pageable.getOffset(), pageable.getPageSize());
        }

        return sql;
    }

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return JSON.toJSONString(attribute);
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        return JSON.parse(dbData);
    }
}
