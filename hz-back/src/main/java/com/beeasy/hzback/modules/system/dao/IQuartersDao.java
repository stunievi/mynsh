package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Department;
import com.beeasy.hzback.modules.system.entity.Quarters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface IQuartersDao extends JpaRepository<Quarters,Long>{
    Page<Quarters> findAllByDepartment_Id(long departmentId, Pageable pageable);
    Quarters findFirstByDepartmentAndName(Department department, String name);
    List<Quarters> findAllByIdIn(long[] ids);
    List<Quarters> findAllByIdIn(Set<Long> ids);
    List<Quarters> findAllByIdIn(List<Long> ids);


    @Query(value = "select count(q1) from Quarters q1 where ( select (q2.department) from Quarters q2 where q2.id = :id ) = q1.department and q1.name = :name")
    int countSameNameFromDepartment(@Param("id") long id, @Param("name") String name);

    @Query(value = "select q.code from Quarters q where q.departmentId = :did order by q.code desc")
    List getQuartersCodeFromDepartment(@Param("did") long did);

    @Query(value = "select q.code from Quarters q where q.id = :qid")
    List getQuartersCode(@Param("qid") long qid);


//    @Query(value = "select count(child) from Quarters child where child.id = :cid and child.code like (select concat(par.code,'%') from Department par where par.id = :pid)")
//    int isFromDepartment(@Param("cid") long cid, @Param("pid") long pid);
}
