package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.core.helper.JSONConverter;
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
public class SystemFile extends AbstractBaseEntity{

    public enum Type {
        FACE,
        MESSAGE,
        CLOUDDISK,
        WORKFLOW,
        TEMP
    }

    @Id
    @GeneratedValue
    Long id;

    String fileName;

    //TODO: 字段类型长度可能错误
    @Column(columnDefinition = JSONConverter.blobType)
    byte[] bytes;

    @Enumerated
    Type type;

    @LastModifiedDate
    Date lastModifyTime;

    boolean removed = false;
}