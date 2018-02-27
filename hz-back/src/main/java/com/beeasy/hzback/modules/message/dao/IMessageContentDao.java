package com.beeasy.hzback.modules.message.dao;

import com.beeasy.hzback.modules.message.entity.Message;
import com.beeasy.hzback.modules.message.entity.MessageContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMessageContentDao extends JpaRepository<MessageContent,Integer> {
}
