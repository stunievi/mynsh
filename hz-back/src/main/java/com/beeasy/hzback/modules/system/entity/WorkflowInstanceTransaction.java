package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_instance_transaction")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowInstanceTransaction extends AbstractBaseEntity{
    @Id
    @GeneratedValue
    Long id;

    @Enumerated(value = EnumType.STRING)
    WorkflowInstance.State fromState;
    @Enumerated(value = EnumType.STRING)
    WorkflowInstance.State toState;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "instance_id", insertable = false, updatable = false)
    WorkflowInstance instance;
    @Column(name = "instance_id")
    Long instanceId;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;
    @Column(name = "user_id")
    Long userId;

    //是否已完毕
    boolean finished = false;

    @CreatedDate
    Date addTime;
}
