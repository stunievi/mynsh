package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.modules.system.service.IWorkflowService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_model_persons")
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowModelPersons {

    @Id
    @GeneratedValue
    Long id;

    String nodeName;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "workflow_id")
    WorkflowModel workflowModel;

    @Enumerated
    IWorkflowService.Type type;

    Long uid;

}
