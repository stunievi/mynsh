package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.SysVar;
import com.beeasy.hzback.entity.User;
import com.beeasy.hzback.entity.WfIns;
import com.beeasy.hzdata.service.SearchService;
import com.beeasy.mscommon.valid.ValidGroup;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class LoanManagerService {

    @Autowired
    SQLManager sqlManager;

    /**
     * 检查按揭类贷款账户信息中的出证状态和购房合同约定交房日期，若出证状态为“未出证”且购房合同约定交房日期为空的，产生贷后任务要求管户经理录入购房合同约定交房日期
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendTaskRule1(){
        List<JSONObject> lists = sqlManager.select("task.查询按揭类贷款账户信息", JSONObject.class, C.newMap("rule","TASK_PRODUCE_RULE_1_ON"));
        if(null == lists || lists.size()==0){
            return;
        }
        for(JSONObject jsonObject : lists){

            Date payDate = jsonObject.getDate("payDate");
            String czStatus = jsonObject.getString("czStatus");
            String loanAccount = jsonObject.getString("loanAccount");
            String accCode = jsonObject.getString("custMgr");

            //未出证且购房合同约定交房日期为空
            if(("0".equals(czStatus) || null == czStatus) && (null == payDate)){
                generateAutoTask(accCode,loanAccount);
            }

        }

    }

    /**
     * 在出证状态为“未出证”且购房合同约定交房日期不为空时，在距离购房合同约定交房日期还有一个月（三十天）时产生贷后任务要求管户经理录入相关出证信息，无法按时出证须录入具体原因（类型为文本）。
     */
    @Scheduled(cron = "0 10 9 * * ?")
    public void sendTaskRule2(){
        List<JSONObject> lists = sqlManager.select("task.查询按揭类贷款账户信息", JSONObject.class, C.newMap("rule","TASK_PRODUCE_RULE_2_ON"));
        if(null == lists || lists.size()==0){
            return;
        }
        SysVar sysVar = sqlManager.lambdaQuery(SysVar.class).andEq(SysVar::getVarName,"TASK_PRODUCE_RULE_2").single();
        String varValue = sysVar.getVarValue();
        if(null == varValue || "".equals(varValue)){
            varValue = "0";
        }

        for(JSONObject jsonObject : lists){

            Date payDate = jsonObject.getDate("payDate");
            String czStatus = jsonObject.getString("czStatus");
            String loanAccount = jsonObject.getString("loanAccount");
            String accCode = jsonObject.getString("custMgr");

            //出证状态为“未出证”且购房合同约定交房日期不为空时
            if(("0".equals(czStatus) || null == czStatus) && (null != payDate)){
                Instant instant = payDate.toInstant();
                ZoneId zoneId = ZoneId.systemDefault();

                LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(localDateTime, now);
                long day = duration.toDays();//相差的天数
                System.out.println(day);
                if(day == Long.parseLong(varValue)){
                    generateAutoTask(accCode,loanAccount);
                }
            }

        }

    }

    /***** 自动生成任务start *****/
    public void generateAutoTask(String CUST_MGR, String loanAccount) {

        String modelName = "贷后跟踪-零售银行部个人按揭";
        System.out.println(">>>>>>>>>>>："+CUST_MGR);

        //  查找任务执行人
        User user = sqlManager.lambdaQuery(User.class).andEq(User::getAccCode, CUST_MGR).single();
        if ($.isNull(user)) {
//            println( "贷款账号%s: 找不到对应的任务执行人", loanAccount);
            return;
        }

        JSONObject data = new JSONObject();
        try {
            String no = String.valueOf(SearchService.InnateMap.getOrDefault(modelName, 0));
            if (no.equals("0")) {
//                println( "贷款账号%s: 查询不到台账信息", loanAccount);
                return;
            }
            data = sqlManager.selectSingle("accloan.329", C.newMap("loanAccount", loanAccount), JSONObject.class);
        } finally {
            if (0 == data.size()) {
//                println( "贷款账号%s: 查询不到台账信息", loanAccount);
                return;
            }
        }

        WfIns ins = new WfIns();
        ins.setModelName(modelName);
        ins.setPubUserId(user.getId());
        ins.setDealUserId(user.getId());
        ins.setPlanStartTime(new Date());
        ins.setTitle(modelName);
        ins.setLoanAccount("");
        ins.set("$data", data);
        ins.set("$startNodeData", new JSONObject());
        ins.set("$uid", user.getId());
        ins.setAutoCreated(true);
        ins.valid(ins, ValidGroup.Add.class);
        ins.onBeforeAdd(sqlManager);
        ins.onAdd(sqlManager);

    }
}
