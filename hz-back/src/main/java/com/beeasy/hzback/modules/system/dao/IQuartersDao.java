package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.system.entity.Quarters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IQuartersDao extends JpaRepository<Quarters,Long>{
    Page<Quarters> findAllByDepartment_Id(long departmentId, Pageable pageable);
    Quarters findFirstByDepartmentAndName(Department department, String name);
    List<Quarters> findAllByIdIn(long[] ids);

}
