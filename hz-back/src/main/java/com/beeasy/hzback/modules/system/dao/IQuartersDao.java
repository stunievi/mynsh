package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Quarters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IQuartersDao extends JpaRepository<Quarters,Integer>{
    Page<Quarters> findAllByDepartment_Id(Integer departmentId, Pageable pageable);
}
