package com.beeasy.hzback.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.RepayAcc;
import com.beeasy.hzback.entity.RepayCus;
import com.beeasy.hzback.entity.RepayLoan;
import com.beeasy.hzback.modules.system.util.DateUtil;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// 还款
@RestController
@RequestMapping("/api/repayment")
public class RepaymentController {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() throws ParseException {
        manageRepayData();


        List<RepayCus> cusList = sqlManager.select("accloan.repay_cus_arr", RepayCus.class);
        for(RepayCus cusInfo : cusList){
            String cusId = cusInfo.getCusId();
            // 客户待还款总额
            BigDecimal cusRepayTotal = new BigDecimal(0);
            // 行内存款账户总余额
            BigDecimal cusBalTotal = cusInfo.getTotalBal();
            // 筛选出还款账户
            List<RepayAcc> accountList = sqlManager.select("accloan.repay_acct_info", RepayAcc.class, C.newMap(
                    "cusId", cusId
            ));
            for(RepayAcc account : accountList){
                // 还款账户待还款总额
                BigDecimal accRepayTotal = new BigDecimal(0);
                // 账户余额
                BigDecimal CURR_BAL = account.getCurrBal();
                cusBalTotal.add(CURR_BAL);
                // 账户关联台账
                List<RepayLoan> accLoanList = sqlManager.select("accloan.repay_loan_acct_info", RepayLoan.class, C.newMap(
                        "repaymentAccount", account.getRepaymentAccount()
                ));
                for(RepayLoan accLoan : accLoanList){
                    // 本期台账需还款总额
                    BigDecimal loanRepayTotal;
                    BigDecimal principal;
                    BigDecimal interest;
                    if("N".equals(accLoan.getRepaySched())){
                        // 协议还款 --- 协议还款计算公式：
                        // 1.按揭贷款-本期应还利息BOIS.ACTL_ADDAPPRV_AMT；按揭贷款-本期应还本金BORM.LOAN_REPAY；
                        // 2.非按揭贷款-本期应还利息BORM.DUE_AMT-BORM.LOAN_REPAY；非按揭贷款-本期应还本金BORM.LOAN_REPAY；
                        // 按揭贷款:: ACC_LOAN.account_class前4位字符是否为‘2104’、‘2105’、‘2106’，是则为按揭贷款； ‘2017’是公积金贷款；
                        if(C.newList("2104","2105","2106").contains(accLoan.getAccountClass4())){
                            interest = accLoan.getActlAddapprvAmt();
                            principal = accLoan.getLoanRepay();
                        }else{
                            interest = accLoan.getDueAmt().subtract(accLoan.getLoanRepay());
                            principal = accLoan.getLoanRepay();
                        }
                    }else{
                        // 非协议还款 ---（按月付息，到期还本；等额本金；等额本息）计算公式：本期应还本金 BORM.LOAN_REPAY；本期应还利息 BORM.INT_ACCR+BORM.INT_INCR*天数（天数=BORM.REPAY_DAY-BORM. SRC_SYS_DATE）；
                        DateFormat df = new SimpleDateFormat("yyyyMMdd");
                        int _calcDate = DateUtil.betweenDay(df.parse(accLoan.getRepayDay()), df.parse(accLoan.getSrcSysDate()));
                        BigDecimal calcDate = new BigDecimal(_calcDate);
                        interest =  accLoan.getIntAccr().multiply(calcDate);
                        principal = accLoan.getLoanRepay();
                    }
                    loanRepayTotal = interest.add(principal);
                    // 更新台账：应收本金、应收利息和应收合计
                    accLoan.setUnpdCap(principal.setScale(2, BigDecimal.ROUND_HALF_UP));
                    accLoan.setUnpdInt(interest.setScale(2, BigDecimal.ROUND_HALF_UP));
                    accLoan.setTotalRepay(loanRepayTotal.setScale(2,BigDecimal.ROUND_HALF_UP));
                    sqlManager.updateById(accLoan);
                    accRepayTotal.add(loanRepayTotal);
                }
                // 更新账户：应收合计
                account.setTotalRepay(accRepayTotal.setScale(2,BigDecimal.ROUND_HALF_UP));
                sqlManager.updateById(account);
                cusRepayTotal.add(accRepayTotal);
            }
            // 更新客户： 行内存款账户总余额、总应收
            if(cusBalTotal.compareTo(cusRepayTotal) > -1){
                cusInfo.setEnoughRepay(1);
            }
            cusInfo.setTotalBal(cusBalTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
            cusInfo.setTotalRepay(cusRepayTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
            sqlManager.updateById(cusInfo);
        }
        return;

    }

    private void manageRepayData(){
        // 清空数据
        sqlManager.executeUpdate(new SQLReady(S.fmt("delete from %s where 1 = 1", "T_REPAY_CUS_LIST")));
        sqlManager.executeUpdate(new SQLReady(S.fmt("delete from %s where 1 = 1", "T_REPAY_ACCT_INFO")));
        sqlManager.executeUpdate(new SQLReady(S.fmt("delete from %s where 1 = 1", "T_LOAN_ACCT_INFO")));
        // 生成数据
        Date now = new Date();
        List<JSONObject> cusList = sqlManager.select("accloan.repay_cus", JSONObject.class);
        BigDecimal bigZero = new BigDecimal(0);
        for (JSONObject cus : cusList){
            RepayCus repayCus = new RepayCus();
            repayCus.setId(U.getSnowflakeIDWorker().nextId());
            repayCus.setAddTime(now);
            repayCus.setCusId(cus.getString("CUS_ID"));
            repayCus.setCusName(cus.getString("CUS_NAME"));
            repayCus.setCertType(cus.getString("CERT_TYPE"));
            repayCus.setCertCode(cus.getString("CERT_CODE"));
            repayCus.setEnoughRepay(0);
            repayCus.setTotalBal(bigZero);
            // TODO:: get phone
//            repayCus.setPhone();
            sqlManager.insert(repayCus);
        }
        List<JSONObject> accountList = sqlManager.select("accloan.repay_pe_payment_account", JSONObject.class);
        for (JSONObject account : accountList){
            RepayAcc repayAcc = new RepayAcc();
            repayAcc.setId(U.getSnowflakeIDWorker().nextId());
            repayAcc.setCurrBal(Optional.ofNullable(account.getBigDecimal("CURR_BAL")).orElse(bigZero));
            repayAcc.setCusId(account.getString("CUS_ID"));
            repayAcc.setRepaymentAccount(account.getString("REPAYMENT_ACCOUNT"));
            repayAcc.setAddTime(now);
            sqlManager.insert(repayAcc);
        }
        List<JSONObject> accLoanList = sqlManager.select("accloan.repay_loan", JSONObject.class);
        for (JSONObject loan : accLoanList){
            RepayLoan repayLoan = new RepayLoan();
            repayLoan.setId(U.getSnowflakeIDWorker().nextId());
            repayLoan.setAccountClass(loan.getString("ACCOUNT_CLASS"));
            repayLoan.setAccountClass4(loan.getString(""));
            repayLoan.setActlAddapprvAmt(Optional.ofNullable(loan.getBigDecimal("ACTL_ADDAPPRV_AMT")).orElse(bigZero));
            repayLoan.setActType(loan.getString(""));

            repayLoan.setContNo(loan.getString("CONT_NO"));
            repayLoan.setCusId(loan.getString("CUS_ID"));
            repayLoan.setCusName(loan.getString("CUS_NAME"));
            repayLoan.setDueAmt(Optional.ofNullable(loan.getBigDecimal("DUE_AMT")).orElse(bigZero));
            repayLoan.setIntAccr(Optional.ofNullable(loan.getBigDecimal("INT_ACCR")).orElse(bigZero));

            repayLoan.setIntIncr(Optional.ofNullable(loan.getBigDecimal("INT_INCR")).orElse(bigZero));
            repayLoan.setIntRepayFreq(loan.getString("REPAY_FREQ"));
            repayLoan.setLoanAccount(loan.getString("LOAN_ACCOUNT"));
            repayLoan.setLoanAccount16(loan.getString("LOAN_ACCOUNT_16"));
            repayLoan.setLoanRepay(Optional.ofNullable(loan.getBigDecimal("LOAN_REPAY")).orElse(bigZero));

            repayLoan.setNextDueDate(loan.getString("NEXT_DUE_DATE"));
            repayLoan.setRepaymentAccount(loan.getString("REPAYMENT_ACCOUNT"));
            repayLoan.setRepayDay(loan.getString("REPAY_DAY"));
            repayLoan.setRepayFreq(loan.getString("REPAY_FREQ"));
            repayLoan.setRepaySched(loan.getString("REPAY_SCHED"));

            repayLoan.setSrcSysDate(loan.getString("SRC_SYS_DATE"));
            repayLoan.setStat(loan.getString("STAT"));
            repayLoan.setAddTime(now);

            sqlManager.insert(repayLoan);
        }
    }

    @Autowired
    SQLManager sqlManager;

    // 还款余额不足 - 客户列表
    @RequestMapping(value = "warn/cusList", method = RequestMethod.GET)
    public Result warnCusList(
            @RequestParam Map<String, Object> params
    ){
        params.put("uid", AuthFilter.getUid());
        return Result.ok(U.beetlPageQuery("accloan.repay_cus_list", JSONObject.class, params));
    }

    // 还款余额不足客户 - 账户列表
    @RequestMapping(value = "warn/account", method = RequestMethod.GET)
    public Result warnCusAccList(
            @RequestParam("cusId") String cusId
    ){
        AuthFilter.getUid();

        return Result.ok(
                sqlManager.select("accloan.repay_acct_info", JSONObject.class, C.newMap("cusId", cusId))
        );
    }

    // 还款余额不足客户账户关联台账
    @RequestMapping(value = "warn/linkLoan", method = RequestMethod.GET)
    public Result warnCusAccLinkLoan(
            @RequestParam("accountId") String accountId
    ){
        AuthFilter.getUid();
        return Result.ok(
                sqlManager.select("accloan.repay_loan_acct_info", JSONObject.class, C.newMap("repaymentAccount", accountId))
        );
    }

}
