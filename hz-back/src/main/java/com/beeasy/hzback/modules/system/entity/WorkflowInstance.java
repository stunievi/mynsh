package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
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

    public enum State{
        UNRECEIVED(0),
        DEALING(1),
        CANCELED(2),
        FINISHED(3);

        private int value;
       State(int value){
            this.value = value;
       }

        public int getValue() {
            return value;
        }
    }

    @Id
    @GeneratedValue
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    WorkflowModel workflowModel;

    @CreatedDate
    Date addTime;

    @Enumerated
    State state;

    String title;
    String info;

    //任务执行人
    @JSONField(serialize = false)
    @ManyToOne(optional = true)
    User dealUser;

    //任务发布人(指派者)
    @JSONField(serialize = false)
    @ManyToOne(optional = false)
    User pubUser;

    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL)
    List<WorkflowNodeInstance> nodeList = new LinkedList<>();

    boolean finished = false;
    Date finishedDate;

    @JSONField(serialize = false)
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
        workflowNodeInstance.setNodeModel(node);
        workflowNodeInstance.setNodeName(node.getName());
        workflowNodeInstance.setInstance(this);
        workflowNodeInstance.setFinished(false);
        getNodeList().add(workflowNodeInstance);
        return workflowNodeInstance;
    }


    @Transient
    public Long getModelId(){
        return workflowModel.getId();
    }

    @Transient
    public Long getDealUserId(){
        return dealUser.getId();
    }

    @Transient
    public Long getPubUserId(){return pubUser.getId();}

//    public Long getCurrentNodeId(){
//        return getCurrentNode().getId();
//    }
}
