package com.beeasy.hzdata.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.*;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

//import com.beeasy.hzdata.entity.*;

@Service
@Slf4j
public class CheckService {

    String logDir;
    SQLManager sqlManager;

    @Qualifier(value = "sqlManagers")
    @Autowired
    Map<String, SQLManager> sqlManagerMap;

    @Autowired
    SearchService searchService;
    @Autowired
    NoticeService2 noticeService2;


    public CheckService(@Value("${uploads.path}") String dir){
        File file = new File(dir, "logs");
        if(!file.exists()) file.mkdirs();
        logDir = file.getAbsolutePath();
    }

//    @Autowired
//    DubboService dubboService;


    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMM");
    private Pattern pattern = Pattern.compile("\\{(.+?)\\}");

//    @Async
    public void clearTriggerLogs(){
        println("正在清除检查日志......");
        sqlManager.update("task.deleteTriggerLogs",C.newMap());
        sqlManager.update("task.deleteNotices", C.newMap());
        sqlManager.executeUpdate(new SQLReady("delete from t_short_message_log"));
    }

//    @Async
    public void checkMany(String num){
        List<String> nums = Arrays.stream(num.split(","))
                .map(String::trim)
                .distinct()
                .collect(toList());
        //这里不列入stream, 因为要受检异常
        for(String n : nums){
            if(n.contains("~")){
                List<Integer> ns = Arrays.stream(n.split("~"))
                        .map(Integer::parseInt)
                        .collect(toList());
                if(ns.size() != 2){
                    continue;
                }
                for(int i = ns.get(0); i <= ns.get(1); i++){
                    checkSingle(i);
                }
            }
            else{
                checkSingle(Integer.parseInt(n));
            }
        }
    }

    public void checkSingle( final int n){
        //反射
        Method method = null;
        try {
            method = getClass().getDeclaredMethod("rule" + n, OutputStream.class);
        } catch (NoSuchMethodException e) {
            //没有就略过
            return;
        }
        method.setAccessible(true);
        beginChcek(n);
        try {
            method.invoke(this, System.out);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        endCheck(n);
    }


    public void Assert(boolean b, OutputStream os, String msg){
        if(!b){
            try {
                os.write(msg.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new RuntimeException();
        }
    }


    @Scheduled(cron = "0 0 2 * * ?")
    public void trigger(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File dir = new File(logDir);
        try
            (
                OutputStream os = new FileOutputStream(new File(dir,sdf.format(new Date()) + ".txt"));
                ){
            sqlManagerMap.forEach((n,sqlManager) -> {
                this.sqlManager = sqlManager;
                DataSourceTransactionManager manager = U.getTxManager(n);
                TransactionStatus transactionStatus = manager.getTransaction(new DefaultTransactionDefinition(){{setPropagationBehavior(PROPAGATION_REQUIRES_NEW);}});
                try{
                    rule1(os);
                    rule2(os);
                    rule3(os);
                    rule4(os);
                    rule5(os);
                    rule6(os);
                    rule7(os);
                    rule10086(os);
                    generateAutoTask(os);
                    manager.commit(transactionStatus);
                }
                catch (Exception e){
                    e.printStackTrace();
                    manager.rollback(transactionStatus);
                    if(e instanceof IOException){
                        throw e;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /***** 检查区域 ****/
//    @Scheduled(cron = "0 10 0 ? * *")
    public void rule1(OutputStream os){
        MsgTmpl template = getTemplate(1);
        Assert(null != template && S.notBlank(template.getTemplate()),os, "找不到消息模板");
        List<JSONObject> res = (sqlManager.select("task.selectRule1UL",JSONObject.class, C.newMap()));
        println(os, "找到待处理条数%d", res.size());
        List<ShortMsg> sms = C.newList();
        List<NoticeTriggerLog> logs = C.newList();
        for (JSONObject re : res) {
            try {
                String la = re.getString("LOAN_ACCOUNT");
                String phone = re.getString("PHONE");
                if(isPhone(phone)){
                    String renderStr = renderMessage(template, re);
                    sms.add(makeShortMessageLog(phone.trim(),renderStr));
                }
                logs.add(makeLog(1,la,null));
            }
            catch (Exception e){
                println(os,e.getMessage(), null);
            }
        }
        sqlManager.insertBatch(ShortMsg.class, sms);
        sqlManager.insertBatch(NoticeTriggerLog.class, logs);
    }


    public void rule2(OutputStream os){
        MsgTmpl template = getTemplate(2);
        Assert(null != template && S.notBlank(template.getTemplate()),os, "找不到消息模板");
        List<JSONObject> res = (sqlManager.select("task.selectRule2UL", JSONObject.class, C.newMap()));
        println(os, "找到待处理条数%d", res.size());
        List<ShortMsg> sms = C.newList();
        List<NoticeTriggerLog> logs = C.newList();
        for (JSONObject re : res) {
            try{
                String la = re.getString("LOAN_ACCOUNT");
                String phone = re.getString("PHONE");
                //写入短信记录
                if(isPhone(phone)){
                    phone = phone.trim();
                    String renderStr = renderMessage(template, re);
                    sms.add(makeShortMessageLog(phone,renderStr));
                }
                //写入日志
                logs.add(makeLog(2,la,null));
            }
            catch (Exception e){
                println(os,e.getMessage(), null);
            }
        }
        sqlManager.insertBatch(ShortMsg.class, sms);
        sqlManager.insertBatch(NoticeTriggerLog.class, logs);
    }


    public void rule3(OutputStream os){
        MsgTmpl template = getTemplate(3);
        Assert(null != template && S.notBlank(template.getTemplate()),os, "找不到消息模板");
        List<JSONObject> res = (sqlManager.select("task.selectRule3UL",JSONObject.class, C.newMap()));
        println( os,"找到待处理条数%d", res.size());

        List<SysNotice> notices = C.newList();
        List<NoticeTriggerLog> logs = C.newList();
        for (JSONObject re : res) {
            try{
                Long uid = re.getLong("UID");
                if(null == uid) continue;
                String renderStr = renderMessage(template, re);
                notices.add(
                    noticeService2.makeNotice(SysNotice.Type.SYSTEM, uid, renderStr, null)
                );
                //写入日志
                logs.add(makeLog(3, re.getString("ACCT_NO"), re.getString("REC_NO") ,null));
            }
            catch (Exception e){
                println(os,e.getMessage(), null);
            }
        }
        sqlManager.insertBatch(SysNotice.class, notices);
        sqlManager.insertBatch(NoticeTriggerLog.class, logs);
    }

    public void rule4(OutputStream os){
        MsgTmpl template = getTemplate(4);
        Assert(null != template && S.notBlank(template.getTemplate()),os, "找不到消息模板");
        List<JSONObject> res1 = (sqlManager.select("task.selectRule4-1UL",JSONObject.class, C.newMap()));
        List<JSONObject> res2 = (sqlManager.select("task.selectRule4-2UL",JSONObject.class, C.newMap()));
        List<SysNotice> notices = C.newList();
        List<ShortMsg> sms = C.newList();
        List<NoticeTriggerLog> logs = C.newList();
        for (JSONObject object : res1) {
            try{
                Long uid = object.getLong("UID");
                if(null != uid){
                    notices.add(
                        noticeService2.makeNotice(SysNotice.Type.SYSTEM, uid, renderMessage("客户: {客户名称}的贷款：{贷款帐号}即将到期，到期日为：{到期日}", object), null)
                    );
                }
                String phone = object.getString("PHONE");
                if(isPhone(phone)){
                    phone = phone.trim();
                    String renderStr = renderMessage(template, object);
                    sms.add(makeShortMessageLog(phone,renderStr));
                }
                logs.add(makeLog(4,object.getString("LOAN_ACCOUNT"),null));
            }
            catch (Exception e){
                println(os,e.getMessage(), null);
            }
        }
        for (JSONObject object : res2) {
            try{
                Long mid = object.getLong("MID");
                if(null != mid){
                    notices.add(
                        noticeService2.makeNotice(SysNotice.Type.SYSTEM, mid, renderMessage("客户: {客户名称}的贷款：{贷款帐号}即将到期，到期日为：{到期日}",object), null)
                    );
                }
            }
            catch (Exception e){
                println(os,e.getMessage(), null);
            }
        }
        sqlManager.insertBatch(SysNotice.class, notices);
        sqlManager.insertBatch(ShortMsg.class, sms);
        sqlManager.insertBatch(NoticeTriggerLog.class, logs);
    }

    public void rule5(OutputStream os){
        MsgTmpl template = getTemplate(5);
        Assert(null != template && S.notBlank(template.getTemplate()),os, "找不到消息模板");

        List<JSONObject> res1 = (sqlManager.select("task.selectRule5-1UL",JSONObject.class, C.newMap()));
        List<JSONObject> res2 = (sqlManager.select("task.selectRule5-2UL",JSONObject.class, C.newMap()));
        List<SysNotice> notices = C.newList();
        List<ShortMsg> sms = C.newList();
        List<NoticeTriggerLog> logs = C.newList();

        for (JSONObject object : res1) {
            try{
                Long uid = object.getLong("UID");
                if(null != uid){
                    notices.add(
                        noticeService2.makeNotice(SysNotice.Type.SYSTEM, uid, renderMessage("客户：{客户名称}的贷款：{贷款帐号}发生逾期，欠本：{拖欠本金}，逾期日：{本金逾期起始日}，欠息：{欠息累积}，逾期日{利息逾期起始日}", object), null)
                    );
                }
                String phone = object.getString("PHONE");
                if(isPhone(phone)){
                    phone = phone.trim();
                    String renderStr = renderMessage(template, object);
                    sms.add(makeShortMessageLog(phone,renderStr));
                }
                logs.add(makeLog(5,object.getString("LOAN_ACCOUNT"),null));
            }
            catch (Exception e){
                println(os,e.getMessage(), null);
            }
        }
        for (JSONObject object : res2) {
            try{
                Long mid = object.getLong("MID");
                if(null != mid){
                    notices.add(
                        noticeService2.makeNotice(SysNotice.Type.SYSTEM, mid, renderMessage("客户：{客户名称}的贷款：{贷款帐号}发生逾期，欠本：{拖欠本金}，逾期日：{本金逾期起始日}，欠息：{欠息累积}，逾期日{利息逾期起始日}",object), null)
                    );
                }
            }
            catch (Exception e){
                println(os,e.getMessage(), null);
            }
        }
        sqlManager.insertBatch(SysNotice.class, notices);
        sqlManager.insertBatch(ShortMsg.class, sms);
        sqlManager.insertBatch(NoticeTriggerLog.class, logs);
    }


    public void rule6(OutputStream os){
        List<JSONObject> res = sqlManager.select("task.selectRule6UL", JSONObject.class, C.newMap());
        println( os,"找到待处理条数%d", res.size());
        JSONObject sent = new JSONObject();
        List<SysNotice> notices = C.newList();
        List<NoticeTriggerLog> logs = C.newList();
        for (JSONObject item : res) {
            try{
                //给客户经理发消息
                Long uid = item.getLong("UID");
                if(!sent.containsKey(uid)){
                    sent.put("uid",1);
                    notices.add(
                        noticeService2.makeNotice(SysNotice.Type.WORKFLOW, uid, String.format("你的任务 %s(任务编号: %s) 已有 %d 天未处理, 请及时处理", item.get("TITLE"), item.get("ID") + "", (int)item.get("EXPR_DAYS")),C.newMap("taskId", item.get("ID") + ""))
                    );
                }
                //给主管发消息
                Long mid = item.getLong("MID");
                if(null != mid){
                    notices.add(
                        noticeService2.makeNotice(
                            SysNotice.Type.WORKFLOW,
                            mid,
                            String.format("你部门 %s(%s) 处理的任务 %s(任务编号: %s) 已有 %d 天未处理", item.getString("UTNAME"), item.getString("UNAME"), item.getString("TITLE"), item.get("ID") + "", item.getInteger("EXPR_DAYS")),
                            C.newMap("taskId",item.get("ID") + "")
                        )
                    );
                }
                logs.add(makeLog(6, item.getLong("DID") + "", null));
            }
            catch (Exception e){
                println(os,e.getMessage(), null);
            }
        }
        sqlManager.insertBatch(SysNotice.class, notices);
        sqlManager.insertBatch(NoticeTriggerLog.class, logs);
    }

    public void rule7(OutputStream os) {
        List<JSONObject> res = sqlManager.select("task.selectRule7UL", JSONObject.class, C.newMap());
        println( os,"找到待处理条数%d", res.size());
        List<SysNotice> notices = C.newList();
        List<NoticeTriggerLog> logs = C.newList();
        for (JSONObject item : res) {
            try{
                Long uid = item.getLong("UID");
                //给处理人发信息
                notices.add(
                    noticeService2.makeNotice(
                        SysNotice.Type.WORKFLOW, uid, String.format("你的任务 %s(任务编号: %s) 已执行 %d 天未完成", item.get("TITLE"), item.get("ID") + "", item.getInteger("EXPR_DAYS")), C.newMap("taskId", item.get("ID") + "")
                    )
                );
                for (String manager : String.valueOf(item.get("MANAGERS")).split(",")) {
                    long mid = 0;
                    try {
                        mid = Long.parseUnsignedLong(manager);
                    } catch (Exception e) {
                        continue;
                    }
                    if (mid > 0) {
                        notices.add(
                            noticeService2.makeNotice(
                                SysNotice.Type.WORKFLOW,
                                mid,
                                String.format("你部门 %s(%s) 处理的任务 %s(任务编号: %s) 已有 %d 天未完成", item.get("TRUE_NAME"), item.get("UNAME"), item.get("TITLE"), item.get("ID") + "", (int) item.get("EXPR_DAYS")),
                                C.newMap("taskId",item.get("ID") + "")
                            )
                        );
                    }
                }
                logs.add(makeLog(7, item.getLong("ID") + "", null));
            }
            catch (Exception e){
                println(os,e.getMessage(), null);
            }
        }
        sqlManager.insertBatch(SysNotice.class, notices);
        sqlManager.insertBatch(NoticeTriggerLog.class, logs);
    }

    public void rule17(OutputStream os){
        println("aaa ");
    }


    public void rule10086(OutputStream os){
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
        //rule10
        Integer rule10 = Integer.parseInt(getConfig("MSG_RULE_10"));
        Integer rule11 = Integer.parseInt(getConfig("MSG_RULE_11"));
        Integer rule12 = Integer.parseInt(getConfig("MSG_RULE_12"));
        Integer rule13 = Integer.parseInt(getConfig("MSG_RULE_13"));
        Integer rule14 = Integer.parseInt(getConfig("MSG_RULE_14"));
        Integer rule15 = Integer.parseInt(getConfig("MSG_RULE_15"));
        Integer rule16 = Integer.parseInt(getConfig("MSG_RULE_16"));

        Date now = new Date();
        List<SysNotice> notices = C.newList();
        List<NoticeTriggerLog> logs = C.newList();
        //列出所有正在执行的主流程
        List<WfIns> insList = sqlManager.lambdaQuery(WfIns.class)
            .andEq(WfIns::getModelName, "不良资产主流程")
            .andEq(WfIns::getState, WfIns.State.DEALING)
            .select(WfIns::getId, WfIns::getPlanStartTime, WfIns::getDealUserId, WfIns::getTitle);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        for (WfIns wfIns : insList) {
            //规则10
            //催收流程
            List<WfIns> cs = sqlManager.lambdaQuery(WfIns.class)
                .andEq(WfIns::getModelName, "催收")
                .andEq(WfIns::getState, WfIns.State.FINISHED)
                .andEq(WfIns::getParentId, wfIns.getId())
                .orderBy("add_time desc")
                .select(WfIns::getId, WfIns::getPlanStartTime);
            //判断最后一个
            Date date;
            if(cs.size() > 0){
                WfIns cuishou = cs.get(0);
                //查找日期
                String value = getAttrValue(cuishou.getId(), "CS_DATE");
                if(S.notBlank(value)){
                    try {
                        date = ymd.parse(value);
                    } catch (ParseException e) {
                        date = wfIns.getPlanStartTime();
                    }
                }
                else{
                    date = wfIns.getPlanStartTime();
                }
            }
            else{
                date = wfIns.getPlanStartTime();
            }
            //判断当前时间是否在这之中
            Date max = plusYear(date,3);
            if(between(new Date(), plusDay(max, -rule10), max)){
                notices.add(
                    noticeService2.makeNotice(
                        SysNotice.Type.WORKFLOW, wfIns.getDealUserId(), String.format("你的不良资产管理任务(任务编号: %d)诉讼时效(%s)将至, 请注意及时发起催收或起诉", wfIns.getId(), ymd.format(max) ),C.newMap("taskId", wfIns.getId() + "")
                    )
                );
            }

            //规则11 - 诉讼
            List<WfIns> sss = sqlManager.lambdaQuery(WfIns.class)
                .andEq(WfIns::getModelName, "诉讼")
                .andEq(WfIns::getParentId, wfIns.getId())
                .andEq(WfIns::getCurrentNodeName, "诉前保全")
                .andEq(WfIns::getState, WfIns.State.DEALING)
                .select(
                    WfIns::getId, WfIns::getDealUserId, WfIns::getTitle
                );

            for (WfIns ss : sss) {
                //保全起始时间
                Date dtstart = null;
                Date dtend = null;
                try{
                    dtstart = ymd.parse(getAttrValue(ss.getId(), "dt_start"));
                    dtend = ymd.parse(getAttrValue(ss.getId(), "dt_end"));
                    if(between(new Date(), plusDay(dtend,-rule11), dtend)){
                        notices.add(
                            noticeService2.makeNotice(
                                SysNotice.Type.WORKFLOW, ss.getDealUserId(), String.format("你的任务 %s(任务编号:%d) 诉前保全期限(%s)即将届满", ss.getTitle(), ss.getId(), ymd.format(dtend)),C.newMap("taskId", ss.getId() + "")
                            )
                        );
                    }
                }
                catch (Exception e) {
                }
            }

            //规则12
            List<WfIns> sss12 = sqlManager.lambdaQuery(WfIns.class)
                .andEq(WfIns::getModelName, "诉讼")
                .andEq(WfIns::getParentId, wfIns.getId())
                .andEq(WfIns::getCurrentNodeName, "案件跟进")
                .andEq(WfIns::getState, WfIns.State.DEALING)
                .select(
                    WfIns::getId, WfIns::getDealUserId, WfIns::getTitle, WfIns::getCurrentNodeInstanceId
                );
            for (WfIns ss : sss12) {
                Date dt_1 = null;
                try {
                    dt_1 = ymd.parse(getAttrValue(ss.getId(), "dt_1"));
                    if(between(now, plusDay(dt_1, -rule12), dt_1)){
                        notices.addAll(
                            noticeService2.makeNotice(SysNotice.Type.WORKFLOW, getCurrentDealers(ss), String.format("你的任务 %s(任务编号:%s) 即将开庭, 开庭时间 %s, 请及时跟进", ss.getTitle(), ss.getId(), ymd.format(dt_1)),C.newMap("taskId", ss.getId() + ""))
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //规则13
            List<WfIns> sss13 = sqlManager.lambdaQuery(WfIns.class)
                .andEq(WfIns::getModelName, "诉讼")
                .andEq(WfIns::getParentId, wfIns.getId())
                .andEq(WfIns::getCurrentNodeName, "判决生效，执行")
                .andEq(WfIns::getState, WfIns.State.DEALING)
                .select(
                    WfIns::getId, WfIns::getDealUserId, WfIns::getTitle, WfIns::getCurrentNodeInstanceId
                );
            for (WfIns ss : sss13) {
                Date dt_3 = null;
                Date dt_2 = null;
                try {
                    dt_3 = ymd.parse(getAttrValue(ss.getId(), "dt_3"));
                    dt_2 = ymd.parse(getAttrValue(ss.getId(), "dt_2"));
                    if(between(now, plusDay(dt_3, -rule13), dt_3)){
                        notices.addAll(
                            noticeService2.makeNotice(
                                SysNotice.Type.WORKFLOW
                                , getCurrentDealers(ss)
                                , String.format("你的任务 %s(任务ID: %d) 判决生效(判决公告日期: %s, 判决生效结束日期: %s)即将到期", ss.getTitle(), ss.getId(), ymd.format(dt_2), ymd.format(dt_3))
                                ,C.newMap("taskId", ss.getId() + "")
                            )
                        );
                    }
                } catch (Exception e) {
                }
            }

            //规则14
            List<WfIns> sss14 = sqlManager.lambdaQuery(WfIns.class)
                .andEq(WfIns::getModelName, "抵债资产接收")
                .andEq(WfIns::getParentId, wfIns.getId())
                .andEq(WfIns::getCurrentNodeName, "入账处理")
                .andEq(WfIns::getState, WfIns.State.DEALING)
                .select(
                    WfIns::getId, WfIns::getDealUserId, WfIns::getTitle, WfIns::getCurrentNodeInstanceId
                );
            for (WfIns ss : sss14) {
                Date dt_2 = null;
                try {
                    dt_2 = ymd.parse(getAttrValue(ss.getId(), "dt_2"));
                    if(between(now, plusDay(dt_2, -rule14), dt_2)){
                        notices.add(
                            noticeService2.makeNotice(
                                SysNotice.Type.WORKFLOW
                                , ss.getDealUserId()
                                , String.format("评估报告有效期 %s 即将届满", ymd.format(dt_2))
                                ,C.newMap("taskId", ss.getId() + "")
                            )
                        );
                    }
                } catch (Exception e) {
                }
            }

            //规则15
            List<WfIns> sss15 = sqlManager.lambdaQuery(WfIns.class)
                .andEq(WfIns::getModelName, "诉讼")
                .andEq(WfIns::getParentId, wfIns.getId())
                .andEq(WfIns::getCurrentNodeName, "法院立案")
                .andEq(WfIns::getState, WfIns.State.DEALING)
                .select(
                    WfIns::getId, WfIns::getDealUserId, WfIns::getTitle, WfIns::getCurrentNodeInstanceId
                );
            for (WfIns ss : sss15) {
                Date la_end = null;
                try {
                    la_end = ymd.parse(getAttrValue(ss.getId(), "la_end"));
                    if(between(now, plusDay(la_end, -rule15), la_end)){
                        notices.addAll(
                            noticeService2.makeNotice(
                                SysNotice.Type.WORKFLOW
                                , getCurrentDealers(ss)
                                , String.format("你的任务 %s(任务编号:%d) 法院立案截止日 %s ", ss.getTitle(), ss.getId(), sdf.format(la_end))
                                , C.newMap("taskId", ss.getId() + "")
                            )
                        );
                    }
                } catch (Exception e) {
                }
            }

            //规则16
            List<WfIns> sss16 = sqlManager.lambdaQuery(WfIns.class)
                .andEq(WfIns::getModelName, "诉讼")
                .andEq(WfIns::getParentId, wfIns.getId())
                .andEq(WfIns::getCurrentNodeName, "缴费")
                .andEq(WfIns::getState, WfIns.State.DEALING)
                .select(
                    WfIns::getId, WfIns::getDealUserId, WfIns::getTitle, WfIns::getCurrentNodeInstanceId
                );
            for (WfIns ss : sss16) {
                Date jf_end = null;
                try {
                    jf_end = ymd.parse(getAttrValue(ss.getId(), "jf_end"));
                    if(between(now, plusDay(jf_end, -rule16), jf_end)){
                        notices.addAll(
                            noticeService2.makeNotice(
                                SysNotice.Type.WORKFLOW
                                , getCurrentDealers(ss)
                                , String.format("你的任务 %s(任务编号:%d) 缴费截止日 %s ", ss.getTitle(), ss.getId(), sdf.format(jf_end))
                                , C.newMap("taskId", ss.getId() + "")
                            )
                        );
                    }
                } catch (Exception e) {
                }
            }
        }

        sqlManager.insertBatch(SysNotice.class, notices);
    }


    /**
     * 查出当前节点的可执行人
     * @param ss
     * @return
     */
    private List<Long> getCurrentDealers(WfIns ss){
        List<Long> uids = sqlManager.lambdaQuery(WfNodeDealer.class)
            .andIn(WfNodeDealer::getType, C.newList(WfNodeDealer.Type.CAN_DEAL,WfNodeDealer.Type.DID_DEAL))
            .andEq(WfNodeDealer::getInsId, ss.getId())
            .andEq(WfNodeDealer::getNodeId, ss.getCurrentNodeInstanceId())
            .select(WfNodeDealer::getUid)
            .stream()
            .map(WfNodeDealer::getUid)
            .distinct()
            .collect(toList());
        return uids;
    }

    /**
     * 查询单一属性
     * 只查询最新的
     *
     * @param insId
     * @param key
     * @return
     */
    private String getAttrValue(long insId, String key){
        WfInsAttr attr = sqlManager.lambdaQuery(WfInsAttr.class)
            .andEq(WfInsAttr::getInsId, insId)
            .andEq(WfInsAttr::getAttrKey,key)
            .orderBy("last_modify_date desc")
            .single();
        if(null == attr){
            return "";
        }
        if(S.blank(attr.getAttrValue())){
            return "";
        }
        return attr.getAttrValue();
    }

    /**
     * 日期操作
     * @param date
     * @param num
     * @return
     */
    private Date plusYear(Date date, int num){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, num);
        return calendar.getTime();
    }
    private Date plusDay(Date date, int num){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num);
        return calendar.getTime();
    }
    private boolean between(Date date, Date min, Date max){
        return date.after(min) && date.before(max);
    }

//    public void rule10(OutputStream os){
//        List<Map> res = sqlManager.select("task.selectRule10UL", Map.class, C.newMap(
//                "day0", getConfig("MSG_RULE_10_VAL1"),
//                "day1", getConfig("MSG_RULE_10_VAL2")
//        ));
//        println( "找到待处理条数%d", res.size());
//        for (Map item : res) {
//            final Long uid = (Long) item.get("DEAL_USER_ID");
//            if($.isNull(uid)){
//                continue;
//            }
//            writeNotice(SysNotice.Type.WORKFLOW, uid, String.format("你的不良资产管理任务(任务编号: %s)诉讼时效将至, 请注意及时发起催收或起诉", item.get("ID") + "", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
//            writeLog(n, String.valueOf(item.get("ID")),null);
//        }
//    }

//    public void rule11( final int n){
//        List<Map> res = sqlManager.select("task.selectRule11UL", Map.class, C.newMap(
//                "day", getConfig("MSG_RULE_11")
//        ));
//        println( "找到待处理条数%d", res.size());
//        for (Map item : res) {
//            final Long uid = (Long) item.get("DEAL_USER_ID");
//            if($.isNull(uid)){
//                continue;
//            }
//            writeNotice(SysNotice.Type.WORKFLOW, uid, String.format("你的任务 %s(任务编号:%s) 诉前保全期限即将届满", item.get("TITLE"), item.get("ID")),C.newMap("taskId", item.get("ID")));
//            writeLog(n, String.valueOf(item.get("ID")),null);
//        }
//    }

//    public void rule12(final int n){
//        List<Map> res = sqlManager.select("task.selectRule12UL", Map.class, C.newMap(
//                "day",  getConfig("MSG_RULE_12")
//        ));
//        println( "找到待处理条数%d", res.size());
//        for (Map item : res) {
//            final Long uid = (Long) item.get("DEAL_USER_ID");
//            if($.isNull(uid)){
//                continue;
//            }
//            writeNotice(SysNotice.Type.WORKFLOW, uid, String.format("你的任务 %s(任务编号:%s) 即将开庭, 开庭时间 %s, 请及时跟进", item.get("TITLE"), item.get("ID"), sdf.format(item.get("STIME"))),C.newMap("taskId", item.get("ID")));
//            writeLog(n, String.valueOf(item.get("ID")),null);
//        }
//    }

//    public void rule13( final int n){
//        List<Map> res = sqlManager.select("task.selectRule13UL", Map.class, C.newMap(
//                "day",  getConfig("MSG_RULE_13")
//        ));
//        println( "找到待处理条数%d", res.size());
//        for (Map item : res) {
//            final Long uid = (Long) item.get("DEAL_USER_ID");
//            if($.isNull(uid)){
//                continue;
//            }
//            writeNotice(SysNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 判决生效即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
//            writeLog(n, String.valueOf(item.get("ID")),null);
//        }
//    }

    public void rule14( final int n){
        List<Map> res = sqlManager.select("task.selectRule14UL", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_14")
        ));
        println( "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SysNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 评估报告有效期即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

//    public void rule15( final int n){
//        List<Map> res = sqlManager.select("task.selectRule15UL", Map.class, C.newMap(
//                "day",  getConfig("MSG_RULE_15")
//        ));
//        println( "找到待处理条数%d", res.size());
//        for (Map item : res) {
//            final Long uid = (Long) item.get("DEAL_USER_ID");
//            if($.isNull(uid)){
//                continue;
//            }
//            writeNotice(SysNotice.Type.WORKFLOW, uid, String.format("你的任务 %s(任务编号:%s) 法院立案时效至 %s", item.get("TITLE"), item.get("ID"), sdf.format(item.get("ETIME"))),C.newMap("taskId", item.get("ID")));
//            writeLog(n, String.valueOf(item.get("ID")),null);
//        }
//    }

//    public void rule16( final int n){
//        List<Map> res = sqlManager.select("task.selectRule16UL", Map.class, C.newMap(
//                "day",  getConfig("MSG_RULE_16")
//        ));
//        println( "找到待处理条数%d", res.size());
//        for (Map item : res) {
//            final Long uid = (Long) item.get("DEAL_USER_ID");
//            if($.isNull(uid)){
//                continue;
//            }
//            writeNotice(SysNotice.Type.WORKFLOW, uid, String.format("你的任务 %s(任务编号:%s) 缴费时效至 %s", item.get("TITLE"), item.get("ID"), sdf.format(item.get("ETIME"))),C.newMap("taskId", item.get("ID")));
//            writeLog(n, String.valueOf(item.get("ID")),null);
//        }
//    }

    /***** 检查end ****/

    /***** 自动生成任务start *****/
    public void generateAutoTask(OutputStream os){
        Map<String,String> configs = getConfigs();
        Pattern pattern = Pattern.compile("ACC_(\\d+)_");
        List<Integer> idex = configs.entrySet().stream()
                .map(entry -> {
                    Matcher matcher = pattern.matcher(entry.getKey());
                    return matcher.find() ? matcher.group(1) : null;
                })
                .filter($::isNotNull)
                .distinct()
                .map(Integer::parseInt)
                .sorted()
                .collect(toList());
        for (int id : idex) {
            println(os, "");
            println(os, "***************");
            println(os, "开始检查规则%s", id);
            String BIZ_TYPE_SUB = configs.get(String.format("ACC_%s_BIZ_TYPE", id));
            String LOAN_AMOUNT_MAX = (configs.get(String.format("ACC_%s_LOAN_AMOUNT_MAX", id)));
            String LOAN_AMOUNT_MIN = (configs.get(String.format("ACC_%s_LOAN_AMOUNT_MIN", id)));
            String LOAN_CHECK = (configs.get(String.format("ACC_%s_LOAN_CHECK", id)));
            String EXPECT_DAY = (configs.get(String.format("ACC_%s_EXPECT_DAY", id)));
            if(S.isBlank(BIZ_TYPE_SUB) || S.isBlank(LOAN_AMOUNT_MAX) || S.isBlank(LOAN_CHECK) || S.isBlank(EXPECT_DAY) || S.isBlank(LOAN_AMOUNT_MIN)){
                println(os, "配置错误");
                continue;
            }
            List<Map> res = sqlManager.select("task.selectGentaskUL",Map.class, C.newMap(
                    "v0",BIZ_TYPE_SUB,
                    "v1",LOAN_AMOUNT_MIN,
                    "v2", LOAN_AMOUNT_MAX,
                    "v3", LOAN_CHECK,
                    "v4", EXPECT_DAY
            ));
            println(os, "找到待处理条数%d", res.size());
            for (Map item : res) {
                String loanAccount = (String) item.get("LOAN_ACCOUNT");
                //确定任务模型名
                List<JSONObject> mlist = sqlManager.execute(new SQLReady(S.fmt("values FUNC_GET_MODEL_BY_LOAN_ACCOUNT(%s,'贷后跟踪')", loanAccount)),JSONObject.class);
                if(mlist.size() == 0){
                    println(os, "贷款账号%s: 找不到任务对应的模型", loanAccount);
                    continue;
                }
                //查找模型
                String modelName = mlist.get(0).getString("1");
                if(S.empty(modelName)){
                    continue;
                }
                //查找任务执行人
                User user = sqlManager.lambdaQuery(User.class).andEq(User::getAccCode, item.get("CUST_MGR")).single();
                if($.isNull(user)){
                    println(os, "贷款账号%s: 找不到对应的任务执行人", loanAccount);
                    continue;
                }
                //查询信息
                Map data = C.newMap();
                try{
                    String no = String.valueOf(SearchService.InnateMap.getOrDefault(modelName,0));
                    if(no.equals("0")){
                        println(os,"贷款账号%s: 查询不到台账信息", loanAccount);
                        continue;
                    }
                    PageQuery<JSONObject> pageQuery = searchService.search(no,C.newMap("LOAN_ACCOUNT",loanAccount, "page", 1, "size", 100), 0, true);
                    if(pageQuery.getList().size() > 0){
                        data.putAll(pageQuery.getList().get(0));
                    }
                }
                finally {
                    if(0 == data.size()){
                        println(os,"贷款账号%s: 查询不到台账信息", loanAccount);
                        continue;
                    }
                }
                //rpc调用生成任务
                //todo:
                String err = "";
                WfIns ins = new WfIns();
                ins.setModelName(modelName);
                ins.setPubUserId(user.getId());
                ins.setDealUserId(user.getId());
                ins.setPlanStartTime(new Date());
                ins.setTitle(modelName);
                ins.set("$data", JSON.toJSON(data));
                ins.set("$startNodeData", new JSONObject());
                ins.set("$uid", user.getId());
                ins.setAutoCreated(true);
                ins.valid(ins, ValidGroup.Add.class);
                ins.onBeforeAdd(sqlManager);
                ins.onAdd(sqlManager);
//                String err = workflowService2.autoStartTask("ACC_LOAN",item.get("LOAN_ACCOUNT").toString(), modelName, user.getId(), data, ((Date) item.getOrDefault("PLAN_START_TIME", new Date())).getTime() );
                if(S.notEmpty(err)){
                    println( os, "贷款账号%s: 自动生成任务失败, 错误信息: %s", loanAccount, err);
                    continue;
                }
                println(os, "贷款账号%s: 自动生成任务成功", loanAccount, loanAccount);
            }
        }
    }
    /***** 自动生成任务end   *****/


    private void beginChcek(final int num){
        println("");
        println("***********************");
        println("开始检查规则%d",num);
    }

    private void endCheck(final int num){
        println("规则%d结束检查", num);
        println("***********************");
        println("");
    }

    private void println(final String template, Object ...args){
        log.info(String.format(template,args)); 
    }
    private void println(OutputStream os, String template, Object ...args){
        try {
            os.write(S.fmt(template,args).getBytes());
            os.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 短信开关是否开着
     * @return
     */
    private boolean isMsgOpen(){
        return S.eq(getConfig("msg_api_open"),"1");
    }

    /**
     * get all configs
     * @return
     */
    private Map<String,String> getConfigs(){
        Map<String,String> ret = C.newMap();
        List<SysVar> list = sqlManager.lambdaQuery(SysVar.class).select();
        for (SysVar sysVar : list) {
            if(S.blank(sysVar.getVarName())){
                continue; 
            }
            ret.put(sysVar.getVarName().toUpperCase(), sysVar.getVarValue());
        }
        return ret;
    }

    /**
     * get config value by single key
     * @param key
     * @return
     */
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
     * get all message templates
     * @return
     */
    private Map<String, MsgTmpl> getTemplates(){
        return sqlManager.lambdaQuery(MsgTmpl.class).select().stream().collect(toMap(MsgTmpl::getName, t->t, (v1, v2) -> v1));
    }

    /**
     * get a message template
     * @param no
     * @return
     */
    private MsgTmpl getTemplate(int no){
        return sqlManager.query(MsgTmpl.class)
            .andEq("name", getConfig(S.fmt("MSG_RULE_%d_TMPL", no)))
            .single();
    }


    /**
     * send system notice to user
     *
     * @param type
     * @param uid
     * @param content
     * @param bindData
     */
    private void writeNotice(final SysNotice.Type type, final long uid, final String content, Map bindData){
        if($.isNull(bindData)){
            bindData = new HashMap();
        }
        SysNotice notice = new SysNotice();
        notice.setAddTime(new Date());
        notice.setUserId(uid);
        notice.setType(type);
        notice.setState(SysNotice.State.UNREAD);
        notice.setContent(content);
        notice.setBindData(JSON.toJSONString(bindData));
        int row = sqlManager.insert(notice);
        if(0 == row){
            throw new RuntimeException("写入消息错误, 开始回滚数据");
        }
    }

    /**
     * write trigger log
     *
     * @param n
     * @param uuid
     * @param date
     */
    private void writeLog(final int n, final String uuid, Date date){
        if($.isNull(date)) {
            date = new Date();
        }
        NoticeTriggerLog log = new NoticeTriggerLog();
        log.setRuleName("rule" + n);
        log.setTriggerTime(date);
        log.setUuid(uuid);
        int row = sqlManager.insert(log);
        if(0 == row){
            throw new RuntimeException("写入日志错误, 开始回滚数据");
        }
    }

    private NoticeTriggerLog makeLog(final int n, final String uuid, Date date){
        if($.isNull(date)) {
            date = new Date();
        }
        NoticeTriggerLog log = new NoticeTriggerLog();
        log.setRuleName("rule" + n);
        log.setTriggerTime(date);
        log.setUuid(uuid);
        return log;
    }
    private NoticeTriggerLog makeLog(int n, String uuid, String uuid2, Date date){
        if(null == date){
            date = new Date();
        }
        NoticeTriggerLog log = new NoticeTriggerLog();
        log.setRuleName("rule" + n);
        log.setTriggerTime(date);
        log.setUuid(uuid);
        log.setUuid2(uuid2);
        return log;
    }

    /**
     * write showrt message log
     * @param phone
     * @param msg
     */
    private void writeShortMessageLog(final String phone, final String msg){
        ShortMsg shortMsg = new ShortMsg();
        shortMsg.setPhone(phone);
        shortMsg.setMessage(msg);
        shortMsg.setAddTime(new Date());
        int row = sqlManager.insert(shortMsg);
        if(0 == row){
            throw new RuntimeException("写入短信日志错误, 开始回滚数据");
        }
    }


    private ShortMsg makeShortMessageLog(final String phone, String msg){
        ShortMsg sm = new ShortMsg();
        sm.setPhone(phone);
        sm.setAddTime(new Date());
        sm.setMessage(msg);
        sm.setState(-10086);
        return sm;
    }

    private boolean isPhone(String phone){
        return isMsgOpen() && S.notBlank(phone) && phone.trim().length() == 11;
    }

    private String renderMessage(MsgTmpl template, JSONObject object){
        return renderMessage(template.getTemplate(), object);
    }

    private String renderMessage(String template, JSONObject object){
        Matcher m = pattern.matcher(template);
        StringBuffer sb = new StringBuffer();
        while(m.find()){
            String idex = m.group(1).trim();
            if(object.containsKey(idex)){
                m.appendReplacement(sb, (object.getString(idex)));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String renderMessage(MsgTmpl template, String la){
        String renderStr = "";
        try{
            List<Map> singleItem = sqlManager.execute(new SQLReady(template.getPlaceholder().replace("#LOAN_ACCOUNT#", "'" + la + "'")), Map.class);
            if(0 == singleItem.size()){
                return "";
            }
            Map<String,Object> params = singleItem.get(0);
            Matcher m = pattern.matcher(template.getTemplate());
            StringBuffer sb = new StringBuffer();
            while(m.find()){
                String idex = m.group(1).trim();
                if(params.containsKey(idex)){
                    m.appendReplacement(sb, String.valueOf(params.get(idex)));
                }
            }
            m.appendTail(sb);
            return renderStr = sb.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
