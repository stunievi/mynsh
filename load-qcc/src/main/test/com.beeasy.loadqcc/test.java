package com.beeasy.loadqcc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class test {

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    @Test
    public void getQccData(){
        ActiveMQQueue mqQueue = new ActiveMQQueue("qcc-company-infos-request");
        // 按格式封装指令格式放入MQ
        JSONObject result = new JSONObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        result.put("OrderId", "fzsys_" + dateTime);
        result.put("uid", 1);
        JSONObject user = new JSONObject();
        user.put("Sign", "97");
        user.put("Content1", "陈秀女");
        user.put("Content2", "澳达树熊涂料（惠州）有限公司");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(user);
        result.put("OrderData", jsonArray);
        System.out.println("发送内容：" + result.toString());
        jmsMessagingTemplate.convertAndSend(mqQueue, result.toString());

    }

}
