package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.DownloadFileToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface IDownloadFileTokenDao extends JpaRepository<DownloadFileToken,Long>{
    Optional<DownloadFileToken> findTopByTokenAndExprTimeGreaterThan(String token, Date date);
}
