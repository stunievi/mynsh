package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.SysNotice;
import com.beeasy.hzback.entity.SysVar;
import com.beeasy.hzback.entity.User;
import com.beeasy.hzback.entity.WfIns;
import com.beeasy.hzback.modules.system.util.DateUtil;
import com.beeasy.hzdata.service.SearchService;
import com.beeasy.mscommon.valid.ValidGroup;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class LoanManagerService {

    @Autowired
    SQLManager sqlManager;
    @Autowired
    NoticeService2 noticeService2;


    /**
     * 检查按揭类贷款账户信息中的出证状态、购房合同约定交房日期和未按时出证情况说明，若出证状态为“未出证”且购房合同约定交房日期对比当前日期已经超过540天且未按时出证情况说明为空，则产生贷后任务要求管户经理必须录入未按时出证情况说明；
     */
    @Scheduled(cron = "0 10 9 * * ?")
    public void sendTaskRule(){
        List<JSONObject> lists = sqlManager.select("task.查询按揭类贷款账户信息", JSONObject.class, C.newMap("rule","TASK_PRODUCE_RULE_1_ON"));
        if(null == lists || lists.size()==0){
            return;
        }

        SysVar sysVar = sqlManager.lambdaQuery(SysVar.class).andEq(SysVar::getVarName,"TASK_PRODUCE_RULE_1").single();
        String varValue = sysVar.getVarValue();
        if(null == varValue || "".equals(varValue)){
            varValue = "0";
        }
        for(JSONObject jsonObject : lists){

            Date payDate = jsonObject.getDate("payDate");
            String czStatus = jsonObject.getString("czStatus");
            String loanAccount = jsonObject.getString("loanAccount");
            String accCode = jsonObject.getString("custMgr");
            String explain = jsonObject.getString("explain");

            //出证状态为“未出证”且购房合同约定交房日期对比当前日期已经超过540天且未按时出证情况说明为空
            if(("0".equals(czStatus) || null == czStatus) && (null == payDate) && ("".equals(explain) || null == explain)){
                LocalDate localDate = DateUtil.dateToLocalDate(payDate);
                LocalDate now = LocalDate.now();
                long day = DateUtil.betweenDay(localDate, now);//相差的天数
                if(day > Long.parseLong(varValue)){
                    generateAutoTask(accCode,loanAccount);
                }
            }
        }
    }


    /**
     * 检查按揭类贷款账户信息中的出证状态和购房合同约定交房日期，发送消息给管户经理
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendMessageRule1(){
        List<JSONObject> lists1 = sqlManager.select("task.查询按揭类贷款账户信息", JSONObject.class, C.newMap("rule","MSG_RULE_19_ON"));

//        Set<Long> uidList = new HashSet<>();
        List<SysNotice> notices = new ArrayList<>();

        Map<Long, Integer> maps = new HashMap<>();

        if(null != lists1 && lists1.size()>0){
            for(JSONObject jsonObject : lists1) {

                Date payDate = jsonObject.getDate("payDate");
                String czStatus = jsonObject.getString("czStatus");
                Long uid = jsonObject.getLong("id");

                //未出证且购房合同约定交房日期为空
                if (("0".equals(czStatus) || null == czStatus) && (null == payDate)) {
//                    uidList.add(uid);
                    if (null != maps.get(uid) && (0 != maps.get(uid))) {
                        maps.put(uid, maps.get(uid) + 1);
                    } else {
                        maps.put(uid, 1);
                    }
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : maps.entrySet()){
            String renderStr = "您有"+entry.getValue()+"条按揭类贷款台账需要补录交房日期，请在信贷数据管理-贷款资料-贷款台帐界面中，使用按揭贷款信息批量维护按钮，批量导入数据";
            notices.add(
                    noticeService2.makeNotice(SysNotice.Type.SYSTEM, entry.getKey(), renderStr, null)
            );
        }

//        String renderStr = "您有"+total+"条按揭类贷款台账需要补录交房日期，请在信贷数据管理-贷款资料-贷款台帐界面中，使用按揭贷款信息批量维护按钮，批量导入数据";
//        notices = noticeService2.makeNotice(SysNotice.Type.SYSTEM, uidList, renderStr, null);
        sqlManager.insertBatch(SysNotice.class, notices);
    }

    /**
     * 在出证状态为“未出证”且购房合同约定交房日期不为空时，在距离购房合同约定交房日期还有30天时发送消息给管户经理录入相关出证信息
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendMessageRule2(){
        List<JSONObject> lists2 = sqlManager.select("task.查询按揭类贷款账户信息", JSONObject.class, C.newMap("rule","MSG_RULE_20_ON"));

        List<SysNotice> notices = new ArrayList<>();
        Map<Long, Integer> maps = new HashMap<>();

        if(null != lists2 && lists2.size()>0){
            SysVar sysVar = sqlManager.lambdaQuery(SysVar.class).andEq(SysVar::getVarName,"MSG_RULE_20").single();
            String varValue = sysVar.getVarValue();
            if(null == varValue || "".equals(varValue)){
                varValue = "0";
            }
            for(JSONObject jsonObject : lists2) {
                Date payDate = jsonObject.getDate("payDate");
                String czStatus = jsonObject.getString("czStatus");
                Long uid = jsonObject.getLong("id");
                //出证状态为“未出证”且购房合同约定交房日期不为空时
                if(("0".equals(czStatus) || null == czStatus) && (null != payDate)){
                    LocalDate localDate = DateUtil.dateToLocalDate(payDate);

                    LocalDate now = LocalDate.now();
//                    Duration duration = Duration.between(localDate, now);
//                    long day = duration.toDays();//相差的天数
                    long days = DateUtil.betweenDay(localDate, now);
                    if(days == Long.parseLong(varValue)){
                        if (null != maps.get(uid) && (0 != maps.get(uid))) {
                            maps.put(uid, maps.get(uid) + 1);
                        } else {
                            maps.put(uid, 1);
                        }
                    }
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : maps.entrySet()){
            String renderStr = "您有"+entry.getValue()+"条按揭类贷款台账交房日期即将到期，请及时补录出证信息，请在信贷数据管理-贷款资料-贷款台帐界面中，使用按揭贷款信息批量维护按钮，批量导入数据";
            notices.add(
                    noticeService2.makeNotice(SysNotice.Type.SYSTEM, entry.getKey(), renderStr, null)
            );
        }

        sqlManager.insertBatch(SysNotice.class, notices);
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
