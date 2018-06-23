package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_model")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowModel {
    @Id
    @GeneratedValue
    Long id;

//    @Column(columnDefinition = "BLOB")
//    @Convert(converter = ObjectConverter.class)
//    Map<String,BaseNode> model;

    @OneToMany(mappedBy = "model",cascade = CascadeType.ALL)
    List<WorkflowNode> nodeModels = new ArrayList<>();

    String name;

    BigDecimal version;

    String info;

    //任务的原始名称
    String modelName;

    boolean open;

    boolean firstOpen;

    //是否允许手动开启
    boolean manual;

    //是否允许自定义
    boolean custom;

    //是否允许发布该任务
    @Transient
    boolean pubOrPoint = false;

    @CreatedDate
    Date addTime;

    @LastModifiedDate
    Date lastModifyTime;

    //额外的权限配置
//    @OneToMany(mappedBy = "workflowModel", cascade = CascadeType.REMOVE)
//    List<WorkflowExtPermission> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "model",cascade = CascadeType.REMOVE)
    List<WorkflowModelInnate> innates = new ArrayList<>();

    @JSONField(serialize = false)
    @ManyToMany()
    @JoinTable(name = "t_workflowmodel_department",
            joinColumns = {
                    @JoinColumn(name = "MODEL_ID", referencedColumnName = "ID")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "DEPARTMENT_ID", referencedColumnName = "ID")
            }
    )
    List<Department> departments = new ArrayList<>();

    /**
     * 字段类型
     */
    public enum FieldRule{
        Null,
        NotNull,
        AssertTrue,
        AssertFalse,
        Min,
        Max,
        DecimalMin,
        DecimalMax,
        Past,
        Future,
        Pattern,
        Length,
        NotEmpty
    }

//    @OneToOne(mappedBy = "workflowModel",cascade = CascadeType.REMOVE)
//    WorkflowModelStart information;

//    @OneToMany(mappedBy = "workflowModel",cascade = CascadeType.ALL)
//    List<WorkflowModelPersons> persons = new ArrayList<>();

//    @JSONField(serialize = false)
//    @OneToMany(mappedBy = "workflowModel",cascade = CascadeType.REMOVE)
//    List<WorkflowInstance> workflowInstances = new ArrayList<>();

//    @JSONField(serialize = false)
//    @Transient
//    public BaseNode getStartNode(){
//        //start一定存在
//        return
//                getModel()
//                .entrySet()
//                .stream()
//                .filter(entry -> entry.getValue().isStart())
//                .map(stringBaseNodeEntry -> stringBaseNodeEntry.getValue())
//                .findFirst()
//                .get();
//    }


//    @JSONField(serialize = false)
//    @Transient
//    public BaseNode getEndNode(){
//        //end元素一定存在
//        return  getModel()
//                .entrySet()
//                .stream()
//                .filter(entry -> entry.getValue().isEnd())
//                .map(entry -> entry.getValue())
//                .findAny()
//                .get();
//    }

//    @JSONField(serialize = false)
//    @Transient
//    public Optional<BaseNode> findNode(String nodeName){
//        return getModel().entrySet()
//                .stream()
//                .filter(n -> n.getValue().getName().equals(nodeName))
//                .map(n -> n.getValue())
//                .findAny();
//    }


//    @Transient
//    public BaseNode getNextNode(String nodeName){
//        BaseNode node = findNode(nodeName).orElseGet(() -> getEndNode());
//        if(node.isEnd()){
//            return node;
//        }
//        //只有资料节点允许查找下一个, 其余应根据behavior走
//        if(node instanceof InputNode){
//            return findNode(node.getNext().iterator().next()).orElseGet(() -> getEndNode());
//        }
//
//        throw new RuntimeException();
//    }


//    @Getter
//    @Setter
//    public static class Persons implements Serializable{
//        private static final long serialVersionUID = 1L;
//        private LinkedHashSet<Integer> mainQuarters = new LinkedHashSet<>();
//        private LinkedHashSet<Integer> mainUser = new LinkedHashSet<>();
//        private LinkedHashSet<Integer> supportQuarters = new LinkedHashSet<>();
//        private LinkedHashSet<Integer> supportUser = new LinkedHashSet<>();
//    }
}
