package com.beeasy.loadqcc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.ReqQccParam;
import com.beeasy.loadqcc.config.MQConfig;
import com.beeasy.loadqcc.entity.LoadQccDataExtParm;
import com.beeasy.loadqcc.service.GetOriginQccService;
import com.beeasy.loadqcc.service.MongoService;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgl.util.C;
import org.osgl.util.IO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class QccDataController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    MongoService mongoService2;
    @Autowired
    GetOriginQccService getOriginQccService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // 监听MQ更新名单回执
    @JmsListener(destination = "qcc-company-infos-response", containerFactory = "jmsListenerContainerTopic")
    public void qccCompanyInfosResponse(Object o) throws JMSException, IOException {
        if(o instanceof TextMessage){
            System.out.println(((TextMessage) o).getText());
        } else if(o instanceof BlobMessage){
            // 更新完成后的压缩文件
            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
            System.out.println(file);
        }
    }

    private Runnable getThread(JSONObject companyData, LoadQccDataExtParm extParam) {
        return new Runnable() {
            @Override
            public void run() {
                String companyName = ""; // 公司名
                String userName = ""; // 人员名
                String command = companyData.getString("Sign"); // 指令
                if(null == command || command.isEmpty()){
                    getOriginQccService.saveErrLog("更新企查查数据时，"+companyName+":指令为空");
                    return;
                }
                if(command.contains("07") || command.contains("97")){
                    // 获取董监高信息
                    companyName = companyData.getString("Content2");
                    userName = companyData.getString("Content1");
                    if(null==userName || userName.isEmpty()){
                        return;
                    }
                }else{
                    companyName = Optional.ofNullable(companyData.getString("Content")).orElse(companyData.getString("Content1"));
                }
                if(null == companyName|| companyName.isEmpty()){
                    return;
                }
                extParam.setCompanyName(companyName);

                ReqQccParam reqQccParam = new ReqQccParam();
                reqQccParam.setCompanyName(companyName);
                reqQccParam.setUserName(userName);
                // 公司公司信息
                JSONObject companyInfo = new JSONObject();
                // 更新所有
                if(command.contains("00")){
                    reqQccParam.setSign("00");
                    getOriginQccService.loadAllData(reqQccParam, extParam);
                }else{
                    // 需要keyNo的必须拿到工商信息
                    if(command.contains("03") || command.contains("04")){
                        companyInfo = getOriginQccService.ECI_GetDetailsByName(reqQccParam, extParam);
                        reqQccParam.setKeyNo(companyInfo.getString("KeyNo"));
                    }
                    if(command.contains("01")){
                        // 更新基本信息
                        reqQccParam.setSign("01");
                        getOriginQccService.loadDataBlock1(reqQccParam, extParam);
                    }
                    if(command.contains("02")){
                        // 法律诉讼
                        reqQccParam.setSign("02");
                        getOriginQccService.loadDataBlock2(companyName, extParam);
                    }
                    if(command.contains("03")){
                        // 经营风险
                        reqQccParam.setSign("03");
                        getOriginQccService.loadDataBlock5(companyName, reqQccParam.getKeyNo(), extParam);
                    }
                    if(command.contains("04")){
                        // 关联族谱
                        reqQccParam.setSign("04");
                        getOriginQccService.loadDataBlock3(reqQccParam, extParam);
                    }
                    if(command.contains("05")){
                        // 历史信息
                        reqQccParam.setSign("05");
                        getOriginQccService.loadDataBlock4(companyName, extParam);
                    }
                    if(command.contains("06")){
                        // 工商信息，解构后返回董监高名单！！！
                        reqQccParam.setSign("06");
                        getOriginQccService.ECI_GetDetailsByName(reqQccParam, extParam);
                    }
                    if(command.contains("99")){
                        // 工商信息，通过董监高
                        reqQccParam.setSign("99");
                        getOriginQccService.ECI_GetDetailsByName(reqQccParam, extParam);
                    }
                    if(command.contains("98")){
                        // 工商信息，通过对外投资穿
                        reqQccParam.setSign("98");
                        getOriginQccService.ECI_GetDetailsByName(reqQccParam, extParam);
                    }
                    if(command.contains("07")){
                        // 企业董监高， 解构后返还关联公司名单！！！
                        reqQccParam.setSign("07");
//                        getOriginQccService.ECISeniorPerson_GetList(reqQccParam, extParam);
                        getOriginQccService.CIAEmployeeV4_GetStockRelationInfo(reqQccParam, extParam);
                    }
                    if(command.contains("08")){
                        // 对外投资穿透
                        reqQccParam.setSign("08");
                        getOriginQccService.ECIInvestmentThrough_GetInfo(reqQccParam, extParam);
                    }
                    if(command.contains("97")){
                        getOriginQccService.ECISeniorPerson_GetList(reqQccParam, extParam);
                    }
                }

            }
        };
    }

    // 监听MQ递送的更新名单
    @JmsListener(destination = "qcc-company-infos-request")
    public void qccCompanyInfosRequest(Object o) throws JMSException {

        if(o instanceof TextMessage){
            String dataStr = ((TextMessage) o).getText();
            System.out.println(dataStr);
            JSONObject dataObj;
            LoadQccDataExtParm extParam = LoadQccDataExtParm.automatic(dataStr);
            JSONArray dataList = new JSONArray(); // 数据列表
            // 回执
            ActiveMQTopic infosResMqTopic = new ActiveMQTopic("qcc-company-infos-response");
            JSONObject resObj = new JSONObject();
            resObj.put("errorMessage", "");
            resObj.put("sourceRequest", extParam.getCommand());
            resObj.put("finished", "failed");
            resObj.put("requestId", extParam.getResDataId());
            // 解析指令
            try{
                dataObj = JSON.parseObject(dataStr);
                dataList = dataObj.getJSONArray("OrderData");
            }catch (Exception e){
                resObj.put("progress", "0");
                resObj.put("errorMessage", "企查查数据指令解析失败");
                jmsMessagingTemplate.convertAndSend(infosResMqTopic, resObj.toJSONString());
                getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+",企查查数据指令解析失败！");
                return;
            }

            // 更新获取并写入数据
            try{
                ExecutorService fixPool = Executors.newFixedThreadPool(16);
                for(int i = 0 ; i < dataList.size() ; i++) {
                    JSONObject data = (JSONObject) dataList.get(i);
                    fixPool.execute(getThread(data, extParam));
                }
                fixPool.shutdown();
                fixPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            }
            catch (Exception e){
                resObj.put("progress", "1");
                resObj.put("errorMessage", "企查查数据获取失败");
                jmsMessagingTemplate.convertAndSend(infosResMqTopic, resObj.toJSONString());
                getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+",获取企查查数据发生异常," + e.toString());
                return;
            }

            if(null!=extParam.getErrorApi() && extParam.getErrorApi().size()>0){
                resObj.put("progress", "1");
                resObj.put("errorMessage", "企查查数据获取部分成功");
                resObj.put("finished", "warning");
                resObj.put("errorApi", JSONObject.toJSONString(extParam.getErrorApi()));
                jmsMessagingTemplate.convertAndSend(infosResMqTopic, resObj.toJSONString());
                getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+",获取企查查数据部分失败！");
            }
            
            // 响应日志，发送zip包后数据将会删除
            MongoCollection<Document> collResLog = mongoService2.getCollection("Response_Log");
            FindIterable<Document> resLogIterable = collResLog.find(Filters.eq("requestId", extParam.getCommandId()));

            // 写入文件--全部请求完成后压缩文件提交解构服务解构数据
            FileLock lock = null;
            File file = extParam.getQccFileDataPath();
            try (
                    RandomAccessFile raf = new RandomAccessFile(file, "rw");
                    FileChannel channel = raf.getChannel();
            ){
                lock = channel.lock();
                if(extParam.getCompanyCount() < 30){
                    for (Document document : extParam.getCacheArr()) {
                        String str = document.toJson();
                        byte[] bs = (str.getBytes(StandardCharsets.UTF_8.name()));
                        //写入数据包长度
                        raf.writeInt(bs.length);
                        raf.write(bs);
                    }
                }else{
                    MongoCursor<Document> resLogData = resLogIterable.iterator();
                    try {
                        while (resLogData.hasNext()){
                            Document item = resLogData.next();
                            String str = item.toJson();
                            byte[] bs = (str.getBytes(StandardCharsets.UTF_8.name()));
                            raf.writeInt(bs.length);
                            raf.write(bs);
                        }
                    }finally {
                        resLogData.close();
                    }
                }
            } catch (IOException e) {
                // 写入文件失败则忽略此次响应
                resObj.put("progress", "2");
                resObj.put("finished", "failed");
                resObj.put("errorMessage", "因写入文件发生异常，企查查取数中止！");
                jmsMessagingTemplate.convertAndSend(infosResMqTopic, resObj.toJSONString());
                getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+"因写入文件发生异常，企查查取数中止！");
                return;
            }
            finally {
                if (lock != null) {
                    try {
                        lock.release();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            };

            // 生成zip包
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
                resObj.put("progress", "3");
                resObj.put("errorMessage", "压缩文件生成失败！");
                jmsMessagingTemplate.convertAndSend(infosResMqTopic, resObj.toJSONString());
                getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+",企查查数据zip包发送失败！");
                e.printStackTrace();
                return;
            }

            if(__zip.exists()){
                // 将压缩文件放入MQ,供解构服务调用
                ActiveMQQueue mqQueue = new ActiveMQQueue("qcc-deconstruct-request");
                jmsMessagingTemplate.convertAndSend(mqQueue , new MQConfig.FileRequest(extParam.getCommand(), extParam.getResDataId(), __zip));
                resObj.put("progress", "3");
                resObj.put("finished", "success");
                resObj.put("errorMessage", "");
                resObj.put("callApiCount", JSONObject.toJSONString(extParam.getTongJiObj()));
                jmsMessagingTemplate.convertAndSend(infosResMqTopic, resObj.toJSONString());

                MongoCursor<Document> resLogData = resLogIterable.iterator();
                try {
                    while (resLogData.hasNext()){
                        Document item = resLogData.next();
                        collResLog.deleteOne(Filters.eq("_id", item.getObjectId("_id")));
                    }
                }catch (Exception e){
                    getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+",响应日志删除失败！");
                    System.out.println("响应日志删除失败，" + e.toString());
                }finally {
                    resLogData.close();
                }
            }else{
                resObj.put("progress", "3");
                resObj.put("errorMessage", "压缩文件不存在！");
                getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+",压缩文件不存在！");
                jmsMessagingTemplate.convertAndSend(infosResMqTopic, resObj.toJSONString());
            }
        }
    }
}
