package com.beeasy.zed;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.UtilException;
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
import org.osgl.util.E;
import org.osgl.util.S;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.beeasy.zed.DBService.*;
import static com.beeasy.zed.Utils.*;
import static com.beeasy.zed.Config.config;

//import cn.hutool.json.*;

public class DeconstructService extends AbstractService {

    //日志存储
    private File logSourceDir;
    //    private File logSqlDir;
//    private File logUnzipDir;
    private File blobDir;
    //    private ReentrantLock mutex = new ReentrantLock();
//    private ExecutorService executor = Executors.newFixedThreadPool(16);
    private Map<String, Boolean> runningTask = new HashMap<>();

    private ObjectId objectId = ObjectId.get();
    //    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//    private static SimpleDateFormat isoSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//    private static SimpleDateFormat iso2Sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    private static SimpleDateFormat ymdSdf = new SimpleDateFormat("yyyy-MM-dd");
    private static String[] dateFormats = {"yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss"};

    private static ValueGenerator.Wrap DateValue = ValueGenerator.createDate();
    private static ValueGenerator.Wrap ProvinceValue = ValueGenerator.createProvince();
//    private static ValueGenerator.Wrap Base64Value = ValueGenerator.createBase64();

    public static Map<String, DeconstructHandler> handlers = new ConcurrentHashMap<>();


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

    public SqlVectors readySqls = new SqlVectors();


    public static void registerHandler(String url, DeconstructHandler handler) {
        handlers.put(url, handler);
    }


    /**
     * 字段修正
     *
     * @param objects
     * @param objects
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

    private static String getQuery(FullHttpRequest request, String key) {
        return getQuery(request, key, true);
    }

    private static String getQuery(FullHttpRequest request, String key, boolean throwError) {
        JSONObject params = HttpServerHandler.decodeQuery(request);
        if (params.containsKey(key)) {
            return params.getString(key);
        }
        if (throwError) {
            throw new RuntimeException();
        }
        return "";
    }

    @Override
    public void initSync() {
        DeconstructService service = this;
        service.logSourceDir = new File(config.log.source);
        service.blobDir = new File(config.log.blob);

        service.logSourceDir.mkdirs();
        service.blobDir.mkdirs();

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
        registerHandler("/ECIInvestmentThrough/GetInfo", service::GetInfo);
        registerHandler("/ECIInvestment/GetInvestmentList", service::GetInvestmentList);
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
        MQService.getInstance().listenMessage("queue", "qcc-deconstruct-request", m -> {
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
            if (S.empty(requestId)) return;
            //检查是否有相同的任务
            synchronized (service) {
                if (service.runningTask.containsKey(requestId)) {
                    new QccDeconstructReqponse(-1, 0, requestId, sourceRequest, "已经有相同的任务正在执行中");
                    return;
                }
                service.runningTask.put(requestId, true);
            }
            try {
                service.onDeconstructRequest(requestId, sourceRequest, (BlobMessage) m);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (service) {
                service.runningTask.remove(requestId);
            }
        });

        MQService.getInstance().listenMessage("queue", "qcc-redeconstruct-request", m -> {
            TextMessage textMessage = (TextMessage) m;
            JSONObject object = (JSONObject) JSON.parseObject(textMessage.getText());
            String requestId = object.getString("requestId");
            int progress = 0;
            try {
                progress = object.getInteger("progress");
            } finally {
                if (progress < 1) {
                    progress = 1;
                }
                if (progress > 3) {
                    progress = 3;
                }
            }
            if (S.empty(requestId)) return;
            synchronized (service) {
                if (service.runningTask.containsKey(requestId)) {
                    new QccReDeconstructResponse(-1, 0, requestId, "", "已经有相同的任务正在执行中");
                    return;
                }
                service.runningTask.put(requestId, true);
            }
            service.onReDeconstructRequest(requestId, progress);
            synchronized (service) {
                service.runningTask.remove(requestId);
            }
        });


        //文件代理提供
        HttpServerHandler.AddRoute(new Route("/file", new Route.IHandler() {
            @Override
            public Object run(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
                String fid = getQuery(request, "fid");
                File file = new File(blobDir, fid);
                if (!file.exists()) {
                    return null;
                }
                return file;
            }
        }));

        service.refreshTableDefination();

        System.out.println("deconstruct service boot success");
    }

    /**
     * 十层穿透
     *
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
     *
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
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void GetStockRelationInfo(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "companyName");
        String name = getQuery(request, "name");
        for (Map.Entry<String, Object> entry : GetStockRelationInfoMap.entrySet()) {
            JSONArray array = (JSONArray) jsonGetByPath(json, entry.getKey() + ".Result");
            if (array == null) {
                continue;
            }
            changeField(array,
                    "+inner_company_name", compName,
                    "+per_name", name
            );
            doDelete((String) entry.getValue(), new String[]{"inner_company_name", "per_name"}, new String[]{compName, name});
            deconstruct(array, (String) entry.getValue(), "");
        }
    }

    /**
     * 企业图谱
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
                "LianDate->LiAnDate",
                "+LiAnDate", DateValue
        );
        deconstruct(json, "QCC_HIS_SESSION_NOTICE", "Id");
    }

    /**
     * 历史裁判文书
     *
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


    /**
     * 历史法院公告
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
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
                "+inner_company_name", compName,
                "+Province", ProvinceValue
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
     *
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
     *
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
     *
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
                        if (S.empty(FundedRatio)) {
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
     *
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
                "+UpdatedDate", DateValue,
                "+Province", ProvinceValue
        );
        JSONObject object = (JSONObject) json;


        JSONObject kv = (JSONObject) deconstruct(json, "QCC_DETAILS", "inner_company_name");
        //曾用名
        cym:
        {
            JSONArray array = object.getJSONArray("OriginalName");
            if (array == null || array.size() == 0) {
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
            if (array == null || array.size() == 0) {
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
            JSONArray array = object.getJSONArray("Employees");
            if (array == null || array.size() == 0) {
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
            if (array == null || array.size() == 0) {
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
            if (array == null || array.size() == 0) {
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
            if (null == obj || obj.size() == 0) {
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
            if (null == obj || obj.size() == 0) {
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
     * 获取企业对外投资穿透
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    public void GetInfo(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String compName = getQuery(request, "searchKey");
        changeField(json,
//            "-PublishedDate",
//                new ICanChange() {
//                    @Override
//                    public boolean call(String key) {
//                        return key.endsWith("Date");
//                    }
//                }, DateValue,
                "+inner_company_name", compName
        );
        doDelete("QCC_GQ_CHUANTOU", "inner_company_name", compName);
        deconstruct(json, "QCC_GQ_CHUANTOU", "INNER_ID");
    }


    /**
     * 获取企业对外投资列表
     *
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    public void GetInvestmentList(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        String companyName = getQuery(request, "searchKey");
        changeField(json,
//            "-PublishedDate",
//                new ICanChange() {
//                    @Override
//                    public boolean call(String key) {
//                        return key.endsWith("Date");
//                    }
//                }, DateValue,
                "+inner_company_name", companyName
        );


        doDelete("QCC_DUIWAITOUZI", "inner_company_name", companyName);
        deconstruct(json, "QCC_DUIWAITOUZI", "");
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
                "+Id", id,
                "+Open_Time",DateValue
        );
        deconstruct(json, "QCC_COURT_NOTICE", "Id");
        //补充上诉人和被上诉人
        JSONObject object = (JSONObject) json;
        JSONArray list = new JSONArray();
        JSONArray array = object.getJSONArray("Prosecutor");
        if (null != array && array.size() > 0) {
            list.addAll(
                    array
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
        JSONArray Defendant = object.getJSONArray("Defendant");
        if (null != Defendant && Defendant.size() > 0) {
            list.addAll(
                    Defendant
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
                "+LianDate", DateValue,
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
                "+Id", getQuery(request, "id"),
                "+Province", ProvinceValue
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
                "+inner_company_name", getQuery(request, "companyName"),
                "-Content"
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
        String id = getQuery(request, "id");
        writeFile(id + "-caipan", (JSONObject) json, "ContentClear", "Content", "JudgeResult", "PartyInfo", "TrialProcedure", "CourtConsider", "PlaintiffRequest", "DefendantReply", "CourtInspect", "PlaintiffRequestOfFirst", "DefendantReplyOfFirst", "CourtInspectOfFirst", "CourtConsiderOfFirst", "AppellantRequest", "AppelleeArguing", "ExecuteProcess");
        changeField(json,
                new ICanChange() {
                    @Override
                    public boolean call(String key) {
                        return key.endsWith("Date") && !key.equals("Judege_Date");
                    }
                }, DateValue,
                "+Appellor", ValueGenerator.createStrList("Appellor"),
                "Defendantlist->DefendantList",
                "Prosecutorlist->ProsecutorList",
                "+DefendantList", ValueGenerator.createStrList("DefendantList"),
                "+ProsecutorList", ValueGenerator.createStrList("ProsecutorList"),
//            "+Content", Base64Value,
                "+Id", id
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
     * @param channelHandlerContext
     * @param request
     * @param json
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
            //null有可能为一个子结构，如果为null则直接忽略
            if (entry.getValue() == null) continue;
            kv.put(camelToUnderline(entry.getKey()), entry.getValue());
        }
        if (tableName.equals("QCC_GQ_CHUANTOU")) {
            JSONArray jsonArray = new JSONArray();
            getJsonTree(object, jsonArray, object.getString("CompanyName"), 1, "",object.getString("CompanyName"));
            kv.put("result", jsonArray.toString());
        }
        kv.put("input_date", new Date());
        kv.put("inner_id", objectId.nextId());
        JSONObject nkv = new JSONObject();
        kv.forEach((k, v) -> nkv.put(k.toUpperCase(), v));

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
                values[i] = object.getString(key);
//                values[i] = S.wrap(object.getString(key), "'");
                cKeys[i++] = camelToUnderline(key);
            }

            /**
             * super insert
             */
            readySqls.updateItems.add(new Object[]{tableName.toUpperCase(), cKeys, values, nkv});
            if (true) {
                return kv;
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
//            if (autoCommit) {
//                int row = sqlManager.executeUpdate(new SQLReady(sql));
//                if (row > 0) {
//                    return kv;
//                }
//            } else {
            readySqls.update.add(sql);
            return kv;
//            }
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

//        if (autoCommit) {
//            String sql = buildInsertSql(tableName, kv);
//            sqlManager.executeUpdate(new SQLReady(sql));
//        } else {

        readySqls.insertItems.add(new Object[]{tableName.toUpperCase(), nkv});
//        }
        return kv;
    }

    private String formatValue(JSONObject kv, String key) {
        Object object = kv.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof Date) {
            String d = DateUtil.format((Date) object, "yyyy-MM-dd hh:mm:ss");
            if (S.empty(d)) {
                return null;
            } else {
                return d;
            }
        } else {
            return kv.getString(key);
        }
    }

    public static JSONObject newJsonObeject(Object o) {
        JSONObject json1 = JSONObject.parseObject(o.toString());
        return json1;
    }

    //解析企业对外投资，并将其解为tree结构
    public static void getJsonTree(JSONObject json, JSONArray arrass, String parentName, int leve, String path,String dingjiParent) {
        if (leve > 5) return;
        JSONArray jsonArray = json.getJSONArray("BreakThroughList");
        for (Object o : jsonArray) {
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            JSONArray array = jsonObject.getJSONArray("DetailInfoList");
            for (Object itme : array) {
                JSONObject json1 = newJsonObeject(itme);
                String Level = json1.getString("Level");
                String jiequAry = json1.getString("Path").replaceAll("\\([\\d|.]+%\\)->", "");
                String[] jiexiStr = json1.getString("Path").split("\\([\\d|.]+%\\)->");
                if (jiequAry.indexOf(path) > -1 && leve == Integer.parseInt(Level) && jiexiStr[leve - 1].equals(parentName)) {
                    JSONObject object = new JSONObject();
                    JSONArray array1 = new JSONArray();
                    object.put("isHold", getHoldIsBoolean(jiexiStr[leve], dingjiParent));
                    object.put("Name", jiexiStr[leve]);
                    object.put("KeyNo", jsonObject.getString("KeyNo"));
                    object.put("ajaxtouzi", true);
                    object.put("StockType", json1.getString("StockType"));
                    object.put("StockPercent", json1.getString("StockPercent"));
                    object.put("Level", json1.getString("Level"));
                    object.put("BreakThroughStockPercent", json1.getString("BreakThroughStockPercent"));
                    object.put("CapitalType", json1.getString("CapitalType"));
                    object.put("ShouldCapi", json1.getString("ShouldCapi"));
                    object.put("Percent", json1.getString("StockPercent"));
                    object.put("PercentTotal", json1.getString("StockPercent"));
                    object.put("DetailList", array1);
                    getJsonTree(json, array1, jiexiStr[leve], leve + 1, path,dingjiParent);
                    arrass.add(object);
                }
            }
        }
    }

    private static int holdInt = 1;
    public volatile static Map hashMap = new HashMap();
    public static Boolean getHoldIsBoolean(String name, String parentName) {
        //获取是否控股公司
        if(holdInt == 1) {
            String sql = "select name,inner_company_name  from qcc_holding_company_names";
            List<JSONObject> list = sqlManager.execute(new SQLReady(sql), com.alibaba.fastjson.JSONObject.class);
            for (int i = 0; i < list.size(); i++) {
                hashMap.put(list.get(i).getString("name"), list.get(i).getString("innerCompanyName"));
            }
            holdInt +=1;
        }
        try {
            return hashMap.get(name).equals(parentName);
        } catch (Exception e) {
            return false;
        }
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
            if (null != App.concurrentMapWordCounts.get(value) && App.concurrentMapWordCounts.get(value).equals("")) {
                value = App.concurrentMapWordCounts.get(value);
            }
            sb.append(S.fmt("'%s'", value));
        }
        sb.append(",");
    }

    private void doDelete(String tableName, String[] keys, String[] values) {
//        if(autoCommit) {
//
//        } else {
        readySqls.addItem(tableName, keys, values);
//        }
    }

    private void doDelete(String tableName, String key, String value) {
//        if (autoCommit) {
//            String sql = buildDeleteSql(tableName, key, value);
//            sqlManager.executeUpdate(new SQLReady(sql));
//        } else {
//            readySqls.delete.add(sql);
//            readySqls.deleteItems.add(new SqlVectors.DeleteItem(tableName, key, value));
        readySqls.addItem(tableName, key, value);
//        }
    }

    private String buildDeleteSql(String tableName, String key, String value) {
        if (key.contains(",")) {
            String[] keys = key.split(",");
            String[] values = value.split(",");
            String sql = String.format("delete from %s where 1 = 1", tableName);
            int i = 0;
            for (String s : keys) {
                sql += S.fmt(" and %s = '%s'", s, values[i++]);
            }
            return sql;
        } else {
            return S.fmt("delete from %s where %s = '%s'", tableName, key, value);
        }
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
     *
     * @param inputStream
     * @param requestId
     * @throws Exception
     */
    private void deconstructStep0(InputStream inputStream, String requestId) throws Exception {
        File sourcefile = new File(logSourceDir, requestId);
        if (sourcefile.exists()) sourcefile.delete();
        try (
                InputStream is = inputStream;
                FileOutputStream fos = new FileOutputStream(sourcefile);
        ) {
            IoUtil.copy(is, fos);
        } catch (Exception e) {
            throw new Exception();
        }
    }


    /**
     * step2
     * 分析文件
     * 因为sqlready只用了一个容器，以及需要executor线程池处理，所以这里只能使用同步
     * 因为内存足够，所以这里以空间换时间，加大解析速度
     *
     * @param requestId
     */
    private synchronized SqlVectors deconstructStep2(String requestId, InputStream is, AtomicBoolean someError) throws IOException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(16);
//        File unzipFile = new File(logUnzipDir, requestId);
//        List<Slice> tasks = new ArrayList<>();
        final AtomicBoolean hasError = new AtomicBoolean(false);
        readySqls = new SqlVectors();

        try (
                InputStream fis = is;
                ZipInputStream zip = new ZipInputStream(fis);
        ) {
            //暂时只允许一个文件
            ZipEntry entry = zip.getNextEntry();
            byte[] bytes = IoUtil.readBytes(zip);

            //写入日志
            ThreadUtil.execAsync(() -> {
                try {
                    IoUtil.write(new FileOutputStream(new File(logSourceDir, requestId)), true, bytes);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });

            ByteBuf buf = Unpooled.wrappedBuffer(bytes);
//            byte[] buf = new byte[1024];
//            int num = -1;
//            while ((num = zip.read(buf, 0, buf.length)) != -1) {
//                fos.write(buf, 0, num);
//            }
//            IoUtil.copy(zip, new FileOutputStream("d:/test"));
//                System.exit(-1);

            int pos = 0;
            int len = -1;
            while (pos < bytes.length) {
                len = buf.getInt(pos);
                pos += 4;
                String str = buf.getCharSequence(pos, len, StandardCharsets.UTF_8).toString();
                pos += len;
                executor.submit(() -> {
                    try {
                        destructSingle(str, someError);
                    } catch (Exception e) {
                        e.printStackTrace();
                        hasError.set(true);
                    } finally {
                    }
                });
            }

            buf.release();
        }
//        try {
//            raf = new RandomAccessFile(unzipFile, "r");
//            channel = raf.getChannel();
//            FileChannel finalChannel = channel;
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

//        if (raf != null) {
//            try{
//                raf.close();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        if (channel != null) {
//            try{
//                channel.close();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }


        if (hasError.get()) {
            throw new RuntimeException();
        }

//        List<String> sqls = new ArrayList<>();
//        Map<String,Object[]> map = new HashMap<>();
//        for (SqlVectors.DeleteItem deleteItem : readySqls.deleteItems) {
//            Object[] t = map.get(deleteItem.table);
//            if(t == null){
//                t = new Object[3];
//                map.put(deleteItem.table, t);
//                t[0] = deleteItem.table;
//                t[1] = deleteItem.key;
//                t[2] = new HashSet<>();
//            }
//            HashSet set = (HashSet) t[2];
//            set.add("'" + deleteItem.value + "'");
//        }

//        readySqls.deleteItems.forEach((k,v) -> {
//            sqls.add(
//                String.format("delete from %s where %s in (%s)", v[0], v[1], String.join(",", (Iterable<? extends CharSequence>) v[2]))
//            );
//        });
//        sqls.addAll(readySqls.delete);
//        sqls.addAll(readySqls.insert);
//        sqls.addAll(readySqls.update);

//        return sqls;
        return readySqls;
    }

    private void buildInsertBatch(Map<Object, PreparedStatement> cache, StringBuilder sb, Connection conn, String tableName, JSONObject kv) throws SQLException {
        PreparedStatement p = cache.get(tableName);
        Table table = tables.get(tableName);
        if (p == null) {
            sb.setLength(0);
            sb.append("insert into ");
            sb.append(tableName);
            sb.append("(");
            for (String column : table.columns) {
                sb.append(column);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")values(");
            for (String column : table.columns) {
                sb.append("?,");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            String sql = sb.toString();
            p = conn.prepareStatement(sql);
            cache.put(tableName, p);
        }

        int i = 0;
        for (String column : table.columns) {
            p.setObject(++i, null);
        }

        for (Map.Entry<String, Object> entry : kv.entrySet()) {
            int idex = table.columns.indexOf(entry.getKey());
            if (idex > -1) {
                String type = table.types.get(idex);
                switch (type) {
                    case "TIMESTAMP":
                        if (entry.getValue() instanceof Date) {
                            p.setObject(idex + 1, entry.getValue());
                        }
                        break;

                    case "DECIMAL":
                        String value = String.valueOf(entry.getValue());
                        value = value.replaceAll("万", "0000");
                        value = value.replaceAll("人民币元", "");
                        p.setBigDecimal(idex + 1, new BigDecimal(value));
                        break;

                    default:
                        if (entry.getValue() instanceof JSONArray || entry.getValue() instanceof JSONObject) {
                            break;
                        }
                        if (tableName.contains("QCC_TREE_RELATION_MAP") && entry.getValue().equals("企业法人")) {
                            int d = 1;
                        }
                        if (entry.getValue() instanceof Date) {
                            p.setString(idex + 1, DateUtil.format((Date)entry.getValue(), "yyyy-MM-dd hh:mm:ss"));
                            break;
                        }
                        p.setObject(idex + 1, entry.getValue());
                        break;
                }
            }
        }
        p.addBatch();

//            p.executeUpdate();
    }

    /**
     * step3
     * 更新到数据库
     *
     * @param sqlVectors
     */
    public void deconstructStep3(SqlVectors sqlVectors) throws SQLException {
        List<PreparedStatement> sqldebug = new ArrayList<>();
//        List<JSONObject> sqldebug2 = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
        ) {
            conn.setAutoCommit(false);
//            stmt = conn.createStatement();
            long stime = System.currentTimeMillis();

            StringBuilder sb = new StringBuilder();
            Map<Object, PreparedStatement> insertCache = new HashMap<>();
            Map<Object, PreparedStatement> deleteCache = new HashMap<>();

            for (Object[] deleteItem : sqlVectors.deleteItems) {
                if ((boolean) deleteItem[0]) {
                    //use 2 params
                    PreparedStatement p = deleteCache.get(deleteItem[1]);
                    if (p == null) {
                        sb.setLength(0);
                        sb.append("delete from ");
                        sb.append(deleteItem[1]);
                        sb.append(" where ");
                        sb.append(((String[]) deleteItem[2])[0]);
                        sb.append(" = ? and ");
                        sb.append(((String[]) deleteItem[2])[1]);
                        sb.append(" = ?");
//                        String sql = String.format("delete from %s where %s = ? and %s = ?", deleteItem[1], ((String[])deleteItem[2])[0], ((String[])deleteItem[2])[1]);
                        p = conn.prepareStatement(sb.toString());
                        deleteCache.put(deleteItem[1], p);
                    }
                    p.setString(1, ((String[]) deleteItem[3])[0]);
                    p.setString(2, ((String[]) deleteItem[3])[1]);
                    p.addBatch();
                } else {
                    PreparedStatement p = deleteCache.get(deleteItem[1]);
                    if (p == null) {
                        sb.setLength(0);
                        sb.append("delete from ");
                        sb.append(deleteItem[1]);
                        sb.append(" where ");
                        sb.append(deleteItem[2]);
                        sb.append(" = ?");
//                        String sql = String.format("delete from %s where %s = ?", deleteItem[1], deleteItem[2]);
                        p = conn.prepareStatement(sb.toString());
                        deleteCache.put(deleteItem[1], p);
                    }
                    p.setString(1, (String) deleteItem[3]);
                    p.addBatch();
                }
            }


            //deal insert
            for (Object[] insertItem : sqlVectors.insertItems) {
                buildInsertBatch(insertCache, sb, conn, (String) insertItem[0], (JSONObject) insertItem[1]);
            }


            //update
            Map<String, Object[]> updateCache = new HashMap<>();
            //整理
            for (Object[] updateItem : sqlVectors.updateItems) {
                String tableName = (String) updateItem[0];
                String[] keys = (String[]) updateItem[1];
                String[] values = (String[]) updateItem[2];
                JSONObject kv = (JSONObject) updateItem[3];
                //如果有这个，就更新她
                String unique = tableName + "," + String.join(",", values);
                Object[] objects = updateCache.get(unique);
                if (objects == null) {
                    objects = new Object[]{tableName, keys, values, new JSONObject()};
                    updateCache.put(unique, objects);
                }
                JSONObject nkv = (JSONObject) objects[3];
                if (nkv == null) {
                    nkv = kv;
                    objects[3] = nkv;
                } else {
                    JSONObject finalNkv = nkv;
                    kv.forEach((k, v) -> {
                        if(v instanceof String){
                            if (S.isNotEmpty((String)v) && S.isEmpty(finalNkv.getString(k))) {
                                finalNkv.put(k, v);
                            }
                        } else {
                            if(null != v && finalNkv.get(k) == null){
                                finalNkv.put(k,v);
                            }
                        }
                    });
//                    nkv.putAll(kv);
                }
            }

            //入库
            for (Object[] vs : updateCache.values()) {
                String tableName = (String) vs[0];
                String[] keys = (String[]) vs[1];
                String[] values = (String[]) vs[2];
                JSONObject kv = (JSONObject) vs[3];
                //做出delete语句
                PreparedStatement p = deleteCache.get(tableName);
                if (p == null) {
                    sb.setLength(0);
                    sb.append("delete from ");
                    sb.append(tableName);
                    sb.append(" where 1 = 1 ");
                    for (String key : keys) {
                        sb.append(" and ");
                        sb.append(key);
                        sb.append(" = ? ");
                    }
                    p = conn.prepareStatement(sb.toString());
//                    System.out.println(sb.toString());
//                    System.out.println(JSON.toJSONString(values));
                    deleteCache.put(tableName, p);
                }
                int i = 0;
                for (String value : values) {
                    p.setString(++i, value);
                }
//                p.executeUpdate();
                p.addBatch();

                //做出insert语句
                buildInsertBatch(insertCache, sb, conn, tableName, kv);
            }


            for (Map.Entry<Object, PreparedStatement> entry : deleteCache.entrySet()) {
                entry.getValue().executeBatch();
                IoUtil.closeIfPosible(entry.getValue());
            }
            System.out.printf("delete takes %d ms \n", System.currentTimeMillis() - stime);
            stime = System.currentTimeMillis();

            for (Map.Entry<Object, PreparedStatement> entry : insertCache.entrySet()) {
                try {
                    entry.getValue().executeBatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                IoUtil.closeIfPosible(entry.getValue());
            }
            System.out.printf("insert takes %d ms\n", System.currentTimeMillis() - stime);
            try {
                conn.commit();
            }catch (Exception e){
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * step4
     * <p>
     * 写入sql日志
     *
     * @param requestId
     * @param sqls
     */
    @Deprecated
    private void deconstructStep4(String requestId, List<String> sqls) {
        ThreadUtil.execAsync(() -> {
            File sqlfile = new File("", requestId);
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
//                    raf.write("go\r\n".getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static class Table {
        String name;
        List<String> columns = new ArrayList<>();
        List<Integer> lengths = new ArrayList<>();
        List<String> types = new ArrayList<>();

    }

    private Map<String, Table> tables = new HashMap<>();

    private void refreshTableDefination() {
        String sql = "select table_name as t, column_name as c, data_type as type, CHARACTER_MAXIMUM_LENGTH as len from sysibm.columns where table_schema = 'DB2INST1' and TABLE_NAME like 'QCC_%'";
        List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
        //分析定义
        for (JSONObject object : list) {
            String tname = object.getString("t");
            Table table = tables.get(tname);
            if (table == null) {
                table = new Table();
                table.name = tname;
                tables.put(tname, table);
            }
            table.columns.add(object.getString("c"));
            table.lengths.add(object.getInteger("len"));
            table.types.add(object.getString("type"));
        }
    }

    @Deprecated
    public void onReDeconstructRequest(String requestId, int progress) {
        if (progress < 1 || progress > 3) {
            return;
        }

//        autoCommit = false;
        QccReDeconstructResponse reqponse = new QccReDeconstructResponse(-1, 0, requestId, "", "");
        AtomicBoolean someError = new AtomicBoolean(false);
        try {
            List<String> sqls = null;
            //从第一步开始解
            if (progress <= 1) {
                reqponse.progress = 1;
//                deconstructStep1(requestId);
            }
            if (progress <= 2) {
                reqponse.progress = 2;
                deconstructStep2(requestId, null, someError);
            }

            reqponse.progress = 3;
            if (progress <= 2) {
                deconstructStep3(null);
                deconstructStep4(requestId, sqls);
            } else {
                //如果从第三步，可以直接读出sql的文件
                File file = new File("", requestId);
                sqls = new ArrayList<>();
                ByteBuf buf = Unpooled.buffer();
                try (
                        RandomAccessFile raf = new RandomAccessFile(file, "r");
                ) {
                    long ptr = -1;
                    long len = raf.length();
                    while (++ptr < len) {
                        byte b = raf.readByte();
                        buf.writeByte(b);
                        if (b == '\r') {
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
                deconstructStep3(null);
            }
            if (someError.get()) {
                reqponse.finished = 1;
            } else {
                reqponse.finished = 0;
            }
        } catch (Exception e) {
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
     * @apiParam {string="source","unzip","sql"} type 数据类型，分别是“解析前的原始数据”，“解压后的原始数据”,“分析后的sql数据”
     * @apiParam {string} id 数据requestId
     */
    public void onDeconstructRequest(String requestId, String sourceRequest, BlobMessage blobMessage) throws IOException, JMSException {
        onDeconstructRequest(requestId, sourceRequest, blobMessage.getInputStream());
    }

    public void onDeconstructRequest(String requestId, String sourceRequest, InputStream is) {
//        System.out.println("开始解析");
        long stime = System.currentTimeMillis();
//        autoCommit = false;
        QccDeconstructReqponse reqponse = new QccDeconstructReqponse(-1, 0, requestId, sourceRequest, "");
        AtomicBoolean someError = new AtomicBoolean(false);
        try {
//            deconstructStep0(is, requestId);
//            reqponse.progress = 1;
//            deconstructStep1(requestId);
            reqponse.progress = 2;
            //直接进行解压并解析
            SqlVectors sqls = deconstructStep2(requestId, is, someError);
            reqponse.progress = 3;
            System.out.printf("解析完成, 时间%d\n", System.currentTimeMillis() - stime);
//            deconstructStep4(requestId, sqls);
            deconstructStep3(sqls);
            if (someError.get()) {
                reqponse.finished = 1;
            } else {
                reqponse.finished = 0;
            }
            System.out.printf("入库完成, 时间%d\n", System.currentTimeMillis() - stime);
        } catch (Exception e) {
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

    public void destructSingle(String str, AtomicBoolean someError) {
        JSONObject object = JSON.parseObject(str);
        String url = object.getString("FullLink");
        String key = DeconstructService.getPath(url);
        if (handlers.containsKey(key)) {
            DeconstructService.DeconstructHandler handler = handlers.get(key);
            ByteBuf buf = Unpooled.buffer();
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, buf, headers, headers);
            JSONObject od = (object.getJSONObject("OriginData"));
            if (S.eq(od.getString("Status"), "200")) {
                handler.call(null, request, (JSON) od.get("Result"));
            } else if (!S.startsWith(od.getString("Status"), "2")) {
                someError.set(true);
            }
        }
//        for (Map.Entry<String, DeconstructService.DeconstructHandler> entry : DeconstructService.handlers.entrySet()) {
//            if (HttpServerHandler.matches(url, entry.getKey())) {
//
//            }
//        }
    }

    public static String getPath(String uriStr) {
        URL uri = null;

        try {
            uri = new URL(uriStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return uri.getPath();
    }

    private void writeFile(String fileName, JSONObject object, String... fields) {
        JSONObject obj = new JSONObject();
        for (String field : fields) {
            obj.put(field, object.getString(field));
            object.remove(field);
        }
        File file = new File(blobDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            IoUtil.write(fos, true, obj.toJSONString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            private static final int TYPE_PROVINCE = 811;
            public int type;

            public Object call(JSONObject kv, String field) {
                ValueGenerator vg = null;
                switch (type) {
                    case TYPE_PROVINCE:
                        vg = new ValueGenerator() {
                            @Override
                            public Object call(JSONObject kv) {
                                return Version.convertProvince(kv.getString(field));
                            }
                        };
                        break;
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
                if (((String) value).charAt(0) == 160) {
                    value = ((String) value).substring(1);
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

        public static Wrap createProvince() {
            Wrap wrap = new Wrap();
            wrap.type = Wrap.TYPE_PROVINCE;
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
        public List<String> insert = new Vector<>();
        public List<String> update = new Vector<>();
        public List<String> delete = new Vector<>();
        //        private Map<String, Object[]> deleteItems  = new HashMap<>();
        public Vector<Object[]> deleteItems = new Vector<>();
        public Vector<Object[]> insertItems = new Vector<>();
        public Vector<Object[]> updateItems = new Vector<>();

        public void addItem(String table, String key, String value) {
            deleteItems.add(new Object[]{false, table, key, value});
        }

        public void addItem(String table, String[] key, String[] value) {
            deleteItems.add(new Object[]{true, table, key, value});
//            Object[] t = deleteItems.get(table);
//            if(t == null){
//                t = new Object[3];
//                deleteItems.put(table, t);
//                t[0] = table;
//                t[1] = key;
//                t[2] = new HashSet<>();
//            }
//            HashSet set = (HashSet) t[2];
//            set.add("'" + value + "'");
        }

//        public static class DeleteItem{
//            public String table;
//            public String key;
//            public String value;
//
//            public DeleteItem(String table, String key, String values) {
//                this.table = table;
//                this.key = key;
//                this.value = values;
//            }
//        }

    }


//    public static class Slice {
//        String link;
//        long bodyStart;
//        int bodyLen;
//        String body;
//
//        public Slice(String link, long bodyStart, int bodyLen, String body) {
//            this.link = link;
//            this.bodyStart = bodyStart;
//            this.bodyLen = bodyLen;
//            this.body = body;
//        }
//    }


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

        public void send() {
            MQService.getInstance().sendMessage("queue", "qcc-deconstruct-response", this.toString());
        }
    }

    public static class QccReDeconstructResponse extends QccDeconstructReqponse {
        public QccReDeconstructResponse(int finished, int step, String requestId, String sourceRequest, String errorMessage) {
            super(finished, step, requestId, sourceRequest, errorMessage);
        }

        @Override
        public void send() {
            MQService.getInstance().sendMessage("queue", "qcc-redeconstruct-response", this.toString());
        }
    }


//    public interface DeconstructArrayHandler{
//        public void call(ChannelHandlerContext ctx, FullHttpRequest request, JSONArray array);
//    }
}
