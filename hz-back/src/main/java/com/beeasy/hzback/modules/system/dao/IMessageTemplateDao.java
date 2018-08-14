package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface IMessageTemplateDao extends JpaRepository<MessageTemplate, Long>, JpaSpecificationExecutor {
    int countByNameAndIdNot(String name, long id);

    int countByName(String name);
}
