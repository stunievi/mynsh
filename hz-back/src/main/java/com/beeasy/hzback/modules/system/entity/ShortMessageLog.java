package com.beeasy.hzback.modules.system.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "t_short_message_log")
@EntityListeners(AuditingEntityListener.class)
public class ShortMessageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String message;
    String phone;

    @CreatedDate
    Date addTime;


}
