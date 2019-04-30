package com.beeasy.hzback.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name = "T_WORKFLOW_TASK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTask {

    @AssignID("simple")
    Long id;

    Long   taskId;
    Long currentNodeInstanceId;
    Date remindTime;
    Date taskStartTime;
}
