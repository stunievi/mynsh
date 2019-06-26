package com.beeasy.MQSender;

import com.alibaba.fastjson.JSONObject;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;


@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class TimeUtuil {
    @Autowired
    SQLManager sqlManager;

    //3.添加定时任务 每天凌晨一点执行一次
    @Scheduled(cron = "0 0 1 * * ?")
    //或直接指定时间间隔，例如：5秒
//    @Scheduled(fixedRate=5000)
    public void time() {
        List<JSONObject> vars = sqlManager.execute(new SQLReady("select count(1) from t_system_variable where var_name = 'qcc_pq_api_open' and var_value = '1'"), JSONObject.class);
        if (vars.size() == 0 || vars.get(0).getInteger("1") == 0) {
            System.out.println("跑批任务配置没有打开");
            return;
        }
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        final int date = c.get(Calendar.DATE);
        final String monthDate =  c.get(Calendar.MONTH) + 1 + "/" + date;
        switch (date) {
            case 1:
            case 16:
                SendMessage.main("02");
                SendMessage.main("03");
                //调用一次
                break;
        }
        switch (monthDate) {
            case "2/1":
            case "5/1":
            case "8/1":
            case "11/1":
                SendMessage.main("01");
                SendMessage.main("04");
                SendMessage.main("05");
                break;
        }
    }
}
