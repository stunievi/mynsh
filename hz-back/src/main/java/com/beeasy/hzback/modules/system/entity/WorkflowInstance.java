package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_instance")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowInstance {

    public enum State {
        //公共任务
        COMMON,
        //待指派
        UNRECEIVED,
        //处理中
        DEALING,
        //已取消
        CANCELED,
        //已完成
        FINISHED,
        //暂停中
        PAUSE;
    }
    public enum SecondState{
        POINT,
        TRANSFORM
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JSONField(serialize = false)
    @JoinColumn(name = "workflow_model_id", insertable = false, updatable = false)
    @ManyToOne
    WorkflowModel workflowModel;
    @Column(name = "workflow_model_id")
    Long modelId;

    //任务创建日期
    @CreatedDate
    Date addTime;

    Date planStartTime;

    //任务状态
    @Enumerated(value = EnumType.STRING)
    State state;

    @Enumerated(value = EnumType.STRING)
    SecondState secondState;

    //原型是否公共任务
//    boolean common = false;

    //任务标题
    String title;

    //任务内容说明
    String info;

    //工作流模型名称
    String modelName;

    //任务执行人
    @JSONField(serialize = false)
    @JoinColumn(name = "deal_user_id", insertable = false, updatable = false)
    @ManyToOne
    User dealUser;
    @Column(name = "deal_user_id")
    Long dealUserId;

    //任务发布人(指派者)
    Long pubUserId;

    //处理节点列表
    @OneToMany(mappedBy = "instance", cascade = CascadeType.REMOVE)
    List<WorkflowNodeInstance> nodeList = new LinkedList<>();

    //固有属性
    @OneToMany(mappedBy = "instance", cascade = CascadeType.REMOVE)
    List<WorkflowInstanceAttribute> attributes = new ArrayList<>();

    //任务完成日期
    Date finishedDate;


    //前置任务ID
//    Long prevTaskId;
    @JSONField(serialize = false)
    @JoinColumn(name = "prev_instance_id",insertable = false,updatable = false)
    @OneToOne
    WorkflowInstance prevInstance;

    @Column(name = "prev_instance_id")
    Long prevInstanceId;

    @JSONField(serialize = false)
    @OneToOne(mappedBy = "prevInstance")
    WorkflowInstance nextInstance;

    //父进程节点ID (此节点开启的子任务)
    @JSONField(serialize = false)
    @JoinColumn(name = "parent_node_id", insertable = false, updatable = false)
    @ManyToOne
    WorkflowNodeInstance parentNode;
    @Column(name = "parent_node_id")
    Long parentNodeId;


    @JSONField(serialize = false)
    @OneToMany(mappedBy = "instance", cascade = CascadeType.REMOVE)
    List<WorkflowInstanceTransaction> transactions;

    //特殊状态
    boolean canDeal = false;
    //台账编号

    @Column(name = "BILL_NO")
    String billNo;

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "instance")
    List<InfoCollectLink> collectLinks = new ArrayList<>();

//    @JSONField(serialize = false)
//    @Transient
//    public WorkflowNodeInstance getCurrentNode() {
//        //选择当前最后一个不为空的节点返回
//        Optional<WorkflowNodeInstance> currentNode = getNodeList()
//                .stream()
//                .filter(n -> !n.isFinished())
//                .findAny();
//        return currentNode.orElse(getNodeList().get(getNodeList().size() - 1));
//    }

    @Transient
    public WorkflowNodeInstance addNode(WorkflowNode node, boolean add) {
        WorkflowNodeInstance workflowNodeInstance = new WorkflowNodeInstance();
        workflowNodeInstance.setNodeModelId(node.getId());
        workflowNodeInstance.setNodeName(node.getName());
        workflowNodeInstance.setType(node.getType());
        workflowNodeInstance.setInstanceId(getId());
        workflowNodeInstance.setFinished(false);
        if(add){
            getNodeList().add(workflowNodeInstance);
        }
        return workflowNodeInstance;
    }


//    @Transient
//    public Long getDealUserId() {
//        return null == dealUser ? 0 : dealUser.getId();
//    }
//
//    @Transient
//    public long getPubUserId() {
//        return isAutoCreated() ? 0 : pubUser.getId();
//    }

    @Transient
    public long getNextInstanceId(){
        return null == nextInstance ? 0 : nextInstance.getId();
    }
    @Transient
    public String getNextInstanceTitle(){
        return null == nextInstance ? "" : nextInstance.getTitle();
    }
//
//    public void setPrevInstance(WorkflowInstance prevInstance) {
//        this.prevInstance = prevInstance;
//    }

    /**
     * 是否自动创建的任务
     *
     * @return
     */
    @Transient
    public boolean isAutoCreated() {
        return null == pubUserId ? true : false;
    }

}
