//package com.beeasy.hzback.modules.setting.dao;
//
//import com.beeasy.hzback.modules.system.entity.Department;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface IDepartmentDao extends JpaRepository<Department,Long> {
//    List<Department> findAllByParent(Department department);
//    List<Department> findAllByParentId(Integer parentId);
//    Department findByParent(Department department);
//    Department findFirstByName(String name);
//    List<Department> findAllByName(String name);
//
//}
