package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.modules.system.service.WorkflowService;
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
    Integer id;

    String nodeName;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "workflow_id")
    WorkflowModel workflowModel;

    @Enumerated
    WorkflowService.Type type;

    Integer uid;

}
