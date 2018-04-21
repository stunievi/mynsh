package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.system.dao.IWorkflowNodeDao;
import com.beeasy.hzback.modules.system.node.BaseNode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node_instance")
public class WorkflowNodeInstance {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instance_id")
    WorkflowInstance instance;

    String nodeName;

    //节点类型
    String type;

    Date dealDate;

    //是否已经处理完成
    boolean finished = false;

    @OneToMany(mappedBy = "nodeInstance",cascade = CascadeType.ALL)
    Set<WorkflowNodeAttribute> attributeList = new LinkedHashSet<>();

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
    public WorkflowNode getNodeModel(){
         return SpringContextUtils.getBean(IWorkflowNodeDao.class).findFirstByModelAndName(getInstance().getWorkflowModel(),getNodeName()).orElse(null);
    }
}
