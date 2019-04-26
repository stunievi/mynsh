package com.beeasy.loadqcc.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
    // 文件写入状态是否失败
    private boolean writeTxtFileState;
    // 公司名
    private String companyName;
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
        param.setResDataId("load-qcc"+ UUID.randomUUID());
        param.setCommandId(commandObj.getString("OrderId"));
        param.setCommand(command);
        param.setWriteTxtFileState(true);
        param.setTrigger("automatic");
        setQccDataPath(param);
        return param;
    }
}
