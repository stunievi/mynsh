package com.beeasy.hzback.modules.mobile.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UnreadMessageRequest {
    List<Long> toUserIds = new ArrayList<>();
}
