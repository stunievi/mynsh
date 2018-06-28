package com.beeasy.hzback.modules.system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_info_collect_link")
public class InfoCollectLink {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "instance_id", insertable = false, updatable = false)
    WorkflowInstance instance;
    @Column(name = "instance_id")
    Long instanceId;

    String billNo;
}
