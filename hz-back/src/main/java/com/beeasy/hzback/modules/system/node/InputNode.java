package com.beeasy.hzback.modules.system.node;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class InputNode extends BaseNode{
    String type = "input";
    boolean start;
    Map<String,String> content;
}

