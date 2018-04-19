package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IDepartmentDao extends JpaRepository<Department,Long> {
    List<Department> findAllByParent(Department department);
    List<Department> findAllByParent_Id(Long parentId);
    List<Department> findAllByParentId(Long parentId);

    Department findByParent(Department department);
    Optional<Department> findFirstByParentAndName(Department department, String name);
    List<Department> findAllByName(String name);

}
