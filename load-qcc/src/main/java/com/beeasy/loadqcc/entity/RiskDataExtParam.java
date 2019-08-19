package com.beeasy.loadqcc.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class RiskDataExtParam {

    public static String LOAD_RISK_TXT_PATH;
    public static String LOAD_RISK_ZIP_PATH;

    private File qccFileDataPath;
    // 压缩包文件路径
    private File qccZipDataPath;
    //    orderNo	String	订单号
    String orderNo;
    //    downloadUrl	String	下载压缩包地址
    String downloadUrl;
    //    downloadUrlExpiredTime	String	下载压缩包地址失效时间
    String downloadUrlExpiredTime;
    //    unzipPassword	String	压缩包解压密码
    String unzipPassword;

    public RiskDataExtParam(JSONObject data) {
        String orderNo = data.getString("orderNo");
        this.setOrderNo(data.getString("orderNo"));
        this.setDownloadUrl(data.getString("downloadUrl"));
        this.setDownloadUrlExpiredTime(data.getString("downloadUrlExpiredTime"));
        this.setUnzipPassword(data.getString("unzipPassword"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File filePath = new File(LOAD_RISK_TXT_PATH , sdf.format(new Date()));
        File zipPath = new File(LOAD_RISK_ZIP_PATH , sdf.format(new Date()));
        zipPath.mkdirs();
        filePath.mkdirs();
        File __file = new File(filePath,orderNo + ".txt");
        File __zip = new File(zipPath,orderNo + ".zip");
        this.setQccFileDataPath(__file);
        this.setQccZipDataPath(__zip);
    }

}
