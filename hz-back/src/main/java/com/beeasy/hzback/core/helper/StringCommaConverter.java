package com.beeasy.hzback.core.helper;

import org.apache.commons.lang.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCommaConverter implements AttributeConverter<List<String>,String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return StringUtils.join(attribute,",");
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if(null == dbData) return new ArrayList<>();
        return (Arrays.asList(dbData.split(","))).stream().filter(item -> !StringUtils.isEmpty(item)).collect(Collectors.toList());
    }
}
