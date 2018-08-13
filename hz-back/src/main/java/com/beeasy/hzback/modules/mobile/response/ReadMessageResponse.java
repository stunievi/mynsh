package com.beeasy.hzback.modules.mobile.response;

import com.beeasy.common.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReadMessageResponse {
    Message.LinkType toType;
    Long toId;
    boolean success = false;
}
