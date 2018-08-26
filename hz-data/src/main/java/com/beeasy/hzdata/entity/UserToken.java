package com.beeasy.hzdata.entity;

import act.util.SimpleBean;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.DateTemplate;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

import java.util.Date;

@Table(name = "T_USER_TOKEN")
@OrmQuery(
        value = {
                @OrmCondition(target=User.class,attr="userId",targetAttr="id",type=OrmQuery.Type.ONE,lazy=true),
        }
)
public class UserToken extends TailBean implements SimpleBean {
    public Long id;
    public Long userId;
    public String token;
    public Date exprTime;

    public interface Mapper extends BaseMapper<UserToken>{
    }
}
