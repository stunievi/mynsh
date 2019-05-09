package com.beeasy.hzback.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name="T_QCC_COUNT")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QccCount {
    @AssignID("simple")
    Long id;
    int count;

    String ifNameEN;
    String ifNameCH;
    String orderID;
    String dataID;

    Date addTime;
}
