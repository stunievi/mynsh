package com.beeasy.zed;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.activemq.BlobMessage;
import org.beetl.sql.core.DSTransactionManager;
import org.beetl.sql.core.SQLReady;
import org.osgl.util.S;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.beeasy.zed.DBService.*;
import static com.beeasy.zed.Utils.*;

//import cn.hutool.json.*;

public class DeconstructService {

    //日志存储
    private File logSourceDir;
    private File logSqlDir;
    private File logUnzipDir;
    private ReentrantLock mutex = new ReentrantLock();
    private ExecutorService executor = Executors.newFixedThreadPool(16);
    private Map<String, Boolean> runningTask = new HashMap<>();

    private ObjectId objectId = ObjectId.get();
    //    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//    private static SimpleDateFormat isoSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//    private static SimpleDateFormat iso2Sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    private static SimpleDateFormat ymdSdf = new SimpleDateFormat("yyyy-MM-dd");
    private static String[] dateFormats = {"yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss"};

    private static ValueGenerator.Wrap DateValue = ValueGenerator.createDate();
    private static ValueGenerator.Wrap Base64Value = ValueGenerator.createBase64();

    public static Map<String, DeconstructHandler> handlers = new HashMap<>();

    public static JSONObject HistorytEciMap = newJsonObject(
        "CompanyNameList", "QCC_HIS_ECI_COMPANY_NAME_LIST",
        "OperList", "QCC_HIS_ECI_OPER_LIST",
        "RegistCapiList", "QCC_HIS_ECI_REGIST_CAPI_LIST",
        "AddressList", "QCC_HIS_ECI_ADDRESS_LIST",
        "ScopeList", "QCC_HIS_ECI_SCOPE_LIST",
        "EmployeeList", "QCC_HIS_ECI_EMPLOYEE_LIST",
//        "EmployeeList.Employees@inner_id->el_inner_id", "QCC_HIS_ECI_EMPLOYEE_LIST.QCC_HIS_ECI_EMPLOYEE_LIST_JOB",
        "BranchList", "QCC_HIS_ECI_BRANCH_LIST",
        "TelList", "QCC_HIS_ECI_TEL_LIST",
        "EmailList", "QCC_HIS_ECI_EMAIL_LIST",
        "WebsiteList", "QCC_HIS_ECI_WEBSITE_LIST"
    );

    public static JSONObject GetStockRelationInfoMap = newJsonObject(
        "CIACompanyLegals", "QCC_CIA_COMPANY_LEGALS",
        "CIAForeignInvestments", "QCC_CIA_FOREIGN_INVESTMENTS",
        "CIAForeignOffices", "QCC_CIA_FOREIGN_OFFICES"
    );

    public boolean autoCommit = true;
    public SqlVectors readySqls = new SqlVectors();

    public DeconstructService() {
    }


    public static void registerHandler(String url, DeconstructHandler handler) {
        handlers.put(url, handler);
    }

    public Object doNettyRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            if (!request.method().equals(HttpMethod.POST)) {
                return null;
            }
            String jsonstr = request.content().toString(CharsetUtil.UTF_8);
            JSONObject json = JSON.parseObject(jsonstr);
            if (!isSuccess(json)) {
                return null;
            }
            DSTransactionManager.start();
            String url = URLUtil.getPath(request.headers().getAsString("Proxy-Url"));
            for (Map.Entry<String, DeconstructHandler> entry : handlers.entrySet()) {
                if (HttpServerHandler.matches(url, entry.getKey())) {
                    Object handler = entry.getValue();
                    entry.getValue().call(ctx, request, (JSON) json.get("Result"));
                    break;
                }
            }

//            return null;
//            if (matches(url, "SearchShiXin")) {
//                deSearchShiXin(ctx, request, json.getJSONArray("Result"));
////                deconstruct(json.getJSONArray("Result"), "QCC_SHIXIN", "Id");
//            } else if (matches(url, "SearchZhiXing")) {
//                deSearchZhiXing(ctx, request, json.getJSONArray("Result"));
//            } else if (url.contains("SearchJudgmentDoc")) {
//                deconstruct(json.getJSONArray("Result"), "QCC_JUDGMENT_DOC", "Id");
//            } else if (url.contains("GetJudgementDetail")) {
//                deconstruct(json.getJSONObject("Result"), "QCC_JUDGMENT_DOC", "Id");
//            } else if (matches(url, "SearchCourtAnnouncement")) {
//                deconstruct(json.getJSONArray("Result"), "QCC_COURT_ANNOUNCEMENT", "Id");
//            } else if (matches(url, "SearchCourtAnnouncementDetail")) {
//                deSearchCourtAnnouncementDetail(ctx, request, json);
//            } else if (matches(url, "SearchCourtNotice")) {
//                deSearchCourtNotice(ctx, request, json.getJSONArray("Result"));
//            } else if (matches(url, "GetCourtNoticeInfo")) {
//                deGetCourtNoticeInfo(ctx, request, json.getJSONObject("Result"));
//            } else if (matches(url, "GetJudicialAssistance")) {
//                deGetJudicialAssistance(ctx, request, json.getJSONArray("Result"));
//            } else if (matches(url, "GetOpException")) {
//                deGetOpException(ctx, request, json.getJSONArray("Result"));
//            } else if (matches(url, "GetJudicialSaleList")) {
//                deGetJudicialSaleList(ctx, request, json.getJSONArray("Result"));
//            } else if (matches(url, "GetJudicialSaleDetail")) {
//                deGetJudicialSaleDetail(ctx, request, json.getJSONObject("Result"));
//            } else if (matches(url, "GetLandMortgageList")) {
//                deGetLandMortgageList(ctx, request, json.getJSONArray("Result"));
//            } else if (matches(url, "GetLandMortgageDetails")) {
//                deGetLandMortgageDetails(ctx, request, json.getJSONObject("Result"));
//            }


            DSTransactionManager.commit();
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            try {
                DSTransactionManager.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException();
        }

//        if(!request.method().equals(HttpMethod.POST)){
//            return null;
//        }
//        if(request.content().isReadable()){
//            String json = request.content().toString(CharsetUtil.UTF_8);
//            return select(JSON.parseObject(json));
//        }
//        return null;
    }


    /**
     * 字段修正
     *
     * @param json
     * @param changeList
     */
    private static void changeField(Object... objects) {
//        if (objects.length == 3 && objects[0] instanceof JSON && objects[1] instanceof ICanChange && objects[2] instanceof ITargetValue) {
//            JSON json = (JSON) objects[0];
//            ICanChange canChange = (ICanChange) objects[1];
//            ITargetValue targetValue = (ITargetValue) objects[2];
//            if (json instanceof JSONArray) {
//                for (Object object : ((JSONArray) json)) {
//                    changeField(object, canChange, targetValue);
//                }
//            } else if (json instanceof JSONObject) {
//                for (Map.Entry<String, Object> entry : ((JSONObject) json).entrySet()) {
//                    if (canChange.call(entry.getKey())) {
//                        changeField(entry, "+" + entry.getKey(), targetValue.call());
//                    }
//                }
//            }
//
//            return;
//        }
        if (objects.length < 1) {
            return;
        }
        if (objects[0] == null) {
            return;
        }
        LinkedList changeList = new LinkedList(Arrays.asList(objects));
        Object json = changeList.removeFirst();
        if (json instanceof JSONArray) {
            int idex = 0;
            for (Object o : ((JSONArray) json)) {
                if (idex++ == 0) {
                    changeList.addFirst(o);
                } else {
                    changeList.set(0, o);
                }
                changeField(changeList.stream().toArray());
            }
        } else if (json instanceof JSONObject) {
            for (short i = 0; i < changeList.size(); i++) {
                Object o = changeList.get(i);
//                if (!(o instanceof String)) {
//                    continue;
//                }
                if (o instanceof ICanChange) {
                    Object value = changeList.get(++i);
                    for (Map.Entry<String, Object> entry : ((JSONObject) json).entrySet()) {
                        if (((ICanChange) o).call(entry.getKey())) {
                            if (value instanceof ITargetValue) {
                                entry.setValue(((ITargetValue) value).call());
                            } else if (value instanceof ValueGenerator.Wrap) {
                                entry.setValue(((ValueGenerator.Wrap) value).call((JSONObject) json, entry.getKey()));
                            } else {
                                entry.setValue(value);
                            }
                        }
                    }
                } else if (o instanceof String) {
                    String changeType = (String) o;
                    if ((changeType).startsWith("+")) {
                        Object value = changeList.get(++i);
                        if (value instanceof ValueGenerator.Wrap) {
                            String field = changeType.substring(1);
                            ((JSONObject) json).put(field, ((ValueGenerator.Wrap) value).call((JSONObject) json, field));
                        } else if (value instanceof ValueGenerator) {
                            ((JSONObject) json).put(changeType.substring(1), ((ValueGenerator) value).call((JSONObject) json));
                        } else {
                            ((JSONObject) json).put(changeType.substring(1), value);
                        }
                    } else if (changeType.startsWith("-")) {
                        String[] removeFields = changeType.substring(1).split(",");
                        for (String removeField : removeFields) {
                            ((JSONObject) json).remove(removeField);
                        }
                    } else if (changeType.contains("->")) {
                        int idex = changeType.indexOf("->");
                        String removeKey = changeType.substring(0, idex);
                        String targetKey = changeType.substring(idex + 2);
                        ((JSONObject) json).put(targetKey, ((JSONObject) json).get(removeKey));
                        ((JSONObject) json).remove(removeKey);
                    }

                }
            }
        }
    }

    private String getQuery(FullHttpRequest request, String key) {
        return getQuery(request, key, true);
    }

    private String getQuery(FullHttpRequest request, String key, boolean throwError){
        JSONObject params = HttpServerHandler.decodeProxyQuery(request);
        if(params.containsKey(key)){
            return params.getString(key);
        }
        if(throwError){
            throw new RuntimeException();
        }
        return "";
    }

    public static DeconstructService register() {
        DeconstructService service = new DeconstructService();

        ThreadUtil.execAsync(() -> {
            JSONObject log = config.getJSONObject("log");
            service.logSourceDir = new File(log.getString("source"));
            service.logSqlDir = new File(log.getString("sql"));
            service.logUnzipDir = new File(log.getString("unzip"));
            service.logSourceDir.mkdirs();
            service.logSqlDir.mkdirs();
            service.logUnzipDir.mkdirs();

//            HttpServerHandler.AddRoute(new Route(("/deconstruct"), (ctx, req) -> {
//                return service.doNettyRequest(ctx, req);
//            }));

            registerHandler("/CourtV4/SearchShiXin", service::SearchShiXin);
            registerHandler("/CourtV4/SearchZhiXing", service::SearchZhiXing);
            registerHandler("/JudgeDocV4/SearchJudgmentDoc", service::SearchJudgmentDoc);
            registerHandler("/JudgeDocV4/GetJudgementDetail", service::GetJudgementDetail);
            registerHandler("/CourtNoticeV4/SearchCourtAnnouncement", service::SearchCourtAnnouncement);
            registerHandler("/CourtNoticeV4/SearchCourtAnnouncementDetail", service::SearchCourtAnnouncementDetail);
            registerHandler("/CourtAnnoV4/SearchCourtNotice", service::SearchCourtNotice);
            registerHandler("/CourtAnnoV4/GetCourtNoticeInfo", service::GetCourtNoticeInfo);
            registerHandler("/JudicialAssistance/GetJudicialAssistance", service::GetJudicialAssistance);
            registerHandler("/ECIException/GetOpException", service::GetOpException);
            registerHandler("/JudicialSale/GetJudicialSaleList", service::GetJudicialSaleList);
            registerHandler("/JudicialSale/GetJudicialSaleDetail", service::GetJudicialSaleDetail);
            registerHandler("/LandMortgage/GetLandMortgageList", service::GetLandMortgageList);
            registerHandler("/LandMortgage/GetLandMortgageDetails", service::GetLandMortgageDetails);
            registerHandler("/EnvPunishment/GetEnvPunishmentList", service::GetEnvPunishmentList);
            registerHandler("/EnvPunishment/GetEnvPunishmentDetails", service::GetEnvPunishmentDetails);
            registerHandler("/ChattelMortgage/GetChattelMortgage", service::GetChattelMortgage);
            registerHandler("/ECIV4/GetDetailsByName", service::GetDetailsByName);
            registerHandler("/History/GetHistorytEci", service::GetHistorytEci);
            registerHandler("/History/GetHistorytInvestment", service::GetHistorytInvestment);
            registerHandler("/History/GetHistorytShareHolder", service::GetHistorytShareHolder);
            registerHandler("/History/GetHistoryShiXin", service::GetHistoryShiXin);
            registerHandler("/History/GetHistoryZhiXing", service::GetHistoryZhiXing);
            registerHandler("/History/GetHistorytCourtNotice", service::GetHistorytCourtNotice);
            registerHandler("/History/GetHistorytJudgement", service::GetHistorytJudgement);
            registerHandler("/History/GetHistorytSessionNotice", service::GetHistorytSessionNotice);
            registerHandler("/History/GetHistorytMPledge", service::GetHistorytMPledge);
            registerHandler("/History/GetHistorytPledge", service::GetHistorytPledge);
            registerHandler("/History/GetHistorytAdminPenalty", service::GetHistorytAdminPenalty);
            registerHandler("/History/GetHistorytAdminLicens", service::GetHistorytAdminLicens);
            registerHandler("/ECIV4/SearchFresh", service::SearchFresh);
            registerHandler("/ECIRelationV4/SearchTreeRelationMap", service::SearchTreeRelationMap);
            registerHandler("/ECIRelationV4/GetCompanyEquityShareMap", service::GetCompanyEquityShareMap);
            registerHandler("/ECIRelationV4/GenerateMultiDimensionalTreeCompanyMap", service::GenerateMultiDimensionalTreeCompanyMap);
            registerHandler("/CIAEmployeeV4/GetStockRelationInfo", service::GetStockRelationInfo);
            registerHandler("/HoldingCompany/GetHoldingCompany", service::GetHoldingCompany);
            registerHandler("/ECICompanyMap/GetStockAnalysisData", service::GetStockAnalysisData);


            //消息坚挺
            MQService.listenMessage("queue", "qcc-deconstruct-request", m -> {
                BlobMessage blobMessage = (BlobMessage) m;
                String requestId = null;
                String sourceRequest = null;
                try {
                    //这俩都没有还玩个屁
                    requestId = blobMessage.getStringProperty("requestId");
                    sourceRequest = blobMessage.getStringProperty("sourceRequest");
                } catch (JMSException e) {
                    e.printStackTrace();
                    return;
                }
                if(S.empty(requestId)) return;
                //检查是否有相同的任务
                synchronized (service){
                    if(service.runningTask.containsKey(requestId)){
                        new QccDeconstructReqponse(-1, 0, requestId, sourceRequest, "已经有相同的任务正在执行中");
                        return;
                    }
                    service.runningTask.put(requestId, true);
                }
                service.onDeconstructRequest(requestId,sourceRequest,(BlobMessage) m);
                synchronized (service){
                    service.runningTask.remove(requestId);
                }
            });

            MQService.listenMessage("queue", "qcc-redeconstruct-request", m ->{
                TextMessage textMessage = (TextMessage) m;
                JSONObject object = (JSONObject) JSON.parseObject(textMessage.getText());
                String requestId = object.getString("requestId");
                int progress = 0;
                try{
                    progress = object.getInteger("progress");
                } finally {
                    if(progress < 1){
                        progress = 1;
                    }
                    if(progress > 3){
                        progress = 3;
                    }
                }
                if(S.empty(requestId)) return;
                synchronized (service){
                    if(service.runningTask.containsKey(requestId)){
                        new QccReDeconstructResponse(-1, 0, requestId, "", "已经有相同的任务正在执行中");
                        return;
                    }
                    service.runningTask.put(requestId, true);
                }
                service.onReDeconstructRequest(requestId, progress);
                synchronized (service){
                    service.runningTask.remove(requestId);
                }
            } );

            System.out.println("deconstruct service boot success");
        });
//        registerHandler("/History/GetHistorytJudgement", service::GetHistorytCourtNotice);

//        registerHandler("/History/GetHistoryZhiXing", service::GetHistoryZhiXing);
//        registerHandler("/History/GetHistoryShiXin", service::GetHistorytShareHolder);

        return service;
    }

    /**
     * 十层穿透
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetStockAnalysisData(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        JSONObject object = (JSONObject) json;
        JSONObject CompanyData = object.getJSONObject("CompanyData");
        changeField(
            CompanyData,
            "+TermStart", DateValue,
            "+TeamEnd", DateValue,
            "+CheckDate", DateValue,
            "+StartDate", DateValue,
            "+EndDate", DateValue,
            "+UpdatedDate", DateValue,
            "+stock_statistics", object.getJSONObject("StockStatistics").toJSONString(),
            "+inner_company_name", compName
        );
        deconstruct(CompanyData, "QCC_SAD", "inner_company_name");
        doDelete("QCC_SAD_PARTNERS", "inner_company_name", compName);
        doDelete("QCC_SAD_STOCK_LIST", "inner_company_name", compName);
        child:
        {
            JSONArray Partners = CompanyData.getJSONArray("Partners");
            if (Partners == null) {
                break child;
            }
            changeField(Partners,
                "+inner_company_name", compName
            );
            deconstruct(Partners, "QCC_SAD_PARTNERS", "");
        }
        child:
        {
            JSONObject StockList = object.getJSONObject("StockList");
            if (StockList == null) {
                break child;
            }
            changeField(
                StockList,
                "+inner_company_name", compName
            );
            deconstruct(StockList, "QCC_SAD_STOCK_LIST", "");

        }
    }

    /**
     * 控股公司查询
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHoldingCompany(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(
            json,
            "+inner_company_name", compName
        );
        deconstruct(json, "QCC_HOLDING_COMPANY", "inner_company_name");
        doDelete("QCC_HOLDING_COMPANY_NAMES", "inner_company_name", compName);
        doDelete("QCC_HOLDING_COMPANY_NAMES_PATHS", "inner_company_name", compName);
        doDelete("QCC_HOLDING_COMPANY_NAMES_OPER", "inner_company_name", compName);
        JSONObject object = (JSONObject) json;
        //子表
        child:
        {
            JSONArray names = (JSONArray) object.getJSONArray("Names");
            if (names == null) {
                break child;
            }
            for (Object _name : names) {
                JSONObject name = (JSONObject) _name;
                JSONArray paths = (JSONArray) name.getJSONArray("Paths");
                JSONObject oper = (JSONObject) name.getJSONObject("Oper");
                if (oper == null) {
                    oper = new JSONObject();
                }
                if (paths == null) {
                    paths = new JSONArray();
                }
                changeField(name,
                    "+inner_company_name", compName,
                    "+Paths", paths.toJSONString(),
                    "+Oper", oper.toJSONString(),
                    "+StartDate", DateValue
                );
                JSONObject kv = (JSONObject) deconstruct(name, "QCC_HOLDING_COMPANY_NAMES", "");
                // FIXME: 2019/4/14 路径结构有问题，不解构
//                changeField(paths,
//                    "+inner_company_name", compName,
//                    "+parent_inner_id", kv.getString("inner_id")
//                    );
//                deconstruct(paths, "QCC_HOLDING_COMPANY_NAMES_PATHS", "");
                changeField(oper,
                    "+inner_company_name", compName,
                    "+parent_inner_id", kv.getString("inner_id")
                );
                deconstruct(oper, "QCC_HOLDING_COMPANY_NAMES_OPER", "");
            }
        }
    }

    /**
     * 企业人员董监高信息
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetStockRelationInfo(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "companyName");
        for (Map.Entry<String, Object> entry : GetStockRelationInfoMap.entrySet()) {
            JSONArray array = (JSONArray) jsonGetByPath(json, entry.getKey() + ".Result");
            if (array == null) {
                continue;
            }
            changeField(array,
                "+inner_company_name", compName
            );
            doDelete((String) entry.getValue(), "inner_company_name", compName);
            deconstruct(array, (String) entry.getValue(), "");
        }
    }

    /**
     * 企业图谱
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GenerateMultiDimensionalTreeCompanyMap(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String keyNo = getQuery(request, "keyNo");
        String compName = getQuery(request, "fullName");
        JSONObject node = (JSONObject) jsonGetByPath(json, "Node");
        changeField(
            node,
            "+inner_company_no", keyNo,
            "+inner_company_name", compName
        );
        doDelete("QCC_TREE_RELATION_MAP", "inner_company_no", keyNo);
        deconstruct(node, "QCC_TREE_RELATION_MAP", "");
    }

    /**
     * 股权结构
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetCompanyEquityShareMap(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String keyNo = getQuery(request, "keyNo");
        String compName = getQuery(request, "fullName");
        changeField(
            json,
            "+inner_company_no", keyNo,
            "+inner_company_name", compName
        );
        doDelete("QCC_CESM", "inner_company_no", keyNo);
        JSONObject kv = (JSONObject) deconstruct(json, "QCC_CESM", "");

        JSONObject object = (JSONObject) json;
        //实际控股信息
        child:
        {
            JSONArray array = (JSONArray) object.getJSONArray("ActualControllerLoopPath");
            if (array == null) {
                break child;
            }
            changeField(array,
                "+inner_company_no", keyNo,
                "+inner_company_name", compName,
                "+cesm_inner_id", kv.getString("inner_id")
            );
            doDelete("QCC_CESM_ACLP", "INNER_COMPANY_NO", keyNo);
            deconstruct(array, "QCC_CESM_ACLP", "");
        }
    }

    /**
     * 企业族谱
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void SearchTreeRelationMap(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String keyNo = getQuery(request, "keyNo");
        String compName = getQuery(request, "fullName");
        JSONObject node = ((JSONObject) json).getJSONObject("Node");
        changeField(
            node,
            "+inner_company_no", keyNo,
            "+inner_company_name", compName
        );
        doDelete("QCC_COMPANY_MAP", "INNER_COMPANY_NO", keyNo);
        deconstruct(node, "QCC_COMPANY_MAP", "");
    }


    /**
     * 新增公司
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void SearchFresh(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        changeField(
            json,
            "+StartDate", DateValue
        );
        deconstruct(json, "QCC_FRESH", "KeyNo");
    }

    /**
     * 历史行政许可
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistorytAdminLicens(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        JSONObject object = (JSONObject) json;
        JSONArray EciList = object.getJSONArray("EciList");
        JSONArray CreditChinaList = object.getJSONArray("CreditChinaList");
        changeField(
            EciList,
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+inner_company_name", compName
        );
        changeField(
            CreditChinaList,
            "LianDate->LiAnDate",
            "+LiAnDate", DateValue,
            "+inner_company_name", compName
        );
        doDelete("QCC_HIS_ADMIN_LICENS_EL", "inner_company_name", compName);
        doDelete("QCC_HIS_ADMIN_LICENS_CCL", "inner_company_name", compName);
        deconstruct(EciList, "QCC_HIS_ADMIN_LICENS_EL", "");
        deconstruct(CreditChinaList, "QCC_HIS_ADMIN_LICENS_CCL", "");
    }

    /**
     * 历史行政处罚
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistorytAdminPenalty(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        JSONObject object = (JSONObject) json;
        JSONArray EciList = object.getJSONArray("EciList");
        JSONArray CreditChinaList = object.getJSONArray("CreditChinaList");
        changeField(
            EciList,
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+inner_company_name", compName
        );
        changeField(
            CreditChinaList,
            "LianDate->LiAnDate",
            "+LiAnDate", DateValue,
            "+inner_company_name", compName
        );
        doDelete("QCC_HIS_ADMIN_PENALTY_EL", "inner_company_name", compName);
        doDelete("QCC_HIS_ADMIN_PENALTY_CCL", "inner_company_name", compName);
        deconstruct(EciList, "QCC_HIS_ADMIN_PENALTY_EL", "");
        deconstruct(CreditChinaList, "QCC_HIS_ADMIN_PENALTY_CCL", "");
    }

    /**
     * 历史股权出质
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistorytPledge(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(json,
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+inner_company_name", compName
        );
        doDelete("QCC_HIS_PLEDGE", "inner_company_name", compName);
        deconstruct(json, "QCC_HIS_PLEDGE", "");
    }

    /**
     * 历史动产抵押
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistorytMPledge(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(
            json,
            "+RegisterDate", DateValue,
            "+inner_company_name", compName
        );
        doDelete("QCC_HIS_M_PLEDGE", "inner_company_name", compName);
        deconstruct(json, "QCC_HIS_M_PLEDGE", "");
    }

    /**
     * 历史开庭公告
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistorytSessionNotice(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(
            json,
            "+inner_company_name", compName,
            "Executegov->ExecuteGov",
            "LianDate->LiAnDate"
        );
        deconstruct(json, "QCC_HIS_SESSION_NOTICE", "Id");
    }

    /**
     * 历史裁判文书
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistorytJudgement(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(json,
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+inner_company_name", compName
        );
        deconstruct(json, "QCC_HIS_JUDGEMENT", "Id");
    }


    private void GetHistorytCourtNotice(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(
            json,
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+inner_company_name", compName
        );
        deconstruct(json, "QCC_HIS_COURT_NOTICE", "Id");
    }

    private void GetHistoryZhiXing(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(json,
            "Anno->AnNo",
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+inner_company_name", compName
        );
        doDelete("QCC_HIS_ZHIXING", "inner_company_name", compName);
        deconstruct(json, "QCC_HIS_ZHIXING", "");
    }


    /**
     * 历史失信
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistoryShiXin(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(json,
            "Anno->AnNo",
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+inner_company_name", compName
        );
        deconstruct(json, "QCC_HIS_SHIXIN", "Id");
    }


    /**
     * 历史股东
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistorytShareHolder(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        JSONObject object = (JSONObject) json;
        JSONArray ChangeDateList = (JSONArray) object.get("ChangeDateList");
        if (ChangeDateList == null) {
            ChangeDateList = new JSONArray();
        }
        JSONArray details = object.getJSONArray("Details");

        changeField(
            details,
            "+ChangeDateList", ChangeDateList.toJSONString(),
            "+inner_company_name", compName
        );
        doDelete("QCC_HIS_SHARE_HOLDER_DETAILS", "inner_company_name", compName);
        deconstruct(details, "QCC_HIS_SHARE_HOLDER_DETAILS", "");
//        JSONObject object = (JSONObject) json;
//        JSONArray details = object.getJSONArray("Details");
//        changeField(details,
//            "+ShouldDate", DateValue,
//            "+inner_company_name", compName
//            );
//        doDelete("QCC_HIS_SHARE_HOLDER_DETAILS", "inner_company_name", compName);
//        deconstruct(details, "QCC_HIS_SHARE_HOLDER_DETAILS", "");
    }

    /**
     * 历史对外投资
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistorytInvestment(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(
            json,
            "+inner_company_name", compName,
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+RegistCapi", new ValueGenerator() {
                @Override
                public Object call(JSONObject kv) {
                    return kv.getString("RegistCapi").replaceAll("万人民币元|万元人民币", "");
                }
            },
            "+FundedRatio", new ValueGenerator() {
                @Override
                public Object call(JSONObject kv) {
                    String FundedRatio = kv.getString("FundedRatio");
                    if(S.empty(FundedRatio)){
                        FundedRatio = "100";
                    } else {
                        FundedRatio = FundedRatio.replace("%", "");
                    }
                    return FundedRatio;
                }
            }

        );
        doDelete("QCC_HIS_INVESTMENT", "inner_company_name", compName);
        deconstruct(json, "QCC_HIS_INVESTMENT", "");
    }

    /**
     * 历史工商信息
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetHistorytEci(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(json,
            "+inner_company_name", compName,
            "+his_data", json.toJSONString()
        );
        deconstruct(json, "QCC_HIS_ECI", "inner_company_name");
        JSONObject object = (JSONObject) json;
        for (Map.Entry<String, Object> entry : HistorytEciMap.entrySet()) {
            String key = entry.getKey();

            JSON obj = (JSON) object.get(key);
            changeField(obj,
                "+inner_company_name", compName,
                new ICanChange() {
                    @Override
                    public boolean call(String key) {
                        return key.endsWith("Date");
                    }
                }, DateValue);
            doDelete((String) entry.getValue(), "inner_company_name", compName);
            JSON finalobjs = deconstruct(obj, (String) entry.getValue(), "");
            if (entry.getKey().equals("EmployeeList")) {
                doDelete("QCC_HIS_ECI_EMPLOYEE_LIST_JOB", "inner_company_name", compName);
                for (Object _o : (JSONArray) finalobjs) {
                    JSONObject o = (JSONObject) _o;
                    JSONArray arr = o.getJSONArray("employees");
                    for (Object a : arr) {
                        changeField(a,
                            "+inner_company_name", compName,
                            "+el_inner_id", o.getString("inner_id")
                        );
                    }
                    deconstruct(arr, "QCC_HIS_ECI_EMPLOYEE_LIST_JOB", "");
                }
            }


        }
    }

    /**
     * 企业关键字精确获取详细信息(Master)
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetDetailsByName(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyword");
        changeField(
            json,
            "+inner_company_name", compName,
            "+UpdatedDate", DateValue,
            "+StartDate", DateValue,
            "+EndDate", DateValue,
            "+TermStart", DateValue,
            "+TeamEnd", DateValue,
            "+CheckDate", DateValue,
            "+UpdatedDate", DateValue
        );
        JSONObject object = (JSONObject) json;
        JSONObject kv = (JSONObject) deconstruct(json, "QCC_DETAILS", "inner_company_name");
        //曾用名
        cym:
        {
            if (!object.containsKey("OriginalName")) {
                break cym;
            }
            JSONArray array = object.getJSONArray("OriginalName");
            if (array == null) {
                break cym;
            }
            changeField(array,
                "+ChangeDate", ValueGenerator.createDate("ChangeDate"),
                "+inner_company_name", compName
            );
            doDelete("QCC_DETAILS_ORIGINAL_NAME", "inner_company_name", compName);
            deconstruct(array, "QCC_DETAILS_ORIGINAL_NAME", "");
        }

        //股东
        gd:
        {
            if (!object.containsKey("Partners")) {
                break gd;
            }
            JSONArray array = object.getJSONArray("Partners");
            if (array == null) {
                break gd;
            }
            changeField(array,
                "+CapiDate", DateValue,
                "+ShoudDate", DateValue,
                "+inner_company_name", compName
            );
            doDelete("QCC_DETAILS_PARTNERS", "inner_company_name", compName);
            deconstruct(array, "QCC_DETAILS_PARTNERS", "");
        }

        //主要人员
        zyry:
        {
            if (!object.containsKey("Employees")) {
                break zyry;
            }
            JSONArray array = object.getJSONArray("Employees");
            if (array == null) {
                break zyry;
            }
            changeField(
                array,
                "+inner_company_name", compName
            );
            doDelete("QCC_DETAILS_EMPLOYEES", "inner_company_name", compName);
            deconstruct(array, "QCC_DETAILS_EMPLOYEES", "");
        }

        //分支机构
        fzjg:
        {
            if (!object.containsKey("Branches")) {
                break fzjg;
            }
            JSONArray array = object.getJSONArray("Branches");
            if (array == null) {
                break fzjg;
            }
            changeField(
                array,
                "+inner_company_name", compName
            );
            doDelete("QCC_DETAILS_BRANCHES", "inner_company_name", compName);
            deconstruct(array, "QCC_DETAILS_BRANCHES", "");
        }

        //变更信息
        child:
        {
            if (!object.containsKey("ChangeRecords")) {
                break child;
            }
            JSONArray array = object.getJSONArray("ChangeRecords");
            if (array == null) {
                break child;
            }
            changeField(
                array,
                "+ChangeDate", DateValue,
                "+inner_company_name", compName
            );
            doDelete("QCC_DETAILS_CHANGE_RECORDS", "inner_company_name", compName);
            deconstruct(array, "QCC_DETAILS_CHANGE_RECORDS", "");
        }


        //联系信息
        child:
        {
            if (!object.containsKey("ContactInfo")) {
                break child;
            }
            JSONObject obj = object.getJSONObject("ContactInfo");
            if (obj.size() == 0) {
                break child;
            }
            changeField(
                obj,
                "+web_site", new ValueGenerator() {
                    @Override
                    public Object call(JSONObject kv) {
                        JSONArray object1 = kv.getJSONArray("WebSite");
                        if (object1 == null) {
                            object1 = new JSONArray();
                        }
                        return object1.toJSONString();
                    }
                },
                "+inner_company_name", compName
            );
            doDelete("QCC_DETAILS_CONTACT_INFO", "inner_company_name", compName);
            deconstruct(obj, "QCC_DETAILS_CONTACT_INFO", "");
        }

        //行业信息
        child:
        {
            if (!object.containsKey("Industry")) {
                break child;
            }
            JSONObject obj = object.getJSONObject("Industry");
            if (obj.size() == 0) {
                break child;
            }
            changeField(
                obj,
                "+inner_company_name", compName
            );
            doDelete("QCC_DETAILS_INDUSTRY", "inner_company_name", compName);
            deconstruct(obj, "QCC_DETAILS_INDUSTRY", "");
        }
    }

    /**
     * 动产抵押表
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetChattelMortgage(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        JSONArray array = (JSONArray) json;
        doDelete("QCC_CHATTEL_MORTGAGE", "inner_company_name", compName);
        doDelete("QCC_CMD_PLEDGE", "inner_company_name", compName);
        doDelete("QCC_CMD_PLEDGEE_LIST", "inner_company_name", compName);
        doDelete("QCC_CMD_SECURED_CLAIM", "inner_company_name", compName);
        doDelete("QCC_CMD_GUARANTEE_LIST", "inner_company_name", compName);
        doDelete("QCC_CMD_CANCEL_INFO", "inner_company_name", compName);
        doDelete("QCC_CMD_CHANGE_LIST", "inner_company_name", compName);
        for (Object object : array) {
            JSONObject obj = (JSONObject) object;
            changeField(
                obj,
                "+RegisterDate", DateValue,
                "+PublicDate", DateValue,
                "+inner_company_name", compName
            );
            JSONObject ret = (JSONObject) deconstruct(obj, "QCC_CHATTEL_MORTGAGE", "");
            String id = ret.getString("inner_id");
            //pledge
            JSONObject pledge = (JSONObject) jsonGetByPath(obj, "Detail.Pledge");
            if (pledge != null) {
                changeField(pledge,
                    "+cm_id", id,
                    "+inner_company_name", compName,
                    "+REGIST_DATE", DateValue);
                deconstruct(pledge, "QCC_CMD_PLEDGE", "");
            }
            //PledgeeList
            JSONArray pledgeeList = (JSONArray) jsonGetByPath(obj, "Detail.PledgeeList");
            if (pledgeeList != null) {
                changeField(pledgeeList,
                    "+cm_id", id,
                    "+inner_company_name", compName
                );
                deconstruct(pledgeeList, "QCC_CMD_PLEDGEE_LIST", "");
            }
            //SecuredClaim
            JSONObject securedClaim = (JSONObject) jsonGetByPath(obj, "Detail.SecuredClaim");
            if (securedClaim != null) {
                changeField(securedClaim,
                    "+cm_id", id,
                    "+inner_company_name", compName);
                deconstruct(securedClaim, "QCC_CMD_SECURED_CLAIM", "");
            }
            //GuaranteeList
            JSONArray GuaranteeList = (JSONArray) jsonGetByPath(obj, "Detail.GuaranteeList");
            if (GuaranteeList != null) {
                changeField(GuaranteeList,
                    "+cm_id", id,
                    "+inner_company_name", compName
                );
                deconstruct(GuaranteeList, "QCC_CMD_GUARANTEE_LIST", "");
            }

            //CancelInfo
            JSONObject CancelInfo = (JSONObject) jsonGetByPath(obj, "Detail.CancelInfo");
            if (CancelInfo != null) {
                changeField(CancelInfo,
                    "+cm_id", id,
                    "+CancelDate", DateValue,
                    "+inner_company_name", compName);
                deconstruct(CancelInfo, "QCC_CMD_CANCEL_INFO", "");
            }

            //ChangeList
            JSONArray ChangeList = (JSONArray) jsonGetByPath(obj, "Detail.ChangeList");
            if (ChangeList != null) {
                changeField(ChangeList,
                    "+cm_id", id,
                    "+ChangeDate", DateValue,
                    "+inner_company_name", compName
                );
                deconstruct(ChangeList, "QCC_CMD_CHANGE_LIST", "");
            }
        }
    }

    /**
     * 环保处罚详情
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetEnvPunishmentDetails(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String id = getQuery(request, "id");
        changeField(json,
            "+PunishDate", DateValue,
            "+Id", id
        );
        deconstruct(json, "QCC_ENV_PUNISHMENT_LIST", "Id");
    }

    /**
     * 环保处罚列表
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetEnvPunishmentList(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(json,
            "+inner_company_name", compName,
            "+PunishDate", DateValue
        );
        deconstruct(json, "QCC_ENV_PUNISHMENT_LIST", "Id");
    }

    /**
     * 土地抵押详情
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetLandMortgageDetails(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String id = getQuery(request, "id");
        changeField(
            json,
            "+OnBoardStartTime", DateValue,
            "+OnBoardEndTime", DateValue,
            "+Id", id
        );
        deconstruct(json, "QCC_LAND_MORTGAGE", "Id");
        JSONObject object = (JSONObject) json;
        doDelete("JG_LM_PEOPLE_RE", "cn_id", id);
        child1:
        {
            JSONObject inner = object.getJSONObject("MortgagePeople");
            if (inner == null) {
                break child1;
            }
            JSONObject obj = newJsonObject(
                "id", id,
                "key_no", inner.getString("KeyNo"),
                "name", inner.getString("Name"),
                "type", "02"
            );
            deconstruct(obj, "QCC_LAND_MORTGAGE_PEOPLE", "");
        }
        child2:
        {
            JSONObject inner = object.getJSONObject("MortgagorName");
            if (inner == null) {
                break child2;
            }
            JSONObject obj = newJsonObject(
                "id", id,
                "key_no", inner.getString("KeyNo"),
                "name", inner.getString("Name"),
                "type", "01"
            );
            deconstruct(obj, "QCC_LAND_MORTGAGE_PEOPLE", "");
        }
    }

    /**
     * 土地抵押列表
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetLandMortgageList(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(json,
            "+StartDate", DateValue,
            "+EndDate", DateValue,
            "+inner_company_name", compName
        );
        deconstruct(json, "QCC_LAND_MORTGAGE", "Id");
    }

    /**
     * 司法拍卖详情
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetJudicialSaleDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String id = getQuery(request, "id");
        changeField(json,
            "+Id", id
//            "+Context", Base64Value
        );
        deconstruct(json, "QCC_JUDICIAL_SALE", "Id");
    }

    /**
     * 司法拍卖列表
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetJudicialSaleList(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "keyWord");
        changeField(json,
            "Executegov->ExecuteGov",
            "Name->title",
            "+inner_company_name", compName
        );
        deconstruct(json, "QCC_JUDICIAL_SALE", "Id");
    }

    private void GetOpException(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compNo = getQuery(request, "keyNo");
        String compName = getQuery(request, "fullName");
        changeField(json,
            "+RemoveDate", DateValue,
            "+AddDate", DateValue,
            "+inner_company_no", compNo,
            "+inner_company_name", compName
        );
        doDelete("QCC_OP_EXCEPTION", "inner_company_no", compNo);
        deconstruct(json, "QCC_OP_EXCEPTION", "");
    }

    /**
     * 司法协助信息
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetJudicialAssistance(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String companyName = getQuery(request, "keyWord");
        doDelete("QCC_JUDICIAL_ASSISTANCE", "inner_company_name", companyName);
        doDelete("QCC_EQUITY_FREEZE_DETAIL", "inner_company_name", companyName);
        changeField(json,
            "+inner_company_name", getQuery(request, "keyWord")
        );
        JSONArray _array = (JSONArray) deconstruct(json, "QCC_JUDICIAL_ASSISTANCE", "");
        JSONArray array = (JSONArray) json;
        int i = 0;
        for (Object o : array) {
            JSONObject object = (JSONObject) o;
            if (object.containsKey("EquityFreezeDetail")) {
                JSONObject detail = object.getJSONObject("EquityFreezeDetail");
                if (detail != null && detail.size() > 0) {
                    changeField(detail,
                        "+FreezeStartDate", DateValue,
                        "+FreezeEndDate", DateValue,
                        "+PublicDate", DateValue,
                        "+ja_inner_id", _array.getJSONObject(i).getString("inner_id"),
                        "+inner_company_name", companyName,
                        "+FREEZE_TYPE", "1"
                    );
                    deconstruct(detail, "QCC_EQUITY_FREEZE_DETAIL", "");
                }
            }
            if (object.containsKey("EquityUnFreezeDetail")) {
                JSONObject detail = object.getJSONObject("EquityUnFreezeDetail");
                if (detail != null && detail.size() > 0) {
                    changeField(detail,
                        "+UnFreezeDate", DateValue,
                        "+PublicDate", DateValue,
                        "+ja_inner_id", _array.getJSONObject(i).getString("inner_id"),
                        "+inner_company_name", companyName,
                        "+FREEZE_TYPE", "2"
                    );
                    deconstruct(detail, "QCC_EQUITY_FREEZE_DETAIL", "");
                }
            }
            if (object.containsKey("JudicialPartnersChangeDetail")) {
                JSONObject detail = object.getJSONObject("JudicialPartnersChangeDetail");
                if (detail != null && detail.size() > 0) {
                    changeField(detail,
                        "+AssistExecDate", DateValue,
                        "+ja_inner_id", array.getJSONObject(i).getString("inner_id"),
                        "+inner_company_name", companyName,
                        "+FREEZE_TYPE", "3"
                    );
                    deconstruct(detail, "QCC_EQUITY_FREEZE_DETAIL", "");
                }
            }

            i++;
        }
    }

    /**
     * 开庭公告详情
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetCourtNoticeInfo(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String id = getQuery(request, "id");
        changeField(json,
            "+Id", id
        );
        deconstruct(json, "QCC_COURT_NOTICE", "Id");
        //补充上诉人和被上诉人
        JSONObject object = (JSONObject) json;
        JSONArray list = new JSONArray();
        if (object.containsKey("Prosecutor")) {
            list.addAll(
                object.getJSONArray("Prosecutor")
                    .stream()
                    .map(_o -> {
                        JSONObject o = (JSONObject) _o;
                        JSONObject kv = new JSONObject();
                        kv.put("name", o.getString("Name"));
                        kv.put("id", id);
                        kv.put("key_no", o.getString("KeyNo"));
                        kv.put("type", "01");
                        return kv;
                    })
                    .collect(Collectors.toList())
            );
        }
        if (object.containsKey("Defendant")) {
            list.addAll(object.getJSONArray("Defendant")
                .stream()
                .map(_o -> {
                    JSONObject o = (JSONObject) _o;
                    JSONObject kv = new JSONObject();
                    kv.put("name", o.getString("Name"));
                    kv.put("id", id);
                    kv.put("key_no", o.getString("KeyNo"));
                    kv.put("type", "02");
                    return kv;
                })
                .collect(Collectors.toList())
            );
        }

        if (list.size() > 0) {
//            doDelete("QCC_COURT_NOTICE_PEOPLE", "id", object.getString("Id"));
            deconstruct(list, "QCC_COURT_NOTICE_PEOPLE", "id,name,type");
        }

    }

    /**
     * 开庭公告列表
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void SearchCourtNotice(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "searchKey");
        JSONArray list = new JSONArray();
        for (Object o : (JSONArray) json) {
            JSONObject object = (JSONObject) o;
            for (String p : ((String) object.getOrDefault("Prosecutorlist", "")).split("\\t")) {
                list.add(newJsonObject(
                    "name", p,
                    "type", "01",
                    "id", object.getString("Id"),
                    "inner_company_name", compName
                ));
            }

            for (String p : ((String) object.getOrDefault("Defendantlist", "")).split("\\t")) {
                list.add(newJsonObject(
                    "name", p,
                    "type", "02",
                    "id", object.getString("Id"),
                    "inner_company_name", compName
                ));
            }
        }
        changeField(json,
            "-Defendantlist",
            "-Prosecutorlist",
            "Executegov->Execute_gov",
            "LianDate", DateValue,
            "LianDate->Li_an_Date",
            "+inner_company_name", compName
        );
        deconstruct(json, "QCC_COURT_NOTICE", "Id");

        //列表也先一步存关联
        deconstruct(list, "QCC_COURT_NOTICE_PEOPLE", "id,name,type");
    }

    /**
     * 法院公告详情
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void SearchCourtAnnouncementDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        changeField(json,
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "PublishDate->PublishedDate",
            "+Id", getQuery(request, "id")
//            "PublishDate->PublishedDate"
        );
        deconstruct(json, "QCC_COURT_ANNOUNCEMENT", "Id");
        JSONObject object = (JSONObject) json;
        nkn:
        {
            if (!object.containsKey("NameKeyNoCollection")) {
                break nkn;
            }
            JSONArray array = object.getJSONArray("NameKeyNoCollection");
            if (array == null) {
                break nkn;
            }
            changeField(
                array,
                "+id", object.getString("Id")
            );
            doDelete("QCC_COURT_ANNOUNCEMENT_PEOPLE", "id", object.getString("Id"));
            deconstruct(array, "QCC_COURT_ANNOUNCEMENT_PEOPLE", "");
        }
    }

    /**
     * 法院公告列表
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void SearchCourtAnnouncement(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        changeField(json,
//            "-PublishedDate",
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+inner_company_name", getQuery(request, "companyName")
        );
        deconstruct(json, "QCC_COURT_ANNOUNCEMENT", "Id");
    }

    /**
     * 裁判文书详情
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetJudgementDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        changeField(json,
            new ICanChange() {
                @Override
                public boolean call(String key) {
                    return key.endsWith("Date") && !key.equals("Judege_Date");
                }
            }, DateValue,
            "-Content",
            "+Appellor", ValueGenerator.createStrList("Appellor"),
            "Defendantlist->DefendantList",
            "Prosecutorlist->ProsecutorList",
            "+DefendantList", ValueGenerator.createStrList("DefendantList"),
            "+ProsecutorList", ValueGenerator.createStrList("ProsecutorList"),
//            "+Content", Base64Value,
            "+ContentClear", Base64Value
        );
        deconstruct(json, "QCC_JUDGMENT_DOC", "Id");

        //解构子表
        JSONObject object = (JSONObject) json;
        cnlist:
        {
            if (object.containsKey("CourtNoticeList")) {
                JSONArray array = (JSONArray) jsonGetByPath(object, "CourtNoticeList.CourtNoticeInfo");
                if (array == null) {
                    break cnlist;
                }
                //清空关联信息
                doDelete("QCC_JUDGMENT_DOC_CN", "QCC_DETAILS_ID", object.getString("Id"));
                changeField(array,
                    "+TOTAL_NUM", jsonGetByPath(object, "CourtNoticeList.TotalNum"),
                    "+QCC_DETAILS_ID", object.getString("Id")
                );
//                array = JSONUtil.parseArray(array.stream()
//                    .map(i -> {
//                        JSONObject kv = (JSONObject) i;
//                        changeField();
//                        JSONObject ret = new JSONObject();
//                        ret.put("CN_ID", object.getString("Id"));
//                        ret.put("QCC_ID", kv.getString("Id"));
//                        return ret;
//                    })
//                    .toArray()
//                );
                deconstruct(array, "QCC_JUDGMENT_DOC_CN", "");
            }
        }

        rc:
        {
            if (object.containsKey("RelatedCompanies")) {
                JSONArray array = object.getJSONArray("RelatedCompanies");
                if (array == null) {
                    break rc;
                }
                changeField(array,
                    "+QCC_DETAILS_ID", object.getString("Id")
                );

                doDelete("QCC_JUDGMENT_DOC_COM", "QCC_DETAILS_ID", object.getString("Id"));

//                array = JSONUtil.parseArray(
//                    array.stream()
//                        .map(i -> {
//                            JSONObject kv = (JSONObject) i;
//                            JSONObject ret = new JSONObject();
//                            ret.put("jd_id", object.getString("Id"));
//                            ret.put("key_no", kv.getString("KeyNo"));
//                            ret.put("name", kv.getString("Name"));
//                            return ret;
//                        })
//                        .toArray()
//                );
                deconstruct(array, "QCC_JUDGMENT_DOC_COM", "");
            }
        }
    }

    /**
     * 裁判文书列表
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void SearchJudgmentDoc(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        changeField(json,
            "+SubmitDate", DateValue,
            "+UpdateDate", DateValue,
            "+inner_company_name", getQuery(request, "searchKey")
        );
        deconstruct(json, "QCC_JUDGMENT_DOC", "Id");
    }

    /**
     * 失信信息列表
     *
     * @param ctx
     * @param request
     * @param array
     */
    private void SearchShiXin(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        changeField(
            json,
            "+Liandate", DateValue,
            "+Publicdate", DateValue,
            "Sourceid->Source_id",
            "Uniqueno->Unique_no",
            "Liandate->Li_an_date",
            "Anno->an_no",
            "Orgno->org_no",
            "Ownername->Owner_name",
            "Executegov->Execute_gov",
            "Executeunite->Execute_unite",
            "Yiwu->Yi_wu",
            "Executestatus->Execute_status",
            "Actionremark->Action_remark",
            "Publicdate->Public_date",
            "Updatedate->Update_date",
            "Executeno->Execute_no",
            "Performedpart->Performed_part",
            "Unperformpart->Unperform_part",
            "+inner_company_name", getQuery(request, "searchKey")
        );
        deconstruct(json, "QCC_SHIXIN", "Id");
    }


    /**
     * 被执行信息
     *
     * @param ctx
     * @param request
     * @param array
     */
    private void SearchZhiXing(ChannelHandlerContext ctx, FullHttpRequest request, JSON array) {
        changeField(
            array,
            "+Liandate", DateValue,
            "Sourceid->Source_id",
            "Liandate->Li_an_date",
            "Sourceid->Source_id",
            "Anno->An_no",
            "Biaodi->Biao_di",
            "+Biao_di", new ValueGenerator() {
                @Override
                public Object call(JSONObject kv) {
                    BigDecimal bd = new BigDecimal(kv.getString("Biao_di"));
                    return bd.toPlainString();
                }
            },
            "Updatedate->Update_date",
            "+inner_company_name", getQuery(request, "searchKey")
        );
        deconstruct(array, "QCC_ZHIXING", "Id");
    }

    private JSON deconstruct(JSON obj, JSONObject map, Object... common) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof JSONArray && ((JSONArray) obj).size() == 0) {
            return obj;
        }
        if (obj instanceof JSONObject && ((JSONObject) obj).size() == 0) {
            return obj;
        }
        return null;
    }

    private JSON deconstruct(JSON obj, String tableName, String existKey) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof JSONArray && ((JSONArray) obj).size() == 0) {
            return obj;
        }
        if (obj instanceof JSONObject && ((JSONObject) obj).size() == 0) {
            return obj;
        }
        StringBuilder sb = new StringBuilder();
        Date now = new Date();
        if (obj instanceof JSONObject) {
            JSONObject ret = insertSingle((JSONObject) obj, tableName, existKey);
            if (((JSONObject) obj).containsKey("Children") || ((JSONObject) obj).containsKey("children")) {
                JSONArray children = (JSONArray) jsonGetByPath(obj, "Children");
                if (children == null) {
                    children = (JSONArray) jsonGetByPath(obj, "children");
                }
                if (children != null) {
                    String id = ret.getString("inner_id");
                    for (Object _child : children) {
                        JSONObject child = (JSONObject) _child;
                        child.put("inner_parent_id", id);
                        child.put("inner_company_no", ret.getString("inner_company_no"));
                        child.put("inner_company_name", ret.getString("inner_company_name"));
                        deconstruct(child, tableName, "");
                    }
                }
            }
            return ret;

        } else if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            JSONArray ret = new JSONArray();
            for (Object object : array) {
                JSON _ret = deconstruct((JSON) object, tableName, existKey);
                if (_ret instanceof JSONArray) {
                    ret.addAll((JSONArray) _ret);
                } else {
                    ret.add(_ret);
                }
            }
            return ret;
        }
        throw new RuntimeException();
    }

    private JSONObject insertSingle(JSONObject object, String tableName, String existKey) {
        if (object.size() == 0) {
            return object;
        }
        JSONObject kv = new JSONObject();
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            kv.put(camelToUnderline(entry.getKey()), entry.getValue());
        }
        kv.put("input_date", new Date());

        //检查是否有相同的字段
        if (S.notBlank(existKey)) {
            String[] keys = (existKey.split("\\,"));
//            if (S.empty(object.getString(existKey))) {
//                return kv;
//            }
            if (keys.length == 0) {
                return kv;
            }

            String[] cKeys = new String[keys.length];
            String[] values = new String[keys.length];
            int i = 0;
            for (String key : keys) {
                values[i] = S.wrap(object.getString(key), "'");
                cKeys[i++] = camelToUnderline(key);
            }
//            String sql = "select count(1) from " + tableName + " where 1 = 1 ";
            StringBuilder sb = new StringBuilder();
            sb.append(S.fmt("merge into %s q1 using ( values (%s) ) as q2( %s )", tableName, String.join(",", values), String.join(",", cKeys)));
            String on = "";
            for (i = 0; i < keys.length; i++) {
//                sb.append(S.fmt( " and %s = '%s'", cKeys[i], object.getString(keys[i])));
                on += " and q1." + cKeys[i] + " = " + "q2." + cKeys[i];
            }
            on = on.substring(5);
            sb.append(" on ");
            sb.append(on);
            sb.append(" when matched then update set ");
            sb.append(buildUpdateSqlFields(kv));
            sb.append(" when not matched then insert");
            sb.append(buildInsertSqlFields(kv));

//                String k = camelToUnderline(key);
//            String sql = "%s q1 using( select %s from %s where ) q2 on %s when matched then update set %s when not matched then insert %s";
//            sql += " for update";
//            sql += sb.toString();
//            String sql = buildUpdateSql(tableName, kv, sb.toString());
            String sql = sb.toString();
            if (autoCommit) {
                int row = sqlManager.executeUpdate(new SQLReady(sql));
                if (row > 0) {
                    return kv;
                }
            } else {
                readySqls.update.add(sql);
                return kv;
            }
//            List<JSONObject> ret = sqlManager.execute(new SQLReady(sql), JSONObject.class);
//            if(ret.size() > 0){
//                int count = ret.get(0).getInteger("1");
//                if (count > 0) {
//                    sql = buildUpdateSql(tableName, kv, sb.toString());
//                    sqlManager.executeUpdate(new SQLReady(sql));
//                    return kv;
//                }
//            }
        }
        String sql = buildInsertSql(tableName, kv);
        if (autoCommit) {
            sqlManager.executeUpdate(new SQLReady(sql));
        } else {
            readySqls.insert.add(sql);
        }
        return kv;
    }

    private void formatValue(StringBuilder sb, JSONObject kv, String s) {
        Object object = kv.get(s);
        if (object == null) {
            sb.append("null");
        } else if (object instanceof Date) {
            String d = DateUtil.format((Date) object, "yyyy-MM-dd hh:mm:ss");
            if (S.empty(d)) {
                sb.append("null");
            } else {
                sb.append(S.fmt("'%s'", d));
            }
        } else if (object instanceof JSONArray || object instanceof JSONObject) {
            return;
        } else {
            String value = kv.getString(s);
            sb.append(S.fmt("'%s'", value));
        }
        sb.append(",");
    }

    private void doDelete(String tableName, String key, String value) {
        String sql = buildDeleteSql(tableName, key, value);
        if (autoCommit) {
            sqlManager.executeUpdate(new SQLReady(sql));
        } else {
            readySqls.delete.add(sql);
        }
    }

    private String buildDeleteSql(String tableName, String key, String value) {
        return S.fmt("delete from %s where %s = '%s'", tableName, key, value);
    }

    private String buildUpdateSql(String tableName, JSONObject kv, String where) {
        StringBuilder sb = new StringBuilder();
        sb.append(S.fmt("update %s set ", tableName));
        for (String s : kv.keySet()) {
            Object obj = kv.get(s);
            //先不要管关联表
            if (obj instanceof JSONObject || obj instanceof JSONArray) {
                continue;
            }
            sb.append(s);
            sb.append(" = ");
            formatValue(sb, kv, s);
        }
        sb.deleteCharAt(sb.length() - 1);

        sb.append(" where 1 = 1");
        sb.append(where);
        return sb.toString();
    }

    private String buildUpdateSqlFields(JSONObject kv) {
        StringBuilder sb = new StringBuilder();
        for (String s : kv.keySet()) {
            Object obj = kv.get(s);
            //先不要管关联表
            if (obj instanceof JSONObject || obj instanceof JSONArray) {
                continue;
            }
            sb.append(s);
            sb.append(" = ");
            formatValue(sb, kv, s);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String buildInsertSql(String tableName, JSONObject kv) {
        StringBuilder sb = new StringBuilder();
        kv.put("inner_id", objectId.nextId());
        sb.append("insert into ");
        sb.append(tableName);
        sb.append(" (");
        for (String s : kv.keySet()) {
            Object value = kv.get(s);
            if (value instanceof JSONArray || value instanceof JSONObject || value == null) {
                continue;
            }
            sb.append(s);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")values(");
        for (String s : kv.keySet()) {
            Object value = kv.get(s);
            if (value instanceof JSONArray || value instanceof JSONObject || value == null) {
                continue;
            }
            formatValue(sb, kv, s);
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
//        sb.append(" where not exists(select 1 from ");
//        sb.append(tableName);
//        sb.append(" where inner_id = '");
//        sb.append(kv.getString("inner_id"));
//        sb.append("')");
        return sb.toString();
    }

    public String buildInsertSqlFields(JSONObject kv) {
        StringBuilder sb = new StringBuilder();
        kv.put("inner_id", objectId.nextId());
        kv.put("input_date", new Date());
        sb.append(" (");
        for (String s : kv.keySet()) {
            Object value = kv.get(s);
            if (value instanceof JSONArray || value instanceof JSONObject || value == null) {
                continue;
            }
            sb.append(s);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")values(");
        for (String s : kv.keySet()) {
            Object value = kv.get(s);
            if (value instanceof JSONArray || value instanceof JSONObject || value == null) {
                continue;
            }
            formatValue(sb, kv, s);
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    private boolean isSuccess(JSONObject object) {
        return S.eq(object.getString("Status"), "200");
    }


    /**
     * step0 保存文件
     * @param blobMessage
     * @param requestId
     * @throws Exception
     */
    private void deconstructStep0(BlobMessage blobMessage, String requestId) throws Exception {
        File sourcefile = new File(logSourceDir, requestId);
        if (sourcefile.exists()) sourcefile.delete();
        try (
            InputStream is = blobMessage.getInputStream();
            FileOutputStream fos = new FileOutputStream(sourcefile);
        ) {
            IoUtil.copy(is, fos);
        } catch (Exception e) {
            throw new Exception();
        }
    }

    /**
     * step1
     * 解压文件
     */
    private void deconstructStep1(String requestId) throws IOException {
        File unzipFile = new File(logUnzipDir, requestId);
        if (unzipFile.exists()) unzipFile.delete();
        unzipFile(new File(logSourceDir, requestId), unzipFile);
    }

    /**
     * step2
     * 分析文件
     * 因为sqlready只用了一个容器，以及需要executor线程池处理，所以这里只能使用同步
     *
     * @param requestId
     */
    private synchronized List<String> deconstructStep2(String requestId, AtomicBoolean someError) throws IOException, InterruptedException {
        File unzipFile = new File(logUnzipDir, requestId);
        List<Slice> tasks = new ArrayList<>();
        byte[] buf = BufferPool.allocate();
        try (
            RandomAccessFile raf = new RandomAccessFile(unzipFile, "r");
        ) {
            int pos = 0;
            int len = -1;
            while (true) {
                if (pos >= raf.length()) {
                    break;
                }
                raf.seek(pos);
                len = raf.readInt();
                pos += 4;
                raf.read(buf, 0, len);
                String link = new String(buf, 0, len);
                pos += len;
                len = raf.readInt();
                pos += 4;
                Slice slice = new Slice(link, pos, len, null);
                tasks.add(slice);
                pos += len;
            }
        }

        ThreadLocal<RandomAccessFile> local = new ThreadLocal<RandomAccessFile>() {
            private Vector<RandomAccessFile> files = new Vector();

            @Override
            protected RandomAccessFile initialValue() {
                try {
                    RandomAccessFile raf = new RandomAccessFile(unzipFile, "r");
                    files.add(raf);
                    return raf;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void finalize() throws Throwable {
                for (RandomAccessFile file : files) {
                    try {
                        file.close();
                    } finally {
                    }
                }
                super.finalize();
            }
        };

        final AtomicBoolean hasError = new AtomicBoolean(false);
        readySqls = new SqlVectors();
        CountDownLatch cl = new CountDownLatch(tasks.size());
        boolean[] flags = new boolean[tasks.size()];
        for (DeconstructService.Slice slice : tasks) {
            executor.execute(() -> {
                byte[] bs = BufferPool.allocate();
                try {
                    String str = null;
                    RandomAccessFile file = local.get();
                    file.seek(slice.bodyStart);
                    file.read(bs, 0, (int) slice.bodyLen);
                    str = new String(bs, 0, slice.bodyLen);
                    destructSingle(slice.link, str, someError);
                } catch (Exception e) {
                    e.printStackTrace();
                    hasError.set(true);
                } finally {
                    BufferPool.deallocate(bs);
                    cl.countDown();
                }
            });
        }

        cl.await();

        if (hasError.get()) {
            throw new RuntimeException();
        }

        List<String> sqls = new ArrayList<>();
        sqls.addAll(readySqls.delete);
        sqls.addAll(readySqls.insert);
        sqls.addAll(readySqls.update);

        return sqls;
    }

    /**
     * step3
     * 更新到数据库
     * @param sqls
     */
    private void deconstructStep3(List<String> sqls) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            System.out.println("开始插入");
            long stime = System.currentTimeMillis();
            for (String sql : sqls) {
                stmt.addBatch(sql);
            }
//            for (String sql : sqls) {
//                try{
//                    stmt.execute(sql);
//                } catch (Exception e){
//                    System.out.println(sql);
//                }
//            }
            stmt.executeBatch();
            conn.commit();

            System.out.println("插入时间:" + (System.currentTimeMillis() - stime));

            ;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * step4
     *
     * 写入sql日志
     * @param requestId
     * @param sqls
     */
    private void deconstructStep4(String requestId, List<String> sqls) {
        ThreadUtil.execAsync(() -> {
            File sqlfile = new File(logSqlDir, requestId);
            if (sqlfile.exists()) sqlfile.delete();
            FileLock lock = null;
            try (
                RandomAccessFile raf = new RandomAccessFile(sqlfile, "rw");
                FileChannel channel = raf.getChannel();
            ) {
                lock = channel.lock();
                for (String sql : sqls) {
                    raf.write(sql.getBytes(StandardCharsets.UTF_8));
                    raf.write("\r\n".getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void onReDeconstructRequest(String requestId, int progress){
        if(progress < 1 || progress > 3){
            return;
        }
        autoCommit = false;
        QccReDeconstructResponse reqponse = new QccReDeconstructResponse(-1, 0, requestId, "", "");
        AtomicBoolean someError = new AtomicBoolean(false);
        try{
            List<String> sqls = null;
            //从第一步开始解
            if(progress <= 1){
                reqponse.progress = 1;
                deconstructStep1(requestId);
            }
            if(progress <= 2){
                reqponse.progress = 2;
                sqls = deconstructStep2(requestId, someError);
            }

            reqponse.progress = 3;
            if(progress <= 2){
                deconstructStep3(sqls);
                deconstructStep4(requestId, sqls);
            } else {
                //如果从第三步，可以直接读出sql的文件
                File file = new File(logSqlDir, requestId);
                sqls = new ArrayList<>();
                ByteBuf buf = Unpooled.buffer();
                try(
                    RandomAccessFile raf = new RandomAccessFile(file, "r");
                    ){
                    long ptr = -1;
                    long len = raf.length();
                    while(++ptr < len){
                        byte b = raf.readByte();
                        buf.writeByte(b);
                        if(b == '\r'){
                            b = raf.readByte();
                            ++ptr;
                            if (b == '\n') {
                                byte[] bytes = new byte[buf.readableBytes()];
                                buf.readBytes(bytes);
                                sqls.add(new String(bytes, StandardCharsets.UTF_8));
                                buf.clear();
                            }
                        }
                    }
                }
                deconstructStep3(sqls);
            }
            if(someError.get()){
                reqponse.finished = 1;
            } else {
                reqponse.finished = 0;
            }
        } catch (Exception e){
            reqponse.errorMessage = e.getMessage();
            e.printStackTrace();
        }
        reqponse.send();

    }


    /**
     * @api {ActiveMQ} / MQ接口
     * @apiDescription 该接口不是使用http发送请求，而是调用activemq的服务<a target="_blank" href="/doc/test.html">测试地址</a>
     *
     * @apiGroup QCC-MQ
     * @apiVersion 0.0.1
     *
     * @apiSuccessExample 数据加工请求:
     * queue: qcc-deconstruct-request
     * type: BlobMessage
     * properties: {
     *     "requestId":"数据ID",
     *     "sourceRequest":"数据原始信息"
     * }
     * content: 文件信息
     *
     *
     * @apiSuccessExample 加工回执:
     * queue: qcc-deconstruct-response
     * type: TextMessage
     * content: {
     *     "requestId":"数据ID",
     *     "sourceRequest":"原始数据",
     *     "progress":"进行到哪一阶段 0保存数据 1解压 2分析生成sql 3sql入库",
     *     "finished": "是否完成 -1为全部失败 0为成功 1为部分成功",
     *     "errorMessage": "错误信息"
     * }
     *
     *
     * @apiSuccessExample 重加工请求:
     * queue: qcc-redeconstruct-request
     * type: TextMessage
     * content: {
     *     "requestId": "加工失败的数据ID",
     *     "progress": "从哪个阶段重新开始，1解压前 2分析生成sql前 3sql入库前"
     * }
     *
     * @apiSuccessExample 重加工回执:
     * queue: qcc-redeconstruct-response
     * 其余同加工回执
     */



    /**
     * @api {get} /log/deconstruct/{type}/{id} 二进制日志下载接口
     * @apiGroup QCC-MQ
     * @apiVersion 0.0.1
     *
     * @apiParam {string="source","unzip","sql"} type 数据类型，分别是“解析前的原始数据”，“解压后的原始数据”,“分析后的sql数据”
     * @apiParam {string} id 数据requestId
     *
     */
    public void onDeconstructRequest(String requestId, String sourceRequest, BlobMessage blobMessage)  {
        autoCommit = false;

        QccDeconstructReqponse reqponse = new QccDeconstructReqponse(-1, 0, requestId, sourceRequest, "");
        AtomicBoolean someError = new AtomicBoolean(false);
        try{
            deconstructStep0(blobMessage, requestId);
            reqponse.progress = 1;
            deconstructStep1(requestId);
            reqponse.progress = 2;
            List<String> sqls = deconstructStep2(requestId, someError);
            reqponse.progress = 3;
            deconstructStep3(sqls);
            deconstructStep4(requestId, sqls);
            if(someError.get()){
                reqponse.finished = 1;
            } else {
                reqponse.finished = 0;
            }
        } catch (Exception e){
            reqponse.errorMessage = e.getMessage();
            e.printStackTrace();
        }
        reqponse.send();

//        try{
////            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
////            Date now = new Date();
////            String dirName = sdf.format(now);
//            //记录
//
//            //解压
//
//
//            //分析
//
//
//            //不管如何，写入数据
//            List<String> sqls = new ArrayList<>();
//            sqls.addAll(readySqls.delete);
//            sqls.addAll(readySqls.insert);
//            sqls.addAll(readySqls.update);
//            String finalRequestId = requestId;
//            ThreadUtil.execAsync(() -> {
//                File sqlfile = new File(logSqlDir, finalRequestId);
//                if(sqlfile.exists()) sqlfile.delete();
//                try(
//                    RandomAccessFile raf = new RandomAccessFile(sqlfile, "rw");
//                    ){
//                    for (String sql : sqls) {
//                        raf.write(sql.getBytes(StandardCharsets.UTF_8));
//                        raf.write("\r\n".getBytes());
//                    }
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//            });
//
//            if(hasError.get()){
//                throw new RuntimeException();
//            }
//
//            //插入数据库
//            Connection conn = null;
//            try {
//                conn = dataSource.getConnection();
//                conn.setAutoCommit(false);
//                Statement stmt = conn.createStatement();
//                System.out.println("开始插入");
//                long stime = System.currentTimeMillis();
//                for (String s : readySqls.delete) {
//                    stmt.addBatch(s);
//                }
//                for (String s : readySqls.insert) {
//                    stmt.addBatch(s);
//                }
//                for (String s : readySqls.update) {
//                    stmt.addBatch(s);
//                }
//                stmt.executeBatch();
//                conn.commit();
//
//                System.out.println("插入时间:" + (System.currentTimeMillis() - stime));
//
//                ;
//            } catch (SQLException e) {
//                hasError.set(true);
//                if (conn != null) {
//                    try {
//                        conn.rollback();
//                    } catch (SQLException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//                e.printStackTrace();
//            } finally {
//                if (conn != null) {
//                    try {
//                        conn.close();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            if(hasError.get()){
//                throw new RuntimeException();
//            }
//
//            MQService.sendTopicMessage(
//                "qcc-deconstruct-response",
//                new QccDeconstructReqponse(
//                    true,
//                    "end",
//                    requestId,
//                    sourceRequest
//                ).toString()
//            );
//        }
//        catch (Exception e){
//            e.printStackTrace();
//
//            MQService.sendTopicMessage(
//                "qcc-deconstruct-response",
//                new QccDeconstructReqponse(false, "end", requestId, sourceRequest).toString()
//            );
//        } finally {
//            mutex.unlock();
//        }
    }

    public void destructSingle(String url, String str, AtomicBoolean someError) {
        JSONObject object = JSON.parseObject(str);
        for (Map.Entry<String, DeconstructService.DeconstructHandler> entry : DeconstructService.handlers.entrySet()) {
            if (HttpServerHandler.matches(url, entry.getKey())) {
                DeconstructService.DeconstructHandler handler = entry.getValue();
                ByteBuf buf = Unpooled.buffer();
                DefaultHttpHeaders headers = new DefaultHttpHeaders();
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, buf, headers, headers);
                JSONObject od = (object.getJSONObject("OriginData"));
                if (S.eq(od.getString("Status"), "200")) {
                    handler.call(null, request, (JSON) od.get("Result"));
                } else if(!S.startsWith(od.getString("Status"), "2")){
                    someError.set(true);
                }
            }
        }
    }

    private String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        //如果已经是蛇形, 直接返回
        if (param.contains("_")) {
            return param.toLowerCase();
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        if (sb.charAt(0) == '_') {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

//    private static Date convertIsoDate(SimpleDateFormat sdf, String str) {
//        if (str == null) {
//            return null;
//        }
//        try {
//            return sdf.parse(str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            throw new RuntimeException(S.fmt("%s %s 时间转换错误", sdf.toString(), str));
//        }
//    }

    private static Date convertDate(String format, String str) {
        if (str == null) {
            return null;
        }
        try {
            return DateUtil.parse(str, format);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Date convertYmdDate(String str) {
        return convertDate("yyyy-MM-dd", str);
    }

    private static Date converYmdhmsDate(String str) {
        return convertDate("yyyy-MM-dd hh:mm:ss", str);
    }


    public interface ICanChange {
        boolean call(String key);
    }

    public interface ITargetValue {
        Object call();
    }

    public interface ValueGenerator {
        Object call(JSONObject kv);

        public static ValueGenerator createStrList(String key) {
            return kv -> {
                Object json = kv.get(key);
                if (json instanceof JSONArray) {
                    JSONArray array = (JSONArray) json;
                    return array.toJSONString();
                } else {
                    return "[]";
                }
            };
        }


        public static class Wrap {
            private static final int TYPE_DATE = 760;
            private static final int TYPE_ELSE = 114;
            private static final int TYPE_BASE64 = 613;
            public int type;

            public Object call(JSONObject kv, String field) {
                ValueGenerator vg = null;
                switch (type) {
                    case TYPE_DATE:
                        vg = ValueGenerator.createDate(field);
                        break;

                    case TYPE_BASE64:
                        vg = new ValueGenerator() {
                            @Override
                            public Object call(JSONObject kv) {
                                try {
                                    return Base64.getEncoder().encodeToString(kv.getString(field).getBytes(StandardCharsets.UTF_8.name()));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                return "";
                            }
                        };
                }
                return vg.call(kv);
            }
        }

        public static ValueGenerator createDate(String key) {
            return kv -> {
                Object value = kv.get(key);
                if (value instanceof Date) {
                    return value;
                }
                if (value == null) {
                    return null;
                }
                if (value instanceof String && "".equals(value)) {
                    return null;
                }
                for (String dateFormat : dateFormats) {
                    try {
                        return DateUtil.parse((String) value, dateFormat).toJdkDate();
                    } catch (Exception e) {
                    }
                }
                System.out.println("date format error : " + key + "," + kv.getString(key));
                return null;
            };
        }

        public static Wrap createDate() {
            Wrap wrap = new Wrap();
            wrap.type = Wrap.TYPE_DATE;
            return wrap;
        }

        public static Wrap createBase64() {
            Wrap wrap = new Wrap();
            wrap.type = Wrap.TYPE_BASE64;
            return wrap;
        }
    }

    public interface DeconstructHandler {
        public void call(ChannelHandlerContext ctx, FullHttpRequest request, JSON json);
    }

    public static class SqlVectors {
        public Vector<String> insert = new Vector<>();
        public Vector<String> update = new Vector<>();
        public Vector<String> delete = new Vector<>();
    }


    public static class Slice {
        String link;
        long bodyStart;
        int bodyLen;
        String body;

        public Slice(String link, long bodyStart, int bodyLen, String body) {
            this.link = link;
            this.bodyStart = bodyStart;
            this.bodyLen = bodyLen;
            this.body = body;
        }
    }


    public static class QccDeconstructReqponse {
        public int finished;
        public int progress;
        public String requestId;
        public String sourceRequest;
        public String errorMessage;

        public QccDeconstructReqponse(int finished, int step, String requestId, String sourceRequest, String errorMessage) {
            this.finished = finished;
            this.progress = step;
            this.requestId = requestId;
            this.sourceRequest = sourceRequest;
            this.errorMessage = errorMessage;
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }

        public void send(){
            MQService.sendMessage("queue", "qcc-deconstruct-response", this.toString());
        }
    }

    public static class QccReDeconstructResponse extends QccDeconstructReqponse{
        public QccReDeconstructResponse(int finished, int step, String requestId, String sourceRequest, String errorMessage) {
            super(finished, step, requestId, sourceRequest, errorMessage);
        }

        @Override
        public void send() {
            MQService.sendMessage("queue", "qcc-redeconstruct-response", this.toString());
        }
    }





//    public interface DeconstructArrayHandler{
//        public void call(ChannelHandlerContext ctx, FullHttpRequest request, JSONArray array);
//    }
}
