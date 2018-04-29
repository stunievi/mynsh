package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.MessageRead;
import com.beeasy.hzback.modules.system.entity.MessageSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IMessageReadDao extends JpaRepository<MessageRead,Long>{
    Optional<MessageRead> findFirstBySessionAndUser_Id(MessageSession session, long userId);
}
