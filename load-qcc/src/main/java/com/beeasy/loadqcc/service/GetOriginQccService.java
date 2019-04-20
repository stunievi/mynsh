package com.beeasy.loadqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.entity.LoadQccDataExtParm;
import com.beeasy.loadqcc.utils.QccUtil;
import com.google.common.base.Joiner;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GetOriginQccService {

    // 控制开发模式下某些变量
    private boolean devModel = true;

    @Value("${loadQcc.txtPath}")
    String LOAD_TXT_PATH;

    @Value("${loadQcc.zipPath}")
    String LOAD_ZIP_PATH;

    @PostConstruct
    public void init(){
        LoadQccDataExtParm.LOAD_TXT_PATH = LOAD_TXT_PATH;
        LoadQccDataExtParm.LOAD_ZIP_PATH = LOAD_ZIP_PATH;
    }

    @Autowired
    @Qualifier(value = "qcc1")
    MongoService mongoService;

    @Autowired
    @Qualifier(value = "qcc2")
    MongoService mongoService2;

    private static Map<String, String> DetailUrls = new HashMap<>();

//    private static String QCC_DOMAIN_PRX = "http://api.qichacha.com";
    private static String QCC_DOMAIN_PRX = "http://localhost:8015/test/qcc";

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

    public void saveErrLog(
            String msg
    ){
        MongoCollection<Document> coll2 = mongoService2.getCollection("Error_Log");
        coll2.insertOne(new Document().append("Message", msg));
    }

    // 基本信息
    public JSONObject loadDataBlock1(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        if(null == keyWord || keyWord.isEmpty()){
            saveErrLog("下载【基本信息数据块】时公司名为空");
            return new JSONObject();
        }
        // 企业关键字精确获取详细信息(basic)
        // ECI_GetBasicDetailsByName(keyWord);
        // 企业关键字精确获取详细信息(master),基本信息，行业信息，股东信息，主要成员，分支机构，变更信息，联系信息
        JSONObject comInfo = ECI_GetDetailsByName(keyWord, extParam);
        if(null == comInfo || comInfo.isEmpty()){
            return new JSONObject();
        }
        List employees;
        try {
            employees = comInfo.getJSONArray("Employees");
        }catch (Exception e){
            saveErrLog("ECI_GetDetailsByName获取工商信息解析Employees失败！");
            employees = new JSONArray();
        }
        int employeesLength = employees.size();
        if(devModel){
            employeesLength = 1;
        }
        // 控股企业
        HoldingCompany_GetHoldingCompany(keyWord, extParam);
        // 企业人员董监高信息
        for(short i=0;i<employeesLength;i++){
            JSONObject item = (JSONObject) employees.get(i);
            CIAEmployeeV4_GetStockRelationInfo(keyWord, item.getString("Name"), extParam);
        }
        return comInfo;
    }
    // 法律诉讼
    public void loadDataBlock2(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        if(null == keyWord || keyWord.isEmpty()){
            saveErrLog("下载【法律诉讼数据块】时公司名为空");
            return;
        }
        //查询裁判文书
        JudgeDocV4_SearchJudgmentDoc(keyWord, extParam);
        //查询开庭公告
        CourtAnnoV4_SearchCourtNotice(keyWord, extParam);
        //查询法院公告
        CourtNoticeV4_SearchCourtAnnouncement(keyWord, extParam);
        //失信信息
        CourtV4_SearchShiXin(keyWord,extParam);
        //失信被执行人信息
        CourtV4_SearchZhiXing(keyWord,extParam);
        // 获取司法协助信息
        JudicialAssistance_GetJudicialAssistance(keyWord, extParam);
    }
    // 关联族谱
    public void loadDataBlock3(
        String keyWord,
        String keyNo,
        LoadQccDataExtParm extParam
    ){
        if(null == keyWord || keyWord.isEmpty() || null == keyNo || keyNo.isEmpty()){
            saveErrLog("下载【关联族谱数据块】时公司名或keyNo为空");
            return;
        }
        // 企业图谱
        ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap(keyNo,extParam);
        // 投资图谱
        ECIRelationV4_SearchTreeRelationMap(keyNo,extParam);
        // 股权结构图谱
        ECIRelationV4_GetCompanyEquityShareMap(keyNo,extParam);
        // 企业股权穿透十层接口查询
        ECICompanyMap_GetStockAnalysisData(keyWord,extParam);
    }
    // 历史信息
    public void loadDataBlock4(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        if(null == keyWord || keyWord.isEmpty()){
            saveErrLog("下载【历史信息数据块】时公司名为空");
            return;
        }
        // 历史工商信息
        History_GetHistorytEci(keyWord, extParam);
        // 历史对外投资
        History_GetHistorytInvestment(keyWord, extParam);
        // 历史股东
        History_GetHistorytShareHolder(keyWord, extParam);
        //历史失信查询
        History_GetHistoryShiXin(keyWord, extParam);
        //历史被执行
        History_GetHistoryZhiXing(keyWord, extParam);
        //历史法院公告
        History_GetHistorytCourtNotice(keyWord, extParam);
        //历史裁判文书
        History_GetHistorytJudgement(keyWord, extParam);
        //历史开庭公告
        History_GetHistorytSessionNotice(keyWord, extParam);
        //历史动产抵押
        History_GetHistorytMPledge(keyWord, extParam);
        //历史股权出质
        History_GetHistorytPledge(keyWord, extParam);
        //历史行政处罚
        History_GetHistorytAdminPenalty(keyWord, extParam);
        //历史行政许可
        History_GetHistorytAdminLicens(keyWord, extParam);
    }
    // 经营风险
    public void loadDataBlock5(
            String keyWord,
            String keyNo,
            LoadQccDataExtParm extParam
    ){
        if(null == keyWord || keyWord.isEmpty() || null == keyNo || keyNo.isEmpty()){
            saveErrLog("下载【经营风险数据块】时公司名或keyNo为空");
            return;
        }
        // 司法拍卖列表
        JudicialSale_GetJudicialSaleList(keyWord, extParam);
        // 土地抵押
        LandMortgage_GetLandMortgageList(keyWord, extParam);
        // 获取环保处罚列表
        EnvPunishment_GetEnvPunishmentList(keyWord, extParam);
        // 获取动产抵押信息
        ChattelMortgage_GetChattelMortgage(keyWord, extParam);
        // 查询企业经营异常信息
        ECIException_GetOpException(keyNo, extParam);
    }

    // 下载企查查数据
    public void loadAllData(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        JSONObject comInfo = loadDataBlock1(keyWord, extParam);
        if(null == comInfo || comInfo.size() <1){
            return;
        }
        String keyNo = comInfo.getString("KeyNo");
        loadDataBlock2(keyWord, extParam);
        loadDataBlock3(keyWord, keyNo, extParam);
        loadDataBlock4(keyWord, extParam);
        loadDataBlock5(keyWord, keyNo, extParam);
    }

    // 完全原样存入数据
    private void saveOriginData(
        String collName,
        Map<String, ?> queries,
        String data,
        LoadQccDataExtParm extParam
    ){
        String trigger = extParam.getTrigger();
        JSONObject object = JSON.parseObject(data);

        // 数据全量库
        MongoCollection<Document> coll = mongoService.getCollection(collName);
        // 数据历史版本库
        MongoCollection<Document> coll2 = mongoService2.getCollection(collName);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(new Date());
        String dataUrl = QCC_DOMAIN_PRX + "/" + collName.replace("_","/");
        String dataQueries = Joiner.on("&").withKeyValueSeparator("=").join(queries);
        String fullLink = dataUrl + "?" + dataQueries;

        Document dataLog = new Document().append("GetDataTime",dateNowStr).append("TriggerMode", trigger).append("CollName", collName).append("Queries", JSON.toJSONString(queries)).append("OriginData", data);
        if(queries.containsKey("pageIndex")){
            dataLog.append("PageIndex", queries.get("pageIndex"));
        }
        // 写入文件--全部请求完成后压缩文件提交解构服务解构数据
        FileLock lock = null;
        File file = extParam.getQccFileDataPath();
        try (
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                FileChannel channel = raf.getChannel();
        ){
            lock = channel.lock();
            String str = JSON.toJSONString(dataLog);
            byte[] bs = (str.getBytes(StandardCharsets.UTF_8.name()));
            raf.seek(raf.length());
            byte[] linkbytes = fullLink.getBytes(StandardCharsets.UTF_8);
            raf.writeInt(linkbytes.length);
            raf.write(linkbytes);
            //写入数据包长度
            raf.writeInt(bs.length);
            raf.write(bs);

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        };

        // mongo提交过来的数据库数据不再做处理
        if(object.containsKey("QueryParam")){
            return;
        }

        if(object.containsKey("Status") && object.getString("Status").equals("200")){
            // 命中Log
            MongoCollection<Document> collLog = mongoService2.getCollection("Hit_Log");
            collLog.insertOne(dataLog);

            ArrayList filters  = new ArrayList();
            filters.add(Filters.eq("QueryParam.triggerMode", trigger));
            for (Map.Entry<String, ?> entry : queries.entrySet()){
                filters.add(Filters.eq("QueryParam.".concat(entry.getKey()), entry.getValue()));
            }
            // $set可以用来修改一个字段的值，如果这个字段不存在，则创建它
            // 插入数据库
            object.put("QueryParam.getDataTime", dateNowStr);
            Document modifiers = new Document();
            modifiers.append("$set", object);
            UpdateOptions opt = new UpdateOptions();
            opt.upsert(true);
            coll.updateOne(Filters.and(filters), modifiers, opt);
            // 按时间做版本插入版本库
            filters.add(Filters.eq("QueryParam.getDataTime", dateNowStr));
            coll2.updateOne(Filters.and(filters), modifiers, opt);
        }else{
            // 未命中Log
            MongoCollection<Document> collLog = mongoService2.getCollection("Missing_Log");
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
        if(devModel){
            return pageIndex < 2;
        }else{
            return pageIndex <= Math.ceil(Paging.getFloat("TotalRecords")/Paging.getFloat(("PageSize")));
        }

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

    // 今天数据是否已经下载新数据
    private String haveTodayData(
            String tableName,
            Map<String, ?> queries
    ){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(new Date());
        List filters = new ArrayList();
        for (Map.Entry<String, ?> entry : queries.entrySet()){
            filters.add(Filters.eq("QueryParam.".concat(entry.getKey()), entry.getValue()));
        }
        if(!devModel){
            filters.add(Filters.eq("QueryParam.getDataTime", dateNowStr));
        }
        Document data = mongoService2.getCollection(tableName).find(Filters.and(filters)).first();
        if(null == data || data.isEmpty()){
            return null;
        }else{
            return JSON.toJSONString(JSON.toJSON(data));
        }

    }

    // 获取详情数据
    private void getDetailData(
            String collName,
            Map<String, String> queries,
            LoadQccDataExtParm extParam
    ){
        // 表面
        String tableName = collName;
        // 远程数据地址
        String dataUrl = QCC_DOMAIN_PRX + "/" + collName.replace("_","/");
        String ret = haveTodayData(tableName, queries);
        // 判断当前条件下今天是否已经获取过数据
        if(null == ret){
            ret = QccUtil.getData(dataUrl, queries);
        }
        saveOriginData(tableName, queries, ret, extParam);
    }

    // 获取列表数据，有详情数据
    private void getDataList(
            String tableName,
            Map queries,
            String res,
            LoadQccDataExtParm extParam
    ){
        String collName = tableName;

        String dataUrl = QCC_DOMAIN_PRX + "/" + tableName.replace("_","/");
        Integer pageIndex = (int) queries.get("pageIndex");
        if (pageIndex.equals(1) || notLastPage(res, pageIndex)){
            String ret = haveTodayData(tableName, queries);
            // 判断当前条件下今天是否已经获取过数据
            if(null == ret){
                ret = QccUtil.getData(dataUrl, queries);
            }
            saveOriginData(collName, queries, ret, extParam);
            // ！！！！注意放到save后面
            if(isCorrectRes(ret) == false && devModel == false){
                return;
            }
            // 详情表名
            String detailCollName= DetailUrls.get(collName);
            if(null != detailCollName && !"".equals(detailCollName)){
                JSONObject obj = JSON.parseObject(ret);
                JSONArray arr;
                try{
                    arr = obj.getJSONArray("Result");
                }catch (Exception e){
                    if(devModel){
                        arr = new JSONArray();
                        arr.add(C.newMap(
                                "Id", "testId"
                        ));
                    }else {
                        return;
                    }
                }
                int arrLength = arr.size();
                // 获取详情
                if(devModel){
                    arrLength = 1;
                }
                for(short i = 0; i < arrLength; i++){
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
                    Class clazz = this.getClass();
                    Method method = null;
                    try {
                        method = clazz.getDeclaredMethod(detailCollName, String.class, LoadQccDataExtParm.class);
                        method.invoke(this, id, extParam);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        System.out.println(e);
                    }
                }
            }
            if (notLastPage(ret, ++pageIndex)){
                queries.put("pageIndex", pageIndex);
                getDataList(collName, queries, ret, extParam);
            }
        }
    }
    // 获取列表数据，无详情数据
    private void getDataList2(
            String collName,
            Map queries,
            String res,
            LoadQccDataExtParm extParam
    ){
        getDataList(collName, queries, res, extParam);
    }

    /**
     *  企业关键字精确获取详细信息（basic）
     * @param keyWord 现定为使用公司名做搜索。但企查查支持搜索关键字（公司名、注册号、社会统一信用代码或KeyNo）
     */
    public JSONObject ECI_GetBasicDetailsByName(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        String res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIV4/GetBasicDetailsByName", query);
        saveOriginData("ECIV4_GetBasicDetailsByName", query, res, extParam);
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
            LoadQccDataExtParm extParam
    ){
        // 企查查为 keyword !!!
        Map query = C.newMap(
                "keyword", keyWord
        );
        String collName = "ECIV4_GetDetailsByName";
        String res = haveTodayData(collName, query);
        if(null == res){
            res = QccUtil.getData(QCC_DOMAIN_PRX + "/ECIV4/GetDetailsByName", query);
        }
        saveOriginData(collName, query, res, extParam);
        JSONObject resObj = JSON.parseObject(res);
        if(!"200".equals(resObj.getString("Status"))){
            saveErrLog("工商信息获取失败，请确定【"+keyWord+"】公司存在");
            return new JSONObject();
        }
        JSONObject retObj = resObj.getJSONObject("Result");
        return retObj;
    }

    // 控股企业
    public void HoldingCompany_GetHoldingCompany(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("HoldingCompany_GetHoldingCompany", queries, "", extParam);
    }

    /**
     * 企业人员董监高信息
     * @param companyName 公司名
     * @param name 高管名
     */
    public void CIAEmployeeV4_GetStockRelationInfo(
        String companyName,
        String name,
        LoadQccDataExtParm extParam
    ){
        Map query = C.newMap(
                "companyName", companyName,
                "name", name
        );
        getDetailData("CIAEmployeeV4_GetStockRelationInfo", query, extParam);
    }

    // 历史工商信息
    public   void History_GetHistorytEci(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("History_GetHistorytEci", query, extParam);
    }
    // 历史对外投资
    public void History_GetHistorytInvestment(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistorytInvestment", queries, "", extParam);
    }
    // 历史股东
    public void History_GetHistorytShareHolder(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistorytShareHolder", queries, "", extParam);
    }
    //历史失信查询
    public void History_GetHistoryShiXin(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistoryShiXin", queries,"", extParam);
    }
    //历史被执行
    public void History_GetHistoryZhiXing(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList2("History_GetHistoryZhiXing", queries, "", extParam);
    }
    //历史法院公告
    public void History_GetHistorytCourtNotice(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytCourtNotice", queries, "", extParam);
    }
    //历史裁判文书
    public void History_GetHistorytJudgement(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytJudgement", queries, "", extParam);
    }
    //历史开庭公告
    public void History_GetHistorytSessionNotice(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("History_GetHistorytSessionNotice", queries,"", extParam);
    }
    //历史动产抵押
    public void History_GetHistorytMPledge(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("History_GetHistorytMPledge", queries, "", extParam);
    }
    //历史股权出质
    public void History_GetHistorytPledge(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 20
        );
        getDataList2("History_GetHistorytPledge", queries, "", extParam);
    }
    //历史行政处罚
    public void History_GetHistorytAdminPenalty(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("History_GetHistorytAdminPenalty", query, extParam);
    }
    //历史行政许可
    public void History_GetHistorytAdminLicens(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map query = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("History_GetHistorytAdminLicens", query, extParam);
    }
    /**
     * 裁判文书
     * @param keyWord
     */
    public void JudgeDocV4_SearchJudgmentDoc(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "searchKey", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("JudgeDocV4_SearchJudgmentDoc", queries, "", extParam);
    }
    // 裁判文书详情
    public void JudgeDocV4_GetJudgementDetail(
        String id,
        LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("JudgeDocV4_GetJudgementDetail", queries, extParam);
    }
    //  查询开庭公告
    public void CourtAnnoV4_SearchCourtNotice(
            String searchKey,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList("CourtAnnoV4_SearchCourtNotice", queries, "", extParam);
    }
    // 开庭公告详情
    public void CourtAnnoV4_GetCourtNoticeInfo(
            String id,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("CourtAnnoV4_GetCourtNoticeInfo", queries, extParam);
    }
    /**
     * 经营风险
     */
    // 司法拍卖列表
    public void JudicialSale_GetJudicialSaleList(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("JudicialSale_GetJudicialSaleList", queries, "", extParam);
    }
    // 拍卖详情
    public void JudicialSale_GetJudicialSaleDetail(
            String id,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("JudicialSale_GetJudicialSaleDetail", queries, extParam);
    }
    // 土地抵押
    public void LandMortgage_GetLandMortgageList(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("LandMortgage_GetLandMortgageList", queries,"", extParam);
    }
    // 土地抵押详情
    public void LandMortgage_GetLandMortgageDetails(
            String id,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("LandMortgage_GetLandMortgageDetails", queries, extParam);
    }
    // 获取环保处罚列表
    public void EnvPunishment_GetEnvPunishmentList(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord,
                "pageIndex", 1,
                "pageSize", 50
        );
        getDataList("EnvPunishment_GetEnvPunishmentList", queries,"", extParam);
    }
    // 环保处罚详情
    public void EnvPunishment_GetEnvPunishmentDetails(
            String id,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("EnvPunishment_GetEnvPunishmentDetails", queries, extParam);
    }

    // 获取动产抵押信息
    public void ChattelMortgage_GetChattelMortgage(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("ChattelMortgage_GetChattelMortgage", queries, extParam);
    }
    // 查询企业经营异常信息
    public void ECIException_GetOpException(
            String keyNo,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyNo", keyNo
        );
        getDetailData("ECIException_GetOpException", queries, extParam);
    }
    // 查询法院公告
    public void CourtNoticeV4_SearchCourtAnnouncement(
            String searchKey,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "companyName", searchKey,
                "pageSize", 50,
                "pageIndex", 1
        );
        getDataList("CourtNoticeV4_SearchCourtAnnouncement", queries,"", extParam);
    }
    // 法院公告详情
    public void CourtNoticeV4_SearchCourtAnnouncementDetail(
            String id,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "id", id
        );
        getDetailData("CourtNoticeV4_SearchCourtAnnouncementDetail", queries, extParam);
    }
    //失信信息
    public void CourtV4_SearchShiXin(
            String searchKey,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList2("CourtV4_SearchShiXin", queries,"", extParam);
    }
    //失信被执行人信息
    public void  CourtV4_SearchZhiXing(
            String searchKey,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "searchKey", searchKey,
                "pageSize", 50,
                "pageIndex", 1,
                "isExactlySame", true
        );
        getDataList2("CourtV4_SearchZhiXing", queries, "", extParam);
    }
    //获取司法协助信息
    public void JudicialAssistance_GetJudicialAssistance(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("JudicialAssistance_GetJudicialAssistance", queries, extParam);
    }
    // 投资图谱
    public   void ECIRelationV4_SearchTreeRelationMap(
            String keyNo,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyNo", keyNo,
                "upstreamCount", 4,
                "downstreamCount", 4
        );
        getDetailData("ECIRelationV4_SearchTreeRelationMap", queries, extParam);
    }
    // 股权结构图
    public void ECIRelationV4_GetCompanyEquityShareMap(
            String keyNo,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyNo", keyNo
        );
        getDetailData("ECIRelationV4_GetCompanyEquityShareMap", queries, extParam);
    }
    // 企业图谱
    public void ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap(
            String keyNo,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyNo", keyNo
        );
        getDetailData("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap", queries, extParam);
    }
    // 十层股权穿透图
    public void ECICompanyMap_GetStockAnalysisData(
            String keyWord,
            LoadQccDataExtParm extParam
    ){
        Map queries = C.newMap(
                "keyWord", keyWord
        );
        getDetailData("ECICompanyMap_GetStockAnalysisData", queries, extParam);
    }

}
