package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IDepartmentDao extends JpaRepository<Department,Long> {
    List<Department> findAllByParent(Department department);
    List<Department> findAllByParent_Id(Long parentId);

    Department findByParent(Department department);
    Optional<Department> findFirstByParentAndName(Department department, String name);
    Optional<Department> findTopByParentIdAndName(final long departmentId, final String name);
    int countByParentAndName(Department department, String name);
    int countByParentIdAndName(final long did, String name);
    int countByParentIdAndNameAndIdNot(final Long pid, final String name, final long id);

    List<Department> findAllByName(String name);

    int deleteAllByIdIn(Collection<Long> dids);

    @Query(value = "select d.code from Department d where d.id = :id")
    List getDepartmentCode(@Param("id") long id);

    @Query(value = "select d.code from Department d where d.parentId = :parentId order by d.code desc")
    List findLastCode(@Param("parentId") Long parentId);


    @Query(value = "select d.code from Department d where d.parent is null order by d.code desc")
    List findTopLastCode();


//    @Query(value = "select d,(select dd.parentId from Department dd where dd.id = :did) as pid from Department d where d.name = :name and d.id <> :did group by d " +
//            "having pid > 0" + "")
////            "   case when pid is null and d.parentId is null then true " +
////            "        when pid is not null and d.parentId = pid then true" +
////            "   else false " +
////            "   end ")
//    List getSameDepartments(@Param("did") final long did, @Param("name") final String name);

    //是否是子部门
    @Query(value = "select count(par) from Department par where ( select count(child) from Department child where par.code = substring(child.code,1,length(par.code)) and child.id = :cid and child.code <> par.code) > 0 and par.id = :pid")
    int departmentHasChild(@Param("pid") long pid, @Param("cid") long cid);

    //是否是子岗位
    @Query(value = "select count(par) from Department par where ( select count(child) from Quarters child where par.code = substring(child.code, 1, length(par.code)) and child.id = :cid and child.code <> par.code) > 0 and par.id = :pid")
    int departmentHasQuarters(@Param("pid") long pid, @Param("cid") long cid);

    //得到该部门的所有下属子岗位ID
    @Query(value = "select child.id from Quarters child where (select count(par) from Department par where par.code = substring(child.code, 1, length(par.code)) and par.id = :did) > 0 ")
    List getChildQuartersIds(@Param("did") long did);


    //得到某些部门所有下属子部门(包括自身)
    @Query(value = "select distinct d.id from Department d where (select count(dd) from Department dd where d.code like concat(dd.code,'%') and dd.id = :did) > 0")
    List<Long> getChildDepIds(@Param("did") long did);
    @Query(value = "select distinct d from Department d where (select count (dd) from Department dd where d.code like concat(dd.code,'%') and dd.id = :did ) > 0")
    List<Department> getChildDeps(@Param("did") final long did);
}
