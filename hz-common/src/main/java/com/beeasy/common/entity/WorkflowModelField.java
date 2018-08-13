package com.beeasy.common.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;
@Getter
@Setter
@Entity
@Table(name = "t_workflow_model_field")
public class WorkflowModelField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "model_id", insertable = false, updatable = false)
    WorkflowModel workflowModel;

    @Column(name = "model_id")
    Long modelId;

    String name;
    String hint;

    @Enumerated(value = EnumType.STRING)
    Type type;

    @Column(columnDefinition = JSONConverter.type)
    @Convert(converter = JSONConverter.class)
    JSONObject ext;

    public enum Type{
        string,
        textarea,
        date,
        select;
    }

}
