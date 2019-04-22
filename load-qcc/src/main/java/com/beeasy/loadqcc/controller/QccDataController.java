package com.beeasy.loadqcc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.config.MQConfig;
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

    // 发送压缩文件
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
            getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+"企查查数据zip包发送失败！");
            e.printStackTrace();
        }

        // 将压缩文件放入MQ,供解构服务调用
        ActiveMQTopic mqTopic = new ActiveMQTopic("qcc-deconstruct-request");
        jmsMessagingTemplate.convertAndSend(mqTopic, new MQConfig.FileRequest(extParam.getCommond(), extParam.getCommandId(), __zip));
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
        }
        if(null == companyInfo || companyInfo.isEmpty()){
            getOriginQccService.saveErrLog("更新企查查数据时，"+companyName+":公司工商信息获取失败！");
            return companyInfo;
        }
        return companyInfo;
    }

    // 监听MQ递送的更新名单
    @JmsListener(destination = "my_msg", containerFactory = "jmsListenerContainerTopic")
    public void test(Object o) throws JMSException, IOException {
        if(o instanceof TextMessage){
            String dataStr = ((TextMessage) o).getText();
            JSONObject dataObj;
            LoadQccDataExtParm extParam;
            try{
                extParam = LoadQccDataExtParm.automatic(dataStr);
                dataObj = JSON.parseObject(dataStr);
            }catch (Exception e){
                getOriginQccService.saveErrLog("更新企查查数据指令解析失败！");
                return;
            }
            // 更新名单
            try{
                List dataList = dataObj.getJSONArray("OrderData"); // 数据列表
                for(int i = 0 ; i < dataList.size() ; i++) {
                    JSONObject data = (JSONObject) dataList.get(i);
                    String companyName = data.getString("Content"); // 公司名
                    String command = data.getString("Sign"); // 指令
                    if(null == companyName|| companyName.isEmpty()){
                        getOriginQccService.saveErrLog("更新企查查数据时，公司名获取为空！");
                        continue;
                    }
                    if(null == command || command.isEmpty()){
                        getOriginQccService.saveErrLog("更新企查查数据时，"+companyName+":指令为空");
                        continue;
                    }
                    // 公司公司信息
                    JSONObject companyInfo = new JSONObject();
                    // 更新所有
                    if(command.contains("00")){
                        getOriginQccService.loadAllData(companyName, extParam);
                    }else{
                        // 需要keyNo的必须拿到工商信息
                        if(command.contains("03") || command.contains("05")){
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
                            // 关联族谱
                            getOriginQccService.loadDataBlock3(companyName, companyInfo.getString("KeyNo"), extParam);
                        }
                        if(command.contains("04")){
                            // 历史信息
                            getOriginQccService.loadDataBlock4(companyName, extParam);
                        }
                        if(command.contains("05")){
                            // 经营风险
                            getOriginQccService.loadDataBlock5(companyName, companyInfo.getString("KeyNo"), extParam);
                        }
                    }
                }
                sendQccZipToMQ(extParam);
            }
            catch (Exception e){
                getOriginQccService.saveErrLog("指令："+extParam.getCommandId()+"更新企查查数据时发生异常");
            }
        } else if(o instanceof BlobMessage){
            // 更新完成后的压缩文件
//            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
//            System.out.println(file);
        }
    }
}
