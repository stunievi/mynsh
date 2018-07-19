package com.beeasy.hzback.modules.mobile.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplyTaskRequest {
    Long modelId;
    String modelName;

    //父任务ID
    Long parentId;

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

    boolean goNext = false;

    //固有字段信息
    Map<String,String> data = new HashMap<>();
    Map<String,String> startNode = new HashMap<>();

    //第一个节点上传的文件
    List<Long> files = new ArrayList<>();

    //计划开始时间
    @Future
    Date planStartTime;

    //固有数据源
    DataSource dataSource;

    //固有数据源绑定值
    String dataId;

    public enum DataSource{
        CLIENT,
        ACC_LOAN
    }

    public ApplyTaskRequest(Long modelId, String title, String info, Long dealerId, boolean manual, boolean common) {
        this.modelId = modelId;
        this.title = title;
        this.info = info;
        this.dealerId = dealerId;
        this.manual = manual;
        this.common = common;
    }


    @AssertTrue(message = "模型ID和标准模型名至少要有一个")
    public boolean isValidModel(){
        return null != modelId || !StringUtils.isEmpty(modelName);
    }
}
