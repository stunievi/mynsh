package com.beeasy.hzback.modules.system.dao;

//import com.beeasy.hzback.modules.system.aop.SystemLogAop;

import com.beeasy.common.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISystemLogDao extends JpaRepository<SystemLog, Long> {
    Page<List<SystemLog>> findAllByUserName(String userName, org.springframework.data.domain.Pageable pageable);
}
