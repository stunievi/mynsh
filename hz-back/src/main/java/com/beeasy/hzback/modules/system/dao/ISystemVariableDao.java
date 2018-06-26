package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.SystemVariable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ISystemVariableDao extends JpaRepository<SystemVariable,Long>{
    Optional<SystemVariable> findFirstByVarName(String varName);
    List<SystemVariable> findAllByVarNameIn(Collection<String> varNames);

    int deleteByVarNameAndCanDeleteIsTrue(String varName);
}
