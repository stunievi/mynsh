package com.beeasy.hzdata.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_MESSAGE_TEMPLATE")
@Getter
@Setter
public class MessageTemplate {
    private Long id;
    private String name;
    private String template;
    private String placeholder;
}
