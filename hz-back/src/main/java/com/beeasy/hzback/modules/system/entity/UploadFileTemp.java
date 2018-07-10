package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "t_upload_file_temp")
@EntityListeners(AuditingEntityListener.class)
public class UploadFileTemp extends AbstractBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String uuid;
    String fileName;
    String filePath;

    @ManyToOne
    CloudDirectoryIndex directoryIndex;

    @CreatedDate
    Date addTime;
}
