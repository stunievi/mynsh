package com.beeasy.hzback.modules.system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node_attribute")
public class WorkflowNodeAttribute {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "node_id")
    WorkflowNodeInstance nodeInstance;

    String attrKey;
    String attrValue;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User dealUser;
}
