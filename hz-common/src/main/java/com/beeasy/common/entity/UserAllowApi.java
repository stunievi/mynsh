package com.beeasy.common.entity;

import com.beeasy.common.helper.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;
@Getter
@Setter
@Entity
@Table(name = "t_user_allow_api")
public class UserAllowApi extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;
    @Column(name = "user_id")
    Long userId;

    String api;
}
