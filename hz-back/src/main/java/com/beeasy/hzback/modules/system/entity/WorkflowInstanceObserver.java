package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.modules.system.entity_kt.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_instance_observer")
public class WorkflowInstanceObserver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "instance_id", insertable = false, updatable = false)
    WorkflowInstance instance;
    @Column(name = "instance_id")
    Long instanceId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;
    @Column(name = "user_id")
    Long userId;
}
