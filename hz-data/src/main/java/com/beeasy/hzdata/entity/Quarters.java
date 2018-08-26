package com.beeasy.hzdata.entity;

import act.util.SimpleBean;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

@Table(name = "t_quarters")
@OrmQuery(
        @OrmCondition(target = Department.class, attr = "departmentId", targetAttr = "id", type = OrmQuery.Type.ONE, lazy = true, alias = "dep")
)
public class Quarters extends TailBean implements SimpleBean {
    @AutoID
    public Long id;
    public String name;
    public Boolean manager = false;
    public Long departmentId;
}
