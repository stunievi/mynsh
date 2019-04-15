package com.beeasy.loadqcc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.entity.LoadQccDataExtParm;
import com.beeasy.loadqcc.service.GetOriginQccService;
import com.beeasy.loadqcc.service.MongoService;
import com.beeasy.mscommon.Result;
import com.mongodb.client.model.Filters;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.bson.Document;
import org.osgl.util.IO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/qcc/loadData")
public class QccDataController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    @Qualifier(value = "qcc1")
    MongoService mongoService;
    @Autowired
    GetOriginQccService getOriginQccService;

    private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

    private void sendQccZipToMQ(
            LoadQccDataExtParm extParam
    ){
        File __file = extParam.getQccFileDataPath();
        File __zip = extParam.getQccZipDataPath();
        try(
                FileInputStream fis = new FileInputStream(__file);
                FileOutputStream fos = new FileOutputStream(__zip);
                ZipOutputStream zip = new ZipOutputStream(fos);
        ) {
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(__file.length());
            zip.putNextEntry(entry);
            byte[] bs = new byte[1024];
            int len = -1;
            while((len = fis.read(bs)) > 0){
                zip.write(bs, 0, len);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        // 将压缩文件放入mq,供解构服务调用
        ActiveMQTopic mqTopic = new ActiveMQTopic("my_msg");
        jmsMessagingTemplate.convertAndSend(mqTopic, __zip);
    }

    // 从企查查拉取所有数据
    @GetMapping(value = "/getAllQccData")
    Result ECI_GetDetailsByName(
           @RequestParam("keyWord") String keyWord
    ){
        LoadQccDataExtParm extParam = LoadQccDataExtParm.automatic(UUID.randomUUID().toString());
        getOriginQccService.loadAllData(keyWord, extParam);
        sendQccZipToMQ(extParam);
        return Result.ok(keyWord);
    }

    @RequestMapping("/sendMsg")
    public void sendMqQccData(String msg) throws JMSException {
//        ActiveMQTopic mqTopic = new ActiveMQTopic("my_msg");
////        ActiveMQMessage msgs = new ActiveMQMessage();
//        ActiveMQTextMessage m = new ActiveMQTextMessage();
//        m.setText("fffffffff");
//        jmsMessagingTemplate.convertAndSend(mqTopic, m);
//        System.out.println("msg发送成功");
//        jmsMessagingTemplate.convertAndSend(mqTopic, new File("D:\\logs\\2019-03-20.txt"));
    }

    @JmsListener(destination = "my_msg", containerFactory = "jmsListenerContainerTopic")
    public void test(Object o) throws JMSException, IOException {
        if(o instanceof TextMessage){
            try{
                JSONObject dataObj = JSON.parseObject(((TextMessage) o).getText());
                LoadQccDataExtParm extParam = LoadQccDataExtParm.automatic(dataObj.getString("OrderId"));
                autoReqData(dataObj, extParam);
            }
            catch (Exception e){

            }
            System.out.println(((TextMessage) o).getText());
        } else if(o instanceof BlobMessage){
            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
            System.out.println(file);
        }
    }

    private Result autoReqData(
            JSONObject params,
            LoadQccDataExtParm extParam
    ){
        List dataList = params.getJSONArray("OrderData"); // 数据列表
        for(int i = 0 ; i < dataList.size() ; i++) {
            JSONObject data = (JSONObject) dataList.get(i);
            String companyName = data.getString("Content"); // 公司名
            String command = data.getString("Sign"); // 指令
            if(null == command || command.isEmpty()){
                return Result.error("指令为空");
            }
            if(null == companyName|| companyName.isEmpty()){
                return Result.error("公司名为空");
            }
            Document tmep_company_ret = null;
            tmep_company_ret =  mongoService.getCollection("ECIV4_GetDetailsByName").find(Filters.eq("QueryParam.keyword", companyName)).first();
            // 公司公司信息
            JSONObject companyInfo;
            if(null == tmep_company_ret){
                // 从企查查获取工商信息
                companyInfo = getOriginQccService.ECI_GetDetailsByName(companyName, extParam);
            }else{
                companyInfo = (JSONObject) JSON.toJSON(tmep_company_ret);
                companyInfo = companyInfo.getJSONObject("Result");
            }
            if(null == companyInfo || companyInfo.isEmpty()){
                return Result.error("公司工商信息获取失败！");
            }
            String keyNo = companyInfo.getString("KeyNo");
            // 更新所有
            if(command.contains("00")){
                getOriginQccService.loadAllData(companyName, extParam);
            }else{
                if(command.contains("01")){
                    // 更新基本信息
                    getOriginQccService.loadDataBlock1(companyName, extParam);
                }
                if(command.contains("02")){
                    // 法律诉讼
                    getOriginQccService.loadDataBlock2(companyName, extParam);
                }
                if(command.contains("03")){
                    // 关联族谱
                    getOriginQccService.loadDataBlock3(companyName, keyNo, extParam);
                }
                if(command.contains("04")){
                    // 历史信息
                    getOriginQccService.loadDataBlock4(companyName, extParam);
                }
                if(command.contains("05")){
                    // 经营风险
                    getOriginQccService.loadDataBlock5(companyName, keyNo, extParam);
                }
            }
        }
        sendQccZipToMQ(extParam);

        return Result.ok("更新完毕");
    }

    // 自动触发数据块更新
    @RequestMapping(value = "/loadDataBlock", method = RequestMethod.POST)
    Result loadDataBlock(
            @RequestBody() JSONObject params
    ){
        LoadQccDataExtParm extParam = LoadQccDataExtParm.manul(UUID.randomUUID().toString());
        return autoReqData(params, extParam);
    }

    // 手动触发数据块更新 - 基本信息
    @GetMapping(value = "/loadDataBlock1")
    Result loadDataBlock1(
            @RequestParam("keyWord") String keyWord
    ){
        try {
            LoadQccDataExtParm extParam = LoadQccDataExtParm.manul(UUID.randomUUID().toString());
            getOriginQccService.loadDataBlock1(keyWord, extParam);
            sendQccZipToMQ(extParam);
            return Result.ok("loadDataBlock1");
        }catch (Exception e){
            return Result.error("【loadDataBlock1】：更新失败！");
        }
    }
    // 手动触发数据块更新 - 法律诉讼
    @GetMapping(value = "/loadDataBlock2")
    Result loadDataBlock2(
            @RequestParam("keyWord") String keyWord
    ){
        try {
            LoadQccDataExtParm extParam = LoadQccDataExtParm.manul(UUID.randomUUID().toString());
            getOriginQccService.loadDataBlock2(keyWord, extParam);
            sendQccZipToMQ(extParam);
            return Result.ok("loadDataBlock2");
        }catch (Exception e){
            return Result.error("【loadDataBlock2】：更新失败！");
        }
    }
    // 手动触发数据块更新 - 关联族谱
    @GetMapping(value = "/loadDataBlock3")
    Result loadDataBlock3(
            @RequestParam("keyWord") String keyWord,
            @RequestParam("keyNo") String keyNo
    ){
        try {
            LoadQccDataExtParm extParam = LoadQccDataExtParm.manul(UUID.randomUUID().toString());
            getOriginQccService.loadDataBlock3(keyWord, keyNo, extParam);
            sendQccZipToMQ(extParam);
            return Result.ok("loadDataBlock3");
        }catch (Exception e){
            return Result.error("【loadDataBlock3】：更新失败！");
        }

    }
    // 手动触发数据块更新 - 历史信息
    @GetMapping(value = "/loadDataBlock4")
    Result loadDataBlock4(
            @RequestParam("keyWord") String keyWord
    ){
        try {
            LoadQccDataExtParm extParam = LoadQccDataExtParm.manul(UUID.randomUUID().toString());
            getOriginQccService.loadDataBlock4(keyWord, extParam);
            sendQccZipToMQ(extParam);
            return Result.ok("loadDataBlock4");
        }catch (Exception e){
            return Result.error("【loadDataBlock4】：更新失败！");
        }

    }
    // 手动触发数据块更新 - 经营风险
    @GetMapping(value = "/loadDataBlock5")
    Result loadDataBlock5(
            @RequestParam("keyWord") String keyWord,
            @RequestParam("keyNo") String keyNo
    ){
        try {
            LoadQccDataExtParm extParam = LoadQccDataExtParm.manul(UUID.randomUUID().toString());
            getOriginQccService.loadDataBlock5(keyWord, keyNo, extParam);
            sendQccZipToMQ(extParam);
            return Result.ok("loadDataBlock5");
        }catch (Exception e){
            return Result.error("【loadDataBlock4】：更新失败！");
        }

    }

    // 手动触发更新数据
//    @RequestMapping(value = "/{$model}/{$action}", method = RequestMethod.GET)
//    Result autoQccData(
//            @PathVariable String $model
//            , @PathVariable String $action
//            , @RequestParam("keyWord") String keyWord
//    ) throws IllegalAccessException, InvocationTargetException {
//        Document comInfo = mongoService.getCollection("ECIV4_GetDetailsByName").find(Filters.eq("QueryParam.keyWord", keyWord)).first();
//        JSONObject loadComInfo = (JSONObject) JSON.toJSON(comInfo);
//        loadComInfo = loadComInfo.getJSONObject("Result");
//        if(null == loadComInfo || loadComInfo.size() <1){
//            loadComInfo = getOriginQccService.ECI_GetDetailsByName(keyWord, "manual");
//            if(null == loadComInfo || loadComInfo.size()<0){
//                return  Result.error("未找到");
//            }
//        }
//        Class clazz = getOriginQccService.getClass();
//
//        String companyName = loadComInfo.getString("Name");
//        if(null == companyName || "".equals(companyName)){
//            return Result.ok("公司名不存在");
//        }
//        String keyNo = loadComInfo.getString("KeyNo");
//        List employees = loadComInfo.getJSONArray("Employees");
//        String methodStr = $model.concat("_").concat($action);
//        try {
//            if(methodStr.equals("CIAEmployeeV4_GetStockRelationInfo")){
//                // 企业人员董监高信息
//                for(short i=0;i<employees.size();i++){
//                    JSONObject item = (JSONObject) employees.get(i);
//                    Method method = clazz.getDeclaredMethod("CIAEmployeeV4_GetStockRelationInfo", String.class, String.class, String.class);
//                    method.invoke(getOriginQccService, companyName, item.getString("Name"), "manual");
//                }
//            }else if(methodStr.length() > 30 && ("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap|ECIRelationV4_SearchTreeRelationMap|ECIRelationV4_GetCompanyEquityShareMap").contains(methodStr)){
//                Method method = clazz.getDeclaredMethod(methodStr, String.class, String.class);
//                method.invoke(getOriginQccService, keyNo, "manual");
//            }else {
//                Method method = clazz.getDeclaredMethod(methodStr, String.class, String.class);
//                method.invoke(getOriginQccService, companyName, "manual");
//            }
//        }catch (NoSuchMethodException e){
//            return Result.error("方法:" + methodStr + "不存在。");
//        }
//        return Result.ok("已完成");
//    }

}
