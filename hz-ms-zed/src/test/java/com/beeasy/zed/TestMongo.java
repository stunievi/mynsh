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
    public static void onBefore(){
        mongoService.start();
        DBService.init(true);
        deconstructService = DeconstructService.register();
        deconstructService.autoCommit = false;
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
//            File f = new File("C:\\Users\\bin\\Documents\\WeChat Files\\llyb120\\FileStorage\\File\\2019-04\\ec72a17e-164f-4beb-97c6-f3eedb18084f.zip");
//            File f = new File("C:\\Users\\bin\\Documents\\WeChat Files\\llyb120\\FileStorage\\File\\2019-04\\60608e86-0b4d-4e8e-bfc1-736f0bbbe848.zip");
            File f = new File("C:\\Users\\bin\\Documents\\WeChat Files\\llyb120\\FileStorage\\File\\2019-04\\91d50bc5-1e02-4f68-a832-53920c5dfe28.zip");
            unzipFile(f, temp);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        List<DeconstructService.Slice> tasks = new ArrayList<>();
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
                DeconstructService.Slice slice = new DeconstructService.Slice(link, pos, len, null);
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
                        try{
                            file.close();
                        }finally {
                        }
                    }
                    super.finalize();
                }
            };

            CountDownLatch cl = new CountDownLatch(tasks.size());
            for (DeconstructService.Slice slice : tasks) {
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
                        DSTransactionManager.start();
                        try {
                            testDe(slice.link, str);
                            DSTransactionManager.commit();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            try {
                                DSTransactionManager.rollback();
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                            System.exit(-1);
                        }
                        cl.countDown();
                    }
                });
            }


            cl.await();
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
//                conn.setAutoCommit(false);
//                Statement stmt = conn.createStatement();
//                System.out.println("开始插入");
//                long stime = System.currentTimeMillis();
//                for (String s : deconstructService.readySqls.delete) {
//                    stmt.addBatch(s);
//                }
//                for (String s : deconstructService.readySqls.insert) {
//                    stmt.addBatch(s);
//                }
//                for (String s : deconstructService.readySqls.update) {
//                    stmt.addBatch(s);
//                }
//                stmt.executeBatch();
//                conn.commit();

//                System.out.println("插入时间:" + (System.currentTimeMillis() - stime));

                ;
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            int c = 1;

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




}
