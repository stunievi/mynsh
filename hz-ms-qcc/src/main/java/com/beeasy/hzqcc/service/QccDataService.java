package com.beeasy.hzqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QccDataService {

    @Autowired
    MongoService mongoService;

    // 根据条件获取一条数据
    public JSONObject findOne(
            String collName,
            Map<String, String> where
    ){
        if(where.size() <1){
            return new JSONObject();
        }
        where.remove("getOriginData");
        Bson filter = null;
        ArrayList cond = new ArrayList();
        for(Map.Entry<String, String> entry : where.entrySet()){
            cond.add(Filters.eq("QueryParam.".concat(entry.getKey()), entry.getValue()));;
        }
        if(cond.size() > 0){
            filter = Filters.and(cond);
        }
        Document data = mongoService.getCollection(collName).find(filter).projection(new Document().append("_id", 0)).sort(new Document().append("QueryParam.getDataTime", -1)).first();
        if(null == data){
            data = new Document();
            data.put("Status", "201");
            data.put("Message", "请严格输入参数，不能多参数少参数");
        }
        return (JSONObject) JSON.toJSON(data);
    }
    // 获取列表数据
    public JSONObject getDataList(
        String collName,
        Map<String, String> where
    ){
        boolean getOriginData = "true".equals(where.get("getOriginData"));
        if(getOriginData){
            return findOne(collName, where);
        }

        JSONObject paramObj = (JSONObject) JSON.toJSON(where);
        if(where.size() <1){
            return new JSONObject();
        }
        Bson filter = null;
        ArrayList cond = new ArrayList();

        for(Map.Entry<String, String> entry : where.entrySet()){
            String key = entry.getKey();
            if("pageIndex".equals(key) || "pageSize".equals(key) || "getOriginData".equals(key) || "order".equals(key) || "".equals(S.trim(entry.getValue()))){
                continue;
            }
            cond.add(Filters.eq("QueryParam.".concat(entry.getKey()), entry.getValue()));;
        }
        if(cond.size() > 0){
            Filters.gte("QueryParam.getDataTime","");
            filter = Filters.and(cond);
        }else{
            return new JSONObject();
        }
        Map retData = new HashMap();
        MongoCollection<Document> coll = mongoService.getCollection(collName);
        Document oneData = coll.find(filter).sort(new Document().append("QueryParam.getDataTime", -1)).first();
        String dataVersion = "0";
        if(null != oneData){
            JSONObject oneDataObj = (JSONObject) JSON.toJSON(oneData);
            JSONObject oneDateParamObj = oneDataObj.getJSONObject("QueryParam");
            dataVersion = oneDateParamObj.getString("getDataTime");
            if(null != dataVersion){
                cond.add(Filters.gte("QueryParam.getDataTime",dataVersion));
                filter = Filters.and(cond);
            }
        }

        Integer pageSize = paramObj.getInteger("pageSize");
        Integer pageNumber = paramObj.getInteger("pageIndex");
        Map pageObj = new HashMap();
        // 分页数据
        pageObj.put("PageSize", pageSize);
        pageObj.put("PageIndex", pageNumber);
        ArrayList _dataCount = coll
                .aggregate(
                        Arrays.asList(
                                Aggregates.match(filter),
                                Aggregates.unwind("$Result"),
                                Aggregates.count()
                        )
                ).into(new ArrayList<>());
        Integer dataCount = 0;
        if(_dataCount.size() > 0){
            dataCount = (Integer) ((Map) _dataCount.get(0)).get("count");
        }
        pageObj.put("TotalRecords", dataCount);
        retData.put("Paging", pageObj);
        ArrayList data = coll
                .aggregate(
                        Arrays.asList(
                                Aggregates.match(filter),
                                Aggregates.unwind("$Result"),
                                Aggregates.skip(pageSize * ( pageNumber - 1)),
                                Aggregates.limit(pageSize),
                                Aggregates.project(new Document().append("_id", 0).append("Result", 1)),
                                Aggregates.replaceRoot("$Result")
                        )
                ).into(new ArrayList<>());
        retData.put("Result", data);
        return (JSONObject) JSON.toJSON(retData);
    }
    // 切分Result下中某个字段作为数据列表
    public JSONObject getSplitChildDataList(
            String collName,
            Map param,
            String splitChildName
    ){
        JSONObject paramObj = (JSONObject) JSON.toJSON(param);

        if("true".equals(paramObj.getString("getOriginData"))){
            return findOne(collName, param);
        }
        param.remove("getOriginData");

        String keyWord = paramObj.getString("keyWord");
        Integer pageSize = paramObj.getInteger("pageSize");
        Integer pageNumber = paramObj.getInteger("pageIndex");

        JSONObject retData = new JSONObject();
        Map pageObj = new HashMap();
        if(null == keyWord){
            return retData;
        }
        MongoCollection<Document> coll = mongoService.getCollection(collName);
        // 分页数据
        pageObj.put("PageSize", pageSize);
        pageObj.put("PageIndex", pageNumber);
        ArrayList _dataCount = coll
                .aggregate(
                        Arrays.asList(
                                Aggregates.match(Filters.eq("QueryParam.keyWord", keyWord)),
                                Aggregates.unwind("$Result."+splitChildName),
                                Aggregates.count()
                        )
                ).into(new ArrayList<>());
        Integer dataCount = 0;
        if(_dataCount.size() > 0){
            dataCount = (Integer) ((Map) _dataCount.get(0)).get("count");
        }
        pageObj.put("TotalRecords", dataCount);
        retData.put("Paging", pageObj);
        ArrayList data = coll
                .aggregate(
                        Arrays.asList(
                                Aggregates.match(Filters.eq("QueryParam.keyWord", keyWord)),
                                Aggregates.unwind("$Result."+splitChildName),
                                Aggregates.skip(pageSize * ( pageNumber - 1)),
                                Aggregates.limit(pageSize),
                                Aggregates.project(new Document().append("_id", 0).append(splitChildName, "$Result."+splitChildName)),
                                Aggregates.replaceRoot("$"+splitChildName)
                        )
                ).into(new ArrayList<>());
        Map dataList = new HashMap<>();
        dataList.put(splitChildName, data);
        retData.put("Result", dataList);
        return (JSONObject) JSON.toJSON(retData);
    }

}
