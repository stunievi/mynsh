package com.beeasy.hzdata.entity;

import act.util.SimpleBean;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_DEPARTMENT")
public class Department extends TailBean implements SimpleBean {
    @AutoID
    public Long id;
    public String name = "";
    public Long parentId;
    public String accCode = "";
}
