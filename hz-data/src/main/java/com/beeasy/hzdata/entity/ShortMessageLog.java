package com.beeasy.hzdata.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name = "T_SHORT_MESSAGE_LOG")
@Getter
@Setter
public class ShortMessageLog {
    private Long id;
    private String phone;
    private String message;
    private Date addTime;
}
