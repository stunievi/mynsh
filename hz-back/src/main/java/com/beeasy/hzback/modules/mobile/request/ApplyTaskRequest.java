package com.beeasy.hzback.modules.mobile.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplyTaskRequest {
    @NotNull(message = "请选择对应的模型")
    Long modelId;
    @NotEmpty(message = "任务标题不能为空")
    String title;
    String info;

    //任务执行人
    //-1 为自己
    //0 公共任务
    //x 指派的用户ID
    @NotNull(message = "任务执行人不能为空")
    Long dealerId;
}
