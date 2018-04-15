package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "t_cloud_directory_index")
public class CloudDirectoryIndex extends AbstractBaseEntity{
    @Id
    @GeneratedValue
    Long id;

//    @JSONField(serialize = false)
//    @ManyToOne
//    User user;

    Long linkId;

    @Enumerated
    ICloudDiskService.DirType type;

    String dirName;

    @JSONField(serialize = false)
    @ManyToOne
    CloudDirectoryIndex parent;

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "parent")
    List<CloudDirectoryIndex> children = new ArrayList<>();




}
