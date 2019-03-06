package com.beeasy.hzback.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_WF_INS_DEALER")
@Getter
@Setter
public class WfNodeDealer  {
    @AssignID("simple")
    Long id;
    Type type;
//    GP.UserType userType;
    Long insId;
    Long nodeId;
    String nodeName;

    //用户名
    Long uid;
    String uname;
    String utname;

//    Org.Type otype;
    Long oid;
    String oname;

    Date lastModify;

    //部门ID
//    Long depId;
//    //部门NAME
//    String depName;
//    //岗位id
//    Long quartersId;
//    //岗位name
//    String quartersName;

    //角色ID
//    Long roleId;
    //角色name
//    String roleName;

    public enum Type {
        CAN_DEAL,
        DID_DEAL,
        NOT_DEAL,
        OVER_DEAL
    }
}
