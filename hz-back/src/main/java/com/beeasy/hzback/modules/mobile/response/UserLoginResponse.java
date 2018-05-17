package com.beeasy.hzback.modules.mobile.response;

import com.beeasy.hzback.modules.system.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginResponse {
    String token;
    User user;
}
