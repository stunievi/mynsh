package com.beeasy.hzback.modules.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.jms.JMSException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@RequestMapping("/api/instructionChannel/sendMsg")
public class InstructionChannelMQ {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private SQLManager sqlManager;

//    @Autowired
//    private QCCLog qCCLog;

    @RequestMapping(method = RequestMethod.GET)
    public void sendMsg(String msg) throws JMSException {

        ActiveMQQueue mqQueue = new ActiveMQQueue("qcc-company-infos-request");

        JSONObject result = new JSONObject();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);

        Object a = sqlManager.select("qcc.对公客户", JSONObject.class, result);
        String str = a.toString();
        result.put("OrderId", "fzsys_"+dateTime);

        JSONArray json = (JSONArray) JSONArray.parse(str);
        JSONObject user;
        JSONArray jsonArray = new JSONArray();
        for (Object obj : json) {
            JSONObject jo = (JSONObject)obj;
            String cusName= jo.getString("cusName");

            user = new JSONObject();
            user.put("Sign", "00");
            user.put("Content", cusName);

            jsonArray.add(user);
        }

//        user = new JSONObject();
//        user.put("Sign", "00");
//        user.put("Content", "深圳小桔科技有限公司");
//        jsonArray.add(user);
        result.put("OrderData", jsonArray);

//        ActiveMQTextMessage m = new ActiveMQTextMessage();
//        m.setText(result.toString());
        System.out.println("发送内容："+result.toString());
        jmsMessagingTemplate.convertAndSend(mqQueue, result.toString());
        System.out.println("msg发送成功");

    }
}
