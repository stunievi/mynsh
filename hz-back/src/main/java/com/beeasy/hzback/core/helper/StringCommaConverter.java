package com.beeasy.hzback.core.helper;

import org.apache.commons.lang.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class StringCommaConverter implements AttributeConverter<Set<String>,String> {
    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return StringUtils.join(attribute,",");
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        return new LinkedHashSet<>(Arrays.asList(dbData.split(",")));
    }
}
