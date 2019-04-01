package com.beeasy.hzqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bson.conversions.Bson;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QccService {

    @Autowired
    MongoService mongoService;
    @Autowired
    QccDataService qccDataService;

    public static QccService dynamicProxy;
    @PostConstruct
    public void init() {
        dynamicProxy = this;
    }

    // 返回详情
    private JSONObject getResDetails(
            JSONObject ret
    ){
        if(null == ret){
            return new JSONObject();
        }
        return ret.getJSONObject("Result");
    }
    // 返回数组
    private JSONArray getResDataArr(
            JSONObject ret
    ){
        if(null == ret){
            return new JSONArray();
        }
        JSONArray dataList = ret.getJSONArray("Result");
        if(null == dataList || dataList.size() < 1){
            return new JSONArray();
        }
        return dataList;
    }
    // 返回列表
    private JSONObject getResDataList(
            JSONObject ret
    ){
        JSONObject retData = new JSONObject();
        retData.put("list", ret.getJSONArray("Result"));

        JSONObject pageObj = ret.getJSONObject("Paging");
        if(null == pageObj){
            return  retData;
        }
        Integer totalRow = pageObj.getInteger("TotalRecords");
        Integer pageSize = pageObj.getInteger("PageSize");
        retData.put("pageSize", pageObj.getInteger("PageSize"));
        retData.put("pageNumber", pageObj.getInteger("PageIndex"));
        retData.put("totalRow", totalRow);
        retData.put("totalPage", (int) Math.ceil((float) totalRow / (float) pageSize));
        return retData;
    }
    // 修正分页参数
    private Map fixResPageParam(
            Map param
    ){
        JSONObject paramObj = (JSONObject) JSON.toJSON(param);
        Integer pageSize = paramObj.getInteger("size");
        Integer pageNumber = paramObj.getInteger("page");
        if(null == pageSize || "".equals(pageSize) || pageSize<1){
            pageSize = 10;
        }
        if(null == pageNumber || "".equals(pageNumber) || pageNumber<1){
            pageNumber = 1;
        }
        param.remove("page");
        param.put("pageIndex", pageNumber);
        param.remove("size");
        param.put("pageSize", pageSize);
        return param;
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

    // 企业关键字精确获取详细信息(Master)
    public Map ECI_GetDetailsByName(
            Map param
    ){
        JSONObject data = qccDataService.findOne("ECIV4_GetDetailsByName", param);
        return getResDetails(data);
    }
    // 企业关键字精确获取详细信息(Basic)
    public Map ECI_GetBasicDetailsByName(
            String companyName
    ){
        JSONObject data = qccDataService.findOne("ECIV4_GetBasicDetailsByName", C.newMap("keyWord", companyName));
        return getResDetails(data);
    }

    // 获取裁判文书
    public Map JudgeDoc_SearchJudgmentDoc(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("JudgeDocV4_SearchJudgmentDoc", fixResPageParam(param));
        return getResDataList(data);
    }
    // 裁判文书详情
    public Map JudgeDoc_GetJudgementDetail(
            String Id
    ){
        JSONObject data = qccDataService.findOneById("JudgeDocV4_GetJudgementDetail", Id);
        return getResDetails(data);
    }
    // 开庭公告列表
    public Map CourtAnno_SearchCourtNotice(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("CourtAnnoV4_SearchCourtNotice", fixResPageParam(param));
        return getResDataList(data);
    }
    // 开庭公告详情
    public JSONObject CourtAnno_GetCourtNoticeInfo(
            String Id
    ){
        JSONObject data = qccDataService.findOneById("CourtAnnoV4_GetCourtNoticeInfo", Id);
        return getResDetails(data);
    }
    // 失信列表
    public Map Court_SearchShiXin(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("CourtV4_SearchShiXin", fixResPageParam(param));
        return getResDataList(data);
    }
    // 执行列表
    public Map Court_SearchZhiXing(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("CourtV4_SearchZhiXing", fixResPageParam(param));
        return getResDataList(data);
    }
    // 法院公告列表信息
    public Map CourtNotice_SearchCourtAnnouncement(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("CourtNoticeV4_SearchCourtAnnouncement", fixResPageParam(param));
        return getResDataList(data);
    }
    // 法院公告详情
    public Map CourtNotice_SearchCourtAnnouncementDetail(
            String id
    ){
        JSONObject data = qccDataService.findOneById("CourtNoticeV4_SearchCourtAnnouncementDetail", id);
        return getResDetails(data);
    }
    public List JudicialAssistance_GetJudicialAssistance(
            Map param
    ){
        JSONObject data = qccDataService.findOne("JudicialAssistance_GetJudicialAssistance", param);
         return getResDataArr(data);
    }

    // 历史工商信息
    public JSONObject History_GetHistorytEci(
            Map param
    ){
        JSONObject data = qccDataService.findOne("History_GetHistorytEci", param);
        return getResDetails(data);
    }

    // 历史对外投资
    public Map History_GetHistorytInvestment(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytInvestment", fixResPageParam(param));
        return getResDataList(data);
    }
     // 历史股东
    public Map History_GetHistorytShareHolder(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytShareHolder", fixResPageParam(param));
        return getResDataList(data);
    }
    // 历史失信
    public Map History_GetHistoryShiXin(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistoryShiXin", fixResPageParam(param));
        return getResDataList(data);
    }
    // 历史执行
    public Map History_GetHistoryZhiXing(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistoryZhiXing", fixResPageParam(param));
        return getResDataList(data);
    }
    // 历史法院公告
    public Map History_GetHistorytCourtNotice(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytCourtNotice", fixResPageParam(param));
        return getResDataList(data);
    }
    // 历史法院公告
    public Map History_GetHistorytJudgement(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytJudgement", fixResPageParam(param));
        return getResDataList(data);
    }
    // 历史开庭公告
    public Map History_GetHistorytSessionNotice(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytSessionNotice", fixResPageParam(param));
        return getResDataList(data);
    }
    // 历史动产抵押
    public Map History_GetHistorytMPledge(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytMPledge", fixResPageParam(param));
        return getResDataList(data);
    }
    // 历史股权出质
    public Map History_GetHistorytPledge(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytPledge", fixResPageParam(param));
        return getResDataList(data);
    }
    // 历史行政处罚
    public Map History_GetHistorytAdminPenalty(
            Map param
    ){
        JSONObject data = qccDataService.findOne("History_GetHistorytAdminPenalty", param);
        return getResDetails(data);
    }
    // 历史行政许可
    public Map History_GetHistorytAdminLicens(
            Map param
    ){
        JSONObject data = qccDataService.findOne("History_GetHistorytAdminLicens", param);
        return getResDetails(data);
    }
    // 获取环保处罚列表
    public Map EnvPunishment_GetEnvPunishmentList(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("EnvPunishment_GetEnvPunishmentList", fixResPageParam(param));
        return getResDataList(data);
    }
    // 环保处罚详情
    public JSONObject EnvPunishment_GetEnvPunishmentDetails(
            String id
    ){
        JSONObject data = qccDataService.findOneById("EnvPunishment_GetEnvPunishmentDetails", id);
        return getResDetails(data);
    }
    // 获取土地抵押列表
    public Map LandMortgage_GetLandMortgageList(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("LandMortgage_GetLandMortgageList", fixResPageParam(param));
        return getResDataList(data);
    }
    // 土地抵押详情
    public JSONObject LandMortgage_GetLandMortgageDetails(
            String id
    ){
        JSONObject data = qccDataService.findOneById("LandMortgage_GetLandMortgageDetails", id);
        return getResDetails(data);
    }
    // 司法拍卖列表
    public Map JudicialSale_GetJudicialSaleList(
            Map param
    ){
        JSONObject data = qccDataService.getDataList("JudicialSale_GetJudicialSaleList", fixResPageParam(param));
        return getResDataList(data);
    }
    // 司法拍卖详情
    public JSONObject JudicialSale_GetJudicialSaleDetail(
            String id
    ){
        JSONObject data = qccDataService.findOneById("JudicialSale_GetJudicialSaleDetail", id);
        return getResDetails(data);
    }
    // 动产抵押
    public List ChattelMortgage_GetChattelMortgage(
            String keyWord
    ){
        JSONObject data = qccDataService.findOne("ChattelMortgage_GetChattelMortgage", C.newMap("keyWord", keyWord));
        return getResDataArr(data);
    }
    // 企业图谱
    public Map ECIRelation_GenerateMultiDimensionalTreeCompanyMap(
        String keyNo
    ){
        JSONObject data = qccDataService.findOne("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap", C.newMap("keyNo", keyNo));
        return getResDetails(data);
    }
    // 获取控股公司信息
    public Map HoldingCompany_GetHoldingCompany(
            Map param
    ){
        JSONObject resData = qccDataService.HoldingCompany_GetHoldingCompany(fixResPageParam(param));
        JSONObject pageObj = resData.getJSONObject("Paging");
        JSONObject resultObj = resData.getJSONObject("Result");
        Map retData = new HashMap();
        retData.put("list",resultObj.getJSONArray("Names"));
        Integer totalRow = pageObj.getInteger("TotalRecords");
        Integer pageSize = pageObj.getInteger("PageSize");
        retData.put("pageSize", pageObj.getInteger("PageSize"));
        retData.put("pageNumber", pageObj.getInteger("PageIndex"));
        retData.put("totalRow", totalRow);
        retData.put("totalPage", (int) Math.ceil((float) totalRow / (float) pageSize));
        return retData;
    }
}
