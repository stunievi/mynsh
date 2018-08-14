package com.beeasy.hzback.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;

@ApiModel
@Data
public class UserEdit {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^1[3456789][0-9]{9}$", message = "手机号码格式错误")
    private String phone;

    @ApiModelProperty(value = "真实姓名")
    private String trueName;

    @ApiModelProperty(value = "邮箱")
    @Email
    private String email;

    @ApiModelProperty(value = "是否禁用")
    private Boolean baned;

    @ApiModelProperty(value = "用户岗位ID数组")
    private Set<Long> quarters;

    @ApiModelProperty(value = "菜单黑名单")
    private Set<String> unbindMenus;

    @ApiModelProperty(value = "功能黑名单")
    private Set<String> unbindMethods;

    String accCode;
}

