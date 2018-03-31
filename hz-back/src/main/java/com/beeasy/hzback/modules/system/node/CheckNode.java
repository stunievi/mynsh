package com.beeasy.hzback.modules.system.node;

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
    public static class Content{
        String question;
        Set<String> items;
        String passItem;
        int count;
        int pass;
    }
}
