package com.beeasy.hzback.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name = "T_MESSAGE")
@Data
public class Msg {

    @AssignID("simple")
    Long id;


    UserType fromType;
    Long fromId;

    UserType toType;
    Long toId;

    String content;
    Date sendTime;

    Type type;

    public enum Type{
        TEXT,
        FILE;
    }

    public enum UserType{
        USER;
    }
}
