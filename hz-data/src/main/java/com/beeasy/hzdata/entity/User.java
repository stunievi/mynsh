package com.beeasy.hzdata.entity;

import act.util.SimpleBean;
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
public class User extends TailBean implements SimpleBean {
    @AutoID
    public Long id;
    public String username;
    public String password;
    public Boolean su = false;
    public String accCode = "";

    public interface Mapper extends BaseMapper<User> {
        User findById(@Param("id") long id);
    }

}
