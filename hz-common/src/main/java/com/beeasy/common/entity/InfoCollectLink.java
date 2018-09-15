package com.beeasy.common.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_info_collect_link")
public class InfoCollectLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "instance_id", insertable = false, updatable = false)
    WorkflowInstance instance;
    @Column(name = "instance_id")
    Long instanceId;

    @Column(length = 50)
    String loanAccount;
}
