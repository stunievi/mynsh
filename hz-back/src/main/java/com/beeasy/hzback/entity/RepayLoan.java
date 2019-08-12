package com.beeasy.hzback.entity;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.math.BigDecimal;
import java.util.Date;

@Table(name="T_LOAN_ACCT_INFO")
@Data
public class RepayLoan {

    /*
    流水号
    */
    @AssignID()
    private Long id ;
    /*
    核心产品号
    */
    private String accountClass ;
    /*
    核心产品号(前4位）
    */
    private String accountClass4 ;
    /*
    实际追加核准金额
    */
    private BigDecimal actlAddapprvAmt ;
    /*
    产品代码
    */
    private String actType ;
    /*
    应收合计
    */
    private BigDecimal totalRepay ;
    /*
    应收利息
    */
    private BigDecimal unpdInt ;
    /*
    应收本金
    */
    private BigDecimal unpdCap ;
    /*
    合同号
    */
    private String contNo ;
    /*
    客户代码
    */
    private String cusId ;
    /*
    户名
    */
    private String cusName ;
    /*
    理论上本期应还款额,包括要还的本金和利息
    */
    private BigDecimal dueAmt ;
    /*
    利息计提，每日增加一个增量，结息清零
    */
    private BigDecimal intAccr ;
    /*
    利息增量
    */
    private BigDecimal intIncr ;
    /*
    利息还款频率
    */
    private String intRepayFreq ;
    /*
    贷款账号
    */
    private String loanAccount ;
    /*
    贷款账号（前16位）
    */
    private String loanAccount16 ;
    /*
    本期应该还的本金，
    */
    private BigDecimal loanRepay ;
    /*
    下次还款日
    */
    private String nextDueDate ;
    /*
    还款账号
    */
    private String repaymentAccount ;
    /*
    还款日
    */
    private String repayDay ;
    /*
    还款周期
    */
    private String repayFreq ;
    /*
    还款方式
    */
    private String repaySched ;
    /*
    源系统日期
    */
    private String srcSysDate ;
    /*
    状态
    */
    private String stat ;
    /*
    时间
    */
    private Date addTime ;


}
