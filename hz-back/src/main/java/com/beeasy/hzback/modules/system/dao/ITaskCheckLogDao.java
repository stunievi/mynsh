package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.TaskCheckLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITaskCheckLogDao extends JpaRepository<TaskCheckLog,Long> {

    Optional<TaskCheckLog> findFirstByTypeAndLinkIdOrderByLastCheckDateDesc(TaskCheckLog.Type type, Long linkId);

}
