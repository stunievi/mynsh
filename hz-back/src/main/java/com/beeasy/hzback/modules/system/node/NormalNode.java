package com.beeasy.hzback.modules.system.node;

/**
 * 不做任何反应的节点, 通畅为结束节点
 */
public class NormalNode extends BaseNode {
    public NormalNode(String name) {
        super(name, "end");
    }
}
