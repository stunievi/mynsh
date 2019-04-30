package com.beeasy.loadqcc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.config.MQConfig;
import com.beeasy.loadqcc.entity.LoadQccDataExtParm;
import com.beeasy.loadqcc.service.GetOriginQccService;
import com.beeasy.loadqcc.service.MongoService;
import com.mongodb.client.model.Filters;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.bson.Document;
import org.osgl.util.C;
import org.osgl.util.IO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class QccDataController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    @Qualifier(value = "qcc1")
    MongoService mongoService;
    @Autowired
    GetOriginQccService getOriginQccService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // 获取公司工商信息
    private JSONObject getCompanyInfo(
            String companyName,
            LoadQccDataExtParm extParam
    ){
        JSONObject companyInfo = new JSONObject();
        Document tmep_company_ret;
        tmep_company_ret =  mongoService.getCollection("ECIV4_GetDetailsByName").find(Filters.eq("QueryParam.keyword", companyName)).first();
        if(null == tmep_company_ret){
            // 从企查查获取工商信息
            companyInfo = getOriginQccService.ECI_GetDetailsByName(companyName, extParam);
        }else{
            companyInfo = (JSONObject) JSON.toJSON(tmep_company_ret);
            companyInfo = companyInfo.getJSONObject("Result");
            getOriginQccService.saveOriginData("ECIV4_GetDetailsByName", C.newMap(
                    "keyword", companyName
            ), JSON.toJSONString(tmep_company_ret), extParam);
        }
        if(null == companyInfo || companyInfo.isEmpty()){
            getOriginQccService.saveErrLog("更新企查查数据时，"+companyName+":公司工商信息获取失败！");
            return companyInfo;
        }
        return companyInfo;
    }

    // 监听MQ更新名单回执
    @JmsListener(destination = "qcc-company-infos-response", containerFactory = "jmsListenerContainerTopic")
    public void test2(Object o) throws JMSException, IOException {
        if(o instanceof TextMessage){
            System.out.println(((TextMessage) o).getText());
        } else if(o instanceof BlobMessage){
            // 更新完成后的压缩文件
            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
            System.out.println(file);
        }
    }

    // 监听MQ递送的更新名单
    @JmsListener(destination = "qcc-company-infos-request")
    public void test(Object o) throws JMSException {
        if(o instanceof TextMessage){
            String dataStr = ((TextMessage) o).getText();
            JSONObject dataObj;
            LoadQccDataExtParm extParam = LoadQccDataExtParm.automatic(dataStr);
            JSONArray dataList = new JSONArray(); // 数据列表

            // 回执
            ActiveMQTopic mqTopic2 = new ActiveMQTopic("qcc-company-infos-response");
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
                jmsMessagingTemplate.convertAndSend(mqTopic2, resObj.toJSONString());
                getOriginQccService.saveErrLog("企查查数据指令解析失败！");
                return;
            }
            // 更新获取并写入数据
            try{
                for(int i = 0 ; i < dataList.size() ; i++) {
                    JSONObject data = (JSONObject) dataList.get(i);
                    String companyName = data.getString("Content"); // 公司名
                    String command = data.getString("Sign"); // 指令
                    if(null == companyName|| companyName.isEmpty()){
                        continue;
                    }
                    extParam.setCompanyName(companyName);
                    if(null == command || command.isEmpty()){
                        getOriginQccService.saveErrLog("更新企查查数据时，"+companyName+":指令为空");
                        continue;
                    }
                    // 公司公司信息
                    JSONObject companyInfo = new JSONObject();
                    if(extParam.isWriteTxtFileState() == false){
                        resObj.put("progress", "2");
                        resObj.put("finished", "failed");
                        resObj.put("errorMessage", "因写入文件失败，企查查查查取数中止！");
                        jmsMessagingTemplate.convertAndSend(mqTopic2, resObj.toJSONString());
                        break;
                    }

                    // 更新所有
                    if(command.contains("00")){
                        getOriginQccService.loadAllData(companyName, extParam);
                    }else{
                        // 需要keyNo的必须拿到工商信息
                        if(command.contains("03") || command.contains("04")){
                            companyInfo = getCompanyInfo(companyName, extParam);
                        }
                        if(command.contains("01")){
                            // 更新基本信息
                            getOriginQccService.loadDataBlock1(companyName, extParam);
                        }
                        if(command.contains("02")){
                            // 法律诉讼
                            getOriginQccService.loadDataBlock2(companyName, extParam);
                        }
                        if(command.contains("03")){
                            // 经营风险
                            getOriginQccService.loadDataBlock5(companyName, companyInfo.getString("KeyNo"), extParam);
                        }
                        if(command.contains("04")){
                            // 关联族谱
                            getOriginQccService.loadDataBlock3(companyName, companyInfo.getString("KeyNo"), extParam);
                        }
                        if(command.contains("05")){
                            // 历史信息
                            getOriginQccService.loadDataBlock4(companyName, extParam);

                        }
                    }
                }
            }
            catch (Exception e){
                resObj.put("progress", "1");
                resObj.put("errorMessage", "企查查数据获取失败");
                jmsMessagingTemplate.convertAndSend(mqTopic2, resObj.toJSONString());
                getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+"获取企查查数据发生异常" + e.toString());
                return;
            }

            // 写入文件失败则忽略此次响应
            if(extParam.isWriteTxtFileState() == false){
                resObj.put("progress", "2");
                resObj.put("finished", "failed");
                resObj.put("errorMessage", "因写入文件失败，企查查查查取数中止！");
                jmsMessagingTemplate.convertAndSend(mqTopic2, resObj.toJSONString());
                return;
            }

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
                getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+"企查查数据zip包发送失败！");
                resObj.put("progress", "3");
                resObj.put("errorMessage", "压缩文件生成失败！");
                jmsMessagingTemplate.convertAndSend(mqTopic2, resObj.toJSONString());
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
                jmsMessagingTemplate.convertAndSend(mqTopic2, resObj.toJSONString());
            }else{
                resObj.put("progress", "3");
                resObj.put("errorMessage", "压缩文件不存在！");
                jmsMessagingTemplate.convertAndSend(mqTopic2, resObj.toJSONString());
            }

        }
    }
}
