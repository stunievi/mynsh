package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sun.jndi.toolkit.url.UrlUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.S;
import sun.rmi.server.DeserializationChecker;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

        clearTable("QCC_SHIXIN");
        clearTable("QCC_ZHIXING");
        clearTable("QCC_JUDGMENT_DOC");
        clearTable("QCC_COURT_NOTICE");
        clearTable("QCC_COURT_ANNOUNCEMENT");
    }

    @Before
    public void onBefore(){
        HttpServerHandler.LastException = null;
    }

    @Test
    public void SearchShiXin() throws Exception {
        get("SearchShiXin.json?searchKey=小米");
        checkListMatched("/CourtV4/SearchShiXin?searchKey=小米");
    }

    @Test
    public void SearchZhiXing(){
        try {
            get("SearchZhiXing.json?searchKey=小米");
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void testSearchJudgmentDoc(){
        try {
            JSONObject object = get("SearchJudgmentDoc.json?searchKey=小米");
            String id = object.getByPath("Result.0.Id", String.class);
            assertNotNull(id);
            get("GetJudgementDetail.json?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void SearchCourtAnnouncement(){
        try{
            JSONObject object = get("SearchCourtAnnouncement.json");
            String id = object.getByPath("Result.0.Id", String.class);
            assertNotNull(id);
            get("SearchCourtAnnouncementDetail.json?id=" + id);
        } catch (Exception e){
            e.printStackTrace();
            assertNull(e);
        }
    }

    @Test
    public void SearchCourtNotice() {
        try {
            JSONObject object = get("SearchCourtNotice.json?searchKey=小米");
            String id = object.getByPath("Result.0.Id", String.class);
            assertNotNull(id);
            get("GetCourtNoticeInfo.json?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            assertNull(e);
        }

    }





//    @Test
//    public void testGetJudgementDetail(){
//        try {
//            get("GetJudgementDetail.json");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void checkListMatched(String url) throws UnsupportedEncodingException {
        JSONObject result = huGet(url);
        assertEquals(result.getStr("Status"),"200");
        int count = result.getByPath("Paging.TotalRecords", Integer.class);
        assertTrue(count > 0);
    }

    public JSONObject huGet(String url) throws UnsupportedEncodingException {
        url = "http://localhost:8081/qcc" + url;
        if(url.contains("?")){
            int idex = url.indexOf("?");
            url = url.substring(0, idex) + "?" + UrlUtil.encode(url.substring(idex + 1), "UTF-8");
        }
//        url = UrlUtil.encode(url, StandardCharsets.UTF_8.name());
        String str = HttpUtil.get(url);
        return JSONUtil.parseObj(str);
    }

    public JSONObject get(String url, Map<String,String> params) throws FileNotFoundException {
        if (params == null) {
           params = MapUtil.newHashMap();
        }
        String param = "";
        if(url.contains("?")){
            param = url.substring(url.indexOf("?"));
            url = url.substring(0, url.indexOf("?"));
        }
        String str = IoUtil.readUtf8(
            new RandomAccessFile("D:/work/cu/" + url,"r").getChannel()
        );
        url = S.fmt("http://localhost:3033/tt/%s%s", url, param);
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.set("Proxy-Url", url);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(str.getBytes(StandardCharsets.UTF_8));
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, buf, headers, headers);
        deconstructService.doNettyRequest(null, request);
        return JSONUtil.parseObj(str);
    }
    public JSONObject get(String url) throws Exception {
        return get(url, null);
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


    public static void clearTable(String table){
        String sql = S.fmt("delete from %s", table);
        try{
            sqlManager.executeUpdate(new SQLReady(sql));
        } catch (Exception e){
            e.printStackTrace();
            assertNull(e);
        }
    }
}
