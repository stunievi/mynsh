package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "t_cloud_directory_index")
@EntityListeners(AuditingEntityListener.class)
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

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    List<CloudDirectoryIndex> children = new ArrayList<>();

    @OrderBy(value = "dir DESC")
    boolean dir = false;

    @JSONField(serialize = false)
    @OneToOne(cascade = CascadeType.REMOVE)
    SystemFile file;

    @JSONField(serialize = false)
    @OneToMany(cascade = CascadeType.REMOVE)
    List<CloudFileTag> tags = new ArrayList<>();

    //创建时间
    @CreatedDate
    Date addTime;

    //最后一次修改时间
    @LastModifiedDate
    Date modifyTime;


//    @Transient
//    public Long getFileId(){
//        return null == file ? 0 : file.getId();
//    }
}
