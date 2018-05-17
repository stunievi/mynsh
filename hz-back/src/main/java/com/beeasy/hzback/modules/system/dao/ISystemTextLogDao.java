package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.SystemTextLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISystemTextLogDao extends JpaRepository<SystemTextLog,Long>{
    List<SystemTextLog> findAllByTypeAndLinkIdOrderByAddTimeDesc(SystemTextLog.Type type, Long linkId);
}
