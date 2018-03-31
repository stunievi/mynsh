package com.beeasy.hzback.modules.system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node_instance")
public class WorkflowNodeInstance {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "instance_id")
    WorkflowInstance instance;

    String nodeName;

    Date dealDate;

    //是否已经处理完成
    boolean finished = false;
}
