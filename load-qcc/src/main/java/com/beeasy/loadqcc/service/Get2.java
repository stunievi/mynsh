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
public class Get2 {

    @Autowired
    QccGet qccGet;

    public void getData(String companyName){
//        String companyName = "某公司";
        JSONObject comInfo = qccGet.ECIV4GetDetailsByName(companyName);
        // 企业股东（25%及以上）
        Set<String> companyPartners = new HashSet();
        List comArr = comInfo.getJSONArray("Partners"); // 企业关键字精确获取详细信息(Master) - 股东信息 - 企业股东
        comArr.forEach(val->{
            JSONObject p = (JSONObject) val;
            float stockPercent = Float.valueOf(p.getString("StockPercent").replace("%", ""));
            if(stockPercent >= 25 && "企业法人".equals(p.getString("StockType"))){
                companyPartners.add(p.getString("StockName"));
            }
        });
        // 关联公司列表
        ArrayList<JSONObject> groupList = new ArrayList<>();
        for (String comName : companyPartners) {
            boolean lastPage = false;
            int pageSize = 10;
            int pageIndex = 1;
            int totalRecords = 0;

            JSONObject row = new JSONObject();
            row.put("comName", companyName);
            row.put("link", "企业：" + comName);
            row.put("rule", "11.3");
            Set<String> comGroupNames = new HashSet<>();
            do {
                JSONObject params = new JSONObject();
                params.put("keyWord", companyName);
                params.put("pageIndex", pageIndex);
                JSONObject holdingCompanyInfo = qccGet.HoldingCompany_GetHoldingCompany(params);
                if(!"200".equals(holdingCompanyInfo.getString("Status"))){
                    System.out.println(holdingCompanyInfo.toJSONString());
                    break;
                }
                JSONObject pageItem = holdingCompanyInfo.getJSONObject("Paging");
                pageSize = pageItem.getInteger("PageSize");
                pageIndex = pageItem.getInteger("PageIndex");
                totalRecords = pageItem.getInteger("TotalRecords");
                if(pageSize * pageIndex >= totalRecords){
                    lastPage = true;
                }else{
                    ++pageIndex;
                }
                JSONObject holdingCompanysResult = holdingCompanyInfo.getJSONObject("Result");
                JSONArray holdingCompanys = holdingCompanysResult.getJSONArray("Names");
                for (Object item : holdingCompanys){
                    JSONObject com = (JSONObject) item;

                    float stockPercent = Float.valueOf(com.getString("PercentTotal").replace("%", ""));
                    // 控股（25%及以上）公司列表
                    if(stockPercent >= 25 ){
                        // TODO:: 接入db2判断是否在银行有贷款
                        comGroupNames.add(com.getString("Name"));
                    }
                }
            }while (lastPage==false);
            row.put("relatedCom", comGroupNames.stream().collect(Collectors.joining("、")));
            groupList.add(row);
        }

        // 得到当前公司集团名单
         System.out.println(groupList);


    }

}
