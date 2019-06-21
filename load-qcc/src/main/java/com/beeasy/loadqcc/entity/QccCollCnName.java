package com.beeasy.loadqcc.entity;

import java.util.HashMap;
import java.util.Map;

public class  QccCollCnName {

    private static final Map<String, String> data = new HashMap<>();
    static {
        data.put("ECIInvestment_GetInvestmentList","企业对外投资列表");
        data.put("ChattelMortgage_GetChattelMortgage","动产抵押信息");
        data.put("CIAEmployeeV4_GetStockRelationInfo","企业人员董监高信息");
        data.put("CourtAnnoV4_GetCourtNoticeInfo","开庭公告详情");
        data.put("CourtAnnoV4_SearchCourtNotice","开庭公告列表");
        data.put("CourtNoticeV4_SearchCourtAnnouncement","法院公告列表");
        data.put("CourtNoticeV4_SearchCourtAnnouncementDetail","法院公告详情");
        data.put("CourtV4_SearchShiXin","失信信息");
        data.put("CourtV4_SearchZhiXing","被执行信息");
        data.put("ECICompanyMap_GetStockAnalysisData","企业股权穿透十层接口查询");
        data.put("ECIException_GetOpException","企业经营异常");
        data.put("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap","企业图谱");
        data.put("ECIRelationV4_GetCompanyEquityShareMap","股权结构图");
        data.put("ECIRelationV4_SearchTreeRelationMap","投资图谱");
        data.put("ECIInvestmentThrough_GetInfo", "企业对外投资穿透");
        data.put("ECIV4_GetDetailsByName","企业关键字精确获取详细信息(Master)");
        data.put("ECIV4_SearchFresh","新增公司列表");
        data.put("EnvPunishment_GetEnvPunishmentDetails","环保处罚详情");
        data.put("EnvPunishment_GetEnvPunishmentList","环保处罚列表");
        data.put("History_GetHistoryShiXin","历史失信查询");
        data.put("History_GetHistorytAdminLicens","历史行政许可");
        data.put("History_GetHistorytAdminPenalty","历史行政处罚");
        data.put("History_GetHistorytCourtNotice","历史法院公告");
        data.put("History_GetHistorytEci","历史工商信息");
        data.put("History_GetHistorytInvestment","历史对外投资");
        data.put("History_GetHistorytJudgement","历史裁判文书");
        data.put("History_GetHistorytMPledge","历史动产抵押");
        data.put("History_GetHistorytPledge","历史股权出质");
        data.put("History_GetHistorytSessionNotice","历史开庭公告");
        data.put("History_GetHistorytShareHolder","历史股东");
        data.put("History_GetHistoryZhiXing","历史被执行");
        data.put("HoldingCompany_GetHoldingCompany","控股公司信息");
        data.put("JudgeDocV4_GetJudgementDetail","裁判文书详情");
        data.put("JudgeDocV4_SearchJudgmentDoc","裁判文书列表");
        data.put("JudicialAssistance_GetJudicialAssistance","司法协助信息");
        data.put("JudicialSale_GetJudicialSaleDetail","司法拍卖详情");
        data.put("JudicialSale_GetJudicialSaleList","司法拍卖列表");
        data.put("LandMortgage_GetLandMortgageDetails","土地抵押详情");
        data.put("LandMortgage_GetLandMortgageList","土地抵押列表");
    }
    public static String getValue(String keyName){
        return data.get(keyName);
    }
}
