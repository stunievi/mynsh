package com.beeasy.hzback.modules.system.node;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
abstract public class BaseNode implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String type;
    protected boolean start = false;
    protected boolean end = false;
    protected int order;


}
