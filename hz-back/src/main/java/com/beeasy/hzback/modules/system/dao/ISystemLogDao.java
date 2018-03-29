package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.aop.SystemLogAop;
import com.beeasy.hzback.modules.system.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface ISystemLogDao extends JpaRepository<SystemLog,Long>{
    Page<List<SystemLog>> findAllByUserName(String userName, org.springframework.data.domain.Pageable pageable);
}
