package com.beeasy.zed;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.osgl.util.S;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import static com.beeasy.zed.Config.config;

import static com.beeasy.zed.DBService.sqlManager;
import static com.beeasy.zed.Utils.newJsonObject;

public class QccService extends AbstractService{

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static String qccPrefix = "";
    private static Future future;

    private static class QccBeanProcesser extends BeanProcessor {

        public QccBeanProcesser(SQLManager sm) {
            super(sm);
        }

        @Override
        public Map<String, Object> toMap(String sqlId, Class<?> c, ResultSet rs) throws SQLException {
            Map<String, Object> map = super.toMap(sqlId, c, rs);
            map.remove("beetlRn");
            if (sqlId.startsWith("qcc.")) {
                convertToQccStyle(map);
            }
            return map;
        }
    }

    /**
     *
     * @apiDefine PageParam
     *
     * @apiParam {int} pageIndex 页码，默认1
     * @apiParam {int} pageSize 每页数量，默认10
     *
     */

    @Override
    public void initSync() {
        QccService service = this;
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
        registerRoute("/EnvPunishment/GetEnvPunishmentList", service::GetEnvPunishmentList);
        registerRoute("/EnvPunishment/GetEnvPunishmentDetails", service::GetEnvPunishmentDetails);
        registerRoute("/EquityThrough/GetEquityThrough",service::GetEquityThrough);
        registerRoute("/ChattelMortgage/GetChattelMortgage", service::GetChattelMortgage);
        registerRoute("/ECIV4/GetDetailsByName", service::GetDetailsByName);
        registerRoute("/History/GetHistorytEci", service::GetHistorytEci);
        registerRoute("/History/GetHistorytInvestment", service::GetHistorytInvestment);
        registerRoute("/History/GetHistorytShareHolder", service::GetHistorytShareHolder);
        registerRoute("/History/GetHistoryShiXin", service::GetHistoryShiXin);
        registerRoute("/History/GetHistoryZhiXing", service::GetHistoryZhiXing);
        registerRoute("/History/GetHistorytCourtNotice", service::GetHistorytCourtNotice);
        registerRoute("/History/GetHistorytJudgement", service::GetHistorytJudgement);
        registerRoute("/History/GetHistorytSessionNotice", service::GetHistorytSessionNotice);
        registerRoute("/History/GetHistorytMPledge", service::GetHistorytMPledge);
        registerRoute("/History/GetHistorytPledge", service::GetHistorytPledge);
        registerRoute("/History/GetHistorytAdminPenalty", service::GetHistorytAdminPenalty);
        registerRoute("/History/GetHistorytAdminLicens", service::GetHistorytAdminLicens);
        registerRoute("/ECIV4/SearchFresh", service::SearchFresh);
        registerRoute("/ECIRelationV4/SearchTreeRelationMap", service::SearchTreeRelationMap);
        registerRoute("/ECIRelationV4/GetCompanyEquityShareMap", service::GetCompanyEquityShareMap);
        registerRoute("/ECIRelationV4/GenerateMultiDimensionalTreeCompanyMap", service::GenerateMultiDimensionalTreeCompanyMap);
        registerRoute("/CIAEmployeeV4/GetStockRelationInfo", service::GetStockRelationInfo);
        registerRoute("/HoldingCompany/GetHoldingCompany", service::GetHoldingCompany);
        registerRoute("/ECICompanyMap/GetStockAnalysisData", service::GetStockAnalysisData);
        //附加的借口
        registerRoute("/interface/count", service::getInterfaceCount);
    }

    /**
     * @apiDefine QccError
     *
     * @apiErrorExample 请求异常:
     * {
     *     "Status": "500",
     *     "Message": "错误请求"
     * }
     */


    /**
     * @api {get} {企查查数据查询服务地址}/interface/count 企业企查查数据统计
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccessExample 请求成功:
     * {
     * 	"Status":"200",
     * 	"Message":"查询成功",
     * 	"Result":{
     * 		"/History/GetHistorytMPledge":0,
     * 		"/ECIV4/SearchFresh":0,
     * 		"/ECICompanyMap/GetStockAnalysisData":0,
     * 		"/CIAEmployeeV4/GetStockRelationInfo":0,
     * 		"/CourtAnnoV4/SearchCourtNotice":0,
     * 		"/History/GetHistorytShareHolder":0,
     * 		"/JudicialSale/GetJudicialSaleDetail":0,
     * 		"/ECIRelationV4/GenerateMultiDimensionalTreeCompanyMap":0,
     * 		"/LandMortgage/GetLandMortgageDetails":0,
     * 		"/History/GetHistorytInvestment":0,
     * 		"/History/GetHistoryZhiXing":0,
     * 		"/ECIRelationV4/GetCompanyEquityShareMap":0,
     * 		"/History/GetHistoryShiXin":0,
     * 		"/History/GetHistorytAdminPenalty":0,
     * 		"/CourtV4/SearchZhiXing":5,
     * 		"/History/GetHistorytJudgement":0,
     * 		"/History/GetHistorytCourtNotice":0,
     * 		"/JudgeDocV4/GetJudgementDetail":0,
     * 		"/CourtNoticeV4/SearchCourtAnnouncement":9,
     * 		"/EnvPunishment/GetEnvPunishmentList":0,
     * 		"/ECIV4/GetDetailsByName":0,
     * 		"/History/GetHistorytSessionNotice":0,
     * 		"/JudgeDocV4/SearchJudgmentDoc":26,
     * 		"^/zed":0,
     * 		"/History/GetHistorytPledge":0,
     * 		"/JudicialSale/GetJudicialSaleList":0,
     * 		"/file":0,
     * 		"/ECIException/GetOpException":0,
     * 		"/ECIRelationV4/SearchTreeRelationMap":0,
     * 		"/CourtV4/SearchShiXin":1,
     * 		"/LandMortgage/GetLandMortgageList":0,
     * 		"/HoldingCompany/GetHoldingCompany":0,
     * 		"/EnvPunishment/GetEnvPunishmentDetails":0,
     * 		"/History/GetHistorytAdminLicens":0,
     * 		"/JudicialAssistance/GetJudicialAssistance":6,
     * 		"/CourtAnnoV4/GetCourtNoticeInfo":0,
     * 		"/History/GetHistorytEci":0,
     * 		"/CourtNoticeV4/SearchCourtAnnouncementDetail":0,
     * 		"/ChattelMortgage/GetChattelMortgage":0,
     * 		"/interface/count":0
     *        }
     * }
     *
     * @apiUse QccError
     */
    private Object getInterfaceCount(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        String compName = params.getString("fullName");
        if(S.blank(compName)){
            return null;
        }
        String sql = String.format("select interface,count from qcc_interface_count where inner_company_name = '%s'", compName);
        List<JSONObject> objects =  sqlManager.execute(new SQLReady(sql), JSONObject.class);
        JSONObject ret = new JSONObject();
        HttpServerHandler.RouteList.forEach(r -> {
            ret.put(r.regexp, new AtomicInteger(0));
        });
        for (JSONObject object : objects) {
            AtomicInteger in = (AtomicInteger) ret.get(object.getString("interface"));
            in.addAndGet(object.getInteger("count"));
        }
        return ret;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/ECICompanyMap/GetStockAnalysisData 企业股权穿透十层接口查询
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {object} CompanyData 公司资料
     * @apiSuccess {string} CompanyData.TermStart 营业期限自
     * @apiSuccess {string} CompanyData.TeamEnd 营业期限至
     * @apiSuccess {string} CompanyData.CheckDate 发照日期
     * @apiSuccess {string} CompanyData.KeyNo 公司KeyNo
     * @apiSuccess {string} CompanyData.Name 企业名称
     * @apiSuccess {string} CompanyData.No 注册号
     * @apiSuccess {string} CompanyData.BelongOrg 所属机构
     * @apiSuccess {string} CompanyData.OperName 法人名称
     * @apiSuccess {string} CompanyData.StartDate 成立日期
     * @apiSuccess {string} CompanyData.EndDate 吊销日期
     * @apiSuccess {string} CompanyData.Status 状态
     * @apiSuccess {string} CompanyData.Province 省份代码
     * @apiSuccess {string} CompanyData.UpdatedDate 更新日期
     * @apiSuccess {string} CompanyData.ShortStatus 状态简称
     * @apiSuccess {string} CompanyData.RegistCapi 注册资本
     * @apiSuccess {string} CompanyData.EconKind 类型
     * @apiSuccess {string} CompanyData.Address 地址
     * @apiSuccess {string} CompanyData.Scope 营业范围
     * @apiSuccess {string} CompanyData.OrgNo 组织机构代码
     *
     * @apiSuccess {object[]} CompanyData.Partners 股东信息
     * @apiSuccess {string} CompanyData.Partners.CompanyId 公司ID
     * @apiSuccess {string} CompanyData.Partners.StockName 股东名称
     * @apiSuccess {string} CompanyData.Partners.StockType 股东类型
     * @apiSuccess {string} CompanyData.Partners.StockPercent 股东持股百分比
     * @apiSuccess {string} CompanyData.Partners.IdentifyType 证件类型
     * @apiSuccess {string} CompanyData.Partners.IdentifyNo 证件号码
     * @apiSuccess {string} CompanyData.Partners.ShouldCapi 出资额（万元）
     * @apiSuccess {string} CompanyData.Partners.ShoudDate 出资日期
     *
     * @apiSuccess {tree} StockList 股东列表
     * @apiSuccess {string} StockList.KeyNo KeyNo
     * @apiSuccess {string} StockList.Name 企业名称
     * @apiSuccess {string} StockList.PathName 投资路径
     * @apiSuccess {string} StockList.RegistCapi 注册资本
     * @apiSuccess {string} StockList.EconKind 企业类型
     * @apiSuccess {string} StockList.StockType 股东类型
     * @apiSuccess {string} StockList.FundedAmount 出资额
     * @apiSuccess {string} StockList.FundedRate 出资比列
     * @apiSuccess {string} StockList.InvestType 投资类型
     * @apiSuccess {string} StockList.Level 层级
     * @apiSuccess {tree[]} StockList.Children 以上字段的子树
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "CompanyData": {
     *             "Status": "存续（在营、开业、在册）",
     *             "RegistCapi": "100000万人民币",
     *             "No": null,
     *             "BelongOrg": "大连市工商行政管理局",
     *             "CreditCode": "91210200241281392F",
     *             "OperName": "王健林",
     *             "EconKind": "其他股份有限公司(非上市)",
     *             "Address": "辽宁省大连市西岗区长江路539号",
     *             "UpdatedDate": "2018-01-30 06:16:02",
     *             "OrgNo": "24128139-2",
     *             "EndDate": null,
     *             "Province": "LN",
     *             "TermStart": "1992-09-28 12:00:00",
     *             "Name": "大连万达集团股份有限公司",
     *             "TeamEnd": "2037-09-28 12:00:00",
     *             "KeyNo": "befe52d9753b511b6aef5e33fe00f97d",
     *             "StartDate": "1992-09-28 12:00:00",
     *             "Scope": "商业地产投资及经营、酒店建设投资及经营、连锁百货投资及经营、电影院线等文化产业投资及经营；投资与资产管理、项目管理（以上均不含专项审批）；货物进出口、技术进出口，国内一般贸易；代理记账、财务咨询、企业管理咨询、经济信息咨询、计算机信息技术服务与技术咨询、计算机系统集成、网络设备安装与维护。（依法须经批准的项目，经相关部门批准后，方可开展经营活动）***",
     *             "CheckDate": "2016-05-23 12:00:00",
     *             "ShortStatus": "存续",
     *             "Partners": [
     *                 {
     *                     "ShoudDate": "2013-04-03,2013-04-03,2013-04-03",
     *                     "IdentifyType": "企业法人营业执照(公司)",
     *                     "CompanyId": "971e2cafbecd8c978e959d69fc305f42",
     *                     "StockName": "大连合兴投资有限公司",
     *                     "StockType": "企业法人",
     *                     "IdentifyNo": "2102001108389",
     *                     "StockPercent": "99.7600%",
     *                     "ShouldCapi": "88000,3760,8000"
     *                 },
     *                 {
     *                     "ShoudDate": "1993-03-15",
     *                     "IdentifyType": "非公示项",
     *                     "CompanyId": "",
     *                     "StockName": "王健林",
     *                     "StockType": "自然人股东",
     *                     "StockPercent": "0.2400%",
     *                     "ShouldCapi": "240"
     *                 }
     *             ]
     *         },
     *         "StockStatistics": {
     *             "TotalCount": 5,
     *             "LevelDataList": [
     *                 {
     *                     "TotalCount": 2,
     *                     "Level": 1
     *                 },
     *                 {
     *                     "TotalCount": 2,
     *                     "Level": 2
     *                 }
     *             ],
     *             "EconKindDataList": [
     *                 {
     *                     "TotalCount": 2,
     *                     "EconKind": "其他股份有限公司(非上市)"
     *                 }
     *             ],
     *             "StockTypeDataList": [
     *                 {
     *                     "TotalCount": 1,
     *                     "StockType": "企业法人"
     *                 },
     *                 {
     *                     "TotalCount": 3,
     *                     "StockType": "自然人股东"
     *                 }
     *             ]
     *         },
     *         "StockList": {
     *             "KeyNo": "befe52d9753b511b6aef5e33fe00f97d",
     *             "RegistCapi": "100000万人民币",
     *             "EconKind": "其他股份有限公司(非上市)",
     *             "Level": "0",
     *             "PathName": "",
     *             "Children": [
     *                 {
     *                     "RegistCapi": "7860万人民币",
     *                     "EconKind": "有限责任公司(自然人投资或控股)",
     *                     "FundedRate": "99.7600%",
     *                     "InvestType": "货币,货币,货币",
     *                     "Name": "大连合兴投资有限公司",
     *                     "KeyNo": "971e2cafbecd8c978e959d69fc305f42",
     *                     "StockType": "企业法人",
     *                     "Level": "1",
     *                     "PathName": "大连万达集团股份有限公司",
     *                     "FundedAmount": "88000,3760,8000万元",
     *                     "Children": [
     *                         {
     *                             "KeyNo": "",
     *                             "StockType": "自然人股东",
     *                             "FundedRate": "98.00%",
     *                             "Level": "2",
     *                             "PathName": "大连万达集团股份有限公司/大连合兴投资有限公司",
     *                             "FundedAmount": "7702.8万元",
     *                             "Children": [
     *                             ],
     *                             "InvestType": "货币,货币,货币",
     *                             "Name": "王健林"
     *                         },
     *                         {
     *                             "KeyNo": "",
     *                             "StockType": "自然人股东",
     *                             "FundedRate": "2.00%",
     *                             "Level": "2",
     *                             "PathName": "大连万达集团股份有限公司/大连合兴投资有限公司",
     *                             "FundedAmount": "157.2万元",
     *                             "Children": [
     *                             ],
     *                             "InvestType": "货币",
     *                             "Name": "王思聪"
     *                         }
     *                     ]
     *                 },
     *                 {
     *                     "KeyNo": "",
     *                     "StockType": "自然人股东",
     *                     "FundedRate": "0.2400%",
     *                     "Level": "1",
     *                     "PathName": "大连万达集团股份有限公司",
     *                     "FundedAmount": "240万元",
     *                     "Children": [
     *                     ],
     *                     "InvestType": "货币",
     *                     "Name": "王健林"
     *                 }
     *             ],
     *             "Name": "大连万达集团股份有限公司"
     *         }
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GetStockAnalysisData(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject compData = singleQuery("qcc.查询股权穿透十层信息表", params);
        List<JSONObject> partners = listQuery("qcc.查询股权穿透十层股东信息表", params);
        JSONObject ss = JSON.parseObject(compData.getString("StockStatistics"));
        compData.remove("StockStatistics");
        List<JSONObject> stockList = listQuery("qcc.查询股权穿透十层股东列表", params);
        compData.put("Partners", partners);
        return newJsonObject(
            "CompanyData", compData,
            "StockList", convertToTree(stockList),
            "StockStatistics", ss
        );
    }


    /**
     * @api {get} {企查查数据查询服务地址}/HoldingCompany/GetHoldingCompany 控股公司信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} KeyNo 公司KeyNo
     * @apiSuccess {string} CompanyName 公司名称
     * @apiSuccess {string} NameCount 控股公司个数
     *
     * @apiSuccess {object[]} Names 控股公司列表
     * @apiSuccess {string} Names.KeyNo 公司KeyNo
     * @apiSuccess {string} Names.Name 公司名称
     * @apiSuccess {string} Names.PercentTotal 投资比例
     * @apiSuccess {string} Names.Level 层级数
     * @apiSuccess {string} Names.ShortStatus 状态
     * @apiSuccess {string} Names.StartDate 成立时间
     * @apiSuccess {string} Names.RegistCapi 注册资金
     * @apiSuccess {string} Names.ImageUrl Logo
     * @apiSuccess {string} Names.EconKind 企业类型
     *
     * @apiSuccess {object[]} Names.Paths Paths
     * @apiSuccess {string} Names.Paths.KeyNo 公司KeyNo
     * @apiSuccess {string} Names.Paths.Name 公司名称
     * @apiSuccess {string} Names.Paths.PercentTotal 投资比例
     * @apiSuccess {string} Names.Paths.Level 层级
     *
     * @apiSuccess {object} Names.Oper Oper
     * @apiSuccess {string} Names.Oper.Name 法人名称
     * @apiSuccess {string} Names.Oper.KeyNo 法人对应KeyNo
     * @apiSuccess {int} Names.Oper.CompanyCount 关联公司个数
     *
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 10,
     *         "PageIndex": 1
     *     },
     *     "Result": {
     *         "KeyNo": "4659626b1e5e43f1bcad8c268753216e",
     *         "CompanyName": "北京小桔科技有限公司",
     *         "NameCount": "47",
     *         "Names": [
     *             {
     *                 "KeyNo": "05c090155e36541c83e9ab59ab3f402d",
     *                 "RegistCapi": "1000万人民币元",
     *                 "StartDate": "2016-03-24 12:00:00",
     *                 "EconKind": "有限责任公司（自然人投资或控股的法人独资）",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/default.jpg",
     *                 "Level": "1",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "05c090155e36541c83e9ab59ab3f402d",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "嘉兴橙子投资管理有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "p3f038f0b735b9a50fd66c193435f9b0",
     *                     "CompanyCount": 7,
     *                     "Name": "求非曲"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "存续",
     *                 "Name": "嘉兴橙子投资管理有限公司"
     *             },
     *             {
     *                 "KeyNo": "1047b1886e63c9475e10163343a09b76",
     *                 "RegistCapi": "200万人民币元",
     *                 "StartDate": "2018-03-12 12:00:00",
     *                 "EconKind": "有限责任公司(法人独资)",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/default.jpg",
     *                 "Level": "1",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "1047b1886e63c9475e10163343a09b76",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "北京滴滴承信科技咨询服务有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "pre3325af8698e0188fa65a334bdd134",
     *                     "CompanyCount": 1,
     *                     "Name": "张露文"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "在业",
     *                 "Name": "北京滴滴承信科技咨询服务有限公司"
     *             },
     *             {
     *                 "KeyNo": "2018201d8e12e8769946032dbf5e7ac1",
     *                 "RegistCapi": "100万人民币元",
     *                 "StartDate": "2004-04-27 12:00:00",
     *                 "EconKind": "有限责任公司（法人独资）",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/default.jpg",
     *                 "Level": "3",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "3fe7fa121a61e0d869a52b4752b9e272",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "杭州滴滴汽车服务有限公司"
     *                         },
     *                         {
     *                             "KeyNo": "a1b0f97ad43b0e721246790556890b99",
     *                             "Level": "2",
     *                             "PercentTotal": "100%",
     *                             "Name": "杭州小木吉汽车服务有限公司"
     *                         },
     *                         {
     *                             "KeyNo": "2018201d8e12e8769946032dbf5e7ac1",
     *                             "Level": "3",
     *                             "PercentTotal": "100%",
     *                             "Name": "深圳市伟恒汽车有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "pr98c18a754ba7493fd8a4ddb18953c6",
     *                     "CompanyCount": 1,
     *                     "Name": "杨志新"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "存续",
     *                 "Name": "深圳市伟恒汽车有限公司"
     *             },
     *             {
     *                 "KeyNo": "205a8c9f2bd6b437ce8b3d0bdd3ae62a",
     *                 "RegistCapi": "100万人民币元",
     *                 "StartDate": "2018-05-31 12:00:00",
     *                 "EconKind": "有限责任公司(法人独资)",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/default.jpg",
     *                 "Level": "2",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "afb3daf1797df272997f22b143c964f6",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "北京车胜科技有限公司"
     *                         },
     *                         {
     *                             "KeyNo": "205a8c9f2bd6b437ce8b3d0bdd3ae62a",
     *                             "Level": "2",
     *                             "PercentTotal": "100%",
     *                             "Name": "小桔(北京)汽车服务有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "pba983a8d51f856d6977e4d6e1c2e49b",
     *                     "CompanyCount": 3,
     *                     "Name": "邵韬"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "在业",
     *                 "Name": "小桔(北京)汽车服务有限公司"
     *             },
     *             {
     *                 "KeyNo": "25658b066d565464839d7c0b214fd42b",
     *                 "RegistCapi": "1000万人民币元",
     *                 "StartDate": "2015-07-15 12:00:00",
     *                 "EconKind": "有限责任公司（自然人投资或控股的法人独资）",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/default.jpg",
     *                 "Level": "2",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "8bd250d6875caa56dc9a6747b49689c0",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "上海吾步信息技术有限公司"
     *                         },
     *                         {
     *                             "KeyNo": "25658b066d565464839d7c0b214fd42b",
     *                             "Level": "2",
     *                             "PercentTotal": "100%",
     *                             "Name": "贵阳吾步数据服务有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "pf9428a8a2afb5057a10f3e4802b9eee",
     *                     "CompanyCount": 46,
     *                     "Name": "陈汀"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "存续",
     *                 "Name": "贵阳吾步数据服务有限公司"
     *             },
     *             {
     *                 "KeyNo": "263ed5f366aa352491f2a9112db02cdd",
     *                 "RegistCapi": "40000万人民币元",
     *                 "StartDate": "2005-05-19 12:00:00",
     *                 "EconKind": "有限责任公司（自然人投资或控股的法人独资）",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/default.jpg",
     *                 "Level": "2",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "44d5992e16ff513c91f86c5b0fdf2227",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "滴滴出行科技有限公司"
     *                         },
     *                         {
     *                             "KeyNo": "263ed5f366aa352491f2a9112db02cdd",
     *                             "Level": "2",
     *                             "PercentTotal": "100%",
     *                             "Name": "上海时园科技有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "p30478fe73bc161a5988c4bb77d43f56",
     *                     "CompanyCount": 3,
     *                     "Name": "刘少荣"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "存续",
     *                 "Name": "上海时园科技有限公司"
     *             },
     *             {
     *                 "KeyNo": "274d9311979595d54821e1d9e8d73e36",
     *                 "RegistCapi": "500万人民币元",
     *                 "StartDate": "2017-11-22 12:00:00",
     *                 "EconKind": "有限责任公司(法人独资)",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/274d9311979595d54821e1d9e8d73e36.jpg",
     *                 "Level": "1",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "274d9311979595d54821e1d9e8d73e36",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "北京再造科技有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "p661ca55f69289b4e72edac3164e99ff",
     *                     "CompanyCount": 2,
     *                     "Name": "罗文"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "在业",
     *                 "Name": "北京再造科技有限公司"
     *             },
     *             {
     *                 "KeyNo": "2bbaaaf09d9877b8dd851a02ad9600a2",
     *                 "RegistCapi": "2000万人民币元",
     *                 "StartDate": "2013-10-21 12:00:00",
     *                 "EconKind": "有限责任公司（自然人投资或控股的法人独资）",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/2bbaaaf09d9877b8dd851a02ad9600a2.jpg",
     *                 "Level": "1",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "2bbaaaf09d9877b8dd851a02ad9600a2",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "上海奇漾信息技术有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "pf9428a8a2afb5057a10f3e4802b9eee",
     *                     "CompanyCount": 46,
     *                     "Name": "陈汀"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "存续",
     *                 "Name": "上海奇漾信息技术有限公司"
     *             },
     *             {
     *                 "KeyNo": "3a079aa5beaf85378a2dba72ec6d563a",
     *                 "RegistCapi": "100万人民币元",
     *                 "StartDate": "2014-06-12 12:00:00",
     *                 "EconKind": "有限责任公司(法人独资)",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/default.jpg",
     *                 "Level": "1",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "3a079aa5beaf85378a2dba72ec6d563a",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "北京通达无限科技有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "pf243c8bcd428b850f367092d7f9b34c",
     *                     "CompanyCount": 2,
     *                     "Name": "李锦飞"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "在业",
     *                 "Name": "北京通达无限科技有限公司"
     *             },
     *             {
     *                 "KeyNo": "3d1a9683a46bf88fdb82ba7c88720406",
     *                 "RegistCapi": "2000万人民币元",
     *                 "StartDate": "2018-04-16 12:00:00",
     *                 "EconKind": "有限责任公司(法人独资)",
     *                 "ImageUrl": "https://co-image.qichacha.com/CompanyImage/default.jpg",
     *                 "Level": "3",
     *                 "Paths": [
     *                     [
     *                         {
     *                             "KeyNo": "44d5992e16ff513c91f86c5b0fdf2227",
     *                             "Level": "1",
     *                             "PercentTotal": "100%",
     *                             "Name": "滴滴出行科技有限公司"
     *                         },
     *                         {
     *                             "KeyNo": "483a812cab4c1ab3b9c63acbf0d1e357",
     *                             "Level": "2",
     *                             "PercentTotal": "100%",
     *                             "Name": "迪润（天津）科技有限公司"
     *                         },
     *                         {
     *                             "KeyNo": "3d1a9683a46bf88fdb82ba7c88720406",
     *                             "Level": "3",
     *                             "PercentTotal": "100%",
     *                             "Name": "西安小木吉网络科技有限公司"
     *                         }
     *                     ]
     *                 ],
     *                 "Oper": {
     *                     "KeyNo": "pr4193775c4f2e113f7537bc00b81aa8",
     *                     "CompanyCount": 1,
     *                     "Name": "高翔"
     *                 },
     *                 "PercentTotal": "100%",
     *                 "ShortStatus": "在业",
     *                 "Name": "西安小木吉网络科技有限公司"
     *             }
     *         ]
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GetHoldingCompany(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject main = singleQuery("qcc.查询公司信息表", params);
        JSONObject names = pageQuery("qcc.查询控股公司列表信息表", params);
        JSONArray list = names.getJSONArray("list");
        for (Object _object : list) {
            JSONObject object = (JSONObject) _object;
            object.put("Paths", JSON.parse(object.getString("Paths")));
            object.put("Oper", JSON.parse(object.getString("Oper")));
        }
        main.put("Names", list);
        names.remove("list");
        names.put("Result", main);
        return names;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/CIAEmployeeV4/GetStockRelationInfo 企业人员董监高信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiParam {string} personName 人名
     *
     * @apiSuccess {object[]} CIACompanyLegals 担任法人公司信息
     * @apiSuccess {string} CIACompanyLegals.Name 企业名称
     * @apiSuccess {string} CIACompanyLegals.RegNo 注册号
     * @apiSuccess {string} CIACompanyLegals.RegCap 注册资本
     * @apiSuccess {string} CIACompanyLegals.RegCapCur 注册资本币种
     * @apiSuccess {string} CIACompanyLegals.Status 企业状态
     * @apiSuccess {string} CIACompanyLegals.EcoKind 企业类型
     * @apiSuccess {string} CIACompanyLegals.OperName 法人代表
     * @apiSuccess {string} CIACompanyLegals.StartDate 企业注册时间
     *
     * @apiSuccess {object[]} CIAForeignInvestments 对外投资信息
     * @apiSuccess {string} CIAForeignInvestments.SubConAmt 认缴出资额
     * @apiSuccess {string} CIAForeignInvestments.SubCurrency 认缴出资币种
     * @apiSuccess {string} CIAForeignInvestments.EcoKind 企业类型
     * @apiSuccess {string} CIAForeignInvestments.Name 企业名称
     * @apiSuccess {string} CIAForeignInvestments.RegNo 注册号
     * @apiSuccess {string} CIAForeignInvestments.RegCap 注册资本
     * @apiSuccess {string} CIAForeignInvestments.RegCapCur 注册资本币种
     * @apiSuccess {string} CIAForeignInvestments.Status 企业状态
     * @apiSuccess {string} CIAForeignInvestments.OperName 法人代表
     * @apiSuccess {string} CIAForeignInvestments.StartDate 企业注册时间
     *
     * @apiSuccess {object[]} CIAForeignOffices 在外任职信息
     * @apiSuccess {string} CIAForeignOffices.Position 职位
     * @apiSuccess {string} CIAForeignOffices.EcoKind 企业类型
     * @apiSuccess {string} CIAForeignOffices.Name 企业名称
     * @apiSuccess {string} CIAForeignOffices.RegNo 注册号
     * @apiSuccess {string} CIAForeignOffices.RegCap 注册资本
     * @apiSuccess {string} CIAForeignOffices.RegCapCur 注册资本币种
     * @apiSuccess {string} CIAForeignOffices.Status 企业状态
     * @apiSuccess {string} CIAForeignOffices.OperName 法人代表
     * @apiSuccess {string} CIAForeignOffices.StartDate 企业注册时间
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "CIAForeignInvestments": [
     *             {
     *                 "InputDate": "2019-04-14 01:12:43",
     *                 "Status": "存续",
     *                 "RegCap": "10万元人民币",
     *                 "SubConAmt": "1",
     *                 "RegNo": "110105020574345",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京普达鑫投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:43",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "SubConAmt": "800",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海纵庭酒业有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:43",
     *                 "Status": "存续",
     *                 "RegCap": "10万元人民币",
     *                 "SubConAmt": "1",
     *                 "RegNo": "",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京普惠思投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:43",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "SubConAmt": "800",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海水晶荔枝娱乐文化有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:43",
     *                 "Status": "存续",
     *                 "RegCap": "10万元人民币",
     *                 "SubConAmt": "1",
     *                 "RegNo": "110105020574466",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京昌盛四海投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:43",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "SubConAmt": "200",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划体育文化有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "SubConAmt": "200",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划演出经纪有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "SubConAmt": "11.880700",
     *                 "RegNo": "310116003315491",
     *                 "EcoKind": "有限合伙企业",
     *                 "Name": "上海牛铺信息科技合伙企业（有限合伙）"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "SubConAmt": "10.000000",
     *                 "RegNo": "440003000139391",
     *                 "EcoKind": "有限合伙企业",
     *                 "Name": "珠海横琴普斯股权投资企业（有限合伙）"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "注销",
     *                 "RegCap": "30万人民币",
     *                 "SubConAmt": "3",
     *                 "RegNo": "5101042007639",
     *                 "EcoKind": "有限责任公司(自然人投资或控股)",
     *                 "Name": "成都市锦江区大歌星餐饮娱乐有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "200.000000万人民币",
     *                 "SubConAmt": "18",
     *                 "RegNo": "330211000110854",
     *                 "EcoKind": "有限责任公司(自然人投资或控股)",
     *                 "Name": "宁波朗盛投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "SubConAmt": "10",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海普思投资有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "注销",
     *                 "RegCap": "4000万人民币",
     *                 "SubConAmt": "80",
     *                 "RegNo": "310115000814817",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海万尚置业有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "1000万元人民币",
     *                 "SubConAmt": "10",
     *                 "RegNo": "110105016521299",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京达德厚鑫投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "7860万人民币",
     *                 "SubConAmt": "157.2",
     *                 "EcoKind": "有限责任公司(自然人投资或控股)",
     *                 "Name": "大连合兴投资有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "SubConAmt": "200",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划影视文化有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "1000万元人民币",
     *                 "SubConAmt": "10万元",
     *                 "RegNo": "440003000137855",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "珠海横琴普斯投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "100万元人民币",
     *                 "SubConAmt": "1",
     *                 "RegNo": "110105016521346",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京汇德信投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "10000万人民币",
     *                 "SubConAmt": "6850",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划文化发展有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegNo": "330522000184262",
     *                 "EcoKind": "个人独资企业",
     *                 "Name": "珺娱（湖州）文化发展中心"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "526.3158万元人民币",
     *                 "SubConAmt": "98",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司(自然人投资或控股)",
     *                 "Name": "北京叮咚柠檬科技有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "SubConAmt": "1.000000",
     *                 "RegNo": "",
     *                 "EcoKind": "有限合伙企业",
     *                 "Name": "上海沓厚投资合伙企业（有限合伙）"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "SubConAmt": "200",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划音乐有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "1111.11万人民币",
     *                 "SubConAmt": "",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海爱洛星食品有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "2000万元人民币",
     *                 "SubConAmt": "2000",
     *                 "RegNo": "110105012460853",
     *                 "EcoKind": "有限责任公司(自然人独资)",
     *                 "Name": "北京普思投资有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:44",
     *                 "Status": "存续",
     *                 "RegCap": "1000.000000万人民币",
     *                 "SubConAmt": "10",
     *                 "RegNo": "120116000437468",
     *                 "EcoKind": "有限责任公司",
     *                 "Name": "天津普思资产管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "1333.3325万人民币",
     *                 "SubConAmt": "200.000000",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划电子游戏有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "注销",
     *                 "RegCap": "50.000000万元人民币",
     *                 "SubConAmt": "5 万元",
     *                 "RegNo": "330212000097826",
     *                 "EcoKind": "私营有限责任公司(自然人控股或私营性质企业控股)",
     *                 "Name": "宁波市鄞州大歌星餐饮娱乐有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "1111.1111万元人民币",
     *                 "SubConAmt": "200",
     *                 "RegNo": "110105020849335",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京香蕉计划体育文化有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "1000.000000万人民币",
     *                 "SubConAmt": "10.000000",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司",
     *                 "Name": "平潭普思资产管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "1000万",
     *                 "SubConAmt": "300",
     *                 "RegNo": "210200000248224",
     *                 "EcoKind": "有限责任公司(自然人投资或控股)",
     *                 "Name": "蓝泰科技（大连）有限公司"
     *             }
     *         ],
     *         "CIACompanyLegals": [
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "注销",
     *                 "RegNo": "320503600090522",
     *                 "EcoKind": "个体工商户",
     *                 "Name": "苏州市平江区大歌星超市"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "10万元人民币",
     *                 "RegNo": "110105020574345",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京普达鑫投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "10万元人民币",
     *                 "RegNo": "",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京普惠思投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "10万元人民币",
     *                 "RegNo": "110105020574466",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京昌盛四海投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "在业",
     *                 "RegNo": "330212600096923",
     *                 "EcoKind": "个体工商户",
     *                 "Name": "宁波市鄞州钟公庙大歌星自选超市"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "注销",
     *                 "RegNo": "320105600200053",
     *                 "EcoKind": "个体工商户",
     *                 "Name": "南京市建邺区大歌星食品店"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海普思投资有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "注销",
     *                 "RegNo": "110107600527132",
     *                 "EcoKind": "个体（内地）",
     *                 "Name": "北京万达星歌烟酒商店"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "1000万元人民币",
     *                 "RegNo": "110105016521299",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京达德厚鑫投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "注销",
     *                 "RegNo": "310110600351288",
     *                 "EcoKind": "个体",
     *                 "Name": "上海市杨浦区大歌星食品综合商店"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegNo": "310101000680259",
     *                 "EcoKind": "有限责任公司分公司（自然人独资）",
     *                 "Name": "北京普思投资有限公司上海分公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "注销",
     *                 "RegNo": "310225600370456",
     *                 "EcoKind": "个体",
     *                 "Name": "上海市浦东新区周浦镇新歌食品商店"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegCap": "100万元人民币",
     *                 "RegNo": "110105016521346",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京汇德信投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:45",
     *                 "Status": "存续",
     *                 "RegNo": "330522000184262",
     *                 "EcoKind": "个人独资企业",
     *                 "Name": "珺娱（湖州）文化发展中心"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "2000万元人民币",
     *                 "RegNo": "110105012460853",
     *                 "EcoKind": "有限责任公司(自然人独资)",
     *                 "Name": "北京普思投资有限公司"
     *             }
     *         ],
     *         "CIAForeignOffices": [
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "10万元人民币",
     *                 "Position": "执行董事,经理",
     *                 "RegNo": "110105020574345",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京普达鑫投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "10万元人民币",
     *                 "Position": "执行董事,经理",
     *                 "RegNo": "",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京普惠思投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "10万元人民币",
     *                 "Position": "经理,执行董事",
     *                 "RegNo": "110105020574466",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京昌盛四海投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "Position": "监事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划体育文化有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "Position": "监事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划演出经纪有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "4179.7304万人民币",
     *                 "Position": "董事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海网鱼信息科技有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "12884.6436万人民币",
     *                 "Position": "董事长",
     *                 "RegNo": "310113001373438",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海熊猫互娱文化有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "注销",
     *                 "RegCap": "30万",
     *                 "Position": "监事",
     *                 "RegNo": "610103100001977",
     *                 "EcoKind": "有限责任公司(自然人独资)",
     *                 "Name": "西安市碑林区大歌星餐饮娱乐有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "注销",
     *                 "RegCap": "30万人民币",
     *                 "Position": "监事",
     *                 "RegNo": "5101042007639",
     *                 "EcoKind": "有限责任公司(自然人投资或控股)",
     *                 "Name": "成都市锦江区大歌星餐饮娱乐有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "100000万人民币",
     *                 "Position": "董事",
     *                 "RegNo": "310000400782886",
     *                 "EcoKind": "有限责任公司（台港澳法人独资）",
     *                 "Name": "飞凡电子商务有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "Position": "执行董事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海普思投资有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "注销",
     *                 "Position": "",
     *                 "RegNo": "110107600527132",
     *                 "EcoKind": "个体（内地）",
     *                 "Name": "北京万达星歌烟酒商店"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "1000万元人民币",
     *                 "Position": "执行董事,经理",
     *                 "RegNo": "110105016521299",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京达德厚鑫投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "Position": "董事长",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉云集新媒体有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "Position": "监事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划影视文化有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "1000万元人民币",
     *                 "Position": "执行董事",
     *                 "RegNo": "440003000137855",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "珠海横琴普斯投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "44000万人民币",
     *                 "Position": "董事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股的法人独资）",
     *                 "Name": "上海新飞凡电子商务有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:46",
     *                 "Status": "存续",
     *                 "RegCap": "13860.9431万元人民币",
     *                 "Position": "监事",
     *                 "RegNo": "110108003312259",
     *                 "EcoKind": "其他股份有限公司(非上市)",
     *                 "Name": "北京英雄互娱科技股份有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "存续",
     *                 "RegCap": "100000万人民币",
     *                 "Position": "董事",
     *                 "EcoKind": "其他股份有限公司(非上市)",
     *                 "Name": "大连万达集团股份有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "存续",
     *                 "RegCap": "100万元人民币",
     *                 "Position": "经理,执行董事",
     *                 "RegNo": "110105016521346",
     *                 "EcoKind": "其他有限责任公司",
     *                 "Name": "北京汇德信投资管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "存续",
     *                 "RegCap": "10000万人民币",
     *                 "Position": "监事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划文化发展有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "存续",
     *                 "RegCap": "1000万人民币",
     *                 "Position": "监事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划音乐有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "存续",
     *                 "RegCap": "1111.11万人民币",
     *                 "Position": "监事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海爱洛星食品有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "存续",
     *                 "RegCap": "2000万元人民币",
     *                 "Position": "执行董事,经理",
     *                 "RegNo": "110105012460853",
     *                 "EcoKind": "有限责任公司(自然人独资)",
     *                 "Name": "北京普思投资有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "存续",
     *                 "RegCap": "1333.3325万人民币",
     *                 "Position": "监事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司（自然人投资或控股）",
     *                 "Name": "上海香蕉计划电子游戏有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "注销",
     *                 "RegCap": "50.000000万元人民币",
     *                 "Position": "监事",
     *                 "RegNo": "330212000097826",
     *                 "EcoKind": "私营有限责任公司(自然人控股或私营性质企业控股)",
     *                 "Name": "宁波市鄞州大歌星餐饮娱乐有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "存续",
     *                 "RegCap": "1000.000000万人民币",
     *                 "Position": "执行董事",
     *                 "RegNo": "",
     *                 "EcoKind": "有限责任公司",
     *                 "Name": "平潭普思资产管理有限公司"
     *             },
     *             {
     *                 "InputDate": "2019-04-14 01:12:47",
     *                 "Status": "存续",
     *                 "RegCap": "1000万",
     *                 "Position": "监事",
     *                 "RegNo": "210200000248224",
     *                 "EcoKind": "有限责任公司(自然人投资或控股)",
     *                 "Name": "蓝泰科技（大连）有限公司"
     *             }
     *         ]
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GetStockRelationInfo(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        String compName = (String) params.getOrDefault("fullName", "");
        String personName = (String) params.getOrDefault("personName", "");
        JSONObject result = new JSONObject();
        int i = 0;
        for (Map.Entry<String, Object> entry : DeconstructService.GetStockRelationInfoMap.entrySet()) {
            String sql;
            if(true){
                sql =  S.fmt("select d.*, qd.Oper_Name, qd.Start_Date from %s d left join QCC_DETAILS qd on d.name = qd.inner_company_name where d.inner_company_name = '%s' and d.per_name = '%s'", entry.getValue(), compName, personName);
            } else {
                sql = S.fmt("select * from %s where inner_company_name = '%s' and per_name = '%s'", entry.getValue(), compName, personName);
            }
            List<JSONObject> array = listQuery(sql, params);
            for (JSONObject object : array) {
                object.entrySet().removeIf(_entry -> _entry.getKey().startsWith("inner"));
            }
            convertToQccStyle(array);
            result.put(entry.getKey(), array);
        }
        return result;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/ECIRelationV4/GenerateMultiDimensionalTreeCompanyMap 企业图谱
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyNo 公司keyNo
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {string} Name 名称
     * @apiSuccess {string} KeyNo 内部KeyNo
     * @apiSuccess {string} Category 1:当前公司2：对外投资3：股东4：高管5：法院公告6：裁判文书8：历史股东9：历史法人
     * @apiSuccess {string} ShortName 简称
     * @apiSuccess {string} Count 数量
     * @apiSuccess {string} Level 层级
     * @apiSuccess {node[]} Children 以上字段的树结构
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "KeyNo": "4659626b1e5e43f1bcad8c268753216e",
     *         "Category": "1",
     *         "Level": "0",
     *         "ShortName": "北京小桔",
     *         "Count": "79",
     *         "Children": [
     *             {
     *                 "Category": "2",
     *                 "Level": "0",
     *                 "ShortName": "对外投资",
     *                 "Count": "23",
     *                 "Children": [
     *                     {
     *                         "KeyNo": "afb3daf1797df272997f22b143c964f6",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "北京车胜",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京车胜科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "274d9311979595d54821e1d9e8d73e36",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "北京再造",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京再造科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "7bb231394fe204e1a3c3e6cfdb24ec21",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴滴旅行",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "苏州滴滴旅行科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "a1b4b72b29c862926c7e715c365640c8",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "杭州青奇",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州青奇科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "6de14b4eeddfad4c9d320e7635c664e5",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "小木吉软件",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州小木吉软件科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "aa0eb37b66413114095520caa0c15961",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "运达无限",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京运达无限科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "3fe7fa121a61e0d869a52b4752b9e272",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴滴汽车服务",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州滴滴汽车服务有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "eb8957f85861e53f023ac63a419a2ce4",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "天津舒行",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "天津舒行科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "39b198dc9b68b3958e21594e4071cdf5",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "橙资互联网",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "橙资（上海）互联网科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "05c090155e36541c83e9ab59ab3f402d",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "橙子投资",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "嘉兴橙子投资管理有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "66658d9633f7002768bbacbd02efb226",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "桔子共享投资",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "嘉兴桔子共享投资合伙企业（有限合伙）"
     *                     },
     *                     {
     *                         "KeyNo": "3049b862cd42fe8cb946497bd075ad20",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "小桔子投资合",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "嘉兴小桔子投资合伙企业（有限合伙）"
     *                     },
     *                     {
     *                         "KeyNo": "94c0f5d6508919e29aff5a66f86ebca1",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴图",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "滴图（北京）科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "9376917a29748c8a7590d16fa01d3e7c",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "上海桔道",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "上海桔道网络科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "d1c68c75aedf702f2422bebf07b1bd2b",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "北岸商业保理",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "深圳北岸商业保理有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "44d5992e16ff513c91f86c5b0fdf2227",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴滴出行",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "滴滴出行科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "e108693960d310ee9e5d663c0f3227ca",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴滴商业服务",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "滴滴商业服务有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "d92b9c60a5e4b3456812eb0a3e4bba63",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "博通畅达",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京博通畅达科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "8bd250d6875caa56dc9a6747b49689c0",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "吾步信息技术",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "上海吾步信息技术有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "307efc57070d09ba32b8f01ee1322647",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "北京长亭",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京长亭科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "3a079aa5beaf85378a2dba72ec6d563a",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "通达无限",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京通达无限科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "2bbaaaf09d9877b8dd851a02ad9600a2",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "奇漾信息技术",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "上海奇漾信息技术有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "c02337970cc15c084571cf1c982f8e22",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "杭州快智",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州快智科技有限公司"
     *                     }
     *                 ],
     *                 "Name": "对外投资"
     *             },
     *             {
     *                 "Category": "3",
     *                 "Level": "0",
     *                 "ShortName": "股东",
     *                 "Count": "5",
     *                 "Children": [
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_王刚",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "王刚"
     *                     },
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_张博",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "张博"
     *                     },
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_程维",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "程维"
     *                     },
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_陈汀",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "陈汀"
     *                     },
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_吴睿",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "吴睿"
     *                     }
     *                 ],
     *                 "Name": "股东"
     *             },
     *             {
     *                 "Category": "4",
     *                 "Level": "0",
     *                 "ShortName": "高管",
     *                 "Count": "3",
     *                 "Children": [
     *                     {
     *                         "Category": "4",
     *                         "Level": "0",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "程维"
     *                     },
     *                     {
     *                         "Category": "4",
     *                         "Level": "0",
     *                         "ShortName": "经理",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "程维"
     *                     },
     *                     {
     *                         "Category": "4",
     *                         "Level": "0",
     *                         "ShortName": "监事",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "吴睿"
     *                     }
     *                 ],
     *                 "Name": "高管"
     *             },
     *             {
     *                 "Category": "8",
     *                 "Level": "0",
     *                 "ShortName": "历史股东",
     *                 "Count": "1",
     *                 "Children": [
     *                     {
     *                         "Category": "8",
     *                         "Level": "0",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "徐涛"
     *                     }
     *                 ],
     *                 "Name": "历史股东"
     *             },
     *             {
     *                 "Category": "9",
     *                 "Level": "0",
     *                 "ShortName": "历史法人",
     *                 "Count": "0",
     *                 "Children": [
     *                 ],
     *                 "Name": "历史法人"
     *             },
     *             {
     *                 "Category": "6",
     *                 "Level": "0",
     *                 "ShortName": "裁判文书",
     *                 "Count": "47",
     *                 "Children": [
     *                     {
     *                         "KeyNo": "7c38ac16b20be711a8f8825a11323db6",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京畅行信息技术有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "0d780bcdc7ad871d824c7dce51973af0",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "武汉兴广亚汽车租赁有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "44d5992e16ff513c91f86c5b0fdf2227",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "滴滴出行科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "ccb6c71c2be0ad917d194e34b1b8292f",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京嘀嘀无限科技发展有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "5a8ee7ebaa5c091e328f0693e204beb9",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "高德信息技术有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "b38d90ee05f8c4f6a2a800d44ef12355",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "高德软件有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "1a8ec2bd97cbe5940f2c234976f4f42c",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中智项目外包服务有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "48d4019cbee2e41ba614205894848661",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京东方车云信息技术有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "f289e491413cfc3846b16f5eca46634b",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "宁波市科技园区妙影电子有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "149e40dd5827381f410076824625f872",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州妙影微电子有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "cbdf2d8f1404d5c43bdf67a932460098",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "深圳市唐氏龙行汽车租赁有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "acea4bb4a5bd3698caa3bd5d1aea6cdd",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国人民财产保险股份有限公司深圳市分公司"
     *                     },
     *                     {
     *                         "KeyNo": "73dc248532ff345f11d5dd8ac2fd8278",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京万古恒信科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "dfb69ffd668429fa619d1fbee9ac4965",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "比亚迪汽车工业有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "bbccdb715f39775aa18016d6d3e8bbd1",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "深圳市迪滴新能源汽车租赁有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "bedd0b1c57c8ea2e4e2f1c57a5683165",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国平安财产保险股份有限公司深圳市龙岗支公司"
     *                     },
     *                     {
     *                         "KeyNo": "4d109516d62f4dd04eb8942460c330d6",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国人民财产保险股份有限公司泉州市分公司"
     *                     },
     *                     {
     *                         "KeyNo": "dcad4bc1a5a4de2c7be5b6be79e80c0e",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国平安财产保险股份有限公司陕西分公司"
     *                     },
     *                     {
     *                         "KeyNo": "3f1930ed2fe91302978aee2b273231f9",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中华联合财产保险股份有限公司西安中心支公司"
     *                     },
     *                     {
     *                         "KeyNo": "1d780a0fcddf561272c35dffbe213319",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "西安志华土方工程有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "aca913da8a64ac18db164eef3543528f",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "长春金城汽车贸易有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "69b0b3cba566d2be56b45934c39c65f0",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国平安财产保险股份有限公司四川分公司"
     *                     },
     *                     {
     *                         "KeyNo": "2a835b4eb8a584ae34f878b33460fead",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国人民财产保险股份有限公司金华市分公司"
     *                     },
     *                     {
     *                         "KeyNo": "86d75562aa40a2b085089fc77ccbba61",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "华泰财产保险有限公司深圳分公司"
     *                     },
     *                     {
     *                         "KeyNo": "44ee722fd23e701019c60d1adb314315",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国平安财产保险股份有限公司深圳分公司"
     *                     },
     *                     {
     *                         "KeyNo": "d0328234128aa5cdea42395c34bd8c5c",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "大连百名汽车租赁有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "0c0d20305779153c5c20002c1e806770",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "浙江外企德科人力资源服务有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "c02337970cc15c084571cf1c982f8e22",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州快智科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "6d82400bcb5d4944e53080a521100913",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国平安财产保险股份有限公司浙江分公司"
     *                     },
     *                     {
     *                         "KeyNo": "c290851899ee5306fda9706fed8ffa61",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国太平洋财产保险股份有限公司西安中心支公司"
     *                     },
     *                     {
     *                         "KeyNo": "e7da6d63073b887c3885ada53fa22e7d",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国人民财产保险股份有限公司成都市分公司"
     *                     },
     *                     {
     *                         "KeyNo": "3239389ea6405e28bbf466ea0631e4df",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国人民财产保险股份有限公司郑州市分公司"
     *                     },
     *                     {
     *                         "KeyNo": "f59629208c9f6088b0eef880267ead76",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "太平财产保险有限公司郑州分公司"
     *                     },
     *                     {
     *                         "KeyNo": "8dd7a2fe01fe2b5e6e8143d68440353d",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "华安财产保险股份有限公司郑州中心支公司"
     *                     },
     *                     {
     *                         "KeyNo": "6ddf5e6cdcf645f670c7c0d8a8de0433",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "华泰财产保险有限公司北京分公司"
     *                     },
     *                     {
     *                         "KeyNo": "59def76b4adae88ebd768b46272f8241",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "首汽租赁有限责任公司"
     *                     },
     *                     {
     *                         "KeyNo": "6b0f0f3f784d8cc1bf339cfc55f4dc96",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国人民财产保险股份有限公司苏州市太湖国家旅游度假区支公司"
     *                     },
     *                     {
     *                         "KeyNo": "gfffa08d683fd30a69de11f216de1776",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州整形医院"
     *                     },
     *                     {
     *                         "KeyNo": "1e3c58e9518324d55bca783423aafa94",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "永诚财产保险股份有限公司双流支公司"
     *                     },
     *                     {
     *                         "KeyNo": "e78d6de329b6dc6a34be158f968a639b",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国人寿财产保险股份有限公司深圳市分公司"
     *                     },
     *                     {
     *                         "KeyNo": "3a079aa5beaf85378a2dba72ec6d563a",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京通达无限科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "1f12f7d5879a7249181eab15abbb4969",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "熹锦实业(上海)有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "1f12f7d5879a7249181eab15abbb4969",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "熹锦实业（上海）有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "a2c4b70a848e7cf3beaf1406839c0d4d",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国人民财产保险股份有限公司扬州市分公司"
     *                     },
     *                     {
     *                         "KeyNo": "d53d1d2759b4903253c0e9910e890eac",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国石化集团江苏石油勘探局"
     *                     },
     *                     {
     *                         "KeyNo": "6ebe7ad82a3a3d2eb886b429edafa6b4",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国太平洋财产保险股份有限公司扬州中心支公司"
     *                     },
     *                     {
     *                         "KeyNo": "80cd056a31c603ca594f27a05a8fa616",
     *                         "Category": "6",
     *                         "Level": "0",
     *                         "ShortName": "北京小桔",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "中国人寿财产保险股份有限公司阜阳市颍州区支公司"
     *                     }
     *                 ],
     *                 "Name": "裁判文书"
     *             },
     *             {
     *                 "Category": "5",
     *                 "Level": "0",
     *                 "ShortName": "法院公告",
     *                 "Count": "0",
     *                 "Children": [
     *                 ],
     *                 "Name": "法院公告"
     *             }
     *         ],
     *         "Name": "北京小桔科技有限公司"
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GenerateMultiDimensionalTreeCompanyMap(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return convertToTree(
            listQuery("qcc.查询投资图谱", params)
        );
    }

    /**
     * @api {get} {企查查数据查询服务地址}/ECIRelationV4/GetCompanyEquityShareMap 股权结构图
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyNo 公司keyNo
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {string} Name 公司名称或者人名
     * @apiSuccess {string} KeyNo 当前股东的公司keyNo
     * @apiSuccess {string} Category 1是公司，2是个人
     * @apiSuccess {string} StockType 股东类型
     * @apiSuccess {string} Count 对应的childrencount
     * @apiSuccess {string} FundedRatio 出资比例
     * @apiSuccess {string} SubConAmt 出资金额
     * @apiSuccess {string} IsAbsoluteController 是否绝对控股
     * @apiSuccess {string} Grade 对应的层级
     * @apiSuccess {string} OperName 法人代表
     * @apiSuccess {string} InParentActualRadio 当前股东所在公司在该公司父级中所占实际比例
     * @apiSuccess {node[]} Children 以上所有字段的树结构
     *
     * @apiSuccess {object[]} ActualControllerLoopPath 实际控股信息
     * @apiSuccess {string} ActualControllerLoopPath.Name 实际控股名称
     * @apiSuccess {string} ActualControllerLoopPath.StockType 实际控股类型
     * @apiSuccess {string} ActualControllerLoopPath.KeyNo 公司keyNo
     * @apiSuccess {string} ActualControllerLoopPath.SubConAmt 出资额
     * @apiSuccess {string} ActualControllerLoopPath.FundedRatio 出资比例
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "KeyNo": "4659626b1e5e43f1bcad8c268753216e",
     *         "OperName": "程维",
     *         "ActualControllerLoopPath": [
     *             {
     *                 "KeyNo": "",
     *                 "FundedRatio": "49.1900%",
     *                 "SubConAmt": "491.9万元",
     *                 "StockType": "自然人股东",
     *                 "Name": "程维"
     *             }
     *         ],
     *         "InParentActualRadio": "0",
     *         "Category": "1",
     *         "FundedRatio": "100%",
     *         "Grade": "1",
     *         "IsAbsoluteController": "True",
     *         "Count": "5",
     *         "Children": [
     *             {
     *                 "KeyNo": "7B0CFF16CA8BA1EC_",
     *                 "InParentActualRadio": "0.4919",
     *                 "Category": "2",
     *                 "FundedRatio": "49.1900%",
     *                 "Grade": "2",
     *                 "IsAbsoluteController": "True",
     *                 "Count": "0",
     *                 "Children": [
     *                 ],
     *                 "Name": "程维"
     *             },
     *             {
     *                 "InParentActualRadio": "0.48225",
     *                 "Category": "2",
     *                 "FundedRatio": "48.2250%",
     *                 "Grade": "2",
     *                 "IsAbsoluteController": "False",
     *                 "Count": "0",
     *                 "Children": [
     *                 ],
     *                 "Name": "王刚"
     *             },
     *             {
     *                 "InParentActualRadio": "0.01553",
     *                 "Category": "2",
     *                 "FundedRatio": "1.5530%",
     *                 "Grade": "2",
     *                 "IsAbsoluteController": "False",
     *                 "Count": "0",
     *                 "Children": [
     *                 ],
     *                 "Name": "张博"
     *             },
     *             {
     *                 "InParentActualRadio": "0.00723",
     *                 "Category": "2",
     *                 "FundedRatio": "0.7230%",
     *                 "Grade": "2",
     *                 "IsAbsoluteController": "False",
     *                 "Count": "0",
     *                 "Children": [
     *                 ],
     *                 "Name": "吴睿"
     *             },
     *             {
     *                 "InParentActualRadio": "0.00309",
     *                 "Category": "2",
     *                 "FundedRatio": "0.3090%",
     *                 "Grade": "2",
     *                 "IsAbsoluteController": "False",
     *                 "Count": "0",
     *                 "Children": [
     *                 ],
     *                 "Name": "陈汀"
     *             }
     *         ],
     *         "Name": "北京小桔科技有限公司"
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GetCompanyEquityShareMap(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        List<JSONObject> array = listQuery("qcc.查询股权结构图", params);
        JSONObject main = convertToTree(array);
        List<JSONObject> aclp = listQuery("qcc.查询股权结构-实际控股信息表", params);
        main.put("ActualControllerLoopPath", aclp);
        return main;
    }

    /**
     * @api {get} {企查查数据查询服务地址}/ECIRelationV4/SearchTreeRelationMap 投资图谱
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyNo 公司keyNo
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {string} Name 名称
     * @apiSuccess {string} KeyNo 内部KeyNo
     * @apiSuccess {string} Category 数据类别，1(当前公司)，2(对外投资)，3(股东)
     * @apiSuccess {string} ShortName 简称
     * @apiSuccess {string} Count 数量
     * @apiSuccess {string} Level 层级
     * @apiSuccess {node[]} Children 树结构，包含以上字段
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "KeyNo": "4659626b1e5e43f1bcad8c268753216e",
     *         "Category": "1",
     *         "Level": "0",
     *         "ShortName": "北京小桔",
     *         "Count": "28",
     *         "Children": [
     *             {
     *                 "Category": "3",
     *                 "Level": "0",
     *                 "ShortName": "股东",
     *                 "Count": "5",
     *                 "Children": [
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_王刚",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "王刚"
     *                     },
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_张博",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "张博"
     *                     },
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_程维",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "程维"
     *                     },
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_陈汀",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "陈汀"
     *                     },
     *                     {
     *                         "KeyNo": "4659626b1e5e43f1bcad8c268753216e_吴睿",
     *                         "Category": "3",
     *                         "Level": "1",
     *                         "ShortName": "自然人股东",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "吴睿"
     *                     }
     *                 ],
     *                 "Name": "股东"
     *             },
     *             {
     *                 "Category": "2",
     *                 "Level": "0",
     *                 "ShortName": "对外投资",
     *                 "Count": "23",
     *                 "Children": [
     *                     {
     *                         "KeyNo": "afb3daf1797df272997f22b143c964f6",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "北京车胜",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京车胜科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "274d9311979595d54821e1d9e8d73e36",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "北京再造",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京再造科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "7bb231394fe204e1a3c3e6cfdb24ec21",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴滴旅行",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "苏州滴滴旅行科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "a1b4b72b29c862926c7e715c365640c8",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "杭州青奇",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州青奇科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "6de14b4eeddfad4c9d320e7635c664e5",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "小木吉软件",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州小木吉软件科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "aa0eb37b66413114095520caa0c15961",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "运达无限",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京运达无限科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "3fe7fa121a61e0d869a52b4752b9e272",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴滴汽车服务",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州滴滴汽车服务有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "eb8957f85861e53f023ac63a419a2ce4",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "天津舒行",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "天津舒行科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "39b198dc9b68b3958e21594e4071cdf5",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "橙资互联网",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "橙资（上海）互联网科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "05c090155e36541c83e9ab59ab3f402d",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "橙子投资",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "嘉兴橙子投资管理有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "66658d9633f7002768bbacbd02efb226",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "桔子共享投资",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "嘉兴桔子共享投资合伙企业（有限合伙）"
     *                     },
     *                     {
     *                         "KeyNo": "3049b862cd42fe8cb946497bd075ad20",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "小桔子投资合",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "嘉兴小桔子投资合伙企业（有限合伙）"
     *                     },
     *                     {
     *                         "KeyNo": "94c0f5d6508919e29aff5a66f86ebca1",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴图",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "滴图（北京）科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "9376917a29748c8a7590d16fa01d3e7c",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "上海桔道",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "上海桔道网络科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "d1c68c75aedf702f2422bebf07b1bd2b",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "北岸商业保理",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "深圳北岸商业保理有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "44d5992e16ff513c91f86c5b0fdf2227",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴滴出行",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "滴滴出行科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "e108693960d310ee9e5d663c0f3227ca",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "滴滴商业服务",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "滴滴商业服务有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "d92b9c60a5e4b3456812eb0a3e4bba63",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "博通畅达",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京博通畅达科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "8bd250d6875caa56dc9a6747b49689c0",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "吾步信息技术",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "上海吾步信息技术有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "307efc57070d09ba32b8f01ee1322647",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "北京长亭",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京长亭科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "3a079aa5beaf85378a2dba72ec6d563a",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "通达无限",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "北京通达无限科技有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "2bbaaaf09d9877b8dd851a02ad9600a2",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "奇漾信息技术",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "上海奇漾信息技术有限公司"
     *                     },
     *                     {
     *                         "KeyNo": "c02337970cc15c084571cf1c982f8e22",
     *                         "Category": "2",
     *                         "Level": "1",
     *                         "ShortName": "杭州快智",
     *                         "Count": "0",
     *                         "Children": [
     *                         ],
     *                         "Name": "杭州快智科技有限公司"
     *                     }
     *                 ],
     *                 "Name": "对外投资"
     *             }
     *         ],
     *         "Name": "北京小桔科技有限公司"
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object SearchTreeRelationMap(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        List<JSONObject> array = listQuery("qcc.查询企业族谱", params);
        return convertToTree(array);
    }




    /**
     * @api {get} {企查查数据查询服务地址}/ECIV4/SearchFresh 新增公司列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyword 关键字
     *
     * @apiSuccess {string} KeyNo 内部KeyNo
     * @apiSuccess {string} Name 公司名称
     * @apiSuccess {string} OperName 法人名称
     * @apiSuccess {string} StartDate 成立日期
     * @apiSuccess {string} Status 企业状态
     * @apiSuccess {string} No 注册号
     * @apiSuccess {string} CreditCode 社会统一信用代码
     * @apiSuccess {string} RegistCapi 注册资本
     * @apiSuccess {string} Address 地址
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 10,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "KeyNo": "c1ad948f28014ad8cd412feae7ad7324",
     *             "RegistCapi": "",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "110108604483716",
     *             "CreditCode": "92110108MA00GTG727",
     *             "OperName": "陈立国",
     *             "Address": "北京市海淀区圆明园西路2号院11号112室",
     *             "Name": "北京食香优源餐饮管理中心"
     *         },
     *         {
     *             "KeyNo": "8160fbebc5ebd81ff9e46aaab2d65da4",
     *             "RegistCapi": "5000万元人民币",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "",
     *             "CreditCode": "91110109MA00GT10X0",
     *             "OperName": "马红利",
     *             "Address": "北京市门头沟区石龙开发区平安路7号LQ0022",
     *             "Name": "北京弘利宜居房地产开发有限公司"
     *         },
     *         {
     *             "KeyNo": "7919266d1488404f9e6a85d76b136887",
     *             "RegistCapi": "",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "110116604199841",
     *             "CreditCode": "92110116MA00GT310K",
     *             "OperName": "邵仕龙",
     *             "Address": "北京市怀柔区北房镇宰相庄村111号",
     *             "Name": "北京国旭龙商店"
     *         },
     *         {
     *             "KeyNo": "39bfe9a434e1cc812c78f3df27c1f602",
     *             "RegistCapi": "",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "110108604483708",
     *             "CreditCode": "92110108MA00GTFT6H",
     *             "OperName": "原五根",
     *             "Address": "北京市海淀区圆明园西路2号院8号102室",
     *             "Name": "北京鑫食健源餐饮管理中心"
     *         },
     *         {
     *             "KeyNo": "65fbd3478a120e20f76d6923aa1f5c8f",
     *             "RegistCapi": "10万元人民币",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "",
     *             "CreditCode": "91110101MA00GRWH06",
     *             "OperName": "颜廷坝",
     *             "Address": "北京市东城区东花市南里东区3号楼1层B06",
     *             "Name": "北京虎视健康咨询有限公司"
     *         },
     *         {
     *             "KeyNo": "8f8bd462c8330f60220ddbc9f7e85eaf",
     *             "RegistCapi": "",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "110116604199905",
     *             "CreditCode": "92110116MA00GTJA6L",
     *             "OperName": "张国",
     *             "Address": "北京市怀柔区雁栖湖南岸(北京市律师培训中心5幢1层)",
     *             "Name": "北京悦文军商店"
     *         },
     *         {
     *             "KeyNo": "d2fb554448bd83656c62003f32fa2ad2",
     *             "RegistCapi": "1000万元人民币",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "",
     *             "CreditCode": "91110109MA00GTBF61",
     *             "OperName": "杨慧如",
     *             "Address": "北京市门头沟区石龙经济开发区永安路20号1号楼14层2单元1401室-DXF061",
     *             "Name": "北京大地纯风电子商务有限公司"
     *         },
     *         {
     *             "KeyNo": "5d8da44e84978183e1d43bc934a5f9e6",
     *             "RegistCapi": "200万元人民币",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "",
     *             "CreditCode": "91110109MA00GT4686",
     *             "OperName": "肖海洋",
     *             "Address": "北京市门头沟区雁翅镇高芹路1号院YC-0095",
     *             "Name": "北京元析科技有限公司"
     *         },
     *         {
     *             "KeyNo": "365a227a6dc3613cc369f5cdcb19cdd8",
     *             "RegistCapi": "500万元人民币",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "",
     *             "CreditCode": "91110105MA00GTB54N",
     *             "OperName": "赵鹏",
     *             "Address": "北京市朝阳区广渠东路唐家村23幢18-A",
     *             "Name": "北京黑马先生服装有限公司"
     *         },
     *         {
     *             "KeyNo": "2c120f9f2fae38cb2a2210bf2624b5f8",
     *             "RegistCapi": "100万元人民币",
     *             "StartDate": "2017-08-04 12:00:00",
     *             "Status": "存续（在营、开业、在册）",
     *             "No": "",
     *             "CreditCode": "91110109MA00GRQH29",
     *             "OperName": "宋凯",
     *             "Address": "北京市门头沟区清水镇洪水口村8号",
     *             "Name": "北京豫峰园农业科技有限公司"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object SearchFresh(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询新增的公司信息表", params);
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytAdminLicens 历史行政许可
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     *
     *
     * @apiSuccess {object[]} EciList 历史工商行政许可
     * @apiSuccess {string} EciList.LicensDocNo 许可文件编号
     * @apiSuccess {string} EciList.LicensDocName 许可文件名称
     * @apiSuccess {string} EciList.LicensOffice 许可机关
     * @apiSuccess {string} EciList.LicensContent 许可内容
     * @apiSuccess {string} EciList.ValidityFrom 有效期自
     * @apiSuccess {string} EciList.ValidityTo 有效期至
     *
     * @apiSuccess {object[]} CreditChinaList 历史信用中国行政许可
     * @apiSuccess {string} CreditChinaList.CaseNo 编号
     * @apiSuccess {string} CreditChinaList.Name 项目名称
     * @apiSuccess {string} CreditChinaList.LiAnDate 决定日期
     * @apiSuccess {string} CreditChinaList.Province 地域
     * @apiSuccess {string} CreditChinaList.OwnerName 公司
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "EciList": [
     *         ],
     *         "CreditChinaList": [
     *             {
     *                 "CaseNo": "2011.65",
     *                 "OwnerName": "河南新飞电器有限公司",
     *                 "LiAnDate": "2011-12-09 12:00:00",
     *                 "Province": "总局",
     *                 "Name": "延期缴纳税款"
     *             }
     *         ]
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GetHistorytAdminLicens(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
         return newJsonObject(
            "EciList", listQuery("qcc.查询历史行政许可-工商行政许可信息表", params),
            "CreditChinaList", listQuery("qcc.查询历史行政许可-信用中国行政许可信息表", params)
         );
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytAdminPenalty 历史行政处罚
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {object[]} EciList 工商行政处罚
     * @apiSuccess {string} EciList.DocNo 文号
     * @apiSuccess {string} EciList.PenaltyType 违法行为类型
     * @apiSuccess {string} EciList.Content 处罚内容
     * @apiSuccess {string} EciList.PenaltyDate 决定日期
     * @apiSuccess {string} EciList.PublicDate 作出行政公示日期
     * @apiSuccess {string} EciList.OfficeName 决定机关
     *
     * @apiSuccess {object[]} CreditChinaList 信用中国行政处罚
     * @apiSuccess {string} CreditChinaList.CaseNo 决定文书号
     * @apiSuccess {string} CreditChinaList.Name 处罚名称
     * @apiSuccess {string} CreditChinaList.LiAnDate 决定时间
     * @apiSuccess {string} CreditChinaList.Province 省份
     * @apiSuccess {string} CreditChinaList.OwnerName 公司
     * @apiSuccess {string} CreditChinaList.CaseReason 案由
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "EciList": [
     *             {
     *                 "PenaltyDate": "2016-11-25 12:00:00",
     *                 "Content": "罚款金额0.2万元;没收金额0.0万元",
     *                 "DocNo": "津红国税罚〔2016〕20021",
     *                 "PenaltyType": "违反税收管理",
     *                 "OfficeName": "天津市红桥区国家税务局"
     *             },
     *             {
     *                 "Content": "罚款金额0.2万元;没收金额0.0万元",
     *                 "DocNo": "津红国税罚〔2016〕20021",
     *                 "PenaltyType": "违反税收管理",
     *                 "OfficeName": "天津市红桥区国家税务局"
     *             }
     *         ],
     *         "CreditChinaList": [
     *         ]
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GetHistorytAdminPenalty(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return newJsonObject(
            "EciList", listQuery("qcc.查询历史行政处罚-工商行政处罚信息表", params),
            "CreditChinaList", listQuery("qcc.查询历史行政处罚-信用中国行政处罚信息表", params)
        );
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytPledge 历史股权出质
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} RegistNo 登记编号
     * @apiSuccess {string} Pledgor 出质人
     * @apiSuccess {string} Pledgee 质权人
     * @apiSuccess {string} PledgedAmount 出质股权数额
     * @apiSuccess {string} RegDate 股权出质设立登记日期
     * @apiSuccess {string} PublicDate 公布日期
     * @apiSuccess {string} Status 状态
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 1,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "Pledgee": "鑫融基投资担保有限公司",
     *             "Status": "无效",
     *             "RegistNo": "410700201400000043",
     *             "Pledgor": "堵召辉",
     *             "RegDate": "2014-09-10 12:00:00",
     *             "PublicDate": "2015-07-01 12:00:00",
     *             "PledgedAmount": "330万人民币元"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetHistorytPledge(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询历史股权出质信息表", params);
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytMPledge 历史动产抵押
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} RegisterNo 登记编号
     * @apiSuccess {string} RegisterDate 登记日期
     * @apiSuccess {string} RegisterOffice 登记机关
     * @apiSuccess {string} DebtSecuredAmount 被担保债权数额
     * @apiSuccess {string} Status 状态
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 1,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "Status": "有效",
     *             "RegisterOffice": "资阳市工商行政管理局",
     *             "RegisterNo": "",
     *             "RegisterDate": "2015-09-16 12:00:00",
     *             "DebtSecuredAmount": "1321.8258万元"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    // FIXME: 2019/4/12 
    private Object GetHistorytMPledge(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询历史动产抵押信息表", params);
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytSessionNotice 历史开庭公告
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} Id Id值
     * @apiSuccess {string} CaseReason 案由
     * @apiSuccess {string} ProsecutorList 公诉人/原告/上诉人/申请人
     * @apiSuccess {string} DefendantList 被告人/被告/被上诉人/被申请人
     * @apiSuccess {string} ExecuteGov 执行法院
     * @apiSuccess {string} CaseNo 案号
     * @apiSuccess {string} LiAnDate 开庭日期
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 4
     *     },
     *     "Result": [
     *         {
     *             "CaseNo": "(2017)川01民终4786号",
     *             "ProsecutorList": "中国平安财产保险股份有限公司四川分公司",
     *             "LiAnDate": "2017-04-12 12:00:00",
     *             "CaseReason": "机动车交通事故责任纠纷",
     *             "Id": "8226945fd8445ebfc0df482dd5f3b82f5",
     *             "DefendantList": "王国强\t北京小桔科技有限公司\t何立新\t蒋海涛\t陈玉刚\t曾翠平\t蒋习武\t李美琪",
     *             "ExecuteGov": "四川省成都市中级人民法院"
     *         },
     *         {
     *             "CaseNo": "(2017)沪0115民初15113号",
     *             "ProsecutorList": "柳正浩",
     *             "LiAnDate": "2017-03-15 12:00:00",
     *             "CaseReason": "生命权、健康权、身体权纠纷",
     *             "Id": "53243f38c04c0cd710dcf941ee3d5cc05",
     *             "DefendantList": "杜超杰\t北京小桔科技有限公司",
     *             "ExecuteGov": "上海市浦东新区人民法院"
     *         },
     *         {
     *             "CaseNo": "(2017)沪0112民初4106号",
     *             "ProsecutorList": "左桂军",
     *             "LiAnDate": "2017-03-14 12:00:00",
     *             "CaseReason": "机动车交通事故责任纠纷",
     *             "Id": "487d209e24933dfa94d3aea165b773085",
     *             "DefendantList": "邹建荣\t北京小桔科技有限公司\t沙磊\t上海市闵行区医疗急救中心\t中国人民财产保险股份有限公司上海市分公司",
     *             "ExecuteGov": "上海市闵行区人民法院"
     *         },
     *         {
     *             "CaseNo": "(2017)沪0112民初4103号",
     *             "ProsecutorList": "王庆乐",
     *             "LiAnDate": "2017-03-14 12:00:00",
     *             "CaseReason": "机动车交通事故责任纠纷",
     *             "Id": "7d1da8d2b4b5f8d05eb9b079ecc11d9a5",
     *             "DefendantList": "邹建荣\t北京小桔科技有限公司\t沙磊\t上海市闵行区医疗急救中心\t中国人民财产保险股份有限公司上海市分公司",
     *             "ExecuteGov": "上海市闵行区人民法院"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetHistorytSessionNotice(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询历史开庭公告信息表", params);
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytJudgement 历史裁判文书
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} Id Id值
     * @apiSuccess {string} Court 执行法院
     * @apiSuccess {string} CaseName 案件名称
     * @apiSuccess {string} SubmitDate 发布时间
     * @apiSuccess {string} CaseNo 案件编号
     * @apiSuccess {string} CaseType 案件类型
     * @apiSuccess {string} CaseRole 涉案人员角色
     * @apiSuccess {string} CourtYear 年份
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 3
     *     },
     *     "Result": [
     *         {
     *             "CaseNo": "（2016）京0108民初33393号",
     *             "CourtYear": "2016",
     *             "CaseType": "ms",
     *             "SubmitDate": "2016-11-17 12:00:00",
     *             "CaseName": "张海合与北京小桔科技有限公司网络服务合同纠纷一审民事判决书",
     *             "CaseRole": "[{\"P\":\"张海合\",\"R\":\"原告\"},{\"P\":\"北京小桔科技有限公司\",\"R\":\"被告\"}]",
     *             "Id": "6f6b6dd992c6527acb9bdaacae29fffd",
     *             "Court": "北京市海淀区人民法院"
     *         },
     *         {
     *             "CaseNo": "（2016）京0108民初33183号",
     *             "CourtYear": "2016",
     *             "CaseType": "ms",
     *             "SubmitDate": "2016-11-17 12:00:00",
     *             "CaseName": "庞晶磊与北京小桔科技有限公司合同纠纷一审民事判决书",
     *             "CaseRole": "[{\"P\":\"庞晶磊\",\"R\":\"原告\"},{\"P\":\"北京小桔科技有限公司\",\"R\":\"被告\"}]",
     *             "Id": "7956b180216019230566c4a6e7a06a94",
     *             "Court": "北京市海淀区人民法院"
     *         },
     *         {
     *             "CaseNo": "（2016）浙0602民初9693号",
     *             "CourtYear": "2016",
     *             "CaseType": "ms",
     *             "SubmitDate": "2016-11-16 12:00:00",
     *             "CaseName": "",
     *             "CaseRole": "[{\"P\":\"翁坚超\",\"R\":\"原告\"},{\"P\":\"北京小桔科技有限公司\",\"R\":\"被告\"}]",
     *             "Id": "786440d8293e3a2f81acb63b759c20f8",
     *             "Court": "绍兴市越城区人民法院"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetHistorytJudgement(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询历史裁判文书信息表", params);
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytCourtNotice 历史法院公告
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     *
     * @apiSuccess {string} Id Id值
     * @apiSuccess {string} Category 公告类型
     * @apiSuccess {string} Content 内容
     * @apiSuccess {string} Court 公告人
     * @apiSuccess {string} Party 当事人
     * @apiSuccess {string} Province 省份
     * @apiSuccess {string} PublishPage 刊登版面
     * @apiSuccess {string} SubmitDate 上传日期
     * @apiSuccess {string} PublishDate 公示日期
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 8
     *     },
     *     "Result": [
     *         {
     *             "PublishDate": "2016-01-09 12:00:00",
     *             "Category": "裁判文书",
     *             "Party": "恒大地产集团有限公司、严东",
     *             "SubmitDate": "2016-01-09 12:00:00",
     *             "Content": "严东：本院受理原告恒大地产集团有限公司诉被告严东房屋买卖合同纠纷一案已审理终结。现依法向你公告送达（2015）穗云法民四初字451号民事判决书一份。自公告之日起60日内来本院领取民事判决书，逾期则视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于广东省广州市中级人民法院。...",
     *             "PublishPage": "",
     *             "Id": "289B595F89FE87DE",
     *             "Province": "GD",
     *             "Court": "广州市白云区人民法院"
     *         },
     *         {
     *             "PublishDate": "2015-09-26 12:00:00",
     *             "Category": "裁判文书",
     *             "Party": "周细清",
     *             "SubmitDate": "2015-09-26 12:00:00",
     *             "Content": "周细清：本院受理原告恒大地产集团有限公司诉被告周细清房屋买卖合同纠纷一案已审理终结。现依法向你公告送达（2015）穗云法民四初字第320号民事判决书一份。自公告之日起60日内来本院领取民事判决书，逾期则视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于广东省广州市中级人民法院。...",
     *             "PublishPage": "",
     *             "Id": "3490C356FC007113",
     *             "Province": "GD",
     *             "Court": "[广东]广州市白云区人民法院"
     *         },
     *         {
     *             "PublishDate": "2015-04-01 12:00:00",
     *             "Category": "诉状副本及开庭传票",
     *             "Party": "谢雨波",
     *             "SubmitDate": "2015-04-01 12:00:00",
     *             "Content": "谢雨波：本院受理原告恒大地产集团有限公司诉你商品房销售合同纠纷二案，因你下落不明，现依法向你公告送达起诉状及证据副本、应诉通知书、举证通知书、民事裁定书、告知合议庭组成人员通知书和开庭传票等法律文书。自本公告发出之日起经过60日即视为送达。提出答辩状和举证期限分别为公告期满后的15日和30日内。...",
     *             "PublishPage": "",
     *             "Id": "46671945D858DC70",
     *             "Province": "GD",
     *             "Court": "[广东]恩平市人民法院"
     *         },
     *         {
     *             "PublishDate": "2015-09-12 12:00:00",
     *             "Category": "起诉状副本及开庭传票",
     *             "Party": "恒大地产集团有限公司、严东",
     *             "SubmitDate": "2015-09-12 12:00:00",
     *             "Content": "严东：本院受理原告恒大地产集团有限公司诉被告严东房屋买卖合同纠纷一案【案号：（2015）穗云法民四初字第451号】，现依法向你公告送达起诉状副本、开庭传票。自公告之日起经过六十天，即视为送达。提出答辩状和举证的期限均为公告期满后的30日内。并定于举证期满后的2015年12月16日9时整（遇法定节假日顺延）在本院第十七...",
     *             "PublishPage": "",
     *             "Id": "4AC228FDB9432223",
     *             "Province": "GD",
     *             "Court": "广州市白云区人民法院"
     *         },
     *         {
     *             "PublishDate": "2015-09-26 12:00:00",
     *             "Category": "裁判文书",
     *             "Party": "恒大地产集团有限公司、周细清",
     *             "SubmitDate": "2015-09-26 12:00:00",
     *             "Content": "周细清：本院受理原告恒大地产集团有限公司诉被告周细清房屋买卖合同纠纷一案已审理终结。现依法向你公告送达（2015）穗云法民四初字第320号民事判决书一份。自公告之日起60日内来本院领取民事判决书，逾期则视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于广东省广州市中级人民法院。...",
     *             "PublishPage": "",
     *             "Id": "50AEFB2D3AF33A72",
     *             "Province": "GD",
     *             "Court": "广州市白云区人民法院"
     *         },
     *         {
     *             "PublishDate": "2015-11-04 12:00:00",
     *             "Category": "诉状副本及开庭传票",
     *             "Party": "朱震宇",
     *             "SubmitDate": "2015-11-04 12:00:00",
     *             "Content": "朱震宇：本院受理原告恒大地产集团有限公司诉朱震宇商品房销售合同纠纷一案，现依法向你公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。自公告之日起，经过60日即视为送达。提出答辩状的期限和举证期限分别为公告期满后15日和30日内。并定于举证期满后第3日上午9时（遇法定假日顺延）在本院东三楼第二审判法庭开庭...",
     *             "PublishPage": "",
     *             "Id": "8B1F91E4AE28C0EF",
     *             "Province": "NMG",
     *             "Court": "[内蒙古]包头市九原区人民法院"
     *         },
     *         {
     *             "PublishDate": "2015-09-12 12:00:00",
     *             "Category": "诉状副本及开庭传票",
     *             "Party": "严东",
     *             "SubmitDate": "2015-09-12 12:00:00",
     *             "Content": "严东：本院受理原告恒大地产集团有限公司诉被告严东房屋买卖合同纠纷一案【案号：（2015）穗云法民四初字第451号】，现依法向你公告送达起诉状副本、开庭传票。自公告之日起经过六十天，即视为送达。提出答辩状和举证的期限均为公告期满后的30日内。并定于举证期满后的2015年12月16日9时整（遇法定节假日顺延）在本院第十七...",
     *             "PublishPage": "",
     *             "Id": "99EBD52A387F988E",
     *             "Province": "GD",
     *             "Court": "[广东]广州市白云区人民法院"
     *         },
     *         {
     *             "PublishDate": "2015-06-06 12:00:00",
     *             "Category": "诉状副本及开庭传票",
     *             "Party": "周细清",
     *             "SubmitDate": "2015-06-06 12:00:00",
     *             "Content": "周细清：本院受理原告恒大地产集团有限公司诉被告周细清房屋买卖合同纠纷一案，现依法向你公告送达起诉状副本、开庭传票。自公告之日起经过六十天，即视为送达。提出答辩状和举证的期限分别为公告期满后的30日内。并定于举证期满后的2015年9月2日10时30分（遇法定节假日顺延）在本院第十七法庭开庭审理，逾期将依法缺席判决...",
     *             "PublishPage": "",
     *             "Id": "A78778C01BA06C5C",
     *             "Province": "GD",
     *             "Court": "[广东]广州市白云区人民法院"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetHistorytCourtNotice(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询历史法院公告信息表", params);
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistoryZhiXing 历史被执行
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     *
     * @apiSuccess {string} BiaoDi 标地
     * @apiSuccess {string} CaseNo 案号
     * @apiSuccess {string} ExecuteGov 执行法院
     * @apiSuccess {string} AnNo 执行依据文号
     * @apiSuccess {string} Province 省份
     * @apiSuccess {string} LiAnDate 立案时间
     * @apiSuccess {string} OrgNo 组织机构代码
     * @apiSuccess {string} OrgType 组织类型，1：自然人，2：企业，3：社会组织，空白：无法判定）
     * @apiSuccess {string} OrgTypeName 组织类型名称
     * @apiSuccess {string} Name 名称
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 10
     *     },
     *     "Result": [
     *         {
     *             "CaseNo": "(2016)新4021执732号",
     *             "AnNo": "(2016)新4021执732号",
     *             "OrgType": "2",
     *             "LiAnDate": "2016-07-13 12:00:00",
     *             "OrgNo": "00",
     *             "Province": "新疆",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "伊宁县人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "450000"
     *         },
     *         {
     *             "CaseNo": "(2016)粤01执3073号",
     *             "AnNo": "(2016)粤01执3073号",
     *             "OrgType": "2",
     *             "LiAnDate": "2016-08-18 12:00:00",
     *             "OrgNo": "91654021686****4661",
     *             "Province": "广东",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "广州市中级人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "114040352"
     *         },
     *         {
     *             "CaseNo": "(2016)新40执49号",
     *             "AnNo": "(2016)新40执49号",
     *             "OrgType": "2",
     *             "LiAnDate": "2016-05-05 12:00:00",
     *             "OrgNo": "686480466",
     *             "Province": "新疆",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "新疆维吾尔自治区高级人民法院伊犁哈萨克自治州分院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "1040000"
     *         },
     *         {
     *             "CaseNo": "(2015)伊县法执字第01010号",
     *             "AnNo": "(2015)伊县法执字第01010号",
     *             "OrgType": "2",
     *             "LiAnDate": "2015-08-04 12:00:00",
     *             "OrgNo": "00",
     *             "Province": "新疆",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "伊宁县人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "72157"
     *         },
     *         {
     *             "CaseNo": "(2016)新4021执622号",
     *             "AnNo": "(2016)新4021执622号",
     *             "OrgType": "2",
     *             "LiAnDate": "2016-05-25 12:00:00",
     *             "OrgNo": "00",
     *             "Province": "新疆",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "伊宁县人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "251198.34"
     *         },
     *         {
     *             "CaseNo": "(2016)新4021执345号",
     *             "AnNo": "(2016)新4021执345号",
     *             "OrgType": "2",
     *             "LiAnDate": "2016-03-11 12:00:00",
     *             "OrgNo": "00",
     *             "Province": "新疆",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "伊宁县人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "193984"
     *         },
     *         {
     *             "CaseNo": "(2016)新4021执1113号",
     *             "AnNo": "(2016)新4021执1113号",
     *             "OrgType": "2",
     *             "LiAnDate": "2016-09-18 12:00:00",
     *             "OrgNo": "00",
     *             "Province": "新疆",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "伊宁县人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "6460"
     *         },
     *         {
     *             "CaseNo": "（2017）新4021执1081号",
     *             "AnNo": "（2017）新4021执1081号",
     *             "OrgType": "2",
     *             "LiAnDate": "2017-08-03 12:00:00",
     *             "OrgNo": "91654021686****4661",
     *             "Province": "新疆",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "伊宁县人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "954000"
     *         },
     *         {
     *             "CaseNo": "(2016)浙0522执3375号",
     *             "AnNo": "(2016)浙0522执3375号",
     *             "OrgType": "2",
     *             "LiAnDate": "2016-10-26 12:00:00",
     *             "OrgNo": "68648046-6",
     *             "Province": "浙江",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "长兴县人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "6163938"
     *         },
     *         {
     *             "CaseNo": "(2016)新4021执1005号",
     *             "AnNo": "(2016)新4021执1005号",
     *             "OrgType": "2",
     *             "LiAnDate": "2016-08-22 12:00:00",
     *             "OrgNo": "00",
     *             "Province": "新疆",
     *             "OrgTypeName": "失信企业",
     *             "ExecuteGov": "伊宁县人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "BiaoDi": "450000"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetHistoryZhiXing(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询历史被执行信息表", params);
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistoryShiXin 历史失信查询
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     *
     * @apiSuccess {string} Id Id值
     * @apiSuccess {string} ActionRemark 其他有履行能力而拒不履行生效法律文书确定义务
     * @apiSuccess {string} ExecuteNo 执行依据文号
     * @apiSuccess {string} ExecuteStatus 被执行的履行情况
     * @apiSuccess {string} ExecuteUnite 做出执行依据单位
     * @apiSuccess {string} YiWu 生效法律文书确定的义务
     * @apiSuccess {string} PublicDate 发布时间
     * @apiSuccess {string} CaseNo 案号
     * @apiSuccess {string} ExecuteGov 执行法院
     * @apiSuccess {string} AnNo 执行依据文号
     * @apiSuccess {string} Province 省份
     * @apiSuccess {string} LiAnDate 立案时间
     * @apiSuccess {string} OrgNo 组织机构代码
     * @apiSuccess {string} OrgType 组织类型，1：自然人，2：企业，3：社会组织，空白：无法判定）
     * @apiSuccess {string} OrgTypeName 组织类型名称
     * @apiSuccess {string} Name 名称
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 2
     *     },
     *     "Result": [
     *         {
     *             "YiWu": "向申请人中国化学工程第四建设有限公司支付11961141.75元，执行费79361.14元。",
     *             "ExecuteStatus": "全部未履行",
     *             "OrgNo": "68648046-6",
     *             "Province": "新疆",
     *             "ExecuteGov": "乌鲁木齐市中级人民法院",
     *             "Name": "新疆庆华能源集团有限公司",v
     *             "CaseNo": "（2017）新01执514号",
     *             "AnNo": "（2017）新01执514号",
     *             "ExecuteUnite": "乌鲁木齐仲裁委员会",
     *             "ActionRemark": "有履行能力而拒不履行生效法律文书确定义务,违反财产报告制度",
     *             "OrgType": "2",
     *             "PublicDate": "2018-01-10 12:00:00",
     *             "LiAnDate": "2017-08-03 12:00:00",
     *             "Id": "fd9285c28fffc4caa262eedf064ff74f2",
     *             "ExecuteNo": "（2016）乌仲裁字第0348号",
     *             "OrgTypeName": "失信企业"
     *         },
     *         {
     *             "YiWu": "支付6163938元",
     *             "ExecuteStatus": "全部未履行",
     *             "OrgNo": "68648046-6",
     *             "Province": "浙江",
     *             "ExecuteGov": "长兴县人民法院",
     *             "Name": "新疆庆华能源集团有限公司",
     *             "CaseNo": "(2016)浙0522执3375号",
     *             "AnNo": "(2016)浙0522执3375号",
     *             "ExecuteUnite": "湖州长兴法院",
     *             "ActionRemark": "其他有履行能力而拒不履行生效法律文书确定义务",
     *             "OrgType": "2",
     *             "PublicDate": "2016-11-11 12:00:00",
     *             "LiAnDate": "2016-10-26 12:00:00",
     *             "Id": "c8073c2b875733fbc031199970ccc1e82",
     *             "ExecuteNo": "(2015)湖长泗商初字第00549号",
     *             "OrgTypeName": "失信企业"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetHistoryShiXin(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询历史失信信息表", params);
    }

    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytShareHolder 历史股东
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} PartnerName 股东名称
     * @apiSuccess {string} StockPercent 持股比例
     * @apiSuccess {string} ShouldCapi 认缴出资额
     * @apiSuccess {string} ShouldDate 认缴出资日期
     * @apiSuccess {string} ShouldType 出资类型
     * @apiSuccess {string} ChangeDateList 变更日期
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 10
     *     },
     *     "Result": [
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "73.8806%",
     *             "PartnerName": "广州市凯隆置业有限公司",
     *             "ShouldCapi": "250000万元"
     *         },
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "2.4254%",
     *             "PartnerName": "苏州工业园区睿灿投资企业（有限合伙）",
     *             "ShouldCapi": "8207.0707万元"
     *         },
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "2.0522%",
     *             "PartnerName": "马鞍山市茂文科技工业园有限公司",
     *             "ShouldCapi": "6944.4444万元"
     *         },
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "1.8657%",
     *             "PartnerName": "中信聚恒（深圳）投资控股中心（有限合伙）",
     *             "ShouldCapi": "6313.1313万元"
     *         },
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "1.8657%",
     *             "PartnerName": "深圳市麒翔投资有限公司",
     *             "ShouldCapi": "6313.1313万元"
     *         },
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "1.8657%",
     *             "PartnerName": "深圳市宝信投资控股有限公司",
     *             "ShouldCapi": "6313.1313万元"
     *         },
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "1.8657%",
     *             "PartnerName": "深圳市华建控股有限公司",
     *             "ShouldCapi": "6313.1313万元"
     *         },
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "1.8657%",
     *             "PartnerName": "江西省华达置业集团有限公司",
     *             "ShouldCapi": "6313.1313万元"
     *         },
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "1.8657%",
     *             "PartnerName": "广田投资有限公司",
     *             "ShouldCapi": "6313.1313万元"
     *         },
     *         {
     *             "ShouldType": "",
     *             "ShouldDate": "",
     *             "ChangeDateList": "[\"2017-11-23\",\"2017-06-01\",\"2017-04-01\"]",
     *             "StockPercent": "1.3060%",
     *             "PartnerName": "深圳市键诚投资有限公司",
     *             "ShouldCapi": "4419.1919万元"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetHistorytShareHolder(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询历史股东-股东列表", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytInvestment 历史对外投资
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiParam {int} registCapiMin 最小注册资本
     * @apiParam {int} registCapiMax 最大注册资本
     * @apiParam {int} fundedRatioMin 最小出资比例
     * @apiParam {int} fundedRatioMax 最大出资比例
     * @apiUse PageParam
     *
     * @apiSuccess {string} ChangeDate 变更日期
     * @apiSuccess {string} KeyNo 公司KeyNo
     * @apiSuccess {string} CompanyName 公司名称
     * @apiSuccess {string} OperName 法人名称
     * @apiSuccess {string} RegistCapi 注册资本
     * @apiSuccess {string} EconKind 公司类型
     * @apiSuccess {string} Status 状态
     * @apiSuccess {string} FundedRatio 出资比例
     * @apiSuccess {string} StartDate 投资日期
     *
     *
     * @apiSuccessExample 请求成功:
     *
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 10
     *     },
     *     "Result": [
     *         {
     *             "KeyNo": "0076d172a84d94e537eafa7d8aa97509",
     *             "RegistCapi": "3030万人民币元",
     *             "StartDate": "2017-01-09 12:00:00",
     *             "Status": "存续",
     *             "CompanyName": "平顶山长久置业有限公司",
     *             "OperName": "贾飞",
     *             "EconKind": "其他有限责任公司",
     *             "FundedRatio": "",
     *             "ChangeDate": "2018-04-13 12:00:00"
     *         },
     *         {
     *             "KeyNo": "4886a625749ad0c33a4fe4615882c35d",
     *             "RegistCapi": "10000万人民币元",
     *             "StartDate": "2007-04-30 12:00:00",
     *             "Status": "",
     *             "CompanyName": "广州恒大材料设备有限公司",
     *             "OperName": "苏鑫",
     *             "EconKind": "有限责任公司(法人独资)",
     *             "FundedRatio": "",
     *             "ChangeDate": "2018-01-09 12:00:00"
     *         },
     *         {
     *             "KeyNo": "3b87edcc0b73147d0d220e27985a4a64",
     *             "RegistCapi": "2000万人民币元",
     *             "StartDate": "2009-04-24 12:00:00",
     *             "Status": "",
     *             "CompanyName": "广东恒大排球俱乐部有限公司",
     *             "OperName": "李一萌",
     *             "EconKind": "有限责任公司(法人独资)",
     *             "FundedRatio": "",
     *             "ChangeDate": "2017-12-12 12:00:00"
     *         },
     *         {
     *             "KeyNo": "97eeeffd2a1d8ccbfb8d8472f5aa5584",
     *             "RegistCapi": "10000万人民币元",
     *             "StartDate": "2015-09-30 12:00:00",
     *             "Status": "存续",
     *             "CompanyName": "深圳市小牛消费服务有限公司",
     *             "OperName": "彭最鸿",
     *             "EconKind": "有限责任公司（法人独资）",
     *             "FundedRatio": "",
     *             "ChangeDate": "2017-11-20 12:00:00"
     *         },
     *         {
     *             "KeyNo": "24850a5c259e5ab475affeb0c1ae8e6b",
     *             "RegistCapi": "36255万人民币元",
     *             "Status": "",
     *             "CompanyName": "广州市俊鸿房地产开发有限公司",
     *             "OperName": "吉兴顺",
     *             "EconKind": "有限责任公司(法人独资)",
     *             "FundedRatio": "",
     *             "ChangeDate": "2017-06-21 12:00:00"
     *         },
     *         {
     *             "KeyNo": "b0bad4b66053fa8cf409d186bfcb3a4d",
     *             "RegistCapi": "2000000万人民币元",
     *             "StartDate": "2015-05-19 12:00:00",
     *             "Status": "",
     *             "CompanyName": "恒大旅游集团有限公司",
     *             "OperName": "汤济泽",
     *             "EconKind": "有限责任公司(法人独资)",
     *             "FundedRatio": "",
     *             "ChangeDate": "2016-11-28 12:00:00"
     *         },
     *         {
     *             "KeyNo": "112df39675782fa6688179ea2795e1b6",
     *             "RegistCapi": "5398498.000000万人民币元",
     *             "StartDate": "2009-08-26 12:00:00",
     *             "Status": "",
     *             "CompanyName": "恒大集团(南昌)有限公司",
     *             "OperName": "鞠志明",
     *             "EconKind": "其他有限责任公司",
     *             "FundedRatio": "",
     *             "ChangeDate": "2016-11-28 12:00:00"
     *         },
     *         {
     *             "KeyNo": "e72fd45d73e23667c84e8f9bb6b4dc98",
     *             "RegistCapi": "100万人民币元",
     *             "Status": "",
     *             "CompanyName": "深圳市铭之瑞科技有限公司",
     *             "OperName": "张波",
     *             "EconKind": "有限责任公司（自然人独资）",
     *             "FundedRatio": "",
     *             "ChangeDate": "2016-09-27 12:00:00"
     *         },
     *         {
     *             "KeyNo": "27508fccfd3110a8056327e45e703ac2",
     *             "RegistCapi": "500万人民币元",
     *             "Status": "",
     *             "CompanyName": "启东市欣晴娱乐有限公司",
     *             "OperName": "艾冬",
     *             "EconKind": "有限责任公司（法人独资）",
     *             "FundedRatio": "",
     *             "ChangeDate": "2016-05-30 12:00:00"
     *         },
     *         {
     *             "KeyNo": "517ae662bfd202a8eb567224c0637d4b",
     *             "RegistCapi": "600万人民币元",
     *             "Status": "",
     *             "CompanyName": "启东市金色海岸大酒店有限公司",
     *             "OperName": "艾冬",
     *             "EconKind": "有限责任公司（法人独资）",
     *             "FundedRatio": "",
     *             "ChangeDate": "2016-05-30 12:00:00"
     *         }
     *     ]
     * }
     *
     *
     * @apiUse QccError
     */
    private Object GetHistorytInvestment(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        // FIXME: 2019/4/24 REGIST_CAPI和FUNDED_RATIO 字段应该用int存储，并且在sql中不再强转为int
        return pageQuery("qcc.查询历史对外投资信息表", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/History/GetHistorytEci 历史工商信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {string} KeyNo 公司KeyNo
     * @apiSuccess {object[]} CompanyNameList 历史名称
     * @apiSuccess {string} CompanyNameList.ChangeDate 变更日期
     * @apiSuccess {string} CompanyNameList.CompanyName 公司名称
     *
     * @apiSuccess {object[]} OperList 法人信息
     * @apiSuccess {string} OperList.ChangeDate 变更日期
     * @apiSuccess {string} OperList.OperName 法人名称
     *
     * @apiSuccess {object[]} RegistCapiList 历史注册资本
     * @apiSuccess {string} RegistCapiList.ChangeDate 变更日期
     * @apiSuccess {string} RegistCapiList.RegistCapi 注册资本
     * @apiSuccess {string} RegistCapiList.Amount 金额
     * @apiSuccess {string} RegistCapiList.Unit 单位
     *
     * @apiSuccess {object[]} AddressList 历史地址
     * @apiSuccess {string} AddressList.ChangeDate 变更日期
     * @apiSuccess {string} AddressList.Address 地址
     *
     * @apiSuccess {object[]} ScopeList 历史经营范围
     * @apiSuccess {string} ScopeList.ChangeDate 变更日期
     * @apiSuccess {string} ScopeList.Scope 经营范围
     *
     * @apiSuccess {object[]} EmployeeList 历史主要人员
     * @apiSuccess {string} EmployeeList.ChangeDate 变更日期
     * @apiSuccess {string} EmployeeList.Employees.KeyNo 公司KeyNo
     * @apiSuccess {string} EmployeeList.Employees.EmployeeName 名称
     * @apiSuccess {string} EmployeeList.Employees.Job 职位
     *
     * @apiSuccess {object[]} BranchList 历史分支机构
     * @apiSuccess {string} BranchList.ChangeDate 变更日期
     * @apiSuccess {string} BranchList.KeyNo 公司KeyNo
     * @apiSuccess {string} BranchList.BranchName 机构名称
     *
     * @apiSuccess {object[]} TelList 历史电话
     * @apiSuccess {string} TelList.ChangeDate 变更日期
     * @apiSuccess {string} TelList.Tel 电话
     *
     * @apiSuccess {object[]} EmailList 历史邮箱
     * @apiSuccess {string} EmailList.ChangeDate 变更日期
     * @apiSuccess {string} EmailList.Email 邮箱
     *
     * @apiSuccess {object[]} WebsiteList 历史网站
     * @apiSuccess {string} WebsiteList.ChangeDate 变更日期
     * @apiSuccess {string} WebsiteList.Email 邮箱
     *
     *
     * @apiSuccessExample 请求成功:
     *
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "KeyNo": "befe52d9753b511b6aef5e33fe00f97d",
     *         "RegistCapiList": [
     *             {
     *                 "RegistCapi": "1.2亿人民币元",
     *                 "Amount": "120000000",
     *                 "ChangeDate": "2013-04-08",
     *                 "Unit": "人民币元"
     *             }
     *         ],
     *         "TelList": [
     *         ],
     *         "CompanyNameList": [
     *         ],
     *         "BranchList": [
     *         ],
     *         "OperList": [
     *         ],
     *         "WebsiteList": [
     *         ],
     *         "ScopeList": [
     *             {
     *                 "Scope": "商业地产投资及经营、酒店建设投资及经营、连锁百货投资及经营、电影院线等文化产业投资及经营;投资与资产管理、项目管理(以上均不含专项审批);货物进出口、技术进出口,国内一般贸易;代理记账、财务咨询、企业管理咨询、经济信息咨询、计算机信息技术服务与技术咨询、计算机系统集成、网络设备安装与维护(依法须经批准的项目,经相关部门批准后,方可开展经营活动)***",
     *                 "ChangeDate": "2018-04-19"
     *             }
     *         ],
     *         "EmailList": [
     *         ],
     *         "AddressList": [
     *             {
     *                 "Address": "大连中山区解放街9号",
     *                 "ChangeDate": "1999-03-12"
     *             },
     *             {
     *                 "Address": "大连市中山区解放街９号",
     *                 "ChangeDate": "1997-11-13"
     *             }
     *         ],
     *         "EmployeeList": [
     *             {
     *                 "ChangeDate": "2018-08-13",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "p70fb7e3420d037533540165fe84b545",
     *                         "Job": "董事",
     *                         "EmployeeName": "林宁"
     *                     },
     *                     {
     *                         "KeyNo": "pd54d0f650573ecbc2036f333fb0cfe0",
     *                         "Job": "董事",
     *                         "EmployeeName": "尹海"
     *                     },
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "董事长",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "p2d91474fa9ffa548de9464ad3d0a1f5",
     *                         "Job": "监事",
     *                         "EmployeeName": "侯鸿军"
     *                     },
     *                     {
     *                         "KeyNo": "pe31835041554c724fde88ec748aa6f4",
     *                         "Job": "监事",
     *                         "EmployeeName": "韩旭"
     *                     },
     *                     {
     *                         "KeyNo": "p3ef4e77096b20e7aef87d487aff8a0a",
     *                         "Job": "监事",
     *                         "EmployeeName": "张谌"
     *                     },
     *                     {
     *                         "KeyNo": "p165991ca474f1da37007ab96536b1a5",
     *                         "Job": "董事",
     *                         "EmployeeName": "张霖"
     *                     },
     *                     {
     *                         "KeyNo": "p532a92afcfac2fe4ba8a22ba866dc2f",
     *                         "Job": "董事",
     *                         "EmployeeName": "齐界"
     *                     },
     *                     {
     *                         "KeyNo": "pdd3325c48473fcd64180921d82ead80",
     *                         "Job": "董事",
     *                         "EmployeeName": "王思聪"
     *                     },
     *                     {
     *                         "KeyNo": "pf902f9eaae047fe1173120b7509a21d",
     *                         "Job": "董事兼总经理",
     *                         "EmployeeName": "丁本锡"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "ChangeDate": "2016-01-26",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "p2d91474fa9ffa548de9464ad3d0a1f5",
     *                         "Job": "监事",
     *                         "EmployeeName": "侯鸿军"
     *                     },
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "总经理",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "p90e173852d40037a1bec4ea12ec426e",
     *                         "Job": "董事",
     *                         "EmployeeName": "王贵亚"
     *                     },
     *                     {
     *                         "KeyNo": "p532a92afcfac2fe4ba8a22ba866dc2f",
     *                         "Job": "董事",
     *                         "EmployeeName": "齐界"
     *                     },
     *                     {
     *                         "KeyNo": "pf902f9eaae047fe1173120b7509a21d",
     *                         "Job": "董事",
     *                         "EmployeeName": "丁本锡"
     *                     },
     *                     {
     *                         "KeyNo": "p165991ca474f1da37007ab96536b1a5",
     *                         "Job": "董事",
     *                         "EmployeeName": "张霖"
     *                     },
     *                     {
     *                         "KeyNo": "p70fb7e3420d037533540165fe84b545",
     *                         "Job": "董事",
     *                         "EmployeeName": "林宁"
     *                     },
     *                     {
     *                         "KeyNo": "p3ef4e77096b20e7aef87d487aff8a0a",
     *                         "Job": "监事",
     *                         "EmployeeName": "张谌"
     *                     },
     *                     {
     *                         "KeyNo": "pe31835041554c724fde88ec748aa6f4",
     *                         "Job": "监事",
     *                         "EmployeeName": "韩旭"
     *                     },
     *                     {
     *                         "KeyNo": "pd54d0f650573ecbc2036f333fb0cfe0",
     *                         "Job": "董事",
     *                         "EmployeeName": "尹海"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "ChangeDate": "2014-03-14",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "pf902f9eaae047fe1173120b7509a21d",
     *                         "Job": "董事",
     *                         "EmployeeName": "丁本锡"
     *                     },
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "总经理",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "p165991ca474f1da37007ab96536b1a5",
     *                         "Job": "董事",
     *                         "EmployeeName": "张霖"
     *                     },
     *                     {
     *                         "KeyNo": "p3ef4e77096b20e7aef87d487aff8a0a",
     *                         "Job": "监事",
     *                         "EmployeeName": "张谌"
     *                     },
     *                     {
     *                         "KeyNo": "pe31835041554c724fde88ec748aa6f4",
     *                         "Job": "监事",
     *                         "EmployeeName": "韩旭"
     *                     },
     *                     {
     *                         "KeyNo": "pd54d0f650573ecbc2036f333fb0cfe0",
     *                         "Job": "董事",
     *                         "EmployeeName": "尹海"
     *                     },
     *                     {
     *                         "KeyNo": "pf8025469fdbad176f535893d11c7e49",
     *                         "Job": "董事",
     *                         "EmployeeName": "陈平"
     *                     },
     *                     {
     *                         "KeyNo": "p2d91474fa9ffa548de9464ad3d0a1f5",
     *                         "Job": "监事",
     *                         "EmployeeName": "侯鸿军"
     *                     },
     *                     {
     *                         "KeyNo": "p923fc15cca706138ccabf568bf4a3a3",
     *                         "Job": "董事",
     *                         "EmployeeName": "李耀汉"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "ChangeDate": "2011-03-08",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "总经理",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "pdd3325c48473fcd64180921d82ead80",
     *                         "Job": "董事",
     *                         "EmployeeName": "王思聪"
     *                     },
     *                     {
     *                         "KeyNo": "p2d91474fa9ffa548de9464ad3d0a1f5",
     *                         "Job": "监事",
     *                         "EmployeeName": "侯鸿军"
     *                     },
     *                     {
     *                         "KeyNo": "p5a2d2e51101af0b5d7a56cb8fcc9908",
     *                         "Job": "董事",
     *                         "EmployeeName": "张诚"
     *                     },
     *                     {
     *                         "KeyNo": "p532a92afcfac2fe4ba8a22ba866dc2f",
     *                         "Job": "监事",
     *                         "EmployeeName": "齐界"
     *                     },
     *                     {
     *                         "KeyNo": "p3ef4e77096b20e7aef87d487aff8a0a",
     *                         "Job": "监事",
     *                         "EmployeeName": "张谌"
     *                     },
     *                     {
     *                         "KeyNo": "pf8025469fdbad176f535893d11c7e49",
     *                         "Job": "董事",
     *                         "EmployeeName": "陈平"
     *                     },
     *                     {
     *                         "KeyNo": "p923fc15cca706138ccabf568bf4a3a3",
     *                         "Job": "董事",
     *                         "EmployeeName": "李耀汉"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "ChangeDate": "2010-02-10",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "prdd0277127508b36a18d7a264b31467",
     *                         "Job": "监事",
     *                         "EmployeeName": "王健"
     *                     },
     *                     {
     *                         "KeyNo": "p3ef4e77096b20e7aef87d487aff8a0a",
     *                         "Job": "监事",
     *                         "EmployeeName": "张谌"
     *                     },
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "董事长",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "pdd3325c48473fcd64180921d82ead80",
     *                         "Job": "董事",
     *                         "EmployeeName": "王思聪"
     *                     },
     *                     {
     *                         "KeyNo": "p8d85ac0618f79ea3da1c2f4ec478857",
     *                         "Job": "董事",
     *                         "EmployeeName": "崔宗明"
     *                     },
     *                     {
     *                         "KeyNo": "p923fc15cca706138ccabf568bf4a3a3",
     *                         "Job": "董事",
     *                         "EmployeeName": "李耀汉"
     *                     },
     *                     {
     *                         "KeyNo": "p83317b99e4b5cb7a5cc9e7be55841c4",
     *                         "Job": "董事",
     *                         "EmployeeName": "黄平"
     *                     },
     *                     {
     *                         "KeyNo": "p532a92afcfac2fe4ba8a22ba866dc2f",
     *                         "Job": "监事",
     *                         "EmployeeName": "齐界"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "ChangeDate": "2009-08-07",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "p532a92afcfac2fe4ba8a22ba866dc2f",
     *                         "Job": "监事",
     *                         "EmployeeName": "齐界"
     *                     },
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "总经理",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "pf902f9eaae047fe1173120b7509a21d",
     *                         "Job": "董事",
     *                         "EmployeeName": "丁本锡"
     *                     },
     *                     {
     *                         "KeyNo": "p3ef4e77096b20e7aef87d487aff8a0a",
     *                         "Job": "监事",
     *                         "EmployeeName": "张谌"
     *                     },
     *                     {
     *                         "KeyNo": "prdd0277127508b36a18d7a264b31467",
     *                         "Job": "监事",
     *                         "EmployeeName": "王健"
     *                     },
     *                     {
     *                         "KeyNo": "pd54d0f650573ecbc2036f333fb0cfe0",
     *                         "Job": "董事",
     *                         "EmployeeName": "尹海"
     *                     },
     *                     {
     *                         "KeyNo": "pf8025469fdbad176f535893d11c7e49",
     *                         "Job": "董事",
     *                         "EmployeeName": "陈平"
     *                     },
     *                     {
     *                         "KeyNo": "p923fc15cca706138ccabf568bf4a3a3",
     *                         "Job": "董事",
     *                         "EmployeeName": "李耀汉"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "ChangeDate": "2009-01-06",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "p3ef4e77096b20e7aef87d487aff8a0a",
     *                         "Job": "监事",
     *                         "EmployeeName": "张谌"
     *                     },
     *                     {
     *                         "KeyNo": "p414902f5dbbd64f9d73d668c390ecdd",
     *                         "Job": "董事",
     *                         "EmployeeName": "聂茁"
     *                     },
     *                     {
     *                         "KeyNo": "pf8025469fdbad176f535893d11c7e49",
     *                         "Job": "董事",
     *                         "EmployeeName": "陈平"
     *                     },
     *                     {
     *                         "KeyNo": "p532a92afcfac2fe4ba8a22ba866dc2f",
     *                         "Job": "监事",
     *                         "EmployeeName": "齐界"
     *                     },
     *                     {
     *                         "KeyNo": "pr7beece1b71e105b84851644efc54b4",
     *                         "Job": "董事",
     *                         "EmployeeName": "罗昕"
     *                     },
     *                     {
     *                         "KeyNo": "pf479e05f9306979f83a621cdd451dbf",
     *                         "Job": "董事",
     *                         "EmployeeName": "冷传金"
     *                     },
     *                     {
     *                         "KeyNo": "prdd0277127508b36a18d7a264b31467",
     *                         "Job": "监事",
     *                         "EmployeeName": "王健"
     *                     },
     *                     {
     *                         "KeyNo": "p923fc15cca706138ccabf568bf4a3a3",
     *                         "Job": "董事",
     *                         "EmployeeName": "李耀汉"
     *                     },
     *                     {
     *                         "KeyNo": "pf902f9eaae047fe1173120b7509a21d",
     *                         "Job": "董事",
     *                         "EmployeeName": "丁本锡"
     *                     },
     *                     {
     *                         "KeyNo": "pd54d0f650573ecbc2036f333fb0cfe0",
     *                         "Job": "董事",
     *                         "EmployeeName": "尹海"
     *                     },
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "董事长",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "pa21c9d253848f65999adb9011ed7d11",
     *                         "Job": "董事",
     *                         "EmployeeName": "孙喜双"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "ChangeDate": "2005-12-29",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "pf902f9eaae047fe1173120b7509a21d",
     *                         "Job": "董事",
     *                         "EmployeeName": "丁本锡"
     *                     },
     *                     {
     *                         "KeyNo": "p83317b99e4b5cb7a5cc9e7be55841c4",
     *                         "Job": "董事",
     *                         "EmployeeName": "黄平"
     *                     },
     *                     {
     *                         "KeyNo": "pf479e05f9306979f83a621cdd451dbf",
     *                         "Job": "董事",
     *                         "EmployeeName": "冷传金"
     *                     },
     *                     {
     *                         "KeyNo": "p923fc15cca706138ccabf568bf4a3a3",
     *                         "Job": "董事",
     *                         "EmployeeName": "李耀汉"
     *                     },
     *                     {
     *                         "KeyNo": "p414902f5dbbd64f9d73d668c390ecdd",
     *                         "Job": "监事",
     *                         "EmployeeName": "聂茁"
     *                     },
     *                     {
     *                         "KeyNo": "pa21c9d253848f65999adb9011ed7d11",
     *                         "Job": "董事",
     *                         "EmployeeName": "孙喜双"
     *                     },
     *                     {
     *                         "KeyNo": "pr0eb93d36e0a295df4f6c9effa1d66d",
     *                         "Job": "监事",
     *                         "EmployeeName": "孙湛"
     *                     },
     *                     {
     *                         "KeyNo": "p0910f694bab2853557fd86156fefba6",
     *                         "Job": "董事",
     *                         "EmployeeName": "汤天伟"
     *                     },
     *                     {
     *                         "KeyNo": "prdd0277127508b36a18d7a264b31467",
     *                         "Job": "监事",
     *                         "EmployeeName": "王健"
     *                     },
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "董事长",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "pd54d0f650573ecbc2036f333fb0cfe0",
     *                         "Job": "董事",
     *                         "EmployeeName": "尹海"
     *                     },
     *                     {
     *                         "KeyNo": "p876e7ef826ecd738b2f3dfc5c5bd926",
     *                         "Job": "董事",
     *                         "EmployeeName": "周良君"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "ChangeDate": "2004-02-09",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "pr87ad6e0eb962a5adf29c5df27c65e3",
     *                         "Job": "董事",
     *                         "EmployeeName": "董永成"
     *                     },
     *                     {
     *                         "KeyNo": "prc8109677552074586879e0137796a4",
     *                         "Job": "董事",
     *                         "EmployeeName": "郭岩"
     *                     },
     *                     {
     *                         "KeyNo": "prfbf7c12ad06f5fed2ba6988cddb3ad",
     *                         "Job": "董事",
     *                         "EmployeeName": "姜雄城"
     *                     },
     *                     {
     *                         "KeyNo": "pf479e05f9306979f83a621cdd451dbf",
     *                         "Job": "董事",
     *                         "EmployeeName": "冷传金"
     *                     },
     *                     {
     *                         "KeyNo": "pr943a6cdf9e73966e9d9ff9c46c1b44",
     *                         "Job": "监事",
     *                         "EmployeeName": "李学峰"
     *                     },
     *                     {
     *                         "KeyNo": "p923fc15cca706138ccabf568bf4a3a3",
     *                         "Job": "董事",
     *                         "EmployeeName": "李耀汉"
     *                     },
     *                     {
     *                         "KeyNo": "p414902f5dbbd64f9d73d668c390ecdd",
     *                         "Job": "监事",
     *                         "EmployeeName": "聂茁"
     *                     },
     *                     {
     *                         "KeyNo": "p532a92afcfac2fe4ba8a22ba866dc2f",
     *                         "Job": "监事",
     *                         "EmployeeName": "齐界"
     *                     },
     *                     {
     *                         "KeyNo": "pa21c9d253848f65999adb9011ed7d11",
     *                         "Job": "董事",
     *                         "EmployeeName": "孙喜双"
     *                     },
     *                     {
     *                         "KeyNo": "prf35887e34d98ced1031ba18dcae409",
     *                         "Job": "董事",
     *                         "EmployeeName": "谭业军"
     *                     },
     *                     {
     *                         "KeyNo": "p0910f694bab2853557fd86156fefba6",
     *                         "Job": "董事",
     *                         "EmployeeName": "汤天伟"
     *                     },
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "董事长",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "pr1963fa9b51a96fd9ef5c7f69dfbbaa",
     *                         "Job": "董事",
     *                         "EmployeeName": "谢里修"
     *                     },
     *                     {
     *                         "KeyNo": "p876e7ef826ecd738b2f3dfc5c5bd926",
     *                         "Job": "董事",
     *                         "EmployeeName": "周良君"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "ChangeDate": "2002-12-23",
     *                 "Employees": [
     *                     {
     *                         "KeyNo": "prdf7c0c6fa5d2ed5bd1a740f7d9ddce",
     *                         "Job": "董事",
     *                         "EmployeeName": "程绍运"
     *                     },
     *                     {
     *                         "KeyNo": "pr87ad6e0eb962a5adf29c5df27c65e3",
     *                         "Job": "董事",
     *                         "EmployeeName": "董永成"
     *                     },
     *                     {
     *                         "KeyNo": "p99bbfce6356861ccd4a78a32adbac90",
     *                         "Job": "副总经理",
     *                         "EmployeeName": "高茜"
     *                     },
     *                     {
     *                         "KeyNo": "p83317b99e4b5cb7a5cc9e7be55841c4",
     *                         "Job": "监事",
     *                         "EmployeeName": "黄平"
     *                     },
     *                     {
     *                         "KeyNo": "pr0d86904673eae79a7475b8ec782bc5",
     *                         "Job": "总经理",
     *                         "EmployeeName": "姜积成"
     *                     },
     *                     {
     *                         "KeyNo": "pf479e05f9306979f83a621cdd451dbf",
     *                         "Job": "监事",
     *                         "EmployeeName": "冷传金"
     *                     },
     *                     {
     *                         "KeyNo": "pr75fc6a0715bec3b280c6e79ee52190",
     *                         "Job": "监事",
     *                         "EmployeeName": "苏仲义"
     *                     },
     *                     {
     *                         "KeyNo": "pr8a1582fb0f163f476231f990275fbb",
     *                         "Job": "董事",
     *                         "EmployeeName": "孙昆双"
     *                     },
     *                     {
     *                         "KeyNo": "prf35887e34d98ced1031ba18dcae409",
     *                         "Job": "董事",
     *                         "EmployeeName": "谭业军"
     *                     },
     *                     {
     *                         "KeyNo": "pr59f365564c03fb79c2399a26adedf0",
     *                         "Job": "董事",
     *                         "EmployeeName": "汤闯"
     *                     },
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Job": "董事长",
     *                         "EmployeeName": "王健林"
     *                     },
     *                     {
     *                         "KeyNo": "pr1963fa9b51a96fd9ef5c7f69dfbbaa",
     *                         "Job": "董事",
     *                         "EmployeeName": "谢里修"
     *                     },
     *                     {
     *                         "KeyNo": "p55a2871e4369c3b03d16f93a4b7c27d",
     *                         "Job": "副总经理",
     *                         "EmployeeName": "佘世耀"
     *                     }
     *                 ]
     *             }
     *         ]
     *     }
     * }
     *
     *
     * @apiUse QccError
     */
    private Object GetHistorytEci(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject param) {
        JSONObject object = singleQuery("qcc.查询历史工商信息表", param);
        JSONObject hisData = JSON.parseObject(object.getString("HisData"));
        object.remove("HisData");
        if (hisData != null) {
            object.putAll((Map<? extends String, ? extends Object>) hisData);
        }
        return object;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/ECIV4/GetDetailsByName 企业关键字精确获取详细信息(Master)
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {string} KeyNo 内部KeyNo
     * @apiSuccess {string} Name 公司名称
     * @apiSuccess {string} No 注册号
     * @apiSuccess {string} BelongOrg 登记机关
     * @apiSuccess {string} OperName 法人名
     * @apiSuccess {string} StartDate 成立日期
     * @apiSuccess {string} EndDate 吊销日期
     * @apiSuccess {string} Status 企业状态
     * @apiSuccess {string} Province 省份
     * @apiSuccess {string} UpdatedDate 更新日期
     * @apiSuccess {string} CreditCode 社会统一信用代码
     * @apiSuccess {string} RegistCapi 注册资本
     * @apiSuccess {string} EconKind 企业类型
     * @apiSuccess {string} Address 地址
     * @apiSuccess {string} Scope 经营范围
     * @apiSuccess {string} TermStart 营业开始日期
     * @apiSuccess {string} TeamEnd 营业结束日期
     * @apiSuccess {string} CheckDate 发照日期
     * @apiSuccess {string} OrgNo 组织机构代码
     * @apiSuccess {string} IsOnStock 是否上市(0为未上市，1为上市)
     * @apiSuccess {string} StockNumber 上市公司代码
     * @apiSuccess {string} StockType 上市类型
     * @apiSuccess {string} ImageUrl 企业Logo
     *
     * @apiSuccess {object[]} OriginalName 曾用名
     * @apiSuccess {string} OriginalName.Name 曾用名
     * @apiSuccess {string} OriginalName.ChangeDate 变更日期
     *
     * @apiSuccess {object[]} Partners 股东信息
     * @apiSuccess {string} Partners.StockName 股东
     * @apiSuccess {string} Partners.StockType 股东类型
     * @apiSuccess {string} Partners.StockPercent 出资比例
     * @apiSuccess {string} Partners.ShouldCapi 认缴出资额
     * @apiSuccess {string} Partners.ShoudDate 认缴出资时间
     * @apiSuccess {string} Partners.InvestType 认缴出资方式
     * @apiSuccess {string} Partners.InvestName 实际出资方式
     * @apiSuccess {string} Partners.RealCapi 实缴出资额
     * @apiSuccess {string} Partners.CapiDate 实缴时间
     *
     * @apiSuccess {object[]} Employees 主要人员
     * @apiSuccess {string} Employees.Name 姓名
     * @apiSuccess {string} Employees.Job 职位
     *
     * @apiSuccess {object[]} Branches 分支机构
     * @apiSuccess {string} Branches.CompanyId CompanyId
     * @apiSuccess {string} Branches.RegNo 注册号或社会统一信用代码（存在社会统一信用代码显示社会统一信用代码，否则显示注册号）
     * @apiSuccess {string} Branches.Name 名称
     * @apiSuccess {string} Branches.BelongOrg 登记机关
     * @apiSuccess {string} Branches.CreditCode 社会统一信用代码（保留字段，目前为空）
     * @apiSuccess {string} Branches.OperName 法人姓名或负责人姓名（保留字段，目前为空）
     *
     * @apiSuccess {object[]} ChangeRecords 变更信息
     * @apiSuccess {string} ChangeRecords.ProjectName 变更事项
     * @apiSuccess {string} ChangeRecords.BeforeContent 变更前内容
     * @apiSuccess {string} ChangeRecords.AfterContent 变更后内容
     * @apiSuccess {string} ChangeRecords.ChangeDate 变更日期
     *
     * @apiSuccess {object} ContactInfo 联系信息
     * @apiSuccess {object[]} ContactInfo.WebSite 网址信息
     * @apiSuccess {string} ContactInfo.WebSite.Name 网站名称
     * @apiSuccess {string} ContactInfo.WebSite.Url 网站地址
     * @apiSuccess {string} ContactInfo.PhoneNumber 联系电话
     * @apiSuccess {string} ContactInfo.Email 联系邮箱
     *
     * @apiSuccess {object} Industry 行业信息
     * @apiSuccess {string} Industry.IndustryCode 行业门类code
     * @apiSuccess {string} Industry.Industry 行业门类描述
     * @apiSuccess {string} Industry.SubIndustryCode 行业大类code
     * @apiSuccess {string} Industry.SubIndustry 行业大类描述
     * @apiSuccess {string} Industry.MiddleCategoryCode 行业中类code
     * @apiSuccess {string} Industry.MiddleCategory 行业中类描述
     * @apiSuccess {string} Industry.SmallCategoryCode 行业小类code
     * @apiSuccess {string} Industry.SmallCategory 行业小类描述
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "RegistCapi": "100万元人民币",
     *         "BelongOrg": "深圳市市场监督管理局",
     *         "CreditCode": "91440300786561802R",
     *         "EconKind": "有限责任公司",
     *         "Address": "深圳市福田区梅林街道梅丰社区梅华路105号多丽工业区3栋4层402E房",
     *         "UpdatedDate": null,
     *         "Employees": [
     *             {
     *                 "Job": "执行董事",
     *                 "Name": "陈海文"
     *             },
     *             {
     *                 "Job": "监事",
     *                 "Name": "黄坚"
     *             },
     *             {
     *                 "Job": "总经理",
     *                 "Name": "陈海文"
     *             }
     *         ],
     *         "Name": "深圳市桑协世纪科技有限公司",
     *         "StartDate": "2006-03-17 12:00:00",
     *         "Industry": {
     *             "Industry": "科学研究和技术服务业",
     *             "SubIndustryCode": "75",
     *             "IndustryCode": "M",
     *             "MiddleCategory": "其他科技推广服务业",
     *             "SmallCategoryCode": "7590",
     *             "SmallCategory": "其他科技推广服务业",
     *             "SubIndustry": "科技推广和应用服务业",
     *             "MiddleCategoryCode": "759"
     *         },
     *         "StockType": null,
     *         "ChangeRecords": [
     *             {
     *                 "ProjectName": "章程备案",
     *                 "ChangeDate": "2019-03-11 12:00:00",
     *                 "AfterContent": "2019-03-07",
     *                 "BeforeContent": "2018-12-19"
     *             },
     *             {
     *                 "ProjectName": "名称变更（字号名称、集团名称等）",
     *                 "ChangeDate": "2019-03-11 12:00:00",
     *                 "AfterContent": "深圳市桑协世纪科技有限公司",
     *                 "BeforeContent": "深圳市康银信息技术有限公司"
     *             },
     *             {
     *                 "ProjectName": "名称变更（字号名称、集团名称等）",
     *                 "ChangeDate": "2018-12-21 12:00:00",
     *                 "AfterContent": "深圳市康银信息技术有限公司",
     *                 "BeforeContent": "深圳市桑协世纪科技有限公司"
     *             },
     *             {
     *                 "ProjectName": "章程备案",
     *                 "ChangeDate": "2018-12-21 12:00:00",
     *                 "AfterContent": "2018-12-19",
     *                 "BeforeContent": "2018-10-31"
     *             },
     *             {
     *                 "ProjectName": "地址变更（住所地址、经营场所、驻在地址等变更）",
     *                 "ChangeDate": "2018-11-02 12:00:00",
     *                 "AfterContent": "深圳市福田区梅林街道梅丰社区梅华路105号多丽工业区3栋4层402E房",
     *                 "BeforeContent": "深圳市福田区华强北街道鹏基上步工业区101栋第五层516室(入驻深圳市网协商务秘书有限公司)"
     *             },
     *             {
     *                 "ProjectName": "",
     *                 "ChangeDate": "2018-11-02 12:00:00",
     *                 "AfterContent": "计算机软件、信息系统软件的开发、销售；信息系统设计、集成、运行维护；信息技术咨询；集成电路设计、研发；通信线路和设备安装；电子设备工程安装；电子自动化工程安装；监控系统安装；保安监控及防盗报警系统安装；智能卡系统安装；电子工程安装；智能化系统安装；建筑物空调设备、采暖系统、通风设备系统安装；机电设备安装、维修；门窗安装；电工维修；木工维修；管道工维修；计算机、软件及辅助设备的销售；通讯设备的销售；j计算机系统集成；无线数据产品(不含限制项目)的销售。(法律、行政法规、国务院决定禁止的项目除外,限制的项目须取得许可后方可经营)",
     *                 "BeforeContent": "通信线路和设备安装；电子设备工程安装；电子自动化工程安装；监控系统安装；保安监控及防盗报警系统安装；智能卡系统安装；电子工程安装；智能化系统安装；建筑物空调设备、采暖系统、通风设备系统安装；机电设备安装、维修；门窗安装；电工维修；木工维修；管道工维修。计算机、软件及辅助设备的销售。通讯设备的销售；系统集成及无线数据产品(不含限制项目)的销售。"
     *             },
     *             {
     *                 "ProjectName": "章程备案",
     *                 "ChangeDate": "2018-11-02 12:00:00",
     *                 "AfterContent": "2018-10-31",
     *                 "BeforeContent": "2017-03-06"
     *             },
     *             {
     *                 "ProjectName": "章程备案",
     *                 "ChangeDate": "2017-03-08 12:00:00",
     *                 "AfterContent": "2017-03-06",
     *                 "BeforeContent": "2016-05-06"
     *             },
     *             {
     *                 "ProjectName": "",
     *                 "ChangeDate": "2017-03-08 12:00:00",
     *                 "AfterContent": "通信线路和设备安装；电子设备工程安装；电子自动化工程安装；监控系统安装；保安监控及防盗报警系统安装；智能卡系统安装；电子工程安装；智能化系统安装；建筑物空调设备、采暖系统、通风设备系统安装；机电设备安装、维修；门窗安装；电工维修；木工维修；管道工维修。计算机、软件及辅助设备的销售。通讯设备的销售；系统集成及无线数据产品(不含限制项目)的销售。",
     *                 "BeforeContent": "电子产品的技术开发、上门维修,信息咨询(以上不含人才中介服务及其它限制项目)。"
     *             },
     *             {
     *                 "ProjectName": "其他事项备案",
     *                 "ChangeDate": "2016-05-10 12:00:00",
     *                 "AfterContent": "91440300786561802R",
     *                 "BeforeContent": ""
     *             },
     *             {
     *                 "ProjectName": "期限变更（经营期限、营业期限、驻在期限等变更）",
     *                 "ChangeDate": "2016-05-10 12:00:00",
     *                 "AfterContent": "2006-03-17,5000-01-01",
     *                 "BeforeContent": "2006-03-17,2016-03-17"
     *             },
     *             {
     *                 "ProjectName": "地址变更（住所地址、经营场所、驻在地址等变更）",
     *                 "ChangeDate": "2016-05-10 12:00:00",
     *                 "AfterContent": "深圳市福田区华强北街道鹏基上步工业区101栋第五层516室(入驻深圳市网协商务秘书有限公司)",
     *                 "BeforeContent": "深圳市福田区梅华路105号福田国际电子商务产业园1栋1422室"
     *             },
     *             {
     *                 "ProjectName": "地址变更（住所地址、经营场所、驻在地址等变更）",
     *                 "ChangeDate": "2015-06-11 12:00:00",
     *                 "AfterContent": "深圳市福田区梅华路105号福田国际电子商务产业园1栋1422室",
     *                 "BeforeContent": "深圳市福田区振华路(东)兰光大厦C座312房"
     *             },
     *             {
     *                 "ProjectName": "期限变更（经营期限、营业期限、驻在期限等变更）",
     *                 "ChangeDate": "2016-05-10 12:00:00",
     *                 "AfterContent": "永续经营",
     *                 "BeforeContent": "从2006-03-17至2016-03-17"
     *             },
     *             {
     *                 "ProjectName": "指定联系人",
     *                 "ChangeDate": "2015-06-11 12:00:00",
     *                 "AfterContent": "陈海文*",
     *                 "BeforeContent": ""
     *             },
     *             {
     *                 "ProjectName": "经营范围变更（含业务范围变更）",
     *                 "ChangeDate": "2010-05-10 12:00:00",
     *                 "AfterContent": "电子产品的技术开发、上门维修,信息咨询(以上不含人才中介服务及其它限制项目)。",
     *                 "BeforeContent": "电子及信息产品的技术开发及咨询(不含限制项目)；国内商业、物资供销业(不含专营、专控、专卖商品)；兴办实业(具体项目另行申报)。"
     *             },
     *             {
     *                 "ProjectName": "注册号/注册号升级",
     *                 "ChangeDate": "2008-12-11 12:00:00",
     *                 "AfterContent": "440301103762313",
     *                 "BeforeContent": "4403011216989"
     *             },
     *             {
     *                 "ProjectName": "地址变更（住所地址、经营场所、驻在地址等变更）",
     *                 "ChangeDate": "2008-12-11 12:00:00",
     *                 "AfterContent": "深圳市福田区振华路(东)兰光大厦C座312房",
     *                 "BeforeContent": "深圳市福田区燕南路403栋399A"
     *             },
     *             {
     *                 "ProjectName": "地址变更（住所地址、经营场所、驻在地址等变更）",
     *                 "ChangeDate": "2007-06-19 12:00:00",
     *                 "AfterContent": "深圳市福田区燕南路403栋399A",
     *                 "BeforeContent": "深圳市福田区振华路(东)兰光大厦C座307房"
     *             },
     *             {
     *                 "ProjectName": "名称变更（字号名称、集团名称等）",
     *                 "ChangeDate": "2007-03-19 12:00:00",
     *                 "AfterContent": "深圳市桑协世纪科技有限公司",
     *                 "BeforeContent": "深圳市辰光伟业科技有限公司"
     *             },
     *             {
     *                 "ProjectName": "地址变更（住所地址、经营场所、驻在地址等变更）",
     *                 "ChangeDate": "2006-08-18 12:00:00",
     *                 "AfterContent": "深圳市福田区振华路(东)兰光大厦C座307房",
     *                 "BeforeContent": "深圳市福田区华强北路2006号华联发大厦1023号"
     *             },
     *             {
     *                 "ProjectName": "经营范围",
     *                 "ChangeDate": "2017-03-08 12:00:00",
     *                 "AfterContent": "通信线路和设备安装；电子设备工程安装；电子自动化工程安装；监控系统安装；保安监控及防盗报警系统安装；智能卡系统安装；电子工程安装；智能化系统安装；建筑物空调设备、采暖系统、通风设备系统安装；机电设备安装、维修；门窗安装；电工维修；木工维修；管道工维修。计算机、软件及辅助设备的销售。通讯设备的销售；系统集成及无线数据产品(不含限制项目)的销售。^",
     *                 "BeforeContent": "电子产品的技术开发、上门维修,信息咨询(以上不含人才中介服务及其它限制项目)。^"
     *             },
     *             {
     *                 "ProjectName": "审批项目",
     *                 "ChangeDate": "2017-03-08 12:00:00",
     *                 "AfterContent": "验资报告深中法验字[2006]第B036号",
     *                 "BeforeContent": "验资报告深中法验字[2006]第B036号"
     *             },
     *             {
     *                 "ProjectName": "经营期限",
     *                 "ChangeDate": "2016-05-10 12:00:00",
     *                 "AfterContent": "永续经营",
     *                 "BeforeContent": "自2006年3月17日起至2016年3月17日止"
     *             },
     *             {
     *                 "ProjectName": "指定联系人",
     *                 "ChangeDate": "2015-06-11 12:00:00",
     *                 "AfterContent": "姓名:电话:邮箱:",
     *                 "BeforeContent": "姓名:电话:邮箱:"
     *             }
     *         ],
     *         "CheckDate": "2019-03-11 12:00:00",
     *         "ContactInfo": {
     *             "Email": "840019811@qq.com",
     *             "WebSite": {
     *             },
     *             "PhoneNumber": "0755-83314237"
     *         },
     *         "Status": "存续（在营、开业、在册）",
     *         "No": "440301103762313",
     *         "OperName": "陈海文",
     *         "Branches": [
     *         ],
     *         "ImageUrl": "https://co-image.qichacha.com/CompanyImage/default.jpg",
     *         "OrgNo": "78656180-2",
     *         "OriginalName": [
     *             {
     *                 "ChangeDate": "2019-03-11 12:00:00",
     *                 "Name": "深圳市康银信息技术有限公司"
     *             },
     *             {
     *                 "ChangeDate": "2007-03-19 12:00:00",
     *                 "Name": "深圳市辰光伟业科技有限公司"
     *             }
     *         ],
     *         "EndDate": null,
     *         "Province": "GD",
     *         "TermStart": "2006-03-17 12:00:00",
     *         "KeyNo": "692a8d87536443b042bccb655398e3a0",
     *         "TeamEnd": null,
     *         "Partners": [
     *             {
     *                 "StockName": "陈海文",
     *                 "StockType": "自然人股东",
     *                 "StockPercent": "55.00%",
     *                 "ShouldCapi": "55",
     *                 "InvestType": ""
     *             },
     *             {
     *                 "StockName": "黄坚",
     *                 "StockType": "自然人股东",
     *                 "StockPercent": "45.00%",
     *                 "ShouldCapi": "45",
     *                 "InvestType": ""
     *             }
     *         ],
     *         "Scope": "计算机软件、信息系统软件的开发、销售;信息系统设计、集成、运行维护;信息技术咨询;集成电路设计、研发;通信线路和设备安装;电子设备工程安装;电子自动化工程安装;监控系统安装;保安监控及防盗报警系统安装;智能卡系统安装;电子工程安装;智能化系统安装;建筑物空调设备、采暖系统、通风设备系统安装;机电设备安装、维修;门窗安装;电工维修;木工维修;管道工维修;计算机、软件及辅助设备的销售;通讯设备的销售;j计算机系统集成;无线数据产品(不含限制项目)的销售。(法律、行政法规、国务院决定禁止的项目除外,限制的项目须取得许可后方可经营)",
     *         "IsOnStock": "0",
     *         "StockNumber": null
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GetDetailsByName(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询工商信息表", params);
        object.put("OriginalName", listQuery("qcc.查询工商信息曾用名信息表", params));
        object.put("Partners", listQuery("qcc.查询工商信息股东信息表", params));
        object.put("Employees", listQuery("qcc.查询工商信息主要人员信息表", params));
        object.put("Branches", listQuery("qcc.查询工商信息分支机构表", params));
        object.put("ChangeRecords", listQuery("qcc.查询工商信息变更信息表", params));
        JSONObject ContactInfo = singleQuery("qcc.查询工商信息联系信息表", params);
        ContactInfo.put("WebSite", JSON.parse(ContactInfo.getString("WebSite")));
        object.put("ContactInfo", ContactInfo);
        JSONObject Industry = singleQuery("qcc.查询工商信息行业信息表", params);
        object.put("Industry", Industry);

        return object;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/ChattelMortgage/GetChattelMortgage 动产抵押信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {string} RegisterNo 登记编号
     * @apiSuccess {string} RegisterDate 登记时间
     * @apiSuccess {string} PublicDate 公示时间
     * @apiSuccess {string} RegisterOffice 登记机关
     * @apiSuccess {string} DebtSecuredAmount 被担保债权数额
     * @apiSuccess {string} Status 状态
     *
     * @apiSuccess {object} Detail 动产抵押详细信息
     *
     * @apiSuccess {object} Detail.Pledge 动产抵押Pledge
     * @apiSuccess {string} Detail.Pledge.RegistNo 登记编号
     * @apiSuccess {string} Detail.Pledge.RegistDate 注册时间
     * @apiSuccess {string} Detail.Pledge.RegistOffice 注册单位
     *
     * @apiSuccess {object[]} Detail.PledgeeList 动产抵押PledgeeList
     * @apiSuccess {string} Detail.PledgeeList.Name 名称
     * @apiSuccess {string} Detail.PledgeeList.IdentityType 抵押权人证照/证件类型
     * @apiSuccess {string} Detail.PledgeeList.IdentityNo 证照/证件号码
     *
     * @apiSuccess {object} Detail.SecuredClaim 动产抵押SecuredClaim
     * @apiSuccess {string} Detail.SecuredClaim.Kind 种类
     * @apiSuccess {string} Detail.SecuredClaim.Amount 数额
     * @apiSuccess {string} Detail.SecuredClaim.AssuranceScope 担保的范围
     * @apiSuccess {string} Detail.SecuredClaim.FulfillObligation 债务人履行债务的期限
     * @apiSuccess {string} Detail.SecuredClaim.Remark 备注
     *
     * @apiSuccess {object[]} Detail.GuaranteeList 动产抵押GuaranteeList
     * @apiSuccess {string} Detail.GuaranteeList.Name 名称
     * @apiSuccess {string} Detail.GuaranteeList.Ownership 所有权归属
     * @apiSuccess {string} Detail.GuaranteeList.Other 数量、质量、状况、所在地等情况
     * @apiSuccess {string} Detail.GuaranteeList.Remark 备注
     *
     * @apiSuccess {object} Detail.CancelInfo 动产抵押CancelInfo
     * @apiSuccess {string} Detail.CancelInfo.CancelDate 动产抵押登记注销时间
     * @apiSuccess {string} Detail.CancelInfo.CancelReason 动产抵押登记注销原因
     *
     * @apiSuccess {object[]} Detail.ChangeList 动产抵押ChangeList
     * @apiSuccess {string} Detail.ChangeList.ChangeDate 动产抵押登记变更日期
     * @apiSuccess {string} Detail.ChangeList.ChangeContent 动产抵押登记变更内容
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": [
     *         {
     *             "RegisterOffice": "伊宁县市场监督管理局",
     *             "RegisterDate": "2014-12-17 12:00:00",
     *             "DebtSecuredAmount": "36236",
     *             "RegisterNo": "新抵F212014030",
     *             "Detail": {
     *                 "GuaranteeList": [
     *                     {
     *                         "Ownership": "",
     *                         "Other": "抵押数量为：5抵押品牌为：抵押单价为：",
     *                         "Name": "设备",
     *                         "Remark": ""
     *                     }
     *                 ],
     *                 "ChangeList": [
     *                 ],
     *                 "CancelInfo": {
     *                 },
     *                 "Pledge": {
     *                     "RegistNo": "新抵F212014030",
     *                     "RegistOffice": "伊宁县市场监督管理局",
     *                     "RegistDate": "2014-12-17 12:00:00"
     *                 },
     *                 "SecuredClaim": {
     *                     "AssuranceScope": "1,2,3,4,",
     *                     "Amount": 36236,
     *                     "Kind": "买卖合同",
     *                     "FulfillObligation": "2015-06-15至2027-06-14",
     *                     "Remark": ""
     *                 },
     *                 "PledgeeList": [
     *                     {
     *                         "IdentityType": "",
     *                         "IdentityNo": "",
     *                         "Name": "国家开发银行股份有限公司伊犁哈萨克自治州分行"
     *                     }
     *                 ]
     *             }
     *         },
     *         {
     *             "RegisterOffice": "伊宁县市场监督管理局",
     *             "RegisterDate": "2018-05-02 12:00:00",
     *             "DebtSecuredAmount": "5000",
     *             "RegisterNo": "新抵F212018258",
     *             "Detail": {
     *                 "GuaranteeList": [
     *                     {
     *                         "Ownership": "",
     *                         "Other": "抵押数量为：1抵押品牌为：抵押单价为：",
     *                         "Name": "设备",
     *                         "Remark": ""
     *                     }
     *                 ],
     *                 "ChangeList": [
     *                 ],
     *                 "CancelInfo": {
     *                 },
     *                 "Pledge": {
     *                     "RegistNo": "新抵F212018258",
     *                     "RegistOffice": "伊宁县市场监督管理局",
     *                     "RegistDate": "2018-05-02 12:00:00"
     *                 },
     *                 "SecuredClaim": {
     *                     "AssuranceScope": "1,2,3,4,",
     *                     "Amount": 5000,
     *                     "Kind": "买卖合同",
     *                     "FulfillObligation": "2015-12-17至2018-06-17",
     *                     "Remark": ""
     *                 },
     *                 "PledgeeList": [
     *                     {
     *                         "IdentityType": "企业法人营业执照(公司)",
     *                         "IdentityNo": "",
     *                         "Name": "伊犁哈萨克自治州财通国有资产经营有限责任公司"
     *                     }
     *                 ]
     *             }
     *         },
     *         {
     *             "RegisterOffice": "伊宁县市场监督管理局",
     *             "RegisterDate": "2018-05-02 12:00:00",
     *             "DebtSecuredAmount": "18500",
     *             "RegisterNo": "新抵F212018258",
     *             "Detail": {
     *                 "GuaranteeList": [
     *                     {
     *                         "Ownership": "",
     *                         "Other": "抵押数量为：1抵押品牌为：抵押单价为：",
     *                         "Name": "设备",
     *                         "Remark": ""
     *                     }
     *                 ],
     *                 "ChangeList": [
     *                 ],
     *                 "CancelInfo": {
     *                 },
     *                 "Pledge": {
     *                     "RegistNo": "新抵F212018258",
     *                     "RegistOffice": "伊宁县市场监督管理局",
     *                     "RegistDate": "2018-05-02 12:00:00"
     *                 },
     *                 "SecuredClaim": {
     *                     "AssuranceScope": "1,2,3,4,",
     *                     "Amount": 18500,
     *                     "Kind": "买卖合同",
     *                     "FulfillObligation": "2016-06-17至2018-06-15",
     *                     "Remark": ""
     *                 },
     *                 "PledgeeList": [
     *                     {
     *                         "IdentityType": "企业法人营业执照(公司)",
     *                         "IdentityNo": "",
     *                         "Name": "伊犁哈萨克自治州财通国有资产经营有限责任公司"
     *                     }
     *                 ]
     *             }
     *         },
     *         {
     *             "RegisterOffice": "伊宁县市场监督管理局",
     *             "RegisterDate": "2016-08-05 12:00:00",
     *             "DebtSecuredAmount": "8000",
     *             "RegisterNo": "新抵F2120160021",
     *             "Detail": {
     *                 "GuaranteeList": [
     *                     {
     *                         "Ownership": "",
     *                         "Other": "抵押数量为：265抵押品牌为：抵押单价为：",
     *                         "Name": "设备",
     *                         "Remark": ""
     *                     }
     *                 ],
     *                 "ChangeList": [
     *                 ],
     *                 "CancelInfo": {
     *                 },
     *                 "Pledge": {
     *                     "RegistNo": "新抵F2120160021",
     *                     "RegistOffice": "伊宁县市场监督管理局",
     *                     "RegistDate": "2016-08-05 12:00:00"
     *                 },
     *                 "SecuredClaim": {
     *                     "AssuranceScope": "1,2,3,4,",
     *                     "Amount": 8000,
     *                     "Kind": "买卖合同",
     *                     "FulfillObligation": "2016-03-01至2020-03-01",
     *                     "Remark": ""
     *                 },
     *                 "PledgeeList": [
     *                     {
     *                         "IdentityType": "企业法人营业执照(非公司)",
     *                         "IdentityNo": "",
     *                         "Name": "广发银行股份有限公司乌鲁木齐分行"
     *                     }
     *                 ]
     *             }
     *         }
     *     ]
     * }
     * @apiUse QccError
     */
    private Object GetChattelMortgage(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        List<JSONObject> PledgeeList = listQuery("qcc.查询动产抵押PledgeeList", params);
        List<JSONObject> GuaranteeList = listQuery("qcc.查询动产抵押GuaranteeList", params);
        List<JSONObject> ChangeList = listQuery("qcc.查询动产抵押ChangeList", params);
        List<JSONObject> list = listQuery("qcc.查询动产抵押", params);
        for (Object o : list) {
            JSONObject object = (JSONObject) o;
            JSONObject detail = new JSONObject();
            JSONObject Pledge = new JSONObject();
            JSONObject SecuredClaim = new JSONObject();
            JSONObject CancelInfo = new JSONObject();
            Iterator<Map.Entry<String, Object>> it = object.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                if (entry.getKey().startsWith("Ex1")) {
                    Pledge.put(entry.getKey().replace("Ex1", ""), entry.getValue());
                    it.remove();
                }
                if (entry.getKey().startsWith("Ex2")) {
                    SecuredClaim.put(entry.getKey().replace("Ex2", ""), entry.getValue());
                    it.remove();
                }
                if (entry.getKey().startsWith("Ex3")) {
                    CancelInfo.put(entry.getKey().replace("Ex3", ""), entry.getValue());
                    it.remove();
                }
            }
            detail.put("Pledge", Pledge);
            detail.put("SecuredClaim", SecuredClaim);
            detail.put("CancelInfo", CancelInfo);
            detail.put("PledgeeList",
                PledgeeList
                    .stream()
                    .map(i -> (JSONObject) i)
                    .filter(i -> {
                        if (Objects.equals(i.getOrDefault("CmId", "##"), object.getOrDefault("InnerId", "$$"))) {
                            i.remove("CmId");
                            return true;
                        }
                        return false;
                    })
                    .toArray()
            );
            detail.put("GuaranteeList",
                GuaranteeList
                    .stream()
                    .filter(oo -> {
                        JSONObject i = (JSONObject) oo;
                        if (Objects.equals(i.getOrDefault("CmId", "##"), object.getOrDefault("InnerId", "$$"))) {
                            i.remove("CmId");
                            return true;
                        }
                        return false;
                    })
                    .toArray()
            );
            detail.put("ChangeList",
                ChangeList
                    .stream()
                    .filter(oo -> {
                        JSONObject i = (JSONObject) oo;
                        if (Objects.equals(i.getOrDefault("CmId", "##"), object.getOrDefault("InnerId", "$$"))) {
                            i.remove("CmId");
                            return true;
                        }
                        return false;
                    })
                    .toArray()
            );

            object.remove("InnerId");
            object.put("Detail", detail);
        }
        return list;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/EnvPunishment/GetEnvPunishmentDetails 环保处罚详情
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} id id
     *
     * @apiSuccess {string} CaseNo 决定书文号
     * @apiSuccess {string} IllegalType 违法类型
     * @apiSuccess {string} PunishReason 处罚事由
     * @apiSuccess {string} PunishBasis 处罚依据
     * @apiSuccess {string} PunishmentResult 处罚结果
     * @apiSuccess {string} PunishDate 处罚日期
     * @apiSuccess {string} PunishGov 处罚单位
     * @apiSuccess {string} Implementation 执行情况
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "CaseNo": "资环罚[2017]48号",
     *         "PunishGov": "资阳市环境保护局",
     *         "PunishDate": "2017-09-30 12:00:00",
     *         "PunishmentResult": "我局决定对你单位处以3万元（大写：叁万元整）的罚款。",
     *         "PunishBasis": "依据《建设项目环保保护管理条例》第二十八条之规定。",
     *         "IllegalType": "",
     *         "PunishReason": "发现你单位实施了以下环境违法行为：你单位新建汽车零部件项目未经环保竣工验收即投产使用。",
     *         "Implementation": ""
     *     }
     * }
     * @apiUse QccError
     */
    private Object GetEnvPunishmentDetails(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return singleQuery("qcc.查询环保处罚详情", params);
    }

    /**
     *
     * @api {get} {企查查数据查询服务地址}/EquityThrough/GetEquityThrough 查询股权穿透信息
     *
     * {
     *     "OrderNumber": "EQUITYTHROUGH2019040817072690532597",
     *     "Result": {
     *         "KeyNo": "befe52d9753b511b6aef5e33fe00f97d",
     *         "Name": "大连万达集团股份有限公司",
     *         "Count": "2",
     *         "Children": [
     *             {
     *                 "KeyNo": "971e2cafbecd8c978e959d69fc305f42",
     *                 "Name": "大连合兴投资有限公司",
     *                 "Category": "0",
     *                 "FundedRatio": "99.76%",
     *                 "InParentActualRadio": "99.76%",
     *                 "Count": "2",
     *                 "Grade": "1",
     *                 "ShouldCapi": "99760",
     *                 "StockRightNum": "",
     *                 "Children": [
     *                     {
     *                         "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                         "Name": "王健林",
     *                         "Category": "2",
     *                         "FundedRatio": "98.00%",
     *                         "InParentActualRadio": "97.76%",
     *                         "Count": "0",
     *                         "Grade": "2",
     *                         "ShouldCapi": "7702.8",
     *                         "StockRightNum": "",
     *                         "Children": null,
     *                         "ShortStatus": ""
     *                     },
     *                     {
     *                         "KeyNo": "pdd3325c48473fcd64180921d82ead80",
     *                         "Name": "王思聪",
     *                         "Category": "2",
     *                         "FundedRatio": "2.00%",
     *                         "InParentActualRadio": "2.00%",
     *                         "Count": "0",
     *                         "Grade": "2",
     *                         "ShouldCapi": "157.2",
     *                         "StockRightNum": "",
     *                         "Children": null,
     *                         "ShortStatus": ""
     *                     }
     *                 ],
     *                 "ShortStatus": "存续"
     *             },
     *             {
     *                 "KeyNo": "pea5ac417585edc0effd7d23406510da",
     *                 "Name": "王健林",
     *                 "Category": "2",
     *                 "FundedRatio": "0.24%",
     *                 "InParentActualRadio": "0.24%",
     *                 "Count": "0",
     *                 "Grade": "1",
     *                 "ShouldCapi": "240",
     *                 "StockRightNum": "",
     *                 "Children": null,
     *                 "ShortStatus": ""
     *             }
     *         ]
     *     },
     *     "Status": "200",
     *     "Message": "查询成功"
     * }
     * @param channelHandlerContext
     * @param request
     * @param params
     * @return
     */
    private Object GetEquityThrough(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return singleQuery("qcc.查询股权穿透信息", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/EnvPunishment/GetEnvPunishmentList 环保处罚列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} Id Id
     * @apiSuccess {string} CaseNo 决定书文号
     * @apiSuccess {string} PunishDate 处罚日期
     * @apiSuccess {string} IllegalType 违法类型
     * @apiSuccess {string} PunishGov 处罚单位
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 1,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "CaseNo": "资环罚[2017]48号",
     *             "PunishGov": "资阳市环境保护局",
     *             "PunishDate": "2017-09-30 12:00:00",
     *             "IllegalType": "",
     *             "Id": "17e3156d060094b649c7f8be77d15fa2"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
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
     * @api {get} {企查查数据查询服务地址}/LandMortgage/GetLandMortgageDetails 土地抵押详情
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} id id
     *
     *
     * @apiSuccess {string} LandSign 宗地标识
     * @apiSuccess {string} LandNo 宗地编号
     * @apiSuccess {string} AdministrativeArea 行政区
     * @apiSuccess {string} Acreage 土地面积（公顷）
     * @apiSuccess {string} Address 宗地地址
     * @apiSuccess {string} ObligeeNo 土地他项权利人证号
     * @apiSuccess {string} UsufructNo 土地使用权证号
     * @apiSuccess {string} MortgagorNature 土地抵押人性质
     * @apiSuccess {string} MortgagePurpose 抵押土地用途
     * @apiSuccess {string} NatureAndType 抵押土地权属性质与使用权类型
     * @apiSuccess {string} MortgageAcreage 抵押面积（公顷）
     * @apiSuccess {string} AssessmentPrice 评估金额（万元）
     * @apiSuccess {string} MortgagePrice 抵押金额（万元）
     * @apiSuccess {string} OnBoardStartTime 土地抵押登记起始时间
     * @apiSuccess {string} OnBoardEndTime 土地抵押结束时间
     *
     * @apiSuccess {object} MortgagorName 土地抵押人名称
     * @apiSuccess {string} MortgagorName.KeyNo KeyNo
     * @apiSuccess {stirng} MortgagorName.Name 名称
     *
     * @apiSuccess {object} MortgagePeople 土地抵押人名称
     * @apiSuccess {string} MortgagePeople.KeyNo KeyNo
     * @apiSuccess {stirng} MortgagePeople.Name 名称
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "AdministrativeArea": "510113000",
     *         "OnBoardStartTime": "2014-09-04 12:00:00",
     *         "ObligeeNo": "青他项（2014）第88号",
     *         "MortgagorNature": "有限责任公司",
     *         "OnBoardEndTime": "2017-09-02 12:00:00",
     *         "Address": "青白江区九峰路以南、青白江大道以西",
     *         "UsufructNo": "青国用（2011）第5536号",
     *         "MortgagePrice": "550.0000",
     *         "MortgagePurpose": "工业用地",
     *         "Acreage": "1.1357",
     *         "LandNo": "QBJ1-22-80",
     *         "MortgagorName": {
     *             "KeyNo": "4d3b51b032f55c2c8e196615bf4ba628",
     *             "Name": "成都农村商业银行股份有限公司青白江弥牟支行"
     *         },
     *         "MortgagePeople": {
     *             "KeyNo": "005ab78bc0c2e3535a40050f855fa844",
     *             "Name": "成都威格佳门窗有限公司"
     *         },
     *         "NatureAndType": "国有土地、出让",
     *         "LandSign": "",
     *         "MortgageAcreage": "1.1357",
     *         "AssessmentPrice": "554.2100"
     *     }
     * }
     * @apiUse QccError
     */
    private Object GetLandMortgageDetails(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询土地抵押详情", params);
        Iterator<Map.Entry<String, Object>> iterator = object.entrySet().iterator();
        JSONObject mo1 = newJsonObject();
        JSONObject mo2 = newJsonObject();

        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            if (entry.getKey().startsWith("Re1")) {
                mo1.put(entry.getKey().replace("Re1", ""), entry.getValue());
                iterator.remove();
            }
            if (entry.getKey().startsWith("Re2")) {
                mo2.put(entry.getKey().replace("Re2", ""), entry.getValue());
                iterator.remove();
            }
        }
        object.put("MortgagorName", mo1);
        object.put("MortgagePeople", mo2);

        return object;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/LandMortgage/GetLandMortgageList 土地抵押列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} Id Id
     * @apiSuccess {string} Address 地址
     * @apiSuccess {string} AdministrativeArea 行政区
     * @apiSuccess {string} MortgageAcreage 抵押面积（公顷）
     * @apiSuccess {string} MortgagePurpose 抵押土地用途
     * @apiSuccess {string} StartDate 开始日期
     * @apiSuccess {string} EndDate 结束日期
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 2,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "AdministrativeArea": "510113000",
     *             "StartDate": "2014-09-04 12:00:00",
     *             "Address": "青白江区九峰路以南、青白江大道以西",
     *             "Id": "a05a38627a629a6f1a49fef7fff4d648",
     *             "EndDate": "2017-09-02 12:00:00",
     *             "MortgageAcreage": "1.1357",
     *             "MortgagePurpose": "工业用地"
     *         },
     *         {
     *             "AdministrativeArea": "510113000",
     *             "StartDate": "2012-06-28 12:00:00",
     *             "Address": "青白江区九峰路以南、青白江大道以西",
     *             "Id": "71f3f48cc1cf485f0b4516879c39f311",
     *             "MortgageAcreage": "1.1357",
     *             "MortgagePurpose": "工业用地"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetLandMortgageList(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询土地抵押列表", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/JudicialSale/GetJudicialSaleDetail 司法拍卖详情
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} id id
     *
     * @apiSuccess {string} Title 标题
     * @apiSuccess {string} Context 详情内容
     *
     *
     * @apiSuccessExample 请求成功:
     *
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "Context": "<p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 25.15pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">江苏省无锡市新吴区人民法院将于<\/span><span style=\"color: red;line-height: 150.0%;font-size: 14.0pt;\">2018<\/span><span style=\"color: red;line-height: 150.0%;font-size: 14.0pt;\">年<span>5<\/span>月<span>4<\/span>日<span>10<\/span>时<\/span><span style=\"color: red;line-height: 150.0%;font-size: 14.0pt;\">至<span>2018<\/span>年<span>5<\/span>月<span>5<\/span>日<span>10<\/span>时止<\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">（延时除外）在阿里巴巴司法拍卖网络平台（本院账户名：江苏省无锡市新吴区人民法院，网址<span><a target=\"_blank\">https://sf.taobao.com<\/a><\/span>）进行公开拍卖活动，现公告如下：<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 29.9pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">一、拍卖标的：<\/span><span style=\"color: red;line-height: 150.0%;font-size: 14.0pt;\">无锡尚德太阳能电力有限公司所有的一批太阳能组件。<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 29.9pt;\" class=\"MsoNormal\"><span style=\"color: red;line-height: 150.0%;font-size: 14.0pt;\">起拍价：<span>2737600<\/span>元；保证金：<span>400000<\/span>元；加价幅度：<span>13000<\/span>元；<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;\" class=\"MsoNormal\"><span style=\"color: red;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: red;line-height: 150.0%;font-size: 14.0pt;\">【特别提醒】按现状拍卖，法院不保证资产质量，以交付当天为现状为准。无锡尚德太阳能电力有限公司所有的一批太阳能组件<span>6678<\/span>片，合计<span>1760445<\/span>瓦，评估值（含税）为<span>4887675<\/span>元，该批资产现场勘察主要采用目测观察手段，未使用仪器对委估资产进行测试和查验，不可能确定其有无内部缺损。拍卖成交后，相关手续由买受人自行办理，并承担费用。买受人应当自收到本院成交确认书之日起十五日内自行前往尚德公司提取上述资产，由此产生的整理、装载、运输等相关费用和安全责任均由买受人承担。付款期限为竞拍成功后<span>10<\/span>日内。<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 27.85pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">二、咨询、展示看样的时间与方式：自公告之日起至开拍当日止（双休、节假日除外）接受咨询<\/span><strong><span style=\"color: red;\">（咨询时间为正常工作日，上午<span>9<\/span>点至<span>11<\/span>点，下午<span>1<\/span>点至<span>4<\/span>点），咨询人：无锡嘉元拍卖行有限公司 张伟<span>13771020898 ,本次拍卖不安排看样。<\/span><\/span><\/strong><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 25.15pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">三、拍卖裁定与拍卖通知邮寄送达到被执行人住所地。<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">四、优先购买权人参与竞拍情况说明：<\/span><strong><span style=\"color: red;\">本标的如有优先购买权人，请最迟于开始拍卖前五日到本院执行局<\/span><\/strong><strong><span style=\"color: red;\">1703<\/span><\/strong><strong><span style=\"color: red;\">室提交书面申请。<\/span><\/strong><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">.<\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">五、竞买人应通过支付宝账户缴纳足额的拍卖保证金，<span>(<\/span>因本院目前条件无法满足接受竞买人线下缴纳保证金报名<span>)<\/span>。（保证金支付帮助：<span><a target=\"_blank\"><span style=\"color: black;\">https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q1<\/span><\/a><\/span>）<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">六、拍卖成交余款请在成交当日起<span>10<\/span>日内支付：<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">1<\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">、银行付款：通过银行汇款到法院指定帐户（户名：<\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">无锡市新吴区人民法院，开户银行：中国建设银行股份有限公司无锡高新技术产业开发区支行，账号：<span>32050161543600000413<\/span><\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">）<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">2<\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">、支付宝网付款：登录我的淘宝<span>-<\/span>我的拍卖支付（付款教程：<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"><a target=\"_blank\"><span style=\"color: black;\">https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q8<\/span><\/a><\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">）；<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 25.8pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">七、司法拍卖因标的物本身价值，其起拍价、保证金、竞拍成交价格相对较高的。竞买人参与竞拍，支付保证金及余款可能当天限额而无法支付，请根据自身情况选择网上充值银行。各大银行充值和支付限额的查询网址：<span><a target=\"_blank\"><span style=\"color: black;\">https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q1<\/span><\/a><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 25.8pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">八、委托竞买的，委托人与受托人须提前将委托手续将法院确认，竞买成功后，及时与承办法官至法院办理交接手续。如委托手续不全，竞买活动认定为受托人的个人行为。<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">九、特别提醒：标的物以实物现状为准，本院不承担本标的瑕疵保证。有意者请亲自实地看样，未看样的竞买人视为对本标的实物现状的确认，责任自负。<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">十、与本标的物有利害关系的当事人可参加竞拍，不参加竞拍的请关注本次拍卖活动的整个过程。<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">十一、对本次拍卖标的物权属有异议者，请于开拍当日前与本院联系。<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">十二、本拍卖标的物，可（或者不可）<span style=\"background: white;\">办理银行贷款的。如可以，请竞买人关注“拍卖贷款”栏目。并要求注明银行贷款最迟发放到法院的时间及操作程序及相应法律后果<span>(<\/span>可在须知中明确<span>)<\/span>。<\/span><span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">   <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">竞买人在拍卖竞价前请务必再仔细阅读本院发布的拍卖须知，遵守拍卖须知规定。<span><\/span><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 24.45pt;\" class=\"MsoNormal\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">本规则其他未尽事宜请向本院咨询，系统平台管理：<span>0510-82211199<\/span>；淘宝技术咨询：<span>400-822-2870<\/span>；法院举报监督：<\/span><span style=\"color: #333333;line-height: 150.0%;font-size: 14.0pt;\">0510-81190520<\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\">。<\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;text-align: right;line-height: 150.0%;text-indent: 25.15pt;\" class=\"MsoNormal\" align=\"right\"><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <\/span><span style=\"color: black;line-height: 150.0%;font-size: 14.0pt;\"> <a target=\"_blank\"><span style=\"color: black;\"><span>江苏省无锡市<\/span><\/span><span style=\"color: black;\"><span>新吴区<\/span><\/span><span style=\"color: black;\"><span>人民法院<\/span><\/span><\/a><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p><p style=\"background: white;margin: 0.0cm 0.0cm 0.0pt;text-align: right;line-height: 150.0%;text-indent: 23.75pt;\" class=\"MsoNormal\" align=\"right\"><span style=\"color: red;line-height: 150.0%;font-size: 14.0pt;\">2018<\/span><span style=\"color: red;line-height: 150.0%;font-size: 14.0pt;\">年<span>4<\/span>月<span>18<\/span>日<\/span><span style=\"line-height: 150.0%;font-size: 14.0pt;\"><\/span><\/p><p><span style=\"color: #000000;\"><\/span><\/p>",
     *         "Title": "无锡市新吴区人民法院关于无锡尚德太阳能电力有限公司所有的一批太阳能组件。（第二次拍卖）的公告"
     *     }
     * }
     * @apiUse QccError
     */
    private Object GetJudicialSaleDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询司法拍卖详情", params);
//        if(object.containsKey("Context")){
//            object.put("Context", new String(Base64.getDecoder().decode(object.getString("Context"))));
//        }
        return object;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/JudicialSale/GetJudicialSaleList 司法拍卖列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} Id 主键
     * @apiSuccess {string} Name 标题
     * @apiSuccess {string} ExecuteGov 委托法院
     * @apiSuccess {string} ActionRemark 拍卖时间
     * @apiSuccess {string} YiWu 起拍价
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 2,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "ActionRemark": "2018年5月4日10时至2018年5月5日10时止",
     *             "YiWu": "2,737,600",
     *             "Id": "ba518106e41c0966120d69236f3e0eb1",
     *             "ExecuteGov": "无锡市新吴区人民法院",
     *             "Name": "无锡市新吴区人民法院关于无锡尚德太阳能电力有限公司所有的一批太阳能组件。（第二次拍卖）的公告"
     *         },
     *         {
     *             "ActionRemark": "2018年4月16日10时至2018年4月17日10时止",
     *             "YiWu": "3,422,000",
     *             "Id": "76180fda649d71a71642f5e3ef703b9e",
     *             "ExecuteGov": "无锡市新吴区人民法院",
     *             "Name": "无锡市新吴区人民法院关于无锡尚德太阳能电力有限公司所有的一批太阳能组件。（第一次拍卖）的公告"
     *         }
     *     ]
     * }
     * @apiUse QccError
     */
    private Object GetJudicialSaleList(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询司法拍卖列表", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/ECIException/GetOpException 企业经营异常
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {string} AddReason 列入经营异常名录原因
     * @apiSuccess {string} AddDate 列入日期
     * @apiSuccess {string} RomoveReason 移出经营异常名录原因
     * @apiSuccess {string} RemoveDate 移出日期
     * @apiSuccess {string} DecisionOffice 作出决定机关
     * @apiSuccess {string} RemoveDecisionOffice 移除决定机关
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": [
     *         {
     *             "AddDate": "2015-07-08 12:00:00",
     *             "RemoveDate": "2016-01-29 12:00:00",
     *             "AddReason": "未依照《企业信息公示暂行条例》第八条规定的期限公示年度报告的",
     *             "RomoveReason": "列入经营异常名录3年内且依照《经营异常名录管理办法》第六条规定被列入经营异常名录的企业，可以在补报未报年份的年度报告并公示后，申请移出",
     *             "DecisionOffice": "北京市工商行政管理局海淀分局"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetOpException(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return listQuery("qcc.查询企业经营异常信息", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/JudicialAssistance/GetJudicialAssistance 司法协助信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     *
     * @apiSuccess {string} ExecutedBy 被执行人
     * @apiSuccess {string} EquityAmount 股权数额
     * @apiSuccess {string} EnforcementCourt 执行法院
     * @apiSuccess {string} ExecutionNoticeNum 执行通知书文号
     * @apiSuccess {string} Status 类型
     *
     * @apiSuccess {object} EquityFreezeDetail 股权冻结情况
     * @apiSuccess {string} EquityFreezeDetail.CompanyName 相关企业名称
     * @apiSuccess {string} EquityFreezeDetail.ExecutionMatters 执行事项
     * @apiSuccess {string} EquityFreezeDetail.ExecutionDocNum 执行文书文号
     * @apiSuccess {string} EquityFreezeDetail.ExecutionVerdictNum 执行裁定书文号
     * @apiSuccess {string} EquityFreezeDetail.ExecutedPersonDocType 被执行人证件种类
     * @apiSuccess {string} EquityFreezeDetail.ExecutedPersonDocNum 被执行人证件号码
     * @apiSuccess {string} EquityFreezeDetail.FreezeStartDate 冻结开始日期
     * @apiSuccess {string} EquityFreezeDetail.FreezeEndDate 冻结结束日期
     * @apiSuccess {string} EquityFreezeDetail.FreezeTerm 冻结期限
     * @apiSuccess {string} EquityFreezeDetail.PublicDate 公示日期
     *
     * @apiSuccess {object} EquityUnFreezeDetail 解除冻结详情
     * @apiSuccess {string} EquityUnFreezeDetail.ExecutionMatters 执行事项
     * @apiSuccess {string} EquityUnFreezeDetail.ExecutionVerdictNum 执行裁定书文号
     * @apiSuccess {string} EquityUnFreezeDetail.ExecutionDocNum 执行文书文号
     * @apiSuccess {string} EquityUnFreezeDetail.ExecutedPersonDocType 被执行人证件种类
     * @apiSuccess {string} EquityUnFreezeDetail.ExecutedPersonDocNum 被执行人证件号码
     * @apiSuccess {string} EquityUnFreezeDetail.UnFreezeDate 解除冻结日期
     * @apiSuccess {string} EquityUnFreezeDetail.PublicDate 公示日期
     * @apiSuccess {string} EquityUnFreezeDetail.ThawOrgan 解冻机关
     * @apiSuccess {string} EquityUnFreezeDetail.ThawDocNo 解冻文书号
     *
     * @apiSuccess {object} JudicialPartnersChangeDetail 股东变更信息
     * @apiSuccess {string} JudicialPartnersChangeDetail.ExecutionMatters 执行事项
     * @apiSuccess {string} JudicialPartnersChangeDetail.ExecutionVerdictNum 执行裁定书文号
     * @apiSuccess {string} JudicialPartnersChangeDetail.ExecutedPersonDocType 被执行人证件种类
     * @apiSuccess {string} JudicialPartnersChangeDetail.ExecutedPersonDocNum 被执行人证件号码
     * @apiSuccess {string} JudicialPartnersChangeDetail.Assignee 受让人
     * @apiSuccess {string} JudicialPartnersChangeDetail.AssistExecDate 协助执行日期
     * @apiSuccess {string} JudicialPartnersChangeDetail.AssigneeDocKind 受让人证件种类
     * @apiSuccess {string} JudicialPartnersChangeDetail.AssigneeRegNo 受让人证件号码
     * @apiSuccess {string} JudicialPartnersChangeDetail.StockCompanyName 股权所在公司名称
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status":"200",
     *     "Message":"查询成功",
     *     "Result":[
     *         {
     *             "EnforcementCourt":"北京市第二中级人民法院",
     *             "EquityUnFreezeDetail":{
     *             },
     *             "ExecutedBy":"霍庆华",
     *             "Status":"股权冻结|冻结",
     *             "JudicialPartnersChangeDetail":{
     *             },
     *             "EquityAmount":"15000万人民币元",
     *             "ExecutionNoticeNum":"（2017）京02民初58号",
     *             "EquityFreezeDetail":{
     *                 "CompanyName":"霍庆华",
     *                 "FreezeTerm":"1095",
     *                 "ExecutionVerdictNum":"（2017）京02民初58号",
     *                 "ExecutedPersonDocNum":"",
     *                 "FreezeStartDate":"2017-09-13 12:00:00",
     *                 "FreezeEndDate":"2020-09-12 12:00:00",
     *                 "ExecutionMatters":"轮候冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType":"居民身份证"
     *             }
     *         },
     *         {
     *             "EnforcementCourt":"新疆维吾尔自治区高级人民法院",
     *             "EquityUnFreezeDetail":{
     *             },
     *             "ExecutedBy":"中国庆华能源集团有限公司",
     *             "Status":"股权冻结|冻结",
     *             "JudicialPartnersChangeDetail":{
     *             },
     *             "EquityAmount":"254984.5万人民币元",
     *             "ExecutionNoticeNum":"(2017)新执47号",
     *             "EquityFreezeDetail":{
     *                 "CompanyName":"中国庆华能源集团有限公司",
     *                 "FreezeTerm":"1095",
     *                 "ExecutionVerdictNum":"(2017)新执47号",
     *                 "ExecutedPersonDocNum":"110105011796483",
     *                 "FreezeStartDate":"2017-08-16 12:00:00",
     *                 "FreezeEndDate":"2020-08-16 12:00:00",
     *                 "ExecutionMatters":"轮候冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType":""
     *             }
     *         },
     *         {
     *             "EnforcementCourt":"杭州市西湖区人民法院",
     *             "EquityUnFreezeDetail":{
     *             },
     *             "ExecutedBy":"霍庆华",
     *             "Status":"股权冻结|冻结",
     *             "JudicialPartnersChangeDetail":{
     *             },
     *             "EquityAmount":"15000万人民币元",
     *             "ExecutionNoticeNum":"(2017)浙0106民初6913号",
     *             "EquityFreezeDetail":{
     *                 "CompanyName":"霍庆华",
     *                 "FreezeTerm":"1095",
     *                 "ExecutionVerdictNum":"(2017)浙0106民初6913号",
     *                 "ExecutedPersonDocNum":"",
     *                 "FreezeStartDate":"2017-08-29 12:00:00",
     *                 "FreezeEndDate":"2020-08-28 12:00:00",
     *                 "ExecutionMatters":"公示冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType":"居民身份证"
     *             }
     *         },
     *         {
     *             "EnforcementCourt":"北京市第二中级人民法院",
     *             "EquityUnFreezeDetail":{
     *             },
     *             "ExecutedBy":"中国庆华能源集团有限公司",
     *             "Status":"股权冻结|冻结",
     *             "JudicialPartnersChangeDetail":{
     *             },
     *             "EquityAmount":"254984.5万人民币元",
     *             "ExecutionNoticeNum":"(2017)京02民初58号",
     *             "EquityFreezeDetail":{
     *                 "CompanyName":"中国庆华能源集团有限公司",
     *                 "FreezeTerm":"1095",
     *                 "ExecutionVerdictNum":"(2017)京02民初58号",
     *                 "ExecutedPersonDocNum":"110105011796483",
     *                 "FreezeStartDate":"2017-09-13 12:00:00",
     *                 "FreezeEndDate":"2020-09-12 12:00:00",
     *                 "ExecutionMatters":"轮候冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType":""
     *             }
     *         },
     *         {
     *             "EnforcementCourt":"广东省广州市中级人民法院",
     *             "EquityUnFreezeDetail":{
     *                 "UnFreezeDate":"2017-07-26 12:00:00",
     *                 "ExecutedPersonDocNum":"110105011796483",
     *                 "ExecutionVerdictNum":"（2016）粤01执3073号",
     *                 "ExecutionMatters":"轮候冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType":""
     *             },
     *             "ExecutedBy":"中国庆华能源集团有限公司",
     *             "Status":"股权冻结|冻结",
     *             "JudicialPartnersChangeDetail":{
     *             },
     *             "EquityAmount":"254984.5万人民币元",
     *             "ExecutionNoticeNum":"（2016）粤01执3073号",
     *             "EquityFreezeDetail":{
     *             }
     *         },
     *         {
     *             "EnforcementCourt":"杭州市西湖区人民法院",
     *             "EquityUnFreezeDetail":{
     *                 "UnFreezeDate":"2017-12-15 12:00:00",
     *                 "ExecutedPersonDocNum":"110105011796483",
     *                 "ExecutionVerdictNum":"(2017)浙0106民初6913号",
     *                 "ExecutionMatters":"轮候冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType":""
     *             },
     *             "ExecutedBy":"中国庆华能源集团有限公司",
     *             "Status":"股权冻结|冻结",
     *             "JudicialPartnersChangeDetail":{
     *             },
     *             "EquityAmount":"254984.5万人民币元",
     *             "ExecutionNoticeNum":"(2017)浙0106民初6913号",
     *             "EquityFreezeDetail":{
     *             }
     *         },
     *         {
     *             "EnforcementCourt":"杭州市西湖区人民法院",
     *             "EquityUnFreezeDetail":{
     *                 "UnFreezeDate":"2017-12-15 12:00:00",
     *                 "ExecutedPersonDocNum":"110105011796483",
     *                 "ExecutionVerdictNum":"（2017）浙0106民初702、703号",
     *                 "ExecutionMatters":"轮候冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType":""
     *             },
     *             "ExecutedBy":"中国庆华能源集团有限公司",
     *             "Status":"股权冻结|冻结",
     *             "JudicialPartnersChangeDetail":{
     *             },
     *             "EquityAmount":"254984.5万人民币元",
     *             "ExecutionNoticeNum":"（2017）浙0106民初702、703号",
     *             "EquityFreezeDetail":{
     *             }
     *         },
     *         {
     *             "EnforcementCourt":"广东省广州市中级人民法院",
     *             "EquityUnFreezeDetail":{
     *                 "UnFreezeDate":"2017-07-26 12:00:00",
     *                 "ExecutedPersonDocNum":"110105011796483",
     *                 "ExecutionVerdictNum":"(2016)粤01执3073号",
     *                 "ExecutionMatters":"公示冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType":""
     *             },
     *             "ExecutedBy":"中国庆华能源集团有限公司",
     *             "Status":"股权冻结|冻结",
     *             "JudicialPartnersChangeDetail":{
     *             },
     *             "EquityAmount":"254984.5万人民币元",
     *             "ExecutionNoticeNum":"(2016)粤01执3073号",
     *             "EquityFreezeDetail":{
     *             }
     *         },
     *         {
     *             "EnforcementCourt":"广东省广州市中级人民法院",
     *             "EquityUnFreezeDetail":{
     *                 "UnFreezeDate":"2017-07-26 12:00:00",
     *                 "ExecutedPersonDocNum":"110105011796483",
     *                 "ExecutionVerdictNum":"（2016）粤01执3073号",
     *                 "ExecutionMatters":"续行冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType":"居民身份证"
     *             },
     *             "ExecutedBy":"霍庆华",
     *             "Status":"股权冻结|冻结",
     *             "JudicialPartnersChangeDetail":{
     *             },
     *             "EquityAmount":"15000万人民币元",
     *             "ExecutionNoticeNum":"（2016）粤01执3073号",
     *             "EquityFreezeDetail":{
     *             }
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetJudicialAssistance(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        List<JSONObject> list = listQuery("qcc.查询司法协助信息", params);
        List<JSONObject>[] ds = new List[]{
            listQuery("qcc.查询司法协助EquityFreezeDetail", params),
            listQuery("qcc.查询司法协助EquityUnFreezeDetail", params),
            listQuery("qcc.查询司法协助JudicialPartnersChangeDetail", params)
        };
        String[] keys = {
            "EquityFreezeDetail","EquityUnFreezeDetail","JudicialPartnersChangeDetail"
        };
        for (Object o : list) {
            JSONObject object = (JSONObject) o;
            String id = object.getString("InnerId");
            object.remove("InnerId");
            int i = 0;
            for (List<JSONObject> d : ds) {
                Iterator<JSONObject> it = d.iterator();
                JSONObject goal = new JSONObject();
                while(it.hasNext()){
                    JSONObject obj = it.next();
                    if(S.eq(obj.getString("JaInnerId"), id)){
                        goal.putAll(obj);
                        it.remove();
                        break;
                    }
                }
                goal.remove("JaInnerId");
                object.put(keys[i], goal);
                i++;
            }
        }
        return list;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/CourtAnnoV4/GetCourtNoticeInfo 开庭公告详情
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} id id
     *
     * @apiSuccess {string} Province 省份
     * @apiSuccess {string} CaseReason 案由
     * @apiSuccess {string} ScheduleTime 排期日期
     * @apiSuccess {string} ExecuteGov 法院
     * @apiSuccess {string} UndertakeDepartment 承办部门
     * @apiSuccess {string} ExecuteUnite 法庭
     * @apiSuccess {string} ChiefJudge 审判长/主审人
     * @apiSuccess {string} OpenTime 开庭日期
     * @apiSuccess {string} CaseNo 案号
     *
     * @apiSuccess {object[]} Prosecutor 上诉人信息
     * @apiSuccess {string} Prosecutor.Name 上诉人
     * @apiSuccess {string} Prosecutor.KeyNo KeyNo
     *
     * @apiSuccess {object[]} Defendant 被上诉人信息
     * @apiSuccess {string} Defendant.Name 被上诉人
     * @apiSuccess {string} Defendant.KeyNo KeyNo
     *
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "CaseNo": "（2017）沪0112民初23287号",
     *         "ExecuteUnite": "新虹桥第六法庭",
     *         "OpenTime": "2017-10-09 01:45:00",
     *         "ScheduleTime": "2017-10-09 12:00:00",
     *         "Defendant": [
     *             {
     *                 "KeyNo": "4659626b1e5e43f1bcad8c268753216e",
     *                 "Name": "北京小桔科技有限公司"
     *             }
     *         ],
     *         "CaseReason": "运输合同纠纷",
     *         "Prosecutor": [
     *             {
     *                 "KeyNo": "",
     *                 "Name": "林允昌"
     *             }
     *         ],
     *         "UndertakeDepartment": "新虹桥法庭",
     *         "ChiefJudge": "陈雪琼",
     *         "Province": "上海",
     *         "ExecuteGov": "闵行"
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GetCourtNoticeInfo(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询开庭公告详情", params);
        params.put("type", "01");
        object.put("Prosecutor", listQuery("qcc.查询开庭公告关联人", params));
        params.put("type", "02");
        object.put("Defendant", listQuery("qcc.查询开庭公告关联人", params));
        return object;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/CourtAnnoV4/SearchCourtNotice 开庭公告列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} DefendantList 被告/被上诉人
     * @apiSuccess {string} ExecuteGov 法院
     * @apiSuccess {string} ProsecutorList 原告/上诉人
     * @apiSuccess {string} LiAnDate 开庭日期
     * @apiSuccess {string} CaseReason 案由
     * @apiSuccess {string} Id 内部ID
     * @apiSuccess {string} CaseNo 案号
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 10,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "CaseNo": "（2017）沪0112民初23287号",
     *             "ProsecutorList": "林允昌",
     *             "LiAnDate": "2017-10-09 01:45:00",
     *             "CaseReason": "运输合同纠纷",
     *             "Id": "88f9247d61008eb59cc932358554e78f5",
     *             "DefendantList": "北京小桔科技有限公司",
     *             "ExecuteGov": "闵行"
     *         },
     *         {
     *             "CaseNo": "（2017）沪0115民初15113号",
     *             "LiAnDate": "2017-07-25 01:40:00",
     *             "CaseReason": "生命权、健康权、身体权纠纷",
     *             "Id": "b54ec153ee9dd8a073eda1b0b42c044b5",
     *             "ExecuteGov": "浦东"
     *         },
     *         {
     *             "CaseNo": "（2017）沪0112民初18563号",
     *             "LiAnDate": "2017-07-24 08:45:00",
     *             "CaseReason": "机动车交通事故责任纠纷",
     *             "Id": "86f1f6b1e100c82775d7188739c37d865",
     *             "ExecuteGov": "闵行"
     *         },
     *         {
     *             "CaseNo": "（2016）浙0702民初14535号",
     *             "LiAnDate": "2017-06-22 09:30:00",
     *             "CaseReason": "生命权、健康权、身体权纠纷",
     *             "Id": "6cb34523c61a2085eb3cfa5c830164c15",
     *             "ExecuteGov": "金华市婺城区人民法院"
     *         },
     *         {
     *             "CaseNo": "（2017）粤03民终7473号",
     *             "LiAnDate": "2017-05-26 04:10:00",
     *             "CaseReason": "机动车交通事故责任纠纷",
     *             "Id": "a00a158c02b62a4c134302821b8984575",
     *             "ExecuteGov": "深圳市中级人民法院"
     *         },
     *         {
     *             "CaseNo": "（2017）粤03民终7468号",
     *             "LiAnDate": "2017-05-24 11:00:00",
     *             "CaseReason": "机动车交通事故责任纠纷",
     *             "Id": "753546f91fcefdf7bdb0bc504bff03fd5",
     *             "ExecuteGov": "深圳市中级人民法院"
     *         },
     *         {
     *             "CaseNo": "（2017）沪0104民初5567号",
     *             "LiAnDate": "2017-05-18 02:00:00",
     *             "CaseReason": "财产损害赔偿纠纷",
     *             "Id": "f82d642cc3e9d08000b9d46801ccb0295",
     *             "ExecuteGov": "徐汇"
     *         },
     *         {
     *             "CaseNo": "(2017)渝0101民初4156号",
     *             "LiAnDate": "2017-05-11 02:30:00",
     *             "CaseReason": "运输合同纠纷",
     *             "Id": "92203bb36fede57755d60f0db01a61125",
     *             "ExecuteGov": "重庆市万州区人民法院"
     *         },
     *         {
     *             "CaseNo": "（2017）浙07民终1315号",
     *             "LiAnDate": "2017-04-26 08:40:00",
     *             "CaseReason": "机动车交通事故责任纠纷",
     *             "Id": "4c283bea02021eb9d43e3d7e01e011a75",
     *             "ExecuteGov": "金华市中级人民法院"
     *         },
     *         {
     *             "CaseNo": "（2017）沪0109民初1841号",
     *             "LiAnDate": "2017-04-21 09:00:00",
     *             "CaseReason": "运输合同纠纷",
     *             "Id": "b0da79d5c7182f98e8462a78a017552f5",
     *             "ExecuteGov": "虹口 "
     *         }
     *     ]
     * }
     *
     *
     * @apiUse QccError
     */
    private Object SearchCourtNotice(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询开庭公告列表", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/CourtNoticeV4/SearchCourtAnnouncementDetail 法院公告详情
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} id id
     *
     * @apiSuccess {string} Court 公告法院
     * @apiSuccess {string} Content 内容
     * @apiSuccess {string} SubmitDate 上传日期
     * @apiSuccess {string} Province 所在省份代码
     * @apiSuccess {string} Category 类别
     * @apiSuccess {string} PublishedDate 刊登日期
     * @apiSuccess {string} Party 当事人
     *
     * @apiSuccess {object[]} NameKeyNoCollection 当事人信息
     * @apiSuccess {string} NameKeyNoCollection.KeyNo 公司KeyNo
     * @apiSuccess {string} NameKeyNoCollection.Name 名称
     *
     *
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "Category": "裁判文书",
     *         "Party": "吉林省安华通讯科技发展有限公司",
     *         "Content": "吉林省安华通讯科技发展有限公司：本院受理小米科技有限责任公司诉你侵害商标权纠纷一案，已审理终结，现依法向你公告送达（2017）吉01民初770号民事判决书。自公告之日起60日内来本院领取民事判决书，逾期即视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于吉林省高级人民法院。逾期本判决即发生法律效力。",
     *         "SubmitDate": "2019-03-29 12:00:00",
     *         "PublishedDate": "2019-04-04 12:00:00",
     *         "NameKeyNoCollection": [
     *             {
     *                 "KeyNo": "",
     *                 "Name": "吉林省安华通讯科技发展有限公司"
     *             }
     *         ],
     *         "Province": "JL",
     *         "Court": "吉林省长春市中级人民法院"
     *     }
     * }
     * @apiUse QccError
     */
    private Object SearchCourtAnnouncementDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询法院公告详情", params);
        object.put(
            "NameKeyNoCollection", listQuery("qcc.查询法院公告-当事人信息", params)
        );
        return object;
    }

    /**
     * @api {get} {企查查数据查询服务地址}/CourtNoticeV4/SearchCourtAnnouncement 法院公告列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     *
     * @apiSuccess {string} UploadDate 下载时间
     * @apiSuccess {string} Court 执行法院
     * @apiSuccess {string} Content 内容
     * @apiSuccess {string} Category 种类
     * @apiSuccess {string} PublishedDate 公布日期
     * @apiSuccess {string} PublishedPage 公布、页
     * @apiSuccess {string} Party 公司名、当事人
     * @apiSuccess {string} Id 主键
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status":"200",
     *     "Message":"查询成功",
     *     "Paging":{
     *         "PageSize":10,
     *         "TotalRecords":20,
     *         "PageIndex":1
     *     },
     *     "Result":[
     *         {
     *             "PublishedPage":"G41",
     *             "UploadDate":"2019-03-29 12:00:00",
     *             "Category":"裁判文书",
     *             "Party":"吉林省安华通讯科技发展有限公司",
     *             "Content":"吉林省安华通讯科技发展有限公司：本院受理小米科技有限责任公司诉你侵害商标权纠纷一案，已审理终结，现依法向你公告送达（2017）吉01民初770号民事判决书。自公告之日起60日内来本院领取民事判决书，逾期即视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于吉林省高级人民法院。逾期本判决即发生法律效力。",
     *             "PublishedDate":"2019-04-04 12:00:00",
     *             "Id":"52C02354F9069DD6CF28F1EB3C398EFE",
     *             "Court":"吉林省长春市中级人民法院"
     *         },
     *         {
     *             "PublishedPage":"G47",
     *             "UploadDate":"2019-03-28 12:00:00",
     *             "Category":"起诉状副本及开庭传票",
     *             "Party":"彭育洲",
     *             "Content":"彭育洲:本院受理原告小米科技有限责任公司与被告彭育洲、浙江淘宝网络有限公司侵害商标权纠纷一案，现依法向你送达起诉状副本、应诉通知书、举证通知书、合议庭组成人员通知书及开庭传票等。自本公告发出之日起，经过60日视为送达，提出答辩状和举证期限均为公告送达期满后的15日内。并定于2019年6月24日上午9时于本院二十...",
     *             "PublishedDate":"2019-04-02 12:00:00",
     *             "Id":"B3401AFD0F71D64E8589B4039EF8F9EB",
     *             "Court":"杭州市余杭区人民法院"
     *         },
     *         {
     *             "PublishedPage":"G15",
     *             "UploadDate":"2019-03-22 12:00:00",
     *             "Category":"裁判文书",
     *             "Party":"陈銮卿",
     *             "Content":"陈銮卿（公民身份号码440524196608184264）：本院受理原告小米科技有限责任公司诉你侵害商标权纠纷一案，现依法向你公告送达(2018)粤05民初20号民事判决。本院判决你应立即停止销售侵犯8911270号商标的商品，销毁库存侵权商品，在判决生效之日起10日内支付赔偿金2.5万元。自公告发出之日起经过60日即视为送达。...",
     *             "PublishedDate":"2019-03-28 12:00:00",
     *             "Id":"058D2A58AC141970DFD05935F43DD6E9",
     *             "Court":"广东省汕头市中级人民法院"
     *         },
     *         {
     *             "PublishedPage":"G14G15中缝",
     *             "UploadDate":"2019-03-19 12:00:00",
     *             "Category":"起诉状副本及开庭传票",
     *             "Party":"北京华美通联通讯器材销售中心",
     *             "Content":"北京市东城区人民法院公告北京华美通联通讯器材销售中心：本院受理原告小米科技有限责任公司诉北京华美通联通讯器材销售中心侵害商标权纠纷一案，现依法向你公司公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告送达期满后次日起15日和30日内。...",
     *             "PublishedDate":"2019-03-21 12:00:00",
     *             "Id":"E690D42D47091DADDB6E4BD541513BB1",
     *             "Court":"北京市东城区人民法院"
     *         },
     *         {
     *             "PublishedPage":"G26",
     *             "UploadDate":"2019-03-14 12:00:00",
     *             "Category":"起诉状副本及开庭传票",
     *             "Party":"龚坤宏",
     *             "Content":"龚坤宏：本院受理小米科技有限责任公司诉龚坤宏侵害商标权纠纷一案，现依法向你龚坤宏公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。龚坤宏自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。并定于举证期满后7月1日下午16时00(遇法定假日顺延)在本院上海市徐汇区龙漕路...",
     *             "PublishedDate":"2019-03-19 12:00:00",
     *             "Id":"3667F54FDF5BB15AA8192C3FC6DDD0CE",
     *             "Court":"上海市徐汇区人民法院"
     *         },
     *         {
     *             "PublishedPage":"G91",
     *             "UploadDate":"2019-03-21 12:00:00",
     *             "Category":"起诉状副本及开庭传票",
     *             "Party":"赖木辉",
     *             "Content":"赖木辉：本院受理的原告小米科技有限责任公司与被告赖木辉、浙江淘宝网络有限公司侵害商标权纠纷一案，现依法向你公告送达起诉状副本、应诉通知书、举证通知书、开庭传票等。自本公告发出之日起，经过60日即视为送达，提出答辩状和举证期限均为公告送达期满后的15日内。并定于2019年6月26日下午14时30分在本院第二十三审判...",
     *             "PublishedDate":"2019-03-26 12:00:00",
     *             "Id":"008F9CFA6E0C42DDE50D759B6275F77C",
     *             "Court":"杭州市余杭区人民法院"
     *         },
     *         {
     *             "PublishedPage":"G68",
     *             "UploadDate":"2019-03-20 12:00:00",
     *             "Category":"裁判文书",
     *             "Party":"牟平区栋财手机店",
     *             "Content":"牟平区栋财手机店：本院受理原告小米科技有限责任公司诉被告牟平区栋财手机店侵害商标权纠纷一案，现依法向你方公告送达（2017）鲁06民初134号民事判决书，自公告之日起满60日，即视为送达。如不服本判决，可自送达之日起15日内向本院递交上诉状，上诉于山东省高级人民法院。逾期本判决即发生法律效力。...",
     *             "PublishedDate":"2019-03-24 12:00:00",
     *             "Id":"AACB2C6C9ADBE303D98BA65AFB0081BF",
     *             "Court":"山东省烟台市中级人民法院"
     *         },
     *         {
     *             "PublishedPage":"G26",
     *             "UploadDate":"2019-03-14 12:00:00",
     *             "Category":"起诉状副本及开庭传票",
     *             "Party":"周远沐",
     *             "Content":"周远沐：本院受理小米科技有限责任公司诉周远沐侵害商标权纠纷一案，现依法向你周远沐公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。周远沐自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。并定于举证期满后7月1日下午15时00分(遇法定假日顺延)在本院上海市徐汇区龙漕路...",
     *             "PublishedDate":"2019-03-19 12:00:00",
     *             "Id":"CF993D8969C131444897BA2A16FB3A24",
     *             "Court":"上海市徐汇区人民法院"
     *         },
     *         {
     *             "PublishedPage":"G26",
     *             "UploadDate":"2019-03-14 12:00:00",
     *             "Category":"起诉状副本及开庭传票",
     *             "Party":"吕晓佳",
     *             "Content":"吕晓佳：本院受理小米科技有限责任公司诉吕晓佳侵害商标权纠纷一案，现依法向你吕晓佳公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。吕晓佳自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。并定于举证期满后7月1日下午14时20分(遇法定假日顺延)在本院上海市徐汇区龙漕路...",
     *             "PublishedDate":"2019-03-19 12:00:00",
     *             "Id":"7B93214CE7506672043921D14BB4FAB6",
     *             "Court":"上海市徐汇区人民法院"
     *         },
     *         {
     *             "PublishedPage":"G26",
     *             "UploadDate":"2019-03-14 12:00:00",
     *             "Category":"起诉状副本及开庭传票",
     *             "Party":"曲阜诚盛商贸有限公司",
     *             "Content":"曲阜诚盛商贸有限公司：本院受理小米科技有限责任公司诉曲阜诚盛商贸有限公司侵害商标权纠纷一案，现依法向你曲阜诚盛商贸有限公司公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。曲阜诚盛商贸有限公司自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。...",
     *             "PublishedDate":"2019-03-19 12:00:00",
     *             "Id":"3667F54FDF5BB15A26439A69EEEC1DB0",
     *             "Court":"上海市徐汇区人民法院"
     *         }
     *     ]
     * }
     * @apiUse QccError
     */
    private Object SearchCourtAnnouncement(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询法院公告列表", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/JudgeDocV4/GetJudgementDetail 裁判文书详情
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} id id
     *
     * @apiSuccess {string} Id Id
     * @apiSuccess {string} CaseName 裁判文书名字
     * @apiSuccess {string} CaseNo 裁判文书编号
     * @apiSuccess {string} CaseType 裁判文书类型
     * @apiSuccess {string} Content 裁判文书内容
     * @apiSuccess {string} Court 执行法院
     * @apiSuccess {string} CreateDate 创建时间
     * @apiSuccess {string} SubmitDate 提交时间
     * @apiSuccess {string} UpdateDate 修改时间
     * @apiSuccess {string} Appellor 当事人
     * @apiSuccess {string} JudgeDate 裁判时间
     * @apiSuccess {string} CaseReason 案由
     * @apiSuccess {string} TrialRound 审理程序
     * @apiSuccess {string} Defendantlist 被告
     * @apiSuccess {string} Prosecutorlist 原告
     * @apiSuccess {string} IsValid 是否有效，True或false
     * @apiSuccess {string} ContentClear 裁判文书内容（QCC加工）
     * @apiSuccess {string} JudgeResult 判决结果（文书内容）
     * @apiSuccess {string} PartyInfo 当事人(文书内容)
     * @apiSuccess {string} TrialProcedure 审理经过(文书内容)
     * @apiSuccess {string} CourtConsider 本院认为(文书内容)
     * @apiSuccess {string} PlaintiffRequest 原告诉求(文书内容)
     * @apiSuccess {string} DefendantReply 被告答辩(文书内容)
     * @apiSuccess {string} CourtInspect 本院查明(文书内容)
     * @apiSuccess {string} PlaintiffRequestOfFirst 一审原告诉求(文书内容)
     * @apiSuccess {string} DefendantReplyOfFirst 一审被告答辩(文书内容)
     * @apiSuccess {string} CourtInspectOfFirst 一审法院查明(文书内容)
     * @apiSuccess {string} CourtConsiderOfFirst 一审法院认为(文书内容)
     * @apiSuccess {string} AppellantRequest 上诉人诉求(文书内容)
     * @apiSuccess {string} AppelleeArguing 被上诉人答辩(文书内容)
     * @apiSuccess {string} ExecuteProcess 执行经过(文书内容)
     * @apiSuccess {string} CollegiateBench 合议庭(文书内容)
     * @apiSuccess {string} JudegeDate 裁判日期(文书内容)
     * @apiSuccess {string} Recorder 记录员(文书内容)
     *
     * @apiSuccess {object} CourtNoticeList 关联开庭公告
     * @apiSuccess {string} CourtNoticeList.TotalNum 关联开庭公告总条目数
     * @apiSuccess {object[]} CourtNoticeList.CourtNoticeInfo 关联开庭公告详情
     * @apiSuccess {string} CourtNoticeList.CourtNoticeInfo.CaseNo 案号，CourtNoticeInfo列表字段
     * @apiSuccess {string} CourtNoticeList.CourtNoticeInfo.OpenDate 开庭日期，CourtNoticeInfo列表字段
     * @apiSuccess {string} CourtNoticeList.CourtNoticeInfo.Defendant 被告，CourtNoticeInfo列表字段
     * @apiSuccess {string} CourtNoticeList.CourtNoticeInfo.CaseReason 案由，CourtNoticeInfo列表字段
     * @apiSuccess {string} CourtNoticeList.CourtNoticeInfo.Prosecutor 原告，CourtNoticeInfo列表字段
     * @apiSuccess {string} CourtNoticeList.CourtNoticeInfo.Id 内部ID，CourtNoticeInfo列表字段
     * @apiSuccess {object[]} RelatedCompanies 关联公司列表
     * @apiSuccess {string} RelatedCompanies.KeyNo 公司KeyNo
     * @apiSuccess {string} RelatedCompanies.Name 公司名称
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status":"200",
     *     "Message":"查询成功",
     *     "Result":{
     *         "CollegiateBench":"\n审判长唐荣平\n审判员郑松荣\n审判员李旭兵\n",
     *         "SubmitDate":"2018-12-12 12:00:00",
     *         "ProsecutorList":[
     *             "广州银行股份有限公司惠州分行"
     *         ],
     *         "CreateDate":"2018-12-14 01:09:54",
     *         "Court":"广东省惠州市中级人民法院",
     *         "CaseNo":"（2018）粤13执异79号",
     *         "UpdateDate":"2018-12-14 01:09:54",
     *         "TrialRound":"",
     *         "JudgeDate":"2018-09-04 12:00:00",
     *         "DefendantList":[
     *             "惠州市腾飞盛世贸易有限公司",
     *             "王海雄",
     *             "惠州市维也纳惠尔曼酒店管理有限公司",
     *             "黄秋玲",
     *             "惠州市喜相逢实业有限公司",
     *             "王海霞",
     *             "翟好球"
     *         ],
     *         "IsValid":"true",
     *         "Appellor":[
     *             "惠州市腾飞盛世贸易有限公司",
     *             "广州银行股份有限公司惠州分行",
     *             "王海雄",
     *             "惠州市维也纳惠尔曼酒店管理有限公司",
     *             "黄秋玲",
     *             "石耀先",
     *             "惠州市喜相逢实业有限公司",
     *             "王海霞",
     *             "翟好球"
     *         ],
     *         "ContentClear":"<div style='TEXT-ALIGN: center; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 0cm; FONT-FAMILY: 宋体; FONT-SIZE: 22pt;'>广东省惠州市中级人民法院</div><div style='TEXT-ALIGN: center; LINE-HEIGHT: 30pt; MARGIN: 0.5pt 0cm; FONT-FAMILY: 仿宋; FONT-SIZE: 26pt;'>执 行 裁 定 书</div><div style='TEXT-ALIGN: right; LINE-HEIGHT: 30pt; MARGIN: 0.5pt 0cm;  FONT-FAMILY: 仿宋;FONT-SIZE: 16pt; '>（2018）粤13执异79号</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>异议人（案外人）：石耀先，男。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>申请执行人：<a href=\"https://www.qichacha.com/firm_fd9f01322888029ce2bbffc827505815.html\" target=\"_blank\">广州银行股份有限公司惠州分行</a>。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>法定代表人：郑文伟，行长。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>被执行人：<a href=\"https://www.qichacha.com/firm_e8b4f7b7e5aee43ffbb6887acdfa1da4.html\" target=\"_blank\">惠州市喜相逢实业有限公司</a>。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>法定代表人：王海雄。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>被执行人：王海雄，男。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>被执行人：黄秋玲，女。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>被执行人：<a href=\"https://www.qichacha.com/firm_f3599b7ea48c084ac5194cc7050d27d7.html\" target=\"_blank\">惠州市维也纳惠尔曼酒店管理有限公司</a>。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>法定代表人：王海雄。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>被执行人：王海霞，女。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>被执行人：翟好球，男。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>被执行人：<a href=\"https://www.qichacha.com/firm_e6cdaddebb6de8420a2e7784b1df7fcf.html\" target=\"_blank\">惠州市腾飞盛世贸易有限公司</a>。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>法定代表人：黄建光。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>本院在执行申请执行人<a href=\"https://www.qichacha.com/firm_fd9f01322888029ce2bbffc827505815.html\" target=\"_blank\">广州银行股份有限公司惠州分行</a>（下称广州银行惠州分行）与被执行人<a href=\"https://www.qichacha.com/firm_e8b4f7b7e5aee43ffbb6887acdfa1da4.html\" target=\"_blank\">惠州市喜相逢实业有限公司</a>（下称喜相逢公司）、王海雄、黄秋玲、<a href=\"https://www.qichacha.com/firm_f3599b7ea48c084ac5194cc7050d27d7.html\" target=\"_blank\">惠州市维也纳惠尔曼酒店管理有限公司</a>（下称惠尔曼公司）、王海霞、翟好球、<a href=\"https://www.qichacha.com/firm_e6cdaddebb6de8420a2e7784b1df7fcf.html\" target=\"_blank\">惠州市腾飞盛世贸易有限公司</a>（下称腾飞公司）金融借款合同纠纷一案中，案外人石耀先向本院提出执行异议，本院受理后，依法组成合议庭进行审查。本案现已审查终结。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>案外人石耀先提出异议申请称，请求暂停拍卖（2016）粤13执260号案涉的位于惠州市惠城区××办事处××大道××号合生国际新城GJ-1栋1层09号商铺（房产证号粤房地权证惠州字第××，下称涉案商铺）。事实与理由：2014年4月28日，石耀先与王海霞签订《房地产买卖合同》，由<a href=\"https://www.qichacha.com/firm_fe94e25618158044867e970e0d18644f.html\" target=\"_blank\">惠州市正能实业发展有限公司</a>作为王海霞的代理人，购买了王海霞的上述涉案商铺。当天石耀先以银行转账和现金支付的方式，向<a href=\"https://www.qichacha.com/firm_fe94e25618158044867e970e0d18644f.html\" target=\"_blank\">惠州市正能实业发展有限公司</a>支付了37万元首期款；此前支付了购房定金3万元；共支付了40万元房款。2016年3月5日，石耀先收到广东康景物业有限公司惠州分公司《交楼通知书》，当天支付了王海霞拖欠的物业费共3514.8元，同时与该物业公司签订了《前期物业管理服务协议》、《楼主售楼资料册》等。此后，石耀先一直催促王海霞及其代理人办理房产过户手续，但他们均以各种理由推脱。多次交涉后，王海霞及其代理人与石耀先于2016年12月23日签订了《补充协议》，石耀先同意延期办理产权过户手续，但是王海霞及代理人应每月支付申请人一定的经济损失。并同意先将该物业交给申请人使用和装修等。收楼后，石耀先与李婷签订《房屋租赁合同》，将涉案商铺租给其使用，租期5年，租金每月1000元。2016年3月起，石耀先对涉案商铺进行装修后，承租人正式营业。石耀先属于退休人员，上有年老患病的母亲需要赡养，为个人生计，基于对合生国际大楼盘开发商的信赖，购买涉案商铺，以便收取租金维持家用。石耀先已为涉案商铺支付了绝大部分对价，且已经收楼和装修、使用，为维护申请人的合法权益，恳请贵院暂停对涉案商铺的拍卖。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>案外人石耀先向本院提交了《房地产买卖合同》、《补充协议》、《业主收楼资料册》、《前期物业管理服务协议》、《商铺租赁合同》及物业管理费发票、《收据》、银行转账凭证等证据。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>经审查查明：</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>2013年12月5日，被执行人王海霞与申请执行人广州银行惠州分行签订《最高额抵押合同》，将包括涉案商铺在内的31套房产抵押给广州银行惠州分行，为被执行人喜相逢公司与广州银行惠州分行签订借款人民币5500万元的《授信协议书》、《流动资金借款合同》提供抵押担保，并办理了抵押登记手续。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>本案诉讼之前，惠州市惠城区人民法院根据广州银行惠州分行诉前财产保全申请，于2014年9月30日作出（2014）惠城法立保字第716号民事裁定书，裁定查封了被告王海雄、黄秋玲名下的房产、以及王海霞、翟好球、腾飞公司提供抵押担保的房产（包括涉案商铺）。惠州市惠城区人民法院于2015年1月20日立案受理了本案，并于2015年4月18日在《人民法院报》向喜相逢公司公告送达原告起诉状，公告期至2015年6月18日届满，即原告起诉状已于2015年6月18日送达喜相逢公司。在答辩期内，被告惠尔曼公司提出管辖权异议，惠州市惠城区人民法院作出（2015）惠城法民二初字第104号民事裁定书，裁定惠尔曼公司对管辖权提出的异议成立，将本案移送本院审理。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>申请执行人广州银行惠州分行与被执行人喜相逢公司、王海雄、黄秋玲、惠尔曼公司、王海霞、翟好球、腾飞盛世公司金融借款合同纠纷一案，执行依据为已发生法律效力的（2015）惠中法民二初字第13号民事判决，该判决确定：一、解除原告广州银行惠州分行与被告喜相逢公司签订的《授信协议书》（广银惠授字第[2013]0029号）和《流动资金借款合同》（广银惠借字第[2013]0050号）。二、被告喜相逢公司应于本判决生效之日起十日内，偿还原告广州银行惠州分行借款本金人民币48822740元，并支付利息、罚息（还款期限内的利息，按月利率5.8938&permil;计，从2014年8月21日起计算，先予合同解除日即2015年6月18日到期的，计算至借款合同约定的还款期限到期之日止；后于合同解除日到期的，还款期限视为在合同解除日到期，计算至2015年6月18日止。罚息也即逾期利息，罚息利率按在中国人民银行规定的同期同类贷款利率上浮15%的水平上加收50%计，从还款期限届满之次日计至借款清偿之日止，计算罚息的不再计算正常利息），以及依约定的还款期限内已产生的利息计算的复利（按在中国人民银行规定的同期同类贷款利率上浮15%的水平上加收50%计算至利息清偿之日止，对罚息不计复利）。三、被告王海雄、黄秋玲、惠尔曼公司对上列第二判项确定的债务承担连带清偿责任。承担保证责任后，有权向被告喜相逢公司追偿。四、原告广州银行惠州分行对被告王海霞提供抵押的其名下位于惠州市惠城区新岸路1号世贸中心31层C、D房（房产证号：粤房地权证惠州字第××号）、位于惠州市惠城区××办事处××大道××号合生国际新城GJ-2栋2层01号房产（房产证号：粤房地权证惠州字第××号）、位于惠州市惠城区××办事处××大道××号合生国际新城GJ-1栋1层05-30号的26套房产（房产证号：粤房地权证惠州字第××、11××30、1100220650-××5、11××64、11××66、11××68、11××72、11××75、11××78、11××80、11××81号）、位于惠州市××城区××半岛××东方××花园××号、××房产（房产证号：粤房地权证惠州字第××、11××66、11××68号）享有优先受偿权。被告王海霞在原告实现抵押权后，有权向被告喜相逢公司追偿。五、原告广州银行惠州分行对被告腾飞公司提供抵押的其名下位于惠州市桥东桃子园25号金典花园C栋1层06号房产（房产证号：粤房地权证惠州字第××号）、位于惠州市桥东桃子××花园××、××、××房产（房产证号：粤房地权证惠州字第××号）享有优先受偿权。被告腾飞公司在原告实现抵押权后，有权向被告喜相逢公司追偿。六、被告翟好球应在抵押合同中约定抵押的其名下位于惠州市××城区××半岛××东方××花园××房产（房产证号：粤房地权证惠州字第××号、粤房地权证惠州字第××号）的价值范围内向原告承担连带清偿责任。七、驳回原告广州银行惠州分行的其他诉讼请求。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>判决生效后，2016年4月25日，广州银行惠州分行向本院申请强制执行，本院经审查，于2016年5月4日立案执行，案号为（2016）粤13执260号。2016年9月22日，本院作出（2016）粤13执260号之二执行裁定书，裁定继续查封被执行人王海雄名下的13套房产、被执行人黄秋玲名下的1套房产、被执行人腾飞公司名下的2套房产和被执行人王海霞名下的31套房产（包括涉案商铺）。2017年9月28日，本院作出（2016）粤13执260号《公告》，告知相关人员，被执行人王海霞名下的29套房产（包括涉案商铺）已由房产管理部门办理登记查封，如对上述房产有租赁关系或对其权属有异议的，请于本公告发出之日起十五日向本院提交有关租赁合同、权属证明等证明材料，逾期，本院将依法强制执行。2018年3月6日，本院作出（2016）粤13执260号之三执行裁定书，裁定对被执行王海霞名下的31套房产（包括涉案商铺）进行拍卖。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>2018年7月31日，案外人石耀先向本院提出书面异议。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>本案争议焦点为案外人石耀先对于涉案商铺是否享有足以排除执行的实体权利。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>《中华人民共和国物权法》第一百九十一条第二款规定：“抵押期间，抵押人未经抵押权人同意，不得转让抵押财产，但受让人代为清偿债务消灭抵押权的除外”，《最高人民法院关于人民法院办理执行异议和复议案件若干问题的规定》第二十七条规定：“申请执行人对执行标的依法享有对抗案外人的担保物权等优先受偿权，人民法院对案外人提出的排除执行异议不予支持，但法律、司法解释另有规定的除外”。本案中，被执行人王海霞系设定抵押后，惠州市惠城区人民法院查封前，在未经抵押权人广州银行惠州分行同意的情况下，将涉案商铺转让给案外人石耀先，案外人石耀先也未代为清偿债务消灭抵押权，故涉案商铺的转让行为无效。案外人石耀先虽在法院查封之前受让涉案商铺，支付绝大部分价款，但由于申请执行人广州银行惠州分行对涉案商铺享有优先受偿权，根据上述事实和法律规定，案外人石耀先对涉案商铺并不享有足以排除执行的实体权利，本院查封、拍卖涉案商铺的执行行为并无不当。因此，案外人石耀先的异议请求，与事实不符，于法无据，本院均不予采纳。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>综上，依照《中华人民共和国民事诉讼法》第二百二十七条、《最高人民法院关于人民法院办理执行异议和复议案件若干问题的规定》第二十七条之规定，裁定如下：</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>驳回案外人石耀先的异议请求。</div><div style='LINE-HEIGHT: 25pt;TEXT-ALIGN:justify;TEXT-JUSTIFY:inter-ideograph; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>如不服本裁定，可以自本裁定送达之日起十五日内，向本院提起诉讼。</div><div style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 72pt 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>审 判 长　唐荣平</div><div style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 72pt 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>审 判 员　郑松荣</div><div style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 72pt 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>审 判 员　李旭兵</div><br/><div style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 72pt 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>二〇一八年九月四日</div><div style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 72pt 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>法官助理　张泽晖</div><div style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 72pt 0.5pt 0cm;FONT-FAMILY: 仿宋; FONT-SIZE: 16pt;'>书 记 员　陈铁洪</div>",
     *         "JudegeDate":"\n二零一八年九月四日\n",
     *         "CaseName":"石耀先、广州银行股份有限公司惠州分行金融借款合同纠纷执行审查类执行裁定书",
     *         "Recorder":"\n书记员陈铁洪\n",
     *         "PartyInfo":"\n异议人（案外人）：石耀先，男。\n申请执行人：广州银行股份有限公司惠州分行。\n法定代表人：郑文伟，行长。\n被执行人：惠州市喜相逢实业有限公司。\n法定代表人：王海雄。\n被执行人：王海雄，男。\n被执行人：黄秋玲，女。\n被执行人：惠州市维也纳惠尔曼酒店管理有限公司。\n法定代表人：王海雄。\n被执行人：王海霞，女。\n被执行人：翟好球，男。\n被执行人：惠州市腾飞盛世贸易有限公司。\n法定代表人：黄建光。\n",
     *         "ExecuteProcess":"\n案外人石耀先提出异议申请称，请求暂停拍卖（2016）粤13执260号案涉的位于惠州市惠城区××办事处××大道××号合生国际新城GJ-1栋1层09号商铺（房产证号粤房地权证惠州字第××，下称涉案商铺）。事实与理由：2014年4月28日，石耀先与王海霞签订《房地产买卖合同》，由惠州市正能实业发展有限公司作为王海霞的代理人，购买了王海霞的上述涉案商铺。当天石耀先以银行转账和现金支付的方式，向惠州市正能实业发展有限公司支付了37万元首期款；此前支付了购房定金3万元；共支付了40万元房款。2016年3月5日，石耀先收到广东康景物业有限公司惠州分公司《交楼通知书》，当天支付了王海霞拖欠的物业费共3514.8元，同时与该物业公司签订了《前期物业管理服务协议》、《楼主售楼资料册》等。此后，石耀先一直催促王海霞及其代理人办理房产过户手续，但他们均以各种理由推脱。多次交涉后，王海霞及其代理人与石耀先于2016年12月23日签订了《补充协议》，石耀先同意延期办理产权过户手续，但是王海霞及代理人应每月支付申请人一定的经济损失。并同意先将该物业交给申请人使用和装修等。收楼后，石耀先与李婷签订《房屋租赁合同》，将涉案商铺租给其使用，租期5年，租金每月1000元。2016年3月起，石耀先对涉案商铺进行装修后，承租人正式营业。石耀先属于退休人员，上有年老患病的母亲需要赡养，为个人生计，基于对合生国际大楼盘开发商的信赖，购买涉案商铺，以便收取租金维持家用。石耀先已为涉案商铺支付了绝大部分对价，且已经收楼和装修、使用，为维护申请人的合法权益，恳请贵院暂停对涉案商铺的拍卖。\n案外人石耀先向本院提交了《房地产买卖合同》、《补充协议》、《业主收楼资料册》、《前期物业管理服务协议》、《商铺租赁合同》及物业管理费发票、《收据》、银行转账凭证等证据。\n经审查查明：\n2013年12月5日，被执行人王海霞与申请执行人广州银行惠州分行签订《最高额抵押合同》，将包括涉案商铺在内的31套房产抵押给广州银行惠州分行，为被执行人喜相逢公司与广州银行惠州分行签订借款人民币5500万元的《授信协议书》、《流动资金借款合同》提供抵押担保，并办理了抵押登记手续。\n本案诉讼之前，惠州市惠城区人民法院根据广州银行惠州分行诉前财产保全申请，于2014年9月30日作出（2014）惠城法立保字第716号民事裁定书，裁定查封了被告王海雄、黄秋玲名下的房产、以及王海霞、翟好球、腾飞公司提供抵押担保的房产（包括涉案商铺）。惠州市惠城区人民法院于2015年1月20日立案受理了本案，并于2015年4月18日在《人民法院报》向喜相逢公司公告送达原告起诉状，公告期至2015年6月18日届满，即原告起诉状已于2015年6月18日送达喜相逢公司。在答辩期内，被告惠尔曼公司提出管辖权异议，惠州市惠城区人民法院作出（2015）惠城法民二初字第104号民事裁定书，裁定惠尔曼公司对管辖权提出的异议成立，将本案移送本院审理。\n申请执行人广州银行惠州分行与被执行人喜相逢公司、王海雄、黄秋玲、惠尔曼公司、王海霞、翟好球、腾飞盛世公司金融借款合同纠纷一案，执行依据为已发生法律效力的（2015）惠中法民二初字第13号民事判决，该判决确定：一、解除原告广州银行惠州分行与被告喜相逢公司签订的《授信协议书》（广银惠授字第[2013]0029号）和《流动资金借款合同》（广银惠借字第[2013]0050号）。二、被告喜相逢公司应于本判决生效之日起十日内，偿还原告广州银行惠州分行借款本金人民币48822740元，并支付利息、罚息（还款期限内的利息，按月利率5.8938‰计，从2014年8月21日起计算，先予合同解除日即2015年6月18日到期的，计算至借款合同约定的还款期限到期之日止；后于合同解除日到期的，还款期限视为在合同解除日到期，计算至2015年6月18日止。罚息也即逾期利息，罚息利率按在中国人民银行规定的同期同类贷款利率上浮15%的水平上加收50%计，从还款期限届满之次日计至借款清偿之日止，计算罚息的不再计算正常利息），以及依约定的还款期限内已产生的利息计算的复利（按在中国人民银行规定的同期同类贷款利率上浮15%的水平上加收50%计算至利息清偿之日止，对罚息不计复利）。三、被告王海雄、黄秋玲、惠尔曼公司对上列第二判项确定的债务承担连带清偿责任。承担保证责任后，有权向被告喜相逢公司追偿。四、原告广州银行惠州分行对被告王海霞提供抵押的其名下位于惠州市惠城区新岸路1号世贸中心31层C、D房（房产证号：粤房地权证惠州字第××号）、位于惠州市惠城区××办事处××大道××号合生国际新城GJ-2栋2层01号房产（房产证号：粤房地权证惠州字第××号）、位于惠州市惠城区××办事处××大道××号合生国际新城GJ-1栋1层05-30号的26套房产（房产证号：粤房地权证惠州字第××、11××30、1100220650-××5、11××64、11××66、11××68、11××72、11××75、11××78、11××80、11××81号）、位于惠州市××城区××半岛××东方××花园××号、××房产（房产证号：粤房地权证惠州字第××、11××66、11××68号）享有优先受偿权。被告王海霞在原告实现抵押权后，有权向被告喜相逢公司追偿。五、原告广州银行惠州分行对被告腾飞公司提供抵押的其名下位于惠州市桥东桃子园25号金典花园C栋1层06号房产（房产证号：粤房地权证惠州字第××号）、位于惠州市桥东桃子××花园××、××、××房产（房产证号：粤房地权证惠州字第××号）享有优先受偿权。被告腾飞公司在原告实现抵押权后，有权向被告喜相逢公司追偿。六、被告翟好球应在抵押合同中约定抵押的其名下位于惠州市××城区××半岛××东方××花园××房产（房产证号：粤房地权证惠州字第××号、粤房地权证惠州字第××号）的价值范围内向原告承担连带清偿责任。七、驳回原告广州银行惠州分行的其他诉讼请求。\n判决生效后，2016年4月25日，广州银行惠州分行向本院申请强制执行，本院经审查，于2016年5月4日立案执行，案号为（2016）粤13执260号。2016年9月22日，本院作出（2016）粤13执260号之二执行裁定书，裁定继续查封被执行人王海雄名下的13套房产、被执行人黄秋玲名下的1套房产、被执行人腾飞公司名下的2套房产和被执行人王海霞名下的31套房产（包括涉案商铺）。2017年9月28日，本院作出（2016）粤13执260号《公告》，告知相关人员，被执行人王海霞名下的29套房产（包括涉案商铺）已由房产管理部门办理登记查封，如对上述房产有租赁关系或对其权属有异议的，请于本公告发出之日起十五日向本院提交有关租赁合同、权属证明等证明材料，逾期，本院将依法强制执行。2018年3月6日，本院作出（2016）粤13执260号之三执行裁定书，裁定对被执行王海霞名下的31套房产（包括涉案商铺）进行拍卖。\n2018年7月31日，案外人石耀先向本院提出书面异议。\n本案争议焦点为案外人石耀先对于涉案商铺是否享有足以排除执行的实体权利。\n《中华人民共和国物权法》第一百九十一条第二款规定：“抵押期间，抵押人未经抵押权人同意，不得转让抵押财产，但受让人代为清偿债务消灭抵押权的除外”，《最高人民法院关于人民法院办理执行异议和复议案件若干问题的规定》第二十七条规定：“申请执行人对执行标的依法享有对抗案外人的担保物权等优先受偿权，人民法院对案外人提出的排除执行异议不予支持，但法律、司法解释另有规定的除外”。本案中，被执行人王海霞系设定抵押后，惠州市惠城区人民法院查封前，在未经抵押权人广州银行惠州分行同意的情况下，将涉案商铺转让给案外人石耀先，案外人石耀先也未代为清偿债务消灭抵押权，故涉案商铺的转让行为无效。案外人石耀先虽在法院查封之前受让涉案商铺，支付绝大部分价款，但由于申请执行人广州银行惠州分行对涉案商铺享有优先受偿权，根据上述事实和法律规定，案外人石耀先对涉案商铺并不享有足以排除执行的实体权利，本院查封、拍卖涉案商铺的执行行为并无不当。因此，案外人石耀先的异议请求，与事实不符，于法无据，本院均不予采纳。\n综上，依照《中华人民共和国民事诉讼法》第二百二十七条、《最高人民法院关于人民法院办理执行异议和复议案件若干问题的规定》第二十七条之规定，裁定如下：\n",
     *         "TrialProcedure":"\n本院在执行申请执行人广州银行股份有限公司惠州分行（下称广州银行惠州分行）与被执行人惠州市喜相逢实业有限公司（下称喜相逢公司）、王海雄、黄秋玲、惠州市维也纳惠尔曼酒店管理有限公司（下称惠尔曼公司）、王海霞、翟好球、惠州市腾飞盛世贸易有限公司（下称腾飞公司）金融借款合同纠纷一案中，案外人石耀先向本院提出执行异议，本院受理后，依法组成合议庭进行审查。本案现已审查终结。\n",
     *         "CaseType":"zx",
     *         "RelatedCompanies":[
     *             {
     *                 "KeyNo":"e6cdaddebb6de8420a2e7784b1df7fcf",
     *                 "Name":"惠州市腾飞盛世贸易有限公司"
     *             },
     *             {
     *                 "KeyNo":"fd9f01322888029ce2bbffc827505815",
     *                 "Name":"广州银行股份有限公司惠州分行"
     *             },
     *             {
     *                 "KeyNo":"f3599b7ea48c084ac5194cc7050d27d7",
     *                 "Name":"惠州市维也纳惠尔曼酒店管理有限公司"
     *             },
     *             {
     *                 "KeyNo":"fe94e25618158044867e970e0d18644f",
     *                 "Name":"惠州市正能实业发展有限公司"
     *             },
     *             {
     *                 "KeyNo":"e8b4f7b7e5aee43ffbb6887acdfa1da4",
     *                 "Name":"惠州市喜相逢实业有限公司"
     *             }
     *         ],
     *         "CaseReason":"金融借款合同纠纷",
     *         "Id":"d817013acaf3d9bb0c9c0e4a3533da1b0",
     *         "JudgeResult":"驳回案外人石耀先的异议请求。如不服本裁定，可以自本裁定送达之日起十五日内，向本院提起诉讼。审 判 长　唐荣平审 判 员　郑松荣审 判 员　李旭兵二〇一八年九月四日法官助理　张泽晖书 记 员　陈铁洪",
     *         "CourtNoticeList":{
     *             "TotalNum":0,
     *             "CourtNoticeInfo":[]
     *         }
     *     }
     * }
     * @apiUse QccError
     */
    private Object GetJudgementDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询裁判文书详情", params);
        if (object.size() == 0) {
            return object;
        }
        object.put("Appellor", JSON.parseArray(object.getString("Appellor")));
        object.put("DefendantList", JSON.parseArray(object.getString("DefendantList")));
        object.put("ProsecutorList", JSON.parseArray(object.getString("ProsecutorList")));
        //
        List<JSONObject> courtNotices = listQuery("qcc.查询裁判文书详情-开庭公告", newJsonObject("id", params.getString("id")));
        object.put("CourtNoticeList", newJsonObject(
            "TotalNum", courtNotices.size(),
            "CourtNoticeInfo", courtNotices
        ));
        List<JSONObject> companies = listQuery("qcc.查询裁判文书详情-关联公司", newJsonObject("id", params.getString("id")));
        object.put("RelatedCompanies", companies);
//        object.put("Content", new String(Base64.getDecoder().decode(object.getString("Content"))));
//        JSONObject log = DBService.config.getJSONObject("log");
        try{
//            Map ps = new HashMap();
//            ps.put("fid", params.getString("id") + "-caipan");
            String str = HttpUtil.get(String.format("http://%s/file?fid=%s-caipan", config.file, params.getString("id")));
            JSONObject obj = JSON.parseObject(str);
            if(!StrUtil.equals(obj.getString("Status"), "500")){
                object.putAll(obj);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

//        try(
//            FileInputStream fis = new FileInputStream(new File(log.getString("blob"), params.getString("id") + "-caipan"));
//            FileChannel channel = fis.getChannel();
//            ) {
//            String str = IoUtil.readUtf8(channel);
//            object.putAll(JSON.parseObject(str));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String contentClear = object.getString("ContentClear");
//        if (contentClear == null) {
//           contentClear = "";
//        }
        return object;
    }


    /**
     * @api {get} {企查查数据查询服务地址}/JudgeDocV4/SearchJudgmentDoc 裁判文书列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiParam {string} caseReason 案由，不传为查全部
     * @apiParam {string} submitDateStart 最小发布时间，格式为yyyy-MM-dd
     * @apiParam {string} submitDateEnd 最大发布时间，格式为yyyy-MM-dd
     *
     * @apiUse PageParam
     *
     * @apiSuccess {string} Id Id
     * @apiSuccess {string} Court 执行法院
     * @apiSuccess {string} CaseName 裁判文书名字
     * @apiSuccess {string} CaseNo 裁判文书编号
     * @apiSuccess {string} CaseType 裁判文书类型
     * @apiSuccess {string} SubmitDate 发布时间
     * @apiSuccess {string} UpdateDate 审判时间
     * @apiSuccess {string} IsProsecutor 是否原告（供参考）
     * @apiSuccess {string} IsDefendant 是否被告（供参考）
     * @apiSuccess {string} CourtYear 开庭时间年份
     * @apiSuccess {string} CaseRole 涉案人员角色
     * @apiSuccess {string} CourtLevel 法院级别，最高法院
     * @apiSuccess {string} CaseReason 案由
     * @apiSuccess {string} CaseReasonType 案由类型
     * @apiSuccess {string} CourtMonth 开庭时间月份
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 1,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "CourtMonth": "201812",
     *             "SubmitDate": "2018-12-27 12:00:00",
     *             "CaseName": "瑞幸咖啡(北京)有限公司与上海梓赫置业有限公司房屋租赁合同纠纷一审民事判决书",
     *             "IsDefendant": "false",
     *             "Court": "上海市浦东新区人民法院",
     *             "CaseNo": "（2018）沪0115民初60627号",
     *             "UpdateDate": "2019-01-06 07:50:13",
     *             "CourtYear": "2018",
     *             "IsProsecutor": "false",
     *             "CaseReasonType": "0019",
     *             "CaseType": "ms",
     *             "CaseRole": "[{\"P\":\"瑞幸咖啡(北京)有限公司\",\"R\":\"原告\"},{\"P\":\"上海梓赫置业有限公司\",\"R\":\"被告\"}]",
     *             "CaseReason": "房屋租赁合同纠纷",
     *             "Id": "73cb2065b6b986d442056ece96efafa20",
     *             "CourtLevel": "2"
     *         }
     *     ]
     * }
     * @apiUse QccError
     */
    private Object SearchJudgmentDoc(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询裁判文书列表", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/CourtV4/SearchZhiXing 被执行信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} Id Id
     * @apiSuccess {string} SourceId 官网系统ID（内部保留字段，一般为空）
     * @apiSuccess {string} Name 名称
     * @apiSuccess {string} LiAnDate 立案时间
     * @apiSuccess {string} AnNo 立案号
     * @apiSuccess {string} ExecuteGov 执行法院
     * @apiSuccess {string} BiaoDi 标地
     * @apiSuccess {string} Status 状态（内部保留字段，现已不使用）
     * @apiSuccess {string} PartyCardNum 身份证号码/组织机构代码
     * @apiSuccess {string} UpdateDate 数据更新时间（内部保留字段，现已不再更新时间）
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 10,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫0105执8021号",
     *             "LiAnDate": "2017-07-05 12:00:00",
     *             "Id": "35792ff96142cda1b079ae9f7f7b61fa1",
     *             "PartyCardNum": "796788541",
     *             "ExecuteGov": "郑州市金水区人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "2794488"
     *         },
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1628执798号",
     *             "LiAnDate": "2017-06-16 12:00:00",
     *             "Id": "478230a106a17e95c7c0785314802c371",
     *             "PartyCardNum": "79678854-1",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "236000"
     *         },
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1628执700号",
     *             "LiAnDate": "2017-06-08 12:00:00",
     *             "Id": "e23d3af6bb54cad3a63392f19d953be11",
     *             "PartyCardNum": "79678854-1",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "11034235"
     *         },
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1627执812号",
     *             "LiAnDate": "2017-05-16 12:00:00",
     *             "Id": "2d97ded6b7cccc4f0c2c5b03ee46bc381",
     *             "PartyCardNum": "79678854-1",
     *             "ExecuteGov": "太康县人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "1022300"
     *         },
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1602执1496号",
     *             "LiAnDate": "2017-05-03 12:00:00",
     *             "Id": "0f171a88f75ad73aada5542e8400c5e21",
     *             "PartyCardNum": "796788541",
     *             "ExecuteGov": "周口市川汇区人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "1.799E+7"
     *         },
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1628执517号",
     *             "LiAnDate": "2017-04-17 12:00:00",
     *             "Id": "b317ac796a0999dddf0930c1f9114fb61",
     *             "PartyCardNum": "79678854-1",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "1000000"
     *         },
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1628执502号",
     *             "LiAnDate": "2017-04-13 12:00:00",
     *             "Id": "ee8179791e9d6d271da732cff759e1df1",
     *             "PartyCardNum": "41160100006184",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "4286040"
     *         },
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1602执802号",
     *             "LiAnDate": "2017-03-03 12:00:00",
     *             "Id": "277d2b15e90f798970e60603dd903ce11",
     *             "PartyCardNum": "79678854-1",
     *             "ExecuteGov": "周口市川汇区人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "5000000"
     *         },
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1602执803号",
     *             "LiAnDate": "2017-03-03 12:00:00",
     *             "Id": "dce46e0648b52fbbc5ad9f62eba84ff61",
     *             "PartyCardNum": "79678854-1",
     *             "ExecuteGov": "周口市川汇区人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "6000000"
     *         },
     *         {
     *             "Status": "",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1628执恢33号",
     *             "LiAnDate": "2017-02-22 12:00:00",
     *             "Id": "8e8ff1fc6966bde81d4745e7be83957a1",
     *             "PartyCardNum": "79678854-1",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "BiaoDi": "0"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object SearchZhiXing(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询被执行信息", params);
    }


    /**
     * @api {get} {企查查数据查询服务地址}/CourtV4/SearchShiXin 失信信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
     * @apiSuccess {string} Id 主键
     * @apiSuccess {string} SourceId 官网主键（内部保留字段，一般为空）
     * @apiSuccess {string} UniqueNo 唯一编号（内部保留字段，一般为空）
     * @apiSuccess {string} Name 被执行人姓名/名称
     * @apiSuccess {string} LiAnDate 立案时间
     * @apiSuccess {string} AnNo 案号
     * @apiSuccess {string} OrgNo 身份证号码/组织机构代码
     * @apiSuccess {string} OwnerName 法定代表人或者负责人姓名
     * @apiSuccess {string} ExecuteGov 执行法院
     * @apiSuccess {string} Province 所在省份缩写
     * @apiSuccess {string} ExecuteUnite 做出执行依据单位
     * @apiSuccess {string} YiWu 生效法律文书确定的义务
     * @apiSuccess {string} ExecuteStatus 被执行人的履行情况
     * @apiSuccess {string} ActionRemark 失信被执行人行为具体情形
     * @apiSuccess {string} PublicDate 发布时间
     * @apiSuccess {string} Age 年龄
     * @apiSuccess {string} Sexy 性别
     * @apiSuccess {string} UpdateDate 数据更新时间（内部保留字段，现已不再更新时间）
     * @apiSuccess {string} ExecuteNo 执行依据文号
     * @apiSuccess {string} PerformedPart 已履行
     * @apiSuccess {string} UnperformPart 未履行
     * @apiSuccess {string} OrgType 组织类型（1：自然人，2：企业，3：社会组织，空白：无法判定）
     * @apiSuccess {string} OrgTypeName 组织类型名称
     *
     *
     * @apiSuccessExample 请求成功:
     * {
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 10,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "OwnerName": "秦亚",
     *             "YiWu": "236000",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "79678854-1",
     *             "Province": "河南",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1628执798号",
     *             "ExecuteUnite": "鹿邑县人民法院",
     *             "ActionRemark": "被执行人无正当理由拒不履行执行和解协议",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2017-07-02 12:00:00",
     *             "LiAnDate": "2017-06-16 12:00:00",
     *             "Id": "478230a106a17e95c7c0785314802c372",
     *             "ExecuteNo": "（2016）豫1628民初1162号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         },
     *         {
     *             "OwnerName": "秦亚",
     *             "YiWu": "11034234.66",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "79678854-1",
     *             "Province": "河南",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1628执700号",
     *             "ExecuteUnite": "鹿邑县人民法院",
     *             "ActionRemark": "有履行能力而拒不履行生效法律文书确定义务",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2017-07-02 12:00:00",
     *             "LiAnDate": "2017-06-08 12:00:00",
     *             "Id": "e23d3af6bb54cad3a63392f19d953be12",
     *             "ExecuteNo": "（2017）豫1628民初1125号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         },
     *         {
     *             "OwnerName": "秦亚",
     *             "YiWu": "详见判决书",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "796788541",
     *             "Province": "河南",
     *             "ExecuteGov": "周口市川汇区人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1602执1496号",
     *             "ExecuteUnite": "河南省周口市川汇区人民法院",
     *             "ActionRemark": "有履行能力而拒不履行生效法律文书确定义务的",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2017-05-22 12:00:00",
     *             "LiAnDate": "2017-05-03 12:00:00",
     *             "Id": "0f171a88f75ad73aada5542e8400c5e22",
     *             "ExecuteNo": "（2017）豫1602民初70号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         },
     *         {
     *             "OwnerName": "秦亚",
     *             "YiWu": "1000000",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "79678854-1",
     *             "Province": "河南",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1628执517号",
     *             "ExecuteUnite": "鹿邑县人民法院",
     *             "ActionRemark": "有履行能力而拒不履行生效法律文书确定义务",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2017-07-02 12:00:00",
     *             "LiAnDate": "2017-04-17 12:00:00",
     *             "Id": "b317ac796a0999dddf0930c1f9114fb62",
     *             "ExecuteNo": "（2016）豫1628民初2572号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         },
     *         {
     *             "OwnerName": "秦亚",
     *             "YiWu": "详见判决书",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "79678854-1",
     *             "Province": "河南",
     *             "ExecuteGov": "周口市川汇区人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1602执802号",
     *             "ExecuteUnite": "周口市中级人民法院",
     *             "ActionRemark": "其他有履行能力而拒不履行生效法律文书确定义务的",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2017-03-17 12:00:00",
     *             "LiAnDate": "2017-03-03 12:00:00",
     *             "Id": "277d2b15e90f798970e60603dd903ce12",
     *             "ExecuteNo": "（2015）周民初字第82号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         },
     *         {
     *             "OwnerName": "秦亚",
     *             "YiWu": "详见判决书",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "79678854-1",
     *             "Province": "河南",
     *             "ExecuteGov": "周口市川汇区人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1602执803号",
     *             "ExecuteUnite": "周口市中级人民法院",
     *             "ActionRemark": "其他有履行能力而拒不履行生效法律文书确定义务的",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2017-04-18 12:00:00",
     *             "LiAnDate": "2017-03-03 12:00:00",
     *             "Id": "dce46e0648b52fbbc5ad9f62eba84ff62",
     *             "ExecuteNo": "（2015）周民初字第82号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         },
     *         {
     *             "OwnerName": "秦亚",
     *             "YiWu": "详见民事判决书",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "79678854-1",
     *             "Province": "河南",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2017)豫1628执恢33号",
     *             "ExecuteUnite": "鹿邑县人民法院",
     *             "ActionRemark": "其他有履行能力而拒不履行生效法律文书确定义务的",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2017-02-22 12:00:00",
     *             "LiAnDate": "2017-02-22 12:00:00",
     *             "Id": "8e8ff1fc6966bde81d4745e7be83957a2",
     *             "ExecuteNo": "（2016）豫1628民初1161号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         },
     *         {
     *             "OwnerName": "无",
     *             "YiWu": "",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "79678854－1",
     *             "Province": "河南",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2016)豫1628执682号",
     *             "ExecuteUnite": "鹿邑县人民法院",
     *             "ActionRemark": "其它规避执行",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2016-09-29 12:00:00",
     *             "LiAnDate": "2016-08-16 12:00:00",
     *             "Id": "b1b8effa78373af62ce348193f2dede22",
     *             "ExecuteNo": "（2016）豫1628民初1386号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         },
     *         {
     *             "OwnerName": "无",
     *             "YiWu": "",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "79678854-1",
     *             "Province": "河南",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2016)豫1628执640号",
     *             "ExecuteUnite": "鹿邑县人民法院",
     *             "ActionRemark": "其他有履行能力而拒不履行生效法律文书确定义务",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2016-08-09 12:00:00",
     *             "LiAnDate": "2016-08-01 12:00:00",
     *             "Id": "31a81bb2643f7f4e65a9ea41f2a60bf42",
     *             "ExecuteNo": "（2016）豫1628民初1161号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         },
     *         {
     *             "OwnerName": "无",
     *             "YiWu": "23.6万 ",
     *             "SourceId": "0",
     *             "ExecuteStatus": "全部未履行",
     *             "UnperformPart": "",
     *             "OrgNo": "79678854-1",
     *             "Province": "河南",
     *             "ExecuteGov": "鹿邑县人民法院",
     *             "PerformedPart": "",
     *             "Name": "河南亚华安全玻璃有限公司",
     *             "UpdateDate": "0001-01-01T00:00:00",
     *             "AnNo": "(2016)豫1628执624号",
     *             "ExecuteUnite": "鹿邑县人民法院",
     *             "ActionRemark": "其他有履行能力而拒不履行生效法律文书确定义务",
     *             "Sexy": "",
     *             "OrgType": "2",
     *             "PublicDate": "2016-07-14 12:00:00",
     *             "LiAnDate": "2016-07-12 12:00:00",
     *             "Id": "3c95eccd2708d62ec56174d3e2f4040d2",
     *             "ExecuteNo": "（2016）豫1628民初1162号",
     *             "OrgTypeName": "失信企业",
     *             "Age": "0",
     *             "UniqueNo": ""
     *         }
     *     ]
     * }
     *
     *
     * @apiUse QccError
     */
    private Object SearchShiXin(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询失信信息", params);
    }


    public static void registerRoute(String url, IQccRoute route) {
        HttpServerHandler.AddRoute(new Route(S.fmt("%s%s", qccPrefix, url), (ctx, req) -> {
            Object result = route.call(ctx, req, HttpServerHandler.decodeQuery(req));
            JSONObject realResult = newJsonObject(
                "Status", "200",
                "Message", "查询成功"
            );
            if (result == null) {
                realResult.put("Status", "500");
                return realResult;
            }
            //分页结构
//            if (result instanceof PageQuery) {
//                result = newJsonObject(
//                    "totalRow", ((PageQuery) result).getTotalRow(),
//                    "pageSize", ((PageQuery) result).getPageSize(),
//                    "pageIndex", ((PageQuery) result).getPageNumber(),
//                    "list", ((PageQuery) result).getList()
//                );
//            }
            if (result instanceof JSONObject) {
                JSONObject resultObj = (JSONObject) result;
                if (resultObj.containsKey("totalRow")) {
                    if (resultObj.getInteger("totalRow") == 0){
                        realResult.put("Status", "201");
                    }
                    realResult.put("Paging", newJsonObject(
                        "PageSize", resultObj.getInteger("pageSize"),
                        "PageIndex", resultObj.getInteger("pageNumber"),
                        "TotalRecords", resultObj.getInteger("totalRow")
                    ));
                    if (resultObj.containsKey("list")) {
                        realResult.put("Result", resultObj.get("list"));
                    }
                    if (resultObj.containsKey("Result")) {
                        realResult.put("Result", resultObj.get("Result"));
                    }
                } else {
                    if(resultObj.size() == 0){
                        realResult.put("Status", "201");
                    } else {
                        realResult.put("Result", resultObj);
                    }
                }

            } else {
                if(result instanceof JSONArray && ((JSONArray) result).size() == 0){
                    realResult.put("Status", "201");
                }
                realResult.put("Result", result);
            }
            return realResult;
        }));
    }

    public JSONObject singleQuery(String sqlId, JSONObject params) {
        JSONObject map = null;
        if (sqlId.contains(".")) {
            map = sqlManager.selectSingle(sqlId, params, JSONObject.class);
        } else {
            List<JSONObject> list = sqlManager.execute(sqlId, JSONObject.class, params);
            if (list.size() > 0) {
                map = list.get(0);
            }
        }
        if (map == null) {
            return new JSONObject();
        }
        return map;
    }


    public List<JSONObject> listQuery(String sqlId, Map<String, Object> params) {
        List<JSONObject> list;
        if (sqlId.contains("qcc.")) {
            list = (sqlManager.select(sqlId, JSONObject.class, params));
        } else {
            list = (sqlManager.execute(sqlId, JSONObject.class, params));
        }
        return list;
    }

    public JSONObject pageQuery(String sqlId, JSONObject params) {
        PageQuery<JSONObject> pageQuery = new PageQuery<>();
        int page = 1;
        int size = 10;
        try {
            page = params.getIntValue("pageIndex");
            size = params.getIntValue("pageSize");
        } finally {
            if(page < 1){
                page = 1;
            }
            if(size < 1){
                size = 10;
            }
            pageQuery.setPageSize(size);
            pageQuery.setPageNumber(page);
        }
        pageQuery.setParas(params);
        sqlManager.pageQuery(sqlId, JSONObject.class, pageQuery);
//        return newJsonObject(
//            "pageNumber", pageQuery.getPageNumber(),
//            "pageSize", pageQuery.getPageSize(),
//            "totalRow", pageQuery.getTotalRow(),
//            "totalPage", pageQuery.getTotalPage(),
//            "list", JSON.toJSON(pageQuery.getList())
//        );
        return (JSONObject) JSON.toJSON(pageQuery);
    }


    public static String firstLetterUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static JSONObject convertToTree(List<JSONObject> array){
        JSONObject map = new JSONObject();
        JSONObject main = null;
        for (JSONObject object : array) {
            JSONArray children = new JSONArray();
            map.put(object.getString("InnerId"), children);
            object.put("Children", children);
        }
        for (JSONObject object : array) {
            String pid = (String) object.getOrDefault("InnerParentId","");
            if(pid == null){
                main = object;
            } else {
                map.getJSONArray(pid).add(object);
            }
        }
        for (JSONObject object : array) {
            object.remove("InnerId");
            object.remove("InnerParentId");
        }
        if (main == null) {
            main = new JSONObject();
        }
        return main;
    }

    public static void convertToQccStyle(Map<String, Object> json) {
        Map map = new HashMap();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            String s = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Date) {
                map.put(firstLetterUpper(s), sdf.format(value));
            } else {
                map.put(firstLetterUpper(s), value);
            }
        }
        json.clear();
        json.putAll(map);
    }

    public static void convertToQccStyle(Collection json) {
        for (Object object : json) {
            if (object instanceof Map) {
                convertToQccStyle((Map) object);
            }
        }
    }

    public interface IQccRoute {
        Object call(ChannelHandlerContext ctx, FullHttpRequest request, JSONObject params);
    }

}
