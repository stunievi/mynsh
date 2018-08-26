package com.beeasy.hzdata.command;

import act.cli.CliContext;
import act.cli.Command;
import act.cli.Optional;
import act.db.beetlsql.BeetlSqlTransactional;
import com.beeasy.hzdata.entity.MessageTemplate;
import com.beeasy.hzdata.entity.SystemNotice;
import com.beeasy.hzdata.utils.Utils;
import org.beetl.sql.core.SQLManager;
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
import static java.util.stream.Collectors.*;

@With(BeetlSqlTransactional.class)
public class CheckCommand {

    static SimpleDateFormat dayFormater = new SimpleDateFormat("dd");

    @Inject
    SQLManager sqlManager;

    @Command(name = "task", help = "-h")
    public String checkRule(
            @Optional(lead = "-n", help = "the number which task you want to check, use comma to split") String num,
            CliContext context
    ) {
        if(S.isNotBlank(num)){
            List<Integer> nums = Arrays.stream(num.split(","))
                    .map(Integer::parseInt)
                    .distinct()
                    .collect(toList());
            //这里不列入stream, 因为要受检异常
            for(int n : nums){
                //反射
                Method method = null;
                try {
                    method = getClass().getDeclaredMethod("rule" + n, CliContext.class, int.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                if(null == method){
                    return "找不到这个检查函数";
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
        }
        return "";
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
        writeNotice();
        for (Map item : res) {
            final Long uid = (Long) item.get("DEAL_USER_ID");
            if($.isNull(uid)){
                continue;
            }



            int b = 1;
        }
    }


    /***** 检查end ****/


    private void beginChcek(final CliContext context, final int num){
        context.println("开始检查规则%d",num);
        context.flush();
    }

    private void endCheck(final CliContext context, final int num){
        context.println("规则%d结束检查", num);
        context.flush();
    }

    private void println(final CliContext context, final String template, Object ...args){
        context.println(template,args);
        context.flush();
    }

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

    private String getConfig(final String key){
        List<Map> list = sqlManager.select("system.selectConfigByKey", Map.class, C.newMap("varName",key));
        if(C.empty(list)){
            return "";
        }
        return String.valueOf(list.get(0).getOrDefault("VAR_VALUE",""));

    }

    private Map<String, MessageTemplate> getTemplates(){
        return sqlManager.select("system.selectTemplates", MessageTemplate.class, C.newMap())
                .stream()
                .collect(toMap(t->t.name,t->t));
    }

    private java.util.Optional<MessageTemplate> getTemplate(final String name){
        return java.util.Optional.ofNullable(sqlManager.query(MessageTemplate.class).andEq("name", name).single());
    }

    private void writeNotice(){
        SystemNotice notice = new SystemNotice();
        notice.addTime = new Date();
        notice.userId = 1L;
        notice.type = SystemNotice.Type.SYSTEM;
        notice.state = SystemNotice.State.UNREAD;
        int id = sqlManager.insert(notice);
        int c = 1;
    }

}
