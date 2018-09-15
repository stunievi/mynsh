package com.beeasy.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node_instance", indexes = {
        @Index(name = "finished", columnList = "finished")
})
@EntityListeners(AuditingEntityListener.class)
public class WorkflowNodeInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    //任务主体实例
    @JSONField(serialize = false)
    @JoinColumn(name = "instance_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    WorkflowInstance instance;

    @Column(name = "instance_id")
    Long instanceId;


    @Column(columnDefinition = JSONConverter.VARCHAR_20)
    String nodeName;

    @Enumerated
    WorkflowNode.Type type;

    //节点模型
    @JSONField(serialize = false)
    @JoinColumn(name = "node_model_id", insertable = false, updatable = false)
    @ManyToOne
    WorkflowNode nodeModel;
    @Column(name = "node_model_id")
    Long nodeModelId;


    //节点处理人
    Long dealerId;
//    @JSONField(serialize = false)
//    @ManyToOne
//    User dealer;

    //任务完成时间
    Date dealDate;

    //添加时间
    @CreatedDate
    Date addTime;

    //是否已经处理完成
    boolean finished = false;

    @OneToMany(mappedBy = "nodeInstance")
    List<WorkflowNodeAttribute> attributeList = new ArrayList<>();
    @OneToMany(mappedBy = "nodeInstance", cascade = CascadeType.REMOVE)
    List<WorkflowNodeFile> fileList = new ArrayList<>();

    //处理人列表
    @OneToMany(mappedBy = "nodeInstance", cascade = CascadeType.REMOVE)
    List<WorkflowNodeInstanceDealer> dealers = new ArrayList<>();

    //节点文件指针
    String files;

    public void setFinished(boolean finished) {
        this.finished = finished;
        if (finished) {
            setDealDate(new Date());
        } else {
            setDealDate(null);
        }
    }


    //子任务
//    @Setter
//    @Getter
//    @AllArgsConstructor
//    public static class SimpleInstance{
//        Long id;
//        String title;
//        Date addTime;
//    }

//    @JSONField(serialize = false)
//    @OneToMany(mappedBy = "parentNode")
//    List<WorkflowInstance> childInstances = new ArrayList<>();
//
//    @Transient
//    public List<SimpleInstance> getSimpleChildInstances(){
//        if(null == childInstances) return new ArrayList<>();
//        return childInstances.stream().map(item -> new SimpleInstance(item.getId(),item.getTitle(),item.getAddTime())).collect(Collectors.toList());
//    }


//    @Transient
//    public Long getDealerId(){ return dealer == null ? 0 : dealer.getId();}
//    @Transient
//    public WorkflowNode getNodeModel(){
//         return SpringContextUtils.getBean(IWorkflowNodeDao.class).findFirstByModelAndName(getInstance().getWorkflowModel(),getNodeName()).orElse(null);
//    }
}
