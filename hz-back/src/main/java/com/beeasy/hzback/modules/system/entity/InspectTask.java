package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.service.IWorkflowService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "t_inspect_task")
@EntityListeners(AuditingEntityListener.class)
public class InspectTask {
    @Id
    @GeneratedValue
    Long id;

    //流程的内置模型名, 默认以最新的版本来
    String modelName;

    @CreatedDate
    Date addTime;

    Date acceptDate;


    //是自动生成的还是手动生成的任务
    @Enumerated
    IWorkflowService.InspectTaskType type;

    @Enumerated
    IWorkflowService.InspectTaskState state;

    //处理的用户,可以为空, 为空的情况这条任务发布为公共任务
    @ManyToOne()
    @JoinColumn(name = "deal_user_id")
    User dealUser;

    //关联的检查任务
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "workflow_instance_id")
    WorkflowInstance instance;

}
