package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.core.helper.ObjectConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "t_message")
@EntityListeners(AuditingEntityListener.class)
public class Message extends AbstractBaseEntity{
    @Id
    @GeneratedValue
    Long id;

    Long fromUserId;

//    @JSONField(serialize = false)
//
//    @ManyToOne
//    User toUser;

    @CreatedDate
    Date sendTime;

//    String title;

    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    String content;

    @OneToMany
    Set<SystemFile> files = new LinkedHashSet<>();

    @ManyToOne
    MessageSession session;

//    boolean checked = false;



}
