package com.beeasy.hzqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QccService {

    @Autowired
    QccDataService qccDataService;
    @Autowired
    QccHttpDataService qccHttpDataService;

    public static QccService dynamicProxy;
    @PostConstruct
    public void init() {
        dynamicProxy = this;
    }

    // 返回详情
    private JSONObject getResDetails(
            JSONObject ret,
            boolean isOrigin
    ){
        if(null == ret){
            return new JSONObject();
        }
        if(isOrigin){
            return  ret;
        }
        return ret.getJSONObject("Result");
    }
    // 返回数组
    private JSONObject getResDataArr(
            JSONObject ret,
            boolean isOrigin
    ){
        JSONObject retData = new JSONObject();
        if(null == ret){
            return new JSONObject();
        }
        if(isOrigin){
            return ret;
        }
        JSONArray dataList = ret.getJSONArray("Result");
        if(null == dataList || dataList.size() < 1){
            return new JSONObject();
        }
        retData.put("list", dataList);
        return retData;
    }
    // 返回列表
    private JSONObject getResDataList(
            JSONObject ret,
            boolean isOrigin
    ){
        JSONObject retData = new JSONObject();
        if(isOrigin){
            return ret;
        }
        if(null == ret){
            return retData;
        }
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
    private JSONObject getResSplitChild(
            JSONObject resData,
            String splitChildName,
            boolean isOrigin
    ){
        if(isOrigin){
            return resData;
        }
        if(null == resData || resData.isEmpty() || !"200".equals(resData.getString("Status"))){
            return new JSONObject();
        }
        JSONObject pageObj = resData.getJSONObject("Paging");
        JSONObject resultObj = resData.getJSONObject("Result");
        JSONObject retData = new JSONObject();
        retData.put("list",resultObj.getJSONArray(splitChildName));
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
            pageSize = paramObj.getInteger("pageSize");
            if(null == pageSize || "".equals(pageSize) || pageSize<1){
                pageSize = 10;
            }
        }
        if(null == pageNumber || "".equals(pageNumber) || pageNumber<1){
            pageNumber = paramObj.getInteger("pageIndex");
            if(null == pageNumber || "".equals(pageNumber) || pageNumber<1){
                pageNumber = 1;
            }
        }
        param.remove("page");
        param.put("pageIndex", pageNumber);
        param.remove("size");
        param.put("pageSize", pageSize);
        return param;
    }
    // 企业关键字精确获取详细信息(Master)
    public Map ECIV4_GetDetailsByName(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.findOne("ECIV4_GetDetailsByName", param);
        return getResDetails(data,isOrigin);
    }
    // 企业关键字精确获取详细信息(Basic)
    public Map ECI_GetBasicDetailsByName(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.findOne("ECIV4_GetBasicDetailsByName", param);
        return getResDetails(data,isOrigin);
    }
    // 企业人员董监高信息
    public Map CIAEmployeeV4_GetStockRelationInfo(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.findOne("CIAEmployeeV4_GetStockRelationInfo", param);
        return getResDetails(data,isOrigin);
    }
    // 获取裁判文书
    public Map JudgeDocV4_SearchJudgmentDoc(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.getDataList("JudgeDocV4_SearchJudgmentDoc", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 裁判文书详情
    public Map JudgeDocV4_GetJudgementDetail(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.findOne("JudgeDocV4_GetJudgementDetail", param);
        return getResDetails(data,isOrigin);
    }
    // 开庭公告列表
    public Map CourtAnnoV4_SearchCourtNotice(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.getDataList("CourtAnnoV4_SearchCourtNotice", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 开庭公告详情
    public JSONObject CourtAnnoV4_GetCourtNoticeInfo(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.findOne("CourtAnnoV4_GetCourtNoticeInfo", param);
        return getResDetails(data,isOrigin);
    }
    // 失信列表
    public Map CourtV4_SearchShiXin(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.getDataList("CourtV4_SearchShiXin", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 执行列表
    public Map CourtV4_SearchZhiXing(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.getDataList("CourtV4_SearchZhiXing", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 法院公告列表信息
    public Map CourtNoticeV4_SearchCourtAnnouncement(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.getDataList("CourtNoticeV4_SearchCourtAnnouncement", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 法院公告详情
    public Map CourtNoticeV4_SearchCourtAnnouncementDetail(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.findOne("CourtNoticeV4_SearchCourtAnnouncementDetail", param);
        return getResDetails(data,isOrigin);
    }
    // 获取司法协助信息eDoc/GetJudgementDetail.html
    public JSONObject JudicialAssistance_GetJudicialAssistance(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.findOne("JudicialAssistance_GetJudicialAssistance", param);
         return getResDataArr(data, isOrigin);
    }

    // 历史工商信息
    public JSONObject History_GetHistorytEci(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.findOne("History_GetHistorytEci", param);
        return getResDetails(data,isOrigin);
    }
    // 历史股东
    public Map History_GetHistorytShareHolder(
            Map param,
            boolean isOrigin
    ){
        JSONObject resData = qccHttpDataService.getSplitChildDataList("History_GetHistorytShareHolder",fixResPageParam(param));
        return getResSplitChild(resData, "Details", isOrigin);
    }
    // 历史对外投资
    public Map History_GetHistorytInvestment(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytInvestment", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 历史失信
    public Map History_GetHistoryShiXin(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistoryShiXin", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 历史执行
    public Map History_GetHistoryZhiXing(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistoryZhiXing", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 历史法院公告
    public Map History_GetHistorytCourtNotice(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytCourtNotice", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 历史法院公告
    public Map History_GetHistorytJudgement(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytJudgement", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 历史开庭公告
    public Map History_GetHistorytSessionNotice(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytSessionNotice", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 历史动产抵押
    public Map History_GetHistorytMPledge(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytMPledge", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 历史股权出质
    public Map History_GetHistorytPledge(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.getDataList("History_GetHistorytPledge", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 历史行政处罚
    public Map History_GetHistorytAdminPenalty(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.findOne("History_GetHistorytAdminPenalty", param);
        return getResDetails(data,isOrigin);
    }
    // 历史行政许可
    public Map History_GetHistorytAdminLicens(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccDataService.findOne("History_GetHistorytAdminLicens", param);
        return getResDetails(data,isOrigin);
    }
    // 获取环保处罚列表
    public Map EnvPunishment_GetEnvPunishmentList(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.getDataList("EnvPunishment_GetEnvPunishmentList", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 环保处罚详情
    public JSONObject EnvPunishment_GetEnvPunishmentDetails(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.findOne("EnvPunishment_GetEnvPunishmentDetails", param);
        return getResDetails(data,isOrigin);
    }
    // 获取土地抵押列表
    public Map LandMortgage_GetLandMortgageList(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.getDataList("LandMortgage_GetLandMortgageList", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 土地抵押详情
    public JSONObject LandMortgage_GetLandMortgageDetails(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.findOne("LandMortgage_GetLandMortgageDetails", param);
        return getResDetails(data,isOrigin);
    }
    // 司法拍卖列表
    public Map JudicialSale_GetJudicialSaleList(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.getDataList("JudicialSale_GetJudicialSaleList", fixResPageParam(param));
        return getResDataList(data, isOrigin);
    }
    // 司法拍卖详情
    public JSONObject JudicialSale_GetJudicialSaleDetail(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.findOne("JudicialSale_GetJudicialSaleDetail", param);
        return getResDetails(data,isOrigin);
    }
    // 动产抵押
    public JSONObject ChattelMortgage_GetChattelMortgage(
            Map param,
            boolean isOrigin
    ){
        JSONObject data = qccHttpDataService.findOne("ChattelMortgage_GetChattelMortgage", param);
        return getResDataArr(data, isOrigin);
    }

    // 股权结构图
    public Map ECIRelationV4_GetCompanyEquityShareMap(
            Map param,
            boolean isOrigin
    ){
        JSONObject tupuInfo = qccDataService.findOne("ECIRelationV4_GetCompanyEquityShareMap", param);
        return getResDetails(tupuInfo, isOrigin);
    }
    // 十层股权穿透图
    public Map ECICompanyMap_GetStockAnalysisData(
            Map param,
            boolean isOrigin
    ){
        JSONObject tupuInfo = qccDataService.findOne("ECICompanyMap_GetStockAnalysisData", param);
        return getResDetails(tupuInfo, isOrigin);
    }

    // 企业图谱
    public Map ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap(
        Map param,
        boolean isOrigin
    ){
        JSONObject comInfo = qccDataService.findOne("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap", param);
        if(null == comInfo){
            return new HashMap();
        }
        if(isOrigin){
            return comInfo;
        }
        comInfo = comInfo.getJSONObject("Result");
        if(null == comInfo){
            return new HashMap();
        }
        String comInfoStr = JSON.toJSONString(comInfo);
        comInfoStr = comInfoStr.replaceAll("name", "Name");
        comInfo = JSON.parseObject(comInfoStr);
        comInfo = comInfo.getJSONObject("Node");
        Map retData = new HashMap();
        JSONArray childs = comInfo.getJSONArray("Children");
        retData.put("Name", comInfo.getString("Name"));
        retData.put("KeyNo", comInfo.getString("KeyNo"));
        retData.put("Employees", new ArrayList<>()); // 无相关数据
        for(short i=0;i<childs.size();i++){
            Map child = (Map) childs.get(i);
            List temp_child = new ArrayList();
            if(child.get("Category").equals(2)){
                // 对外投资
                retData.put("EquityShareDetail", child.get("Children"));
            }else if(child.get("Category").equals(3)){
                // 股东
                ArrayList temp_list = new ArrayList();
                for(short j=0;j<((JSONArray) child.get("Children")).size();j++){
                    Map item = (Map) ((JSONArray) child.get("Children")).get(j);
                    item.put("StockName", item.get("Name"));
                    temp_list.add(item);
                }
                retData.put("partners", child.get("Children"));
            }else if(child.get("Category").equals(4)){
                // 高管
                retData.put("Employees", child.get("Children"));
            }else if(child.get("Category").equals(9)){
                // 历史法人
                retData.put("HistoryOpers", child.get("Children"));
            }
        }
        return retData;
    }
    // 获取控股公司信息
    public Map HoldingCompany_GetHoldingCompany(
            Map param,
            boolean isOrigin
    ){
        JSONObject resData = qccDataService.getSplitChildDataList("HoldingCompany_GetHoldingCompany", fixResPageParam(param));
        return getResSplitChild(resData, "Names", isOrigin);
    }

}
