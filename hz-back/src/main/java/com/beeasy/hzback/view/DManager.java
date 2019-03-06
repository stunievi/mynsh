package com.beeasy.hzback.view;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_DEPARTMENT_MANAGER")
@Getter
@Setter
public class DManager {
    Long id;
    Long uid;
    String utname;
    String oname;
}
