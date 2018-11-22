package com.beeasy.hzback.entity;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_MESSAGE_READ")
@Data
public class MsgRead {
    @AssignID("simple")
    Long id;

    Long userId;

    Msg.UserType toType;
    Long toId;

    Integer unreadNum;
}
