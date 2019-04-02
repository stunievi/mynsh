package com.beeasy.zed;

//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;
import org.beetl.sql.core.DSTransactionManager;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.osgl.util.C;
import org.osgl.util.S;

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
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static SimpleDateFormat isoSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static SimpleDateFormat ymdSdf = new SimpleDateFormat("yyyy-MM-dd");
    private static Map<String, Pattern> urlRegexs = new HashMap<>();
    private static Map<String, DeconstructHandler> handlers = new HashMap<>();

    public DeconstructService(){}
    public DeconstructService(SQLManager sqlManager){
        this.sqlManager = sqlManager;
    }



    public static void registerHandler(String url, DeconstructHandler handler){
        handlers.put(url, handler);
    }

    public Object doNettyRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            if (!request.method().equals(HttpMethod.POST)) {
                return null;
            }
            String jsonstr = request.content().toString(CharsetUtil.UTF_8);
            JSONObject json = JSONUtil.parseObj(jsonstr);
            if (!isSuccess(json)) {
                return null;
            }
            DSTransactionManager.start();
            String url = request.headers().getAsString("Proxy-Url");
            for (Map.Entry<String, DeconstructHandler> entry : handlers.entrySet()) {
                if(matches(url, entry.getKey())){
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
    private void changeField(Object... objects) {
        if (objects.length < 1) {
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
                changeField(changeList.toArray(new Object[changeList.size()]));
            }
        } else if (json instanceof JSONObject) {
            for (short i = 0; i < changeList.size(); i++) {
                Object o = changeList.get(i);
                if (!(o instanceof String)) {
                    continue;
                }
                String changeType = (String) o;
                if ((changeType).startsWith("+")) {
                    Object value = changeList.get(++i);
                    if (value instanceof ValueGenerator) {
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

    private String getQuery(FullHttpRequest request, String key) {
        JSONObject params = HttpServerHandler.decodeProxyQuery(request);
        if (!params.containsKey(key)) {
            throw new RuntimeException();
        }
        return params.getStr(key);
    }

    public static DeconstructService register(ZedService zedService){
        DeconstructService service = new DeconstructService();
        service.sqlManager = zedService.sqlManager;

        HttpServerHandler.AddRoute(new Route(Pattern.compile("^\\/deconstruct"), (ctx, req) -> {
            return service.doNettyRequest(ctx, req);
        }));

        registerHandler("SearchShiXin", service::SearchShiXin);
        registerHandler("SearchZhiXing", service::SearchZhiXing);
        registerHandler("SearchJudgmentDoc", service::SearchJudgmentDoc);
        registerHandler("GetJudgementDetail", service::GetJudgementDetail);
        registerHandler("SearchCourtAnnouncement", service::SearchCourtAnnouncement);
        registerHandler("SearchCourtAnnouncementDetail", service::SearchCourtAnnouncementDetail);
        registerHandler("SearchCourtNotice", service::SearchCourtNotice);
        registerHandler("GetCourtNoticeInfo", service::GetCourtNoticeInfo);

        return service;
    }

    /**
     * 开庭公告详情
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
        if(object.containsKey("Prosecutor")){
            list.addAll(JSONUtil.parseArray(object.getJSONArray("Prosecutor").toList(JSONObject.class)
                .stream()
                .map(o -> {
                    JSONObject kv = new JSONObject();
                    kv.put("Name", kv.getStr("Name"));
                    kv.put("cn_id", id);
                    kv.put("key_no", kv.getStr("KeyNo"));
                    kv.put("type", "01");
                    return kv;
                })
                .toArray()
            ));
        }
        if(object.containsKey("Defendant")){
            list.addAll(JSONUtil.parseArray(object.getJSONArray("Defendant").toList(JSONObject.class)
                .stream()
                .map(o -> {
                    JSONObject kv = new JSONObject();
                    kv.put("Name", kv.getStr("Name"));
                    kv.put("cn_id", id);
                    kv.put("key_no", kv.getStr("KeyNo"));
                    kv.put("type", "02");
                    return kv;
                })
                .toArray()
            ));
        }

        if(list.size() > 0){
            doDelete("JG_NOTICE_PEOPLE_RE", "cn_id", object.getStr("Id"));
            deconstruct(list, "JG_NOTICE_PEOPLE_RE", "");
        }

    }

    /**
     * 开庭公告列表
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void SearchCourtNotice(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        changeField(json,
            "Defendantlist->Defendant_list",
            "Executegov->Execute_gov",
            "Prosecutorlist->Prosecutor_list",
            "LianDate", ValueGenerator.createYmdhmsDate("LianDate"),
            "LianDate->Li_an_Date",
            "+company_name", getQuery(request, "searchKey")
            );
        deconstruct(json, "QCC_COURT_NOTICE", "Id");
    }

    /**
     * 法院公告详情
     * @param channelHandlerContext
     * @param request
     * @param json
     */
    private void SearchCourtAnnouncementDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSON json) {
        changeField(json,
            "-PublishDate",
            "+Id", getQuery(request, "id")
            );
        deconstruct(json, "QCC_COURT_ANNOUNCEMENT", "Id");
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
            "-PublishedDate"
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
            "+SubmitDate", ValueGenerator.createYmdhmsDate("SubmitDate"),
            "+UpdateDate", ValueGenerator.createYmdhmsDate("UpdateDate"),
            "+CreateDate", ValueGenerator.createYmdhmsDate("CreateDate"),
            "+JudgeDate", ValueGenerator.createYmdhmsDate("JudgeDate")
        );
        deconstruct(json, "QCC_JUDGMENT_DOC", "Id");

        //解构子表
        JSONObject object = (JSONObject) json;
        if(object.containsKey("CourtNoticeList")){
            //清空关联信息
            doDelete("JG_JD_NOTICE_RE", "CN_ID", object.getStr("Id"));
            JSONArray array = (JSONArray) object.getByPath("CourtNoticeList.CourtNoticeInfo");
            array = JSONUtil.parseArray(array.stream()
                .map(i -> {
                    JSONObject kv = (JSONObject) i;
                    JSONObject ret = new JSONObject();
                    ret.put("CN_ID", object.getStr("Id"));
                    ret.put("QCC_ID", kv.getStr("Id"));
                    return ret;
                })
                .toArray()
            );
            deconstruct(array, "JG_JD_NOTICE_RE", "");
        }

        if(object.containsKey("RelatedCompanies")){
            doDelete("JG_JD_COM_RE", "JD_ID", object.getStr("Id"));
            JSONArray array = object.getJSONArray("RelatedCompanies");
            array = JSONUtil.parseArray(
                array.stream()
                .map(i -> {
                    JSONObject kv = (JSONObject) i;
                    JSONObject ret = new JSONObject();
                    ret.put("jd_id", object.getStr("Id"));
                    ret.put("key_no", kv.getStr("KeyNo"));
                    ret.put("name", kv.getStr("Name"));
                    return ret;
                })
                .toArray()
            );
            deconstruct(array, "JG_JD_COM_RE", "");
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
            "+SubmitDate", ValueGenerator.createYmdhmsDate("SubmitDate"),
            "+UpdateDate", ValueGenerator.createYmdhmsDate("UpdateDate"),
            "+company_name", getQuery(request, "searchKey")
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
            "+Liandate", ValueGenerator.createIsoDate("Liandate"),
            "+Publicdate", ValueGenerator.createYmdDate("Publicdate"),
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
            "+company_name", getQuery(request, "searchKey")
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
            "+Liandate", ValueGenerator.createIsoDate("Liandate"),
            "Sourceid->Source_id",
            "Liandate->Li_an_date",
            "Sourceid->Source_id",
            "Anno->An_no",
            "Biaodi->Biao_di",
            "Updatedate->Update_date",
            "+company_name", getQuery(request, "searchKey")
        );
        deconstruct(array, "QCC_ZHIXING", "Id");
    }


    /**
     * 土地拍卖详情
     *
     * @param ctx
     * @param request
     * @param array
     */
    private void deGetLandMortgageDetails(ChannelHandlerContext ctx, FullHttpRequest request, JSONObject array) {
        changeField(array, C.newList(
            "+Id", getQuery(request, "id"),
            "-MortgagePeople,MortgagorName"
        ));
        deconstruct(array, "QCC_LAND_MORTGAGE", "Id");
    }

    /**
     * 土地拍卖列表
     *
     * @param ctx
     * @param request
     * @param array
     */
    private void deGetLandMortgageList(ChannelHandlerContext ctx, FullHttpRequest request, JSONArray array) {
        changeField(array, C.newList(
            "+company_name", getQuery(request, "keyWord")
        ));
        deconstruct(array, "QCC_LAND_MORTGAGE", "Id");
    }

    /**
     * 司法拍卖列表
     *
     * @param ctx
     * @param request
     * @param array
     */
    private void deGetJudicialSaleList(ChannelHandlerContext ctx, FullHttpRequest request, JSONArray array) {
        changeField(array, C.newList(
            "+company_name", getQuery(request, "keyWord")
        ));
        deconstruct(array, "QCC_JUDICIAL_SALE", "Id");
    }


    /**
     * 司法拍卖详情
     *
     * @param ctx
     * @param request
     * @param array
     */
    private void deGetJudicialSaleDetail(ChannelHandlerContext ctx, FullHttpRequest request, JSONObject object) {
        changeField(object, C.newList(
            "+Id", getQuery(request, "id")
        ));
        deconstruct(object, "QCC_JUDICIAL_SALE", "Id");
    }

    /**
     * 经营异常
     *
     * @param ctx
     * @param request
     * @param json
     */
    private void deGetOpException(ChannelHandlerContext ctx, FullHttpRequest request, JSONArray array) {
        String companyName = getQuery(request, "keyNo");
        doDelete("QCC_OP_EXCEPTION", "company_name", companyName);
        changeField(array, C.newList(
            "+COMPANY_NAME", companyName
        ));
        deconstruct(array, "QCC_OP_EXCEPTION", "");
    }

    /**
     * 司法协助
     *
     * @param ctx
     * @param request
     * @param json
     */
    private void deGetJudicialAssistance(ChannelHandlerContext ctx, FullHttpRequest request, JSONArray array) {
//        JSONArray array = json.getJSONArray("Result");
        //补充公司名
        String companyName = getQuery(request, "keyWord");
        for (Object object : array) {
            JSONObject kv = (JSONObject) object;
            kv.put("COMPANY_NAME", companyName);
            kv.remove("EquityFreezeDetail");
            kv.remove("EquityUnFreezeDetail");
            kv.remove("JudicialPartnersChangeDetail");
        }
        deconstruct(array, "QCC_JUDICIAL_ASSISTANCE", "COMPANY_NAME");
    }

    /**
     * 法院公告详情
     *
     * @param ctx
     * @param request
     * @param object
     */
    private void deSearchCourtAnnouncementDetail(ChannelHandlerContext ctx, FullHttpRequest request, JSONObject object) {
        //详情不给ID真逗B
        object.remove("PublishDate");
        object.put("Id", getQuery(request, "id"));
        deconstruct(object, "QCC_COURT_ANNOUNCEMENT", "Id");
    }

    /**
     * 开庭公告列表
     * 该表过于逗B，字段格式以详情为准
     *
     * @param ctx
     * @param request
     * @param json
     */
    private void deSearchCourtNotice(ChannelHandlerContext ctx, FullHttpRequest request, JSONArray array) {
        changeField(array, C.newList(
            "Executegov->ExecuteGov"
        ));
//        for (Object object : array) {
//            JSONObject obj = (JSONObject) object;
//            String value = obj.getStr("Executegov");
//            obj.remove("Executegov");
//            obj.put("ExecuteGov", value);
//        }
        deconstruct(array, "QCC_COURT_NOTICE", "Id");
    }

    /**
     * 开庭公告详情
     *
     * @param ctx
     * @param request
     * @param json
     */
    private void deGetCourtNoticeInfo(ChannelHandlerContext ctx, FullHttpRequest request, JSONObject object) {
        object.remove("PublishDate");
        JSONObject params = HttpServerHandler.decodeProxyQuery(request);
        if (!params.containsKey("id")) {
            return;
        }
        object.put("Id", params.get("id"));
        deconstruct(object, "QCC_COURT_NOTICE", "Id");
    }

    private JSON deconstruct(JSON obj, String tableName, String existKey) {
        StringBuilder sb = new StringBuilder();
        Date now = new Date();
        if (obj instanceof JSONObject) {
            return insertSingle((JSONObject) obj, tableName, existKey);
        } else if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            JSONArray ret = new JSONArray();
            for (Object object : array) {
                ret.add(insertSingle((JSONObject) object, tableName, existKey));
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
        if (S.notEmpty(existKey)) {
            if (S.empty(object.getStr(existKey))) {
                return kv;
            }
            String sql = S.fmt("select count(1) from %s where %s = #%s#", tableName, camelToUnderline(existKey), camelToUnderline(existKey));
            JSONArray ret = JSONUtil.parseArray(sqlManager.execute(sql, JSONObject.class, C.newMap(camelToUnderline(existKey), object.getStr(existKey))));
            Integer count = ret.getByPath("0.1", Integer.class);
            if (count != null && count > 0) {
                sql = buildUpdateSql(tableName, kv, existKey, object.getStr(existKey));
                sqlManager.executeUpdate(new SQLReady(sql));
                return kv;
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
            sb.append(S.fmt("'%s'", sdf.format(object)));
        } else if (object instanceof JSONArray || object instanceof JSONObject) {
            return;
        } else {
            String value = kv.getStr(s);
            sb.append(S.fmt("'%s'", value));
//            sb.append("#");
//            sb.append(s);
//            sb.append("#");
        }
        sb.append(",");
    }

    private void doDelete(String tableName, String key, String value) {
        sqlManager.executeUpdate(new SQLReady(buildDeleteSql(tableName, key, value)));
    }

    private String buildDeleteSql(String tableName, String key, String value) {
        return S.fmt("delete from %s where %s = '%s'", tableName, key, value);
    }

    private String buildUpdateSql(String tableName, JSONObject kv, String existKey, String value) {
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
        sb.append(S.fmt(" where %s = '%s'", existKey, value));
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
            if(value instanceof JSONArray || value instanceof JSONObject){
                continue;
            }
            sb.append(s);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")values(");
        for (String s : kv.keySet()) {
            Object value = kv.get(s);
            if(value instanceof JSONArray || value instanceof JSONObject){
                continue;
            }
            formatValue(sb, kv, s);
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    private boolean isSuccess(JSONObject object) {
        return S.eq(object.getStr("Status"), "200");
    }

    private boolean matches(String url, String pattern) {
        pattern = "\\b" + pattern + "\\b";
        Pattern p = urlRegexs.get(pattern);
        if (p == null) {
            p = Pattern.compile(pattern);
            urlRegexs.put(pattern, p);
        }
        Matcher m = p.matcher(url);
        return m.find();
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

    private static Date convertIsoDate(String str) {
        try {
            return isoSdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(S.fmt("%s %s 时间转换错误", isoSdf.toString(), str));
        }
    }

    private static Date convertYmdDate(String str) {
        try {
            return ymdSdf.parse(str);
        } catch (Exception e) {
            throw new RuntimeException(S.fmt("%s yyyy-MM-dd 时间转换错误", str));
        }
    }

    private static Date converYmdhmsDate(String str){
        try {
            return sdf.parse(str);
        } catch (Exception e) {
            throw new RuntimeException(S.fmt("%s yyyy-MM-dd hh:mm:ss 时间转换错误", str));
        }

    }

    public interface ValueGenerator {
        Object call(JSONObject kv);

        public static ValueGenerator createIsoDate(String key) {
            return kv -> convertIsoDate(kv.getStr(key));
        }

        ;

        public static ValueGenerator createYmdDate(String key) {
            return kv -> convertYmdDate(kv.getStr(key));
        }

        public static ValueGenerator createYmdhmsDate(String key){
            return kv -> converYmdhmsDate(kv.getStr(key));
        }
    }
    
    public interface DeconstructHandler{
        public void call(ChannelHandlerContext ctx, FullHttpRequest request, JSON json); 
    }

//    public interface DeconstructArrayHandler{
//        public void call(ChannelHandlerContext ctx, FullHttpRequest request, JSONArray array);
//    }
}
