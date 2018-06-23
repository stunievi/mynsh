package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.SystemVariable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISystemVariableDao extends JpaRepository<SystemVariable,Long>{
    Optional<SystemVariable> findFirstByVarName(String varName);

    int deleteByVarNameAndCanDeleteIsTrue(String varName);
}
