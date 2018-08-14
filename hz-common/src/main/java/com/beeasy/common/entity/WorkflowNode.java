package com.beeasy.common.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.AbstractBaseEntity;
import com.beeasy.common.helper.JSONConverter;
import com.beeasy.common.helper.StringCommaConverter;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node")
public class WorkflowNode extends AbstractBaseEntity {
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

    @Column(length = 20)
    @Enumerated(value = EnumType.STRING)
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

    public enum Type {
        input,
        check,
        logic,
        end
    }

}
