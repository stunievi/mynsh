package com.beeasy.zed;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.http.*;
import org.apache.activemq.BlobMessage;
import org.beetl.ext.fn.StringUtil;
import org.beetl.sql.core.DSTransactionManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.ext.gen.GenConfig;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.IO;
import org.osgl.util.S;
import sun.tracing.dtrace.DTraceProviderFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

import static com.beeasy.zed.DBService.*;
import static com.beeasy.zed.Utils.unzipFile;

public class TestMongo {
    private static MongoService mongoService = new MongoService();
    private static DeconstructService deconstructService;
    private static ZedService zedService = new ZedService();

    @BeforeClass
    public static void onBefore() throws ExecutionException, InterruptedException {
        mongoService.start();
        DBService.init(true);
        DBService.await();
        deconstructService = DeconstructService.register();
        deconstructService.autoCommit = false;

    }

    @AfterClass
    public static void onAfter(){
        mongoService.close();
    }






    @Test
    public void clearTables(){
        String sql = "select  tabname from syscat.tables where tabschema='DB2INST1'\n";
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
    public void testSingleFile() throws FileNotFoundException {
        deconstructService.onDeconstructRequest("1","2", new FileInputStream("d:/load-qccd0586ad9-6379-418b-82d8-2b896704d866"));
    }



}
