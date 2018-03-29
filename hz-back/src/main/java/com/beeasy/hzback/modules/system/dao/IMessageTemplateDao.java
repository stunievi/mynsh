package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMessageTemplateDao extends JpaRepository<MessageTemplate,Integer>{
    Page<List<MessageTemplate>> findAllByName(String name, Pageable pageable);
}
