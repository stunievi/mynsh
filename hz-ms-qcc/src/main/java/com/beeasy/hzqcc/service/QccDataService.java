package com.beeasy.hzqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QccDataService {

    @Autowired
    @Qualifier(value = "qcc1")
    MongoService mongoService;

    @Autowired
    @Qualifier(value = "qcc2")
    MongoService mongoService2;

    // 根据条件获取一条数据
    public JSONObject findOne(
            String collName,
            Map<String, String> where
    ){
        where.remove("pageSize");
        where.remove("order");
        Bson filter = null;
        ArrayList cond = new ArrayList();
        for(Map.Entry<String, String> entry : where.entrySet()){
            cond.add(Filters.eq("QueryParam.".concat(entry.getKey()), entry.getValue()));;
        }
        if(cond.size() > 0){
            filter = Filters.and(cond);
        }else{
            JSONObject data = new JSONObject();
            data.put("Status", "201");
            data.put("Message", "请输入查询条件");
            return data;
        }
        Document data = mongoService.getCollection(collName).find(filter).projection(new Document().append("_id", 0).append("QueryParam", 0)).sort(new Document().append("QueryParam.getDataTime", -1)).first();

        Document data2 = mongoService2.getCollection(collName).find(filter).projection(new Document().append("_id", 0).append("QueryParam", 0)).sort(new Document().append("QueryParam.getDataTime", -1)).first();
        if(null == data){
            data = new Document();
            data.put("Status", "201");
            data.put("Message", "请严格输入参数，不能多参数少参数;pageSize不用且不能指定！");
        }
        return (JSONObject) JSON.toJSON(data);
    }
    // 获取列表数据
    public JSONObject getDataList(
        String collName,
        Map<String, String> where
    ){
        return findOne(collName, where);
    }
    // 切分Result下中某个字段作为数据列表
    public JSONObject getSplitChildDataList(
            String collName,
            Map param
    ){
        return findOne(collName, param);
    }

}
