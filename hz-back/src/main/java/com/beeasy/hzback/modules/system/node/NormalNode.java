package com.beeasy.hzback.modules.system.node;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 不做任何反应的节点, 通畅为结束节点
 */
@Getter
@Setter
public class NormalNode extends BaseNode {
    private String behavior;

    public NormalNode(Map v) {
        behavior = String.valueOf(v.getOrDefault("behavior", ""));
    }
}
