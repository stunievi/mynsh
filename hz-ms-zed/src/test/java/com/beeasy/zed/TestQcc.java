package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.beetl.sql.core.SQLBatchReady;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;
import org.osgl.util.S;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class TestQcc {

    public static SQLManager sqlManager;
    public static ZedService zedService;
    public static DeconstructService deconstructService;

    @BeforeClass
    public static void onInit(){
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                assertNull(e);
            }
        });
        zedService = new ZedService();
        zedService.initConfig();
        zedService.initDB(true);
        sqlManager = zedService.sqlManager;
        QccService.register(zedService);
        deconstructService = DeconstructService.register(zedService);
        ThreadUtil.execAsync(zedService::initNetty);
        ThreadUtil.sleep(500);

//        clearTable("QCC_JUDGMENT_DOC");
//        clearTable("QCC_COURT_NOTICE");
//        clearTable("QCC_COURT_ANNOUNCEMENT");
    }

    @Before
    public void onBefore(){
        HttpServerHandler.LastException = null;
    }

    @Test
    public void SearchShiXin() throws Exception {
        clearTable("QCC_SHIXIN");
        read("/CourtV4/SearchShiXin.json?searchKey=惠州市帅星贸易有限公司");
        checkPageMatched("/CourtV4/SearchShiXin?searchKey=惠州市帅星贸易有限公司");
    }

    @Test
    public void SearchZhiXing() throws Exception {
        clearTable("QCC_ZHIXING");
        read("/CourtV4/SearchZhiXing.json?searchKey=惠州市帅星贸易有限公司");
        checkPageMatched("/CourtV4/SearchZhiXing?searchKey=惠州市帅星贸易有限公司");
    }

    @Test
    public void SearchJudgmentDoc() throws Exception {
        clearTable("QCC_JUDGMENT_DOC_COM","QCC_JUDGMENT_DOC_CN","QCC_JUDGMENT_DOC");
        JSONObject object = read("/JudgeDocV4/SearchJudgmentDoc.json?searchKey=惠州市帅星贸易有限公司");
        String id = object.getByPath("Result.0.Id", String.class);
        assertNotNull(id);
        read("/JudgeDocV4/GetJudgementDetail.json?id=" + id);

        checkPageMatched("/JudgeDocV4/SearchJudgmentDoc?searchKey=惠州市帅星贸易有限公司");
        JSONObject detail = checkObjectMatched("/JudgeDocV4/GetJudgementDetail?id=" + id);
        assertTrue(detail.get("Appellor") instanceof JSONArray);
        assertTrue(detail.get("DefendantList") instanceof JSONArray);
        assertTrue(detail.get("ProsecutorList") instanceof JSONArray);
    }

    @Test
    public void SearchCourtAnnouncement() throws Exception {
        clearTable("QCC_COURT_ANNOUNCEMENT","QCC_COURT_ANNOUNCEMENT_PEOPLE");
        JSONObject object = read("/CourtNoticeV4/SearchCourtAnnouncement.json?companyName=惠州市帅星贸易有限公司");
        String id = object.getByPath("Result.0.Id", String.class);
        assertNotNull(id);
        read("/CourtNoticeV4/SearchCourtAnnouncementDetail.json?id=" + id);

        checkPageMatched("/CourtNoticeV4/SearchCourtAnnouncement?companyName=惠州市帅星贸易有限公司");
        JSONObject obj = checkObjectMatched("/CourtNoticeV4/SearchCourtAnnouncementDetail?id=" + id);
        assertTrue(obj.getJSONArray("NameKeyNoCollection").size() > 0);

    }

    /**
     * 开庭公告
     */
    @Test
    public void SearchCourtNotice() throws Exception {
        clearTable("QCC_COURT_NOTICE_PEOPLE", "QCC_COURT_NOTICE");

        JSONObject s1 = read("/CourtAnnoV4/SearchCourtNotice.json?searchKey=惠州市帅星贸易有限公司");
        String id = s1.getByPath("Result.0.Id", String.class);
        assertNotNull(id);
        JSONObject s2 = read("/CourtAnnoV4/GetCourtNoticeInfo.json?id=" + id);

        JSONObject page = checkResult("/CourtAnnoV4/SearchCourtNotice?searchKey=惠州市帅星贸易有限公司");
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
       read("/JudicialAssistance/GetJudicialAssistance.json?keyWord=惠州市帅星贸易有限公司");
        String url = ("/JudicialAssistance/GetJudicialAssistance?keyWord=惠州市帅星贸易有限公司");
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"),"200");
        JSONArray list = result.getJSONArray("Result");

        assertTrue(list.size() > 0);
}



    /**
     * 经营异常信息
     * @throws Exception
     */
    @Test
    public void GetOpException() throws Exception {
        clearTable("QCC_OP_EXCEPTION");
        read("/ECIException/GetOpException.json?keyNo=惠州市帅星贸易有限公司");
        String url = "/ECIException/GetOpException?keyNo=惠州市帅星贸易有限公司";
        JSONArray list = checkListMatched(url);
    }

    @Test
    public void GetJudicialSaleList() throws Exception {
        clearTable("QCC_JUDICIAL_SALE");
        read("/JudicialSale/GetJudicialSaleList.json?keyWord=惠州市帅星贸易有限公司");
        String url = "/JudicialSale/GetJudicialSaleList?keyWord=惠州市帅星贸易有限公司";
        JSONArray object = checkPageMatched(url);
        String id = object.getByPath("0.Id", String.class);
        assertNotNull(id);
        read("/JudicialSale/GetJudicialSaleDetail.json?id=" + id);
        checkObjectMatched("/JudicialSale/GetJudicialSaleDetail?id=" + id);
    }


    /**
     * 土地抵押
     * @throws Exception
     */
    @Test
    public void GetLandMortgageList() throws Exception {
        clearTable("QCC_LAND_MORTGAGE");
        clearTable("JG_LM_PEOPLE_RE");
        JSONObject source = read("/LandMortgage/GetLandMortgageList.json?keyWord=惠州市帅星贸易有限公司");
        String url = "/LandMortgage/GetLandMortgageList?keyWord=惠州市帅星贸易有限公司";
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
        read("/EnvPunishment/GetEnvPunishmentList.json?keyWord=惠州市帅星贸易有限公司");
        JSONArray array = checkPageMatched("/EnvPunishment/GetEnvPunishmentList?keyWord=惠州市帅星贸易有限公司");;
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
        clearTable("QCC_CHATTEL_MORTGAGE","QCC_CMD_PLEDGE\n","QCC_CMD_PLEDGEE_LIST\n", "QCC_CMD_SECURED_CLAIM\n","QCC_CMD_GUARANTEE_LIST\n", "QCC_CMD_CANCEL_INFO\n", "QCC_CMD_CHANGE_LIST\n");
        JSONObject source = read("/ChattelMortgage/GetChattelMortgage.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/ChattelMortgage/GetChattelMortgage?keyWord=惠州市帅星贸易有限公司");
        checkSim(source, target);
//        JSONArray list = checkListMatched("/ChattelMortgage/GetChattelMortgage?keyWord=惠州市帅星贸易有限公司");
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
     * @throws Exception
     */
    @Test
    public void GetDetailsByName() throws Exception {
        clearTable("QCC_DETAILS");
        JSONObject source = read("/ECIV4/GetDetailsByName.json?keyword=惠州市帅星贸易有限公司");
        JSONObject object = checkResult("/ECIV4/GetDetailsByName?keyword=惠州市帅星贸易有限公司");
        double sim = sim(object.toJSONString(0), source.toJSONString(0));
        assertTrue(sim > 0.7);
        int c = 1;
    }

    /**
     * 工商历史信息
     * @throws Exception
     */
    @Test
    public void GetHistorytEci() throws Exception {
        clearTable("QCC_HIS_ECI");
        for (Map.Entry<String, Object> entry : DeconstructService.HistorytEciMap.entrySet()) {
            clearTable((String) entry.getValue());
        }
        clearTable("QCC_HIS_ECI_EMPLOYEE_LIST_JOB");
        JSONObject source = read("/History/GetHistorytEci.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytEci?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }


    @Test
    public void GetHistorytInvestment() throws Exception {
        clearTable("QCC_HIS_INVESTMENT");
        JSONObject source = read("/History/GetHistorytInvestment.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytInvestment?fullName=惠州市帅星贸易有限公司");
        checkSim(source,target);
    }


    /**
     * 历史股东
     * @throws Exception
     */
    @Test
    public void GetHistorytShareHolder() throws Exception {
        clearTable("QCC_HIS_SHARE_HOLDER", "QCC_HIS_SHARE_HOLDER_DETAILS");
        JSONObject source = read("/History/GetHistorytShareHolder.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytShareHolder?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target, 0.6);
    }

    @Test
    public void GetHistoryShiXin() throws Exception {
        clearTable("QCC_HIS_SHIXIN");
        JSONObject source = read("/History/GetHistoryShiXin.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistoryShiXin?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistoryZhiXing() throws Exception {
        clearTable("QCC_HIS_ZHIXING");
        JSONObject source = read("/History/GetHistoryZhiXing.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistoryZhiXing?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }

    @Test
    public void GetHistorytCourtNotice() throws Exception {
        clearTable("QCC_HIS_COURT_NOTICE");
        JSONObject source = read("/History/GetHistorytCourtNotice.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytCourtNotice?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }
    @Test
    public void GetHistorytJudgement() throws Exception {
        clearTable("QCC_HIS_JUDGEMENT\n");
        JSONObject source = read("/History/GetHistorytJudgement.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytJudgement?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }
    @Test
    public void GetHistorytSessionNotice() throws Exception {
        clearTable("QCC_HIS_SESSION_NOTICE\n");
        JSONObject source = read("/History/GetHistorytSessionNotice.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytSessionNotice?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }
    @Test
    public void GetHistorytMPledge() throws Exception {
        clearTable("QCC_HIS_M_PLEDGE\n");
        JSONObject source = read("/History/GetHistorytMPledge.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytMPledge?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }
    @Test
    public void GetHistorytPledge() throws Exception {
        clearTable("QCC_HIS_PLEDGE\n");
        JSONObject source = read("/History/GetHistorytPledge.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytPledge?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }
    @Test
    public void GetHistorytAdminPenalty() throws Exception {
        clearTable("QCC_HIS_ADMIN_PENALTY_EL\n","QCC_HIS_ADMIN_PENALTY_CCL\n");
        JSONObject source = read("/History/GetHistorytAdminPenalty.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytAdminPenalty?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }
    @Test
    public void GetHistorytAdminLicens() throws Exception {
        clearTable("QCC_HIS_ADMIN_LICENS_EL\n", "QCC_HIS_ADMIN_LICENS_CCL\n");
        JSONObject source = read("/History/GetHistorytAdminLicens.json?keyWord=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/History/GetHistorytAdminLicens?fullName=惠州市帅星贸易有限公司");
        checkSim(source, target);
    }
    @Test
    public void SearchFresh() throws Exception {
        clearTable("QCC_FRESH");
        JSONObject source = read("/ECIV4/SearchFresh.json?keyword=惠州市帅星贸易有限公司");
        JSONObject target = checkResult("/ECIV4/SearchFresh?keyword=北京");
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
        assertEquals(result.getStr("Status"),"200");
        return result;
    }

    public JSONObject checkObjectMatched(String url) throws UnsupportedEncodingException {
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"),"200");
        JSONObject _result = result.getJSONObject("Result");
        assertTrue(_result.size() > 0);
        return _result;
    }

    public JSONArray checkListMatched(String url) throws UnsupportedEncodingException {
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"),"200");
        JSONArray array = result.getJSONArray("Result");
        assertTrue(result.size() > 0);
        return array;
    }

    public JSONArray checkPageMatched(String url) throws UnsupportedEncodingException {
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"),"200");
        int count = result.getByPath("Paging.TotalRecords", Integer.class);
        assertTrue(count > 0);
        return result.getJSONArray("Result");
    }

    public JSONObject huGet(String url) throws UnsupportedEncodingException {
        url = "http://localhost:8081/qcc" + url;
        if(url.contains("?")){
            int idex = url.indexOf("?");
            url = url.substring(0, idex) + "?" + URLUtil.encode(url.substring(idex + 1));
        }
        String str = HttpUtil.get(url);
        return JSONUtil.parseObj(str);
    }

    public JSONObject read(String url, Map<String,String> params) throws FileNotFoundException {
        if (params == null) {
           params = MapUtil.newHashMap();
        }
        String param = "";
        if(url.contains("?")){
            param = url.substring(url.indexOf("?"));
            url = url.substring(0, url.indexOf("?"));
        }
        String str = IoUtil.readUtf8(
            new RandomAccessFile("D:/work/cu" + url,"r").getChannel()
        );
        url = S.fmt("http://localhost:3033/qcc%s%s", url, param);
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.set("Proxy-Url", url);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(str.getBytes(StandardCharsets.UTF_8));
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, buf, headers, headers);
        deconstructService.doNettyRequest(null, request);
        return JSONUtil.parseObj(str);
    }
    public JSONObject read(String url) throws Exception {
        return read(url, null);
    }
    

    public int count(String table){
        try{
            String sql = S.fmt("select count(1) from %s", table);
            List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
            return list.get(0).getInt("1", 0);
        } catch (Exception e){
            e.printStackTrace();
            assertNull(e);
        }
        return 0;
    }

    public void checkCount(String table){
        assertTrue(count(table) > 0);
    }

    public void checkSim(JSONObject source, JSONObject target, double limit){
        source.remove("OrderNumber");
        String ss = source.toJSONString(0);
        String st = target.toJSONString(0);
        double sim = sim(ss,st);

        System.out.println(ss);
        System.out.println(st);
        System.out.println(sim);
        assertTrue(sim > 0.7);
    }
    public void checkSim(JSONObject source, JSONObject target){
        checkSim(source,target, 0.7);
    }


    public static void clearTable(String ...table){
        for (String s : table) {
            String sql = S.fmt("delete from %s", s);
            sqlManager.executeUpdate(new SQLReady(sql));
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
    private static byte[] getBytes (char[] chars) {
        Charset cs = Charset.forName ("UTF-8");
        CharBuffer cb = CharBuffer.allocate (chars.length);
        cb.put (chars);
        cb.flip ();
        ByteBuffer bb = cs.encode (cb);
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
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]+ temp);
            }
        }
        return d[n][m];
    }
    public static double sim(String str1, String str2) {
        try {
            double ld = (double)ld(str1, str2);
            return (1-ld/(double)Math.max(str1.length(), str2.length()));
        } catch (Exception e) {
            return 0.1;
        }
    }


}
