package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDepartmentDao extends JpaRepository<Department,Long> {
    List<Department> findAllByParent(Department department);
    List<Department> findAllByParentId(Long parentId);
    Department findByParent(Department department);
    Department findByName(String name);
    List<Department> findAllByName(String name);

}
