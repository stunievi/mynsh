package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.helper.ObjectConverter;
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
    @JSONField(serialize = false)
    @Id
    @GeneratedValue
    Long id;

    @JSONField(serialize = false)
    @ManyToOne(optional = false)
    User user;

    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    Set<String> unbinds = new HashSet<>();

    @JSONField(serialize = false)
    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    Set<String> unbindItems = new HashSet<>();

    @Enumerated
    IUserService.PermissionType type;


}
