package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_instance_attribute")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowInstanceAttribute extends AbstractBaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "instance_id", insertable = false, updatable = false)
    WorkflowInstance instance;

    @Column(name = "instance_id")
    Long instanceId;

    @Enumerated(value = EnumType.STRING)
    Type type;

    String attrKey;
    String attrValue;
    String attrCName;

    @LastModifiedDate
    Date lastModifyDate;

    public enum Type{
        //固有
        INNATE,
        //节点属性
        NODE;
    }

}
