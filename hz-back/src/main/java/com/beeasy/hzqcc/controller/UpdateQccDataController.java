package com.beeasy.hzqcc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.QCCLog;
import com.beeasy.hzback.entity.SysNotice;
import com.beeasy.hzback.entity.User;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/qcc/auto")
public class UpdateQccDataController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private SQLManager sqlManager;
    @Autowired
    NoticeService2 noticeService2;

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
        String signName = getSignName(sign);
        saveQccLog("手动更新："+fullName + "--" + signName, "01", "fzsys_"+dateTime, "", uid);

        return Result.ok("指令已发送！");
    }

    private void saveQccLog(String instructions, String type, String orderId, String dataId, String uid){

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

    @JmsListener(destination = "qcc-deconstruct-response")
    public void test(Object o) throws JMSException, IOException {
        if(o instanceof TextMessage){
            try{
//                JSON.parseObject()
            }
            catch (Exception e){

            }
            System.out.println(((TextMessage) o).getText());

            JSONObject jo = JSON.parseObject(((TextMessage) o).getText());
            int finished = jo.getInteger("finished");
            String requestId = jo.getString("requestId");

            String progressText = "";

            String progress = jo.getString("progress");

            String sourceRequest = jo.getString("sourceRequest");
            JSONObject jsStr = JSONObject.parseObject(sourceRequest);

            Iterator<String> iterator = jsStr.keySet().iterator();

            String uid = (String)jsStr.get("uid");
            String orderId = (String)jsStr.get("OrderId");

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
                if(ja1.size() <=0 || null == ja1){
                    continue;
                }
                Object ob = ja1.get(0);
                JSONObject jObject = (JSONObject) ob;
                String cusName = jObject.getString("Content");
                String sign = getSignName(jObject.getString("Sign"));
                String content = "";

                if(ja1.size()>1){
                    content = toCon(finished,progressText,content,cusName,sign,"等公司",jo);
                }else if(ja1.size() == 1){
                    content = toCon(finished,progressText,content,cusName,sign,"",jo);
                }
                saveQccLog(content, "02", orderId, requestId, uid);

                /*for (Object obj : ja1) {
                    JSONObject jo1 = (JSONObject) obj;
                    String cusName = jo1.getString("Content");
                    String sign = getSignName(jo1.getString("Sign"));
                    String content = "";

                    if(0 == finished){ // 成功
                        content = progressText +"成功："+ cusName + "--" + sign;
                    }else if( 1 == finished){   // 部分失败
                        sendToAdminMsg("数据解构部分");
                        String errorMessage = jo.getString("errorMessage");
                        if(null != errorMessage){
                            content = "部分失败："+errorMessage;
                        }else{
                            content = "部分失败："+cusName + "--" + sign;
                        }

                    }else if(-1 == finished){   // 全部失败
                        sendToAdminMsg("数据解构全部");
                        String errorMessage = jo.getString("errorMessage");
                        if(null != errorMessage){
                            content = "全部失败："+errorMessage;
                        }else{
                            content = "全部失败："+cusName + "--" + sign;
                        }
                    }
                    saveQccLog(content, "02", orderId, requestId, uid);
                }*/

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
            String progress = jo.getString("progress");
            String requestId = jo.getString("requestId");

            String sourceRequest = jo.getString("sourceRequest");
            JSONObject jsStr = JSONObject.parseObject(sourceRequest);

            String uid = (String)jsStr.get("uid");
            String orderId = (String)jsStr.get("OrderId");

            Iterator<String> iterator = jsStr.keySet().iterator();

            if("success".equals(finished) && "3".equals(progress)){

                while(iterator.hasNext()){
                    String key = iterator.next();
                    if("uid".equals(key) || "OrderId".equals(key)){
                        continue;
                    }
                    //获得key值对应的value
                    JSONArray ja1 = jsStr.getJSONArray(key);
                    if(ja1.size() <=0 || null == ja1){
                        continue;
                    }

                    Object ob = ja1.get(0);
                    JSONObject jObject = (JSONObject) ob;
                    String cusName = jObject.getString("Content");
                    String sign = getSignName(jObject.getString("Sign"));
                    if(ja1.size()>1){
                        saveQccLog("获取数据全部成功："+ cusName + "--" + sign +"等公司", "03", orderId, requestId, uid);
                    }else if(ja1.size() == 1){
                        saveQccLog("获取数据全部成功："+ cusName + "--" + sign, "03", orderId, requestId, uid);
                    }
//                    for (Object obj : ja1) {
//                        JSONObject jo1 = (JSONObject) obj;
//                        String cusName = jo1.getString("Content");
//                        String sign = getSignName(jo1.getString("Sign"));
//                        saveQccLog("获取数据全部成功："+ cusName + "--" + sign, "03", orderId, requestId, uid);
//                    }
                }
            }else if("failed".equals(finished)){
                sendToAdminMsg("数据获取全部");
                String errorMessage = jo.getString("errorMessage");
                saveQccLog("获取数据失败："+errorMessage, "03", orderId, requestId, uid);
            }

        } else if(o instanceof BlobMessage){
            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
            System.out.println(file);
        }
    }

    private String getSignName(String signCode){
        String signName = "";
        switch (signCode){
            case "00":
                signName = "所有";
                break;
            case "01":
                signName = "基本信息";
                break;
            case "02":
                signName = "法律诉讼";
                break;
            case "03":
                signName = "经营风险";
                break;
            case "04":
                signName = "企业族谱";
                break;
            case "05":
                signName = "历史信息";
                break;
            case "01,02,03,04,05":
                signName = "所有";
                break;
        }
        return signName;
    }

    /**
     * 失败后给管理员发送消息
     */
    private void sendToAdminMsg(String errText){
//        User user = sqlManager.lambdaQuery(User.class).andEq(User::getUsername,"admin").single();
        String renderStr = "企查查日志"+errText+"失败！";
        List<SysNotice> notices = new ArrayList<>();
        notices.add(
                noticeService2.makeNotice(SysNotice.Type.SYSTEM, 1, renderStr, null)
        );
        sqlManager.insertBatch(SysNotice.class, notices);
    }

    private String toCon(int finished, String progressText, String content, String cusName, String sign, String te, JSONObject jo){
        if(0 == finished){ // 成功
            content = progressText +"成功："+ cusName + "--" + sign + te;
        }else if( 1 == finished){   // 部分失败
            sendToAdminMsg("数据解构部分");
            String errorMessage = jo.getString("errorMessage");
            if(null != errorMessage){
                content = "部分失败："+errorMessage;
            }else{
                content = "部分失败："+cusName + "--" + sign + te;
            }

        }else if(-1 == finished){   // 全部失败
            sendToAdminMsg("数据解构全部");
            String errorMessage = jo.getString("errorMessage");
            if(null != errorMessage){
                content = "全部失败："+errorMessage;
            }else{
                content = "全部失败："+cusName + "--" + sign + te;
            }
        }
        return content;
    }

}
