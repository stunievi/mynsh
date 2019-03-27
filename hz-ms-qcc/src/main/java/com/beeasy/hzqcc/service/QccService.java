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
            pageSize = param.getInteger("size");
            if(null == pageSize || "".equals(pageSize) || pageSize<1){
                pageSize = 10;
            }
        }
        if(null == pageNumber || "".equals(pageNumber) || pageNumber<1){
            pageNumber = param.getInteger("page");
            if(null == pageNumber || "".equals(pageNumber) || pageNumber<1){
                pageNumber = 1;
            }
        }
        Bson orderBy = new BasicDBObject("_id", 1);
        if(null == filter){
            totalRow = tableDoc.countDocuments();
            tempList = tableDoc.find().sort(orderBy).skip((pageNumber-1)*pageSize).limit(pageSize).iterator();
        }else{
            totalRow = tableDoc.countDocuments(filter);
            tempList = tableDoc.find(filter).sort(orderBy).skip((pageNumber-1)*pageSize).limit(pageSize).iterator();
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

    private Map getSimpleParamAndFilter(
            Map param
    ){
        JSONObject paramObj = (JSONObject) JSON.toJSON(param);
        Bson filter = null;
        ArrayList cond = new ArrayList();
        if(null != param.get("keyWord") && !"".equals(paramObj.getString("keyWord"))){
            cond.add(Filters.eq("KeyWordVal", paramObj.getString("keyWord")));
        }
        if(cond.size() > 0){
            filter = Filters.and(cond);
        }
        Map ret = new HashMap();
        ret.put("paramObj", paramObj);
        ret.put("filter", filter);
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

    // 历史对外投资
    public Map History_GetHistorytInvestment(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("History_GetHistorytInvestment", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
     // 历史股东
    public Map History_GetHistorytShareHolder(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("History_GetHistorytShareHolder", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 历史失信
    public Map History_GetHistoryShiXin(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("History_GetHistoryShiXin", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 历史执行
    public Map History_GetHistoryZhiXing(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("History_GetHistoryZhiXing", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 历史法院公告
    public Map History_GetHistorytCourtNotice(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("History_GetHistorytCourtNotice", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 历史法院公告
    public Map History_GetHistorytJudgement(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("History_GetHistorytJudgement", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 历史开庭公告
    public Map History_GetHistorytSessionNotice(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("History_GetHistorytSessionNotice", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 历史动产抵押
    public Map History_GetHistorytMPledge(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("History_GetHistorytMPledge", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 历史股权出质
    public Map History_GetHistorytPledge(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("History_GetHistorytPledge", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 历史行政处罚
    public Map History_GetHistorytAdminPenalty(
            String keyWord
    ){
        return mongoService.findOne("History_GetHistorytAdminPenalty", Filters.eq("KeyWordVal", keyWord));
    }
    // 历史行政许可
    public Map History_GetHistorytAdminLicens(
            String keyWord
    ){
        return mongoService.findOne("History_GetHistorytAdminLicens", Filters.eq("KeyWordVal", keyWord));
    }
    // 获取环保处罚列表
    public Map EnvPunishment_GetEnvPunishmentList(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("EnvPunishment_GetEnvPunishmentList", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 环保处罚详情
    public Document EnvPunishment_GetEnvPunishmentDetails(
            String id
    ){
        return mongoService.findOne("EnvPunishment_GetEnvPunishmentList", Filters.eq("Id",id));
    }
    // 获取土地抵押列表
    public Map LandMortgage_GetLandMortgageList(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("LandMortgage_GetLandMortgageList", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 土地抵押详情
    public Document LandMortgage_GetLandMortgageDetails(
            String id
    ){
        return mongoService.findOne("LandMortgage_GetLandMortgageList", Filters.eq("Id",id));
    }
    // 司法拍卖列表
    public Map JudicialSale_GetJudicialSaleList(
            Map param
    ){
        Map ret = getSimpleParamAndFilter(param);
        return getDataList("JudicialSale_GetJudicialSaleList", (JSONObject) ret.get("paramObj"), (Bson) ret.get("filter"));
    }
    // 司法拍卖详情
    public Document JudicialSale_GetJudicialSaleDetail(
            String id
    ){
        return mongoService.findOne("JudicialSale_GetJudicialSaleList", Filters.eq("Id",id));
    }
    // 动产抵押
    public ArrayList ChattelMortgage_GetChattelMortgage(
            String keyWord
    ){
        ArrayList dataList = new ArrayList();
        MongoCursor<Document> list = mongoService.getCollection("ChattelMortgage_GetChattelMortgage").find(Filters.eq("KeyWordVal", keyWord)).iterator();
        while (list.hasNext()){
            dataList.add(list.next());
        }
        return dataList;
    }

}
