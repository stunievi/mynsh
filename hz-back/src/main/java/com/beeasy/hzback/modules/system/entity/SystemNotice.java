package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_system_notice")
@EntityListeners(AuditingEntityListener.class)
public class SystemNotice {
    @Id
    @GeneratedValue
    Long id;

    //消息内容
    String content;

    @Enumerated(value = EnumType.STRING)
    Type type;

    @CreatedDate
    Date addTime;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;

    @Column(name = "user_id")
    Long userId;

    @Enumerated(value = EnumType.STRING)
    State state;

    //消息类别
    public enum Type{
        //系统通知
        SYSTEM,
        //工作流
        WORKFLOW
    }

    public enum State{
        UNREAD,
        READ
    }

}
