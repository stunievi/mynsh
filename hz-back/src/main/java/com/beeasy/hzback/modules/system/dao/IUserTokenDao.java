package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IUserTokenDao extends JpaRepository<UserToken,Long> {

    @Query(value = "select ut.userId from UserToken ut where ut.exprTime > CURRENT_TIME() and token = :token")
    List getUidFromToken(@Param("token") String token);

    @Modifying
    @Query(value = "update UserToken set exprTime = :exprTime where exprTime > current_time() and token = :token")
    int updateToken(@Param("token") String token, @Param("exprTime") Date exprTime);

    int deleteAllByExprTimeLessThan(Date date);

    @Modifying
    @Query(value = "delete from UserToken where exprTime <= current_time() ")
    int cleanTokens();

}
