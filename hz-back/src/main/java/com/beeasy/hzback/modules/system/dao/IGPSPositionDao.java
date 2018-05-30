package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.GPSPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGPSPositionDao extends JpaRepository<GPSPosition,Long> {
}
