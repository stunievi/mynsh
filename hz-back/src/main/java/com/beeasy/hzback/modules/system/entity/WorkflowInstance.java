package com.beeasy.hzback.modules.system.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_instance")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowInstance {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_id")
    WorkflowModel workflowModel;

    @CreatedDate
    Date addTime;

    String state;

}
