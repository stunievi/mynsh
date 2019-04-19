package com.beeasy.hzqcc.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.Result;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/qcc/auto")
public class UpdateQccDataController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping(value ="/updateData", method = RequestMethod.GET)
    public Result sendMsg(
            @RequestParam("fullName") String fullName,
            @RequestParam("sign") String sign
    ){
        ActiveMQTopic mqTopic = new ActiveMQTopic("my_msg");
        // 按格式封装指令格式放入MQ
        JSONObject result = new JSONObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        result.put("OrderId", "fzsys_"+dateTime);
        JSONObject user = new JSONObject();
        user.put("Sign", sign);
        user.put("Content", fullName);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(user);
        result.put("OrderData", jsonArray);

        System.out.println("发送内容："+result.toString());
        jmsMessagingTemplate.convertAndSend(mqTopic, result.toString());
        return Result.ok("指令已发送！");
    }

}
