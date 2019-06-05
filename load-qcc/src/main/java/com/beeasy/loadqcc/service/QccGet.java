package com.beeasy.loadqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.utils.QccUtil;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class QccGet {
    @Autowired
    MongoService mongoService2;

    private static String QCC_DOMAIN_PRX = "http://api.qichacha.com";

    private String haveTodayData(
            String tableName,
            Map<String, ?> queries
    ){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(new Date());
        List filters = new ArrayList();
        for (Map.Entry<String, ?> entry : queries.entrySet()){
            filters.add(Filters.eq("QueryParam.".concat(entry.getKey()), entry.getValue()));
        }
        Document data = mongoService2.getCollection(tableName).find(Filters.and(filters)).first();
        if(null == data || data.isEmpty()){
            return null;
        }else{
            return JSON.toJSONString(JSON.toJSON(data));
        }
    }

    // 企业工商信息
    public JSONObject ECIV4GetDetailsByName(String keyWord){
        // 企查查为 keyword !!!
        Map query = C.newMap(
                "keyword", keyWord
        );
        String collName = "ECIV4_GetDetailsByName";
        String res = haveTodayData(collName, query);
        if(null == res){
            res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIV4/GetDetailsByName", query);
        }
        JSONObject resObj = JSON.parseObject(res);
        if(!"200".equals(resObj.getString("Status"))){
            System.out.println("公司信息不存在");
            return new JSONObject();
        }
        JSONObject retObj = resObj.getJSONObject("Result");
        return retObj;
    }
    // 董监高信息列表(新版)
    public JSONObject ECISeniorPersonGetList(JSONObject params){
        Map query = C.newMap(
                "searchKey", params.getString("searchKey"),
                "personName", params.getString("personName"),
                "pageIndex", params.getInteger("pageIndex")

        );
        String collName = "ECISeniorPerson_GetList";
        String res = haveTodayData(collName, query);
        if(null == res){
            res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECISeniorPerson/GetList", query);
        }
        JSONObject resObj = JSON.parseObject(res);
        return resObj;
    }
    // 控股公司
    public JSONObject HoldingCompany_GetHoldingCompany(JSONObject params){
        Map query = C.newMap(
                "keyWord", params.getString("keyWord"),
                "pageSize", 20,
                "pageIndex", params.getInteger("pageIndex")

        );
        String collName = "HoldingCompany_GetHoldingCompany";
        String res = haveTodayData(collName, query);
        if(null == res){
            res = QccUtil.getData(QCC_DOMAIN_PRX + "/HoldingCompany/GetHoldingCompany", query);
        }
        JSONObject resObj = JSON.parseObject(res);
        return resObj;
    }
}
