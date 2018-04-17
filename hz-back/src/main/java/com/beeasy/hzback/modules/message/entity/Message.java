package com.beeasy.hzback.modules.message.entity;

import bin.leblanc.message.type.MessageType;
import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.modules.system.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_message")
@EntityListeners(AuditingEntityListener.class)
public class Message extends AbstractBaseEntity{
    @Id
    @GeneratedValue
    private Integer id;

    @JoinColumn(name = "from_user_id")
    @ManyToOne()
    @JSONField(serialize = false)
    private User fromUser;

    private Integer toUser;

    @Enumerated()
    private MessageType messageType;

    @CreatedDate
    private Date sendTime;

    @OneToOne(mappedBy = "message")
    private MessageContent content;
}
