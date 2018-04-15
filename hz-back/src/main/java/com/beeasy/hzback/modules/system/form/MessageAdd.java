package com.beeasy.hzback.modules.system.form;

import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Set;

@ApiModel
@Data
public class MessageAdd {

    @Data
    public static class File{
        ICloudDiskService.DirType type;
        Long fileId;
    }

    @ApiModelProperty(value = "接收用户ID列表",required = true)
    @NotNull(message = "接收用户不能为空")
    Set<Long> toUserIds;

    @ApiModelProperty(value = "消息名",required = true)
    @NotEmpty(message = "消息标题不能为空")
    String title;

    @ApiModelProperty(value = "消息内容",required = true)
    @NotEmpty(message = "消息内容不能为空")
    String content;

    @ApiModelProperty(value = "消息附件", notes = "文件在文件柜中的路径")
    Set<File> files;
}
