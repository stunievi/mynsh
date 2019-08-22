package com.beeasy.loadqcc.controller;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.service.*;
import org.junit.Test;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
//import org.beetl.sql.core.SQLManager;

@RestController
public class test {

//    @Autowired
//    SQLManager sqlManager;

    @Autowired
    Get1 get1;

    @Autowired
    Get2 get2;

    @Autowired
    Get3 get3;

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    MongoService mongoService;

    @Value("${loadQcc.riskTxtPath}")
    String LOAD_RISK_TXT_PATH;

    @Value("${loadQcc.riskZipPath}")
    String LOAD_RISK_ZIP_PATH;

    @RequestMapping(value = "/test")
    public void test() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        JSONObject reqData = (JSONObject) JSONObject.toJSON(C.newMap(
                "orderNo", "RADAR2017111319194268140201",
                "downloadUrl", "http://pro-files.qichacha.com/RadarData/93aa4282a.zip",
                "downloadUrlExpiredTime", "2018-11-20 12:12:00",
                "unzipPassword", "1111111"
        ));

        TestRiskMonitor testRiskMonitor = new TestRiskMonitor(
            jmsMessagingTemplate,
            mongoService,
            LOAD_RISK_TXT_PATH,
            LOAD_RISK_ZIP_PATH
        );
        testRiskMonitor.resRiskData(reqData);

//        get1.getData("惠州市昌氏实业有限公司");
//        get2.getData("澳达树熊涂料（惠州）有限公司");
//        get3.getData("澳达树熊涂料（惠州）有限公司");
    }
}
