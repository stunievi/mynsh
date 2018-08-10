package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.modules.system.entity_kt.SystemFile;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_download_file_token")
public class DownloadFileToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    SystemFile systemFile;

    @Column(name = "file_id")
    Long fileId;

    String token;

    Date exprTime;
}
