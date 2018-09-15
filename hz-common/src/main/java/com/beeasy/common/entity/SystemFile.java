package com.beeasy.common.entity;//package com.beeasy.hzback.modules.system.entity;

import com.beeasy.common.helper.AbstractBaseEntity;
import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "t_system_file")
@EntityListeners(AuditingEntityListener.class)
public class SystemFile extends AbstractBaseEntity {

    public enum Type {
        FACE,
        MESSAGE,
        CLOUDDISK,
        WORKFLOW,
        TEMP
    }

    public enum StorageDriver {
        NATIVE,
        FASTDFS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String fileName;

    //TODO: 字段类型长度可能错误
    @Column(columnDefinition = JSONConverter.blobType)
    byte[] bytes;

    @Enumerated(value = EnumType.STRING)
    Type type;

    @Column(length = 15)
    @Enumerated(value = EnumType.STRING)
    StorageDriver storageDriver;

    @LastModifiedDate
    Date lastModifyTime;

    String ext = "";

    String filePath = "";

    Long creatorId;
    String creatorName;

    String token;
    Date exprTime;
//    boolean removed = false;
}