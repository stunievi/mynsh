package com.beeasy.hzdata.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.EnumMapping;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.mapper.BaseMapper;

import java.util.Date;

@Table(name = "T_SYSTEM_NOTICE")
@Getter
@Setter
public class SystemNotice {
    @AutoID
    private Long id;
    private Long userId;
    private Date addTime;
    private State state = State.UNREAD;
    private Type type;
    private String content = "";
    private String bindData = "";

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
