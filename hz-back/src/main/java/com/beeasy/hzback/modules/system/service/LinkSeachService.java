package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.mscommon.util.U;
import lombok.AllArgsConstructor;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.util.C;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LinkSeachService {

    /**
     * 股东清单查询
     */
    public PageQuery<JSONObject> gdSeach(String no,
         Map<String, Object> params){
        PageQuery<com.alibaba.fastjson.JSONObject> pageQuery = U.beetlPageQuery("accloan." + no, JSONObject.class, params);
        return pageQuery;
    }

    static String text_path = "D:\\java projects\\hznsh\\hz-back\\src\\main\\resources\\link\\text";
    static String zip_path = "D:\\java projects\\hznsh\\hz-back\\src\\main\\resources\\link\\zip";

    public static JSONObject creatLinkParam(
            String sign,
            long uid
    ){
        // 11：关联方清单；12：股东清单；13：集团客户清单；14：股东关联清单；
        JSONObject retOjb = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式
        Date nowDate = new Date();
        File filePath = new File(text_path , sdf.format(nowDate));
        File zipPath = new File(zip_path , sdf.format(nowDate));
        String orderId = "fzsys_link_" + sdf2.format(nowDate);
        zipPath.mkdirs();
        filePath.mkdirs();
        File __file = new File(filePath,"fzsys_link_" + orderId + ".txt");
        File __zip = new File(zipPath,"fzsys_link_" + orderId + ".zip");
        retOjb.put("OrderId", orderId);
        retOjb.put("Sign", sign);
        retOjb.put("txtPath", __file);
        retOjb.put("zipPath", __zip);
        retOjb.put("uid", uid);
        retOjb.put("Content", C.newList());
        return retOjb;
    }

    // 逐条写入数据，直到data为false，停止写入，然后压缩文件
    public void writeLinkData(
            JSONObject param,
            Object data
    ){
        if(data.equals(false)){
            this.zipLinkData(param);
            return;
        }
        long uid = param.getLong("uid");
        FileLock lock = null;
        File file = (File) param.get("txtPath");
        try (
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                FileChannel channel = raf.getChannel();
        ){
            lock = channel.lock();
            String str = JSON.toJSONString(data);
            byte[] bs = (str.getBytes(StandardCharsets.UTF_8.name()));
            //写入数据包长度
            raf.writeInt(bs.length);
            raf.write(bs);
        } catch (IOException e) {
            // 写入文件失败则忽略此次响应
            Log.logByUid("关联写入失败", uid);
            return;
        }
        finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        };
    }

    // 压缩文件
    private static void  zipLinkData(
            JSONObject param
    ){
        File __file = (File) param.get("txtPath");
        File __zip = (File) param.get("zipPath");
        try(
                FileInputStream fis = new FileInputStream(__file);
                FileOutputStream fos = new FileOutputStream(__zip);
                ZipOutputStream zip = new ZipOutputStream(fos);
        ) {
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(__file.length());
            zip.putNextEntry(entry);
            byte[] bs = new byte[1024];
            int len = -1;
            while((len = fis.read(bs)) > 0){
                zip.write(bs, 0, len);
            }
        }catch (IOException e){
            return;
        }
    }

    @AllArgsConstructor
    public static class LinkRequest{
        public String OrderId;
        public String Sign;
        public File Content;
    }

}
