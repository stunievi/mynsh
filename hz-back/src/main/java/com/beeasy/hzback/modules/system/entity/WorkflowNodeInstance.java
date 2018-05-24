package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node_instance")
public class WorkflowNodeInstance {

    @Id
    @GeneratedValue
    Long id;

    //任务主体实例
    @JSONField(serialize = false)
    @ManyToOne(optional = false)
    WorkflowInstance instance;

    String nodeName;

    //节点模型
    @JSONField(serialize = false)
    @ManyToOne
    WorkflowNode nodeModel;

    //节点处理人
    @JSONField(serialize = false)
    @ManyToOne
    User dealer;

    //任务完成时间
    Date dealDate;

    //是否已经处理完成
    boolean finished = false;

    @OneToMany(mappedBy = "nodeInstance",cascade = CascadeType.ALL)
    List<WorkflowNodeAttribute> attributeList = new ArrayList<>();
    @OneToMany(mappedBy = "nodeInstance",cascade = CascadeType.REMOVE)
    List<WorkflowNodeFile> fileList = new ArrayList<>();

    public void setFinished(boolean finished) {
        this.finished = finished;
        if(finished){
            setDealDate(new Date());
        }
        else{
            setDealDate(null);
        }
    }

    @Transient
    public String getNodeName(){
        return getNodeModel().getName();
    }

    @Transient
    public Long getNodeModelId(){
        return nodeModel == null ? 0 : getNodeModel().getId();
    }

    @Transient
    public Long getDealerId(){ return dealer == null ? 0 : dealer.getId();}
//    @Transient
//    public WorkflowNode getNodeModel(){
//         return SpringContextUtils.getBean(IWorkflowNodeDao.class).findFirstByModelAndName(getInstance().getWorkflowModel(),getNodeName()).orElse(null);
//    }
}
