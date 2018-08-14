//package com.beeasy.hzback.modules.system.dao;
//
//import com.beeasy.common.entity.SystemTask;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Set;
//
//public interface ISystemTaskDao extends JpaRepository<SystemTask,Long>{
//
////    @Query("select t from SystemTask t where t.runTime <= :currentDate and t.threadLock = false order by t.id asc")
////    Page<SystemTask> findTasks(Date currentDate, Pageable pageable);
//    Page<SystemTask> findAllByRunTimeBeforeAndThreadLock(Date currentDate, boolean lock, Pageable pageable);
//
//    List<SystemTask> findAllByRunTimeBeforeAndThreadLock(Date date,boolean lock);
//
//    @Modifying
//    @Transactional
//    @Query("update SystemTask set threadLock = true, lockTime = :date where id in :ids")
//    void lockByIds(@Param("ids") Set<Long> ids, @Param("date") Date date);
//
//    void deleteAllByIdIn(Set<Long> taskIds);
//
//    @Modifying
//    @Transactional
//    @Query("delete from SystemTask where threadLock = true and lockTime < :expr")
//    void deleteFailedLock(@Param("expr") Date expr);
//
//}
