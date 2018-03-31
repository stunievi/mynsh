package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
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

    public enum Type {
        MAIN_QUARTERS,
        MAIN_USER,
        SUPPORT_QUARTERS,
        SUPPORT_USER
    }

    @Id
    @GeneratedValue
    Integer id;

    String nodeName;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "workflow_id")
    WorkflowModel workflowModel;

    @Enumerated
    Type type;

    Integer uid;

}
