package com.beeasy.hzdata.command;

import act.cli.CliContext;
import act.cli.Command;
import act.cli.Optional;
import act.db.beetlsql.BeetlSqlTransactional;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzdata.entity.*;
import com.beeasy.hzdata.utils.Utils;
import com.beeasy.rpc.DubboService;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.osgl.$;
import org.osgl.mvc.annotation.With;
import org.osgl.util.C;
import org.osgl.util.S;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

@With(BeetlSqlTransactional.class)
public class CheckCommand {

    static SimpleDateFormat dayFormater = new SimpleDateFormat("dd");

    @Inject
    SQLManager sqlManager;
    @Inject
    DubboService dubboService;



    @Command(name = "task", help = "-h")
    public String checkRule(
            @Optional(lead = "-n", help = "the number which task you want to check, use comma to split") String num,
            @Optional(lead = "-c", help = "clear trigger logs") String cl,
            @Optional(lead = "-g", help = "generate auto checked task") String g,
            CliContext context
    ) {
        if($.isNotNull(cl)){
            println(context,"正在清除检查日志......");
            sqlManager.executeUpdate("task.deleteTriggerLogs",C.newMap());
        }

        if(S.isNotBlank(num)){
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
                        checkSingle(context,i);
                    }
                }
                else{
                    checkSingle(context, Integer.parseInt(n));
                }
            }
        }
        if($.isNotNull(g)){
            generateAutoTask(context);
        }
        return "";
    }

    public void checkSingle(CliContext context, final int n){
        //反射
        Method method = null;
        try {
            method = getClass().getDeclaredMethod("rule" + n, CliContext.class, int.class);
        } catch (NoSuchMethodException e) {
            //没有就略过
            return;
        }
        method.setAccessible(true);
        beginChcek(context,n);
        try {
            method.invoke(this, context, n);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        endCheck(context,n);
    }


    /***** 检查区域 ****/
    public void rule1(CliContext context, final int n){
        MessageTemplate template = getTemplate(getConfig("MSG_RULE_1_TMPL")).orElse(null);
        if(null == template){
            println(context,"找不到消息模板");
            return;
        }
        List<Map> res = (sqlManager.select("task.selectRule1",Map.class, C.newMap(
                "day", getConfig("MSG_RULE_1"),
                "on", getConfig("MSG_RULE_1_ON")
        )));
        println(context, "找到待处理条数%d", res.size());
        for (Map<String, Object> item : res) {

        }

    }

    public void rule2(){

    }

    public void rule10(CliContext context, final int n){
        List<Map> res = sqlManager.select("task.selectRule10", Map.class, C.newMap(
                "day0", getConfig("MSG_RULE_10_VAL1"),
                "day1", getConfig("MSG_RULE_10_VAL2")
        ));
        println(context, "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的不良资产管理任务 %s 需要有催收或者诉讼的子流程", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule11(CliContext context, final int n){
        List<Map> res = sqlManager.select("task.selectRule11", Map.class, C.newMap(
            "day", getConfig("MSG_RULE_11")
        ));
        println(context, "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 诉前保全即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule12(CliContext context, final int n){
        List<Map> res = sqlManager.select("task.selectRule12", Map.class, C.newMap(
            "day",  getConfig("MSG_RULE_12")
        ));
        println(context, "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 开庭公告即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule13(CliContext context, final int n){
        List<Map> res = sqlManager.select("task.selectRule13", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_13")
        ));
        println(context, "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 判决生效即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule14(CliContext context, final int n){
        List<Map> res = sqlManager.select("task.selectRule14", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_14")
        ));
        println(context, "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("你的任务 %s 评估报告有效期即将到期", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule15(CliContext context, final int n){
        List<Map> res = sqlManager.select("task.selectRule15", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_15")
        ));
        println(context, "找到待处理条数%d", res.size());
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }
            writeNotice(SystemNotice.Type.WORKFLOW, uid, String.format("请尽快办理你的任务 %s", item.get("TITLE")),C.newMap("taskId", item.get("ID")));
            writeLog(n, String.valueOf(item.get("ID")),null);
        }
    }

    public void rule16(CliContext context, final int n){
        List<Map> res = sqlManager.select("task.selectRule16", Map.class, C.newMap(
                "day",  getConfig("MSG_RULE_16")
        ));
        println(context, "找到待处理条数%d", res.size());
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
    public void generateAutoTask(final CliContext context){
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
            println(context, "");
            println(context, "***************");
            println(context, "开始检查规则%s", id);
            String BIZ_TYPE_SUB = configs.get(String.format("ACC_%s_BIZ_TYPE", id));
            String LOAN_AMOUNT_MAX = (configs.get(String.format("ACC_%s_LOAN_AMOUNT_MAX", id)));
            String LOAN_AMOUNT_MIN = (configs.get(String.format("ACC_%s_LOAN_AMOUNT_MIN", id)));
            String LOAN_CHECK = (configs.get(String.format("ACC_%s_LOAN_CHECK", id)));
            String EXPECT_DAY = (configs.get(String.format("ACC_%s_EXPECT_DAY", id)));
            if(S.isBlank(BIZ_TYPE_SUB) || S.isBlank(LOAN_AMOUNT_MAX) || S.isBlank(LOAN_CHECK) || S.isBlank(EXPECT_DAY) || S.isBlank(LOAN_AMOUNT_MIN)){
                println(context, "配置错误");
                continue;
            }
            List<Map> res = sqlManager.select("task.selectGentask",Map.class, C.newMap(
                "v0",BIZ_TYPE_SUB,
                    "v1",LOAN_AMOUNT_MIN,
                    "v2", LOAN_AMOUNT_MAX,
                    "v3", LOAN_CHECK,
                    "v4", EXPECT_DAY
            ));
            println(context, "找到待处理条数%d", res.size());
            for (Map item : res) {
                //确定任务模型名
                Map<String,String> map = sqlManager.selectSingle("task.selectModelName",C.newMap(), Map.class);
                String loanAccount = (String) item.get("LOAN_ACCOUNT");
                if(C.empty(map)){
                    println(context, "贷款账号%s: 找不到任务对应的模型", loanAccount);
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
                    println(context, "贷款账号%s: 找不到对应的任务还行人", loanAccount);
                    continue;
                }
                //rpc调用生成任务
                try{
                    String err = dubboService.autoStartTask("ACC_LOAN",item.get("LOAN_ACCOUNT").toString(), modelName, user.id, new Date());
                    if(S.notEmpty(err)){
                        println(context, "贷款账号%s: 自动生成任务失败, 错误信息: %s", loanAccount, err);
                        continue;
                    }
                }
                catch (Exception e){
                    println(context, "贷款账号%s: 自动生成任务失败, 错误信息: %s", loanAccount, "RPC调用失败");
                    continue;
                }
                println(context, "贷款账号%s: 自动生成任务成功", loanAccount, loanAccount);
            }
        }
    }
    /***** 自动生成任务end   *****/


    private void beginChcek(final CliContext context, final int num){
        context.println("");
        context.println("***********************");
        context.println("开始检查规则%d",num);
        context.flush();
    }

    private void endCheck(final CliContext context, final int num){
        context.println("规则%d结束检查", num);
        context.println("***********************");
        context.println("");
        context.flush();
    }

    private void println(final CliContext context, final String template, Object ...args){
        context.println(template,args);
        context.flush();
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
                .collect(toMap(t->t.name,t->t));
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
        notice.addTime = new Date();
        notice.userId = uid;
        notice.type = type;
        notice.state = SystemNotice.State.UNREAD;
        notice.content = content;
        notice.bindData = JSON.toJSONString(bindData);
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
        log.ruleName = "rule" + n;
        log.triggerTime = date;
        log.uuid = uuid;
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
        shortMessageLog.phone = phone;
        shortMessageLog.message = msg;
        shortMessageLog.addTime = new Date();
        int row = sqlManager.insert(shortMessageLog);
        if(0 == row){
            throw new RuntimeException("写入短信日志错误, 开始回滚数据");
        }
    }

}
