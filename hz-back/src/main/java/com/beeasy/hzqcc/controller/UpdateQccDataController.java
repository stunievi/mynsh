package com.beeasy.hzqcc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.QCCLog;
import com.beeasy.hzback.entity.SysNotice;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.osgl.util.IO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.*;

@Transactional
@RestController
@RequestMapping("/api/qcc/auto")
public class UpdateQccDataController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private SQLManager sqlManager;
    @Autowired
    NoticeService2 noticeService2;

    private static final Map<String, String> data = new HashMap<>();
    static {
        data.put("ChattelMortgage_GetChattelMortgage","动产抵押信息");
        data.put("CIAEmployeeV4_GetStockRelationInfo","企业人员董监高信息");
        data.put("CourtAnnoV4_GetCourtNoticeInfo","开庭公告详情");
        data.put("CourtAnnoV4_SearchCourtNotice","开庭公告列表");
        data.put("CourtNoticeV4_SearchCourtAnnouncement","法院公告列表");
        data.put("CourtNoticeV4_SearchCourtAnnouncementDetail","法院公告详情");
        data.put("CourtV4_SearchShiXin","失信信息");
        data.put("CourtV4_SearchZhiXing","被执行信息");
        data.put("ECICompanyMap_GetStockAnalysisData","企业股权穿透十层接口查询");
        data.put("ECIException_GetOpException","企业经营异常");
        data.put("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap","企业图谱");
        data.put("ECIRelationV4_GetCompanyEquityShareMap","股权结构图");
        data.put("ECIRelationV4_SearchTreeRelationMap","投资图谱");
        data.put("ECIV4_GetDetailsByName","企业关键字精确获取详细信息(Master)");
        data.put("ECIV4_SearchFresh","新增公司列表");
        data.put("EnvPunishment_GetEnvPunishmentDetails","环保处罚详情");
        data.put("EnvPunishment_GetEnvPunishmentList","环保处罚列表");
        data.put("History_GetHistoryShiXin","历史失信查询");
        data.put("History_GetHistorytAdminLicens","历史行政许可");
        data.put("History_GetHistorytAdminPenalty","历史行政处罚");
        data.put("History_GetHistorytCourtNotice","历史法院公告");
        data.put("History_GetHistorytEci","历史工商信息");
        data.put("History_GetHistorytInvestment","历史对外投资");
        data.put("History_GetHistorytJudgement","历史裁判文书");
        data.put("History_GetHistorytMPledge","历史动产抵押");
        data.put("History_GetHistorytPledge","历史股权出质");
        data.put("History_GetHistorytSessionNotice","历史开庭公告");
        data.put("History_GetHistorytShareHolder","历史股东");
        data.put("History_GetHistoryZhiXing","历史被执行");
        data.put("HoldingCompany_GetHoldingCompany","控股公司信息");
        data.put("JudgeDocV4_GetJudgementDetail","裁判文书详情");
        data.put("JudgeDocV4_SearchJudgmentDoc","裁判文书列表");
        data.put("JudicialAssistance_GetJudicialAssistance","司法协助信息");
        data.put("JudicialSale_GetJudicialSaleDetail","司法拍卖详情");
        data.put("JudicialSale_GetJudicialSaleList","司法拍卖列表");
        data.put("LandMortgage_GetLandMortgageDetails","土地抵押详情");
        data.put("LandMortgage_GetLandMortgageList","土地抵押列表");

        data.put("ECIInvestmentThrough_GetInfo", "企业对外投资穿透");
        data.put("ECIInvestment_GetInvestmentList","企业对外投资列表");
    }

    @RequestMapping(value = "/updateData", method = RequestMethod.GET)
    public Result sendMsg(
            @RequestParam("fullName") String fullName,
            @RequestParam("sign") String sign
    ) {
        List<JSONObject> ruleList = sqlManager.select("user.查询总行企查查风险角色", JSONObject.class, C.newMap());
        List<Long> uidList = new ArrayList<>();
        for(JSONObject list : ruleList){
            uidList.add(list.getLong("uid"));
        }
        boolean flag = uidList.contains(AuthFilter.getUid());
        if(!flag){
            return Result.error("您没有该功能权限！");
        }
        ActiveMQQueue mqQueue = new ActiveMQQueue("qcc-company-infos-request");
        // 按格式封装指令格式放入MQ
        JSONObject result = new JSONObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        result.put("OrderId", "fzsys_" + dateTime);

        String uid = String.valueOf(AuthFilter.getUid());
        result.put("uid", uid);
        JSONObject user = new JSONObject();
        user.put("Sign", sign);
        user.put("Content", fullName);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(user);
        result.put("OrderData", jsonArray);

        System.out.println("发送内容：" + result.toString());
        jmsMessagingTemplate.convertAndSend(mqQueue, result.toString());
        String signName = getSignName(sign);
        saveQccLog("手动更新：" + fullName + "--" + signName, "01", "fzsys_" + dateTime, "", uid);

        return Result.ok("指令已发送！");
    }

    private void saveQccLog(String instructions, String type, String orderId, String dataId, String uid) {

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
        if (o instanceof TextMessage) {
            try {
//                JSON.parseObject()
            } catch (Exception e) {

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

            String uid = (String) jsStr.get("uid");
            String orderId = (String) jsStr.get("OrderId");

            switch (progress) {
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

            while (iterator.hasNext()) {
                String key = iterator.next();
                if ("uid".equals(key) || "OrderId".equals(key)) {
                    continue;
                }
                //获得key值对应的value
                JSONArray ja1 = jsStr.getJSONArray(key);
                if (ja1.size() <= 0 || null == ja1) {
                    continue;
                }
                Object ob = ja1.get(0);
                JSONObject jObject = (JSONObject) ob;
                String cusName = jObject.getString("Content");
                String sign = getSignName(jObject.getString("Sign"));
                String content = "";

                if (ja1.size() > 1) {
                    content = deconstructResponse(finished, progressText, content, cusName, "", "等公司", jo);
                } else if (ja1.size() == 1) {
                    content = deconstructResponse(finished, progressText, content, cusName, "--" + sign, "", jo);
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

        } else if (o instanceof BlobMessage) {
            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
            System.out.println(file);
        }
    }

    @JmsListener(destination = "qcc-company-infos-response", containerFactory = "jmsListenerContainerTopic")
    public void infoResponse(Object o) throws JMSException, IOException {
        if (o instanceof TextMessage) {

            System.out.println(((TextMessage) o).getText());

            JSONObject jo = JSON.parseObject(((TextMessage) o).getText());
            String finished = jo.getString("finished");
            String progress = jo.getString("progress");
            String requestId = jo.getString("requestId");

            String sourceRequest = jo.getString("sourceRequest");

            JSONObject jsStr = JSONObject.parseObject(sourceRequest);

            String uid = (String) jsStr.get("uid");
            String orderId = (String) jsStr.get("OrderId");

            Iterator<String> iterator = jsStr.keySet().iterator();
            //----
            /*JSONArray ja2 = jsStr.getJSONArray("OrderData");
            JSONObject jObject2 = (JSONObject) ja2.get(0);
            String sign2 = getSignName(jObject2.getString("Sign"));
            System.out.println(jObject2.getString("Content"));*/
            //---
            while (iterator.hasNext()) {
                String key = iterator.next();
                if ("uid".equals(key) || "OrderId".equals(key)) {
                    continue;
                }
                //获得key值对应的value
                JSONArray ja1 = jsStr.getJSONArray(key);
                if (ja1.size() <= 0 || null == ja1) {
                    continue;
                }

                Object ob = ja1.get(0);
                JSONObject jObject = (JSONObject) ob;
                String cusName = jObject.getString("Content");
                String sign = getSignName(jObject.getString("Sign"));

                String content = "";

                if (ja1.size() > 1) {
                    content = companyInfosResponse(finished, progress, content, cusName, "", "等公司", jo);
                } else if (ja1.size() == 1) {
                    content = companyInfosResponse(finished, progress, content, cusName, "--" + sign, "", jo);
                }
                saveQccLog(content, "03", orderId, requestId, uid);
//                    for (Object obj : ja1) {
//                        JSONObject jo1 = (JSONObject) obj;
//                        String cusName = jo1.getString("Content");
//                        String sign = getSignName(jo1.getString("Sign"));
//                        saveQccLog("获取数据全部成功："+ cusName + "--" + sign, "03", orderId, requestId, uid);
//                    }
            }

            JSONObject countObject = jo.getJSONObject("callApiCount");
            if(null != countObject && countObject.size() >0){
                Iterator<String> it = countObject.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    int value = countObject.getInteger(key);

                    sqlManager.update("qcc.保存企查查接口调用次数",C.newMap("ID",U.getSnowflakeIDWorker().nextId(),"ADD_TIME",new Date(),"IF_NAME_EN",key,"IF_NAME_CH",data.get(key),"COUNT",value,"ORDER_ID",orderId,"DATA_ID",requestId));
                    /*QccCount entity = new QccCount();
                    entity.setId(U.getSnowflakeIDWorker().nextId());
                    entity.setCount(value);
                    entity.setAddTime(new Date());
                    entity.setIfNameCH(data.get(key));
                    entity.setIfNameEN(key);
                    entity.setOrderID(orderId);
                    entity.setDataID(requestId);
                    sqlManager.insert(entity);*/
                }
            }

        } else if (o instanceof BlobMessage) {
            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
            System.out.println(file);
        }
    }

    private String getSignName(String signCode) {
        String signName = "";
        switch (signCode) {
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
    private void sendToAdminMsg(String errText, String cusName, String sign) {
//        User user = sqlManager.lambdaQuery(User.class).andEq(User::getUsername,"admin").single();
        String renderStr = "企查查" + errText + "失败！" + cusName + sign;
        List<SysNotice> notices = new ArrayList<>();
        notices.add(
                noticeService2.makeNotice(SysNotice.Type.SYSTEM, 1, renderStr, null)
        );
        sqlManager.insertBatch(SysNotice.class, notices);
    }

    private String deconstructResponse(int finished, String progressText, String content, String cusName, String sign, String te, JSONObject jo) {
        if (0 == finished || 1 == finished) { // 成功
            content = progressText + "成功：" + cusName + sign + te;
        }
        /*else if (1 == finished) {   // 部分失败
            sendToAdminMsg("数据解构部分", cusName, sign);
            String errorMessage = jo.getString("errorMessage");
            if (null != errorMessage) {
                content = "<span style='color:red'>解构部分失败</span>：" + errorMessage + cusName + sign + te;
            } else {
                content = "<span style='color:red'>解构部分失败</span>：" + cusName + sign + te;
            }

        }*/
        else if (-1 == finished) {   // 全部失败
            sendToAdminMsg("数据解构", cusName, sign);
            String errorMessage = jo.getString("errorMessage");
            if (null != errorMessage) {
                content = "<span style='color:red'>解构失败</span>：" + errorMessage + cusName + sign + te;
            } else {
                content = "<span style='color:red'>解构失败</span>：" + cusName + sign + te;
            }
        }
        return content;
    }

    private String companyInfosResponse(String finished, String progress, String content, String cusName, String sign, String te, JSONObject jo) {
        if ("success".equals(finished) && "3".equals(progress)) {
            content = "成功：" + cusName + sign + te;
        } else if("warning".equals(finished) && "1".equals(progress)){
            // todo 部分成功需要处理发送具有管理员角色人员
            String qcjiekou = "";
            String ycjiekou = "";
            content = "部分成功" + cusName + sign + te;
            JSONObject jsStr = jo.getJSONObject("errorApi");
            Iterator<String> iterator = jsStr.keySet().iterator();
            List<SysNotice> notices = new ArrayList<>();
            while (iterator.hasNext()){
                String key = iterator.next();
                JSONObject ja1 = jsStr.getJSONObject(key);
                if (ja1.size() <= 0 || null == ja1) {
                    continue;
                }
                Iterator<String> iterator2 = ja1.keySet().iterator();
                while (iterator2.hasNext()){
                    String key2 = iterator2.next();
//                    JSONObject ja2 = ja1.getJSONObject(key2);
//                    if (ja2.size() <= 0 || null == ja2) {
//                        continue;
//                    }
                    //112	当前账户已欠费
                    //102	当前KEY已欠费
                    if("102".equals(key2) || "112".equals(key2)){
                        qcjiekou = qcjiekou + data.get(key) + "，";
                    }else{
                        ycjiekou = ycjiekou + data.get(key) + "，";
                    }
                }

            }
            List<JSONObject> userList = sqlManager.select("accloan.查询角色", JSONObject.class,C.newMap("name","系统管理员"));
            if (qcjiekou.length() > 0 && !("").equals(qcjiekou)) {
                String qc = qcjiekou.substring(0,qcjiekou.length()-1);
                if(null != qc && !"".equals(qc)){
                    qcjiekou = qc +"接口已欠费，";
                }
            }
            if (ycjiekou.length() > 0 && !("").equals(ycjiekou)) {
                String yc = ycjiekou.substring(0,ycjiekou.length()-1);
                if(null != yc && !"".equals(yc)){
                    ycjiekou = yc +"接口异常";
                }
            }
            String msg = qcjiekou + ycjiekou;
            if(msg.endsWith("，")){
                msg = msg.substring(0, msg.length()-1);
            }
            for(JSONObject jsonObject:userList){
                Long uid = jsonObject.getLong("UID");
                notices.add(
                    noticeService2.makeNotice(SysNotice.Type.SYSTEM, uid, msg, null)
                );
            }
            sqlManager.insertBatch(SysNotice.class, notices);


        }else if ("failed".equals(finished)) {
            sendToAdminMsg("数据获取", cusName, sign);
            String errorMessage = jo.getString("errorMessage");
            content = "<span style='color:red'>失败</span>：" + errorMessage + cusName + sign + te;
        }
        return content;
    }


    @RequestMapping(value = "/qccCount", method = RequestMethod.GET)
    public Result sendMsg1(
             String beginTime,
             String endTime
    ) {
        List<JSONObject> lists = sqlManager.select("qcc.查询企查查接口调用次数",JSONObject.class, C.newMap("beginTime",beginTime,"endTime",endTime));
        Map<String, String> maps = new HashMap<>();

        for(Map.Entry<String, String> dataMap: data.entrySet()){
            String key = dataMap.getKey();

            if (null == maps.get(key) || ("".equals(maps.get(key)))) {
                maps.put(key, "0");
            }
            for (JSONObject jsonObject : lists){
                String ifNameEn = jsonObject.getString("ifNameEn");
                if(ifNameEn.equals(key)){
                    String num = jsonObject.getString("number");
                    maps.put(key, num);
                }
            }
        }

        return Result.ok(maps);
    }

}
