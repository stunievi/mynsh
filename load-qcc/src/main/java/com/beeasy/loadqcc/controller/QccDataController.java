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

import java.util.List;

@RestController
@RequestMapping("/qcc/loadData")
public class QccDataController {

    @Autowired
    MongoService mongoService;
    @Autowired
    GetOriginQccService getOriginQccService;

    public void loadData(String keyWord, Integer retryTimes) throws InterruptedException {
        try {
            if(retryTimes < 3){
                getOriginQccService.loadAllData(keyWord);
            }
        }catch (Exception e){
            if(retryTimes > 0 && retryTimes < 3){
                Thread.sleep(1000);
                loadData(keyWord, ++retryTimes);
            }
        }
    }
    @GetMapping(value = "/getAllQccData")
    Result ECI_GetDetailsByName(
           @RequestParam("keyWord") String keyWord
    ){
        try{
            loadData(keyWord, 1);
        }catch (InterruptedException e){
            return Result.error("重试失败！");
        }
        return Result.ok(keyWord);
    }

    // 基本信息
    @RequestMapping(value = "/loadDataBlock", method = RequestMethod.POST)
    Result loadDataBlock(
            @RequestBody() JSONObject params
    ){
        List dataList = params.getJSONArray("dataList");
        for(int i = 0 ; i < dataList.size() ; i++) {
            JSONObject data = (JSONObject) dataList.get(i);
            String companyName = data.getString("Content"); // 公司名
            String command = data.getString("Sign"); // 指令
            if(null == command || command.isEmpty()){
                return Result.error("指定为空");
            }
            if(null == companyName|| companyName.isEmpty()){
                return Result.error("公司名为空");
            }
            Document tmep_company_ret = mongoService.getCollection("ECIV4_GetDetailsByName").find(Filters.eq("QueryParam.keyWord", companyName)).first();
            // 公司公司信息
            JSONObject companyInfo;
            if(null == tmep_company_ret){
                // 从企查查获取工商信息
                companyInfo = getOriginQccService.ECI_GetDetailsByName(companyName, "manual");
            }else{
                companyInfo = (JSONObject) JSON.toJSON(tmep_company_ret);
                companyInfo = companyInfo.getJSONObject("Result");
            }
            if(null == companyInfo || companyInfo.isEmpty()){
                return Result.error("公司工商信息获取失败！");
            }
            String keyNo = companyInfo.getString("KeyNo");
            // 更新所有
            if(command.contains("00")){
                getOriginQccService.loadAllData(companyName);
            }else{
                if(command.contains("01")){
                    // 更新基本信息
                    getOriginQccService.loadDataBlock1(companyName);
                }
                if(command.contains("02")){
                    // 法律诉讼
                    getOriginQccService.loadDataBlock2(companyName);
                }
                if(command.contains("03")){
                    // 关联族谱
                    getOriginQccService.loadDataBlock3(companyName, keyNo);
                }
                if(command.contains("04")){
                    // 历史信息
                    getOriginQccService.loadDataBlock4(companyName);
                }
                if(command.contains("05")){
                    // 经营风险
                    getOriginQccService.loadDataBlock5(companyName, keyNo);
                }
            }
        }
        return Result.ok("更新完毕");
    }

    // 基本信息
    @GetMapping(value = "/loadDataBlock1")
    Result loadDataBlock1(
            @RequestParam("keyWord") String keyWord
    ){
        getOriginQccService.loadDataBlock1(keyWord);
        return Result.ok("loadDataBlock1");
    }
    // 法律诉讼
    @GetMapping(value = "/loadDataBlock2")
    Result loadDataBlock2(
            @RequestParam("keyWord") String keyWord
    ){
        getOriginQccService.loadDataBlock2(keyWord);
        return Result.ok("loadDataBlock2");
    }
    // 关联族谱
    @GetMapping(value = "/loadDataBlock3")
    Result loadDataBlock3(
            @RequestParam("keyWord") String keyWord,
            @RequestParam("keyNo") String keyNo
    ){
        getOriginQccService.loadDataBlock3(keyWord, keyNo);
        return Result.ok("loadDataBlock3");
    }
    // 历史信息
    @GetMapping(value = "/loadDataBlock4")
    Result loadDataBlock4(
            @RequestParam("keyWord") String keyWord
    ){
        getOriginQccService.loadDataBlock4(keyWord);
        return Result.ok("loadDataBlock4");
    }
    // 经营风险
    @GetMapping(value = "/loadDataBlock5")
    Result loadDataBlock5(
            @RequestParam("keyWord") String keyWord,
            @RequestParam("keyNo") String keyNo
    ){
        getOriginQccService.loadDataBlock5(keyWord, keyNo);
        return Result.ok("loadDataBlock5");
    }

    // 手动触发更新数据
//    @RequestMapping(value = "/{$model}/{$action}", method = RequestMethod.GET)
//    Result autoQccData(
//            @PathVariable String $model
//            , @PathVariable String $action
//            , @RequestParam("keyWord") String keyWord
//    ) throws IllegalAccessException, InvocationTargetException {
//        Document comInfo = mongoService.getCollection("ECIV4_GetDetailsByName").find(Filters.eq("QueryParam.keyWord", keyWord)).first();
//        JSONObject loadComInfo = (JSONObject) JSON.toJSON(comInfo);
//        loadComInfo = loadComInfo.getJSONObject("Result");
//        if(null == loadComInfo || loadComInfo.size() <1){
//            loadComInfo = getOriginQccService.ECI_GetDetailsByName(keyWord, "manual");
//            if(null == loadComInfo || loadComInfo.size()<0){
//                return  Result.error("未找到");
//            }
//        }
//        Class clazz = getOriginQccService.getClass();
//
//        String companyName = loadComInfo.getString("Name");
//        if(null == companyName || "".equals(companyName)){
//            return Result.ok("公司名不存在");
//        }
//        String keyNo = loadComInfo.getString("KeyNo");
//        List employees = loadComInfo.getJSONArray("Employees");
//        String methodStr = $model.concat("_").concat($action);
//        try {
//            if(methodStr.equals("CIAEmployeeV4_GetStockRelationInfo")){
//                // 企业人员董监高信息
//                for(short i=0;i<employees.size();i++){
//                    JSONObject item = (JSONObject) employees.get(i);
//                    Method method = clazz.getDeclaredMethod("CIAEmployeeV4_GetStockRelationInfo", String.class, String.class, String.class);
//                    method.invoke(getOriginQccService, companyName, item.getString("Name"), "manual");
//                }
//            }else if(methodStr.length() > 30 && ("ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap|ECIRelationV4_SearchTreeRelationMap|ECIRelationV4_GetCompanyEquityShareMap").contains(methodStr)){
//                Method method = clazz.getDeclaredMethod(methodStr, String.class, String.class);
//                method.invoke(getOriginQccService, keyNo, "manual");
//            }else {
//                Method method = clazz.getDeclaredMethod(methodStr, String.class, String.class);
//                method.invoke(getOriginQccService, companyName, "manual");
//            }
//        }catch (NoSuchMethodException e){
//            return Result.error("方法:" + methodStr + "不存在。");
//        }
//        return Result.ok("已完成");
//    }

}
