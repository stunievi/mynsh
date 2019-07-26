package com.beeasy.hzreport.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.hzreport.config.ExportTo;
import com.beeasy.hzreport.config.UseSimpleSql;
import com.beeasy.hzreport.service.ExcelService;
import com.beeasy.hzreport.service.ReportService;
import com.beeasy.mscommon.filter.AuthFilter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/report/export")
public class ExcelController {

    @Autowired
    ReportService reportService;
    @Autowired
    ExcelService excelService;
    @Autowired
    SQLManager sqlManager;

    @Value("${filepath.path}")
    String path;

//    private static final Map<String,String> templates = C.newMap(
//            "report_1", "惠州农商银行信贷资产质量情况统计表（月报表）"
//            , "report_2", "惠州农商银行信贷资产质量情况统计表（月报表）"
//    );

    @RequestMapping(value = "/{no}")
    public ResponseEntity<byte[]> exportex(
            @PathVariable String no
            , @RequestParam Map<String,Object> params
            , @PageableDefault(value = 5000, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) throws ClassNotFoundException {
        try {
            params.put("uid", AuthFilter.getUid());
            Method method = reportService.getClass().getMethod(no, Map.class , Pageable.class);
            UseSimpleSql simpleSql = Class.forName(ReportService.class.getName())
                    .getMethod(no, Map.class, Pageable.class)
                    .getAnnotation(UseSimpleSql.class);
            Object result;
            if(null != simpleSql){
                if(simpleSql.usePage()){
                    result = reportService.query(no, params, pageable);
                }
                else{
                    result = sqlManager.select( "report." + no, JSONObject.class, params);
                }
            }
            else{
                result = method.invoke(reportService, params, pageable);
            }
            ExportTo exportTo = Class.forName(ReportService.class.getName())
                    .getMethod(no, Map.class, Pageable.class)
                    .getAnnotation(ExportTo.class);
            byte[] bytes = null;
            if(result instanceof Page){
                bytes = excelService.exportTableByTemplate2(no + ".xlsx", ((Page) result).getContent());
            }
            else if(result instanceof PageQuery){
                bytes = excelService.exportTableByTemplate2(no + ".xlsx", (List<JSONObject>) ((PageQuery) result).getList());
            }
            else if(result instanceof List){
                bytes = excelService.exportTableByTemplate2(no + ".xlsx", (List<JSONObject>) result);
            }
            ReportController.logCase(no,3);
            return download(exportTo.value(), bytes);
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return new ResponseEntity<byte[]>(new byte[0], new HttpHeaders(), HttpStatus.OK);
    }


    @RequestMapping(value = "/report_33")
    public ResponseEntity<byte[]> report_33(
            @PageableDefault(value = 5000, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
            ,@RequestParam Map<String,Object> params
    ){
        JSONObject o_head = (reportService.report_33_head(params,pageable));
        List<JSONObject> list = (reportService.report_33_body(params,pageable));
        byte[] bytes = excelService.exportExcelByTemplate("report_33.xlsx",o_head, list);
        return download("贷款单户明细.xlsx", bytes);
    }

    @RequestMapping(value = "/mortgage")
    public ResponseEntity<byte[]> mortgage(
            @PageableDefault(value = 5000, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
            ,@RequestParam Map<String,Object> params
    ){
        List<JSONObject> list_g = (reportService.mortgage_g(params,pageable));
        List<JSONObject> list_p = (reportService.mortgage_p(params,pageable));
        List<JSONObject> list_er = (reportService.mortgage_er(params,pageable));
        List<List<JSONObject>> list = C.newList();
        list.add(list_g);
        list.add(list_p);
        list.add(list_er);
        byte[] bytes = excelService.exportTableByTemplate("mortgage.xlsx", list);
        return download("抵押物明细.xlsx", bytes);
    }

    @RequestMapping(value = "/mortgage/templateDownload")
    public ResponseEntity<byte[]> templateDownload(){
        List<JSONObject> result = new ArrayList<>();
        byte[] bytes = excelService.exportTableByTemplate2("按揭类出证信息导入模板.xlsx", result);
        Log.log("按揭类出证信息导入模板下载");
        return download("按揭类出证信息导入模板.xlsx", bytes);
    }

    @RequestMapping(value = "/mortgage/definitionFileDownload")
    public ResponseEntity<byte[]> dingyiDownload(){
        List<JSONObject> result = new ArrayList<>();
        byte[] bytes = excelService.exportExcel2(path, result);
        Log.log("信贷中间表转换定义文件导出");
        return download("信贷中间表转换定义文件.xlsx", bytes);
    }

    /**
     * 企查查风险信息导出
     * @return
     */
    @RequestMapping(value = "/riskInfo/exportExcel")
    public ResponseEntity<byte[]> riskInfoExport(
            @PageableDefault(value = 5000, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
            ,@RequestParam Map<String,Object> params){
        List<JSONObject> result = null;
        params.put("uid", AuthFilter.getUid());
        String startTime = params.get("startTime").toString();
        String endTime = params.get("endTime").toString();
        if(!"".equals(startTime)){
            String y = startTime.substring(0,4);
            String m = startTime.substring(4,6);
            String d = startTime.substring(6,8);
            params.put("startTime",y+"-"+m+"-"+d);
        }
        if(!"".equals(endTime)){
            String y = endTime.substring(0,4);
            String m = endTime.substring(4,6);
            String d = endTime.substring(6,8);
            params.put("endTime",y+"-"+m+"-"+d);
        }

        result = sqlManager.select( "accloan.企查查风险信息查询", JSONObject.class, params);

        byte[] bytes = excelService.exportTableByTemplate2("report_34.xlsx", result);
        Log.log("企查查风险信息导出");
        return download("企查查风险信息.xlsx", bytes);
    }

    /**
     * @Author gotomars
     * @Description 自定义列表导出
     * @Date 9:39 2019/7/10
     **/
    @RequestMapping(value = "/diy/{act}")
    public ResponseEntity<byte[]> riskInfoExport(
            @PathVariable String act,
            @RequestParam("head") String headData,
            @RequestParam("query") String queryData
    ){
        JSONArray head = JSON.parseArray(headData);
        JSONObject query = JSON.parseObject(queryData);
        query.put("uid", AuthFilter.getUid());
        List<JSONObject> result = null;
        String fileName = "";
        String sqlId = "";
        if("groupCusList".equals(act)){
            fileName = "集团客户信息.xlsx";
            sqlId = "link.search_group_cus_list";
        }else if("holderLink".equals(act)){
            fileName = "股东关联信息.xlsx";
            sqlId = "link.search_holder_link";
        }else if("linkListLoan".equals(act)){
            query.put("LINK_MODEL", "linkList");
            fileName = "关联方贷款.xlsx";
            sqlId = "link.loan_link_list";
        }else if("groupCusLoan".equals(act)){
            query.put("LINK_MODEL", "groupCus");
            fileName = "集团客户贷款.xlsx";
            sqlId = "link.loan_link_list";
        }else if("stockHolderLoan".equals(act)){
            query.put("LINK_MODEL", "stockHolder");
            fileName = "股东及股东关联方贷款.xlsx";
            sqlId = "link.loan_link_list";
        }else if("qualCusLink".equals(act)){
            fileName = "资质客户关联方.xlsx";
            sqlId = "link.查询资质客户关联方";
        }else {
            throw new RuntimeException("非法请求");
        }
        result = sqlManager.select( sqlId, JSONObject.class, query);
        if(null!=result){
            System.out.println(result.get(0).toJSONString());
        }
        byte[] bytes = excelService.exportTableToExcel(head, JSONArray.parseArray(JSON.toJSONString(result)));
        return download(fileName, bytes);
    }


    private ResponseEntity<byte[]> download(String filename, byte[] bytes){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String fileName =  filename;
//        "666.xlsx";
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.set("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=utf-8''" + fileName);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
    }
}
