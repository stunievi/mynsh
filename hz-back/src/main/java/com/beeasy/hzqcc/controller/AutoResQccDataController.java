package com.beeasy.hzqcc.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzqcc.service.QccHttpDataService;
import com.beeasy.hzqcc.service.QccService;
import com.beeasy.tool.OkHttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 完全原样返回企查查数据；若需要返回稍做处理后的数据，需在请求时包含getOriginData字段
 */
@RestController
@RequestMapping("/api/qcc/autoRes")
public class AutoResQccDataController {

    @Autowired
    QccService qccService;

    @RequestMapping(value = "/{$model}/{$action}", method = RequestMethod.GET)
    String autoResQccData(
            @PathVariable String $model
            , @PathVariable String $action
            , @RequestParam Map<String,String> params
    ) throws IllegalAccessException, InvocationTargetException {

        try {
            return OkHttpUtil.get(QccHttpDataService.QCC_HTTP_DATA_PRX + $model + "/" + $action, params);
        } catch (IOException e) {
            JSONObject data = new JSONObject();
            data.put("Status", "500");
            data.put("Message", "请求异常");
            return data.toJSONString();
//            e.printStackTrace();
        }

//        Class clazz = qccService.getClass();
//        try {
//            Method method = clazz.getDeclaredMethod($model.concat("_").concat($action), Map.class, boolean.class);
//            Object data = method.invoke(qccService,params, true);
//            return (JSONObject) JSON.toJSON(data);
//        }catch (NoSuchMethodException e){
//            JSONObject data = new JSONObject();
//            data.put("Status", "201");
//            data.put("Message", "方法不存在");
//            return data;
//        }
    }
}
