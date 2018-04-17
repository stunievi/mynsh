package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Department;
import com.beeasy.hzback.modules.system.entity.Quarters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface IQuartersDao extends JpaRepository<Quarters,Long>{
    Page<Quarters> findAllByDepartment_Id(long departmentId, Pageable pageable);
    Quarters findFirstByDepartmentAndName(Department department, String name);
    List<Quarters> findAllByIdIn(long[] ids);
    List<Quarters> findAllByIdIn(Set<Long> ids);
    List<Quarters> findAllByIdIn(List<Long> ids);

}
