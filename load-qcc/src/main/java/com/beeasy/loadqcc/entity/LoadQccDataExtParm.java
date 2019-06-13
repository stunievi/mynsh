package com.beeasy.loadqcc.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.bson.Document;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static javafx.scene.input.KeyCode.V;

@Getter
@Setter
public class LoadQccDataExtParm {

    public static String LOAD_TXT_PATH;
    public static String LOAD_ZIP_PATH;

    // 触发模式
    private String trigger;
    // 指令id
    private String commandId;
    // 指令
    private String command;
    // 数据ID
    private String resDataId;
    // txt文件路径
    private File qccFileDataPath;
    // 压缩包文件路径
    private File qccZipDataPath;
    // 公司名
//    private String companyName;
    private static ThreadLocal<String> cLocal = new ThreadLocal<>();
    private Vector<Document> cacheArr = new Vector<>();
    private int companyCount = 0;
    // api调用次数统计
    private Map tongJiObj = new HashMap();
    // 发生异常的api
    private Map<String, ConcurrentHashMap> errorApi = new ConcurrentHashMap<>();

    public synchronized void setErrorApi(String collName, String status){
        if(errorApi.containsKey(collName)){
            Map item = errorApi.get(collName);
            if(item.containsKey(status)){
                ((AtomicInteger) item.get(status)).incrementAndGet();
            }else{
                AtomicInteger atomicInteger = new AtomicInteger(1);
                item.put(status, atomicInteger);
            }
        }else{
            AtomicInteger atomicInteger = new AtomicInteger(1);
            ConcurrentHashMap states = new ConcurrentHashMap<>();
            states.put(status, atomicInteger);
            errorApi.put(collName, states);
        }
    }

    public void setTongJiObj(String collName, int count){
        if(tongJiObj.containsKey(collName)){
            ((AtomicInteger) tongJiObj.get(collName)).updateAndGet(n->n+count);
        }else{
            AtomicInteger atomicInteger = new AtomicInteger(count);
            tongJiObj.put(collName, atomicInteger);
        }
    }
    public void setTongJiObj(String collName){
        setTongJiObj(collName, 1);
    }
    public int getTongJi(String collName){
        if(tongJiObj.containsKey(collName)){
            return ((AtomicInteger) tongJiObj.get(collName)).intValue();
        }else{
            return 0;
        }
    }

    private static void setQccDataPath(
            LoadQccDataExtParm param
    ){
        String resDataId = param.getResDataId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File filePath = new File(LOAD_TXT_PATH , sdf.format(new Date()));
        File zipPath = new File(LOAD_ZIP_PATH , sdf.format(new Date()));
        zipPath.mkdirs();
        filePath.mkdirs();
        File __file = new File(filePath,resDataId + ".txt");
        File __zip = new File(zipPath,resDataId + ".zip");
        param.setQccFileDataPath(__file);
        param.setQccZipDataPath(__zip);
    }
    // 更新企查查数据
    public static LoadQccDataExtParm automatic(String command){
        JSONObject commandObj = JSON.parseObject(command);
        LoadQccDataExtParm param = new LoadQccDataExtParm();
        if(null == commandObj.getJSONArray("OrderData")){
            param.setCompanyCount(0);
        }else{
            param.setCompanyCount(commandObj.getJSONArray("OrderData").size());
        }
        param.setResDataId("load-qcc"+ UUID.randomUUID());
        param.setCommandId(commandObj.getString("OrderId"));
        param.setCommand(command);
        param.setTrigger("automatic");
        setQccDataPath(param);
        return param;
    }

    public String getCompanyName() {
        return cLocal.get();
    }

    public void setCompanyName(String companyName) {
        cLocal.set(companyName);
    }
}
