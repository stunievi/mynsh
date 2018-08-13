//package com.beeasy.hzback.modules.system.dao;
//
//import com.beeasy.common.entity.RolePermission;
//import com.beeasy.common.entity.User;
//import com.beeasy.hzback.modules.system.service.IUserService;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface IRolePermissionDao extends JpaRepository<RolePermission,Integer>{
//    Optional<RolePermission> findFirstByUserAndType(User user, IUserService.PermissionType type);
//}
