package com.beeasy.mscommon.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_DEPARTMENT")
@Getter
@Setter
public class Department extends TailBean{
    Long id;
    String name = "";
    Long parentId;
    String accCode = "";
}
