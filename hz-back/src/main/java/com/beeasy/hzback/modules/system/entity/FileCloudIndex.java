package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_file_cloud_index")
public class FileCloudIndex extends AbstractBaseEntity {
    @Id
    @GeneratedValue
    Long id;

    @Enumerated
    ICloudDiskService.DirType type;
    Long linkId;
    Long fileId;

    boolean dir;
}
