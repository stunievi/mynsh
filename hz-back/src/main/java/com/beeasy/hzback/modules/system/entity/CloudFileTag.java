package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_cloud_tag")
public class CloudFileTag extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    CloudDirectoryIndex index;

    String tag;
}
