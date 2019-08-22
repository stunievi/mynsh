package com.beeasy.loadqcc.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.service.RiskMonitorService;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
public class RiskMonitorTask {


    // @Scheduled(cron = "0 10 0 ? * *")
    public void clearTokens() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // 获取最近10笔订单号
        JSONObject resObj = RiskMonitorService.listLatestOrderNo();
        JSONArray orderArr = resObj.getJSONArray("resultList");
        // 下载压缩包地址
//        JSONArray fileArr = new JSONArray();
        for(Object order : orderArr){
            JSONObject orderItem = (JSONObject) order;
            String orderSn = orderItem.getString("orderNo");
            // 根据订单号获取下载文件的url地址
            RiskMonitorService riskMonitorService = new RiskMonitorService();
            JSONObject addrObj = riskMonitorService.getDownloadUrl(orderSn);
            riskMonitorService.resRiskData(addrObj);
        }
    }

}
