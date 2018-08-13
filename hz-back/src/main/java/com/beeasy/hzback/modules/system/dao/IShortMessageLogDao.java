package com.beeasy.hzback.modules.system.dao;


import com.beeasy.common.entity.ShortMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IShortMessageLogDao extends JpaRepository<ShortMessageLog,Long>,JpaSpecificationExecutor{
}
