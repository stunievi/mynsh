package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IMessageTemplateDao extends JpaRepository<MessageTemplate,Long>{
    int deleteById(long id);
    Optional<MessageTemplate> findTopById(long id);
}
