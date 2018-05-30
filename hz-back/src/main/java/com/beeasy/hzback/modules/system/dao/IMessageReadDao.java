package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.MessageRead;
import com.beeasy.hzback.modules.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IMessageReadDao extends JpaRepository<MessageRead,Long>{
//    Optional<MessageRead> findFirstBySessionAndUser_Id(MessageSession session, long userId);
    @Query(value = "select r from MessageRead r where r.user = :user and r.toType = 0 and r.toId = :toId")
    Optional<MessageRead> getUser2UserRead(@Param("user") User user, @Param("toId") long toId);

    @Query(value = "select r from MessageRead r where r.user = :user and r.toType = 0 and r.toId in :toIds")
    List<MessageRead> getUser2UsersRead(@Param("user") User user, @Param("toIds") Long[] toIds);
}
