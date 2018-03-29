package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface IDepartmentDao extends JpaRepository<Department,Integer> {
    List<Department> findAllByParent(Department department);
    List<Department> findAllByParentId(Integer parentId);
    Department findByParent(Department department);
    Department findByName(String name);
    List<Department> findAllByName(String name);

}
