package com.beeasy.hzback.modules.message.entity;

import bin.leblanc.message.converter.StringBlobConverter;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "t_message_content")
public class MessageContent extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @JoinColumn(name = "msg_id")
    @OneToOne()
    private Message message;

    @Column(columnDefinition = "BLOB")
    @Convert(converter = StringBlobConverter.class)
    private String content;
}
