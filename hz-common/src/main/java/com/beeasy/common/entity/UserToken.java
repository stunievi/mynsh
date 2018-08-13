package com.beeasy.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;
import java.util.*;
@Getter
@Setter
@Entity
@Table(name = "t_user_token", indexes = {
        @Index(name = "user_id",columnList = "user_id")
//        @Index(name = "type",columnList = "type")
//        @Index(name = "expr_time",columnList = "EXPR_TIME")
})
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;
    @Column(name = "user_id")
    Long userId;

    String token;

    Date exprTime;

    @Column(length = 20)
    @Enumerated(value = EnumType.STRING)
    Type type;


    public enum Type{
        WEB,
        ANDROID
    }
}
