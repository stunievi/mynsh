package com.beeasy.hzdata.entity;

import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_MESSAGE_TEMPLATE")
public class MessageTemplate {
    public Long id;
    public String name;
    public String template;
    public String placeholder;
}
