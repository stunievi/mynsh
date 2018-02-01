package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface IDepartmentDao extends JpaRepository<Department,Integer> {
    Set<Department> findAllByParent(Department department);
    Department findByParent(Department department);
    Department findByName(String name);



}
