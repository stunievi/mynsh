package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.form.UserAdd;

import java.util.List;

public interface IUserService {

    /**
     * 层级用.表示 例如 工作台.我的工作台
     * @param uid
     * @param menus
     * @return
     */
    boolean bindMenus(long uid, List<String> menus);
    boolean unbindMenus(long uid, List<String> menus);

    JSONArray getMenus(long uid);

    User createUser(UserAdd add) throws RestException;

    boolean deleteUser(long id);

    User saveUser(User user);

    boolean addQuarters(long uid, long ...qids);
    boolean removeQuarters(long uid, long ...qids);

    /**
     * 设置岗位, 会清空之前的内容
     * @param uid
     * @param qids
     */
    boolean setQuarters(long uid, long ...qids);

}
