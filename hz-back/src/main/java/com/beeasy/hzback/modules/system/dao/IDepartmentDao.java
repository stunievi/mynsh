package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Department;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDepartmentDao extends JpaRepository<Department,Long> {
    List<Department> findAllByParent(Department department);
    List<Department> findAllByParent_Id(Long parentId);

    Department findByParent(Department department);
    Optional<Department> findFirstByParentAndName(Department department, String name);
    List<Department> findAllByName(String name);

    @Query(value = "select d.code from Department d where d.id = :id")
    List getDepartmentCode(@Param("id") long id);

    @Query(value = "select d.code from Department d where d.parentId = :parentId order by d.code desc")
    List getLastCode(@Param("parentId") Long parentId);


    @Query(value = "select d.code from Department d where d.parent is null order by d.code desc")
    List getTopLastCode();

    //是否是子部门
    @Query(value = "select count(par) from Department par where ( select count(child) from Department child where par.code = substring(child.code,1,length(par.code)) and child.id = :cid and child.code <> par.code) > 0 and par.id = :pid")
    int departmentHasChild(@Param("pid") long pid, @Param("cid") long cid);

    //是否是子岗位
    @Query(value = "select count(par) from Department par where ( select count(child) from Quarters child where par.code = substring(child.code, 1, length(par.code)) and child.id = :cid and child.code <> par.code) > 0 and par.id = :pid")
    int departmentHasQuarters(@Param("pid") long pid, @Param("cid") long cid);

    //得到该部门的所有下属子岗位ID
    @Query(value = "select child.id from Quarters child where (select count(par) from Department par where par.code = substring(child.code, 1, length(par.code)) and par.id = :did) > 0 ")
    List getChildQuartersIds(@Param("did") long did);
}
