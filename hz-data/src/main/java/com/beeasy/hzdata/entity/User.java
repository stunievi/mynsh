package com.beeasy.hzdata.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

@Table(name = "T_USER")
@OrmQuery(value = {
       @OrmCondition(target = Quarters.class, attr = "id", targetAttr = "userId", sqlId = "user.selectQuarters", type = OrmQuery.Type.MANY, lazy = true, alias = "qs")
})
@Getter
@Setter
public class User extends TailBean {
    private Long id;
    private String username;
    private String password;
    private Boolean su = false;
    private String accCode = "";

}
