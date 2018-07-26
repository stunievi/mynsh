//package com.beeasy.hzback.modules.system.dao;
//
//import com.beeasy.hzback.modules.system.entity.GlobalPermission;
//import com.beeasy.hzback.modules.system.entity.GlobalPermissionCenter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collection;
//
//public interface IGlobalPermissionCenterDao extends JpaRepository<GlobalPermissionCenter,Long>{
////    int deleteAllByPermissionIdIn(Collection<Long> pids);
//
//    @Transactional
//    @Modifying
//    @Query(value = "delete from GlobalPermissionCenter where permissionId in :ids")
//    int deleteAllByPermissionIdIn(@Param("ids") Collection<Long> ids);
//
//
//    @Query(value = "select count(center) from GlobalPermissionCenter center where center.permission.type = :type and center.permission.objectId = :objectId and center.userId = :uid")
//    int checkPermission(@Param("type") GlobalPermission.Type type, @Param("objectId") long objectId, @Param("uid") long uid);
//}
