package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 用于保存某个节点应该由谁处理, 或者已经由谁处理
 */
@Entity
@Getter
@Setter
@Table(name = "t_workflow_node_instance_dealer")
public class WorkflowNodeInstanceDealer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(value = EnumType.STRING)
    Type type;

    //授权方式
    @Enumerated(value = EnumType.STRING)
    GlobalPermission.UserType userType;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "node_instance_id", insertable = false, updatable = false)
    WorkflowNodeInstance nodeInstance;

    @Column(name = "node_instance_id")
    Long nodeInstanceId;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;

    @Column(name = "user_id")
    Long userId;

    //用户名
    String userTrueName;

    //部门ID
    Long depId;
    //部门NAME
    String depName;

    public enum Type{
        CAN_DEAL,
        DID_DEAL,
        NOT_DEAL
    }
}
