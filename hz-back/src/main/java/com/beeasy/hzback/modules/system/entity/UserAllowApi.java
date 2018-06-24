package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_user_allow_api")
public class UserAllowApi extends AbstractBaseEntity {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;
    @Column(name = "user_id")
    Long userId;

    String api;
}
