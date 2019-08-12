package com.beeasy.hzback.modules.system.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.*;
import com.beeasy.hzback.modules.system.util.DateUtil;
import com.beeasy.mscommon.util.U;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@Transactional
public class TaskRunner {

    @Autowired
    @Qualifier(value = "sqlManagers")
    Map<String, SQLManager> sqlManagers;

    @Autowired
    private SQLManager sqlManager;

    private String getConfig(final String key){
        SysVar sysVar = sqlManager.lambdaQuery(SysVar.class)
                .andEq(SysVar::getVarName, key)
                .single();
        if(null == sysVar){
            return "";
        }
        return sysVar.getVarValue();
    }

    /**
     * 定时清理登录令牌(每天失效)
     */
    @Scheduled(cron = "0 10 0 ? * *")
    public void clearTokens(){
        sqlManagers.forEach((s, sqlManager) -> {
            sqlManager.lambdaQuery(UserToken.class).delete();
        });
    }

    /**
     * @Author gotomars
     * @Description 功能实现：定时任务脚本，遍历所有符合条件的贷款台账的贷款合同金额、客户名称、证件号码与贷前关联方查询录入的受理金额、客户名称、证件号码一致时，更新贷款台账的“贷前关联查询”字段值为01
     * @Date 16:01 2019/7/30
     **/
//    @Scheduled(cron = "0 10 1 * * ?")
//    public void updateLoanLinkSearchState(){
//        JSONObject cond = new JSONObject();
//        List<JSONObject> list = sqlManager.select("accloan.xxx", JSONObject.class, cond);
//        list.forEach(item->{
//            JSONObject loan = (JSONObject) item;
//            sqlManager.update("accloan.xxx", C.newMap(
//                    "", "",
//                    "CUS_NAME", loan.getString("CUS_NAME"),
//                    "CERT_CODE", loan.getString("CERT_CODE")
//
//            ));
//        });
//    }


    // 维护还款余额信息统计数据
    private void manageRepayData(){
        // 清空数据
        sqlManager.execute(new SQLReady(S.fmt("delete from %s where 1 = 1", "T_REPAY_CUS_LIST")), null);
        sqlManager.execute(new SQLReady(S.fmt("delete from %s where 1 = 1", "T_REPAY_ACCT_INFO")), null);
        sqlManager.execute(new SQLReady(S.fmt("delete from %s where 1 = 1", "T_LOAN_ACCT_INFO")), null);
        // 生成数据
        Date now = new Date();
        List<JSONObject> cusList = sqlManager.select("普通贷款客户列表", JSONObject.class);
        for (JSONObject cus : cusList){
            RepayCus repayCus = new RepayCus();
            repayCus.setId(U.getSnowflakeIDWorker().nextId());
            repayCus.setAddTime(now);
            repayCus.setCusId(cus.getString("CUS_ID"));
            repayCus.setCusName(cus.getString("CUS_NAME"));
            repayCus.setCertType(cus.getString("CERT_TYPE"));
            // TODO:: get phone
//            repayCus.setPhone();
            sqlManager.insert(repayCus);
        }
        List<JSONObject> accountList = sqlManager.select("客户还款账户列表", JSONObject.class);
        for (JSONObject account : accountList){
            RepayAcc repayAcc = new RepayAcc();
            repayAcc.setId(U.getSnowflakeIDWorker().nextId());
            repayAcc.setCurrBal(account.getBigDecimal("CURR_BAL"));
            repayAcc.setCusId(account.getString("CUS_ID"));
            repayAcc.setRepaymentAccount(account.getString("REPAYMENT_ACCOUNT"));
            repayAcc.setAddTime(now);
            sqlManager.insert(repayAcc);
        }
        List<JSONObject> accLoanList = sqlManager.select("客户还款账户列表", JSONObject.class);
        for (JSONObject loan : accLoanList){
            RepayLoan repayLoan = new RepayLoan();
            repayLoan.setId(U.getSnowflakeIDWorker().nextId());
            repayLoan.setAccountClass(loan.getString("ACCOUNT_CLASS"));
            repayLoan.setAccountClass4(loan.getString(""));
            repayLoan.setActlAddapprvAmt(loan.getBigDecimal("ACTL_ADDAPPRV_AMT"));
            repayLoan.setActType(loan.getString(""));

            repayLoan.setContNo(loan.getString("CONT_NO"));
            repayLoan.setCusId(loan.getString("CUS_ID"));
            repayLoan.setCusName(loan.getString("CUS_NAME"));
            repayLoan.setDueAmt(loan.getBigDecimal("DUE_AMT"));
            repayLoan.setIntAccr(loan.getBigDecimal("INT_ACCR"));

            repayLoan.setIntIncr(loan.getBigDecimal("INT_INCR"));
            repayLoan.setIntRepayFreq(loan.getString("REPAY_FREQ"));
            repayLoan.setLoanAccount(loan.getString("LOAN_ACCOUNT"));
            repayLoan.setLoanAccount16(loan.getString("LOAN_ACCOUNT_16"));
            repayLoan.setLoanRepay(loan.getBigDecimal("LOAN_REPAY"));

            repayLoan.setNextDueDate(loan.getString("NEXT_DUE_DATE"));
            repayLoan.setRepaymentAccount(loan.getString("REPAYMENT_ACCOUNT"));
            repayLoan.setRepayDay(loan.getString("REPAY_DAY"));
            repayLoan.setRepayFreq(loan.getString("REPAY_FREQ"));
            repayLoan.setRepaySched(loan.getString("REPAY_SCHED"));

            repayLoan.setSrcSysDate(loan.getString("SRC_SYS_DATE"));
            repayLoan.setStat(loan.getString("STAT"));
            repayLoan.setAddTime(now);

            sqlManager.insert(loan);
        }
    }

    /**
     * @Author gotomars
     * @Description 系统定时跑批检索存量客户的还款账户余额，以及计算该客户所有贷款台帐本月应还本金及利息，将还款账户余额不足的客户信息输出到系统查询界面，以便相关人员跟进处理。
     * @Date 11:01 2019/8/6
     **/
    @Scheduled(cron = "0 30 2 * * ?")
    public void updateLoanLinkSearchState() throws ParseException {
        int taskDate = Integer.parseInt(getConfig("MSG_RULE_10"));
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currDay = calendar.get(Calendar.DAY_OF_MONTH);
        // 若是不等于指定跑批日期，忽略
        if(currDay != taskDate){
            return;
        }

        manageRepayData();

        // --计算账户还款余额情况
        // 筛选出已贷款客户
        List<RepayCus> cusList = new ArrayList<>();
        for(RepayCus cusInfo : cusList){
            // 客户待还款总额
            BigDecimal cusRepayTotal = new BigDecimal(0);
            // 行内存款账户总余额
            BigDecimal cusBalTotal = cusInfo.getTotalBal();
            // 筛选出还款账户
            List<RepayAcc> accountList = new ArrayList<>();
            for(RepayAcc account : accountList){
                // 还款账户待还款总额
                BigDecimal accRepayTotal = new BigDecimal(0);
                // 账户余额
                BigDecimal CURR_BAL = account.getCurrBal();
                cusBalTotal.add(CURR_BAL);
                // 账户关联台账
                List<RepayLoan> accLoanList = new ArrayList<>();
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
                    // 更新台账
                    accLoan.setUnpdCap(principal);
                    accLoan.setUnpdInt(interest);
                    accLoan.setTotalRepay(loanRepayTotal);
                    sqlManager.lambdaQuery(RepayLoan.class).update(accLoan);
                    accRepayTotal.add(loanRepayTotal);
                }
                // 更新账户信息
                account.setTotalRepay(accRepayTotal);
                sqlManager.lambdaQuery(RepayAcc.class).update(account);
                cusRepayTotal.add(accRepayTotal);
            }
            // 更新客户信息： 行内存款账户总余额、本月应收
            if(cusBalTotal.compareTo(cusRepayTotal) == -1){
                cusInfo.setEnoughRepay(0);
            }
            cusInfo.setTotalRepay(cusRepayTotal);
            sqlManager.lambdaQuery(RepayCus.class).update(cusInfo);
        }
        return;
    }
}
