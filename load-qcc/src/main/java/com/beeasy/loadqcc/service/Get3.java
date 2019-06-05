package com.beeasy.loadqcc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class Get3 {

    @Autowired
    QccGet qccGet;

    public  void getData(String companyName){
        JSONObject comInfo = qccGet.ECIV4GetDetailsByName(companyName);
        JSONArray partners = comInfo.getJSONArray("Partners");
        // 关联公司列表
        ArrayList<JSONObject> groupList = new ArrayList<>();

        JSONObject row = new JSONObject();
        row.put("comName", companyName);
        row.put("link", "企业股东");
        row.put("rule", "11.4");

        Set<String> comGroupNames = new HashSet<>();
        for (Object item : partners){
            JSONObject partner = (JSONObject) item;
            float stockPercent = Float.valueOf(partner.getString("StockPercent").replace("%", ""));
            if(stockPercent > 25 && "企业法人".equals(partner.getString("StockType"))){
                String stockName = partner.getString("StockName");
                // TODO:: 接入db2判断是否在银行有贷款
                comGroupNames.add(stockName);
            }
        }
        row.put("relatedCom", comGroupNames.stream().collect(Collectors.joining("、")));
        groupList.add(row);

        // 得到当前公司集团名单
         System.out.println(groupList);
    }

}
