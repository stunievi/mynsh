package com.beeasy.loadqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.config.MQConfig;
import com.beeasy.loadqcc.utils.QccUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.apache.activemq.command.ActiveMQQueue;
import org.bson.Document;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestRiskMonitor {

    private JmsMessagingTemplate jmsMessagingTemplate;

    MongoService mongoService;

    String LOAD_RISK_TXT_PATH;

    String LOAD_RISK_ZIP_PATH;

    public TestRiskMonitor(
            JmsMessagingTemplate jmsMessagingTemplate,
            MongoService mongoService,
            String textPath,
            String zipPath
    ){
        this.jmsMessagingTemplate = jmsMessagingTemplate;
        this.mongoService = mongoService;
        this.LOAD_RISK_TXT_PATH = textPath;
        this.LOAD_RISK_ZIP_PATH = zipPath;
    }

    // 文件路径
    private File qccFileDataPath;
    // 压缩包路径
    private File qccZipDataPath;
    private String orderNo;
    private String downloadUrl;
    private String unzipPassword;
    private JSONObject zipData;

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
            "11", "judgementDtl",
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
            "6","zhixingDtl"
    );

    private void saveRiskBlockData(
            MongoCollection coll,
            JSONObject object
    ){
        String orderNo = this.orderNo;
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

    // 获取企查查zip包，解压，返回zip包数据
    public void unZipRiskData() throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException {
        String downloadUrl = this.downloadUrl;
        String unzipPassword = this.unzipPassword;

        // TODO::
        JSONObject zipData = new JSONObject();

        // 企业监控文件
        JSONObject corpData = JSONObject.parseObject("{\"pushDate\":\"2018-11-20 09:00:00\",\"totalSize\":2,\"result\":[{\"id\":\"97c945cad5a84f17be9f8232bc279911\",\"corpKeyNo\":\"10ce0780ab7644008b73bc2120479d31\",\"corpName\":\"测试科技有限责任公司\",\"mainType\":\"so2\",\"mainTypeName\":\"司法诉讼\",\"type\":\"11\",\"typeName\":\"裁判文书\",\"category\":\"W\",\"categoryName\":\"警示信息\",\"beneficiaryKeyNo\":\"\",\"beneficiaryName\":\"\",\"changeWay\":\"1\",\"changeWayName\":\"新增\",\"beforeValue\":\"\",\"afterValue\":\"新增测试标题1\",\"changeDate\":\"2018-11-20\",\"detailId\":\"956a60c200b1b5c8051690142c1bad66\",\"extendValue1\":\"ms\",\"extendValue1Name\":\"民事\",\"extendValue2\":\"0\",\"extendValue2Name\":\"原告\"},{\"id\":\"97c945cad5a84f17be9f8232bc279911\",\"corpKeyNo\":\"10ce0780ab7644008b73bc2120479d31\",\"corpName\":\"测试科技有限责任公司\",\"mainType\":\"so2\",\"mainTypeName\":\"司法诉讼\",\"type\":\"11\",\"typeName\":\"裁判文书\",\"category\":\"W\",\"categoryName\":\"警示信息\",\"beneficiaryKeyNo\":\"\",\"beneficiaryName\":\"\",\"changeWay\":\"1\",\"changeWayName\":\"新增\",\"beforeValue\":\"\",\"afterValue\":\"新增测试标题2\",\"changeDate\":\"2018-11-20\",\"detailId\":\"956a60c200b1b5c8051690142c1bad11\",\"extendValue1\":\"xz\",\"extendValue1Name\":\"行政\",\"extendValue2\":\"0\",\"extendValue2Name\":\"原告\"}]}");
        JSONArray corpList = corpData.getJSONArray("result");
        // 人员监控文件
        JSONObject personData = JSON.parseObject("{\"pushDate\":\"2018-11-20 09:00:00\",\"totalSize\":2,\"result\":[{\"id\":\"97c945cad5a84f17be9f8232bc279922\",\"personKeyNo\":\"p576b31ce27248d1293f3be15ec8ee11\",\"personName\":\"张三\",\"corpKeyNo\":\"10ce0780ab7644008b73bc2120479d31\",\"corpName\":\"测试科技有限责任公司\",\"mainType\":\"st2\",\"mainTypeName\":\"经营风险\",\"type\":\"6\",\"typeName\":\"被执行信息\",\"category\":\"W\",\"categoryName\":\"警示信息\",\"changeWay\":\"1\",\"changeWayName\":\"新增\",\"beforeValue\":\"\",\"afterValue\":\"（2017）粤03执恢300号\",\"changeDate\":\"2018-11-20\",\"detailId\":\"0dcd3297997d5fc6d4f70dc44c4dba011\"},{\"id\":\"97c945cad5a84f17be9f8232bc279922\",\"personKeyNo\":\"p576b31ce27248d1293f3be15ec8ee11\",\"personName\":\"张三\",\"corpKeyNo\":\"10ce0780ab7644008b73bc2120479d31\",\"corpName\":\"测试科技有限责任公司\",\"mainType\":\"st2\",\"mainTypeName\":\"经营风险\",\"type\":\"6\",\"typeName\":\"被执行信息\",\"category\":\"W\",\"categoryName\":\"警示信息\",\"changeWay\":\"1\",\"changeWayName\":\"新增\",\"beforeValue\":\"\",\"afterValue\":\"（2017）粤03执恢301号\",\"changeDate\":\"2018-11-20\",\"detailId\":\"0dcd3297997d5fc6d4f70dc44c4dba022\"}]}");
        JSONArray personList = personData.getJSONArray("result");
        // 企业新闻舆情推送文件(推送数量限制请参考企查查专业版: 监控设置-推送设置)
        JSONObject corpNewsData = JSONObject.parseObject("{\"pushDate\":\"2019-07-17 15:53:39\",\"totalSize\":10,\"result\":[{\"corpKeyNo\":\"60f83985cf442a0434e827c0445e7a1f\",\"corpName\":\"广州小鹏汽车科技有限公司\",\"extraInfo1\":\"\",\"extraInfo2\":\"\",\"impact\":\"negative\",\"impactDesc\":\"消极\",\"newsId\":\"1bf4d96115ce5a6058a0d5d69502e40d\",\"newsTagsDesc\":\"#小鹏汽车 ; #曹光植 ; #车主 ; #iCloud ; #Model X\",\"newsTagsList\":[\"小鹏汽车\",\"曹光植\",\"车主\",\"iCloud\",\"Model X\"],\"newsTypeDesc\":\"产品信息\",\"newsTypeList\":[\"5100\"],\"publishTime\":\"2019-07-16 23:49:46\",\"source\":\"百家号\",\"title\":\"特斯拉全系车型官方调价 小鹏汽车回应老车主维权\",\"url\":\"https://mbd.baidu.com/newspage/data/landingshare?context=%7B%22nid%22%3A%22news_9117635074386336775%22%2C%22sourceFrom%22%3A%22bjh%22%7D&type=news\"},{\"corpKeyNo\":\"6b242b475738f45a4dd180564d029aa9\",\"corpName\":\"华为技术有限公司\",\"extraInfo1\":\"\",\"extraInfo2\":\"\",\"impact\":\"positive\",\"impactDesc\":\"积极\",\"newsId\":\"544926a67a58b1d6d5e8d1047c1350b6\",\"newsTagsDesc\":\"#华为P30 Pro ; #华为mate20 ; #机型 ; #系统 ; #用户\",\"newsTagsList\":[\"华为P30 Pro\",\"华为mate20\",\"机型\",\"系统\",\"用户\"],\"newsTypeDesc\":\"产品信息\",\"newsTypeList\":[\"5100\"],\"publishTime\":\"2019-07-16 23:46:14\",\"source\":\"百家号\",\"title\":\"华为mate20升级EMUI9.1系统，增加7项功能，网友：再战两年\",\"url\":\"https://mbd.baidu.com/newspage/data/landingshare?context=%7B%22nid%22%3A%22news_9549784706569385148%22%2C%22sourceFrom%22%3A%22bjh%22%7D&type=news\"}]}");
        JSONArray corpNewsList = personData.getJSONArray("result");

        writeFile(corpData.toJSONString());
        writeFile(personData.toJSONString());

        MongoCollection<Document> coll = mongoService.getCollection("riskMonitor_corp.json");
        saveRiskBlockData(coll, corpData);
        MongoCollection<Document> coll2 = mongoService.getCollection("riskMonitor_person.json");
        saveRiskBlockData(coll2,  personData);
        MongoCollection<Document> coll3 = mongoService.getCollection("riskMonitor_corp_news.json");
        saveRiskBlockData(coll3, corpNewsData);

        invokeDtl("corp", corpList);
        invokeDtl("person", personList);

        this.zipData = zipData;

        return;
    }

    // 循环列表获取详情
    public void invokeDtl(
            String dtlType,
            JSONArray dataList
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        if(null == dataList || dataList.size() < 1){
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(new Date());
        for(Object item : dataList){
            JSONObject dataItem = (JSONObject) item;
            // id String  监控记录唯一 ID
            String dataId = dataItem.getString("id");
            String type = dataItem.getString("type");
            String dtlApi = "";
            if(dtlType.equals("corp")){
                dtlApi = corpDtlApi.get(type);
            }else if(dtlType.equals("person")){
                dtlApi = (String) personDtlApi.get(type);
            }
            if(S.notBlank(dtlApi)){
                Method method = this.getClass().getDeclaredMethod(dtlApi, JSONObject.class);
                JSONObject retData = (JSONObject) method.invoke(this, dataItem);
                if(null == retData){
                    return;
                }
                MongoCollection<Document> coll = mongoService.getCollection("riskMonitor_"+dtlApi);
                retData.put("getDataTime", dateNowStr);
                retData.put("id", dataId);
                retData.put("orderNo", this.orderNo);
                Document modifiers = new Document();
                modifiers.append("$set", retData);
                UpdateOptions opt = new UpdateOptions();
                opt.upsert(true);
                List filters = new ArrayList();
                filters.add(Filters.eq("orderNo", this.orderNo));
                filters.add(Filters.eq("id", dataId));
                filters.add(Filters.eq("getDataTime", dateNowStr));
                coll.updateOne(Filters.and(filters), modifiers, opt);

                // 写入待压缩文件
                writeFile(retData.toJSONString());
            }
        }
    }

    private void zipResData(){
        File __file = this.qccFileDataPath;
        File __zip = this.qccZipDataPath;
        try(
                FileInputStream fis = new FileInputStream(__file);
                FileOutputStream fos = new FileOutputStream(__zip);
                ZipOutputStream zip = new ZipOutputStream(fos);
        ) {
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(__file.length());
            zip.putNextEntry(entry);
            byte[] bs = new byte[1024];
            int len = -1;
            while((len = fis.read(bs)) > 0){
                zip.write(bs, 0, len);
            }
        }catch (IOException e){
            e.printStackTrace();
            return;
        }
    }

    private RandomAccessFile fileRaf;

    private void writeFile(
            String data
    ) throws IOException {
        byte[] bs = (data.getBytes(StandardCharsets.UTF_8.name()));
        //写入数据包长度
        this.fileRaf.writeInt(bs.length);
        this.fileRaf.write(bs);
    }

    // 根据下载文件信息整理数据
    public void resRiskData(
            JSONObject reqData
    ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 下载文件的url地址信息

        this.orderNo = reqData.getString("orderNo");
        this.downloadUrl = reqData.getString("downloadUrl");
        this.unzipPassword = reqData.getString("unzipPassword");
        // 解压获取zip内数据 - 详见，[风险监控接口.pdf](https://pro.qichacha.com/p/api/download?id=1P2ZH50CDMQFR9OCDMN4EDDI60A6PQ9T)

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File filePath = new File(LOAD_RISK_TXT_PATH , sdf.format(new Date()));
        File zipPath = new File(LOAD_RISK_ZIP_PATH , sdf.format(new Date()));
        zipPath.mkdirs();
        filePath.mkdirs();
        File __file = new File(filePath,orderNo + ".txt");
        File __zip = new File(zipPath,orderNo + ".zip");
        this.qccFileDataPath = __file;
        this.qccZipDataPath = __zip;

        FileLock lock = null;
        File file = __file;
        try (
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                FileChannel channel = raf.getChannel();
        ){
            lock = channel.lock();
            this.fileRaf = raf;
            // 解压文件，得到数据列表并获取详情
            unZipRiskData();
        } catch (IOException e) {
            // 写入文件失败则忽略此次响应
            System.out.println("风险监控写入失败。");
            return;
        }
        finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        };

        zipResData();

        ActiveMQQueue mqQueue = new ActiveMQQueue("qcc-deconstruct-request");
        jmsMessagingTemplate.convertAndSend(mqQueue , new MQConfig.FileRequest(orderNo, orderNo, __zip));

    }

    // 获取企查查数据
    private static JSONObject getRiskRemoteData(
            String url,
            Map params
    ){
        String resStr = QccUtil.getData(url, params);
        return JSONObject.parseObject(resStr);
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
    public static JSONObject listLatestOrderNo(){
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
    public static JSONObject getDownloadUrl(
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
            JSONObject reqItemData
    ){
        return  JSONObject.parseObject("{\n"
            + "\t\"status\": \"200\",\n"
            + "\t\"msg\": \"查询成功\",\n"
            + "\t\"result\": {\n"
            + "\t\t\"name\": \"宁夏宁丰餐饮服务有限公司\",\n"
            + "\t\t\"orgNo\": \"22768302-9\",\n"
            + "\t\t\"executeGov\": \"银川市兴庆区人民法院\",\n"
            + "\t\t\"executeStatus\": \"全部未履行\",\n"
            + "\t\t\"province\": \"宁夏\",\n"
            + "\t\t\"executeNo\": \"（2016）银劳人仲裁字1403号\",\n"
            + "\t\t\"lianDate\": \"2017-01-23\",\n"
            + "\t\t\"anNo\": \"(2017)宁0104执756号\",\n"
            + "\t\t\"executeUnite\": \"银川市劳动人事争议仲裁委员会\",\n"
            + "\t\t\"publicDate\": \"2017-02-27\",\n"
            + "\t\t\"actionRemark\": \"其他有履行能力而拒不履行生效法律文书确定义务的\",\n"
            + "\t\t\"yiWu\": \"支付申请人工资12672元\"\n"
            + "\t}\n"
            + "}");
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
            JSONObject reqItemData
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/shixinDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                )
        );
    }

    /**
     * 司法拍卖详情
     *
     * @return
     * content	String	拍卖内容
     */
    public JSONObject judicialSaleDtl(
            JSONObject reqItemData
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
            JSONObject reqItemData
    ){
        return JSONObject.parseObject("{\n"
                + "\t\"status\": \"200\",\n"
                + "\t\"msg\": \"查询成功\",\n"
                + "\t\"result\": {\n"
                + "\t\t\"content\": \"内容\"\n"
                + "\t}\n"
                + "}");
//        return getRiskRemoteData(
//                "https://pro.qichacha.com/api/corpdtl/riskMonitor/judgementDtl",
//                C.newMap(
//                        "detailId", reqItemData.getString("detailId")
//                )
//        );
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
            JSONObject reqItemData
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
            JSONObject reqItemData
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
            JSONObject reqItemData
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
                )
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
            JSONObject reqItemData
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/taxIllegalDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                )
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
            JSONObject reqItemData
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/getDetailOfOweNotice",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                )
        );
    }

    /**
     * 送达公告详情
     *
     * @return
     * content	String	公告内容
     */
    public JSONObject getQccDeliveryNoticeDtl(
            JSONObject reqItemData
    ){
        return getRiskRemoteData(
                "https://pro.qichacha.com/api/corpdtl/riskMonitor/getQccDeliveryNoticeDtl",
                C.newMap(
                        "detailId", reqItemData.getString("detailId")
                )
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

