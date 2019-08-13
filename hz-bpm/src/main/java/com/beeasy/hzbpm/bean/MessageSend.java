package com.beeasy.hzbpm.bean;

import com.alibaba.fastjson.JSONObject;
import org.beetl.sql.core.SQLReady;

import java.util.List;
import java.util.regex.Pattern;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;

public class MessageSend {

    private static APIClient handler = new APIClient();

    /**
     * 根据手机号码发送短信
     */
    public static void send(String phone, String message) {
        boolean flag = isMobile(phone);
        if(!flag){
            return;
        }

//        ConnectionSource source = ConnectionSourceHelper.getSimple("com.ibm.db2.jcc.DB2Driver", "jdbc:db2://47.94.97.138:50000/test", "", "db2inst1", "db2inst1");
//        DB2SqlStyle style = new DB2SqlStyle();
//        SQLLoader loader = new ClasspathLoader("/sql");
//// 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的
//        UnderlinedNameConversion nc = new UnderlinedNameConversion();
//// 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
//        SQLManager sqlManager = new SQLManager(style, loader, source, nc, new Interceptor[]{new DebugInterceptor()});

        //检查短信接口是否开启
        List<JSONObject> vars = sqlManager.execute(new SQLReady("select count(1) from t_system_variable where var_name = 'msg_api_open' and var_value = '1'"), JSONObject.class);
        if (vars.size() == 0 || vars.get(0).getInteger("1") == 0) {
            System.out.println("短信网关配置没有打开");
            return;
        }

        int connectRe = handler.init("118.2.82.252", "hzrcbJava", "hzrcb@pwd2896880", "javaApiMsg", "mas");
        if (connectRe != APIClient.IMAPI_SUCC) {
            System.out.println("无法连接短信接口");
            return;
        }

        Long srcId = 10086L;
        // 发送
        handler.sendSM(phone, message, srcId);

//        ShortMsg entity = new ShortMsg();
//        entity.setPhone(phone);
//        entity.setAddTime(new Date());
//        entity.setMessage(message);
//        entity.setState(0);
//        sqlManager.insert(entity);

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

}
