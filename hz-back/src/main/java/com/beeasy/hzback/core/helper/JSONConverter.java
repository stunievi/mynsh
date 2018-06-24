package com.beeasy.hzback.core.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

public class JSONConverter implements AttributeConverter<Object,String> {


    public static final String type = "LONGTEXT";
    public static final String blobType = "LONGBLOB";
//    public static final String type = "CLOB";
//    public static final String blobType = "BLOB(16M)";

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return JSON.toJSONString(attribute);
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        return JSON.parse(dbData);
    }
}
