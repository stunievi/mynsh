package com.beeasy.loadqcc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.loadqcc.service.GetOriginQccService;
import com.beeasy.loadqcc.service.MongoService;
import com.beeasy.mscommon.Result;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@RestController
@RequestMapping("/qcc/loadData")
public class QccDataController {

    @Autowired
    MongoService mongoService;
    @Autowired
    GetOriginQccService getOriginQccService;

    @GetMapping(value = "/getAllQccData")
    Result ECI_GetDetailsByName(
           @RequestParam("keyword") String keyword
    ){
        getOriginQccService.loadAllData(keyword);
        return Result.ok(keyword);
    }
    // 手动触发更新数据
    @RequestMapping(value = "/{$model}/{$action}", method = RequestMethod.GET)
    Result autoQccData(
            @PathVariable String $model
            , @PathVariable String $action
            , @RequestParam("keyWord") String keyWord
    ) throws IllegalAccessException, InvocationTargetException {

        Document comInfo = mongoService.getCollection("ECIV4_GetDetailsByName").find(Filters.eq("QueryParam.keyWord", keyWord)).first();
        JSONObject loadComInfo = (JSONObject) JSON.toJSON(comInfo);
        loadComInfo = loadComInfo.getJSONObject("Result");
        if(null == loadComInfo || loadComInfo.size() <1){
            loadComInfo = getOriginQccService.ECI_GetDetailsByName(keyWord);
            if(null == loadComInfo || loadComInfo.size()<0){
                return  Result.error("未找到");
            }
        }
        Class clazz = getOriginQccService.getClass();

        String companyName = loadComInfo.getString("Name");
        if(null == companyName || "".equals(companyName)){
            return Result.ok("公司名不存在");
        }
        String keyNo = loadComInfo.getString("KeyNo");
        List employees = loadComInfo.getJSONArray("Employees");
        String methodStr = $model.concat("_").concat($action);
        try {
            if(methodStr.equals("CIAEmployeeV4_GetStockRelationInfo")){
                // 企业人员董监高信息
                for(short i=0;i<employees.size();i++){
                    JSONObject item = (JSONObject) employees.get(i);
                    Method method = clazz.getDeclaredMethod("CIAEmployeeV4_GetStockRelationInfo", String.class, String.class);
                    method.invoke(getOriginQccService, companyName, item.getString("Name"));
                }
            }else if(methodStr.length() > 30 && ("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap|ECIRelationV4_SearchTreeRelationMap|ECIRelationV4_GetCompanyEquityShareMap").contains(methodStr)){
                Method method = clazz.getDeclaredMethod(methodStr, String.class);
                method.invoke(getOriginQccService, keyNo);
            }else {
                Method method = clazz.getDeclaredMethod(methodStr, String.class);
                method.invoke(getOriginQccService, companyName);
            }
        }catch (NoSuchMethodException e){
            return Result.error("方法:" + methodStr + "不存在。");
        }

        return Result.ok("已完成");
    }

}
