package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IGlobalPermissionDao extends JpaRepository<GlobalPermission,Long>{
    int countByTypeAndObjectIdAndUserTypeAndLinkId(GlobalPermission.Type type, long objectId, GlobalPermission.UserType userType, long linkId);

    int deleteAllByIdIn(Collection<Long> ids);

    class SQL{
        public static final String GET_UIDS = "select user.id from GlobalPermission gp, User user join user.quarters uq where gp.type in :types and gp.objectId = :oid and (" +
            //按人授权,直接取uid
            "(gp.userType = 2 and gp.linkId = user.id) or " +
            //按岗位授权,拥有这个岗位
            "(gp.userType = 1 and uq.id = gp.linkId) or " +
            //按部门授权,岗位在这个部门里
            "(gp.userType = 0 and (select count(d.id)  from Department d where d.id = gp.linkId and uq.code like concat(d.code,'_%') ) > 0 )" +
            " )";

        public static final String GET_UIDS_WITHOUT_OID = "select user.id from GlobalPermission gp, User user join user.quarters uq where gp.type in :types and (" +
            //按人授权,直接取uid
            "(gp.userType = 2 and gp.linkId = user.id) or " +
            //按岗位授权,拥有这个岗位
            "(gp.userType = 1 and uq.id = gp.linkId) or " +
            //按部门授权,岗位在这个部门里
            "(gp.userType = 0 and (select count(d.id)  from Department d where d.id = gp.linkId and uq.code like concat(d.code,'_%') ) > 0 )" +
            " )";
    }

    @Query(value = SQL.GET_UIDS)
    List getUids(@Param("types")Collection<GlobalPermission.Type> types, @Param("oid") long oid);

    @Query(value = "select count(user.id) from GlobalPermission gp, User user join user.quarters uq where gp.type in :types and gp.objectId = :oid and user.id = :uid and (" +
            //按人授权,直接取uid
            "(gp.userType = 2 and gp.linkId = user.id) or " +
            //按岗位授权,拥有这个岗位
            "(gp.userType = 1 and uq.id = gp.linkId) or " +
            //按部门授权,岗位在这个部门里
            "(gp.userType = 0 and (select count(d.id)  from Department d where d.id = gp.linkId and uq.code like concat(d.code,'_%') ) > 0 )" +
            " )")
    int hasPermission(@Param("uid") long uid, @Param("types") Collection<GlobalPermission.Type> types, @Param("oid") long oid);

    Optional<GlobalPermission> findTopByTypeAndObjectIdAndUserTypeAndLinkId(GlobalPermission.Type type, long objectId, GlobalPermission.UserType userType, long linkId);
}
