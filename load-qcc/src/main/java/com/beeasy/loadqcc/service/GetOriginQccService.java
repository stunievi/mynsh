package com.beeasy.loadqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.utils.QccUtil;
import com.google.common.base.Joiner;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GetOriginQccService {

    @Autowired
    MongoService mongoService;

    private static Map<String, String> DetailUrls = new HashMap<>();

    private static String QCC_DOMAIN_PRX = "http://api.qichacha.com";

    static {
        // 表名, 详情接口
        /* 法律诉讼服 */
        // 裁判文书
        DetailUrls.put("JudgeDocV4_SearchJudgmentDoc", "JudgeDocV4_GetJudgementDetail");
        // 开庭公告
        DetailUrls.put("CourtAnnoV4_SearchCourtNotice", "CourtAnnoV4_GetCourtNoticeInfo");
        // 查询法院公告
         DetailUrls.put("CourtNoticeV4_SearchCourtAnnouncement", "CourtNoticeV4_SearchCourtAnnouncementDetail");
        /* 经营风险 */
        // 司法拍卖列表
        DetailUrls.put("JudicialSale_GetJudicialSaleList", "JudicialSale_GetJudicialSaleDetail");
        // 土地抵押
        DetailUrls.put("LandMortgage_GetLandMortgageList", "LandMortgage_GetLandMortgageDetails");
        // 环保处罚
        DetailUrls.put("EnvPunishment_GetEnvPunishmentList", "EnvPunishment_GetEnvPunishmentDetails");
    }

    // 基本信息
    public JSONObject loadDataBlock1(
            String keyWord
    ){
        if(null == keyWord || "".equals(keyWord)){
            return new JSONObject();
        }
        // 企业关键字精确获取详细信息(basic)
        // ECI_GetBasicDetailsByName(keyWord);
        // 企业关键字精确获取详细信息(master),基本信息，行业信息，股东信息，主要成员，分支机构，变更信息，联系信息
        JSONObject comInfo = ECI_GetDetailsByName(keyWord, "automatic");
        if(comInfo.size() < 1){
            return new JSONObject();
        }
        List employees = comInfo.getJSONArray("Employees");
        // 控股企业
        HoldingCompany_GetHoldingCompany(keyWord, "automatic");
        // 企业人员董监高信息
        for(short i=0;i<employees.size();i++){
            JSONObject item = (JSONObject) employees.get(i);
            CIAEmployeeV4_GetStockRelationInfo(keyWord, item.getString("Name"), "automatic");
        }
        return comInfo;
    }
    // 法律诉讼
    public void loadDataBlock2(
            String keyWord
    ){
        //查询裁判文书
        JudgeDocV4_SearchJudgmentDoc(keyWord, "automatic");
        //查询开庭公告
        CourtAnnoV4_SearchCourtNotice(keyWord, "automatic");
        //查询法院公告
        CourtNoticeV4_SearchCourtAnnouncement(keyWord, "automatic");
        //失信信息
        CourtV4_SearchShiXin(keyWord,"automatic");
        //失信被执行人信息
        CourtV4_SearchZhiXing(keyWord,"automatic");
        // 获取司法协助信息
        JudicialAssistance_GetJudicialAssistance(keyWord, "automatic");
    }
    // 关联族谱
    public void loadDataBlock3(
        String keyWord,
        String keyNo
    ){
        if(null == keyNo){
            return;
        }
        // 企业图谱
        ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap(keyNo,"automatic");
        // 投资图谱
        ECIRelationV4_SearchTreeRelationMap(keyNo,"automatic");
        // 股权结构图谱
        ECIRelationV4_GetCompanyEquityShareMap(keyNo,"automatic");
        // 企业股权穿透十层接口查询
        ECICompanyMap_GetStockAnalysisData(keyWord,"automatic");
    }
    // 历史信息
    public void loadDataBlock4(
            String keyWord
    ){
        // 历史工商信息
        History_GetHistorytEci(keyWord, "automatic");
        // 历史对外投资
        History_GetHistorytInvestment(keyWord, "automatic");
        // 历史股东
        History_GetHistorytShareHolder(keyWord, "automatic");
        //历史失信查询
        History_GetHistoryShiXin(keyWord, "automatic");
        //历史被执行
        History_GetHistoryZhiXing(keyWord, "automatic");
        //历史法院公告
        History_GetHistorytCourtNotice(keyWord, "automatic");
        //历史裁判文书
        History_GetHistorytJudgement(keyWord, "automatic");
        //历史开庭公告
        History_GetHistorytSessionNotice(keyWord, "automatic");
        //历史动产抵押
        History_GetHistorytMPledge(keyWord, "automatic");
        //历史股权出质
        History_GetHistorytPledge(keyWord, "automatic");
        //历史行政处罚
        History_GetHistorytAdminPenalty(keyWord, "automatic");
        //历史行政许可
        History_GetHistorytAdminLicens(keyWord, "automatic");
    }
    // 经营风险
    public void loadDataBlock5(
            String keyWord,
            String keyNo
    ){
        // 司法拍卖列表
        JudicialSale_GetJudicialSaleList(keyWord, "automatic");
        // 土地抵押
        LandMortgage_GetLandMortgageList(keyWord, "automatic");
        // 获取环保处罚列表
        EnvPunishment_GetEnvPunishmentList(keyWord, "automatic");
        // 获取动产抵押信息
        ChattelMortgage_GetChattelMortgage(keyWord, "automatic");
        // 查询企业经营异常信息
        ECIException_GetOpException(keyNo, "automatic");
    }

    // 下载企查查数据
    public void loadAllData(
            String keyWord
    ){
        JSONObject comInfo = loadDataBlock1(keyWord);
        String companyName = comInfo.getString("Name");
        if(null == companyName || "".equals(companyName)){
            return;
        }
        String keyNo = comInfo.getString("KeyNo");
        loadDataBlock2(keyWord);
        loadDataBlock3(keyWord, keyNo);
        loadDataBlock4(keyWord);
        loadDataBlock5(keyWord, keyNo);
    }

    // 完全原样存入数据
    private void saveOriginData(
            String collName,
            Map<String, ?> queries,
            String data,
            String trigger
    ){
        JSONObject object = JSON.parseObject(data);
        MongoCollection<Document> coll = mongoService.getCollection(collName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(new Date());
        String dataUrl = QCC_DOMAIN_PRX + "/" + collName.replace("_","/");
        String dataQueries = Joiner.on("&").withKeyValueSeparator("=").join(queries);
        String fullLink = dataUrl + "?" + dataQueries;
        Document dataLog = new Document().append("GetDataTime",dateNowStr).append("TriggerMode", trigger).append("CollName", collName).append("Queries", JSON.toJSONString(queries)).append("OriginData", data).append("FullLink",fullLink);
        if(object.containsKey("Status") && object.getString("Status").equals("200")){
            // 命中Log
            MongoCollection<Document> collLog = mongoService.getCollection("Hit_Log");
            collLog.insertOne(dataLog);

            ArrayList filters  = new ArrayList();
            filters.add(Filters.eq("QueryParam.getDataTime", dateNowStr));
            filters.add(Filters.eq("QueryParam.triggerMode", trigger));
            for (Map.Entry<String, ?> entry : queries.entrySet()){
                filters.add(Filters.eq("QueryParam.".concat(entry.getKey()), entry.getValue()));
            }
            // $set可以用来修改一个字段的值，如果这个字段不存在，则创建它
            Document modifiers = new Document();
            modifiers.append("$set", object);
            UpdateOptions opt = new UpdateOptions();
            opt.upsert(true);
            coll.updateOne(Filters.and(filters), modifiers, opt);
        }else{
            // 未命中Log
            MongoCollection<Document> collLog = mongoService.getCollection("Missing_Log");
            collLog.insertOne(dataLog);
        }
    }

    /**
     * 判断是否为最后一页
     */
    private boolean notLastPage(
            String res,
            Integer pageIndex
    ){
        if(null == res || res.equals("")){
            return false;
        }
        JSONObject obj = JSON.parseObject(res);
        JSONObject Paging = obj.getJSONObject("Paging");
        if(null == Paging || "".equals(Paging)){
            return false;
        }
        return pageIndex <= Math.ceil(Paging.getFloat("TotalRecords")/Paging.getFloat(("PageSize"))) && pageIndex < 500;
    }

    // 是否为格式正确的响应数据
    private boolean isCorrectRes(
            String ret
    ){
        if(null == ret || "".equals(ret)){
            return false;
        }
        try{
            JSONObject obj = JSON.parseObject(ret);
            if("200".equals(obj.getString("Status"))){
                return true;
            }
            return  false;
        }catch (Exception e){
            return  false;
        }
    }

    // 获取详情数据
    private void getDetailData(
            String collName,
            Map<String, String> queries,
            String trigger
    ){
        String tableName = collName;
        String dataUrl = QCC_DOMAIN_PRX + "/" + collName.replace("_","/");
        String ret = QccUtil.getData(dataUrl, queries);
        saveOriginData(tableName, queries, ret, trigger);
    }

    // 获取列表数据，有详情数据
    private void getDataList(
            String tableName,
            Map queries,
            String res,
            String trigger
    ){
        String collName = tableName;
        String dataUrl = QCC_DOMAIN_PRX + "/" + tableName.replace("_","/");
        Integer pageIndex = (int) queries.get("pageIndex");
        if (pageIndex.equals(1) || notLastPage(res, pageIndex)){
            String ret = QccUtil.getData(dataUrl, queries);
            saveOriginData(collName, queries, ret, trigger);
            if(isCorrectRes(ret) == false){
                return;
            }
            JSONObject obj = JSON.parseObject(ret);
            JSONArray arr = obj.getJSONArray("Result");
            for(short i = 0; i < arr.size(); i++){
                JSONObject item = arr.getJSONObject(i);
                if (item == null) {
                    continue;
                }
                String id = "";
                if(null != item.getString("Id") && !"".equals(item.getString("Id"))){
                    id = item.getString("Id");
                }else{
                    break;
                }
                // TODO:: 获取详情
                String detailCollName= DetailUrls.get(collName);
                if(null == detailCollName || "".equals(detailCollName)){
                    return;
                }else{
                    Class clazz = this.getClass();
                    Method method = null;
                    try {
                        method = clazz.getDeclaredMethod(detailCollName, String.class, String.class);
                        method.invoke(this, id, trigger);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        System.out.println(e);
                    }
                }
            }
            if (notLastPage(ret, ++pageIndex)){
                queries.put("pageIndex", pageIndex);
                getDataList(collName, queries, ret, trigger);
            }
        }
    }
    // 获取列表数据，无详情数据
    private void getDataList2(
            String collName,
            Map queries,
            String res,
            String trigger
    ){
        String tableName = collName;
        String dataUrl = QCC_DOMAIN_PRX + "/" + collName.replace("_","/");
        Integer pageIndex = (int) queries.get("pageIndex");
        if (pageIndex.equals(1) || notLastPage(res, pageIndex)){
            String ret = QccUtil.getData(dataUrl, queries);
            saveOriginData(tableName, queries, ret, trigger);
            if(isCorrectRes(ret) == false){
                return;
            }
            if (notLastPage(ret, ++pageIndex)){
                queries.put("pageIndex", pageIndex);
                getDataList2(tableName, queries, ret, trigger);
            }
        }
    }

    /**
     *  企业关键字精确获取详细信息（basic）
     * @param keyWord 现定为使用公司名做搜索。但企查查支持搜索关键字（公司名、注册号、社会统一信用代码或KeyNo）
     */
    public JSONObject ECI_GetBasicDetailsByName(
            String keyWord,
            String trigger
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIV4/GetBasicDetailsByName", query);
        saveOriginData("ECIV4_GetBasicDetailsByName", query, res, trigger);
        JSONObject resObj = JSON.parseObject(res).getJSONObject("Result");
        if(null == resObj || resObj.isEmpty()){
            return new JSONObject();
        }
        return resObj;
    }
    /**
     * 企业关键字精确获取详细信息（master）
     * @param keyWord
     * @return
     */
    public JSONObject ECI_GetDetailsByName(
            String keyWord,
            String trigger
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIV4/GetDetailsByName", query);
        saveOriginData("ECIV4_GetDetailsByName", query, res, trigger);
        JSONObject resObj = JSON.parseObject(res);
        if(!"200".equals(resObj.getString("Status"))){
            return new JSONObject();
        }
        JSONObject retObj = resObj.getJSONObject("Result");
        if(null == retObj || retObj.isEmpty()){
            return new JSONObject();
        }
        return retObj;
    }

    // 控股企业
    public void HoldingCompany_GetHoldingCompany(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("HoldingCompany_GetHoldingCompany", queries, "", trigger);
    }

    /**
     * 企业人员董监高信息
     * @param companyName 公司名
     * @param name 高管名
     */
    public void CIAEmployeeV4_GetStockRelationInfo(
        String companyName,
        String name,
        String trigger
    ){
        Map query = C.newMap(
                "companyName", companyName,
                "name", name
        );
        getDetailData("CIAEmployeeV4_GetStockRelationInfo", query, trigger);
    }

    // 历史工商信息
    public   void History_GetHistorytEci(
            String keyWord,
            String trigger
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("History_GetHistorytEci", query, trigger);
    }
    // 历史对外投资
    public void History_GetHistorytInvestment(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistorytInvestment", queries, "", trigger);
    }
    // 历史股东
    public void History_GetHistorytShareHolder(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistorytShareHolder", queries, "", trigger);
    }
    //历史失信查询
    public void History_GetHistoryShiXin(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistoryShiXin", queries,"", trigger);
    }
    //历史被执行
    public void History_GetHistoryZhiXing(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistoryZhiXing", queries, "", trigger);
    }
    //历史法院公告
    public void History_GetHistorytCourtNotice(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytCourtNotice", queries, "", trigger);
    }
    //历史裁判文书
    public void History_GetHistorytJudgement(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytJudgement", queries, "", trigger);
    }
    //历史开庭公告
    public void History_GetHistorytSessionNotice(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytSessionNotice", queries,"", trigger);
    }
    //历史动产抵押
    public void History_GetHistorytMPledge(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("History_GetHistorytMPledge", queries, "", trigger);
    }
    //历史股权出质
    public void History_GetHistorytPledge(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("History_GetHistorytPledge", queries, "", trigger);
    }
    //历史行政处罚
    public void History_GetHistorytAdminPenalty(
            String keyWord,
            String trigger
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("History_GetHistorytAdminPenalty", query, trigger);
    }
    //历史行政许可
    public void History_GetHistorytAdminLicens(
            String keyWord,
            String trigger
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("History_GetHistorytAdminLicens", query, trigger);
    }
    /**
     * 裁判文书
     * @param keyWord
     */
    public void JudgeDocV4_SearchJudgmentDoc(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "searchKey", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("JudgeDocV4_SearchJudgmentDoc", queries, "", trigger);
    }
    // 裁判文书详情
    public void JudgeDocV4_GetJudgementDetail(
        String id,
        String trigger
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("JudgeDocV4_GetJudgementDetail", queries, trigger);
    }
    //  查询开庭公告
    public void CourtAnnoV4_SearchCourtNotice(
            String searchKey,
            String trigger
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList("CourtAnnoV4_SearchCourtNotice", queries, "", trigger);
    }
    // 开庭公告详情
    public void CourtAnnoV4_GetCourtNoticeInfo(
            String id,
            String trigger
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("CourtAnnoV4_GetCourtNoticeInfo", queries, trigger);
    }
    /**
     * 经营风险
     */
    // 司法拍卖列表
    public void JudicialSale_GetJudicialSaleList(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("JudicialSale_GetJudicialSaleList", queries, "", trigger);
    }
    // 拍卖详情
    public void JudicialSale_GetJudicialSaleDetail(
            String id,
            String trigger
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("JudicialSale_GetJudicialSaleDetail", queries, trigger);
    }
    // 土地抵押
    public void LandMortgage_GetLandMortgageList(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("LandMortgage_GetLandMortgageList", queries,"", trigger);
    }
    // 土地抵押详情
    public void LandMortgage_GetLandMortgageDetails(
            String id,
            String trigger
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("LandMortgage_GetLandMortgageDetails", queries, trigger);
    }
    // 获取环保处罚列表
    public void EnvPunishment_GetEnvPunishmentList(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("EnvPunishment_GetEnvPunishmentList", queries,"", trigger);
    }
    // 环保处罚详情
    public void EnvPunishment_GetEnvPunishmentDetails(
            String id,
            String trigger
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("EnvPunishment_GetEnvPunishmentDetails", queries, trigger);
    }

    // 获取动产抵押信息
    public void ChattelMortgage_GetChattelMortgage(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("ChattelMortgage_GetChattelMortgage", queries, trigger);
    }
    // 查询企业经营异常信息
    public void ECIException_GetOpException(
            String keyNo,
            String trigger
    ){
        Map queries = C.newMap(
                "keyNo", keyNo
        );
        getDetailData("ECIException_GetOpException", queries, trigger);
    }
    // 查询法院公告
    public void CourtNoticeV4_SearchCourtAnnouncement(
            String searchKey,
            String trigger
    ){
        Map queries = C.newMap(
                "companyName", searchKey,
                "pageSize", 50,
                "pageIndex", 1
        );
        getDataList("CourtNoticeV4_SearchCourtAnnouncement", queries,"", trigger);
    }
    // 法院公告详情
    public void CourtNoticeV4_SearchCourtAnnouncementDetail(
            String id,
            String trigger
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("CourtNoticeV4_SearchCourtAnnouncementDetail", queries, trigger);
    }
    //失信信息
    public void CourtV4_SearchShiXin(
            String searchKey,
            String trigger
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList2("CourtV4_SearchShiXin", queries,"", trigger);
    }
    //失信被执行人信息
    public void  CourtV4_SearchZhiXing(
            String searchKey,
            String trigger
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList2("CourtV4_SearchZhiXing", queries, "", trigger);
    }
    //获取司法协助信息
    public void JudicialAssistance_GetJudicialAssistance(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("JudicialAssistance_GetJudicialAssistance", queries, trigger);
    }
    // 投资图谱
    public   void ECIRelationV4_SearchTreeRelationMap(
            String keyNo,
            String trigger
    ){
        Map queries = C.newMap(
                "keyNo", keyNo,
                "upstreamCount", 4,
                "downstreamCount", 4
        );
        getDetailData("ECIRelationV4_SearchTreeRelationMap", queries, trigger);
    }
    // 股权结构图
    public void ECIRelationV4_GetCompanyEquityShareMap(
            String keyNo,
            String trigger
    ){
        Map queries = C.newMap(
                "keyNo", keyNo
        );
        getDetailData("ECIRelationV4_GetCompanyEquityShareMap", queries, trigger);
    }
    // 企业图谱
    public void ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap(
            String keyNo,
            String trigger
    ){
        Map queries = C.newMap(
                "keyNo", keyNo
        );
        getDetailData("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap", queries, trigger);
    }
    // 十层股权穿透图
    public void ECICompanyMap_GetStockAnalysisData(
            String keyWord,
            String trigger
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("ECICompanyMap_GetStockAnalysisData", queries, trigger);
    }

}
