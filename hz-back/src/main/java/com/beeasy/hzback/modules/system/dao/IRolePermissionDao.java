//package com.beeasy.hzback.modules.system.dao;
//
//import com.beeasy.hzback.modules.system.entity.RolePermission;
//import com.beeasy.hzback.modules.system.entity_kt.User;
//import com.beeasy.hzback.modules.system.service.IUserService;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface IRolePermissionDao extends JpaRepository<RolePermission,Integer>{
//    Optional<RolePermission> findFirstByUserAndType(User user, IUserService.PermissionType type);
//}
