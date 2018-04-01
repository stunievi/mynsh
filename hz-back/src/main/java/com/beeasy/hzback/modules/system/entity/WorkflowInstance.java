package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_instance")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowInstance {

    @Id
    @GeneratedValue
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "workflow_id")
    WorkflowModel workflowModel;

    @CreatedDate
    Date addTime;

    String state;

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "instance")
    List<WorkflowNodeInstance> nodeList = new LinkedList<>();

}
