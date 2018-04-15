package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

@ApiModel
@Data
public class UserAdd {

    @ApiModelProperty(value = "用户名",required = true)
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "密码",required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "真实姓名",required = true)
    @NotEmpty(message = "真实姓名不能为空")
    private String trueName;

    @ApiModelProperty(value = "手机号",required = true)
    @NotEmpty(message = "手机不能为空")
    @Pattern(regexp = "^1[3456789][0-9]{9}$",message = "手机号码格式错误")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @Email
    private String email;

    @ApiModelProperty(value = "是否禁用", required = true)
    private boolean baned;
}
