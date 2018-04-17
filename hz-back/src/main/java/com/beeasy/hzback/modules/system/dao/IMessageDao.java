package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Message;
import com.beeasy.hzback.modules.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

public interface IMessageDao extends JpaRepository<Message,Long>, JpaSpecificationExecutor  {
    void readMessagesByFromUserAndIdIn(User user, Set<Long> ids);
}
