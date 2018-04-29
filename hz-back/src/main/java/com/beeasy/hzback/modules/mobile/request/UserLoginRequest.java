package com.beeasy.hzback.modules.mobile.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class UserLoginRequest {
    @NotEmpty(message = "用户名不能为空")
    String username;
    @NotEmpty(message = "密码不能为空")
    String password;
}
