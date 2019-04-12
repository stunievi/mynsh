package com.beeasy.zed;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.beeasy.zed.Utils.*;

public class QccService {

    private static SQLManager sqlManager;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static String qccPrefix = "/qcc";

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

    /**
     * @apiDefine QccError
     *
     * @apiErrorExample 请求异常:
     * {
     *     "Status": "500",
     *     "Message": "错误请求"
     * }
     */

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
        registerRoute("/EnvPunishment/GetEnvPunishmentList", service::GetEnvPunishmentList);
        registerRoute("/EnvPunishment/GetEnvPunishmentDetails", service::GetEnvPunishmentDetails);
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

    }


    /**
     * @api {get} /ECIV4/SearchFresh 新增公司列表
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
     * @api {get} /History/GetHistorytAdminLicens 历史行政许可
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
     * @api {get} /History/GetHistorytAdminPenalty 历史行政处罚
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
     * @api {get} /History/GetHistorytPledge 历史股权出质
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
     * @api {get} /History/GetHistorytMPledge 历史动产抵押
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
     * @api {get} /History/GetHistorytSessionNotice 历史开庭公告
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
     * @api {get} /History/GetHistorytJudgement 历史裁判文书
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
     * @api {get} /History/GetHistorytCourtNotice 历史法院公告
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
     * @api {get} /History/GetHistoryZhiXing 历史被执行
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
     * @api {get} /History/GetHistoryShiXin 历史失信查询
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
     * @api {get} /History/GetHistorytShareHolder 历史股东
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
     * @api {get} /History/GetHistorytInvestment 历史对外投资
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} fullName 公司全名
     * @apiUse PageParam
     *
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
        return pageQuery("qcc.查询历史对外投资信息表", params);
    }


    /**
     * @api {get} /History/GetHistorytEci 历史工商信息
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
     * @apiSuccess {object[]} OperList 历史名称
     * @apiSuccess {string} OperList.ChangeDate 变更日期
     * @apiSuccess {string} OperList.OperName 公司名称
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
        JSON hisData = JSONUtil.parse(object.getStr("HisData"));
        object.remove("HisData");
        object.putAll((Map<? extends String, ? extends Object>) hisData);
        return object;
    }


    /**
     * @api {get} /ECIV4/GetDetailsByName 企业关键字精确获取详细信息(Master)
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyword 公司全名
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
        ContactInfo.put("WebSite", JSONUtil.parse(ContactInfo.getStr("WebSite")));
        object.put("ContactInfo", ContactInfo);
        JSONObject Industry = singleQuery("qcc.查询工商信息行业信息表", params);
        object.put("Industry", Industry);
        return object;
    }


    /**
     * @api {get} /ChattelMortgage/GetChattelMortgage 动产抵押信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyWord 公司全名
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
        JSONArray PledgeeList = listQuery("qcc.查询动产抵押PledgeeList", params);
        JSONArray GuaranteeList = listQuery("qcc.查询动产抵押GuaranteeList", params);
        JSONArray ChangeList = listQuery("qcc.查询动产抵押ChangeList", params);
        JSONArray list = listQuery("qcc.查询动产抵押", params);
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
                        if (i.getStr("CmId", "##").equals(object.getStr("InnerId", "$$"))) {
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
                        if (i.getStr("CmId", "##").equals(object.getStr("InnerId", "$$"))) {
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
                        if (i.getStr("CmId", "##").equals(object.getStr("InnerId", "$$"))) {
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
     * @api {get} /EnvPunishment/GetEnvPunishmentDetails 环保处罚详情
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
     * @api {get} /EnvPunishment/GetEnvPunishmentList 环保处罚列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyWord 公司全名
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
     * @api {get} /LandMortgage/GetLandMortgageDetails 土地抵押详情
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
     * @api {get} /LandMortgage/GetLandMortgageList 土地抵押列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyWord 公司全名
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
     * @api {get} /JudicialSale/GetJudicialSaleDetail 司法拍卖详情
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
        return singleQuery("qcc.查询司法拍卖详情", params);
    }


    /**
     * @api {get} /JudicialSale/GetJudicialSaleList 司法拍卖列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyWord 公司全名
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
     * @api {get} /ECIException/GetOpException 企业经营异常
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyNo 公司全名
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
     * @api {get} /JudicialAssistance/GetJudicialAssistance 司法协助信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} keyWord 公司全名
     *
     * @apiSuccess {string} ExecutedBy 被执行人
     * @apiSuccess {string} EquityAmount 股权数额
     * @apiSuccess {string} EnforcementCourt 执行法院
     * @apiSuccess {string} ExecutionNoticeNum 执行通知书文号
     * @apiSuccess {string} Status 类型
     *
     * @apiSuccess {object} EquityFreezeDetail 股权冻结情况
     * @apiSuccess {string} EquityFreezeDetail.CompanyName 相关企业名称
     * @apiSuccess {string} EEquityFreezeDetail.ExecutionMatters 执行事项
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
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": [
     *         {
     *             "Status": "股权冻结|冻结",
     *             "EnforcementCourt": "北京市第二中级人民法院",
     *             "EquityUnFreezeDetail": {
     *                 "CompanyName": "小米",
     *                 "FreezeTerm": "1095",
     *                 "ExecutionVerdictNum": "（2017）京02民初58号",
     *                 "ExecutedPersonDocNum": "",
     *                 "FreezeStartDate": "2017-09-13 12:00:00",
     *                 "ExecutionMatters": "轮候冻结股权、其他投资权益",
     *                 "FreezeEndDate": "2020-09-12 12:00:00",
     *                 "ExecutedPersonDocType": "居民身份证"
     *             },
     *             "EquityAmount": "15000万人民币元",
     *             "ExecutionNoticeNum": "（2017）京02民初58号",
     *             "ExecutedBy": "霍庆华"
     *         },
     *         {
     *             "Status": "股权冻结|冻结",
     *             "EnforcementCourt": "新疆维吾尔自治区高级人民法院",
     *             "EquityUnFreezeDetail": {
     *                 "CompanyName": "小米",
     *                 "FreezeTerm": "1095",
     *                 "ExecutionVerdictNum": "(2017)新执47号",
     *                 "ExecutedPersonDocNum": "110105011796483",
     *                 "FreezeStartDate": "2017-08-16 12:00:00",
     *                 "ExecutionMatters": "轮候冻结股权、其他投资权益",
     *                 "FreezeEndDate": "2020-08-16 12:00:00",
     *                 "ExecutedPersonDocType": ""
     *             },
     *             "EquityAmount": "254984.5万人民币元",
     *             "ExecutionNoticeNum": "(2017)新执47号",
     *             "ExecutedBy": "中国庆华能源集团有限公司"
     *         },
     *         {
     *             "Status": "股权冻结|冻结",
     *             "EnforcementCourt": "杭州市西湖区人民法院",
     *             "EquityUnFreezeDetail": {
     *                 "CompanyName": "小米",
     *                 "FreezeTerm": "1095",
     *                 "ExecutionVerdictNum": "(2017)浙0106民初6913号",
     *                 "ExecutedPersonDocNum": "",
     *                 "FreezeStartDate": "2017-08-29 12:00:00",
     *                 "ExecutionMatters": "公示冻结股权、其他投资权益",
     *                 "FreezeEndDate": "2020-08-28 12:00:00",
     *                 "ExecutedPersonDocType": "居民身份证"
     *             },
     *             "EquityAmount": "15000万人民币元",
     *             "ExecutionNoticeNum": "(2017)浙0106民初6913号",
     *             "ExecutedBy": "霍庆华"
     *         },
     *         {
     *             "Status": "股权冻结|冻结",
     *             "EnforcementCourt": "北京市第二中级人民法院",
     *             "EquityUnFreezeDetail": {
     *                 "CompanyName": "小米",
     *                 "FreezeTerm": "1095",
     *                 "ExecutionVerdictNum": "(2017)京02民初58号",
     *                 "ExecutedPersonDocNum": "110105011796483",
     *                 "FreezeStartDate": "2017-09-13 12:00:00",
     *                 "ExecutionMatters": "轮候冻结股权、其他投资权益",
     *                 "FreezeEndDate": "2020-09-12 12:00:00",
     *                 "ExecutedPersonDocType": ""
     *             },
     *             "EquityAmount": "254984.5万人民币元",
     *             "ExecutionNoticeNum": "(2017)京02民初58号",
     *             "ExecutedBy": "中国庆华能源集团有限公司"
     *         },
     *         {
     *             "Status": "股权冻结|冻结",
     *             "ExecutionNoticeNum": "（2016）粤01执3073号",
     *             "EnforcementCourt": "广东省广州市中级人民法院",
     *             "JudicialPartnersChangeDetail": {
     *                 "ExecutionVerdictNum": "（2016）粤01执3073号",
     *                 "ExecutedPersonDocNum": "110105011796483",
     *                 "ExecutionMatters": "续行冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType": "居民身份证"
     *             },
     *             "EquityAmount": "15000万人民币元",
     *             "ExecutedBy": "霍庆华"
     *         },
     *         {
     *             "Status": "股权冻结|冻结",
     *             "ExecutionNoticeNum": "(2017)浙0106民初6913号",
     *             "EnforcementCourt": "杭州市西湖区人民法院",
     *             "JudicialPartnersChangeDetail": {
     *                 "ExecutionVerdictNum": "(2017)浙0106民初6913号",
     *                 "ExecutedPersonDocNum": "110105011796483",
     *                 "ExecutionMatters": "轮候冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType": ""
     *             },
     *             "EquityAmount": "254984.5万人民币元",
     *             "ExecutedBy": "中国庆华能源集团有限公司"
     *         },
     *         {
     *             "Status": "股权冻结|冻结",
     *             "ExecutionNoticeNum": "(2016)粤01执3073号",
     *             "EnforcementCourt": "广东省广州市中级人民法院",
     *             "JudicialPartnersChangeDetail": {
     *                 "ExecutionVerdictNum": "(2016)粤01执3073号",
     *                 "ExecutedPersonDocNum": "110105011796483",
     *                 "ExecutionMatters": "公示冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType": ""
     *             },
     *             "EquityAmount": "254984.5万人民币元",
     *             "ExecutedBy": "中国庆华能源集团有限公司"
     *         },
     *         {
     *             "Status": "股权冻结|冻结",
     *             "ExecutionNoticeNum": "（2017）浙0106民初702、703号",
     *             "EnforcementCourt": "杭州市西湖区人民法院",
     *             "JudicialPartnersChangeDetail": {
     *                 "ExecutionVerdictNum": "（2017）浙0106民初702、703号",
     *                 "ExecutedPersonDocNum": "110105011796483",
     *                 "ExecutionMatters": "轮候冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType": ""
     *             },
     *             "EquityAmount": "254984.5万人民币元",
     *             "ExecutedBy": "中国庆华能源集团有限公司"
     *         },
     *         {
     *             "Status": "股权冻结|冻结",
     *             "ExecutionNoticeNum": "（2016）粤01执3073号",
     *             "EnforcementCourt": "广东省广州市中级人民法院",
     *             "JudicialPartnersChangeDetail": {
     *                 "ExecutionVerdictNum": "（2016）粤01执3073号",
     *                 "ExecutedPersonDocNum": "110105011796483",
     *                 "ExecutionMatters": "轮候冻结股权、其他投资权益",
     *                 "ExecutedPersonDocType": ""
     *             },
     *             "EquityAmount": "254984.5万人民币元",
     *             "ExecutedBy": "中国庆华能源集团有限公司"
     *         }
     *     ]
     * }
     *
     * @apiUse QccError
     */
    private Object GetJudicialAssistance(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONArray list = listQuery("qcc.查询司法协助信息", params);
        for (Object o : list) {
            JSONObject object = (JSONObject) o;
            JSONObject[] objects = {new JSONObject(), new JSONObject(), new JSONObject()};
            Iterator<Map.Entry<String, Object>> it = object.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                for (short i = 0; i < objects.length; i++) {
                    if (entry.getKey().startsWith("D" + i)) {
                        objects[i].put(entry.getKey().replace("D" + i, ""), entry.getValue());
                        it.remove();
                        break;
                    }
                }
            }
            for (int i = 0; i < objects.length; i++) {
                if (objects[i].size() == 0) {
                    objects[i] = null;
                }
            }
            object.put("EquityFreezeDetail", objects[0]);
            object.put("EquityUnFreezeDetail", objects[1]);
            object.put("JudicialPartnersChangeDetail", objects[2]);
        }

        return list;
    }


    /**
     * @api {get} /CourtAnnoV4/GetCourtNoticeInfo 开庭公告详情
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
     * @api {get} /CourtAnnoV4/SearchCourtNotice 开庭公告列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} searchKey 公司全名
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
     * @api {get} /CourtNoticeV4/SearchCourtAnnouncementDetail 法院公告详情
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
     * @api {get} /CourtNoticeV4/SearchCourtAnnouncement 法院公告列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} companyName 公司全名
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
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Paging": {
     *         "PageSize": 10,
     *         "TotalRecords": 20,
     *         "PageIndex": 1
     *     },
     *     "Result": [
     *         {
     *             "PublishedPage": "G41",
     *             "UploadDate": "2019-03-29 12:00:00",
     *             "Category": "裁判文书",
     *             "Party": "吉林省安华通讯科技发展有限公司",
     *             "Content": "吉林省安华通讯科技发展有限公司：本院受理小米科技有限责任公司诉你侵害商标权纠纷一案，已审理终结，现依法向你公告送达（2017）吉01民初770号民事判决书。自公告之日起60日内来本院领取民事判决书，逾期即视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于吉林省高级人民法院。逾期本判决即发生法律效力。",
     *             "Id": "52C02354F9069DD6CF28F1EB3C398EFE",
     *             "Court": "吉林省长春市中级人民法院"
     *         },
     *         {
     *             "PublishedPage": "G47",
     *             "UploadDate": "2019-03-28 12:00:00",
     *             "Category": "起诉状副本及开庭传票",
     *             "Party": "彭育洲",
     *             "Content": "彭育洲:本院受理原告小米科技有限责任公司与被告彭育洲、浙江淘宝网络有限公司侵害商标权纠纷一案，现依法向你送达起诉状副本、应诉通知书、举证通知书、合议庭组成人员通知书及开庭传票等。自本公告发出之日起，经过60日视为送达，提出答辩状和举证期限均为公告送达期满后的15日内。并定于2019年6月24日上午9时于本院二十...",
     *             "Id": "B3401AFD0F71D64E8589B4039EF8F9EB",
     *             "Court": "杭州市余杭区人民法院"
     *         },
     *         {
     *             "PublishedPage": "G15",
     *             "UploadDate": "2019-03-22 12:00:00",
     *             "Category": "裁判文书",
     *             "Party": "陈銮卿",
     *             "Content": "陈銮卿（公民身份号码440524196608184264）：本院受理原告小米科技有限责任公司诉你侵害商标权纠纷一案，现依法向你公告送达(2018)粤05民初20号民事判决。本院判决你应立即停止销售侵犯8911270号商标的商品，销毁库存侵权商品，在判决生效之日起10日内支付赔偿金2.5万元。自公告发出之日起经过60日即视为送达。...",
     *             "Id": "058D2A58AC141970DFD05935F43DD6E9",
     *             "Court": "广东省汕头市中级人民法院"
     *         },
     *         {
     *             "PublishedPage": "G91",
     *             "UploadDate": "2019-03-21 12:00:00",
     *             "Category": "起诉状副本及开庭传票",
     *             "Party": "赖木辉",
     *             "Content": "赖木辉：本院受理的原告小米科技有限责任公司与被告赖木辉、浙江淘宝网络有限公司侵害商标权纠纷一案，现依法向你公告送达起诉状副本、应诉通知书、举证通知书、开庭传票等。自本公告发出之日起，经过60日即视为送达，提出答辩状和举证期限均为公告送达期满后的15日内。并定于2019年6月26日下午14时30分在本院第二十三审判...",
     *             "Id": "008F9CFA6E0C42DDE50D759B6275F77C",
     *             "Court": "杭州市余杭区人民法院"
     *         },
     *         {
     *             "PublishedPage": "G68",
     *             "UploadDate": "2019-03-20 12:00:00",
     *             "Category": "裁判文书",
     *             "Party": "牟平区栋财手机店",
     *             "Content": "牟平区栋财手机店：本院受理原告小米科技有限责任公司诉被告牟平区栋财手机店侵害商标权纠纷一案，现依法向你方公告送达（2017）鲁06民初134号民事判决书，自公告之日起满60日，即视为送达。如不服本判决，可自送达之日起15日内向本院递交上诉状，上诉于山东省高级人民法院。逾期本判决即发生法律效力。...",
     *             "Id": "AACB2C6C9ADBE303D98BA65AFB0081BF",
     *             "Court": "山东省烟台市中级人民法院"
     *         },
     *         {
     *             "PublishedPage": "G14G15中缝",
     *             "UploadDate": "2019-03-19 12:00:00",
     *             "Category": "起诉状副本及开庭传票",
     *             "Party": "北京华美通联通讯器材销售中心",
     *             "Content": "北京市东城区人民法院公告北京华美通联通讯器材销售中心：本院受理原告小米科技有限责任公司诉北京华美通联通讯器材销售中心侵害商标权纠纷一案，现依法向你公司公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告送达期满后次日起15日和30日内。...",
     *             "Id": "E690D42D47091DADDB6E4BD541513BB1",
     *             "Court": "北京市东城区人民法院"
     *         },
     *         {
     *             "PublishedPage": "G26",
     *             "UploadDate": "2019-03-14 12:00:00",
     *             "Category": "起诉状副本及开庭传票",
     *             "Party": "龚坤宏",
     *             "Content": "龚坤宏：本院受理小米科技有限责任公司诉龚坤宏侵害商标权纠纷一案，现依法向你龚坤宏公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。龚坤宏自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。并定于举证期满后7月1日下午16时00(遇法定假日顺延)在本院上海市徐汇区龙漕路...",
     *             "Id": "3667F54FDF5BB15AA8192C3FC6DDD0CE",
     *             "Court": "上海市徐汇区人民法院"
     *         },
     *         {
     *             "PublishedPage": "G26",
     *             "UploadDate": "2019-03-14 12:00:00",
     *             "Category": "起诉状副本及开庭传票",
     *             "Party": "项春燕",
     *             "Content": "项春燕：本院受理小米科技有限责任公司诉项春燕侵害商标权纠纷一案，现依法向你项春燕公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。项春燕自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。并定于举证期满后7月1日下午14时40分(遇法定假日顺延)在本院上海市徐汇区龙漕路...",
     *             "Id": "3667F54FDF5BB15A26D2ADF663C0C649",
     *             "Court": "上海市徐汇区人民法院"
     *         },
     *         {
     *             "PublishedPage": "G18G19中缝",
     *             "UploadDate": "2019-03-14 12:00:00",
     *             "Category": "起诉状副本及开庭传票",
     *             "Party": "张少鸿",
     *             "Content": "张少鸿:本院受理原告小米科技有限责任公司诉你方侵害商标权纠纷一案，原告诉请本院判令:1.被告立即停止(2016)冀石国证字第9081号公证书公证的侵犯原告第8911270号商标专用权的行为，即停止销售和许诺销售侵权产品;2.被告赔偿原告经济损失及制止侵权所支付的费用合计五万元;3.被告承担本案诉讼费、公告费、保全费等全部费用...",
     *             "Id": "6620FD7E3529C46E51DFABE3544CD9B9",
     *             "Court": "深圳市龙岗区人民法院"
     *         },
     *         {
     *             "PublishedPage": "G26",
     *             "UploadDate": "2019-03-14 12:00:00",
     *             "Category": "起诉状副本及开庭传票",
     *             "Party": "周远沐",
     *             "Content": "周远沐：本院受理小米科技有限责任公司诉周远沐侵害商标权纠纷一案，现依法向你周远沐公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。周远沐自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。并定于举证期满后7月1日下午15时00分(遇法定假日顺延)在本院上海市徐汇区龙漕路...",
     *             "Id": "CF993D8969C131444897BA2A16FB3A24",
     *             "Court": "上海市徐汇区人民法院"
     *         }
     *     ]
     * }
     * @apiUse QccError
     */
    private Object SearchCourtAnnouncement(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return pageQuery("qcc.查询法院公告列表", params);
    }


    /**
     * @api {get} /JudgeDocV4/GetJudgementDetail 裁判文书详情
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
     *     "Status": "200",
     *     "Message": "查询成功",
     *     "Result": {
     *         "CollegiateBench": "\n审判员何绍辉\n",
     *         "SubmitDate": "2018-12-27 12:00:00",
     *         "ProsecutorList": [
     *             "瑞幸咖啡（北京）有限公司"
     *         ],
     *         "AppelleeArguing": null,
     *         "CreateDate": "2019-01-06 07:50:13",
     *         "Court": "上海市浦东新区人民法院",
     *         "CourtConsider": "\n本院认为，原、被告就系争房屋签订的房屋租赁合同系双方当事人真实意思表示，内容不违反法律规定，合法有效。合同签订后，原告依约向被告指定账户支付了租金、押金、进场费计47,500元，但被告未按约定向原告交付租赁物，违反了合同约定，原告行使合同解除权，符合合同约定，本院予以准许。合同解除后，尚未履行的，终止履行；已经履行的，根据履行情况和合同性质，当事人可以要求恢复原状、采取其他补救措施，并有权要求赔偿损失。原告未实际使用系争房屋，合同解除后，被告应将其收取原告的47,500元返还原告。原告举证的付款金额为8,400元的收条，该收条系案外人签某，对于收款人的身份，原告未进一步举证证实收款人与被告之间的关系或者该收款人有权代被告收款的相应证据。且原告向案外其他人的付款行为，亦与被告出具的转账委托书内容不符。故仅凭案外人出具的收条，不足以证明原告向被告付款8,400元的事实。原告主张被告返还该笔款项，本院难以支持。由于被告违约致使涉案租赁合同解除，原告要求被告按照合同约定承担解除合同的违约金5万元，于法有据，本院予以支持。被告经本院合法传唤，无正当理由未到庭，放弃了对原告提交证据的质证权利，由此造成的不利后果由其自行承担。\n综上，依照《中华人民共和国合同法》第六十条、第九十三条第二款、第九十七条，《中华人民共和国民事诉讼法》第一百四十四条之规定，判决如下：\n",
     *         "CaseNo": "（2018）沪0115民初60627号",
     *         "PlaintiffRequestOfFirst": null,
     *         "UpdateDate": "2019-01-06 07:50:13",
     *         "CourtInspectOfFirst": null,
     *         "TrialRound": "一审",
     *         "DefendantReply": "\n上海梓赫置业有限公司未作答辩。\n",
     *         "JudgeDate": "2018-10-24 12:00:00",
     *         "AppellantRequest": null,
     *         "DefendantList": [
     *             "上海梓赫置业有限公司"
     *         ],
     *         "IsValid": "true",
     *         "Appellor": [
     *             "上海梓赫置业有限公司",
     *             "瑞幸咖啡（北京）有限公司",
     *             "瑞幸咖啡(北京)有限公司"
     *         ],
     *         "ContentClear": "XXXXXXXXXX",
     *         "CaseName": "瑞幸咖啡(北京)有限公司与上海梓赫置业有限公司房屋租赁合同纠纷一审民事判决书",
     *         "JudegeDate": "\n二零一八年十月二十四日\n",
     *         "DefendantReplyOfFirst": null,
     *         "Recorder": "\n书记员陈韫鏐\n",
     *         "PartyInfo": "\n原告：瑞幸咖啡(北京)有限公司，住所地北京市。\n法定代表人：钱治亚，执行董事。\n委托诉讼代理人：刘超。\n被告：上海梓赫置业有限公司，住所地上海市浦东新区。\n法定代表人：陈鸣，执行董事。\n",
     *         "ExecuteProcess": null,
     *         "TrialProcedure": "\n原告瑞幸咖啡(北京)有限公司与被告上海梓赫置业有限公司房屋租赁合同纠纷一案，本院于2018年8月8日立案受理后，依法适用简易程序，公开开庭进行了审理。原告瑞幸咖啡(北京)有限公司的委托诉讼代理人刘超到庭参加诉讼，被告上海梓赫置业有限公司经本院传票传唤，无正当理由拒不到庭参加诉讼。本院依法缺席审理。本案现已审理终结。\n",
     *         "CaseType": "ms",
     *         "CourtConsiderOfFirst": null,
     *         "Content": "XXXXXXXXX",
     *         "PlaintiffRequest": "\n瑞幸咖啡(北京)有限公司向本院提出诉讼请求1、判令解除双方的《房屋租赁合同》；2、判令被告向原告返还55,900元(包括房租25,500元、押金17,000元、进场费5,000元、装修押金7,000元、装修管理费1,400元)；3、判令被告向原告支付违约金50,000元。事实和理由：2017年11月30日，原、被告签订《房屋租赁合同》，约定被告将上海市浦东新区东明路2600、2608、2612号晶华公馆地下二层A21编号B2-A21的商铺(以下简称系争房屋)租赁给被告经营咖啡及轻餐饮使用。租赁合同签订后，原告按约向被告支付了租金、押金等各款项，但被告迟迟未将系争房屋交付给原告。直至原告发现经营场所贴出告知书，原告才得知由于被告一直拖欠其上家的租金导致被案外人解除与被告的租赁合同。被告无法向原告交付系争房屋的违约行为，导致双方合同目的无法实现。综上，为了维护原告的合法权益，诉至法院。\n",
     *         "CourtInspect": "\n本院经审理认定事实如下：2017年11月30日，被告(出租代理方、甲方)与原告(承租方、乙方)签订《房屋租赁合同》一份，合同约定，甲方代理出租给乙方的商户位于上海市浦东新区东明路2600、2608、2612号晶华公馆地下二层A21，商铺编号B2-A21,乙方承租的商铺使用面积为14平方米。本房屋的实际租赁区域平面图见本合同附件一。乙方承租租赁房屋主营产品为现场制售咖啡及轻餐饮，并遵守国家和本市有关房屋使用和物业管理的规定。甲方同意于2017年12月1日前将本房屋交付乙方。乙方租赁本房屋的租赁期限为2年，自2017年12月15日至2019年12月14日止。租赁房屋的起租日为2017年12月15日。甲、乙双方约定，本房屋每合同年的租金为102,000元。本房屋先付租金后使用。乙方同意在本合同签订之日起3天内向甲方支付第一季度租金25,500元，以后乙方最晚应于下季度开始前的15日向甲方支付下一季度的租金。甲、乙双方约定，甲方交付租赁房屋前，乙方应向甲方支付押金，押金金额相当于2个月的租金，即17,000元。甲、乙双方同意，有下列情形之一的，一方可书面通知另一方解除本合同。违反合同的一方，应向另一方支付50,000元的违约金；给对方造成损失的，若支付的违约金不足抵付一方损失，还应赔偿造成损失与违约金的差额部分：(一)甲方延期交付租赁房屋超过30日，经宽限期后仍未交付的；……。合同另对其他事项作了约定。合同附件中还附标注商铺的室号、面积的平面分割图。同日双方又签订了《晶华公馆B2-A21商户进场协议》，该协议约定，乙方(即原告)须支付甲方(即被告)进场费10,000元整，5,000元于合同生效三个工作日内支付给甲方。协议另对其他事项对了约定。\n2017年12月2日，被告向原告出具转账委托授权书，同意原告将业务往来款打入被告指定的受托人陈超的账号(并附账号)。\n2017年12月5日，原告向陈超账户转账付款47,500元。原告称前述款项中包括房租25,500元、押金17,000元、进场费5,000元。\n审理中，原告另提交日期为2017年12月22日的收条一份，内容为“今收到瑞幸咖啡(北京)有限公司装修押金7,000元，装修管理费1,400元，共计8,400元”。末尾署名：(上海梓赫置业)陈佳；2018年6月，原告至租赁地点拍摄的照片及视频，证明截止今年6月，系争房屋处大门紧闭，门上张贴了上海辕盛实业发展有限公司的告商户函，该函件载明由于上海梓赫置业有限公司欠付其租金，现通知解除其与上海梓赫置业有限公司的租赁合同。因被告未能向原告交付租赁物，原告提起诉讼。\n",
     *         "RelatedCompanies": [
     *             {
     *                 "KeyNo": "8fab5089a695e3ef695c8af434345e43",
     *                 "Name": "上海梓赫置业有限公司"
     *             },
     *             {
     *                 "KeyNo": "7035da3364f34e4f291ab35ca6489285",
     *                 "Name": "上海辕盛实业发展有限公司"
     *             },
     *             {
     *                 "KeyNo": "ac3c8ac00cba0a53e918435104ae21e7",
     *                 "Name": "瑞幸咖啡(北京)有限公司"
     *             }
     *         ],
     *         "CaseReason": "房屋租赁合同纠纷",
     *         "Id": "73cb2065b6b986d442056ece96efafa20",
     *         "JudgeResult": "一、解除原告瑞幸咖啡(北京)有限公司与被告上海梓赫置业有限公司于2017年11月30日签订的《房屋租赁合同》；二、被告上海梓赫置业有限公司于本判决生效之日起十日内返还原告瑞幸咖啡(北京)有限公司47,500元；三、被告上海梓赫置业有限公司于本判决生效之日起十日内支付原告瑞幸咖啡(北京)有限公司违约金50,000元。如果未按本判决指定的期间履行给付金钱义务，应当依照《中华人民共和国民事诉讼法》第二百五十三条规定，加倍支付迟延履行期间的债务利息。案件受理费2,418元，减半收取计1,209元，由原告瑞幸咖啡(北京)有限公司负担25元，被告上海梓赫置业有限公司负担1,184元。如不服本判决，可以在判决书送达之日起十五日内，向本院递交上诉状，并按对方当事人的人数提出副本，上诉于上海市第一中级人民法院。",
     *         "CourtNoticeList": {
     *             "TotalNum": 2,
     *             "CourtNoticeInfo": [
     *                 {
     *                     "InputDate": "2019-04-04 03:35:54",
     *                     "CnId": "73cb2065b6b986d442056ece96efafa20",
     *                     "InnerId": "5ca5b3da2970c436733f86b6",
     *                     "QccId": "6cadf82f0d19d2c9ccf7acd2bba329c45"
     *                 },
     *                 {
     *                     "InputDate": "2019-04-04 03:35:54",
     *                     "CnId": "73cb2065b6b986d442056ece96efafa20",
     *                     "InnerId": "5ca5b3da2970c436733f86b7",
     *                     "QccId": "40f42e340ef30b945f6804ab1acccd295"
     *                 }
     *             ]
     *         }
     *     }
     * }
     *
     * @apiUse QccError
     */
    private Object GetJudgementDetail(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        JSONObject object = singleQuery("qcc.查询裁判文书详情", params);
        if (object.size() == 0) {
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


    /**
     * @api {get} /JudgeDocV4/SearchJudgmentDoc 裁判文书列表
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} searchKey 公司全名
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
     * @api {get} /CourtV4/SearchZhiXing 被执行信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} searchKey 公司全名
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
     * @api {get} /CourtV4/SearchShiXin 失信信息
     * @apiGroup QCC
     * @apiVersion 0.0.1
     *
     * @apiParam {string} searchKey 公司全名
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
            if (result instanceof PageQuery) {
                result = newJsonObject(
                    "totalRow", ((PageQuery) result).getTotalRow(),
                    "pageSize", ((PageQuery) result).getPageSize(),
                    "pageIndex", ((PageQuery) result).getPageNumber(),
                    "list", ((PageQuery) result).getList()
                );
            }
            if (result instanceof JSONObject) {
                JSONObject resultObj = (JSONObject) result;
                if (resultObj.containsKey("totalRow")) {
                    if (resultObj.getInt("totalRow") == 0){
                        realResult.put("Status", "201");
                    }
                    realResult.put("Paging", newJsonObject(
                        "PageSize", resultObj.getInt("pageSize"),
                        "PageIndex", resultObj.getInt("pageNumber"),
                        "TotalRecords", resultObj.getInt("totalRow")
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
        Map map = null;
        if (sqlId.contains(".")) {
            map = sqlManager.selectSingle(sqlId, params, Map.class);
        } else {
            List<Map> list = sqlManager.execute(sqlId, Map.class, params);
            if (list.size() > 0) {
                map = list.get(0);
            }
        }
        if (map == null) {
            return new JSONObject();
        }
        return JSONUtil.parseFromMap(map);
    }


    public JSONArray listQuery(String sqlId, Map<String, Object> params) {
        if (sqlId.contains(".")) {
            return JSONUtil.parseArray(sqlManager.select(sqlId, JSONObject.class, params));
        } else {
            return JSONUtil.parseArray(sqlManager.execute(sqlId, JSONObject.class, params));
        }
    }

    public JSONObject pageQuery(String sqlId, JSONObject params) {
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
        return JSONUtil.parseObj(pageQuery);
    }


    public static String firstLetterUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
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
