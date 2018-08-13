package com.beeasy.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;
import java.util.*;

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
