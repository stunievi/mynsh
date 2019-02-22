package com.beeasy.hzreport.controller;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzreport.config.ExportTo;
import com.beeasy.hzreport.config.UseSimpleSql;
import com.beeasy.hzreport.filter.AuthFilter;
import com.beeasy.hzreport.service.ReportService;
import com.beeasy.mscommon.Result;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@RequestMapping
@RestController
public class ReportController {

    @Autowired
    ReportService reportService;
    @Autowired
    SQLManager sqlManager;


//
//    @RequestMapping(value = "/report/cap_expect")
//    public Result cap_expect(
//            String START_DATE,
//            String END_DATE,
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.cap_expect(pageable,START_DATE,END_DATE,PRD_TYPE,LOAN_NATURE));
//    }
//
//    @RequestMapping(value = "/report/int_expect")
//    public Result int_expect(
//            String START_DATE,
//            String END_DATE,
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.int_expect(pageable,START_DATE,END_DATE,PRD_TYPE,LOAN_NATURE));
//    }
//
//    @RequestMapping(value = "/report/cap_overdue")
//    public Result cap_overdue(
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.cap_overdue(pageable,PRD_TYPE,LOAN_NATURE));
//    }
//
//    @RequestMapping(value = "/report/int_overdue")
//    public Result int_overdue(
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.int_overdue(pageable,PRD_TYPE,LOAN_NATURE));
//    }
//
//    //惠州农商银行信贷资产质量情况统计表（月报表）
//    @RequestMapping(value = "/report/report_1")
//    public Result report_1(
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
//            @RequestParam Map<String,Object> params
//    ){
//        return Result.ok(reportService.report_1(params,pageable));
//    }

    @RequestMapping(value = "/report/{no}")
    public Result reportex(
            @PathVariable String no
            , @RequestParam Map<String,Object> params
            , @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
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
                    result = reportService.query( no, params, pageable);
                }
                else{
                    result = sqlManager.select("report." + no, JSONObject.class, params);
                }
            }
            else{
                result = method.invoke(reportService, params, pageable);
            }

            return Result.ok(result);
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return Result.error();
    }

//
//
//    //表内正常贷款欠息情况统计报表
//    @RequestMapping(value = "/report/report_6")
//    public Result report_6(
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_6(params,pageable));
//    }
//
//    //惠州市农商行隐性不良贷款明细表
//    @RequestMapping(value = "/report/report_7")
//    public Result report_7(
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_7(pageable,PRD_TYPE,LOAN_NATURE));
//    }
//
//    //惠州农商行五级分类不良贷款明细表
//    @RequestMapping(value = "/report/report_8")
//    public Result report_8(
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_8(pageable,PRD_TYPE,LOAN_NATURE));
//    }
//
//    //惠州农商行五级分类不良贷款现金收回明细表
//    @RequestMapping(value = "/report/report_9")
//    public Result report_9(
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_9(pageable,PRD_TYPE,LOAN_NATURE));
//    }
//
//    //惠州农商行五级分类不良贷款上调明细表
//    @RequestMapping(value = "/report/report_10")
//    public Result report_10(
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_10(pageable,PRD_TYPE,LOAN_NATURE));
//    }
//
//    //一般贷款上一年年度统计表
//    @RequestMapping(value = "/report/report_11")
//    public Result report_11(
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
//            @RequestParam Map<String,Object> params
//            ){
//        return Result.ok(reportService.report_11(params,pageable));
//    }
//
//    //信贷营销情况（一般贷款）
//    @RequestMapping(value = "/report/report_12")
//    public Result report_12(
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_12(pageable));
//    }
//
//    //信贷营销情况（按揭贷款）
//    @RequestMapping(value = "/report/report_13")
//    public Result report_13(
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_13(pageable));
//    }
//
//    //发放及到期收回情况(月份）
//
//    //信贷资产质量情况
//    @RequestMapping(value = "/report/report_17")
//    public Result report_17(
//    ){
//        return Result.ok(reportService.report_17());
//    }
//
//    //隐性不良贷款情况
//
//
//    //按担保类型划分
//    @RequestMapping(value = "/report/report_19")
//    public Result report_19(
//    ){
//        return Result.ok(reportService.report_19());
//    }
//
//    //前十大户和最大单户情况
//    @RequestMapping(value = "/report/report_20")
//    public Result report_20(
//    ){
//        return Result.ok(reportService.report_20());
//    }
//
//    //房地产开发贷款情况
//    @RequestMapping(value = "/report/report_21")
//    public Result report_21(
//    ){
//        return Result.ok(reportService.report_21());
//    }
//
//    //个人按揭业务情况
//
//    //卡贷宝贷款业务情况
//
//    //卡贷宝营销情况
//
//    //正常贷款的五大欠息户
//    @RequestMapping(value = "/report/report_25")
//    public Result report_25(
//    ){
//        return Result.ok(reportService.report_25());
//    }
//
//    //新增贷款利率结构表
//    @RequestMapping(value = "/report/report_26")
//    public Result report_26(
//            String START_DATE,
//            String END_DATE
//    ){
//        return Result.ok(reportService.report_26(START_DATE,END_DATE));
//    }
//
//    //新增贷款明细表
//    @RequestMapping(value = "/report/report_27")
//    public Result report_27(
//            String START_DATE,
//            String END_DATE,
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_27(pageable,START_DATE,END_DATE,PRD_TYPE,LOAN_NATURE));
//    }
//
//    //新增不良贷款明细表
//    @RequestMapping(value = "/report/report_28")
//    public Result report_28(
//            String START_DATE,
//            String END_DATE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_28(pageable,START_DATE,END_DATE));
//    }
//
//    //逾期台帐明细表
//    @RequestMapping(value = "/report/report_29")
//    public Result report_29(
//            String PRD_TYPE,
//            String LOAN_NATURE,
//            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(reportService.report_29(pageable,PRD_TYPE,LOAN_NATURE));
//    }
//
//    //逾期台帐统计表
//    @RequestMapping(value = "/report/report_30")
//    public Result report_30(
//            Integer LOAN_TERM_MIN,
//            Integer LOAN_TERM_MAX
//    ){
//        return Result.ok(reportService.report_30(LOAN_TERM_MIN,LOAN_TERM_MAX));
//    }
//
//    //新增贷款统计表
//    @RequestMapping(value = "/report/report_31")
//    public Result report_31(
//            String START_DATE,
//            String END_DATE,
//            Integer LOAN_TERM_MIN,
//            Integer LOAN_TERM_MAX
//    ){
//        return Result.ok(reportService.report_31(START_DATE,END_DATE,LOAN_TERM_MIN,LOAN_TERM_MAX));
//    }
//
//    //新增不良贷款统计表
//    @RequestMapping(value = "/report/report_32")
//    public Result report_32(
//            String START_DATE,
//            String END_DATE,
//            Integer LOAN_TERM_MIN,
//            Integer LOAN_TERM_MAX
//    ){
//        return Result.ok(reportService.report_32(START_DATE,END_DATE,LOAN_TERM_MIN,LOAN_TERM_MAX));
//    }

    //分期贷款单户明细表-表头
//    @RequestMapping(value = "/report/report_33_head")
//    public Result report_33_1(
//            String LOAN_ACCOUNT
//    ){
//        return Result.ok(reportService.report_33_head(LOAN_ACCOUNT));
//    }
    //分期贷款单户明细表
    @RequestMapping(value = "/report/report_33")
    public Result report_33(
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Map<String,Object> params
    ){
        return Result.ok(
                C.newMap(
                        "head",reportService.report_33_head(params,pageable)
                        , "body",reportService.report_33_body(params,pageable)
                )
        );
    }

    //分期贷款应还未还明细表



    //APP接口-获取用户信息
    @RequestMapping(value = "/report/app_userstatus")
    public Result app_userstatus(
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Map<String,Object> params
    ){
        return Result.ok(reportService.app_userstatus(params,pageable));
    }

    //APP接口-获取五级分类统计
    @RequestMapping(value = "/report/app_cla")
    public Result app_cla(
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Map<String,Object> params
    ){
        return Result.ok(reportService.app_cla(params,pageable));
    }

    //APP接口-获取客户信息
    @RequestMapping(value = "/report/app_cus")
    public Result app_cus(
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Map<String,Object> params
    ){
        return Result.ok(reportService.app_cus(params,pageable));
    }

    @RequestMapping(value = "/report/app_loan")
    public Result app_loan(
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Map<String,Object> params
    ){
        return Result.ok(reportService.app_loan(params,pageable));
    }

    //APP接口-获取台帐分类统计
    @RequestMapping(value = "/report/app_loan_class_all")
    public Result app_loan_class_all(
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Map<String,Object> params
    ){
        return Result.ok(reportService.app_loan_class_all(params,pageable));
    }

    //APP接口-获取台帐分类统计1
    @RequestMapping(value = "/report/app_loan_class")
    public Result app_loan_class(
            @PageableDefault(value = 15, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Map<String,Object> params
    ){
        return Result.ok(reportService.app_loan_class(params,pageable));
    }



}
