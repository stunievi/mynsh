package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "t_user_ext_permission")
public class UserExternalPermission extends AbstractBaseEntity{
    @Id
    @GeneratedValue
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    User user;

    @Enumerated
    Permission permission;


    public enum Permission{
        COMMON_CLOUD_DISK("common_cloud_disk");

        private String value;
        Permission(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
