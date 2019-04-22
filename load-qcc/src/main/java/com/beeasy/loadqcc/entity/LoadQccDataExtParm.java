package com.beeasy.loadqcc.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadQccDataExtParm {

    public static String LOAD_TXT_PATH;
    public static String LOAD_ZIP_PATH;

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }
    // 触发模式
    private String trigger;

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }
    // 指令id
    private String commandId;

    public String getCommond() {
        return commond;
    }

    public void setCommond(String commond) {
        this.commond = commond;
    }

    private String commond;

    public RandomAccessFile getRaf() {
        return raf;
    }

    public void setRaf(RandomAccessFile raf) {
        this.raf = raf;
    }

    private RandomAccessFile raf;

    public File getQccFileDataPath() {
        return qccFileDataPath;
    }

    public void setQccFileDataPath(File qccFileDataPath) {
        this.qccFileDataPath = qccFileDataPath;
    }

    private File qccFileDataPath;

    public File getQccZipDataPath() {
        return qccZipDataPath;
    }

    public void setQccZipDataPath(File qccZipDataPath) {
        this.qccZipDataPath = qccZipDataPath;
    }

    private File qccZipDataPath;

    private static void setQccDataPath(
            LoadQccDataExtParm param
    ){
        String commandId = param.getCommandId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File filePath = new File(LOAD_TXT_PATH , sdf.format(new Date()));
        File zipPath = new File(LOAD_ZIP_PATH , sdf.format(new Date()));
        zipPath.mkdirs();
        filePath.mkdirs();
        File __file = new File(filePath,commandId + ".txt");
        File __zip = new File(zipPath,commandId + ".zip");
        param.setQccFileDataPath(__file);
        param.setQccZipDataPath(__zip);
    }
    // 手动更新企查查数据
    public static LoadQccDataExtParm manul(String commandId){
        LoadQccDataExtParm param = new LoadQccDataExtParm();
        param.setCommandId(commandId);
        param.setTrigger("manual");
        setQccDataPath(param);
        return param;
    }
    // 自动更新企查查数据
    public static LoadQccDataExtParm automatic(String command){
        JSONObject commandObj = JSON.parseObject(command);
        LoadQccDataExtParm param = new LoadQccDataExtParm();
        param.setCommandId(commandObj.getString("OrderId"));
        param.setCommond(command);
        param.setTrigger("automatic");
        setQccDataPath(param);
        return param;
    }
}
