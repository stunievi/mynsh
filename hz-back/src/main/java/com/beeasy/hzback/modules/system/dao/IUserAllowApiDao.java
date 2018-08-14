package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.UserAllowApi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserAllowApiDao extends JpaRepository<UserAllowApi, Long> {
    int deleteAllByUserId(long uid);

    int countByUserIdAndApi(long uid, String api);
}
