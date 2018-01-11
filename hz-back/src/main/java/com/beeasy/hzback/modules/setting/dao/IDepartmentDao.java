package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.modules.setting.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDepartmentDao extends JpaRepository<Department,Integer> {

}
