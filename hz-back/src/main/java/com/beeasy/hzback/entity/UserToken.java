package com.beeasy.hzback.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

import java.util.Date;

@Table(name = "T_USER_TOKEN")
@OrmQuery(
        value = {
                @OrmCondition(target=User.class,attr="userId",targetAttr="id",type=OrmQuery.Type.ONE,lazy=true),
        }
)
@Getter
@Setter
public class UserToken extends TailBean{
    @AssignID("simple")
    private Long id;

    private Long userId;
    private String token;
    private Date exprTime;
    private Type type;

    public enum Type {
        WEB,
        ANDROID
    }

}
