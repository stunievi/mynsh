package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.helper.ObjectConverter;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "t_menu_permission")
public class MenuPermission {
    @Id
    @GeneratedValue
    Integer id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;

    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    Set<String> unbinds = new HashSet<>();
}
