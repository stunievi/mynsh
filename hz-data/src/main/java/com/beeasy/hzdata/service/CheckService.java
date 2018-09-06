package com.beeasy.hzdata.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzdata.entity.*;
import com.beeasy.rpc.DubboService;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional
@Slf4j
public class CheckService {
    @Autowired
    SQLManager sqlManager;

    @Autowired
    DubboService dubboService;

    @Async
    public void clearTriggerLogs(){
        println("正在清除检查日志......");
        sqlManager.update("task.deleteTriggerLogs",C.newMap());
    }

    @Async
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
            method = getClass().getDeclaredMethod("rule" + n, int.class);
        } catch (NoSuchMethodException e) {
            //没有就略过
            return;
        }
        method.setAccessible(true);
        beginChcek(n);
        try {
            method.invoke(this, n);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        endCheck(n);
    }


    /***** 检查区域 ****/
    public void rule1( final int n){
        MessageTemplate template = getTemplate(getConfig("MSG_RULE_1_TMPL")).orElse(null);
        if(null == template){
            println("找不到消息模板");
            return;
        }
        List<Map> res = (sqlManager.select("task.selectRule1",Map.class, C.newMap(
                "day", getConfig("MSG_RULE_1"),
                "on", getConfig("MSG_RULE_1_ON")
        )));
        println( "找到待处理条数%d", res.size());
        for (Map<String, Object> item : res) {

        }

    }

    public void rule2(){

    }

    public void rule10( final int n){
        List<Map> res = sqlManager.select("task.selectRule10", Map.class, C.newMap(
                "day0", getConfig("MSG_RULE_10_VAL1"),
                "day1", getConfig("MSG_RULE_10_VAL2")
        ));
        println( "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的不良资产管理任务 %s 需要有催收或者诉讼的子流程", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule11( final int n){
        List<Map> res = sqlManager.select("task.selectRule11", Map.class, C.newMap(
                "day", getConfig("MSG_RULE_11")
        ));
        println( "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 诉前保全即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule12( final int n){
        List<Map> res = sqlManager.select("task.selectRule12", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_12")
        ));
        println( "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 开庭公告即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule13( final int n){
        List<Map> res = sqlManager.select("task.selectRule13", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_13")
        ));
        println( "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 判决生效即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule14( final int n){
        List<Map> res = sqlManager.select("task.selectRule14", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_14")
        ));
        println( "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 评估报告有效期即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule15( final int n){
        List<Map> res = sqlManager.select("task.selectRule15", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_15")
        ));
        println( "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("请尽快办理你的任务 %s", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule16( final int n){
        List<Map> res = sqlManager.select("task.selectRule16", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_16")
        ));
        println( "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("请尽快办理你的任务 %s", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    /***** 检查end ****/

    /***** 自动生成任务start *****/
    public void generateAutoTask(){
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
            println( "");
            println( "***************");
            println( "开始检查规则%s", id);
            String BIZ_TYPE_SUB = configs.get(String.format("ACC_%s_BIZ_TYPE", id));
            String LOAN_AMOUNT_MAX = (configs.get(String.format("ACC_%s_LOAN_AMOUNT_MAX", id)));
            String LOAN_AMOUNT_MIN = (configs.get(String.format("ACC_%s_LOAN_AMOUNT_MIN", id)));
            String LOAN_CHECK = (configs.get(String.format("ACC_%s_LOAN_CHECK", id)));
            String EXPECT_DAY = (configs.get(String.format("ACC_%s_EXPECT_DAY", id)));
            if(S.isBlank(BIZ_TYPE_SUB) || S.isBlank(LOAN_AMOUNT_MAX) || S.isBlank(LOAN_CHECK) || S.isBlank(EXPECT_DAY) || S.isBlank(LOAN_AMOUNT_MIN)){
                println( "配置错误");
                continue;
            }
            List<Map> res = sqlManager.select("task.selectGentask",Map.class, C.newMap(
                    "v0",BIZ_TYPE_SUB,
                    "v1",LOAN_AMOUNT_MIN,
                    "v2", LOAN_AMOUNT_MAX,
                    "v3", LOAN_CHECK,
                    "v4", EXPECT_DAY
            ));
            println( "找到待处理条数%d", res.size());
            for (Map item : res) {
                //确定任务模型名
                Map<String,String> map = sqlManager.selectSingle("task.selectModelName",C.newMap(), Map.class);
                String loanAccount = (String) item.get("LOAN_ACCOUNT");
                if(C.empty(map)){
                    println( "贷款账号%s: 找不到任务对应的模型", loanAccount);
                    continue;
                }
                //查找模型
                String modelName = map.get("MODEL_NAME");
                if(S.empty(modelName)){
                    continue;
                }
                //查找任务执行人
                User user = sqlManager.query(User.class).andEq("accCode", item.get("CUST_MGR")).single();
                if($.isNull(user)){
                    println( "贷款账号%s: 找不到对应的任务执行人", loanAccount);
                    continue;
                }
                //rpc调用生成任务
                try{
                    String err = dubboService.autoStartTask("ACC_LOAN",item.get("LOAN_ACCOUNT").toString(), modelName, user.getId(), new Date());
                    if(S.notEmpty(err)){
                        println( "贷款账号%s: 自动生成任务失败, 错误信息: %s", loanAccount, err);
                        continue;
                    }
                }
                catch (Exception e){
                    println( "贷款账号%s: 自动生成任务失败, 错误信息: %s", loanAccount, "RPC调用失败");
                    continue;
                }
                println( "贷款账号%s: 自动生成任务成功", loanAccount, loanAccount);
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

    /**
     * get all configs
     * @return
     */
    private Map<String,String> getConfigs(){
        Map<String,String> ret = C.newMap();
        List<Map> list = (sqlManager.select("system.selectConfigs", Map.class, C.newMap()));
        for (Map<String, Object> map : list) {
            if(S.isBlank(String.valueOf(map.get("VAR_NAME")))){
                continue;
            }
            ret.put(String.valueOf(map.get("VAR_NAME")).toUpperCase(), String.valueOf(map.get("VAR_VALUE")));
        }
        return ret;
    }

    /**
     * get config value by single key
     * @param key
     * @return
     */
    private String getConfig(final String key){
        List<Map> list = sqlManager.select("system.selectConfigByKey", Map.class, C.newMap("varName",key));
        if(C.empty(list)){
            return "";
        }
        return String.valueOf(list.get(0).getOrDefault("VAR_VALUE",""));

    }

    /**
     * get all message templates
     * @return
     */
    private Map<String, MessageTemplate> getTemplates(){
        return sqlManager.select("system.selectTemplates", MessageTemplate.class, C.newMap())
                .stream()
                .collect(toMap(MessageTemplate::getName,t->t));
    }

    /**
     * get a message template
     * @param name
     * @return
     */
    private java.util.Optional<MessageTemplate> getTemplate(final String name){
        return java.util.Optional.ofNullable(sqlManager.query(MessageTemplate.class).andEq("name", name).single());
    }


    /**
     * send system notice to user
     *
     * @param type
     * @param uid
     * @param content
     * @param bindData
     */
    private void writeNotice(final SystemNotice.Type type, final long uid, final String content, Map bindData){
        if($.isNull(bindData)){
            bindData = new HashMap();
        }
        SystemNotice notice = new SystemNotice();
        notice.setAddTime(new Date());
        notice.setUserId(uid);
        notice.setType(type);
        notice.setState(SystemNotice.State.UNREAD);
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

    /**
     * write showrt message log
     * @param phone
     * @param msg
     */
    private void writeShortMessageLog(final String phone, final String msg){
        ShortMessageLog shortMessageLog = new ShortMessageLog();
        shortMessageLog.setPhone(phone);
        shortMessageLog.setMessage(msg);
        shortMessageLog.setAddTime(new Date());
        int row = sqlManager.insert(shortMessageLog);
        if(0 == row){
            throw new RuntimeException("写入短信日志错误, 开始回滚数据");
        }
    }
}
