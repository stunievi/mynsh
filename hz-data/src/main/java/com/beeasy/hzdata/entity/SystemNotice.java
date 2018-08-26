package com.beeasy.hzdata.entity;

import act.util.SimpleBean;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.EnumMapping;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.mapper.BaseMapper;

import java.util.Date;

@Table(name = "T_SYSTEM_NOTICE")
public class SystemNotice implements SimpleBean {
    @AutoID
    public Long id;
    public Long userId;
    public Date addTime;
    public State state;
    public Type type;
    public String content = "";
    public String bindData = "";

    @EnumMapping(EnumMapping.EnumType.STRING)
    public enum Type {
        //系统通知
        SYSTEM,
        //工作流
        WORKFLOW,
        //台账信息
        ACC_LOAN
    }

    @EnumMapping(EnumMapping.EnumType.STRING)
    public enum State {
        UNREAD,
        READ
    }

}
