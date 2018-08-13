package com.beeasy.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import javax.persistence.*;
import java.util.*;
@Getter
@Setter
@Entity
@Table(name = "t_system_notice")
@EntityListeners(AuditingEntityListener.class)
public class SystemNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(columnDefinition = JSONConverter.type)
    @Convert(converter = JSONConverter.class)
    Object bindData;

    //消息类别
    public enum Type{
        //系统通知
        SYSTEM,
        //工作流
        WORKFLOW,
        //台账信息
        ACC_LOAN
    }

    public enum State{
        UNREAD,
        READ
    }

}
