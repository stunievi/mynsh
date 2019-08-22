package com.beeasy.hzback.entity;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.math.BigDecimal;
import java.util.Date;

@Table(name="T_REPAY_ACCT_INFO")
@Data
public class RepayAcc {

    /*
    流水号
    */
    @AssignID()
    private Long id ;
    /*
    账户余额
    */
    private BigDecimal currBal ;
    /*
    客户代码
    */
    private String cusId ;
    /*
    还款账号
    */
    private String repaymentAccount ;
    /*
    应收合计
    */
    private BigDecimal totalRepay ;
    /*
    时间
    */
    private Date addTime ;

}
