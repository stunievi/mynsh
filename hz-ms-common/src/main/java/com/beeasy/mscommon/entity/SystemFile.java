package com.beeasy.mscommon.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.Table;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Getter
@Setter
@Table(name = "T_SYSTEM_FILE")
public class SystemFile {
    Long id;
    String ext;
    @JSONField(serialize = false)
    String filePath;
    Driver storageDriver;
    String fileName;
    Type type;
    Date lastModifyTime;

    Long creatorId;
    String creatorName;
    String tags;

    //文件下载令牌
    String token;
    //文件超时时间
    Date exprTime;

    public enum Driver{
        NATIVE,
        FASTDFS
    }

    public enum Type {
        DEFALUT_FACE,
        FACE,
        MESSAGE,
        CLOUDDISK,
        WORKFLOW,
        TEMP
    }
}
