package com.beeasy.hzback.view;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_DEPARTMENT_USER")
@Getter
@Setter
public class DepartmentUser {
    Long uid;
    Long did;
    String trueName;
    String dname;
    String avatar;
    Long qid;
    String qname;
}
