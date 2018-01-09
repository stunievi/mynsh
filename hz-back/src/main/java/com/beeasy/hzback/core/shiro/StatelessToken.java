package com.beeasy.hzback.core.shiro;

import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.AuthenticationToken;

import java.io.Serializable;

public class StatelessToken implements AuthenticationToken,Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String userCode;

    @Getter
    @Setter
    private String token;

    public StatelessToken(String userCode, String token) {
        this.userCode = userCode;
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return userCode;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
