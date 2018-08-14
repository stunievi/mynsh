package com.beeasy.common.entity;

import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_system_log")
@EntityListeners(AuditingEntityListener.class)
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long userId;
    String userName;

    @Column(columnDefinition = JSONConverter.VARCHAR_5O)
    String controller;
    @Column(columnDefinition = JSONConverter.VARCHAR_5O)
    String method;

    @Column(columnDefinition = JSONConverter.type)
    @Convert(converter = JSONConverter.class)
    Object params;

    @CreatedDate
    Date addTime;
//    String ip;

}
