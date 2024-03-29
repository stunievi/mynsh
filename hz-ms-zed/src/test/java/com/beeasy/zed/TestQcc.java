package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.beetl.sql.core.SQLReady;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.E;
import org.osgl.util.S;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.beeasy.zed.DBService.sqlManager;
import static org.junit.Assert.*;


public class TestQcc {

    public static ZedService zedService;
    public static DeconstructService deconstructService = new DeconstructService();

    @BeforeClass
    public static void onInit() {
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                assertNull(e);
            }
        });
        DBService.getInstance().initSync();
        new QccService().initSync();
        deconstructService.initSync();
        new NettyService().initAsync();

//        clearTable("QCC_JUDGMENT_DOC");
//        clearTable("QCC_COURT_NOTICE");
//        clearTable("QCC_COURT_ANNOUNCEMENT");
    }

    @Before
    public void onBefore() {
        HttpServerHandler.LastException = null;
    }

    @Test
    public void SearchShiXin() throws Exception {
        clearTable("QCC_SHIXIN");
        read("/CourtV4/SearchShiXin.json?searchKey=惠州市维也纳惠尔曼酒店管理有限公司");
        checkPageMatched("/CourtV4/SearchShiXin?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
    }

    @Test
    public void SearchZhiXing() throws Exception {
        clearTable("QCC_ZHIXING");
        read("/CourtV4/SearchZhiXing.json?searchKey=惠州市维也纳惠尔曼酒店管理有限公司");
        checkPageMatched("/CourtV4/SearchZhiXing?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
    }

    @Test
    public void SearchJudgmentDoc() throws Exception {
        clearTable("QCC_JUDGMENT_DOC_COM", "QCC_JUDGMENT_DOC_CN", "QCC_JUDGMENT_DOC");
        JSONObject object = read("/JudgeDocV4/SearchJudgmentDoc.json?searchKey=惠州市维也纳惠尔曼酒店管理有限公司");
        String id = object.getByPath("Result.0.Id", String.class);
        assertNotNull(id);
        read("/JudgeDocV4/GetJudgementDetail.json?id=" + id);

        checkPageMatched("/JudgeDocV4/SearchJudgmentDoc?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject detail = checkObjectMatched("/JudgeDocV4/GetJudgementDetail?id=" + id);
        assertTrue(detail.get("Appellor") instanceof JSONArray);
        assertTrue(detail.get("DefendantList") instanceof JSONArray);
        assertTrue(detail.get("ProsecutorList") instanceof JSONArray);
    }

    @Test
    public void SearchCourtAnnouncement() throws Exception {
//        clearTable("QCC_COURT_ANNOUNCEMENT","QCC_COURT_ANNOUNCEMENT_PEOPLE");
        JSONObject object = read("/CourtNoticeV4/SearchCourtAnnouncement.json?companyName=惠州市维也纳惠尔曼酒店管理有限公司");
        String id = object.getByPath("Result.0.Id", String.class);
        assertNotNull(id);
        read("/CourtNoticeV4/SearchCourtAnnouncementDetail.json?id=" + id);

        checkPageMatched("/CourtNoticeV4/SearchCourtAnnouncement?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject obj = checkObjectMatched("/CourtNoticeV4/SearchCourtAnnouncementDetail?id=" + id);
        assertTrue(obj.getJSONArray("NameKeyNoCollection").size() > 0);

    }

    /**
     * 开庭公告
     */
    @Test
    public void SearchCourtNotice() throws Exception {
        clearTable("QCC_COURT_NOTICE_PEOPLE", "QCC_COURT_NOTICE");

        JSONObject s1 = read("/CourtAnnoV4/SearchCourtNotice.json?searchKey=惠州市维也纳惠尔曼酒店管理有限公司");
        String id = s1.getByPath("Result.0.Id", String.class);
        assertNotNull(id);
        JSONObject s2 = read("/CourtAnnoV4/GetCourtNoticeInfo.json?id=" + id);

        JSONObject page = checkResult("/CourtAnnoV4/SearchCourtNotice?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(s1, page);
        JSONObject obj = checkResult("/CourtAnnoV4/GetCourtNoticeInfo?id=" + id);
        checkSim(s2, obj);
    }

    /**
     * 司法协助信息
     */
    @Test
    public void GetJudicialAssistance() throws Exception {
        clearTable("QCC_JUDICIAL_ASSISTANCE", "QCC_EQUITY_FREEZE_DETAIL");
        read("/JudicialAssistance/GetJudicialAssistance.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        String url = ("/JudicialAssistance/GetJudicialAssistance?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"), "200");
        JSONArray list = result.getJSONArray("Result");

        assertTrue(list.size() > 0);
    }


    /**
     * 经营异常信息
     *
     * @throws Exception
     */
    @Test
    public void GetOpException() throws Exception {
        clearTable("QCC_OP_EXCEPTION");
        read("/ECIException/GetOpException.json?fullName=惠州市维也纳惠尔曼酒店管理有限公司&keyNo=692a8d87536443b042bccb655398e3a0");
        String url = "/ECIException/GetOpException?fullName=惠州市维也纳惠尔曼酒店管理有限公司";
        JSONArray list = checkListMatched(url);
        int c = 1;
    }

    @Test
    public void GetJudicialSaleList() throws Exception {
        clearTable("QCC_JUDICIAL_SALE");
        read("/JudicialSale/GetJudicialSaleList.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        String url = "/JudicialSale/GetJudicialSaleList?fullName=惠州市维也纳惠尔曼酒店管理有限公司";
        JSONArray object = checkPageMatched(url);
        String id = object.getByPath("0.Id", String.class);
        assertNotNull(id);
        read("/JudicialSale/GetJudicialSaleDetail.json?id=" + id);
        checkObjectMatched("/JudicialSale/GetJudicialSaleDetail?id=" + id);
    }


    /**
     * 土地抵押
     *
     * @throws Exception
     */
    @Test
    public void GetLandMortgageList() throws Exception {
        clearTable("QCC_LAND_MORTGAGE");
        clearTable("QCC_LAND_MORTGAGE_PEOPLE");
        JSONObject source = read("/LandMortgage/GetLandMortgageList.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        String url = "/LandMortgage/GetLandMortgageList?fullName=惠州市维也纳惠尔曼酒店管理有限公司";
        JSONArray array = checkPageMatched(url);
        String id = array.getByPath("1.Id", String.class);
        assertNotNull(id);
        read("/LandMortgage/GetLandMortgageDetails.json?id=" + id);
        JSONObject object = checkObjectMatched("/LandMortgage/GetLandMortgageDetails?id=" + id);
//        assertTrue(object.getJSONObject("MortgagorName").size() > 0);
//        assertTrue(object.getJSONObject("MortgagePeople").size() > 0);
    }

    /**
     * 环保处罚
     */
    @Test
    public void GetEnvPunishmentList() throws Exception {
        clearTable("QCC_ENV_PUNISHMENT_LIST");
        read("/EnvPunishment/GetEnvPunishmentList.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONArray array = checkPageMatched("/EnvPunishment/GetEnvPunishmentList?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        ;
        String id = (String) array.getByPath("0.Id");
        assertNotNull(id);
        read("/EnvPunishment/GetEnvPunishmentDetails.json?id=" + id);
        checkObjectMatched("/EnvPunishment/GetEnvPunishmentDetails?id=" + id);
    }

    /**
     * 动产抵押
     */
    @Test
    public void GetChattelMortgage() throws Exception {
        clearTable("QCC_CHATTEL_MORTGAGE", "QCC_CMD_PLEDGE\n", "QCC_CMD_PLEDGEE_LIST\n", "QCC_CMD_SECURED_CLAIM\n", "QCC_CMD_GUARANTEE_LIST\n", "QCC_CMD_CANCEL_INFO\n", "QCC_CMD_CHANGE_LIST\n");
        JSONObject source = read("/ChattelMortgage/GetChattelMortgage.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/ChattelMortgage/GetChattelMortgage?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
//        JSONArray list = checkListMatched("/ChattelMortgage/GetChattelMortgage?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
//        String[] children = {"Pledge","PledgeeList","SecuredClaim", "GuaranteeList","CancelInfo", "ChangeList"};
//        assertTrue(list.size() > 0);
//        for (Object object : list) {
//            JSONObject obj = (JSONObject) object;
//            for (String child : children) {
//                assertNotNull(obj.getByPath("Detail." + child, JSON.class));
//            }
//        }
    }

    /**
     * 工商基本信息
     *
     * @throws Exception
     */
    @Test
    public void GetDetailsByName() throws Exception {
//        clearTable("QCC_DETAILS");
        JSONObject source = read("/ECIV4/GetDetailsByName.json?keyword=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject object = checkResult("/ECIV4/GetDetailsByName?fullName=博罗长城学校");
        double sim = sim(object.toJSONString(0), source.toJSONString(0));
        assertTrue(sim > 0.7);
        int c = 1;
    }

    /**
     * 工商历史信息
     *
     * @throws Exception
     */
    @Test
    public void GetHistorytEci() throws Exception {
        clearTable("QCC_HIS_ECI");
        for (Map.Entry<String, Object> entry : DeconstructService.HistorytEciMap.entrySet()) {
            clearTable((String) entry.getValue());
        }
        clearTable("QCC_HIS_ECI_EMPLOYEE_LIST_JOB");
        JSONObject source = read("/History/GetHistorytEci.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytEci?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }


    @Test
    public void GetHistorytInvestment() throws Exception {
        clearTable("QCC_HIS_INVESTMENT");
        JSONObject source = read("/History/GetHistorytInvestment.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytInvestment?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }


    /**
     * 历史股东
     *
     * @throws Exception
     */
    @Test
    public void GetHistorytShareHolder() throws Exception {
        clearTable("QCC_HIS_SHARE_HOLDER", "QCC_HIS_SHARE_HOLDER_DETAILS");
        JSONObject source = read("/History/GetHistorytShareHolder.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytShareHolder?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target, 0.5);
    }

    @Test
    public void GetHistoryShiXin() throws Exception {
        clearTable("QCC_HIS_SHIXIN");
        JSONObject source = read("/History/GetHistoryShiXin.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistoryShiXin?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistoryZhiXing() throws Exception {
        clearTable("QCC_HIS_ZHIXING");
        JSONObject source = read("/History/GetHistoryZhiXing.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistoryZhiXing?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistorytCourtNotice() throws Exception {
        clearTable("QCC_HIS_COURT_NOTICE");
        JSONObject source = read("/History/GetHistorytCourtNotice.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytCourtNotice?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistorytJudgement() throws Exception {
        clearTable("QCC_HIS_JUDGEMENT\n");
        JSONObject source = read("/History/GetHistorytJudgement.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytJudgement?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistorytSessionNotice() throws Exception {
        clearTable("QCC_HIS_SESSION_NOTICE\n");
        JSONObject source = read("/History/GetHistorytSessionNotice.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytSessionNotice?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistorytMPledge() throws Exception {
        clearTable("QCC_HIS_M_PLEDGE\n");
        JSONObject source = read("/History/GetHistorytMPledge.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytMPledge?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistorytPledge() throws Exception {
        clearTable("QCC_HIS_PLEDGE\n");
        JSONObject source = read("/History/GetHistorytPledge.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytPledge?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistorytAdminPenalty() throws Exception {
        clearTable("QCC_HIS_ADMIN_PENALTY_EL\n", "QCC_HIS_ADMIN_PENALTY_CCL\n");
        JSONObject source = read("/History/GetHistorytAdminPenalty.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytAdminPenalty?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistorytAdminLicens() throws Exception {
        clearTable("QCC_HIS_ADMIN_LICENS_EL\n", "QCC_HIS_ADMIN_LICENS_CCL\n");
        JSONObject source = read("/History/GetHistorytAdminLicens.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/History/GetHistorytAdminLicens?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void SearchFresh() throws Exception {
        clearTable("QCC_FRESH");
        JSONObject source = read("/ECIV4/SearchFresh.json?keyword=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/ECIV4/SearchFresh?keyword=北京");
        checkSim(source, target, 0.6);
    }

    @Test
    public void SearchTreeRelationMap() throws Exception {
        clearTable("QCC_COMPANY_MAP");
        JSONObject source = read("/ECIRelationV4/SearchTreeRelationMap.json?keyNo=692a8d87536443b042bccb655398e3a0&fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/ECIRelationV4/SearchTreeRelationMap?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetCompanyEquityShareMap() throws Exception {
        clearTable("QCC_CESM", "QCC_CESM_ACLP");
        JSONObject source = read("/ECIRelationV4/GetCompanyEquityShareMap.json?keyNo=692a8d87536443b042bccb655398e3a0&fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/ECIRelationV4/GetCompanyEquityShareMap?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target, 0.6);
    }

    @Test
    public void GenerateMultiDimensionalTreeCompanyMap() throws Exception {
        clearTable("QCC_TREE_RELATION_MAP");
        JSONObject source = read("/ECIRelationV4/GenerateMultiDimensionalTreeCompanyMap.json?keyNo=692a8d87536443b042bccb655398e3a0&fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/ECIRelationV4/GenerateMultiDimensionalTreeCompanyMap?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetStockRelationInfo() throws Exception {
        for (Map.Entry<String, Object> entry : DeconstructService.GetStockRelationInfoMap.entrySet()) {
            clearTable((String) entry.getValue());
        }
        JSONObject source = read("/CIAEmployeeV4/GetStockRelationInfo.json?companyName=惠州市维也纳惠尔曼酒店管理有限公司&name=雷军");
        JSONObject target = checkResult("/CIAEmployeeV4/GetStockRelationInfo?fullName=惠州市维也纳惠尔曼酒店管理有限公司&name=雷军");
        checkSim(source, target, 0.6);
    }

    @Test
    public void GetHoldingCompany() throws Exception {
        clearTable("QCC_HOLDING_COMPANY", "QCC_HOLDING_COMPANY_NAMES_PATHS", "QCC_HOLDING_COMPANY_NAMES_OPER");
        JSONObject source = read("/HoldingCompany/GetHoldingCompany.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/HoldingCompany/GetHoldingCompany?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetStockAnalysisData() throws Exception {
        clearTable("QCC_SAD_PARTNERS", "QCC_SAD", "QCC_SAD_STOCK_LIST");
        JSONObject source = read("/ECICompanyMap/GetStockAnalysisData.json?keyWord=惠州市维也纳惠尔曼酒店管理有限公司");
        JSONObject target = checkResult("/ECICompanyMap/GetStockAnalysisData?fullName=惠州市维也纳惠尔曼酒店管理有限公司");
        checkSim(source, target);

    }


//    @Test
//    public void testGetJudgementDetail(){
//        try {
//            read("GetJudgementDetail.json");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public JSONObject checkResult(String url) throws UnsupportedEncodingException {
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"), "200");
        return result;
    }

    public JSONObject checkObjectMatched(String url) throws UnsupportedEncodingException {
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"), "200");
        JSONObject _result = result.getJSONObject("Result");
        assertTrue(_result.size() > 0);
        return _result;
    }

    public JSONArray checkListMatched(String url) throws UnsupportedEncodingException {
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"), "200");
        JSONArray array = result.getJSONArray("Result");
        assertTrue(result.size() > 0);
        return array;
    }

    public JSONArray checkPageMatched(String url) throws UnsupportedEncodingException {
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"), "200");
        int count = result.getByPath("Paging.TotalRecords", Integer.class);
        assertTrue(count > 0);
        return result.getJSONArray("Result");
    }

    public JSONObject huGet(String url) throws UnsupportedEncodingException {
        url = "http://localhost:8081" + url;
        if (url.contains("?")) {
            int idex = url.indexOf("?");
            url = url.substring(0, idex) + "?" + URLUtil.encode(url.substring(idex + 1));
        }
        String str = HttpUtil.get(url);
        return JSONUtil.parseObj(str);
    }

    public JSONObject read(String url, Map<String, String> params) throws FileNotFoundException {
        if (params == null) {
            params = MapUtil.newHashMap();
        }
        String param = "";
        if (url.contains("?")) {
            param = url.substring(url.indexOf("?"));
            url = url.substring(0, url.indexOf("?"));
        }
        String str = IoUtil.readUtf8(
                new RandomAccessFile("D:/work" + url, "r").getChannel()
        );
//        str = str.replaceAll("", "惠州市维也纳惠尔曼酒店管理有限公司");
        str = str.replaceAll("深圳市桑协世纪科技有限公司", "惠州市维也纳惠尔曼酒店管理有限公司");
        str = str.replaceAll("惠州市帅星贸易有限公司", "惠州市维也纳惠尔曼酒店管理有限公司");

        url = S.fmt("http://localhost:3033/qcc%s%s", url, param);
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.set("Proxy-Url", url);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(str.getBytes(StandardCharsets.UTF_8));
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, buf, headers, headers);
//        deconstructService.doNettyRequest(null, request);
        return JSONUtil.
                parseObj(str);
    }

    public JSONObject read(String url) throws Exception {
        return read(url, null);
    }


    public int count(String table) {
        try {
            String sql = S.fmt("select count(1) from %s", table);
            List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
            return list.get(0).getInt("1", 0);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
        return 0;
    }

    public void checkCount(String table) {
        assertTrue(count(table) > 0);
    }

    public void checkSim(JSONObject source, JSONObject target, double limit) {
        source.remove("OrderNumber");
        String ss = source.toJSONString(0);
        String st = target.toJSONString(0);
        double sim = sim(ss, st);

        System.out.println(ss);
        System.out.println(st);
        System.out.println(sim);
        assertTrue(sim > limit);
    }

    public void checkSim(JSONObject source, JSONObject target) {
        checkSim(source, target, 0.7);
    }


    public static void clearTable(String... table) {
        for (String s : table) {
            String sql = S.fmt("delete from %s where inner_company_name = '惠州市维也纳惠尔曼酒店管理有限公司'", s);
            try {
                sqlManager.executeUpdate(new SQLReady(sql));
            } catch (Exception e) {
            }
        }
    }

    private static int min(int one, int two, int three) {
        int min = one;
        if (two < min) {
            min = two;
        }
        if (three < min) {
            min = three;
        }
        return min;
    }

    private static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();

    }

    public static int ld(String str1, String str2) {
        int d[][]; // 矩阵
        int n = str1.length();
        int m = str2.length();
        int i; // 遍历str1的
        int j; // 遍历str2的
        char ch1; // str1的
        char ch2; // str2的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        char[] c1 = str1.toCharArray();
        char[] c2 = str2.toCharArray();
        Arrays.sort(c1);
        Arrays.sort(c2);
        str1 = new String(c1);
        str2 = new String(c2);

        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }
        for (i = 1; i <= n; i++) { // 遍历str1
            ch1 = str1.charAt(i - 1);
            // 去匹配str2
            for (j = 1; j <= m; j++) {
                ch2 = str2.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    public static double sim(String str1, String str2) {
        try {
            double ld = (double) ld(str1, str2);
            return (1 - ld / (double) Math.max(str1.length(), str2.length()));
        } catch (Exception e) {
            return 0.1;
        }
    }


    //根据inner_company_name 修改省份
    @Test
    public void testUpdate() {
        Version.update("QCC_COURT_ANNOUNCEMENT");
//        Version.update("qcc_details");

    }

    @Test
    public void testSql() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select on_board_start_time, on_board_end_time,end_date,start_date ,id from QCC_LAND_MORTGAGE";

//        String sql = "select on_board_start_time, on_board_end_time,end_date,start_date ,inner_company_name from QCC_LAND_MORTGAGE where inner_company_name = '惠州市海东置业有限公司'";

        List<com.alibaba.fastjson.JSONObject> list = sqlManager.execute(new SQLReady(sql), com.alibaba.fastjson.JSONObject.class);
        for (int i = 1; i < list.size(); i++) {
            String updateSql = "update QCC_LAND_MORTGAGE set on_board_start_time = '" + simpleDateFormat.format(new Date(list.get(i).getString("onBoardStartTime"))) + "',  on_board_end_time = '" + simpleDateFormat.format(new Date(list.get(i).getString("onBoardEndTime"))) + "', start_date = '" + simpleDateFormat.format(new Date(list.get(i).getString("startDate"))) + "' ,end_date = '" + simpleDateFormat.format(new Date(list.get(i).getString("endDate"))) + "'where  id = '" + list.get(i).getString("id") + "'";
            sqlManager.executeUpdate(new SQLReady(updateSql));
//        String updateSql = "update QCC_LAND_MORTGAGE set   start_date = '" + simpleDateFormat.format(new Date(list.get(0).getString("startDate"))) + "' ,end_date = '" + simpleDateFormat.format(new Date(list.get(0).getString("endDate"))) + "'where  inner_company_name = '"+ list.get(0).getString("innerCompanyName")+"'";
//        sqlManager.executeUpdate(new SQLReady(updateSql));
        }

    }


    @Test
    public void testSqlDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select inner_id,capi_date from QCC_DETAILS_PARTNERS";
        List<com.alibaba.fastjson.JSONObject> list = sqlManager.execute(new SQLReady(sql), com.alibaba.fastjson.JSONObject.class);
        for (int i = 1; i < list.size(); i++) {
            if (null != list.get(i).getString("capiDate")) {
                String updateSql = "update QCC_DETAILS_PARTNERS set capi_date = '" + simpleDateFormat.format(new Date(list.get(i).getString("capiDate"))) + "' where  inner_id = '" + list.get(i).getString("innerId") + "' and capi_date  is not null";
                sqlManager.executeUpdate(new SQLReady(updateSql));
            }
        }
    }

    @Test
    public void testSqlDate2() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select shoud_date,inner_id from QCC_DETAILS_PARTNERS";
        List<com.alibaba.fastjson.JSONObject> list = sqlManager.execute(new SQLReady(sql), com.alibaba.fastjson.JSONObject.class);
        for (int i = 1; i < list.size(); i++) {
            if (null != list.get(i).getString("shoudDate")) {
                String updateSql = "update QCC_DETAILS_PARTNERS set shoud_date = '" + simpleDateFormat.format(new Date(list.get(i).getString("shoudDate"))) + "' where  inner_id = '" + list.get(i).getString("innerId") + "' and shoud_date  is not null";
                sqlManager.executeUpdate(new SQLReady(updateSql));
            }
        }
    }


    @Test
    public void testSelectSql(){
        String sql = "select name,inner_company_name  from qcc_holding_company_names";
        List<com.alibaba.fastjson.JSONObject> list = sqlManager.execute(new SQLReady(sql), com.alibaba.fastjson.JSONObject.class);
        Map map = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getString("name")+ "=>"+ list.get(i).getString("innerCompanyName"),list.get(i).getString("innerCompanyName"));
        }
        System.out.println(map.toString());
    }


    //简单粗暴删除所有QCC开头的数据
    @Test
    public  void selectAndDelete(){
        String sql = "select TABNAME from syscat.tables where  tabschema='DB2INST1' and   tabname like 'QCC_%'";
        List<com.alibaba.fastjson.JSONObject> list = sqlManager.execute(new SQLReady(sql), com.alibaba.fastjson.JSONObject.class);
        System.out.println(list.toString());
        for (int i = 0; i < list.size(); i++) {
            String delSq = "delete from " +  list.get(i).getString("tabname");
            System.out.println(delSq);
            try {
                sqlManager.executeUpdate(new SQLReady(delSq));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
