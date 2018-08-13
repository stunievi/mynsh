package com.beeasy.hzback.modules.mobile.response;

import com.beeasy.common.entity.Message;
import lombok.Data;

@Data
public class UnreadMessageResponse {
    Message.LinkType toType = Message.LinkType.USER;
    Long toId = 0L;
    int unreadNums = 0;
    Message lastMessage;
}
