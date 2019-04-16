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
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.IO;
import org.osgl.util.S;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
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
//            File f = new File("C:\\Users\\bin\\Documents\\WeChat Files\\llyb120\\FileStorage\\File\\2019-04\\bd1d46d2-b80d-4669-9b28-5b6081d2bfad.zip");
            File f = new File("C:\\Users\\bin\\Documents\\WeChat Files\\llyb120\\FileStorage\\File\\2019-04\\ec72a17e-164f-4beb-97c6-f3eedb18084f.zip");
//            f = new File("cubi.zip");
            unzipFile(f, temp);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        List<Slice> tasks = new ArrayList<>();
        byte[] buf = BufferPool.allocate();
        try(
            RandomAccessFile raf = new RandomAccessFile(temp, "r");
        ){
//            raf
            int pos = 0;
            int len = -1;
            while(true){
                if(pos >= raf.length()){
                    break;
                }
                raf.seek(pos);
                len = raf.readInt();
                pos += 4;
                raf.read(buf, 0, len);
                String link = new String(buf, 0, len);
                pos += len;
                len = raf.readInt();
                pos += 4;
                Slice slice = new Slice(link, pos, len, null);
                tasks.add(slice);
                pos += len;
            }

            ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()) {
                @Override
                protected void afterExecute(Runnable r, Throwable t) {
                    super.afterExecute(r, t);
                    if (t != null) {
                        t.printStackTrace();
                        System.exit(0);
                    }
                }
            };

            File finalTemp = temp;
            ThreadLocal<RandomAccessFile> local = new ThreadLocal<RandomAccessFile>(){
                private Vector<RandomAccessFile> files = new Vector();

                @Override
                protected RandomAccessFile initialValue() {
                    try {
                        RandomAccessFile raf = new RandomAccessFile(finalTemp, "r");
                        files.add(raf);
                        return raf;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                }


                @Override
                protected void finalize() throws Throwable {
                    for (RandomAccessFile file : files) {
                        file.close();
                    }
                    super.finalize();
                }
            };

            CountDownLatch cl = new CountDownLatch(tasks.size());
            for (Slice slice : tasks) {
                executor.execute(() -> {
                    String str = null;
                    RandomAccessFile file = local.get();
                    byte[] bs = BufferPool.allocate();
                    try {
                        file.seek(slice.bodyStart);
                        file.read(bs, 0, (int) slice.bodyLen);
                        str = new String(bs, 0, slice.bodyLen);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        BufferPool.deallocate(bs);
                    }
                    if (str != null) {
                        testDe(slice.link, str);
                        cl.countDown();
                    }
                });
            }


            cl.await();

        } catch (IOException e) {
            Assert.assertNull(e);
            e.printStackTrace();
        } finally {
            BufferPool.deallocate(buf);
            if (temp != null) {
               temp.delete();
            }
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
                cn.hutool.json.JSONObject od = JSONUtil.parseObj(object.getString("OriginData"));
                if(S.eq(od.getStr("Status"), "200")){
                    handler.call(null, request, (cn.hutool.json.JSON) od.get("Result"));
                }
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


    public static class Slice{
        String link;
        long bodyStart;
        int bodyLen;
        String body;

        public Slice(String link, long bodyStart, int bodyLen, String body) {
            this.link = link;
            this.bodyStart = bodyStart;
            this.bodyLen = bodyLen;
            this.body = body;
        }
    }

}
