package com.beeasy.hzqcc.service;

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
//        if(items.size() > 0){
            return Filters.and(items);
//        }
    }

    public Map ECI_GetDetailsByName(
            String companyName
    ){
        Document fu = mongoService.getCollection("fu").find().first();
        return fu;
    }

    private Map getDataList(
            String collName,
            Map param
    ){
        MongoCollection<Document> tableDoc = mongoService.getCollection(collName);
        MongoCursor<Document> tempList;
        Long totalRow;
        Integer pageSize = Integer.parseInt((String) param.get("pageSize"));
        Integer pageNumber = Integer.parseInt((String) param.get("pageNumber"));
        param.remove("pageSize");
        param.remove("pageNumber");
        if(param.size() > 0){
            totalRow = tableDoc.countDocuments(fixParam(param));
            tempList = tableDoc.find(fixParam(param)).skip((pageNumber-1)*pageSize).limit(pageSize).iterator();
        }else{
            totalRow = tableDoc.countDocuments();
            tempList = tableDoc.find().skip((pageNumber-1)*pageSize).limit(pageSize).iterator();
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

    // 获取裁判文书
    public Map JudgeDoc_SearchJudgmentDoc(
            Map param
    ){
//        MongoCollection<Document> tableDoc = mongoService.getCollection("JudgeDoc_SearchJudgmentDoc");
//        MongoCursor<Document> tempList;
//        if(param.size() > 0){
//            tempList = tableDoc.find(fixParam(param)).iterator();
//        }else{
//            tempList = tableDoc.find().iterator();
//        }
//        ArrayList<Map> dataList = new ArrayList<>();
//        while (tempList.hasNext()){
//            dataList.add(tempList.next());
//        }
        return getDataList("JudgeDoc_SearchJudgmentDoc", param);
    }

    public Map JudgeDoc_GetJudgementDetail(
            String Id
    ){
        return mongoService.getCollection("JudgeDoc_SearchJudgmentDoc").find(Filters.eq("Id", Id)).first();
    }

}
