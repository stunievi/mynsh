package com.beeasy.hzqcc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.QCCLog;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.IO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;

@RestController
@RequestMapping("/api/qcc/auto")
public class UpdateQccDataController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private SQLManager sqlManager;

    @RequestMapping(value ="/updateData", method = RequestMethod.GET)
    public Result sendMsg(
            @RequestParam("fullName") String fullName,
            @RequestParam("sign") String sign
    ){
        ActiveMQQueue mqQueue = new ActiveMQQueue("qcc-company-infos-request");
        // 按格式封装指令格式放入MQ
        JSONObject result = new JSONObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        result.put("OrderId", "fzsys_"+dateTime);

        String uid = String.valueOf(AuthFilter.getUid());
        result.put("uid", uid);
        JSONObject user = new JSONObject();
        user.put("Sign", sign);
        user.put("Content", fullName);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(user);
        result.put("OrderData", jsonArray);

        System.out.println("发送内容："+result.toString());
        jmsMessagingTemplate.convertAndSend(mqQueue, result.toString());
        saveQccLog("手动更新："+fullName, "01", "fzsys", "", uid);

        return Result.ok("指令已发送！");
    }

    private void saveQccLog(String instructions, String type, String orderId, String dataId, String uid){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        if(!"".equals(orderId)){
            orderId = orderId + dateTime;
        }

        QCCLog entity = new QCCLog();
        entity.setId(U.getSnowflakeIDWorker().nextId());
        entity.setAddTime(new Date());
        entity.setContent(instructions);
        entity.setOperator(uid);
        entity.setType(type);
        entity.setOrderId(orderId);
        entity.setDataId(dataId);
        sqlManager.insert(entity);
    }

    @JmsListener(destination = "qcc-deconstruct-response", containerFactory = "jmsListenerContainerTopic")
    public void test(Object o) throws JMSException, IOException {
        if(o instanceof TextMessage){
            try{
//                JSON.parseObject()
            }
            catch (Exception e){

            }
            System.out.println(((TextMessage) o).getText());

            JSONObject jo = JSON.parseObject(((TextMessage) o).getText());
            Boolean finished = jo.getBoolean("finished");
            String uid = "";

            if(finished){
                String progressText = "";
                String cusName = "";
                String progress = jo.getString("progress");
                String requestId = jo.getString("requestId");
                String sourceRequest = jo.getString("sourceRequest");
                JSONObject jsStr = JSONObject.parseObject(sourceRequest);

                Iterator<String> iterator = jsStr.keySet().iterator();

                uid = (String)jsStr.get("uid");

                switch (progress){
                    case "0": //
                        progressText = "保存数据";
                        break;
                    case "1":
                        progressText = "解压";
                        break;
                    case "2":
                        progressText = "分析生成sql";
                        break;
                    case "3":
                        progressText = "sql入库";
                        break;
                }

                while(iterator.hasNext()){
                    String key = iterator.next();
                    if("uid".equals(key) || "OrderId".equals(key)){
                        continue;
                    }
                    //获得key值对应的value
                    JSONArray ja1 = jsStr.getJSONArray(key);
                    for (Object obj : ja1) {
                        JSONObject jo1 = (JSONObject) obj;
                        cusName = jo1.getString("Content");
                        saveQccLog(progressText +"："+ cusName, "02", "", requestId, uid);
                    }

                }
            }else{
                String errorMessage = jo.getString("errorMessage");
                saveQccLog("失败："+errorMessage, "02", "", "", uid);
            }

        } else if(o instanceof BlobMessage){
            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
            System.out.println(file);
        }
    }

    @JmsListener(destination = "qcc-company-infos-response", containerFactory = "jmsListenerContainerTopic")
    public void infoResponse(Object o) throws JMSException, IOException {
        if(o instanceof TextMessage){

            System.out.println(((TextMessage) o).getText());

            JSONObject jo = JSON.parseObject(((TextMessage) o).getText());
            String finished = jo.getString("finished");
            String progressText = "";
            String cusName = "";
            String uid = "";
            String progress = jo.getString("progress");

            if("success".equals(finished) && "3".equals(progress)){
                progressText = "全部成功";

                String sourceRequest = jo.getString("sourceRequest");
                JSONObject jsStr = JSONObject.parseObject(sourceRequest);

                Iterator<String> iterator = jsStr.keySet().iterator();

                uid = (String)jsStr.get("uid");
                while(iterator.hasNext()){
                    String key = iterator.next();
                    if("uid".equals(key) || "OrderId".equals(key)){
                        continue;
                    }
                    //获得key值对应的value
                    JSONArray ja1 = jsStr.getJSONArray(key);
                    for (Object obj : ja1) {
                        JSONObject jo1 = (JSONObject) obj;
                        cusName = jo1.getString("Content");
                        saveQccLog(progressText +"："+ cusName, "02", "", "", uid);
                    }

                }

            }else if("failed".equals(finished)){
                progressText = "失败";
                String errorMessage = jo.getString("errorMessage");
                saveQccLog(progressText +"："+errorMessage, "02", "", "", uid);
            }

        } else if(o instanceof BlobMessage){
            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
            System.out.println(file);
        }
    }

}
