package com.beeasy.hzback.modules.system.node;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CheckNode extends BaseNode{
    protected String type = "check";

    Content content;

    @Getter
    @Setter
    public static class Content extends AbstractBaseEntity{
        String question;
        Set<String> items;
        String passItem;
        String key;
        String ps;
        int count = 1;
        int pass = 1;
        int fail = 1;
    }
}
