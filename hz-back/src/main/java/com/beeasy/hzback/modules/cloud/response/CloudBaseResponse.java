package com.beeasy.hzback.modules.cloud.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CloudBaseResponse {
    List<String> responseCookies = new ArrayList<>();
    String cookie;
    String status;
    String msg;
}
