package com.beeasy.hzback.core.helper;

import org.apache.commons.lang.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchableStringArrayConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        attribute = attribute.stream().map(item -> {
            return item.replaceAll("[\\!\\,\\s]", "");
        }).distinct()
                .filter(item -> !StringUtils.isEmpty(item))
                .map(item -> "!" + item + "!")
                .collect(Collectors.toList());
        return String.join(",", attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (StringUtils.isEmpty(dbData)) {
            return new ArrayList<>();
        }
        return Arrays.asList(dbData.split(",")).stream().map(item -> item.substring(1, item.length() - 1)).collect(Collectors.toList());
    }
}
