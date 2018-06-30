package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface IRoleDao extends JpaRepository<Role,Long>, JpaSpecificationExecutor {

    @Query(value = "select count(*) from t_user_role where role_id = :rid and user_id = :uid", nativeQuery = true)
    int hasPair(@Param("uid") long uid, @Param("rid") long rid);

    int countByNameAndIdNot(String name,Long id);

    //添加角色
    @Modifying
    @Query(value = "insert into t_user_role(user_id,role_id)values(:uid,:rid)", nativeQuery = true)
    int addUserRole(@Param("uid") long uid, @Param("rid") long rid);

    @Modifying
    @Query(value = "DELETE FROM t_user_role WHERE user_id = :uid and role_id = :rid", nativeQuery = true)
    int deleteUserRole(@Param("uid") long uid, @Param("rid") long rid);

    @Modifying
    @Query(value = "DELETE FROM t_user_role WHERE user_id = :uid", nativeQuery = true)
    int deleteUserRoles(@Param("uid") long uid);

    int countById(long id);
    int deleteById(long id);

    List<Role> findAllByIdIn(Collection<Long> ids);
}
