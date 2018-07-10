package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_message_read")
public class MessageRead extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id = 0L;

//    用户ID(这里不需要使用关联)
    @JSONField(serialize = false)
    @ManyToOne
    User user;

//    Long fromId = 0L;
    Long toId = 0L;
//    Message.LinkType fromType = Message.LinkType.USER;
    Message.LinkType toType = Message.LinkType.USER;


    int unreadNum = 0;

    //会话ID, 不使用关联
//    @ManyToOne
//    MessageSession session;
}
