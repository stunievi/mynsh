package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.form.UserAdd;

import java.util.Optional;

public interface IUserService {

    enum PermissionType{
        MENU,
        METHOD
    }

    User createUser(UserAdd add) throws RestException;

    boolean deleteUser(long id) throws CannotFindEntityException;

    User saveUser(User user);

    Optional<User> findUser(long id);

    User findUserE(long id) throws CannotFindEntityException;
}
