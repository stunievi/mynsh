package com.beeasy.hzback.modules.system.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class StartChildInstanceRequest {
    @NotNull(message = "节点ID不能为空")
    Long nodeInstanceId;
    @NotEmpty(message = "模型名字不能为空")
    String modelName;
    Map<String, String> innateData = new HashMap<>();
}
