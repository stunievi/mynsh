package com.beeasy.hzqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class QccService {

    @Autowired
    MongoService mongoService;

    // 修正条件参数
    private Bson fixParam(
            Map<String, String> param
    ){
        ArrayList items = new ArrayList();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            if(!"".equals(entry.getKey()) && !"".equals(entry.getValue())){
                items.add( Filters.eq(entry.getKey(), entry.getValue()));
            }
        }
        return Filters.and(items);
    }

    // 分页获取数据
    private Map getDataList(
            String collName,
            JSONObject param,
            Bson filter
    ){
        MongoCollection<Document> tableDoc = mongoService.getCollection(collName);
        MongoCursor<Document> tempList;
        Long totalRow;
        Integer pageSize = param.getInteger("pageSize");
        Integer pageNumber = param.getInteger("pageNumber");
        if(null == pageSize || "".equals(pageSize) || pageSize<1){
            pageSize = 20;
        }
        if(null == pageNumber || "".equals(pageNumber) || pageNumber<1){
            pageNumber = 1;
        }
        Bson orderBy = new BasicDBObject("_id", 1);
        if(null != filter){
            totalRow = tableDoc.countDocuments(filter);
            tempList = tableDoc.find(filter).sort(orderBy).skip((pageNumber-1)*pageSize).limit(pageSize).iterator();
        }else{
            totalRow = tableDoc.countDocuments();
            tempList = tableDoc.find().sort(orderBy).skip((pageNumber-1)*pageSize).limit(pageSize).iterator();
        }
        ArrayList<Map> dataList = new ArrayList<>();
        while (tempList.hasNext()){
            dataList.add(tempList.next());
        }
        Map ret = new HashMap();

        ret.put("totalPage", (int)Math.ceil(totalRow/pageSize));
        ret.put("totalRow", totalRow.intValue());
        ret.put("pageSize", pageSize);
        ret.put("pageNumber", pageNumber);
        ret.put("list", dataList);
        return ret;
    }

    // 企业关键字精确获取详细信息(Basic)
    public Map ECI_GetBasicDetailsByName(
            String companyName
    ){
        return mongoService.getCollection("ECI_GetBasicDetailsByName").find(
                Filters.eq("Name", companyName)
        ).first();
    }

    // 获取裁判文书
    public Map JudgeDoc_SearchJudgmentDoc(
            Map param
    ){
        JSONObject paramObj = (JSONObject) JSON.toJSON(param);
        Bson filter = null;
        ArrayList cond = new ArrayList();
        if(null != param.get("caseRole") && !"".equals(paramObj.getString("caseRole"))){
            cond.add(Filters.regex("CaseRole", paramObj.getString("caseRole")));
        }
        if(null != param.get("caseType") && !"".equals(paramObj.getString("caseType"))){
            cond.add(Filters.eq("caseType", paramObj.getString("caseType")));
        }
        if(cond.size() > 0){
            filter = Filters.and(cond);
        }
        return getDataList("JudgeDoc_SearchJudgmentDoc", paramObj, filter);
    }

    // 裁判文书详情
    public Map JudgeDoc_GetJudgementDetail(
            String Id
    ){
        return mongoService.findOne("JudgeDoc_SearchJudgmentDoc", Filters.eq("Id", Id));
    }

    // 开庭公告列表
    public Map CourtAnno_SearchCourtNotice(
            Map param
    ){
        JSONObject paramObj = (JSONObject) JSON.toJSON(param);
        Bson filter = null;
        ArrayList cond = new ArrayList();
        if(null != param.get("searchKey") && !"".equals(paramObj.getString("searchKey"))){
            cond.add(Filters.regex("Defendant.Name", paramObj.getString("searchKey")));
        }
        if(cond.size() > 0){
            filter = Filters.and(cond);
        }
        return getDataList("CourtAnno_SearchCourtNotice", paramObj, filter);
    }

    // 开庭公告详情
    public Document CourtAnno_GetCourtNoticeInfo(
            String Id
    ){
        return mongoService.findOne("CourtAnno_SearchCourtNotice", Filters.eq("Id", Id));
    }

    // 历史工商信息
    public Document History_GetHistorytEci(
            String keyWord
    ){
        return mongoService.findOne("History_GetHistorytEci", Filters.eq("KeyWordVal", keyWord));
    }
}
