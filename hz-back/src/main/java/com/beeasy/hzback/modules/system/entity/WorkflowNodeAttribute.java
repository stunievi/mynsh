package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node_attribute")
public class WorkflowNodeAttribute {

    @Id
    @GeneratedValue
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "node_id")
    WorkflowNodeInstance nodeInstance;

    String attrKey;
    String attrValue;
    String attrCname;

    boolean file = false;

    @JSONField(serialize = false)
    @ManyToOne
    User dealUser;

    @Transient
    public Long getDealerUserId(){
        return dealUser == null ? 0 : dealUser.getId();
    }

}
