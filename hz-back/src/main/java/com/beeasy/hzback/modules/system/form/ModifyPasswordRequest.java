package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@ApiModel
@Data
public class ModifyPasswordRequest {

    @NotEmpty(message = "旧密码不能为空")
    private String oldPassword;

    @NotEmpty(message = "新密码不能为空")
    private String newPassword;
}
