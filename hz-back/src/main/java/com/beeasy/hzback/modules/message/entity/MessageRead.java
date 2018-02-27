package com.beeasy.hzback.modules.message.entity;

import bin.leblanc.message.type.MessageReadType;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_message_read")
@EntityListeners(AuditingEntityListener.class)
public class MessageRead {
    @Id
    @GeneratedValue
    private Integer id;

    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated()
    @JoinColumn(name = "is_read")
    private MessageReadType isRead;

    @CreatedDate
    private Date readTime;

}
