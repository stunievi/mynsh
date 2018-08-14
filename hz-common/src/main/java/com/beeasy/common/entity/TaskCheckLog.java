package com.beeasy.common.entity;

import com.beeasy.common.helper.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "t_task_check_log")
@EntityListeners(AuditingEntityListener.class)
public class TaskCheckLog extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated
    Type type;

    Long linkId;

    @CreatedDate
    Date lastCheckDate;

    public enum Type {
        LOGIC_NODE
    }
}
