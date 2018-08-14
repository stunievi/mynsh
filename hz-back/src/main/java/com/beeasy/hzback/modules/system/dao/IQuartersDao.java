package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.Department;
import com.beeasy.common.entity.Quarters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface IQuartersDao extends JpaRepository<Quarters, Long> {
    Page<Quarters> findAllByDepartment_Id(long departmentId, Pageable pageable);

    Quarters findFirstByDepartmentAndName(Department department, String name);

    List<Quarters> findAllByIdIn(Collection<Long> ids);

    /*
     * @gotomars
     * */
    Quarters findFirstByDepartmentAndNameAndIdNot(Department department, String name, Long id);

    //查找部门下的岗位个数
    int countByDepartment_Id(long id);

    int countByDepartmentIdAndName(final long did, final String name);

    int countByDepartmentIdAndNameAndIdNot(final long did, final String name, final long qid);

    //查找该岗位是否存在
    int countById(long id);

    //批量删除
    int deleteAllByIdIn(Collection<Long> ids);

    //更新岗位
    @Modifying
    @Query(value = "update Quarters set manager = :state where id in :qids")
    int updateManager(@Param("qids") Collection<Long> qids, @Param("state") boolean state);

    @Query(value = "select count(q1) from Quarters q1 where ( select (q2.department) from Quarters q2 where q2.id = :id ) = q1.department and q1.name = :name and q1.id <> :id")
    int countSameNameFromDepartment(@Param("id") long id, @Param("name") String name);

    @Query(value = "select q.code from Quarters q where q.departmentId = :did order by q.code desc")
    List getQuartersCodeFromDepartment(@Param("did") long did);

    @Query(value = "select q.code from Quarters q where q.id = :qid")
    List getQuartersCode(@Param("qid") long qid);

    @Query(value = "select q.departmentId from Quarters q where q.id in :qids")
    List<Long> getDepIds(@Param("qids") Collection<Long> qids);

//    @Query(value = "select count(child) from Quarters child where child.id = :cid and child.code like (select concat(par.code,'%') from Department par where par.id = :pid)")
//    int isFromDepartment(@Param("cid") long cid, @Param("pid") long pid);
}
