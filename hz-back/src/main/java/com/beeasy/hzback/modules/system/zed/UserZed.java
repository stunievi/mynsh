package com.beeasy.hzback.modules.system.zed;

import bin.leblanc.zed.proxy.MethodFile;

import java.util.Map;
import java.util.Optional;


@MethodFile("classpath:zed/zed_template.yaml")
public interface UserZed {

    Optional<Map<String, Map>> checkUser(String username, String phone);
}
