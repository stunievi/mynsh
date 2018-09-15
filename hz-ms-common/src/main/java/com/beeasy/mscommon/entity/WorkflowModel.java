package com.beeasy.mscommon.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_WORKFLOW_MODEL")
@Getter
@Setter
public class WorkflowModel {
    Long id;

    String name;
    Boolean open;
    String modelName;

}
