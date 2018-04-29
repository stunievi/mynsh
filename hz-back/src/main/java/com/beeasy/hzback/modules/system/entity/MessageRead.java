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
    @GeneratedValue
    Long id;

    //用户ID(这里不需要使用关联)
    @JSONField(serialize = false)
    @ManyToOne
    User user;

    //已经读到最末的信息ID, 同样不使用关联
    Long messageId;

    //会话ID, 不使用关联
    @ManyToOne
    MessageSession session;
}
