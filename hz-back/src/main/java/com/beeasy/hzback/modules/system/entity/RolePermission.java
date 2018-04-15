package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.helper.ObjectConverter;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.service.IUserService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "t_user_permission")
public class RolePermission {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;

    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    Set<String> unbinds = new HashSet<>();

    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    Set<String> unbindItems = new HashSet<>();

    @Enumerated
    IUserService.PermissionType type;


}
