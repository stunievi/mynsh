package com.beeasy.hzback.modules.message.dao;

import com.beeasy.hzback.modules.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface IMessageDao extends JpaRepository<Message,Integer>{
}
