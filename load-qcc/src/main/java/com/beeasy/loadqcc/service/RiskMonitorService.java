package com.beeasy.loadqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.entity.LoadQccDataExtParm;
import com.beeasy.loadqcc.entity.RiskDataExtParam;
import com.beeasy.loadqcc.utils.QccUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RiskMonitorService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    MongoService mongoService;

    @Value("${loadQcc.riskTxtPath}")
    String LOAD_RISK_TXT_PATH;

    @Value("${loadQcc.riskZipPath}")
    String LOAD_RISK_ZIP_PATH;

    @PostConstruct
    public void init(){
        RiskDataExtParam.LOAD_RISK_TXT_PATH = LOAD_RISK_TXT_PATH;
        RiskDataExtParam.LOAD_RISK_ZIP_PATH = LOAD_RISK_ZIP_PATH;
    }

    /**
     * 被执行人信息详情 riskMonitor_zhixingDtl
     * 失信被执行人详情 riskMonitor_shixinDtl
     * 司法拍卖详情 riskMonitor_judicialSaleDtl
     * 裁判文书详情 riskMonitor_judgementDtl
     * 法院公告详情 riskMonitor_courtAnncDtl
     * 开庭公告详情 riskMonitor_courtNoticeDtl
     * 股权出质详情 riskMonitor_cmpsPldgListDtl
     * 税收违法详情 riskMonitor_taxIllegalDtl
     * 欠税公告详情 riskMonitor_getDetailOfOweNotice
     * 送达公告详情 riskMonitor_getQccDeliveryNoticeDtl
     * 融资动态详情 riskMonitor_getFinancingsGroupDtl
     */
    private static final C.Map<String, String> corpDtlApi = C.newMap(
            "1", "受益人变更",
            "4", "法定代表人",
            "5", "注册资本",
            "6", "经营状态",
            "7", "股东",
            "8", "主要人员",
            "9", "被执行人信息",
            "10", "失信被执行人",
            "11", "裁判文书",
            "12", "法院公告",
            "13", "开庭公告",
            "14", "司法拍卖",
            "15", "行政处罚",
            "16", "严重违法",
            "17", "环保处罚",
            "18", "股权出质",
            "19", "动产抵押",
            "20", "经营异常",
            "21", "对外投资",
            "22", "清算信息",
            "23", "简易注销",
            "24", "企业地址",
            "25", "经营范围",
            "26", "税收违法",
            "27", "欠税公告",
            "28", "融资动态",
            "29", "大股东变更",
            "30", "实际控制人变更",
            "31", "司法协助",
            "32", "送达公告",
            "33", "招投标"
    );

    private static final Map personDtlApi = C.newMap(
            "1", "法人信息",
            "2"," 任职信息",
            "3"," 投资信息",
            "4"," 股权信息",
            "5"," 失信信息",
            "6"," 被执行信息"
    );

    private void saveRiskBlockData(
        MongoCollection coll,
        String orderNo,
        JSONObject object
    ){
        if(null == object){
            return;
        }
        object.put("orderNo", orderNo);
        Document modifiers = new Document();
        modifiers.append("$set", object);
        UpdateOptions opt = new UpdateOptions();
        opt.upsert(true);
        coll.updateOne(Filters.eq("orderNo", orderNo), modifiers, opt);
    }

    // 获取zip包，解压，返回zip包数据
    public JSONObject unZipRiskData(
        String orderNo,
        String url,
        String password
    ){
//        mongoService.
        // 企业监控文件
        MongoCollection<Document> coll = mongoService.getCollection("corp.json");
        saveRiskBlockData(coll, orderNo, new JSONObject());
        // 人员监控文件
        MongoCollection<Document> coll2 = mongoService.getCollection("person.json");
        saveRiskBlockData(coll2, orderNo, new JSONObject());
        // 企业新闻舆情推送文件(推送数量限制请参考企查查专业版: 监控设置-推送设置)
        MongoCollection<Document> coll3 = mongoService.getCollection("corp_news.json");
        saveRiskBlockData(coll3, orderNo, new JSONObject());
        return new JSONObject();
    }

    // 循环列表获取详情
    public void invokeDtl(
        String orderNo,
        JSONArray dataList,
        RiskDataExtParam extParam
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(null == dataList || dataList.size() < 1){
            return;
        }
        for(Object item : dataList){
            JSONObject dataItem = (JSONObject) item;
            String type = dataItem.getString("key");
            String dtlApi = corpDtlApi.get(type);
            Method method = this.getClass().getDeclaredMethod(dtlApi, JSONObject.class, RiskDataExtParam.class);
            JSONObject retData = (JSONObject) method.invoke(this, dataItem, extParam);
            MongoCollection<Document> coll = mongoService.getCollection("riskMonitor_"+dtlApi);
            saveRiskBlockData(coll, orderNo, retData);
        }
    }

    // 获取数据列表
    public void getDataListDtl(
        String orderNo,
        JSONObject zipData,
        RiskDataExtParam extParam
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // 企业监控文件
        JSONObject corpData = zipData.getJSONObject("corp");
        JSONArray corpList = corpData.getJSONArray("result");
        // 人员监控文件
        JSONObject personData = zipData.getJSONObject("person");
        JSONArray personList = personData.getJSONArray("result");

        invokeDtl(orderNo, corpList, extParam);
        invokeDtl(orderNo, personList, extParam);

    }

    // 根据下载文件信息整理数据
    public void resRiskData(
        JSONObject reqData
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 下载文件的url地址信息
        RiskDataExtParam extParam = new RiskDataExtParam(reqData);

        String orderNo = extParam.getOrderNo();
        String downloadUrl = extParam.getDownloadUrl();
        String unzipPassword = extParam.getUnzipPassword();
        // 解压获取zip内数据 - 详见，[风险监控接口.pdf](https://pro.qichacha.com/p/api/download?id=1P2ZH50CDMQFR9OCDMN4EDDI60A6PQ9T)
        JSONObject zipData = unZipRiskData(orderNo, downloadUrl, unzipPassword);

        getDataListDtl(orderNo, zipData, extParam);
    }

    // 获取企查查数据
    private JSONObject getRiskRemoteData(
            String url,
            Map params,
            RiskDataExtParam extParam
    ){
        String resStr = QccUtil.getData(url, params);
        return JSONObject.parseObject(resStr);
    }
    private JSONObject getRiskRemoteData(
            String url,
            Map params
    ){
        return getRiskRemoteData(url, params, null);
    }

    /**
     * 添加关注公司名单
     * keyWord	String	是	关键字（公司名、注册号、社会统一信用代码）
     */
    public JSONObject setRadarCompany(String keyWord){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/radar/setRadarCompany",
                C.newMap(
                        "keyWord", keyWord
                )
        );
    }
    /**
     * 取消关注公司
     * keyWord	String	是	关键字（公司KeyNo、公司名、注册号、社会统一信用代码）
     */
    public JSONObject cancelRadarCompany(String keyWord){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/radar/cancelRadarCompany",
                C.newMap(
                        "keyWord", keyWord
                )
        );
    }
    /**
     * 添加关注人员
     *  @param mode	Int32	是	用户所传参数的类型：1-人员姓名 + 人员身份证；3-企业名称 + 人员姓名
     *  @param personName	String	是	人员姓名
     *  @param personId	String	否	人员身份证, 如果mode=1时必填
     *  @param corpName	String	否	企业名称, 如果mode=3时必填
     *
     *  @return radarPersonId	String	关注人员ID, 取消关注时需要传该值
     * */
    public JSONObject setRadarPerson(int mode, String personName, String personId, String corpName){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/radar/setRadarPerson",
                C.newMap(
                    "mode", mode,
                    "personName", personName,
                    "personId", personId,
                    "corpName", corpName
                )
        );
    }
    /**
     * 取消关注人员
     * radarPersonId	String	是	添加关注人员返回的关注人员ID
     */
    public JSONObject cancelRadarPerson(String radarPersonId){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/radar/cancelRadarPerson",
                C.newMap(
                        "radarPersonId", radarPersonId
                )
        );
    }
    /**
     * 获取最近10笔订单号
     *
     * @return
     * orderNo    String	订单号
     * createDate	String	创建时间
     */
    public JSONObject listLatestOrderNo(){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/radar/listLatestOrderNo",
                C.newMap()
        );
    }
    /**
     * 根据订单号获取下载文件的url地址
     *
     * @param orderNo	String	是	订单号
     *
     * @return orderNo    String	订单号
     *             downloadUrl	String	下载压缩包地址
     *             downloadUrlExpiredTime	String	下载压缩包地址失效时间
     *             unzipPassword	String	压缩包解压密码
     */
    public JSONObject getDownloadUrl(
            String orderNo
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/radar/getDownloadUrl",
                C.newMap(
                        "orderNo", orderNo
                )
        );
    }

    /**
     * 被执行人信息详情
     *
     * @return
     * name    String	被执行人
     * partyCardNum	String	身份证号码/组织机构代码
     * biaoDi	String	执行标的
     * executeGov	String	执行法院
     * lianDate	String	立案时间
     * anNo	String	案号
     */
    public JSONObject zhixingDtl(
            JSONObject reqItemData,
            RiskDataExtParam extParam
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/zhixingDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                ),
                extParam
        );
    }

    /**
     * 失信被执行人详情
     *
     * @return
     * name    String	失信被执行人
     * orgNo	String	组织机构代码
     * executeGov	String	执行法院
     * executeStatus	String	被执行的履行情况
     * province	String	省份
     * executeNo	String	执行依据文号
     * lianDate	String	立案时间
     * anNo	String	案号
     * executeUnite	String	做出执行依据单位
     * publicDate	String	发布时间
     * actionRemark	String	失信被执行人行为具体情形
     * yiWu	String	生效法律文书确定的义务
     */
    public JSONObject shixinDtl(
        JSONObject reqItemData,
        RiskDataExtParam extParam
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/shixinDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                ),
                extParam
        );
    }

    /**
     * 司法拍卖详情
     *
     * @return
     * content	String	拍卖内容
     */
    public JSONObject judicialSaleDtl(
        JSONObject reqItemData,
        RiskDataExtParam extParam
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/judicialSaleDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                )
        );
    }

    /**
     * 裁判文书详情
     *
     * @return
     * content	String	裁判文书内容
     */
    public JSONObject judgementDtl(
        JSONObject reqItemData,
        RiskDataExtParam extParam
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/judgementDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                )
        );
    }

    /**
     * 法院公告详情
     *
     * @return
     * party    String	当事人
     * category	String	公告类型
     * publishDate	String	刊登日期
     * publishPage	String	刊登版面
     * submitDate	String	上传日期
     * court	String	公告人
     * content	String	内容
     */
    public JSONObject courtAnncDtl(
        JSONObject reqItemData,
        RiskDataExtParam extParam
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/courtAnncDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                )
        );
    }


    /**
     * 开庭公告详情
     *
     * @return
     * chiefJudge	String	审判长/主审人
     * prosecutorList
     * 上诉人	keyNo	String	上诉人KeyNo
     * name	String	上诉人名称
     * caseReason	String	案由
     * defendantList
     * 被上诉人	keyNo	String	被上诉人KeyNo
     * name	String	被上诉人名称
     * caseNo	String	案号
     * openTime	String	开庭日期
     * executeGov	String	法院
     * province	String	地区
     * scheduleTime	String	排期日期
     * executeUnite	String	法庭
     * content	String	公告内容
     * undertakeDepartment	String	承办部门
     */
    public JSONObject courtNoticeDtl(
        JSONObject reqItemData,
        RiskDataExtParam extParam
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/courtNoticeDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                )
        );
    }

    /**
     * 股权出质详情
     *
     * @return
     * regDate    String	股权出质设立登记日期
     * registNo	String	登记编号
     * pledgor	String	出质人
     * pledgedAmount	String	出质股权数额
     * status	String	状态
     */
    public JSONObject cmpsPldgListDtl(
            JSONObject reqItemData,
            RiskDataExtParam extParam
    ){
        String corpKeyNo = reqItemData.getString("corpKeyNo");
        String registerNo = reqItemData.getString("beforeValue");
        if("" == registerNo || null == registerNo){
            registerNo = Optional.ofNullable(reqItemData.getString("afterValue")).orElse("");
        }
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/cmpsPldgListDtl",
                C.newMap(
                        "corpKeyNo", corpKeyNo,
                        "registerNo", registerNo
                ),
                extParam
        );
    }


    /**
     * 税收违法详情
     *
     * @return
     * taxpayerName    String	纳税人名称
     * taxpayerId	String	纳税人名称ID
     * taxpayerNumber	String	纳税人识别号
     * orgCode	String	组织机构代码
     * address	String	注册地址
     * publishDate	String	发布日期(YYYY-MM-DD)
     * caseNature	String	案件性质
     * taxGov	String	所属税务机关
     * illegalContent	String	主要违法事实、相关法律依据及税务处理处罚情况
     * operInfo
     * 法定代表人
     *      operId	String	法定代表人或负责人ID
     *      operName	String	法定代表人或负责人
     *      operGender	String	性别
     *      operCerType	String	证件名称
     *      operCerNo	String	证件号码
     * financeChiefInfo
     * 负有责任的财务负责人
     *      financeChiefId	String	负有责任的财务负责人ID
     *      financeChiefName	String	负有责任的财务负责人
     *      financeChiefGender	String	性别
     *      financeChiefCerType	String	证件名称
     *      financeChiefCerNo	String	证件号码
     *      agencyInfo
     * 负有直接责任的中介机构
     *      agencyKeyNo	String	负有直接责任的中介机构KeyNo
     *      agencyCompanyName	String	负有直接责任的中介机构
     *      agencyPersonId	String	负有直接责任的中介机构从业人员ID
     *      agencyPersonName	String	负有直接责任的中介机构从业人员
     *      agencyPersonGender	String	性别
     *      agencyPersonCerType	String	证件名称
     *      agencyPersonCerNo	String	证件号码
     */
    public JSONObject taxIllegalDtl(
        JSONObject reqItemData,
        RiskDataExtParam extParam
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/taxIllegalDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                ),
                extParam
        );
    }



    /**
     * 欠税公告详情
     *
     * @return
     * companyName    String	单位名称
     * keyNo	String	单位名称KeyNo
     * type	String	纳税人类型
     * identifyNo	String	纳税人识别号
     * oper
     * 负责人
     *      name	String	负责人姓名
     *      keyNo	String	负责人姓名KeyNo
     * idNo	String	证件号码
     * addr	String	经营地点
     * category	String	欠税税种
     * balance	String	欠税余额
     * newBal	String	当前新发生的欠税余额
     * issuedBy	String	发布单位
     * publishDate	String	发布日期(YYYY-MM-DD)
     */
    public JSONObject getDetailOfOweNotice(
        JSONObject reqItemData,
        RiskDataExtParam extParam
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/getDetailOfOweNotice",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                ),
                extParam
        );
    }

    /**
     * 送达公告详情
     *
     * @return
     * content	String	公告内容
     */
    public JSONObject getQccDeliveryNoticeDtl(
        JSONObject reqItemData,
        RiskDataExtParam extParam
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/getQccDeliveryNoticeDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                ),
                extParam
        );
    }

    /**
     * 融资动态详情
     *
     * @param id
     *
     * @return
     * newsUrl	String	融资新闻链接
     */
    public JSONObject getFinancingsGroupDtl(
            String id
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/getFinancingsGroupDtl",
                C.newMap(
                        "id", id
                )
        );
    }



}
