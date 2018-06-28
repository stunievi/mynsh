package com.beeasy.hzback.modules.mobile.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    Long dealerId;

    //是否手动任务
    //禁止传递
    boolean manual = true;

    //是否公共任务
    boolean common = false;

    //固有字段信息
    Map<String,String> data = new HashMap<>();

    //计划开始时间
    @Future
    Date planStartTime;


    public ApplyTaskRequest(Long modelId, String title, String info, Long dealerId, boolean manual, boolean common) {
        this.modelId = modelId;
        this.title = title;
        this.info = info;
        this.dealerId = dealerId;
        this.manual = manual;
        this.common = common;
    }
}
