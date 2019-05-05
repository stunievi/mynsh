package com.beeasy.hzqcc.controller;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzqcc.service.QccService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@RestController
@RequestMapping("/api/qcc/auto")
public class AutoQccDataController {

    @Autowired
    QccService qccService;

    @RequestMapping(value = "/{$model}/{$action}", method = RequestMethod.GET)
    Result autoQccData(
            @PathVariable String $model
            , @PathVariable String $action
            , @RequestParam Map<String,Object> params
    ) throws IllegalAccessException, InvocationTargetException {
        try {
            Class clazz = qccService.getClass();
            Method method = clazz.getDeclaredMethod($model.concat("_").concat($action), Map.class, boolean.class);
            JSONObject data = (JSONObject) method.invoke(qccService,params, false);
            if(null == data || data.isEmpty()){
                return Result.error("查询不到数据");
            }else{
                return Result.ok(
                        data
                );
            }
        }catch (NoSuchMethodException e){
            return Result.error("方法不存在");
        }

    }

}
