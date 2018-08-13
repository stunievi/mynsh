package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.SystemTextLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ISystemTextLogDao extends JpaRepository<SystemTextLog,Long>{

    @Query(value = "select log from SystemTextLog log where log.type = :type and log.linkId = :linkId order by log.id desc")
    List<SystemTextLog> findLogs(@Param("type") SystemTextLog.Type type, @Param("linkId") long linkId);

    List<SystemTextLog> findAllByTypeAndLinkIdOrderByAddTimeDesc(SystemTextLog.Type type, Long linkId);
}
