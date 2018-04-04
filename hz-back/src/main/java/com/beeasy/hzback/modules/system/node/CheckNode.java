package com.beeasy.hzback.modules.system.node;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class CheckNode extends BaseNode{

    String question;
    String key;
    String ps;
    int count = 1;
//    Content content = new Content();
    private Map<String,State> states = new LinkedHashMap();


    public CheckNode(String name) {
        super(name, "check");
    }

//    @Getter
//    @Setter
//    public static class Content extends AbstractBaseEntity{
////        Set<String> items;
////        String passItem;
//
////        int pass = 1;
////        int fail = 1;
//    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class State extends AbstractBaseEntity{
        private String item;
        private int condition;
        private String behaviour;
    }
}
