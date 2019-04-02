package com.beeasy.zed;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.type.DateTypeHandler;
import org.beetl.sql.core.mapping.type.TypeParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QccService {

    private static SQLManager sqlManager;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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
        registerRoute("^/qcc/CourtV4/SearchShiXin", service::SearchShiXin);
        registerRoute("^/qcc/CourtV4/SearchZhiXing", service::SearchZhiXing);
        registerRoute("^/qcc/JudgeDocV4/SearchJudgmentDoc", service::SearchJudgmentDoc);
        registerRoute("/qcc/JudgeDocV4/GetJudgementDetail", service::GetJudgementDetail);
        registerRoute("/qcc/CourtNoticeV4/SearchCourtAnnouncement", service::SearchCourtAnnouncement);
//        HttpServerHandler.AddRoute(new Route(, service::SearchShiXin));
    }

    private Object SearchCourtAnnouncement(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return  pageQuery("qcc.查询法院公告列表", params);
    }

    private Object GetJudgementDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询裁判文书详情", params);
        //
        List<JSONObject> courtNotices = listQuery("qcc.查询裁判文书详情-开庭公告", newJsonObject("id", params.getStr("id")));
        object.put("CourtNoticeList", newJsonObject(
            "TotalNum", object.size(),
            "CourtNoticeInfo", courtNotices
        ));
        List<JSONObject> companies = listQuery("qcc.查询裁判文书详情-关联公司", newJsonObject("id", params.getStr("id")));
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

//    public Object SearchShiXin(ChannelHandlerContext ctx, FullHttpRequest request){
//        JSONObject params = HttpServerHandler.decodeProxyQuery(request);
//
////        App.zedService.sqlManager
//        return null;
//    }

    public static void registerRoute(String url, IQccRoute route) {
        HttpServerHandler.AddRoute(new Route(url, (ctx, req) -> {
            Object result = route.call(ctx, req, HttpServerHandler.decodeQuery(req));
            JSONObject realResult = newJsonObject(
                "Status", "200",
                "Message", "查询成功"
            );
            if (result != null) {
                //分页结构
                if (result instanceof PageQuery) {
                    realResult.put("Paging", newJsonObject(
                        "PageSize", ((PageQuery) result).getPageSize(),
                        "PageIndex", ((PageQuery) result).getPageNumber(),
                        "TotalRecords", ((PageQuery) result).getTotalRow()
                    ));
                    realResult.put("Result", (((PageQuery) result).getList()));
                } else {
                    realResult.put("Result", result);
                }
            } else {
                realResult.put("Status", "400");
            }
            return realResult;
        }));
    }

    public JSONObject singleQuery(String sqlId, JSONObject params){
         return JSONUtil.parseFromMap(sqlManager.selectSingle(sqlId, params, JSONObject.class));
    }

    public List<JSONObject> listQuery(String sqlId, Map<String,Object> params){
        return sqlManager.select(sqlId, JSONObject.class, params);
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

    private static JSONObject newJsonObject(Object ...objects){
        JSONObject object = new JSONObject();
        for(short i = 0; i < objects.length; i+=2){
           object.put((String) objects[i], objects[i+1]);
        }
        return object;
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

//    HttpServerHandler.decodeProxyQuery(request)
}
