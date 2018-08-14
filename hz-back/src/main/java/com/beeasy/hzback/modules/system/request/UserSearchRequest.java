package com.beeasy.hzback.modules.system.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserSearchRequest {
    String name = "";
    Boolean baned = null;
    List<Long> quarters = new ArrayList<>();
}
