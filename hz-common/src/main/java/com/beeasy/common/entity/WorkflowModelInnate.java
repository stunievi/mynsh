package com.beeasy.common.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.AbstractBaseEntity;
import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;
@Getter
@Setter
@Entity
@Table(name = "t_workflow_model_innate")
public class WorkflowModelInnate extends AbstractBaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    WorkflowModel model;

    String fieldName;

    @Column(columnDefinition = JSONConverter.type)
    @Convert(converter = JSONConverter.class)
    JSONObject content;

}
