package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.*;
import com.beeasy.hzdata.service.CheckService;
import com.beeasy.hzdata.service.SearchService;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.valid.ValidGroup;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
public class QccHistLogService {

    @Autowired
    private SQLManager sqlManager;
    @Autowired
    QccHistLogAsyncService qccHistLogAsyncService;
    @Autowired
    CheckService checkService;
    @Autowired
    NoticeService2 noticeService2;
    @Autowired
    SearchService searchService;
    public static OutputStream os = System.out;

    private Pattern pattern = Pattern.compile("\\{(.+?)\\}");
    String logDir;

    public QccHistLogService(@Value("${uploads.path}") String dir) {
        File file = new File(dir, "logs");
        if (!file.exists()) file.mkdirs();
        logDir = file.getAbsolutePath();
    }

    @Scheduled(cron = "0 30 8 * * ?")
    public synchronized void saveQccHisLog() {

        JSONObject object = new JSONObject();
        Object a = sqlManager.select("accloan.对公客户", JSONObject.class, C.newMap("uid", AuthFilter.getUid()));
        String str = a.toString();

        Map<String, Integer> map;
        Map<String, Boolean> sendRuleMap = new HashMap<>();

        JSONArray json = (JSONArray) JSONArray.parse(str);
        // 对公客户
        for (Object jsonObject : json) {
            map = new HashMap<>();
            JSONObject jObject = (JSONObject) jsonObject;
            String customerName = jObject.getString("CUS_NAME");

            long startTime = System.currentTimeMillis();    //获取开始时间
            // 失信信息
//            Future<String> t1 = qccHistLogAsyncService.searchShiXin("惠州市维也纳惠尔曼酒店管理有限公司", map);
            Future<String> t1 = qccHistLogAsyncService.searchShiXin(customerName, map);
            // 被执行信息
            Future<String> t2 = qccHistLogAsyncService.searchZhiXing(customerName, map);
            // 裁判文书
            Future<String> t3 = qccHistLogAsyncService.searchJudgmentDoc(customerName, map);
            // 法院公告
            Future<String> t4 = qccHistLogAsyncService.searchCourtAnnouncement(customerName, map);
            // 开庭公告
            Future<String> t5 = qccHistLogAsyncService.searchCourtNotice(customerName, map);
            // 司法拍卖
            Future<String> t6 = qccHistLogAsyncService.getJudicialSaleList(customerName, map);
            // 环保处罚
            Future<String> t7 = qccHistLogAsyncService.getEnvPunishmentList(customerName, map);
            // 司法协助
            Future<String> t8 = qccHistLogAsyncService.getJudicialAssistance(customerName, map);
            // 经营异常
            Future<String> t9 = qccHistLogAsyncService.getOpException(customerName, map);
            while (true) {
                if (t1.isDone() && t2.isDone() && t3.isDone() && t4.isDone() && t5.isDone() && t6.isDone() && t7.isDone() && t8.isDone() && t9.isDone()) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            long endTime = System.currentTimeMillis();    //获取结束时间
            System.out.println("---------------程序运行时间--：" + (endTime - startTime) + "ms");
            System.out.println("新增数据量------------------:" + map);

            File dir = new File(logDir);
//            OutputStream os = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                os = new FileOutputStream(new File(dir, sdf.format(new Date()) + ".txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!map.isEmpty()) {

                JSONObject jsonObj = new JSONObject();
                JSONObject jsonTaskObj = new JSONObject();
                for (Map.Entry<String, Integer> entry : map.entrySet()) {

                    switch (entry.getKey()) {
                        // 失信信息
                        case "searchShiXin":
                            boolean flag1 = qccRule(os, jsonObj, jsonTaskObj,entry.getKey(), "01", "QCC_MSG_RULE_1_ON", "QCC_TASK_MSG_RULE_1_ON", customerName);
                            sendRuleMap.put(entry.getKey(),flag1);
                            break;
                        // 被执行信息
                        case "searchZhiXing":
                            boolean flag2 = qccRule(os, jsonObj, jsonTaskObj,entry.getKey(), "02", "QCC_MSG_RULE_2_ON", "QCC_TASK_MSG_RULE_2_ON", customerName);
                            sendRuleMap.put(entry.getKey(),flag2);
                            break;
                        // 裁判文书
                        case "searchJudgmentDoc":
                            boolean flag3 = qccRule(os, jsonObj, jsonTaskObj,entry.getKey(), "03", "QCC_MSG_RULE_3_ON", "QCC_TASK_MSG_RULE_3_ON", customerName);
                            sendRuleMap.put(entry.getKey(),flag3);
                            break;
                        // 法院公告
                        case "searchCourtAnnouncement":
                            boolean flag4 = qccRule(os, jsonObj, jsonTaskObj,entry.getKey(), "04", "QCC_MSG_RULE_4_ON", "QCC_TASK_MSG_RULE_4_ON", customerName);
                            sendRuleMap.put(entry.getKey(),flag4);
                            break;
                        // 开庭公告
                        case "searchCourtNotice":
                            boolean flag5 = qccRule(os, jsonObj, jsonTaskObj,entry.getKey(), "05", "QCC_MSG_RULE_5_ON", "QCC_TASK_MSG_RULE_5_ON", customerName);
                            sendRuleMap.put(entry.getKey(),flag5);
                            break;
                        // 司法拍卖
                        case "judicialSaleList":
                            boolean flag6 = qccRule(os, jsonObj, jsonTaskObj,entry.getKey(), "06", "QCC_MSG_RULE_6_ON", "QCC_TASK_MSG_RULE_6_ON", customerName);
                            sendRuleMap.put(entry.getKey(),flag6);
                            break;
                        // 环保处罚
                        case "envPunishmentList":
                            boolean flag7 = qccRule(os, jsonObj, jsonTaskObj,entry.getKey(), "07", "QCC_MSG_RULE_7_ON", "QCC_TASK_MSG_RULE_7_ON", customerName);
                            sendRuleMap.put(entry.getKey(),flag7);
                            break;
                        // 司法协助
                        case "judicialAssistance":
                            boolean flag8 = qccRule(os, jsonObj, jsonTaskObj,entry.getKey(), "08", "QCC_MSG_RULE_8_ON", "QCC_TASK_MSG_RULE_8_ON", customerName);
                            sendRuleMap.put(entry.getKey(),flag8);
                            break;
                        // 经营异常
                        case "opException":
                            boolean flag9 = qccRule(os, jsonObj, jsonTaskObj,entry.getKey(), "09", "QCC_MSG_RULE_9_ON", "QCC_TASK_MSG_RULE_9_ON", customerName);
                            sendRuleMap.put(entry.getKey(),flag9);
                            break;
                    }
                }

                List<SysNotice> notices = new ArrayList<>();
                Iterator<String> sIterator = jsonObj.keySet().iterator();

                // 任务规则
                Iterator<String> taskIterator = jsonTaskObj.keySet().iterator();
                while (taskIterator.hasNext()) {
                    String key = taskIterator.next();
                    //获得key值对应的value
                    JSONArray ja1 = jsonTaskObj.getJSONArray(key);

                    for (Object obj : ja1) {
                        JSONObject jo = (JSONObject) obj;
                        String loanAccount = jo.getString("loanAccount");
                        List<JSONObject> res = sqlManager.select("task.查询企查查贷后任务",JSONObject.class,C.newMap("loan",loanAccount));
                        if(res.size()>0){
                            continue;
                        }

                        generateAutoTask(os, jo.getString("accCode"), loanAccount);
                    }
                    break;
                }

                while (sIterator.hasNext()) {
                    // 获得key
                    String key = sIterator.next();
                    Long uid = null;    // 信贷主管
                    Long cusCode = null;    // 客户经理
                    // 客户经理map
                    Map<String, String> cusNameMap = new HashMap<>();
                    Map<String, String> loanMap = new HashMap<>();

                    //获得key值对应的value
                    JSONArray ja1 = jsonObj.getJSONArray(key);

                    for (Object obj : ja1) {

                        JSONObject jo = (JSONObject) obj;
                        String loanAccount1 = jo.getString("loanAccount");
                        String cusName = jo.getString("cusName");
                        uid = jo.getLong("uid");
                        cusCode = jo.getLong("cusId");

                        cusNameMap.put(loanAccount1 + "-" + cusCode, cusName);
                        loanMap.put(loanAccount1, cusName);
//                        String renderStr = getContent(map, loanAccount1, cusName);
//                        try {
                            // 对公客户：【客户名称】，贷款账号：【贷款账号】，该用户新增【数量】条失信信息，【数量】条被执行信息。。。
//                            if(null != uid){
//                                // 主管
//                                notices.add(
//                                        noticeService2.makeNotice(SysNotice.Type.SYSTEM, uid, renderStr, null)
//                                );
//                            }
                            //写入日志
//                        logs.add(makeLog(1, re.getString("LOAN_ACCOUNT"), re.getString("REC_NO") ,null));
//                        } catch (Exception e) {
//                            println( e.getMessage(), null);
//                        }

//                        generateAutoTask(os, jo.getString("accCode"), jo.getString("loanAccount"));
                    }

                    if(cusNameMap.size()>0 && null != cusNameMap){
                        for (Map.Entry<String, String> entry : cusNameMap.entrySet()){
                            String mKey = entry.getKey();
                            String loanAccount = mKey.substring(0, mKey.indexOf("-"));
                            String code = mKey.substring(mKey.indexOf("-")+1, mKey.length());
                            String renderStr = getContent(map, loanAccount, entry.getValue(), sendRuleMap);
                            // 给客户经理发送消息
                            notices.add(
                                    noticeService2.makeNotice(SysNotice.Type.SYSTEM, Long.valueOf(code), renderStr, null)
                            );

                            // 给支行行长/信贷主管发送消息
                            sendMsgToPresident(renderStr, notices, code);

                        }
                    }
                    if(loanMap.size()>0 && null != loanMap){
                        for (Map.Entry<String, String> entry : loanMap.entrySet()){
                            String renderStr = getContent(map, entry.getKey(), entry.getValue(), sendRuleMap);
                            // 给总行企查查风险角色发送消息
                            sendMsgTORule(renderStr, notices);

                        }
                    }

                    break;
                }

                sqlManager.insertBatch(SysNotice.class, notices);

                // 发送任务
            /*for(Map.Entry<String, String> entry : m1.entrySet()){
                generateAutoTask(os,entry.getKey(),entry.getValue());
            }*/

                //        sqlManager.insertBatch(NoticeTriggerLog.class, logs);

            }
        }

        // 两种传参方式
//        String str = HttpUtil.get("http://47.94.97.138/qcc/CourtV4/SearchShiXin?fullName="+ URLUtil.encode("惠州市帅星贸易有限公司", StandardCharsets.UTF_8) +"&pageIndex=1&pageSize=999");
//        String str =  HttpUtil.get("http://47.94.97.138/qcc/CourtV4/SearchShiXin", C.newMap(
//                "fullName", "惠州市帅星贸易有限公司","pageSize","999"
//        ));
        println("执行完成！");
    }

    /**
     * 给总行企查查风险角色发送消息
     */
    private void sendMsgTORule(String renderStr, List<SysNotice> notices){
        List<JSONObject> ruleList = sqlManager.select("user.查询总行企查查风险角色", JSONObject.class, C.newMap());
        if(null != ruleList && ruleList.size()>0){
            for(JSONObject rule : ruleList){
                Long ruleId = rule.getLong("uid");
                notices.add(
                        noticeService2.makeNotice(SysNotice.Type.SYSTEM, ruleId, renderStr, null)
                );
            }
        }
    }

    /**
     * 支行行长/信贷主管
     */
    private void sendMsgToPresident(String renderStr, List<SysNotice> notices, String uid){
        // 支行行长

        //查询客户经理所在部门下所有岗位
        List<JSONObject> oList = sqlManager.select("qcc.查询支行行长1", JSONObject.class, C.newMap("uid",uid));
        // 查询客户经理所在部门的父节点下所有部门
        List<JSONObject> pList = sqlManager.select("qcc.查询支行行长2", JSONObject.class, C.newMap("uid",uid));
        Set<Long> uidList = new HashSet<>();
        if(null != oList && oList.size()>0){
            for(JSONObject rule : oList){
                Long ruleId = rule.getLong("uid");
                uidList.add(ruleId);
            }
        }
        if(null != pList && pList.size()>0){
            for(JSONObject rule : pList){
                Long ruleId = rule.getLong("uid");
                uidList.add(ruleId);
            }
        }

        // 信贷主管
        List<JSONObject> ruleList = sqlManager.select("user.信贷主管", JSONObject.class, C.newMap("uid",uid));
        if(null != ruleList && ruleList.size()>0){
            for(JSONObject rule : ruleList){
                Long ruleId = rule.getLong("uid");
                uidList.add(ruleId);
            }
        }

        if(uidList.size()>0 && null != uidList){
            for(Long list : uidList){
                notices.add(
                        noticeService2.makeNotice(SysNotice.Type.SYSTEM, list, renderStr, null)
                );
            }
        }

    }

    public String getContent(Map<String ,Integer> map, String loanAccount1, String cusName, Map<String, Boolean> sendRuleMap){
        String renderStr = "对公客户：<a href=\"#\" class=\"forPublicCustomers_z\">" + cusName + "</a>，贷款账号：" + loanAccount1 + "，该用户新增";

        //判断是否包含指定的键值
        boolean shixin = map.containsKey("searchShiXin");
        boolean zhixing = map.containsKey("searchZhiXing");
        boolean judgmentDoc = map.containsKey("searchJudgmentDoc");
        boolean courtAnnouncement = map.containsKey("searchCourtAnnouncement");
        boolean courtNotice = map.containsKey("searchCourtNotice");
        boolean judicialSaleList = map.containsKey("getJudicialSaleList");
        boolean envPunishmentList = map.containsKey("getEnvPunishmentList");
        boolean judicialAssistance = map.containsKey("judicialAssistance");
        boolean opException = map.containsKey("opException");
        if (shixin) {         //如果条件为真
            int shixinInt = map.get("searchShiXin");
            boolean flag = sendRuleMap.get("searchShiXin");
            if(flag){
                renderStr = renderStr + shixinInt + "条失信信息，";
            }
        }
        if (zhixing) {
            boolean flag = sendRuleMap.get("searchZhiXing");
            if(flag){
                renderStr = renderStr + map.get("searchZhiXing") + "条被执行信息，";
            }
        }
        if (judgmentDoc) {
            boolean flag = sendRuleMap.get("searchJudgmentDoc");
            if(flag){
                renderStr = renderStr + map.get("searchJudgmentDoc") + "条裁判文书，";
            }
        }
        if (courtAnnouncement) {
            boolean flag = sendRuleMap.get("searchCourtAnnouncement");
            if(flag){
                renderStr = renderStr + map.get("searchCourtAnnouncement") + "条法院公告，";
            }
        }
        if (courtNotice) {
            boolean flag = sendRuleMap.get("searchCourtNotice");
            if(flag){
                renderStr = renderStr + map.get("searchCourtNotice") + "条开庭公告，";
            }
        }
        if (judicialSaleList) {
            boolean flag = sendRuleMap.get("getJudicialSaleList");
            if(flag){
                renderStr = renderStr + map.get("getJudicialSaleList") + "条司法拍卖，";
            }
        }
        if (envPunishmentList) {
            boolean flag = sendRuleMap.get("getEnvPunishmentList");
            if(flag){
                renderStr = renderStr + map.get("getEnvPunishmentList") + "条环保处罚，";
            }
        }
        if (judicialAssistance) {
            boolean flag = sendRuleMap.get("judicialAssistance");
            if(flag){
                renderStr = renderStr + map.get("judicialAssistance") + "条司法协助，";
            }
        }
        if (opException) {
            boolean flag = sendRuleMap.get("opException");
            if(flag){
                renderStr = renderStr + map.get("opException") + "条经营异常，";
            }
        }
        if (renderStr.length() > 0 && !("").equals(renderStr)) {
            renderStr = renderStr.substring(0, renderStr.length() - 1);
        }
        return renderStr;
    }

    public boolean qccRule(OutputStream os, JSONObject jsonObj, JSONObject jsonTaskObj, String key, String type, String rule, String taskRule, String cusName) {

        // 消息规则
            List<JSONObject> res = (sqlManager.select("task.selectQccMsgRule", JSONObject.class, C.newMap("type", type, "rule", rule ,"cusName", cusName)));

        // 任务规则
        List<JSONObject> taskRes = (sqlManager.select("task.selectQccTaskRule", JSONObject.class, C.newMap("type", type, "taskRule", taskRule,"cusName", cusName)));
        JSONObject object;
        JSONArray jsonArr = new JSONArray();
        JSONArray jsonTaskArr = new JSONArray();

        for (JSONObject re : res) {
            // 信贷主管
            Long uid = re.getLong("uid");
            // 客户经理
            Long cusId = re.getLong("cusid");

            String loanAccount = re.getString("loanaccount");
            String customerName = re.getString("cusname");

            object = new JSONObject();
            object.put("uid", uid);
            object.put("cusId", cusId);
            object.put("loanAccount", loanAccount);
            object.put("accCode", re.getString("code"));
            object.put("cusName", customerName);

            jsonArr.add(object);
            jsonObj.put(key, jsonArr);

        }

        for (JSONObject re : taskRes) {

            String loanAccount = re.getString("loanaccount");
            String customerName = re.getString("cusname");

            JSONObject taskObject = new JSONObject();
            taskObject.put("loanAccount", loanAccount);
            taskObject.put("accCode", re.getString("code"));
            taskObject.put("cusName", customerName);

            jsonTaskArr.add(taskObject);
            jsonTaskObj.put(key, jsonTaskArr);

        }
        if(null != res && res.size()>0){
            return true;
        }else{
            return false;
        }
    }

    private MsgTmpl getTemplate(int no) {
        return sqlManager.query(MsgTmpl.class)
                .andEq("name", getConfig(S.fmt("QCC_MSG_RULE_%d_TMPL", no)))
                .single();
    }

    private String getConfig(final String key) {
        SysVar sysVar = sqlManager.lambdaQuery(SysVar.class)
                .andEq(SysVar::getVarName, key)
                .single();
        if (null == sysVar) {
            return "";
        }
        return sysVar.getVarValue();
    }

    private String renderMessage(MsgTmpl template, JSONObject object) {
        return renderMessage(template.getTemplate(), object);
    }

    private String renderMessage(String template, JSONObject object) {
        Matcher m = pattern.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String idex = m.group(1).trim();
            if (object.containsKey(idex)) {
                m.appendReplacement(sb, (object.getString(idex)));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private void println(String template, Object... args) {
        try {
            os.write(S.fmt(template, args).getBytes());
            os.write("\n".getBytes(StandardCharsets.UTF_8.name()));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private NoticeTriggerLog makeLog(int n, String uuid, String uuid2, Date date) {
        if (null == date) {
            date = new Date();
        }
        NoticeTriggerLog log = new NoticeTriggerLog();
        log.setRuleName("qcc_rule" + n);
        log.setTriggerTime(date);
        log.setUuid(uuid);
        log.setUuid2(uuid2);
        return log;
    }

    /***** 自动生成任务start *****/
    public void generateAutoTask(OutputStream os, String CUST_MGR, String loanAccount) {
//    String loanAccount = "30020000004087531";

//    String CUST_MGR ="0024600";
        String modelName = "贷后跟踪-企查查贷后检查";
        System.out.println(">>>>>>>>>>>："+CUST_MGR);

        //  查找任务执行人
        User user = sqlManager.lambdaQuery(User.class).andEq(User::getAccCode, CUST_MGR).single();
        if ($.isNull(user)) {
            println( "贷款账号%s: 找不到对应的任务执行人", loanAccount);
            return;
        }

        JSONObject data = new JSONObject();
        try {
            String no = String.valueOf(SearchService.InnateMap.getOrDefault(modelName, 0));
            if (no.equals("0")) {
                println( "贷款账号%s: 查询不到台账信息", loanAccount);
                return;
            }
            data = sqlManager.selectSingle("accloan." + no, C.newMap("loanAccount", loanAccount), JSONObject.class);
        } finally {
            if (0 == data.size()) {
                println( "贷款账号%s: 查询不到台账信息", loanAccount);
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

    /**
     * get all configs
     *
     * @return
     */
    private Map<String, String> getConfigs() {
        Map<String, String> ret = C.newMap();
        List<SysVar> list = sqlManager.lambdaQuery(SysVar.class).select();
        for (SysVar sysVar : list) {
            if (S.blank(sysVar.getVarName())) {
                continue;
            }
            ret.put(sysVar.getVarName().toUpperCase(), sysVar.getVarValue());
        }
        return ret;
    }

    public void deleteQccHistLog(String cusName){
        if(!"".equals(cusName)){
            sqlManager.lambdaQuery(QccHistLog.class).andEq(QccHistLog::getFullName,cusName).delete();
            println(cusName+" 数据删除成功！");
        }

    }
}
