package com.beeasy.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.beetl.sql.core.SQLReady;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.S;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.beeasy.zed.DBService.dataSource;
import static com.beeasy.zed.DBService.sqlManager;

public class TestMongo {
    private static MongoService mongoService = new MongoService();
    private static DeconstructService deconstructService = new DeconstructService();
    private static ZedService zedService = new ZedService();

    @BeforeClass
    public static void onBefore() throws ExecutionException, InterruptedException {
        mongoService.start();
        DBService.getInstance().initSync();
        deconstructService.initSync();
    }

    @AfterClass
    public static void onAfter(){
        mongoService.close();
    }



    @Test
    public void clearTables(){
        String sql = "select  tabname from syscat.tables where tabschema='DB2INST1' and tabname like 'QCC_%'\n";
        List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
        try {
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            for (JSONObject object : list) {
                String s = "delete from " + object.getString("tabname") ;
                stmt.addBatch(s);
            }
            stmt.executeBatch();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void testDe(String url, String str){
        JSONObject object= JSON.parseObject(str);
//        String url = object.getString("FullLink");
        for (Map.Entry<String, DeconstructService.DeconstructHandler> entry : DeconstructService.handlers.entrySet()) {
            if(HttpServerHandler.matches(url, entry.getKey())){
                DeconstructService.DeconstructHandler handler = entry.getValue();
                ByteBuf buf = Unpooled.buffer();
                DefaultHttpHeaders headers = new DefaultHttpHeaders();
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, buf, headers, headers);
                JSONObject od = (object.getJSONObject("OriginData"));
                if(S.eq(od.getString("Status"), "200")){
                    handler.call(null, request, (JSON) od.get("Result"));
                }
            }
        }
    }


    @Test
    public void testSingleFile() throws FileNotFoundException, InterruptedException {
        deconstructService.onDeconstructRequest("1","2", new FileInputStream("C:\\Users\\bin\\Desktop\\qcc_hz_cus_com_data\\20190508\\jyfx-load-qcca4524d43-bde2-4843-acad-3e7b807d967c.zip"));
    }


    public void testSingleJson() throws NoSuchMethodException, SQLException {
        String path = "/ECIV4/GetDetailsByName";
        String compName = "";
        JSONObject json = null;
        DeconstructService.DeconstructHandler handler = deconstructService.handlers.get(path);
        ByteBuf buf = Unpooled.buffer();
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://www.baidu.com" + path + "?keyword=" + compName, buf, headers, headers);

        handler.call(null, request, json);
        deconstructService.deconstructStep3(deconstructService.readySqls);

    }



}
