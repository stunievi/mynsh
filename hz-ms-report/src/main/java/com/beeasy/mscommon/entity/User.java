package com.beeasy.mscommon.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

@Table(name = "T_USER")
@OrmQuery(value = {
        @OrmCondition(target = Quarters.class, attr = "id", targetAttr = "userId", sqlId = "user.selectQuarters", type = OrmQuery.Type.MANY, lazy = true, alias = "qs"),
        @OrmCondition(target = UserProfile.class, attr = "id", targetAttr = "userId", type = OrmQuery.Type.ONE, lazy = true, alias = "profile")
})
@Getter
@Setter
public class User extends TailBean {
    Long id;
    String username;
    String password;
    String phone;
    String trueName;
    Boolean su = false;
    String accCode;
}
