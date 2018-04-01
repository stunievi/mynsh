package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.form.UserAdd;

import java.util.List;

public interface IUserService {

    /**
     * 层级用.表示 例如 工作台.我的工作台
     * @param user
     * @param menus
     * @return
     */
    boolean bindMenus(User user, List<String> menus);
    boolean unbindMenus(User user, List<String> menus);

    JSONArray getMenus(User user);

    User createUser(UserAdd add) throws RestException;

    boolean deleteUser(User user);
}
