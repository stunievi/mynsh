package com.beeasy.loadqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.utils.QccUtil;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Get1 {

    @Autowired
    QccGet qccGet;

    public void getData(String companyName){
        JSONObject comInfo = qccGet.ECIV4GetDetailsByName(companyName);
        // 法人/董事/自然人股东（25%及以上）
        Set<String> persons = new HashSet();
            // 企业关键字精确获取详细信息(Master)  - 法人名 - 法人姓名
        String operName = comInfo.getString("OperName");
        persons.add(operName);
            // 企业关键字精确获取详细信息(Master) - 主要人员 - 董事
        JSONArray perArr1 = comInfo.getJSONArray("Employees");
        perArr1.forEach(val -> {
            JSONObject p = (JSONObject) val;
            persons.add(p.getString("Name"));
        });
            // 企业关键字精确获取详细信息(Master) - 股东信息 - 自然人股东
        JSONArray perArr2 = comInfo.getJSONArray("Partners");
        perArr2.forEach(val -> {
            JSONObject p = (JSONObject) val;
            float stockPercent = Float.valueOf(p.getString("StockPercent").replace("%", ""));
            if(stockPercent >= 25 && "自然人股东".equals(p.getString("StockType"))){
                persons.add(p.getString("StockName"));
            }
        });
        // 关联公司列表
        ArrayList<JSONObject> groupList = new ArrayList<>();
        // 人名列表，逐个判断，获取董监高信息
        for (String name : persons) {
            JSONObject row = new JSONObject();
            row.put("comName", companyName);
            row.put("link", "自然人：" + name);
            row.put("rule", "11.1");
            // 取得某个高管名字,然后拿去董监高信息，注意是列表！
            boolean lastPage = false;
            int pageSize = 10;
            int pageIndex = 1;
            int totalRecords = 0;
            // 当前人名关联集团名单
            Set<String> comGroupNames = new HashSet<>();
            do {
                JSONObject params = new JSONObject();
                params.put("searchKey", companyName);
                params.put("personName", name);
                params.put("pageIndex", pageIndex);
                JSONObject seniorPersonInfo = qccGet.ECISeniorPersonGetList(params);
                if(!"200".equals(seniorPersonInfo.getString("Status"))){
                    System.out.println(seniorPersonInfo.toJSONString());
                    break;
                }
                JSONObject pageItem = seniorPersonInfo.getJSONObject("Paging");
                pageSize = pageItem.getInteger("PageSize");
                pageIndex = pageItem.getInteger("PageIndex");
                totalRecords = pageItem.getInteger("TotalRecords");
                if(pageSize * pageIndex >= totalRecords){
                    lastPage = true;
                }else{
                    ++pageIndex;
                }
                JSONArray seniorPersonComArr = seniorPersonInfo.getJSONArray("Result");
                for (Object item: seniorPersonComArr){
                    JSONObject com = (JSONObject) item;
                    // 去除当前公司
                    String comName = com.getString("Name");
                    if(companyName.equals(comName)){
                        continue;
                    }
                    JSONArray relationList = com.getJSONArray("RelationList");
                    // 法人
                    boolean cond1 = false;
                    // 股东
                    boolean cond2 = false;
                    // 任职
                    boolean cond3 = false;
                    for(Object relationItem : relationList){
                        JSONObject relation = (JSONObject) relationItem;
                        if("0".equals(relation.getString("Type"))){
                            cond1 = true;
                            break;
                        }else if("1".equals(relation.getString("Type"))){
                            float stockPercent = Float.valueOf(relation.getString("Value").replace("%", ""));
                            if(stockPercent > 25){
                                cond2 = true;
                                break;
                            }
                        }else if("2".equals(relation.getString("Type"))){
                            if(relation.getString("Value").indexOf("董事") > -1){
                                cond3 = true;
                                break;
                            }
                        }
                    }
                    if(cond1 || cond2 || cond3){
                        // TODO:: 接入db2判断是否在银行有贷款
                        comGroupNames.add(comName);
                    }
                }
            }while (lastPage == false);

            row.put("relatedCom", comGroupNames.stream().collect(Collectors.joining("、")));
            groupList.add(row);

        }

        // 得到当前公司集团名单
        System.out.println(groupList);


    }

}
