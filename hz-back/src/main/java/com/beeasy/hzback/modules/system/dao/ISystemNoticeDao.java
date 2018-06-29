package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.SystemNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ISystemNoticeDao extends JpaRepository<SystemNotice, Long>, JpaSpecificationExecutor {


    @Modifying
    @Query("update SystemNotice set state = 'READ' where userId = :uid and id in :ids")
    int updateRead(@Param("uid") long uid, @Param("ids")Collection<Long> ids);


}
