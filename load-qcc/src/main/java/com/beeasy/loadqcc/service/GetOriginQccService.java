package com.beeasy.loadqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.utils.QccUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import netscape.javascript.JSObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GetOriginQccService {

    @Autowired
    MongoService mongoService;

    private static Map<String, String> DetailUrls = new HashMap<>();

    private static String QCC_DOMAIN_PRX = "http://localhost:8015/test/qcc";

    static {
        // 表名, 详情接口
        /* 法律诉讼服 */
        // 裁判文书
        DetailUrls.put("JudgeDocV4_SearchJudgmentDoc", QCC_DOMAIN_PRX + "/JudgeDocV4/GetJudgementDetail");
        // 开庭公告
        DetailUrls.put("CourtAnnoV4_SearchCourtNotice", QCC_DOMAIN_PRX + "/CourtAnnoV4/GetCourtNoticeInfo");
        // 查询法院公告
         DetailUrls.put("CourtNoticeV4_SearchCourtAnnouncement", QCC_DOMAIN_PRX + "/CourtNoticeV4/SearchCourtAnnouncementDetail");
        /* 经营风险 */
        // 司法拍卖列表
        DetailUrls.put("JudicialSale_GetJudicialSaleList", QCC_DOMAIN_PRX + "/JudicialSale/GetJudicialSaleDetail");
        // 土地抵押
        DetailUrls.put("LandMortgage_GetLandMortgageList", QCC_DOMAIN_PRX + "/LandMortgage/GetLandMortgageDetails");
        // 环保处罚
        DetailUrls.put("EnvPunishment_GetEnvPunishmentList", QCC_DOMAIN_PRX + "/EnvPunishment/GetEnvPunishmentDetails");
    }

    // 下载企查查数据
    public void loadAllData(
            String keyWord
    ){
        if(null == keyWord || "".equals(keyWord)){
            return;
        }
        // 企业关键字精确获取详细信息(basic)
        // ECI_GetBasicDetailsByName(keyWord);
        // 企业关键字精确获取详细信息(master),基本信息，行业信息，股东信息，主要成员，分支机构，变更信息，联系信息
        JSONObject comInfo = ECI_GetDetailsByName(keyWord);
        if(comInfo.size() < 1){
            return;
        }
        String companyName = comInfo.getString("Name");
        String keyNo = comInfo.getString("KeyNo");
        List employees = comInfo.getJSONArray("Employees");
        // 控股企业
        HoldingCompany_GetHoldingCompany(keyWord);
        // 企业人员董监高信息
        for(short i=0;i<employees.size();i++){
            JSONObject item = (JSONObject) employees.get(i);
            CIAEmployeeV4_GetStockRelationInfo(keyWord, item.getString("Name"));
        }

        /* 法律诉讼  */
        //查询裁判文书
        JudgeDocV4_SearchJudgmentDoc(keyWord);
        //查询开庭公告
        CourtAnnoV4_SearchCourtNotice(keyWord);
        //查询法院公告
        CourtNoticeV4_SearchCourtAnnouncement(keyWord);
        //失信信息
        CourtV4_SearchShiXin(keyWord);
        //失信被执行人信息
        CourtV4_SearchZhiXing(keyWord);
        // 获取司法协助信息
        JudicialAssistance_GetJudicialAssistance(keyWord);

        // 关联族谱
        // 企业图谱
        ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap(keyNo);
        // 投资图谱
        ECIRelationV4_SearchTreeRelationMap(keyNo);
        // 股权结构图谱
        ECIRelationV4_GetCompanyEquityShareMap(keyNo);

        // 历史信息
        // 历史工商信息
        History_GetHistorytEci(keyWord);
        // 历史对外投资
        History_GetHistorytInvestment(keyWord);
        // 历史股东
        History_GetHistorytShareHolder(keyWord);
        //历史失信查询
        History_GetHistoryShiXin(keyWord);
        //历史被执行
        History_GetHistoryZhiXing(keyWord);
        //历史法院公告
        History_GetHistorytCourtNotice(keyWord);
        //历史裁判文书
        History_GetHistorytJudgement(keyWord);
        //历史开庭公告
        History_GetHistorytSessionNotice(keyWord);
        //历史动产抵押
        History_GetHistorytMPledge(keyWord);
        //历史股权出质
        History_GetHistorytPledge(keyWord);
        //历史行政处罚
        History_GetHistorytAdminPenalty(keyWord);
        //历史行政许可
        History_GetHistorytAdminLicens(keyWord);

        // 经营风险
        // 司法拍卖列表
        JudicialSale_GetJudicialSaleList(keyWord);
        // 土地抵押
        LandMortgage_GetLandMortgageList(keyWord);
        // 获取环保处罚列表
        EnvPunishment_GetEnvPunishmentList(keyWord);
        // 获取动产抵押信息
        ChattelMortgage_GetChattelMortgage(keyWord);

    }

    // 完全原样存入数据
    private void saveOriginData(
            String collName,
            Map<String, ?> queries,
            String data
    ){
        JSONObject object = JSON.parseObject(data);
        MongoCollection<Document> coll = mongoService.getCollection(collName);
        if(object.containsKey("Status") && object.getString("Status").equals("200")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateNowStr = sdf.format(new Date());
            ArrayList filters  = new ArrayList();
            filters.add(Filters.eq("QueryParam.getDataTime", dateNowStr));
            for (Map.Entry<String, ?> entry : queries.entrySet()){
                filters.add(Filters.eq("QueryParam.".concat(entry.getKey()), entry.getValue()));
            }
            // $set可以用来修改一个字段的值，如果这个字段不存在，则创建它
            Document modifiers = new Document();
            modifiers.append("$set", object);
            UpdateOptions opt = new UpdateOptions();
            opt.upsert(true);
            coll.updateOne(Filters.and(filters), modifiers, opt);
        }
    }

    /**
     *
     * @param res
     * @param pageIndex
     * @return
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

    // 获取列表数据中某条的详情
    private void getDataDetail(
            String collName,
            Map<String, String> queries,
            String dataUrl
    ){
        String ret = QccUtil.getData(dataUrl, queries);
        if(isCorrectRes(ret) == false){
            return;
        }
        saveOriginData(collName, queries, ret);
    }

    // 获取列表数据，有详情数据
    private void getDataList(
            String collName,
            Map queries,
            String dataUrl,
            String res
    ){
        Integer pageIndex = (int) queries.get("pageIndex");
        if (pageIndex.equals(1) || notLastPage(res, pageIndex)){
            String ret = QccUtil.getData(dataUrl, queries);
            if(isCorrectRes(ret) == false){
                return;
            }
            saveOriginData(collName, queries, ret);
            JSONObject obj = JSON.parseObject(ret);
            JSONArray arr = obj.getJSONArray("Result");
            for(short i = 0; i < arr.size(); i++){
                JSONObject item = arr.getJSONObject(i);
                if (item == null) {
                    continue;
                }
                Map param = new HashMap();
                if(null != item.getString("Id") && !"".equals(item.getString("Id"))){
                    param.put("id", item.getString("Id"));
                }else if(null != item.getString("KeyNo") && !"".equals(item.getString("KeyNo"))){
                    param.put("keyNo", item.getString("KeyNo"));
                }
                if(param.size() < 1){
                    break;
                }
                // TODO:: 获取详情
                String detailDataUrl = DetailUrls.get(collName);
                if(null == detailDataUrl || "".equals(detailDataUrl)){
                    return;
                }else{
                    String pattern = "qcc/(\\S+)";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(detailDataUrl);
                    if (m.find()) {
                        collName = m.group(1).replace("/","_");
                    }
                    getDataDetail(collName, param, detailDataUrl);
                }
            }
            if (notLastPage(ret, ++pageIndex)){
                queries.put("pageIndex", pageIndex);
                getDataList(collName, queries, dataUrl, ret);
            }
        }
    }
    // 获取列表数据，无详情数据
    private void getDataList2(
            String collName,
            Map queries,
            String dataUrl,
            String res
    ){
        Integer pageIndex = (int) queries.get("pageIndex");
        if (pageIndex.equals(1) || notLastPage(res, pageIndex)){
            String ret = QccUtil.getData(dataUrl, queries);
            if(isCorrectRes(ret) == false){
                return;
            }
            saveOriginData(collName, queries, ret);
            if (notLastPage(ret, ++pageIndex)){
                queries.put("pageIndex", pageIndex);
                getDataList2(collName, queries, dataUrl, ret);
            }
        }
    }

    /**
     *  企业关键字精确获取详细信息（basic）
     * @param keyWord 现定为使用公司名做搜索。但企查查支持搜索关键字（公司名、注册号、社会统一信用代码或KeyNo）
     */
    public JSONObject ECI_GetBasicDetailsByName(
            String keyWord
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIV4/GetBasicDetailsByName", query);
        saveOriginData("ECIV4_GetBasicDetailsByName", query, res);
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
            String keyWord
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIV4/GetDetailsByName", query);
        saveOriginData("ECIV4_GetDetailsByName", query, res);
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
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("HoldingCompany_GetHoldingCompany", queries,QCC_DOMAIN_PRX + "/HoldingCompany/GetHoldingCompany", "");
    }

    /**
     * 企业人员董监高信息
     * @param companyName 公司名
     * @param name 高管名
     */
    public void CIAEmployeeV4_GetStockRelationInfo(
            String companyName,
            String name
    ){
        Map query = C.newMap(
                "companyName", companyName,
                "name", name
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/CIAEmployeeV4/GetStockRelationInfo", query);
        saveOriginData("CIAEmployeeV4_GetStockRelationInfo", query, res);
    }

    // 历史工商信息
    public   void History_GetHistorytEci(
            String keyWord
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/History/GetHistorytEci", query);
        saveOriginData("History_GetHistorytEci", query, res);
    }
    // 历史对外投资
    public void History_GetHistorytInvestment(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistorytInvestment", queries,QCC_DOMAIN_PRX + "/History/GetHistorytInvestment", "");
    }
    // 历史股东
    public void History_GetHistorytShareHolder(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistorytShareHolder", queries,QCC_DOMAIN_PRX + "/History/GetHistorytShareHolder", "");
    }
    //历史失信查询
    public void History_GetHistoryShiXin(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistoryShiXin", queries,QCC_DOMAIN_PRX + "/History/GetHistoryShiXin", "");
    }
    //历史被执行
    public void History_GetHistoryZhiXing(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistoryZhiXing", queries,QCC_DOMAIN_PRX + "/History/GetHistoryZhiXing", "");
    }
    //历史法院公告
    public void History_GetHistorytCourtNotice(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytCourtNotice", queries,QCC_DOMAIN_PRX + "/History/GetHistorytCourtNotice", "");
    }
    //历史裁判文书
    public void History_GetHistorytJudgement(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytJudgement", queries,QCC_DOMAIN_PRX + "/History/GetHistorytJudgement", "");
    }
    //历史开庭公告
    public void History_GetHistorytSessionNotice(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytSessionNotice", queries,QCC_DOMAIN_PRX + "/History/GetHistorytSessionNotice", "");
    }
    //历史动产抵押
    public void History_GetHistorytMPledge(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("History_GetHistorytMPledge", queries,QCC_DOMAIN_PRX + "/History/GetHistorytMPledge", "");
    }
    //历史股权出质
    public void History_GetHistorytPledge(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("History_GetHistorytPledge", queries,QCC_DOMAIN_PRX + "/History/GetHistorytPledge", "");
    }
    //历史行政处罚
    public void History_GetHistorytAdminPenalty(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/History/GetHistorytAdminPenalty", queries);
        saveOriginData("History_GetHistorytAdminPenalty", queries, res);
    }
    //历史行政许可
    public void History_GetHistorytAdminLicens(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/History/GetHistorytAdminLicens", queries);
        saveOriginData("History_GetHistorytAdminLicens", queries, res);
    }
    /**
     * 裁判文书
     * @param keyWord
     */
    public void JudgeDocV4_SearchJudgmentDoc(
            String keyWord
    ){
        Map queries = C.newMap(
                "searchKey", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("JudgeDocV4_SearchJudgmentDoc", queries,QCC_DOMAIN_PRX + "/JudgeDocV4/SearchJudgmentDoc", "");
    }

    //  查询开庭公告
    public void CourtAnnoV4_SearchCourtNotice(
            String searchKey
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList("CourtAnnoV4_SearchCourtNotice", queries,QCC_DOMAIN_PRX + "/CourtAnnoV4/SearchCourtNotice", "");
    }

    /**
     * 经营风险
     */
    // 司法拍卖列表
    public void JudicialSale_GetJudicialSaleList(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("JudicialSale_GetJudicialSaleList", queries,QCC_DOMAIN_PRX + "/JudicialSale/GetJudicialSaleList", "");
    }
    // 土地抵押
    public void LandMortgage_GetLandMortgageList(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("LandMortgage_GetLandMortgageList", queries,QCC_DOMAIN_PRX + "/LandMortgage/GetLandMortgageList", "");
    }
    // 获取环保处罚列表
    public void EnvPunishment_GetEnvPunishmentList(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("EnvPunishment_GetEnvPunishmentList", queries,QCC_DOMAIN_PRX + "/EnvPunishment/GetEnvPunishmentList", "");
    }

    // 获取动产抵押信息
    public void ChattelMortgage_GetChattelMortgage(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/ChattelMortgage/GetChattelMortgage", queries);
        saveOriginData("ChattelMortgage_GetChattelMortgage", queries, res);
    }

    // 查询法院公告
    public void CourtNoticeV4_SearchCourtAnnouncement(
            String searchKey
    ){
        Map queries = C.newMap(
                "companyName", searchKey,
                "pageSize", 50,
                "pageIndex", 1
        );
        getDataList("CourtNoticeV4_SearchCourtAnnouncement", queries,QCC_DOMAIN_PRX + "/CourtNoticeV4/SearchCourtAnnouncement", "");
    }
    //失信信息
    public void CourtV4_SearchShiXin(
            String searchKey
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList("CourtV4_SearchShiXin", queries,QCC_DOMAIN_PRX + "/CourtV4/SearchShiXin", "");
    }
    //失信被执行人信息
    public void  CourtV4_SearchZhiXing(
            String searchKey
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList("CourtV4_SearchZhiXing", queries,QCC_DOMAIN_PRX + "/CourtV4/SearchZhiXing", "");
    }
    //获取司法协助信息
    public void JudicialAssistance_GetJudicialAssistance(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/JudicialAssistance/GetJudicialAssistance", queries);
        saveOriginData("JudicialAssistance_GetJudicialAssistance", queries, res);
    }
    // 投资图谱
    public   void ECIRelationV4_SearchTreeRelationMap(
            String keyNo
    ){
        Map queries = C.newMap(
                "keyNo", keyNo,
                "upstreamCount", 4,
                "downstreamCount", 4
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIRelationV4/SearchTreeRelationMap", queries);
        saveOriginData("ECIRelationV4_SearchTreeRelationMap", queries, res);
    }
    // 股权结构图
    public void ECIRelationV4_GetCompanyEquityShareMap(
            String keyNo
    ){
        Map queries = C.newMap(
                "keyNo", keyNo
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIRelationV4/GetCompanyEquityShareMap", queries);
        saveOriginData("ECIRelationV4_GetCompanyEquityShareMap", queries, res);
    }
    // 企业图谱
    public void ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap(
            String keyNo
    ){
        Map queries = C.newMap(
                "keyNo", keyNo
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIRelationV4/GenerateMultiDimensionalTreeCompanyMap", queries);
        saveOriginData("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap", queries, res);
    }

}
