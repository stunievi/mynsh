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
            map.remove("beetlRn");
            if (sqlId.startsWith("qcc.")) {
                return convertToQccStyle(map);
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
        registerRoute("/EnvPunishment/GetEnvPunishmentList",service::GetEnvPunishmentList);
        registerRoute("/EnvPunishment/GetEnvPunishmentDetails",service::GetEnvPunishmentDetails);
        registerRoute("/ChattelMortgage/GetChattelMortgage", service::GetChattelMortgage);
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
     *
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
            while(it.hasNext()){
                Map.Entry<String, Object> entry = it.next();
                if(entry.getKey().startsWith("Ex1")){
                    Pledge.put(entry.getKey().replace("Ex1",""), entry.getValue());
                    it.remove();
                }
                if(entry.getKey().startsWith("Ex2")){
                    SecuredClaim.put(entry.getKey().replace("Ex2", ""), entry.getValue());
                    it.remove();
                }
                if(entry.getKey().startsWith("Ex3")){
                    CancelInfo.put(entry.getKey().replace("Ex3", ""), entry.getValue());
                    it.remove();
                }
            }
            detail.put("Pledge",Pledge);
            detail.put("SecuredClaim",SecuredClaim);
            detail.put("CancelInfo", CancelInfo);
            detail.put("PledgeeList",
                PledgeeList
                    .stream()
                    .map(i -> (JSONObject)i)
                    .filter(i -> {
                        if(i.getStr("CmId","##").equals(object.getStr("InnerId", "$$"))){
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
                    if(i.getStr("CmId","##").equals(object.getStr("InnerId", "$$"))){
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
                        if(i.getStr("CmId","##").equals(object.getStr("InnerId", "$$"))){
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
     *
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
     *
     * @apiUse QccError
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
     *
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
     *
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
     * @apiSuccess {string} EEquityFreezeDetail.xecutionMatters 执行事项
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
     * @apiSuccess {object[]} Prosecutor 案号
     * @apiSuccess {string} Prosecutor.Name 上诉人
     * @apiSuccess {string} Prosecutor.KeyNo KeyNo
     *
     * @apiSuccess {object[]} Defendant 案号
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
     *
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
        return  pageQuery("qcc.查询法院公告列表", params);
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
     *
     * @apiUse QccError
     */
    private Object SearchJudgmentDoc(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject params) {
        return  pageQuery("qcc.查询裁判文书列表", params);
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
