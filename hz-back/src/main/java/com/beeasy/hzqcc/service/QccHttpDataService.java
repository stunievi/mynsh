package com.beeasy.hzqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.SysVar;
import com.beeasy.hzback.modules.system.service.QccHistLogAsyncService;
import com.beeasy.mscommon.util.OkHttpUtil;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class QccHttpDataService {

    // http://47.94.97.138/qcc/
    public static String QCC_HTTP_DATA_PRX;

    @Autowired
    SQLManager sqlManager;
    @Autowired
    QccHistLogAsyncService qccHistLogAsyncService;

    @PostConstruct
    public void onInit(){
        SysVar sysVar = sqlManager.lambdaQuery(SysVar.class)
                .andEq(SysVar::getVarName, "qcc_url")
                .single();
        if (sysVar != null) {
            QCC_HTTP_DATA_PRX = sysVar.getVarValue();
            qccHistLogAsyncService.onInit(QCC_HTTP_DATA_PRX);
        }
    }

    JSONObject getQccData(
            String collName,
            Map param
    ){
        collName = collName.replace("_","/");
        String res = OkHttpUtil.get( QCC_HTTP_DATA_PRX + collName, param);
        return JSON.parseObject(res);
    }

    JSONObject findOne(
        String collName,
        Map param
    ){
        return getQccData(collName, param);
    }

    JSONObject getDataList(
            String collName,
            Map param
    ){
        return getQccData(collName, param);
    }

    JSONObject getSplitChildDataList(
            String collName,
            Map param
    ){
        return getQccData(collName, param);
    }
}
