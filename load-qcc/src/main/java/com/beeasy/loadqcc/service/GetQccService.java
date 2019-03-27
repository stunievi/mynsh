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
import java.util.*;

@Service
public class GetQccService {

    @Autowired
    MongoService mongoService;

    private static Map<String, String> DetailUrls = new HashMap<>();

    private static String DOMAIN_PRX = "http://localhost:8015/test/qcc";

    static {
        // tableName, detailUrl
        /* 法律诉讼服 */
        // 裁判文书
        DetailUrls.put("JudgeDoc_SearchJudgmentDoc", DOMAIN_PRX + "/JudgeDoc/GetJudgementDetail");
        // 开庭公告
        DetailUrls.put("CourtAnno_SearchCourtNotice", DOMAIN_PRX + "/CourtAnnoV4/GetCourtNoticeInfo");
        // 查询法院公告        //返回id没有写，先注释掉
        // DetailUrls.put("CourtNoticeV4_SearchCourtAnnouncement", DOMAIN_PRX + "/CourtNoticeV4/SearchCourtAnnouncementDetail");

        /* 经营风险 */
        // 司法拍卖列表
        DetailUrls.put("JudicialSale_GetJudicialSaleList", DOMAIN_PRX + "/JudicialSale/GetJudicialSaleDetail");
        // 土地抵押
        DetailUrls.put("LandMortgage_GetLandMortgageList", DOMAIN_PRX + "/LandMortgage/GetLandMortgageDetails");
        // 环保处罚
        DetailUrls.put("EnvPunishment_GetEnvPunishmentList", DOMAIN_PRX + "/EnvPunishment/GetEnvPunishmentDetails");

        
    }

    // 下载企查查数据
    public void loadAllData(
            String keyWord
    ){
        if(null == keyWord || "".equals(keyWord)){
            return;
        }
        // 企业关键字精确获取详细信息
        // ECI_GetBasicDetailsByName(keyWord);

        /* 法律诉讼  */
        //查询裁判文书
        JudgeDoc_SearchJudgmentDoc(keyWord);
        //查询开庭公告
        CourtAnno_SearchCourtNotice(keyWord);
        //查询法院公告
        CourtNoticeV4_SearchCourtAnnouncement(keyWord);
        //失信信息
        CourtV4_SearchShiXin(keyWord);
        //失信被执行人信息
        CourtV4_SearchZhiXing(keyWord);
        // 获取司法协助信息
        JudicialAssistance_GetJudicialAssistance(keyWord);

//        // 历史信息
//        // 历史工商信息
//        History_GetHistorytEci(keyWord);
//        // 历史对外投资
//        History_GetHistorytInvestment(keyWord);
//        //历史失信查询
//        History_GetHistoryShiXin(keyWord);
//        //历史被执行
//        History_GetHistoryZhiXing(keyWord);
//        //历史法院公告
//        History_GetHistorytCourtNotice(keyWord);
//        //历史裁判文书
//        History_GetHistorytJudgement(keyWord);
//        //历史开庭公告
//        History_GetHistorytSessionNotice(keyWord);
//        //历史动产抵押
//        History_GetHistorytMPledge(keyWord);
//        //历史股权出质
//        History_GetHistorytPledge(keyWord);
//        //历史行政处罚
//        History_GetHistorytAdminPenalty(keyWord);
//        //历史行政许可
//        History_GetHistorytAdminLicens(keyWord);

//        // 司法拍卖列表
//        JudicialSale_GetJudicialSaleList(keyWord);
//        // 土地抵押
//        LandMortgage_GetLandMortgageList(keyWord);
//        // 获取环保处罚列表
//        EnvPunishment_GetEnvPunishmentList(keyWord);
//        // 获取动产抵押信息
//        ChattelMortgage_GetChattelMortgage(keyWord);

    }

    // 数据原样保存至mongoDB
    private void saveData(
            String collName,
            Bson filter,
            String data
    ){
        JSONObject object = JSON.parseObject(data);
        MongoCollection<Document> coll = mongoService.getCollection(collName);

        if(null != object.getJSONObject("Result") && object.getJSONObject("Result").isEmpty() == false){
            object = object.getJSONObject("Result");
        }

        object.put("GetDataTime", new Date().getTime());
        // $set可以用来修改一个字段的值，如果这个字段不存在，则创建它
        Document modifiers = new Document();
        modifiers.append("$set", object);
        //
        UpdateOptions opt = new UpdateOptions();
        opt.upsert(true);

        coll.updateOne(filter, modifiers, opt);
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
    public boolean isCorrectRes(
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
    public void getDataDetail(
            String collName,
            Map<String, String> queries,
            String dataUrl,
            Bson filter
    ){
        String ret = QccUtil.getData(dataUrl, queries);
        if(isCorrectRes(ret) == false){
            return;
        }
        JSONObject obj = JSON.parseObject(ret);
        saveData(collName, filter, JSON.toJSONString(obj.getJSONObject("Result")));
    }

    // 获取列表数据（先查找数据，有则更新，无则插入）
    public void getDataList(
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
            JSONObject obj = JSON.parseObject(ret);
            JSONArray arr = obj.getJSONArray("Result");

            // 判断是否含有旧数据 - begin
            List<String> idList = new ArrayList<String>();
            String IdType = "";
            for(short i = 0; i < arr.size(); i++){
                JSONObject item = arr.getJSONObject(i);
                if(null != item.getString("Id") && !"".equals(item.getString("Id"))){
                    IdType = "Id";
                    idList.add(item.getString("Id"));
                }else{
                    IdType = "KeyNo";
                    idList.add(item.getString("KeyNo"));
                }
            }
            Bson filter2;
            if(IdType == "KeyNo"){
                filter2 = Filters.in("KeyNo", idList);
            }else{
                filter2 = Filters.in("Id", idList);
            }
            boolean isFindDoc = false;
            if(mongoService.getCollection(collName).countDocuments(filter2) > 0){
                isFindDoc = true;
            };
            // 判断是否含有旧数据 - end

            for(short i = 0; i < arr.size(); i++){
                JSONObject item = arr.getJSONObject(i);
                if (item == null) {
                    continue;
                }
                Bson filter = null;
                Map param = new HashMap();
                if(null != item.getString("Id") && !"".equals(item.getString("Id"))){
                    filter = Filters.regex("Id", item.getString("Id"));
                    param.put("id", item.getString("Id"));
                }else if(null != item.getString("KeyNo") && !"".equals(item.getString("KeyNo"))){
                    filter = Filters.regex("KeyNo", item.getString("KeyNo"));
                    param.put("keyNo", item.getString("KeyNo"));
                }
                if(null == filter){
                    break;
                }
                if(null != queries.get("keyWord") && !"".equals(queries.get("keyWord"))){
                    item.put("KeyWordVal", queries.get("keyWord"));
                }
                saveData(collName, filter, JSON.toJSONString(item));
                // TODO:: 获取详情
                String detailDataUrl = DetailUrls.get(collName);
                if(null == detailDataUrl || "".equals(detailDataUrl)){
                    return;
                }else{
                    getDataDetail(collName, param, detailDataUrl, filter);
                }
            }
            if (isFindDoc == false && notLastPage(ret, ++pageIndex)){
                queries.put("pageIndex", pageIndex);
                getDataList(collName, queries, dataUrl, ret);
            }
        }
    }

    // 获取列表数据（删除历史数据后插入新数据）
    public void getDataList2(
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
            JSONObject obj = JSON.parseObject(ret);
            MongoCollection<Document> coll = mongoService.getCollection(collName);
            JSONArray arr = obj.getJSONArray("Result");
            // 删除历史数据
            if(pageIndex.equals(1)){
                MongoCursor<Document> findList =  coll.find(Filters.eq("KeyWordVal", queries.get("keyWord"))).iterator();
                while (findList.hasNext()){
                    Document item = findList.next();
                    mongoService.deleteById(coll, item.getObjectId("_id").toString());
                }
            }
            for(short i = 0; i < arr.size(); i++){
                JSONObject item = arr.getJSONObject(i);
                item.put("KeyWordVal", queries.get("keyWord"));
                item.put("GetDataTime", new Date().getTime());
                coll.insertOne(new Document(item));
            }

            if (notLastPage(ret, ++pageIndex)){
                queries.put("pageIndex", pageIndex);
                getDataList2(collName, queries, dataUrl, ret);
            }
        }
    }

    /**
     *  企业关键字精确获取详细信息
     * @param keyWord 现定为使用公司名做搜索。但企查查支持搜索关键字（公司名、注册号、社会统一信用代码或KeyNo）
     */
    public JSONObject ECI_GetBasicDetailsByName(
            String keyWord
    ){
        String res = QccUtil.getData(DOMAIN_PRX + "/ECI/GetBasicDetailsByName", C.newMap(
                "keyWord", keyWord
        ));
        Bson filter = Filters.eq("Name", keyWord);
        saveData("ECI_GetBasicDetailsByName", filter, res);
        return JSON.parseObject(res);
    }

    // 历史工商信息
    private  void History_GetHistorytEci(
            String keyWord
    ){
        String res = QccUtil.getData(DOMAIN_PRX + "/History/GetHistorytEci", C.newMap(
                "keyWord", keyWord
        ));
        Bson filter = Filters.eq("KeyWordVal", keyWord);
        saveData("History_GetHistorytEci", filter, res);
    }
    // 历史对外投资
    private void History_GetHistorytInvestment(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistorytInvestment", queries,DOMAIN_PRX + "/History/GetHistorytInvestment", "");
    }
    //历史失信查询
    private void History_GetHistoryShiXin(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistoryShiXin", queries,DOMAIN_PRX + "/History/GetHistoryShiXin", "");
    }
    //历史被执行
    private void History_GetHistoryZhiXing(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistoryZhiXing", queries,DOMAIN_PRX + "/History/GetHistoryZhiXing", "");
    }
    //历史法院公告
    private void History_GetHistorytCourtNotice(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytCourtNotice", queries,DOMAIN_PRX + "/History/GetHistorytCourtNotice", "");
    }
    //历史裁判文书
    private void History_GetHistorytJudgement(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytJudgement", queries,DOMAIN_PRX + "/History/GetHistorytJudgement", "");
    }
    //历史开庭公告
    private void History_GetHistorytSessionNotice(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytSessionNotice", queries,DOMAIN_PRX + "/History/GetHistorytSessionNotice", "");
    }
    //历史动产抵押
    private void History_GetHistorytMPledge(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("History_GetHistorytMPledge", queries,DOMAIN_PRX + "/History/GetHistorytMPledge", "");
    }
    //历史股权出质
    private void History_GetHistorytPledge(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("History_GetHistorytPledge", queries,DOMAIN_PRX + "/History/GetHistorytPledge", "");
    }
    //历史行政处罚
    private void History_GetHistorytAdminPenalty(
            String keyWord
    ){
        String res = QccUtil.getData(DOMAIN_PRX + "/History/GetHistorytAdminPenalty", C.newMap(
                "keyWord", keyWord
        ));
        Bson filter = Filters.eq("KeyWordVal", keyWord);
        saveData("History_GetHistorytAdminPenalty", filter, res);
    }
    //历史行政许可
    private void History_GetHistorytAdminLicens(
            String keyWord
    ){
        String res = QccUtil.getData(DOMAIN_PRX + "/History/GetHistorytAdminLicens", C.newMap(
                "keyWord", keyWord
        ));
        Bson filter = Filters.eq("KeyWordVal", keyWord);
        saveData("History_GetHistorytAdminLicens", filter, res);
    }
    /**
     * 裁判文书
     * @param keyWord
     */
    public void JudgeDoc_SearchJudgmentDoc(
            String keyWord
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1
        );
        getDataList("JudgeDoc_SearchJudgmentDoc", queries,DOMAIN_PRX + "/JudgeDoc/SearchJudgmentDoc", "");
    }

    //  查询开庭公告
    public void CourtAnno_SearchCourtNotice(
            String searchKey
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList("CourtAnno_SearchCourtNotice", queries,DOMAIN_PRX + "/CourtAnnoV4/SearchCourtNotice", "");
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
        getDataList("JudicialSale_GetJudicialSaleList", queries,DOMAIN_PRX + "/JudicialSale/GetJudicialSaleList", "");
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
        getDataList("LandMortgage_GetLandMortgageList", queries,DOMAIN_PRX + "/LandMortgage/GetLandMortgageList", "");
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
        getDataList("EnvPunishment_GetEnvPunishmentList", queries,DOMAIN_PRX + "/EnvPunishment/GetEnvPunishmentList", "");
    }

    // 获取动产抵押信息
    public void ChattelMortgage_GetChattelMortgage(
            String keyWord
    ){
        MongoCollection<Document> coll = mongoService.getCollection("ChattelMortgage_GetChattelMortgage");
        MongoCursor<Document> findList =  coll.find(Filters.eq("KeyWordVal", keyWord)).iterator();
        while (findList.hasNext()){
            Document item = findList.next();
            mongoService.deleteById(coll, item.getObjectId("_id").toString());
        }
        String res = QccUtil.getData(DOMAIN_PRX + "/ChattelMortgage/GetChattelMortgage", C.newMap(
                "keyWord", keyWord
        ));
        if(isCorrectRes(res)){
            JSONObject obj = JSON.parseObject(res);
            JSONArray dataList = obj.getJSONArray("Result");
            for(short i=0;i<dataList.size();i++){
                JSONObject item = dataList.getJSONObject(i);
                item.put("KeyWordVal", keyWord);
                item.put("GetDataTime", new Date().getTime());
                coll.insertOne(new Document(item));
            }
        }
    }

    // 查询法院公告
    private void CourtNoticeV4_SearchCourtAnnouncement(
            String searchKey
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1
        );
        getDataList("CourtNoticeV4_SearchCourtAnnouncement", queries,DOMAIN_PRX + "/CourtNoticeV4/SearchCourtAnnouncement", "");
    }
    //失信信息
    private void CourtV4_SearchShiXin(
            String searchKey
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList("CourtV4_SearchShiXin", queries,DOMAIN_PRX + "/CourtV4/SearchShiXin", "");
    }
    //失信被执行人信息
    private void  CourtV4_SearchZhiXing(
            String searchKey
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList("CourtV4_SearchZhiXing", queries,DOMAIN_PRX + "/CourtV4/SearchZhiXing", "");
    }
    //获取司法协助信息
    private void JudicialAssistance_GetJudicialAssistance(
            String keyWord
    ){
        MongoCollection<Document> coll = mongoService.getCollection("JudicialAssistance_GetJudicialAssistance");
        MongoCursor<Document> findList =  coll.find(Filters.eq("KeyWordVal", keyWord)).iterator();
        while (findList.hasNext()){
            Document item = findList.next();
            mongoService.deleteById(coll, item.getObjectId("_id").toString());
        }
        String res = QccUtil.getData(DOMAIN_PRX + "/JudicialAssistance/GetJudicialAssistance", C.newMap(
                "keyWord", keyWord
        ));
        if(isCorrectRes(res)){
            JSONObject obj = JSON.parseObject(res);
            JSONArray dataList = obj.getJSONArray("Result");
            for(short i=0;i<dataList.size();i++){
                JSONObject item = dataList.getJSONObject(i);
                item.put("KeyWordVal", keyWord);
                item.put("GetDataTime", new Date().getTime());
                coll.insertOne(new Document(item));
            }
        }
    }



}
