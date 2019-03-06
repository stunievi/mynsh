import com.alibaba.fastjson.JSONObject;
import com.jasson.im.api.APIClient;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DB2SqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.osgl.util.S;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageHandler {

    private static APIClient handler = new APIClient();

    public static void main(String[] args) {
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

        //查出所有要发短信的
        List<JSONObject> msgs = sqlManager.execute(new SQLReady("select * from t_short_message_log where state = -10086"), JSONObject.class);

        int connectRe = handler.init("118.2.82.252", "hzrcbJava", "hzrcb@pwd2896880", "javaApiMsg", "mas");
        if (connectRe != APIClient.IMAPI_SUCC) {
            System.out.println("无法连接短信接口");
            System.exit(-1);
        }

        System.out.println(S.fmt("准备发送短信, 发送条数 %d", msgs.size()));

        Long srcId = 10086L;
        for (JSONObject msg : msgs) {
            handler.sendSM(msg.getString("phone"), msg.getString("message"), srcId);
        }

        sqlManager.executeUpdate(new SQLReady("update t_short_message_log set state = 0"));

        System.out.println("发送完毕");
    }
}
