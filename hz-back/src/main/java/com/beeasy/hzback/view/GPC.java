package com.beeasy.hzback.view;

import com.beeasy.hzback.entity.GP;
import com.beeasy.hzback.entity.Org;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_GLOBAL_PERMISSION_CENTER")
@Getter
@Setter
public class GPC extends TailBean {
    Long id;
    Long uid;
    String uname;
    String utname;
//    Long did;
//    String dname;
//
//    Long qid;
//    String qname;
//
//    Long rid;
//    String rname;

    Long objectId;

    Org.Type otype;
    Long oid;
    String oname;
    Long pid;
    String pname;
    String k1;

    GP.Type type;
    GP.UserType userType;

    String description;
}
