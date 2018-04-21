package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.modules.system.node.BaseNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL)
    List<WorkflowNodeInstance> nodeList = new LinkedList<>();

    boolean finished = false;
    Date finishedDate;

    @Transient
    public WorkflowNodeInstance getCurrentNode(){
        //选择当前最后一个不为空的节点返回
        Optional<WorkflowNodeInstance> currentNode = getNodeList()
                .stream()
                .filter(n -> !n.isFinished())
                .findAny();
        return currentNode.orElse(getNodeList().get(getNodeList().size() - 1));
    }

    @Transient
    public WorkflowNodeInstance addNode(WorkflowNode node){
        WorkflowNodeInstance workflowNodeInstance = new WorkflowNodeInstance();
        workflowNodeInstance.setNodeName(node.getName());
        workflowNodeInstance.setInstance(this);
        workflowNodeInstance.setFinished(false);
        getNodeList().add(workflowNodeInstance);
        return workflowNodeInstance;
    }


}
