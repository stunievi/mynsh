package com.beeasy.hzback.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_WORKFLOW_INS_ATTR")
@Getter
@Setter
public class WfInsAttr extends TailBean {
    Long id;
    Long insId;
    Long nodeId;
    Long uid;
    Type type;
    String attrKey;
    String attrValue;
    String attrCname;

    public enum Type {
        //固有
        INNATE,
        //节点属性
        NODE;
    }

}
