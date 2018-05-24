package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "t_system_file")
public class SystemFile extends AbstractBaseEntity{

    public enum Type {
        FACE,
        MESSAGE,
        CLOUDDISK,
        WORKFLOW
    }

    @Id
    @GeneratedValue
    Long id;

    String fileName;

    //TODO: 字段类型长度可能错误
    @Column(columnDefinition = "LONGBLOB")
    byte[] file;

    @Enumerated
    Type type;

    boolean removed = false;
}