package com.beeasy.hzqcc.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzqcc.service.QccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 完全原样返回企查查数据；若需要返回稍做处理后的数据，需在请求时包含getOriginData字段
 */
@RestController
@RequestMapping("/qcc/autoRes")
public class AutoResQccDataController {

    @Autowired
    QccService qccService;

    @RequestMapping(value = "/{$model}/{$action}", method = RequestMethod.GET)
    JSONObject autoQccData(
            @PathVariable String $model
            , @PathVariable String $action
            , @RequestParam Map<String,Object> params
    ) throws IllegalAccessException, InvocationTargetException {
        Class clazz = qccService.getClass();
        try {
            // 兼容完全透传企查查数据
            if(params.containsKey("getOriginData")){
                params.put("getOriginData", "false");
            }else{
                params.put("getOriginData", "true");
            }
            Method method = clazz.getDeclaredMethod($model.concat("_").concat($action), Map.class, boolean.class);
            Object data = method.invoke(qccService,params, true);
            return (JSONObject) JSON.toJSON(data);
        }catch (NoSuchMethodException e){
            JSONObject data = new JSONObject();
            data.put("Status", "201");
            data.put("Message", "方法不存在");
            return data;
        }
    }
}
