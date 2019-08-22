package com.beeasy.loadqcc.controller;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.service.RiskMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@RestController
public class RiskMonitoringController {

    /**
     * orderNo	String	是	订单号
     * downloadUrl	String	是	下载监控数据URL
     * downloadUrlExpiredTime	String	是	下载监控数据URL失效时间
     * unzipPassword	String	是	压缩包解压密码
     */
    @RequestMapping("/qcc/riskMonitoring/callback")
    public void qccRiskMonitoringCallback(
            @RequestParam Map<String,Object> params
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        RiskMonitorService riskMonitorService = new RiskMonitorService();
        riskMonitorService.resRiskData((JSONObject) params);
    }

}
