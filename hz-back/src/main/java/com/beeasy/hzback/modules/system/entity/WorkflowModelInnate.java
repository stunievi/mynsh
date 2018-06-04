package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.core.helper.ObjectConverter;
import com.beeasy.hzback.modules.system.node.InputNode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_model_innate")
public class WorkflowModelInnate extends AbstractBaseEntity {
    @GeneratedValue
    @Id
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    WorkflowModel model;

    String fieldName;

    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    InputNode.Content content;

}
