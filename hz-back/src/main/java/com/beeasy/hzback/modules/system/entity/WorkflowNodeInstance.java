package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Column(name = "node_model_id", insertable = false, updatable = false)
    Long nodeModelId;


    //节点处理人
    Long dealerId;
//    @JSONField(serialize = false)
//    @ManyToOne
//    User dealer;

    //任务完成时间
    Date dealDate;

    //是否已经处理完成
    boolean finished = false;

    @OneToMany(mappedBy = "nodeInstance",cascade = CascadeType.REMOVE)
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

    @JSONField(serialize = false)
    @Transient
    public String getNodeName(){
        return getNodeModel().getName();
    }



    //子任务
    @Setter
    @Getter
    @AllArgsConstructor
    public static class SimpleInstance{
        Long id;
        String title;
        Date addTime;
    }

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "parentNode")
    List<WorkflowInstance> childInstances = new ArrayList<>();

    @Transient
    public List<SimpleInstance> getSimpleChildInstances(){
        if(null == childInstances) return new ArrayList<>();
        return childInstances.stream().map(item -> new SimpleInstance(item.getId(),item.getTitle(),item.getAddTime())).collect(Collectors.toList());
    }


//    @Transient
//    public Long getDealerId(){ return dealer == null ? 0 : dealer.getId();}
//    @Transient
//    public WorkflowNode getNodeModel(){
//         return SpringContextUtils.getBean(IWorkflowNodeDao.class).findFirstByModelAndName(getInstance().getWorkflowModel(),getNodeName()).orElse(null);
//    }
}
