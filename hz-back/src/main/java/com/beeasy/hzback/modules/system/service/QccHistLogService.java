package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.*;
import com.beeasy.hzdata.service.CheckService;
import com.beeasy.hzdata.service.SearchService;
import com.beeasy.mscommon.valid.ValidGroup;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    private Pattern pattern = Pattern.compile("\\{(.+?)\\}");
    String logDir;

    public QccHistLogService(@Value("${uploads.path}") String dir) {
        File file = new File(dir, "logs");
        if (!file.exists()) file.mkdirs();
        logDir = file.getAbsolutePath();
    }

    public void saveQccHisLog() {

        JSONObject object = new JSONObject();
        Object a = sqlManager.select("qcc.对公客户", JSONObject.class, object);
        String str = a.toString();

        Map<String, Integer> map = new HashMap<>();

        JSONArray json = (JSONArray) JSONArray.parse(str);
        // 对公客户
        for (Object jsonObject : json) {
            JSONObject jObject = (JSONObject) jsonObject;
            String customerName = jObject.getString("cusName");

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
        OutputStream os = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            os = new FileOutputStream(new File(dir, sdf.format(new Date()) + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!map.isEmpty()) {

            JSONObject jsonObj = new JSONObject();
            Set<Object> sendPersonSet = new HashSet<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                System.out.println(entry.getKey());

                switch (entry.getKey()) {
                    // 失信信息
                    case "searchShiXin":
                        qccRule1(os, jsonObj, "searchShiXin", "01");
                        break;
                    // 被执行信息
                    case "searchZhiXing":
                        qccRule1(os, jsonObj, "searchZhiXing", "02");
                        break;
                    // 裁判文书
                    case "searchJudgmentDoc":
                        qccRule1(os, jsonObj, "searchJudgmentDoc", "03");
                        break;
                    // 法院公告
                    case "searchCourtAnnouncement":
                        qccRule1(os, jsonObj, "searchCourtAnnouncement", "04");
                        break;
                    // 开庭公告
                    case "searchCourtNotice":
                        qccRule1(os, jsonObj, "searchCourtNotice", "05");
                        break;
                    // 司法拍卖
                    case "judicialSaleList":
                        qccRule1(os, jsonObj, "judicialSaleList", "06");
                        break;
                    // 环保处罚
                    case "envPunishmentList":
                        qccRule1(os, jsonObj, "envPunishmentList", "07");
                        break;
                    // 司法协助
                    case "judicialAssistance":
                        qccRule1(os, jsonObj, "judicialAssistance", "08");
                        break;
                    // 经营异常
                    case "opException":
                        qccRule1(os, jsonObj, "opException", "09");
                        break;
                }
            }

            List<SysNotice> notices = new ArrayList<>();
            Iterator<String> sIterator = jsonObj.keySet().iterator();

            while (sIterator.hasNext()) {
                // 获得key
                String key = sIterator.next();
                Long uid = null;

                //获得key值对应的value
                JSONArray ja1 = jsonObj.getJSONArray(key);

                for (Object obj : ja1) {

                    JSONObject jo = (JSONObject) obj;
                    String loanAccount1 = jo.getString("loanAccount");
                    String cusName = jo.getString("cusName");
                    uid = jo.getLong("uid");

                    String typeText = "";
                    String info1 = "";
                    String renderStr = "对公客户：" + cusName + "，贷款账号：" + loanAccount1 + "，该用户新增";

                    //判断是否包含指定的键值
                    boolean shixin = map.containsKey("searchShiXin");
                    boolean zhixing = map.containsKey("searchZhiXing");
                    boolean judgmentDoc = map.containsKey("searchJudgmentDoc");
                    boolean courtAnnouncement = map.containsKey("searchCourtAnnouncement");
                    boolean courtNotice = map.containsKey("searchCourtNotice");
                    boolean judicialSaleList = map.containsKey("getJudicialSaleList");
                    boolean envPunishmentList = map.containsKey("getEnvPunishmentList");
                    boolean judicialAssistance = map.containsKey("getJudicialAssistance");
                    boolean opException = map.containsKey("getOpException");
                    if (shixin) {         //如果条件为真
                        int shixinInt = map.get("searchShiXin");
                        renderStr = renderStr + shixinInt + "条失信信息，";
                    }
                    if (zhixing) {
                        renderStr = renderStr + map.get("searchZhiXing") + "条被执行信息，";
                    }
                    if (judgmentDoc) {
                        renderStr = renderStr + map.get("searchJudgmentDoc") + "条裁判文书，";
                    }
                    if (courtAnnouncement) {
                        renderStr = renderStr + map.get("searchCourtAnnouncement") + "条法院公告，";
                    }
                    if (courtNotice) {
                        renderStr = renderStr + map.get("searchCourtNotice") + "条开庭公告，";
                    }
                    if (judicialSaleList) {
                        renderStr = renderStr + map.get("judicialSaleList") + "条司法拍卖，";
                    }
                    if (envPunishmentList) {
                        renderStr = renderStr + map.get("envPunishmentList") + "条环保处罚，";
                    }
                    if (judicialAssistance) {
                        renderStr = renderStr + map.get("judicialAssistance") + "条司法协助，";
                    }
                    if (opException) {
                        renderStr = renderStr + map.get("opException") + "条经营异常";
                    }
                    if (renderStr.length() > 0 && !("").equals(renderStr)) {
                        renderStr = renderStr.substring(0, renderStr.length() - 1);
                    }

                    try {
                        // 对公客户：【客户名称】，贷款账号：【贷款账号】，该用户新增【数量】条失信信息，【数量】条被执行信息。。。
                        notices.add(
                                noticeService2.makeNotice(SysNotice.Type.SYSTEM, uid, renderStr, null)
                        );
                        //写入日志
//                        logs.add(makeLog(1, re.getString("LOAN_ACCOUNT"), re.getString("REC_NO") ,null));
                    } catch (Exception e) {
                        println(os, e.getMessage(), null);
                    }

                    generateAutoTask(os, jo.getString("accCode"), jo.getString("loanAccount"));
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
    }

    private List<SysNotice> ssss(List<SysNotice> notices, JSONObject jsonObj, OutputStream os, String key, String renderStr, String text) {
        //获得key值对应的value
        JSONArray ja = jsonObj.getJSONArray(key);
        for (Object obj : ja) {
            JSONObject jo = (JSONObject) obj;
            String loanAccount1 = jo.getString("loanAccount");
            String cusName = jo.getString("cusName");
            Long uid = jo.getLong("uid");

            JSONObject re = new JSONObject();
            re.put("uid", uid);
            try {
                // 对公客户：【客户名称】，贷款账号：【贷款账号】，该用户新增【数量】条失信信息，。。。
//                    String renderStr = renderMessage(template, re);
                renderStr = "对公客户：" + cusName + "，贷款账号：" + loanAccount1 + "，该用户新增" + ja.size() + "条" + text;
                notices.add(
                        noticeService2.makeNotice(SysNotice.Type.SYSTEM, uid, renderStr, null)
                );
                //写入日志
//                        logs.add(makeLog(1, re.getString("LOAN_ACCOUNT"), re.getString("REC_NO") ,null));
            } catch (Exception e) {
                println(os, e.getMessage(), null);
            }
            generateAutoTask(os, jo.getString("accCode"), jo.getString("loanAccount"));
        }
        return notices;
    }

    public void qccRule1(OutputStream os, JSONObject jsonObj, String key, String type) {

        List<JSONObject> res = (sqlManager.select("task.selectQccRule1", JSONObject.class, C.newMap("type", type)));
        JSONObject object;
        JSONArray jsonArr = new JSONArray();

        for (JSONObject re : res) {
            // 客户经理
            Long uid = re.getLong("uid");
            // 信贷主管
            Long cusId = re.getLong("cusid");

            String loanAccount = re.getString("loanaccount");
            String cusName = re.getString("cusname");

            object = new JSONObject();
            object.put("uid", uid);
            object.put("cusId", cusId);
            object.put("loanAccount", loanAccount);
            object.put("accCode", re.getString("code"));
            object.put("cusName", cusName);

            jsonArr.add(object);
            jsonObj.put(key, jsonArr);

        }
//        return sendPersonSet;

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

    private void println(OutputStream os, String template, Object... args) {
        try {
            os.write(S.fmt(template, args).getBytes());
            os.write("\n".getBytes());
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

        //  查找任务执行人
        User user = sqlManager.lambdaQuery(User.class).andEq(User::getAccCode, CUST_MGR).single();
        if ($.isNull(user)) {
            println(os, "贷款账号%s: 找不到对应的任务执行人", loanAccount);
            return;
        }

        JSONObject data = new JSONObject();
        try {
            String no = String.valueOf(SearchService.InnateMap.getOrDefault(modelName, 0));
            if (no.equals("0")) {
                println(os, "贷款账号%s: 查询不到台账信息", loanAccount);
                return;
            }
            data = sqlManager.selectSingle("accloan." + no, C.newMap("loanAccount", loanAccount), JSONObject.class);
        } finally {
            if (0 == data.size()) {
                println(os, "贷款账号%s: 查询不到台账信息", loanAccount);
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
}
