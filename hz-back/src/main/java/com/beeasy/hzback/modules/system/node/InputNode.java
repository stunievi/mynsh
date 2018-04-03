package com.beeasy.hzback.modules.system.node;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class InputNode extends BaseNode{
    String type = "input";
    boolean start;
    Map<String,Content> content = new HashMap<>();

    @Getter
    @Setter
    public static class Content extends AbstractBaseEntity{
        String type;
        String cname;
        String ename;
        boolean required = false;
    }
}

