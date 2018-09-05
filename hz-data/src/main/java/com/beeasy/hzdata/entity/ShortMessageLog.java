package com.beeasy.hzdata.entity;

import act.util.SimpleBean;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name = "T_SHORT_MESSAGE_LOG")
public class ShortMessageLog implements SimpleBean {
    public Long id;
    public String phone;
    public String message;
    public Date addTime;
}
