package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.modules.system.node.BaseNode;
import com.beeasy.hzback.modules.system.node.EndNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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
    public BaseNode getStartNode(){
        //start一定存在
        return getWorkflowModel()
                .getModel()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isStart())
                .map(stringBaseNodeEntry -> stringBaseNodeEntry.getValue())
                .findFirst()
                .get();
    }

    @Transient
    public BaseNode getEndNode(){
        //end元素一定存在
        return (EndNode) getWorkflowModel().getModel()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isEnd())
                .map(entry -> entry.getValue())
                .findAny()
                .get();
    }

    @Transient
    public BaseNode getNextNode(){
        WorkflowNodeInstance nodeInstance = (getCurrentNode());
//        if(nodeInstance)
        //暂时先用order排序的算
        //过滤掉开始和结束节点
        List<BaseNode> list = getWorkflowModel()
                .getModel()
                .entrySet()
                .stream()
                .map(item -> item.getValue())
                .filter(item -> !item.isStart() && !item.isEnd())
                .collect(Collectors.toList());
        //如果当前节点是开始节点
        if(nodeInstance.getNodeModel().isStart()){
            return list.get(0);
        }
        Collections.sort(list,Comparator.comparingInt(BaseNode::getOrder));
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getName().equals(nodeInstance.getNodeName())){
                return i >= list.size() - 1 ? getEndNode() : list.get(i);
            }
        }
        return getEndNode();
    }
}
