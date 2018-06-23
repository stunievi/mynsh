package com.beeasy.hzback.modules.system.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class SystemVarEditRequest {
    @NotEmpty(message = "key不能为空")
    String key;

    @NotEmpty(message = "value不能为空")
    String value;
}
