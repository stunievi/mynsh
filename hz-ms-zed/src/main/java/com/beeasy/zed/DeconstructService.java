package com.beeasy.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import static com.beeasy.zed.Utils.*;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
//import cn.hutool.json.*;
import com.alibaba.fastjson.support.spring.JSONPResponseBodyAdvice;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;
import org.beetl.sql.core.DSTransactionManager;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.junit.Test;
import org.osgl.util.C;
import org.osgl.util.S;

import javax.jms.ObjectMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DeconstructService {

    private ObjectId objectId = ObjectId.get();
    public SQLManager sqlManager;
//    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//    private static SimpleDateFormat isoSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//    private static SimpleDateFormat iso2Sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    private static SimpleDateFormat ymdSdf = new SimpleDateFormat("yyyy-MM-dd");
    private static String[] dateFormats = {"yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd","yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss"};

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

    public DeconstructService() {
    }

    public DeconstructService(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
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
        JSONObject params = HttpServerHandler.decodeProxyQuery(request);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(key)){
                return (String) entry.getValue();
            }
        }
        throw new RuntimeException();
    }

    public static DeconstructService register(ZedService zedService) {
        DeconstructService service = new DeconstructService();
        service.sqlManager = zedService.sqlManager;

        HttpServerHandler.AddRoute(new Route(("/deconstruct"), (ctx, req) -> {
            return service.doNettyRequest(ctx, req);
        }));

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
            "+stock_statistics", object.getJSONObject("StockStatistics").toJSONString(0),
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
        child:{
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
                    "+Paths", paths.toJSONString(0),
                    "+Oper", oper.toJSONString(0),
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
        JSONObject node = (JSONObject) jsonGetByPath(json, "Node");
        changeField(
            node,
            "+inner_company_no", keyNo
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
        changeField(
            json,
            "+inner_company_no", keyNo
        );
        doDelete("QCC_COMPANY_MAP", "inner_company_no", keyNo);
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
        JSONObject node = ((JSONObject) json).getJSONObject("Node");
        changeField(
            node,
            "+inner_company_no", keyNo
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
        deconstruct(json, "QCC_HIS_SHIXIN", "");
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
            "+ChangeDateList", ChangeDateList.toJSONString(0),
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
            }, DateValue
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
            "+his_data", json.toJSONString(0)
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
                "+RegisterDate", ValueGenerator.createYmdDate("RegisterDate"),
                "+PublicDate", ValueGenerator.createYmdDate("PublicDate"),
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
                    "+REGIST_DATE", ValueGenerator.createYmdDate("REGIST_DATE"));
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
            JSONObject securedClaim = (JSONObject) jsonGetByPath(obj,"Detail.SecuredClaim");
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
                    "+CancelDate", ValueGenerator.createYmdDate("CancelDate"),
                    "+inner_company_name", compName);
                deconstruct(CancelInfo, "QCC_CMD_CANCEL_INFO", "");
            }

            //ChangeList
            JSONArray ChangeList = (JSONArray) jsonGetByPath(obj, "Detail.ChangeList");
            if (ChangeList != null) {
                changeField(ChangeList,
                    "+cm_id", id,
                    "+ChangeDate", ValueGenerator.createYmdDate("ChangeDate"),
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
            "+PunishDate", ValueGenerator.createYmdDate("PunishDate"),
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
            "+PunishDate", ValueGenerator.createYmdDate("PunishDate")
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
            "+OnBoardStartTime", ValueGenerator.createYmdDate("OnBoardStartTime"),
            "+OnBoardEndTime", ValueGenerator.createYmdDate("OnBoardEndTime"),
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
                "cn_id", id,
                "key_no", inner.getString("KeyNo"),
                "name", inner.getString("Name"),
                "type", "02"
            );
            deconstruct(obj, "JG_LM_PEOPLE_RE", "");
        }
        child2:
        {
            JSONObject inner = object.getJSONObject("MortgagorName");
            if (inner == null) {
                break child2;
            }
            JSONObject obj = newJsonObject(
                "cn_id", id,
                "key_no", inner.getString("KeyNo"),
                "name", inner.getString("Name"),
                "type", "01"
            );
            deconstruct(obj, "JG_LM_PEOPLE_RE", "");
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
            "+StartDate", ValueGenerator.createYmdDate("StartDate"),
            "+EndDate", ValueGenerator.createYmdDate("EndDate"),
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
        String compName = getQuery(request, "keyNo");
        changeField(json,
            "+RemoveDate", DateValue,
            "+AddDate", DateValue,
            "+inner_company_name", compName
        );
        doDelete("QCC_OP_EXCEPTION", "inner_company_name", compName);
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
                        "+FreezeStartDate", ValueGenerator.createYmdDate("FreezeStartDate"),
                        "+FreezeEndDate", ValueGenerator.createYmdDate("FreezeEndDate"),
                        "+PublicDate", ValueGenerator.createYmdDate("PublicDate"),
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
                        "+UnFreezeDate", ValueGenerator.createYmdDate("FreezeStartDate"),
                        "+PublicDate", ValueGenerator.createYmdDate("PublicDate"),
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
                        "+AssistExecDate", ValueGenerator.createYmdDate("FreezeStartDate"),
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
            for (String p : ((String)object.getOrDefault("Prosecutorlist", "")).split("\\t")) {
                list.add(newJsonObject(
                    "name", p,
                    "type", "01",
                    "id", object.getString("Id"),
                    "inner_company_name", compName
                ));
            }

            for (String p : ((String)object.getOrDefault("Defendantlist", "")).split("\\t")) {
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
            "LianDate", ValueGenerator.createYmdhmsDate("LianDate"),
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
            "-PublishedDate",
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
                    return key.endsWith("Date");
                }
            }, DateValue,
            "+Appellor", ValueGenerator.createStrList("Appellor"),
            "Defendantlist->DefendantList",
            "Prosecutorlist->ProsecutorList",
            "+DefendantList", ValueGenerator.createStrList("DefendantList"),
            "+ProsecutorList", ValueGenerator.createStrList("ProsecutorList"),
            "+Content", Base64Value,
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
                    "+TOTAL_NUM", jsonGetByPath(object,"CourtNoticeList.TotalNum"),
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
                    children = (JSONArray) jsonGetByPath(obj,"children");
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
        //检查是否有相同的字段
        if (S.notBlank(existKey)) {
            List<String> keys = Arrays.asList(existKey.split("\\,"));
//            if (S.empty(object.getString(existKey))) {
//                return kv;
//            }
            if (keys.size() == 0) {
                return kv;
            }
            String sql = "select count(1) from " + tableName + " where 1 = 1 ";
            StringBuilder sb = new StringBuilder();
            for (String key : keys) {
                String k = camelToUnderline(key);
                sb.append(S.fmt(" and %s = '%s'", k, object.getString(key)));
            }
            sql += sb.toString();
            List<JSONObject> ret = (sqlManager.execute(sql, JSONObject.class, C.newMap()));
            if(ret.size() > 0){
                int count = ret.get(0).getInteger("1");
                if (count > 0) {
                    sql = buildUpdateSql(tableName, kv, sb.toString());
                    sqlManager.executeUpdate(new SQLReady(sql));
                    return kv;
                }
            }
        }

        String sql = buildInsertSql(tableName, kv);
        sqlManager.executeUpdate(new SQLReady(sql));
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
        sqlManager.executeUpdate(new SQLReady(buildDeleteSql(tableName, key, value)));
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

    private String buildInsertSql(String tableName, JSONObject kv) {
        StringBuilder sb = new StringBuilder();
        kv.put("inner_id", objectId.nextId());
        kv.put("input_date", new Date());
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
        return sb.toString();
    }

    private boolean isSuccess(JSONObject object) {
        return S.eq(object.getString("Status"), "200");
    }

//    private boolean matches(String url, String pattern) {
//        pattern = "\\b" + pattern + "\\b";
//        Pattern p = urlRegexs.get(pattern);
//        if (p == null) {
//            p = Pattern.compile(pattern);
//            urlRegexs.put(pattern, p);
//        }
//        Matcher m = p.matcher(url);
//        return m.find();
//    }

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
                if(json instanceof JSONArray){
                   JSONArray array = (JSONArray) json;
                    return array.toJSONString(0);
                } else {
                    return "[]";
                }
            };
        }

        public static ValueGenerator createYmdDate(String key) {
            return kv -> convertYmdDate(kv.getString(key));
        }

        public static ValueGenerator createYmdhmsDate(String key) {
            return kv -> converYmdhmsDate(kv.getString(key));
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
                for (String dateFormat : dateFormats) {
                    try {
                        return DateUtil.parse(kv.getString(key)).toJdkDate();
                    } catch (Exception e) {
                    }
                }
                System.out.println("date format error : " + key);
                return null;
            };
        }

        public static Wrap createDate() {
            Wrap wrap = new Wrap();
            wrap.type = Wrap.TYPE_DATE;
            return wrap;
        }

        public static Wrap createBase64(){
            Wrap wrap = new Wrap();
            wrap.type = Wrap.TYPE_BASE64;
            return wrap;
        }
    }

    public interface DeconstructHandler {
        public void call(ChannelHandlerContext ctx, FullHttpRequest request, JSON json);
    }

//    public interface DeconstructArrayHandler{
//        public void call(ChannelHandlerContext ctx, FullHttpRequest request, JSONArray array);
//    }
}
