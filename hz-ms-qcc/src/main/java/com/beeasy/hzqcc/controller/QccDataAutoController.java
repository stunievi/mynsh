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
public class QccDataAutoController {

    @Autowired
    QccService qccService;

    @RequestMapping(value = "/{$model}/{$action}", method = RequestMethod.GET)
    Result aaaaa(
            @PathVariable String $model
            , @PathVariable String $action
            , @RequestParam Map<String,Object> params
    ) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class clazz = qccService.getClass();
        Method method = clazz.getDeclaredMethod($model.concat("_").concat($action), Map.class);
        Object data = method.invoke(qccService,params);
        return Result.ok(
                data
        );
    }
}
