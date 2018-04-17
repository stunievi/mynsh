package com.beeasy.hzback.modules.setting.entity;

import com.beeasy.hzback.modules.system.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Getter
@Setter
@Entity
@Table(name = "t_user_profile")
public class UserProfile {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @OneToOne()
    private User user;

    private String face;

}
