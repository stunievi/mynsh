package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.modules.system.entity_kt.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_instance",indexes = {
        @Index(name = "state", columnList = "state")
})
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

    //预计开始时间
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

    String dealUserName;

    //任务发布人(指派者)
    Long pubUserId;

    //处理节点列表
    @OneToMany(mappedBy = "instance", cascade = CascadeType.REMOVE)
    List<WorkflowNodeInstance> nodeList = new LinkedList<>();

    //固有属性
    @OneToMany(mappedBy = "instance")
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
//    @JSONField(serialize = false)
//    @JoinColumn(name = "parent_node_id", insertable = false, updatable = false)
//    @ManyToOne
//    WorkflowNodeInstance parentNode;
//    @Column(name = "parent_node_id")
//    Long parentNodeId;

    //父进程ID
    @JSONField(serialize = false)
    @JoinColumn(name = "parent_id")
    @ManyToOne
    WorkflowInstance parentInstance;
    @Column(name = "parent_id", insertable = false, updatable = false)
    Long parentId;

    //父任务名
    String parentTitle;
    //父任务模型名
    String parentModelName;


    //子任务
    @JSONField(serialize = false)
    @OneToMany(mappedBy = "parentInstance")
    List<WorkflowInstance> childInstances = new ArrayList<>();

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "instance", cascade = CascadeType.REMOVE)
    List<WorkflowInstanceTransaction> transactions;

    //特殊状态
    @Transient
    boolean canDeal = false;

    //台账编号
    @Column(name = "BILL_NO")
    String billNo;

    //是否属于自动生成的任务
    boolean autoCreated = false;

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "instance")
    List<InfoCollectLink> collectLinks = new ArrayList<>();

    //任务归属部门id
    Long depId;
    //任务归属部门name
    String depName;

    @JSONField(serialize = false)
    @JoinColumn(name = "current_node_instance_id")
    @OneToOne
    WorkflowNodeInstance currentNodeInstance;

    @Column(name = "current_node_instance_id", insertable = false, updatable = false)
    Long currentNodeInstanceId;

    @Column(name = "current_node_model_id")
    Long currentNodeModelId;

    @Column(length = 20)
    String currentNodeName;

    @JSONField(serialize = false)
    @ElementCollection
    @MapKeyColumn(name = "attrKey")
    @Column(name = "attrValue")
    @CollectionTable(name = "t_workflow_instance_attribute", joinColumns = {@JoinColumn(name = "instance_id")})
    Map<String,String> attributeMap;


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

//    @Transient
//    public WorkflowNodeInstance addNode(WorkflowNode node, boolean add) {
//        WorkflowNodeInstance workflowNodeInstance = new WorkflowNodeInstance();
//        workflowNodeInstance.setNodeModelId(node.getId());
//        workflowNodeInstance.setNodeName(node.getName());
//        workflowNodeInstance.setType(node.getType());
//        workflowNodeInstance.setInstanceId(getId());
//        workflowNodeInstance.setFinished(false);
//        if(add){
//            getNodeList().add(workflowNodeInstance);
//        }
//        return workflowNodeInstance;
//    }


//    @Transient
//    public Long getDealUserId() {
//        return null == dealUser ? 0 : dealUser.getId();
//    }
//
//    @Transient
//    public long getPubUserId() {
//        return isAutoCreated() ? 0 : pubUser.getId();
//    }


//    public String getDealUserName() {
//        if(!StringUtils.isEmpty(dealUserName)){
//            return dealUserName;
//        }
//        else if(null != dealUserId){
//            dealUserName = SpringContextUtils.getBean(UserService.class).findUser(dealUserId).map(User::getTrueName).orElse("");
//            return dealUserName;
//        }
//        return "";
//    }

//    public WorkflowNodeInstance getCurrentNodeInstance(){
//        if(null == currentNodeInstance){
//            Optional<WorkflowNodeInstance> optional = SpringContextUtils.getBean(IWorkflowNodeInstanceDao.class).findFirstByInstanceAndFinishedIsFalse(this);
//            currentNodeInstance = optional.orElse(null);
//            currentNodeInstanceId = optional.map(item -> item.getId()).orElse(null);
//            currentNodeModelId = optional.map(item -> item.getNodeModelId()).orElse(null);
//        }
//        return currentNodeInstance;
//    }
//
//    public Long getCurrentNodeInstanceId() {
//        if(null == currentNodeInstanceId){
//            getCurrentNodeInstance();
//        }
//        return currentNodeInstanceId;
//    }
//
//    public Long getCurrentNodeModelId(){
//        if(null == currentNodeModelId){
//            getCurrentNodeInstance();
//        }
//        return currentNodeModelId;
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
