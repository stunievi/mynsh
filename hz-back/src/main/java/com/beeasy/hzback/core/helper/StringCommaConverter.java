package com.beeasy.hzback.core.helper;

import org.apache.commons.lang.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StringCommaConverter implements AttributeConverter<List<String>,String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return StringUtils.join(attribute,",");
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return (Arrays.asList(dbData.split(",")));
    }
}
