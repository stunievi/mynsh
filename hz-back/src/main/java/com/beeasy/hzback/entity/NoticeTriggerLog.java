package com.beeasy.hzback.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name = "T_NOTICE_TRIGGER_LOG")
@Getter
@Setter
public class NoticeTriggerLog {
    private Long id;
    private String ruleName = "";
    private Date triggerTime;
    private String uuid = "";
    String uuid2;
}
