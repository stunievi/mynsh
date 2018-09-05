package com.beeasy.hzdata.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

@Table(name = "t_quarters")
@OrmQuery(
        @OrmCondition(target = Department.class, attr = "departmentId", targetAttr = "id", type = OrmQuery.Type.ONE, lazy = true, alias = "dep")
)
@Getter
@Setter
public class Quarters extends TailBean{
    private Long id;
    private String name;
    private Boolean manager = false;
    private Long departmentId;
}
