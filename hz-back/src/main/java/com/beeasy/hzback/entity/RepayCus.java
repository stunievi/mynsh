package com.beeasy.hzback.entity;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.math.BigDecimal;
import java.util.Date;

@Table(name="T_REPAY_CUS_LIST")
@Data
public class RepayCus {

    /*
    流水号
    */
    @AssignID()
    private Long id ;
    /*
    证件号码
    */
    private String certCode ;
    /*
    证件类型
    */
    private String certType ;
    /*
    客户代码
    */
    private String cusId ;
    /*
    户名
    */
    private String cusName ;
    /*
    联系电话
    */
    private String phone ;
    /*
    行内存款账户总余额
    */
    private BigDecimal totalBal ;
    /*
    本月应收
    */
    private BigDecimal totalRepay ;
    /*
    时间
    */
    private Date addTime ;
    /*
    * 还款账户余额足够标志	CHAR(1)		0-非主键	1-空	1：足够；0：不足；
     * */
    private int enoughRepay;

}
