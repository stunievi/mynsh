package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.SystemFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISystemFileDao extends JpaRepository<SystemFile,Long>{
    Optional<SystemFile> findFirstByIdAndType(Long id, SystemFile.Type type);
}
