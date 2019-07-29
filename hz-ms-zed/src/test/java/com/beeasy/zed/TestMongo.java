package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.beetl.sql.core.SQLReady;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.E;
import org.osgl.util.S;

import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.beeasy.zed.DBService.dataSource;
import static com.beeasy.zed.DBService.sqlManager;

public class TestMongo {
    private static MongoService mongoService = new MongoService();
    private static DeconstructService deconstructService = new DeconstructService();
    public static ConcurrentMap<String, String> concurrentMapWordCounts = new ConcurrentHashMap<>();

    @BeforeClass
    public static void onBefore() throws ExecutionException, InterruptedException {
        mongoService.start();
        DBService.getInstance().initSync();
        deconstructService.initSync();
        MQService.getInstance().initSync();
        vvv();
    }

    public  static void vvv(){
        JSONObject jsonObject = JSONObject.parseObject(Utils.readFile().toString());
        JSONArray array = jsonObject.getJSONArray("Result");
        for (Object o : array) {
            JSONObject j = JSONObject.parseObject(o.toString());
            concurrentMapWordCounts.put(j.getString("Code"), j.getString("ProvinceName"));
        }
    }

    @AfterClass
    public static void onAfter() {
        mongoService.close();
    }


    @Test
    public void clearTables() {
        String sql = "select  tabname from syscat.tables where tabschema='DB2INST1' and tabname like 'QCC_%'\n";
        List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
        try {
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            for (JSONObject object : list) {
                String s = "delete from " + object.getString("tabname");
                stmt.addBatch(s);
            }
            stmt.executeBatch();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void testDe(String url, String str) {
        JSONObject object = JSON.parseObject(str);
//        String url = object.getString("FullLink");
        for (Map.Entry<String, DeconstructService.DeconstructHandler> entry : DeconstructService.handlers.entrySet()) {
            if (HttpServerHandler.matches(url, entry.getKey())) {
                DeconstructService.DeconstructHandler handler = entry.getValue();
                ByteBuf buf = Unpooled.buffer();
                DefaultHttpHeaders headers = new DefaultHttpHeaders();
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, buf, headers, headers);
                JSONObject od = (object.getJSONObject("OriginData"));
                if (S.eq(od.getString("Status"), "200")) {
                    handler.call(null, request, (JSON) od.get("Result"));
                }
            }
        }
    }


    @Test
    public void testSingleFile() throws FileNotFoundException, InterruptedException {
        deconstructService. onDeconstructRequest("1", "2", new FileInputStream("C:\\Users\\DELL\\Desktop\\load-qcc05db5664-208d-4399-8763-11221c0b212d.zip"));
    }

    @Test
    public void importe(){
//        File file = new File("C:\\Users\\DELL\\Desktop\\zip");
        File file = new File("C:\\Users\\DELL\\Desktop\\datalog\\zip");
        List<File> files = new ArrayList<>();
        walk(files, file);

        files = files.stream()
                .sorted(new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return Long.compare(o1.lastModified(), o2.lastModified());
                    }
                })
                .collect(Collectors.toList());
        for (File f : files) {
            try {
                deconstructService.onDeconstructRequest("1", "2", new FileInputStream(f));//;"C:\\Users\\DELL\\Desktop\\flss-load-qcc528da1.zip"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        int d = 1;

    }

    private void walk(List<File> files, File file){
        for (File listFile : file.listFiles()) {
            if(listFile.isDirectory()){
                walk(files, listFile);
            } else {
                files.add(listFile);
            }
        }
    }


    @Test
    public void updateProvinde() {
//        File file = new File("D:\\mynsh\\mynsh\\hz-ms-zed\\src\\main\\resources\\fixed.txt");
        String sql = "select name,province from  QCC_DETAILS";
        List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
        System.out.println(App.concurrentMapWordCounts);
        for (int i = 371; i < list.size(); i++) {
            if (null != list.get(i).getString("province") && null != App.concurrentMapWordCounts.get(list.get(i).getString("province")) && !App.concurrentMapWordCounts.get(list.get(i).getString("province")).equals("")) {
                String updateSql = "update QCC_DETAILS set province = '"+App.concurrentMapWordCounts.get(list.get(i).getString("province")) +"'  where  name = '"+ list.get(i).getString("name")+ "'";
                sqlManager.executeUpdate(new SQLReady(updateSql));
            }
        }
    }




}
