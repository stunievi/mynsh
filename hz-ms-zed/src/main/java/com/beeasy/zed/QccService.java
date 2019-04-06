package com.beeasy.zed;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.osgl.util.S;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.beeasy.zed.Utils.*;

public class QccService {

    private static SQLManager sqlManager;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static String qccPrefix = "/qcc";

    private static class QccBeanProcesser extends BeanProcessor{

        public QccBeanProcesser(SQLManager sm) {
            super(sm);
        }

        @Override
        public Map<String, Object> toMap(String sqlId, Class<?> c, ResultSet rs) throws SQLException {
            Map<String, Object> map = super.toMap(sqlId, c, rs);
            if (sqlId.startsWith("qcc.")) {
                return convertToQccStyle(map);
            }
            return map;
        }
    }

    public static void register(ZedService zedService) {
        QccService service = new QccService();
        sqlManager = zedService.sqlManager;
        sqlManager.setDefaultBeanProcessors(new QccBeanProcesser(sqlManager));
        registerRoute("/CourtV4/SearchShiXin", service::SearchShiXin);
        registerRoute("/CourtV4/SearchZhiXing", service::SearchZhiXing);
        registerRoute("/JudgeDocV4/SearchJudgmentDoc", service::SearchJudgmentDoc);
        registerRoute("/JudgeDocV4/GetJudgementDetail", service::GetJudgementDetail);
        registerRoute("/CourtNoticeV4/SearchCourtAnnouncement", service::SearchCourtAnnouncement);
        registerRoute("/CourtNoticeV4/SearchCourtAnnouncementDetail", service::SearchCourtAnnouncementDetail);
        registerRoute("/CourtAnnoV4/SearchCourtNotice", service::SearchCourtNotice);
        registerRoute("/CourtAnnoV4/GetCourtNoticeInfo", service::GetCourtNoticeInfo);
        registerRoute("/JudicialAssistance/GetJudicialAssistance", service::GetJudicialAssistance);
        registerRoute("/ECIException/GetOpException", service::GetOpException);
        registerRoute("/JudicialSale/GetJudicialSaleList", service::GetJudicialSaleList);
        registerRoute("/JudicialSale/GetJudicialSaleDetail", service::GetJudicialSaleDetail);
        registerRoute("/LandMortgage/GetLandMortgageList", service::GetLandMortgageList);
        registerRoute("/LandMortgage/GetLandMortgageDetails", service::GetLandMortgageDetails);
        registerRoute("/EnvPunishment/GetEnvPunishmentList",service::GetEnvPunishmentList);
        registerRoute("/EnvPunishment/GetEnvPunishmentDetails",service::GetEnvPunishmentDetails);
    }

    /**
     * 环保处罚详情
     * @param channelHandlerContext
     * @param request
     * @param params
     * @return
     */
    private Object GetEnvPunishmentDetails(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return singleQuery("qcc.查询环保处罚详情", params);
    }

    /**
     * 环保处罚列表
     * @param channelHandlerContext
     * @param request
     * @param params
     * @return
     */
    private Object GetEnvPunishmentList(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询环保处罚列表", params);
    }

    /**
     * 土地抵押详情
     * @param channelHandlerContext
     * @param request
     * @param params
     * @return
     */
    private Object GetLandMortgageDetails(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询土地抵押详情", params);
        Iterator<Map.Entry<String, Object>> iterator = object.entrySet().iterator();
        JSONObject mo1 = newJsonObject();
        JSONObject mo2 = newJsonObject();

        while(iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            if(entry.getKey().startsWith("Re1")){
                mo1.put(entry.getKey().replace("Re1", ""), entry.getValue());
                iterator.remove();
            }
            if(entry.getKey().startsWith("Re2")){
                mo2.put(entry.getKey().replace("Re2", ""), entry.getValue());
                iterator.remove();
            }
        }
        object.put("MortgagorName", mo1);
        object.put("MortgagePeople", mo2);

        return object;
    }

    /**
     * 土地抵押列表
     * @param channelHandlerContext
     * @param request
     * @param params
     * @return
     */
    private Object GetLandMortgageList(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询土地抵押列表", params);
    }

    private Object GetJudicialSaleDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return singleQuery("qcc.查询司法拍卖详情", params);
    }

    private Object GetJudicialSaleList(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询司法拍卖列表", params);
    }

    private Object GetOpException(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return listQuery("qcc.查询企业经营异常信息", params);
    }

    /**
     * 司法协助信息
     * @param channelHandlerContext
     * @param request
     * @param params
     * @return
     */
    private Object GetJudicialAssistance(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONArray list = listQuery("qcc.查询司法协助信息", params);
        for (Object o : list) {
            JSONObject object = (JSONObject) o;
            JSONObject[] objects = {new JSONObject(), new JSONObject(), new JSONObject()};
            Iterator<Map.Entry<String, Object>> it = object.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, Object> entry = it.next();
                for(short i = 0; i < objects.length; i++){
                    if(entry.getKey().startsWith("D"+i)){
                        objects[i].put(entry.getKey().replace("D"+i, ""), entry.getValue());
                        it.remove();
                        break;
                    }
                }
            }
            for (int i = 0; i < objects.length; i++) {
                if(objects[i].size() == 0){
                    objects[i] = null;
                }
            }
            object.put("EquityFreezeDetail", objects[0]);
            object.put("EquityUnFreezeDetail", objects[1]);
            object.put("JudicialPartnersChangeDetail", objects[2]);
        }

        return list;
    }

    private Object GetCourtNoticeInfo(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询开庭公告详情", params);
        params.put("type", "01");
        object.put("Prosecutor", listQuery("qcc.查询开庭公告关联人", params));
        params.put("type", "02");
        object.put("Defendant", listQuery("qcc.查询开庭公告关联人", params));
        return object;
    }

    private Object SearchCourtNotice(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询开庭公告列表", params);
    }

    private Object SearchCourtAnnouncementDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询法院公告详情", params);
        return object;
    }

    private Object SearchCourtAnnouncement(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return  pageQuery("qcc.查询法院公告列表", params);
    }

    private Object GetJudgementDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询裁判文书详情", params);
        if(object.size() == 0){
            return object;
        }
        object.put("Appellor", JSONUtil.parseArray(object.getStr("Appellor")));
        object.put("DefendantList", JSONUtil.parseArray(object.getStr("DefendantList")));
        object.put("ProsecutorList", JSONUtil.parseArray(object.getStr("ProsecutorList")));
        //
        JSONArray courtNotices = listQuery("qcc.查询裁判文书详情-开庭公告", newJsonObject("id", params.getStr("id")));
        object.put("CourtNoticeList", newJsonObject(
            "TotalNum", courtNotices.size(),
            "CourtNoticeInfo", courtNotices
        ));
        JSONArray companies = listQuery("qcc.查询裁判文书详情-关联公司", newJsonObject("id", params.getStr("id")));
        object.put("RelatedCompanies", companies);
        return object;
    }

    private Object SearchJudgmentDoc(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return  pageQuery("qcc.查询裁判文书列表", params);
    }

    private Object SearchZhiXing(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询被执行信息", params);
    }


    private Object SearchShiXin(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询失信信息", params);
    }


    public static void registerRoute(String url, IQccRoute route) {
        HttpServerHandler.AddRoute(new Route(S.fmt("%s%s",qccPrefix, url), (ctx, req) -> {
            Object result = route.call(ctx, req, HttpServerHandler.decodeQuery(req));
            JSONObject realResult = newJsonObject(
                "Status", "200",
                "Message", "查询成功"
            );
            if (result != null) {
                //分页结构
                if (result instanceof PageQuery) {
                    if(((PageQuery) result).getTotalRow() == 0){
                        realResult.put("Status", "201");
                    }
                    realResult.put("Paging", newJsonObject(
                        "PageSize", ((PageQuery) result).getPageSize(),
                        "PageIndex", ((PageQuery) result).getPageNumber(),
                        "TotalRecords", ((PageQuery) result).getTotalRow()
                    ));
                    realResult.put("Result", (((PageQuery) result).getList()));
                } else if(result instanceof JSONObject){
                    if (((JSONObject) result).size() == 0) {
                        realResult.put("Status", "201");
                   }
                    realResult.put("Result", result);
                } else {
                    realResult.put("Result", result);
                }
            } else {
                realResult.put("Status", "500");
            }
            return realResult;
        }));
    }

    public JSONObject singleQuery(String sqlId, JSONObject params){
        Map map = sqlManager.selectSingle(sqlId, params, Map.class);
        if (map == null) {
            return new JSONObject();
        }
        return JSONUtil.parseFromMap(map);
    }

    public JSONArray listQuery(String sqlId, Map<String,Object> params){
        return JSONUtil.parseArray(sqlManager.select(sqlId, JSONObject.class, params));
    }

    public PageQuery<JSONObject> pageQuery(String sqlId, JSONObject params) {
        PageQuery<JSONObject> pageQuery = new PageQuery<>();
        int page = 1;
        int size = 10;
        try {
            page = params.getInt("pageIndex", 1);
            size = params.getInt("pageSize", 10);
        } finally {
            pageQuery.setPageSize(size);
            pageQuery.setPageNumber(page);
        }
        pageQuery.setParas(params);
        sqlManager.pageQuery(sqlId, JSONObject.class, pageQuery);
        return pageQuery;
    }


    public static String firstLetterUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static Map<String, Object> convertToQccStyle(Map<String, Object> json) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            if(entry.getValue() instanceof Date){
                entry.setValue(sdf.format(entry.getValue()));
            }
            map.put(firstLetterUpper(entry.getKey()), entry.getValue());
        }
        return map;
    }

    public interface IQccRoute {
        Object call(ChannelHandlerContext ctx, FullHttpRequest request, JSONObject params);
    }

}
