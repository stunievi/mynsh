package com.beeasy.hzback.core.helper;

import javax.persistence.AttributeConverter;

public class ObjectConverter implements AttributeConverter<Object,byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(Object o) {
        return Object2Array.objectToByteArray(o);
    }

    @Override
    public Object convertToEntityAttribute(byte[] bytes) {
        return Object2Array.byteArrayToObject(bytes);
    }
}
