package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IUserTokenDao extends JpaRepository<UserToken,Long> {

    @Query(value = "select ut.userId from UserToken ut where ut.exprTime > :now and token = :token")
    List getUidFromToken(@Param("token") String token,@Param("now") Date date);

    @Modifying
    @Query(value = "update UserToken set exprTime = :exprTime where exprTime > :now and token = :token")
    int updateToken(@Param("token") String token, @Param("exprTime") Date exprTime, @Param("now") Date now);

    int deleteAllByExprTimeLessThan(Date date);


    Optional<UserToken> findTopByUserIdAndType(final long uid, UserToken.Type type);

}
