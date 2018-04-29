package com.beeasy.hzback.modules.mobile.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginResponse {
    String token;
    String userName;
}
