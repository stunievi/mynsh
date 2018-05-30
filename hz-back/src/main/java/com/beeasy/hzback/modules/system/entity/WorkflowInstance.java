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

    public enum State {
        UNRECEIVED(0, "公共任务"),
        DEALING(1, "处理中"),
        CANCELED(2, "已取消"),
        FINISHED(3, "已完成");

        private int value;
        private String str;

        State(int value, String str) {
            this.value = value;
            this.str = str;
        }

        public int getValue() {
            return value;
        }

        public String toString() {
            return str;
        }
    }

    @Id
    @GeneratedValue
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    WorkflowModel workflowModel;

    //任务创建日期
    @CreatedDate
    Date addTime;

    //任务状态
    @Enumerated
    State state;

    //任务标题
    String title;

    //任务内容说明
    String info;

    //任务执行人
    @JSONField(serialize = false)
    @ManyToOne(optional = true)
    User dealUser;

    //任务发布人(指派者)
    @JSONField(serialize = false)
    @ManyToOne(optional = false)
    User pubUser;

    //处理节点列表
    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL)
    List<WorkflowNodeInstance> nodeList = new LinkedList<>();

    //任务完成日期
    Date finishedDate;

    @JSONField(serialize = false)
    @Transient
    public WorkflowNodeInstance getCurrentNode() {
        //选择当前最后一个不为空的节点返回
        Optional<WorkflowNodeInstance> currentNode = getNodeList()
                .stream()
                .filter(n -> !n.isFinished())
                .findAny();
        return currentNode.orElse(getNodeList().get(getNodeList().size() - 1));
    }

    @Transient
    public WorkflowNodeInstance addNode(WorkflowNode node) {
        WorkflowNodeInstance workflowNodeInstance = new WorkflowNodeInstance();
        workflowNodeInstance.setNodeModel(node);
        workflowNodeInstance.setNodeName(node.getName());
        workflowNodeInstance.setInstance(this);
        workflowNodeInstance.setFinished(false);
        getNodeList().add(workflowNodeInstance);
        return workflowNodeInstance;
    }


    @Transient
    public Long getModelId() {
        return workflowModel.getId();
    }

    @Transient
    public Long getDealUserId() {
        return isCommon() ? 0 : dealUser.getId();
    }

    @Transient
    public long getPubUserId() {
        return isAutoCreated() ? 0 : pubUser.getId();
    }

    /**
     * 是否自动创建的任务
     *
     * @return
     */
    @Transient
    public boolean isAutoCreated() {
        return null == pubUser ? true : false;
    }

    /**
     * 是否公共任务
     *
     * @return
     */
    @Transient
    public boolean isCommon() {
        return state == State.UNRECEIVED && null == dealUser;
    }
}
