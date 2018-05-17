package com.beeasy.hzback.modules.mobile.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StringMessageRequest {
    @NotNull(message = "发送用户不能为空")
    Long toUid;
    String content;
    String uuid;
}
