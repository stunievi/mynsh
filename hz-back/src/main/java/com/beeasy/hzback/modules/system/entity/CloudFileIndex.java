package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "t_cloud_file_index")
public class CloudFileIndex extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    Long id;

    String fileName;
    String filePath;

    @JSONField(serialize = false)
    @ManyToOne
    CloudDirectoryIndex directoryIndex;

//    @ManyToOne
//    User user;

//    @Enumerated
//    ICloudDiskService.Type type;

//    String virtualPath = "/";
}
