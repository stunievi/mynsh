package com.beeasy.hzback.modules.workflow.entity;

import com.beeasy.hzback.modules.setting.entity.WorkFlow;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 工作流实体，具体实例化后的工作流，需要一个版本的引用
 */
@Getter
@Setter
@Entity
@Table(name = "t_workflow_entity")
@EntityListeners(AuditingEntityListener.class)
public class WorkFlowEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * 工作流源，所有的模型根据这个进行变动
     */
    @OneToOne()
    @JoinColumn(name = "workflow_id")
    private WorkFlow workFlow;


    /**
     * 当前进行到哪一步
     * 如果以后出现事件分支，那么这里不能再单独存储某一步
     */
    private Integer currentStep;


    @CreatedDate
    private Date addTime;




}
