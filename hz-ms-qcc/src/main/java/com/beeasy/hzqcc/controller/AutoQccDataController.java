package com.beeasy.hzqcc.controller;

import com.beeasy.hzqcc.service.QccService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@RestController
@RequestMapping("/qcc/auto")
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
            Object data = method.invoke(qccService,params, false);
            return Result.ok(
                    data
            );
        }catch (NoSuchMethodException e){
            return Result.error("方法不存在");
        }

    }
}
