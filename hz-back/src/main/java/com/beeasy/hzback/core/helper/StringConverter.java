package com.beeasy.hzback.core.helper;

//import com.beeasy.hzback.modules.setting.work_engine.BaseWorkNode;

import javax.persistence.AttributeConverter;

public class StringConverter implements AttributeConverter<String,byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(String s) {
        return s.getBytes();
    }

    @Override
    public String convertToEntityAttribute(byte[] bytes) {
        return new String(bytes);
    }
}
