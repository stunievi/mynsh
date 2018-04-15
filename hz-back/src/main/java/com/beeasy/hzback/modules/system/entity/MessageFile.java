package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "t_message_file")
public class MessageFile extends AbstractBaseEntity{
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    Message message;

    ICloudDiskService.DirType type;
    Long linkId;

}
