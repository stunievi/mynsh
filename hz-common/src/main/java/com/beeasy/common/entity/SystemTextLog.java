package com.beeasy.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.AbstractBaseEntity;
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
@Table(name = "t_system_text_log")
@EntityListeners(AuditingEntityListener.class)
public class SystemTextLog extends AbstractBaseEntity {
    public enum Type {
        SYSTEM,
        USER,
        WORKFLOW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String content;

    @Enumerated
    Type type;

    Long linkId;

    @CreatedDate
    Date addTime;

    @JSONField(serialize = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ManyToOne
    User user;
    @Column(name = "user_id")
    Long userId;

}
