package com.beeasy.hzback.modules.system.node;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
abstract public class BaseNode implements Serializable {
    protected static final long serialVersionUID = 1L;

    public BaseNode(String name, String type) {
        this.name = name;
        this.type = type;
    }

    protected String name;
    protected String type;
    protected boolean start = false;
    protected boolean end = false;
    protected int order = 0;


    //下一个节点, 通畅只有资料节点有, 其他则用behaviour执行
    protected Set<String> next = new LinkedHashSet<>();


}
