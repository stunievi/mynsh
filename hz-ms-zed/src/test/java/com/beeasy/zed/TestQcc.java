package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
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
import java.nio.charset.StandardCharsets;
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

        JSONObject object = read("/CourtAnnoV4/SearchCourtNotice.json?searchKey=惠州市帅星贸易有限公司");
        String id = object.getByPath("Result.0.Id", String.class);
        assertNotNull(id);
        read("/CourtAnnoV4/GetCourtNoticeInfo.json?id=" + id);

        checkPageMatched("/CourtAnnoV4/SearchCourtNotice?searchKey=惠州市帅星贸易有限公司");
        JSONObject obj = checkObjectMatched("/CourtAnnoV4/GetCourtNoticeInfo?id=" + id);
        assertTrue(obj.getJSONArray("Prosecutor").size() > 0);
        assertTrue(obj.getJSONArray("Defendant").size() > 0);
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
        read("/LandMortgage/GetLandMortgageList.json?keyWord=惠州市帅星贸易有限公司");
        String url = "/LandMortgage/GetLandMortgageList?keyWord=惠州市帅星贸易有限公司";
        JSONArray array = checkPageMatched(url);
        String id = array.getByPath("1.Id", String.class);
        assertNotNull(id);
        read("/LandMortgage/GetLandMortgageDetails.json?id=" + id);
        JSONObject object = checkObjectMatched("/LandMortgage/GetLandMortgageDetails?id=" + id);
        assertTrue(object.getJSONObject("MortgagorName").size() > 0);
        assertTrue(object.getJSONObject("MortgagePeople").size() > 0);
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
        read("/ChattelMortgage/GetChattelMortgage.json?keyWord=惠州市帅星贸易有限公司");
        JSONArray list = checkListMatched("/ChattelMortgage/GetChattelMortgage?keyWord=惠州市帅星贸易有限公司");
        String[] children = {"Pledge","PledgeeList","SecuredClaim", "GuaranteeList","CancelInfo", "ChangeList"};
        assertTrue(list.size() > 0);
        for (Object object : list) {
            JSONObject obj = (JSONObject) object;
            for (String child : children) {
                assertNotNull(obj.getByPath("Detail." + child, JSON.class));
            }
        }
    }
//    @Test
//    public void testGetJudgementDetail(){
//        try {
//            read("GetJudgementDetail.json");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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


    public static void clearTable(String ...table){
        for (String s : table) {
            String sql = S.fmt("delete from %s", s);
            sqlManager.executeUpdate(new SQLReady(sql));
        }
    }
}
