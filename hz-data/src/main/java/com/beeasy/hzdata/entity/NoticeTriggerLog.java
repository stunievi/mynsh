package com.beeasy.hzdata.entity;

import act.util.SimpleBean;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name = "T_NOTICE_TRIGGER_LOG")
public class NoticeTriggerLog implements SimpleBean {
    public Long id;
    public String ruleName = "";
    public Date triggerTime;
    public String uuid = "";
}
