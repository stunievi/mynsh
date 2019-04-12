package com.beeasy.hzqcc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import com.beeasy.mscommon.util.OkHttpUtil;

import java.util.Map;

@Service
public class QccHttpDataService {

    private String QCC_HTTP_DATA_PRX = "http://47.94.97.138/qcc/";

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
