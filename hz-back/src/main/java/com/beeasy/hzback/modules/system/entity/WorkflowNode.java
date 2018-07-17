package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.core.helper.JSONConverter;
import com.beeasy.hzback.core.helper.ObjectConverter;
import com.beeasy.hzback.core.helper.StringCommaConverter;
import com.beeasy.hzback.modules.system.node.BaseNode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node")
public class WorkflowNode extends AbstractBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    WorkflowModel model;

    @Column(columnDefinition = JSONConverter.type)
    @Convert(converter = JSONConverter.class)
    JSONObject node;

//    @OneToMany(mappedBy = "workflowNode",cascade = CascadeType.ALL)
//    List<WorkflowModelPersons> persons = new ArrayList<>();

    String name;

    @Enumerated
    Type type;

    @Convert(converter = StringCommaConverter.class)
    List<String> next = new ArrayList<>();

    //允许使用的子流程
//    @Convert(converter = StringCommaConverter.class)
//    List<String> allowChildTask = new ArrayList<>();

    boolean start = false;
    boolean end = false;

    int processCycle = 1;
    //节点最大处理人数
    int maxPerson = 1;

    public enum Type{
        input,
        check,
        logic,
        end
    }

}
