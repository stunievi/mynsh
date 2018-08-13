package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.GPSPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGPSPositionDao extends JpaRepository<GPSPosition,Long> {
}
