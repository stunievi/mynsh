package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.ShortMsg;
import com.beeasy.hzback.entity.SysVar;
import com.jasson.im.api.APIClient;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DB2SqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageSend {

    private static APIClient handler = new APIClient();
    @Autowired
    SQLManager sqlManager;

    /**
     * 根据手机号码发送短信
     */
    public void send(String phone) {
        boolean flag = isMobile(phone);
        if(!flag){
            return;
        }

        ConnectionSource source = ConnectionSourceHelper.getSimple("com.ibm.db2.jcc.DB2Driver", "jdbc:db2://47.94.97.138:50000/test", "", "db2inst1", "db2inst1");
        DB2SqlStyle style = new DB2SqlStyle();
        SQLLoader loader = new ClasspathLoader("/sql");
// 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的
        UnderlinedNameConversion nc = new UnderlinedNameConversion();
// 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
        SQLManager sqlManager = new SQLManager(style, loader, source, nc, new Interceptor[]{new DebugInterceptor()});

        //检查短信接口是否开启
        List<JSONObject> vars = sqlManager.execute(new SQLReady("select count(1) from t_system_variable where var_name = 'msg_api_open' and var_value = '1'"), JSONObject.class);
        if (vars.size() == 0 || vars.get(0).getInteger("1") == 0) {
            System.out.println("短信网关配置没有打开");
            System.exit(-1);
        }

        int connectRe = handler.init("118.2.82.252", "hzrcbJava", "hzrcb@pwd2896880", "javaApiMsg", "mas");
        if (connectRe != APIClient.IMAPI_SUCC) {
            System.out.println("无法连接短信接口");
            System.exit(-1);
        }


        String message = "数据滞后提醒：数据内容滞后当前日期已超过两天！";
        Long srcId = 10086L;
        // 发送
        handler.sendSM(phone, message, srcId);

        ShortMsg entity = new ShortMsg();
        entity.setPhone(phone);
        entity.setAddTime(new Date());
        entity.setMessage(message);
        entity.setState(0);
        sqlManager.insert(entity);

        System.out.println("发送完毕");
    }

    /**
     * 手机号验证
     */
    public static boolean isMobile(String mobile) {
//        String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        String REGEX_MOBILE = "^[1][3,4,5,7,8][0-9]{9}$";
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void checkDate(){
        List<JSONObject> objs = sqlManager.execute(new SQLReady("SELECT src_sys_date FROM RPT_M_RPT_SLS_ACCT FETCH FIRST 1 ROWS ONLY"),JSONObject.class);
        try{
            String sysDate = objs.get(0).getString("srcSysDate");
            LocalDate beginDateTime = LocalDate.parse(sysDate, DateTimeFormatter.BASIC_ISO_DATE);
            LocalDate now = LocalDate.now();

            long betweenDays = beginDateTime.until(now, ChronoUnit.DAYS);
            if(betweenDays>2){
                sendMsg();
            }
        }
        catch (Exception e){
        }
    }

    private void sendMsg(){
        SysVar sysVar = sqlManager.lambdaQuery(SysVar.class)
                .andEq(SysVar::getVarName, "data_early_warning")
                .single();
        if (null != sysVar) {
            String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            String[] list = sysVar.getVarValue().replace(" ", "").split(regEx);
            /*Pattern p= Pattern.compile(regEx);
            Matcher m=p.matcher(sysVar.getVarValue());
            System.out.println(m.find());*/

            System.out.println(S.fmt("准备发送短信, 发送条数 %d", list.length));
            for(String str : list){
                send(str);
            }
        }
    }
}
