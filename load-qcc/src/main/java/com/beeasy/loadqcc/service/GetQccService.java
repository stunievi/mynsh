package com.beeasy.loadqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.utils.QccUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
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
        // 法律诉讼服
            // 裁判文书
        DetailUrls.put("JudgeDoc_SearchJudgmentDoc", DOMAIN_PRX + "/JudgeDoc/GetJudgementDetail");
        DetailUrls.put("CourtAnno_SearchCourtNotice", DOMAIN_PRX + "/CourtAnnoV4/GetCourtNoticeInfo");

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
        // 查询裁判文书
        // JudgeDoc_SearchJudgmentDoc(keyWord);
        // 查询开庭公告
        CourtAnno_SearchCourtNotice(keyWord);

        // 历史信息
            // 历史工商信息
        History_GetHistorytEci(keyWord);
            // 历史对外投资
        History_GetHistorytInvestment(keyWord);
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

        object.put("getDataTime", new Date().getTime());
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
                MongoCursor<Document> findList =  coll.find().iterator();
                while (findList.hasNext()){
                    Document item = findList.next();
                    mongoService.deleteById(coll, item.getObjectId("_id").toString());
                }
            }

            for(short i = 0; i < arr.size(); i++){
                JSONObject item = arr.getJSONObject(i);
                item.put("KeyWordVal", queries.get("keyWord"));
                item.put("getDataTime", new Date().getTime());
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

}
