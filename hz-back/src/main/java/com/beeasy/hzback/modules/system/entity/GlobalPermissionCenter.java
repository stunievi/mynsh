package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "t_global_permission_center")
public class GlobalPermissionCenter extends AbstractBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;
    @Column(name = "user_id")
    Long userId;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    GlobalPermission permission;
    @Column(name = "permission_id")
    Long permissionId;



}
