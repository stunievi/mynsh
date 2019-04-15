package com.beeasy.zed;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.http.*;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.IO;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.zip.*;

public class TestMongo {
    private static MongoService mongoService = new MongoService();
    private static DeconstructService deconstructService = new DeconstructService();
    private static ZedService zedService = new ZedService();

    @BeforeClass
    public static void onBefore(){
        mongoService.start();
        zedService.initConfig();
        zedService.initDB(true);
        DeconstructService.register(zedService);
    }

    @AfterClass
    public static void onAfter(){
        mongoService.close();
    }


    @Test
    public void testEncode(){
        MongoCollection<Document> col = mongoService.getCollection("databaseName", "Hit_Log");
        Vector<Document> vector = col.find().into(new Vector<>());
        FileLock lock = null;
        long stime = (System.currentTimeMillis());
        File file = new File("cubi.bin");
        try(
 RandomAccessFile           raf = new RandomAccessFile(file, "rw");
            FileChannel channel = raf.getChannel();
            ) {
            lock = channel.lock();
            for (Document document : vector) {
                String str = JSON.toJSONString(document);
                byte[] bs = (str.getBytes(StandardCharsets.UTF_8.name()));
                //写入数据包长度
                raf.writeInt(bs.length);
                raf.write(bs);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - stime);
        //压缩
        try {
            zipFile(file, new File("cubi.zip"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - stime);
    }

    @Test
    public void testDecode() throws InterruptedException {
        File temp = null;
        try {
            temp = (Files.createTempFile("", "")).toFile();
            File f = new File("C:\\Users\\bin\\Documents\\WeChat Files\\llyb120\\FileStorage\\File\\2019-04\\2f91f5f2-380f-4ade-aabd-e5dd22b9a6c4.zip");
//            f = new File("cubi.zip");
            unzipFile(f, temp);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int n = 0;
        ExecutorService executor = ThreadUtil.newExecutor(8);
        List<String> tasks = new ArrayList<>();
        try(
            RandomAccessFile raf = new RandomAccessFile(temp, "r");
        ){
            int pos = 0;
            int len = -1;
            byte[] bs = new byte[1024000];
            while(true){
                if(pos >= raf.length()){
                    break;
                }
                raf.seek(pos);
                len = raf.readInt();
                raf.read(bs, 0, len);
                String str =
                    new String(bs, 0, len);
                ++n;
                tasks.add(str);

                pos += (4 + len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (temp != null) {
               temp.delete();
            }
        }
        CountDownLatch cl = new CountDownLatch(tasks.size());
        for (String str : tasks) {
            executor.execute(() -> {
//                System.out.println(
//                    str
//                );
                testDe(str);
                cl.countDown();
            });
        }

        cl.await();
    }

    public void testDe(String str){
        JSONObject object = JSON.parseObject(str);
        String url = object.getString("FullLink");
        for (Map.Entry<String, DeconstructService.DeconstructHandler> entry : DeconstructService.handlers.entrySet()) {
            if(HttpServerHandler.matches(url, entry.getKey())){
                DeconstructService.DeconstructHandler handler = entry.getValue();
                ByteBuf buf = Unpooled.buffer();
                DefaultHttpHeaders headers = new DefaultHttpHeaders();
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, buf, headers, headers);
                handler.call(null, request, (cn.hutool.json.JSON) JSONUtil.parseObj(object.getString("OriginData")).get("Result"));
            }
        }
    }

    /***
     * 压缩Zip
     *
     * @param data
     * @return
     */
    public static void zipFile(File src, File dest) throws IOException {
        try(
            FileInputStream fis = new FileInputStream(src);
            FileOutputStream fos = new FileOutputStream(dest);
            ZipOutputStream zip = new ZipOutputStream(fos);
            ) {
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(src.length());
            zip.putNextEntry(entry);
            byte[] bs = new byte[1024];
            int len = -1;
            while((len = fis.read(bs)) > 0){
                zip.write(bs, 0, len);
            }
        }
    }
    public static void unzipFile(File src, File dest) throws IOException {
        try(
            FileInputStream fis = new FileInputStream(src);
            ZipInputStream zip = new ZipInputStream(fis);
            FileOutputStream fos = new FileOutputStream(dest);
        ) {
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    fos.write(buf, 0, num);
                }
            }
        }
    }


}
