package com.beeasy.hzback.modules.mobile.response;

import com.beeasy.hzback.modules.system.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginResponse {
    String token;
    User user;
    String publicKey;

    String cloudUsername;
    String cloudPassword;
    String cloudCommonUsername;
    String cloudCommonPassword;
    long cloudCommonRootId;

    public UserLoginResponse(String token, User user, String publicKey) {
        this.token = token;
        this.user = user;
        this.publicKey = publicKey;
    }

    public UserLoginResponse(String token, User user, String publicKey, long cloudCommonRootId) {
        this.token = token;
        this.user = user;
        this.publicKey = publicKey;
        this.cloudCommonRootId = cloudCommonRootId;
    }
}
