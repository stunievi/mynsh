package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.core.helper.ObjectConverter;
import com.beeasy.hzback.core.helper.StringCommaConverter;
import com.beeasy.hzback.modules.system.node.BaseNode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node")
public class WorkflowNode extends AbstractBaseEntity{
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    WorkflowModel model;

    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    BaseNode node;

    String name;
    String type;

    @Convert(converter = StringCommaConverter.class)
    Set<String> next = new LinkedHashSet<>();

    boolean start = false;
    boolean end = false;

}
