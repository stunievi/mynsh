package com.beeasy.common.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import javax.persistence.*;
import java.util.*;

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
