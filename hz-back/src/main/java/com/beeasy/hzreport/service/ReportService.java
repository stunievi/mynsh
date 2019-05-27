package com.beeasy.hzreport.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzreport.config.ExportTo;
import com.beeasy.hzreport.config.UseSimpleSql;
import com.beeasy.hzreport.request.R1;
import com.beeasy.hzreport.request.R11;
import com.beeasy.mscommon.filter.AuthFilter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class ReportService {

    @Autowired
    SQLManager sqlManager;
    @Autowired
            ExcelService excelService;

    //贷款机构代码
    //constant
    constant constant = new constant();
    public String MAIN_BR_ID[][]={constant.BR1,constant.BR2,constant.BR3,constant.BR4,constant.BR5,
            constant.BR6,constant.BR7,constant.BR8,constant.BR9,constant.BR10
            ,constant.BR11,constant.BR12,constant.BR13,constant.BR14};
    //贷款机构中文名称
    public String MAIN_BR_NAME[]={"中心支行","惠城支行","小金口支行","水口支行",
            "江南支行","惠阳支行","新圩支行","秋长支行","仲恺支行","大亚湾支行",
            "惠城小微企业金融部","仲恺小微企业金融部","惠阳小微金融部","合计"};
    //贷款期限
    public String TERM_TYPE[]={"1年(含)内","1-3年(含)","3-5年(含)","5-8年(含)","8年以上","合计"};

    //预期应收本金（到期）明细表
    @ExportTo("预期应收本金（到期）明细表.xlsx")
    @UseSimpleSql
    public Page<JSONObject> cap_expect(
            Map<String,Object> paramMap, Pageable pageable
    ){
        return null;
    }
    //预期应收利息明细表
    @ExportTo("预期应收利息明细表.xlsx")
    @UseSimpleSql
    public void int_expect(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }
    //逾期应收本金明细表
    @ExportTo("逾期应收本金明细表.xlsx")
    @UseSimpleSql
    public void cap_overdue(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //逾期应收利息明细表
    @ExportTo("逾期应收利息明细表.xlsx")
    @UseSimpleSql
    public void int_overdue(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //惠州农商银行信贷资产质量情况统计表（月报表）
    @ExportTo("惠州农商银行信贷资产质量情况统计表（月报表）.xlsx")
    public List<JSONObject> report_1(
            Map<String,Object> paramMap, Pageable pageable
    ){
        List<JSONObject> retAll = new ArrayList();

        R1 params = JSON.toJavaObject((JSON)JSON.toJSON(paramMap), R1.class);
        //获取年份
        if(null == params.getYear()){
            params.setYear(Integer.valueOf(getCurrentYear()));
        }
        //获取月份
        if(null == params.getMonth()){
            params.setMonth(Integer.valueOf(getCurrentMonth()));
        }

        String strDate = "";
        String strMonth = "";
        int iYear =params.getYear();
        //格式化月份
        strMonth = haoAddOne(String.valueOf(params.getMonth()));
        //日期格式：yyyyMMdd
        strDate = getLastDay(iYear + "",strMonth);

        for(int i=0;i<MAIN_BR_ID.length;i++) {
            JSONObject map = new JSONObject();
            map.put("SRC_SYS_DATE",strDate); //源系统日期
            map.put("MAIN_BR_ID", MAIN_BR_ID[i]);       //主管部门
            List<JSONObject> ret = sqlManager.select("report.report_1", JSONObject.class, map);
            for (Map map1 : ret) {
                BigDecimal loanBalance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                BigDecimal badLoanBalance = (BigDecimal) map1.getOrDefault("BAD_LOAN_BALANCE", new BigDecimal(0));
                BigDecimal hideLoanBalance = (BigDecimal) map1.getOrDefault("HIDE_LOAN_BALANCE", new BigDecimal(0));

                BigDecimal badIr = new BigDecimal(0);
                BigDecimal hideIr = new BigDecimal(0);
                BigDecimal loanBalanceOther = new BigDecimal(0);
                BigDecimal delayIntCumuOther = new BigDecimal(0);
//                if (!loanBalance.equals(BigDecimal.ZERO)){
                if (loanBalance.compareTo(BigDecimal.ZERO)!=0) {
                    BigDecimal percent = new BigDecimal(100);
                    badIr = badLoanBalance.multiply(percent);
                    hideIr = hideLoanBalance.multiply(percent);
                    badIr = badIr.divide(loanBalance, 2, BigDecimal.ROUND_HALF_UP);
                    hideIr = hideIr.divide(loanBalance, 2, BigDecimal.ROUND_HALF_UP);
                }
                map1.put("BAD_IR",badIr);
                map1.put("HIDE_IR",hideIr);
                map1.put("LOAN_BALANCE_OTHER",loanBalanceOther);
                map1.put("DELAY_INT_CUMU_OTHER",delayIntCumuOther);
                map1.put("MAIN_BR_NAME",MAIN_BR_NAME[i]);

            }
            retAll.addAll(ret);
        }
        return retAll;

    }

    //表内正常贷款欠息情况统计报表
    @ExportTo("表内正常贷款欠息情况统计报表.xlsx")
    @UseSimpleSql
    public void report_6(
            Map<String,Object> paramMap, Pageable pageable
    ){
//        long uid = AuthFilter.getUid();
//        paramMap.put("uid", uid);
    }

    //惠州市农商行隐性不良贷款明细表
    @ExportTo("惠州市农商行隐性不良贷款明细表.xlsx")
    @UseSimpleSql
    public void report_7(
            Map<String,Object> paramMap, Pageable pageable
){
    }

    //惠州农商行五级分类不良贷款明细表
    @ExportTo("惠州农商行五级分类不良贷款明细表.xlsx")
    @UseSimpleSql
    public void report_8(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //惠州农商行五级分类不良贷款现金收回明细表
    @ExportTo("惠州农商行五级分类不良贷款现金收回明细表.xlsx")
    @UseSimpleSql
    public void report_9(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //惠州农商行五级分类不良贷款上调明细表
    @ExportTo("惠州农商行五级分类不良贷款上调明细表.xlsx")
    @UseSimpleSql
    public void report_10(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //一般贷款上一年年度统计表
    @ExportTo("一般贷款上一年年度统计表.xlsx")
    public Object report_11(
            Map<String,Object> paramMap, Pageable pageable
    ){
        List retAll = new ArrayList();

        R11 params = JSON.toJavaObject((JSON)JSON.toJSON(paramMap), R11.class);
        if(null == params.getYear()){
            params.setYear(Integer.valueOf(getCurrentYear()));  //获取当前年份
        }

        //年初&上次查询的贷款余额、普通贷款余额、转贴现余额
        BigDecimal firstLoanBalanceOne = new BigDecimal("0");
        BigDecimal firstLoanBalanceTwo = new BigDecimal("0");
        BigDecimal firstLoanBalanceThree = new BigDecimal("0");
        BigDecimal lastLoanBalanceOne = new BigDecimal("0");
        BigDecimal lastLoanBalanceTwo = new BigDecimal("0");
        BigDecimal lastLoanBalanceThree = new BigDecimal("0");
        for(int i=0;i<13;i++) {
            Map map = new HashMap();
            String strDate = "";
            String strMonth = "";
            if (i == 0) {
                strMonth = haoAddOne(String.valueOf(12));    //上一年的12月份
                int iYear =params.getYear()-1;                      //上一年
                strDate = getLastDay(iYear + "",strMonth);
            } else{
                strMonth = haoAddOne(String.valueOf(i));     //当年月份
                strDate = getLastDay(params.getYear() + "",strMonth);
            }
            //strDate = "20180829";
            map.put("SRC_SYS_DATE",strDate); //源系统日期
            List<JSONObject> ret = sqlManager.select("report.report_11", JSONObject.class, map);
            for (JSONObject map1 : ret) {
                BigDecimal loanBalanceOne = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_1", new BigDecimal(0));
                if (loanBalanceOne.compareTo(BigDecimal.ZERO)==0) {
                    map1.put("COMPARE_LAST_YEAR_1",0);
                    map1.put("COMPARE_LAST_MONTH_1",0);
                    map1.put("COMPARE_LAST_YEAR_2",0);
                    map1.put("COMPARE_LAST_MONTH_2",0);
                    map1.put("COMPARE_LAST_YEAR_3",0);
                    map1.put("COMPARE_LAST_MONTH_3",0);
                    break;
                }
                BigDecimal loanBalanceTwo = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_2", new BigDecimal(0));
                BigDecimal loanBalanceThree = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_3", new BigDecimal(0));
                if (i==0){
                    firstLoanBalanceOne = loanBalanceOne;
                    firstLoanBalanceTwo = loanBalanceTwo;
                    firstLoanBalanceThree= loanBalanceThree;
                }else{
                    lastLoanBalanceOne = loanBalanceOne;
                    lastLoanBalanceTwo = loanBalanceTwo;
                    lastLoanBalanceThree = loanBalanceThree;
                }
                map1.put("DATE",strDate);   //月份
                double lastYearOne = sub(loanBalanceOne,firstLoanBalanceOne);
                map1.put("COMPARE_LAST_YEAR_1",lastYearOne);
                double lastMonthOne = sub(loanBalanceOne,lastLoanBalanceOne);
                map1.put("COMPARE_LAST_MONTH_1",lastMonthOne);
                double lastYearTwo = sub(loanBalanceTwo,firstLoanBalanceTwo);
                map1.put("COMPARE_LAST_YEAR_2",lastYearTwo);
                double lastMonthTwo = sub(loanBalanceTwo,lastLoanBalanceTwo);
                map1.put("COMPARE_LAST_MONTH_2",lastMonthTwo);
                double lastYearThree = sub(loanBalanceThree,firstLoanBalanceThree);
                map1.put("COMPARE_LAST_YEAR_3",lastYearThree);
                double lastMonthThree = sub(loanBalanceThree,lastLoanBalanceThree);
                map1.put("COMPARE_LAST_MONTH_3",lastMonthThree);
            }
            retAll.addAll(ret);
        }
        return retAll;
    }

    //信贷营销情况（一般贷款）
    @ExportTo("信贷营销情况（一般贷款）.xlsx")
    public Object report_12(
            Map<String,Object> paramMap, Pageable pageable
    ){
        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        int iTypeFlag =0;
        int j = 0;
        String strTemp = "";
        //遍历所有贷款机构
        for(int i=0;i<MAIN_BR_NAME.length+1;i++) {      //公司部拆分为两个，所以+1
            Map map = new HashMap();
            if (iTypeFlag == 0){
                j = i;
            }else{
                j = i-1;        //公司部拆分为两个，所以后面的贷款机构下标-1
            }
            if (MAIN_BR_NAME[j].equals("中心支行")){
                if (iTypeFlag == 0){                //先查普通贷款，再查转贴现
                    map.put("lN_TYPE","普通贷款");
                    strTemp = "中心支行（普通贷款）";
                    iTypeFlag = 1;
                }else{
                    map.put("lN_TYPE","转贴现");
                    strTemp = "中心支行（转贴现）";
                }
            }
            map.put("MAIN_BR_ID", MAIN_BR_ID[j]);       //主管部门

            List<JSONObject> ret = sqlManager.select("report.report_12", JSONObject.class, map);
            for (JSONObject map1 : ret) {
                BigDecimal loanBalance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                if (MAIN_BR_NAME[j].equals("中心支行")){
                    map1.put("MAIN_BR_NAME",strTemp);
                }else{
                    map1.put("MAIN_BR_NAME",MAIN_BR_NAME[j]);
                }

                //根据日期计算上月最后一天、上一年度最后一天，并从HIS表获取数据
                //查询上一个月最后一天的某个贷款机构的贷款余额，并写入map1中
                Date dt1 = dtSrcSysNow;
                String strYear = "";
                String strMonth = "";
                String strDate = "";
                if (dt1!=null){     //获取一个月前的时间
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(dt1);//把当前时间赋给日历
                    calendar.add(calendar.MONTH, -1);  //设置为前1月
                    dt1 = calendar.getTime();//获取1个月前的时间
                }
                SimpleDateFormat f1=new SimpleDateFormat("yyyy");
                SimpleDateFormat f2=new SimpleDateFormat("MM");
                strYear = f1.format(dt1);
                strMonth = f2.format(dt1);
                strDate = getLastDay(strYear,strMonth);
                Map map2 = new HashMap();
                map2.put("SRC_SYS_DATE",strDate);
                map2.put("MAIN_BR_ID", MAIN_BR_ID[j]);       //主管部门
                if (MAIN_BR_NAME[j].equals("中心支行")){
                    if (iTypeFlag == 0){                //先查普通贷款，再查转贴现
                        map2.put("lN_TYPE","普通贷款");
                        strTemp = "中心支行（普通贷款）";
                    }else{
                        map2.put("lN_TYPE","转贴现");
                        strTemp = "中心支行（转贴现）";
                    }
                }
                List<JSONObject> ret1 = sqlManager.select("report.report_12_1", JSONObject.class, map2);
                for (JSONObject map3 : ret1) {
                    BigDecimal loanBalanceLastMonth = (BigDecimal) map3.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                    map1.put("LOAN_BALANCE_LM",sub(loanBalance,loanBalanceLastMonth));
                }
                //查询上一年的某个贷款机构的贷款余额，并写入map1中
                Date dt2 = dtSrcSysNow;
                if (dt2!=null){     //获取一年前的时间
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(dt2);//把当前时间赋给日历
                    calendar.add(calendar.YEAR, -1);  //设置为前1年
                    dt2 = calendar.getTime();//获取1年前的时间
                }
                strYear = f1.format(dt2);
                strMonth = "12";
                strDate = getLastDay(strYear,strMonth);
                Map map4 = new HashMap();
                map4.put("SRC_SYS_DATE",strDate);
                map4.put("MAIN_BR_ID", MAIN_BR_ID[j]);       //主管部门
                if (MAIN_BR_NAME[j].equals("中心支行")){
                    if (iTypeFlag == 0){                //先查普通贷款，再查转贴现
                        map4.put("lN_TYPE","普通贷款");
                        strTemp = "中心支行（普通贷款）";
                    }else{
                        map4.put("lN_TYPE","转贴现");
                        strTemp = "中心支行（转贴现）";
                    }
                }
                List<JSONObject> ret2 = sqlManager.select("report.report_12_1", JSONObject.class, map4);
                for (JSONObject map5 : ret2) {
                    BigDecimal loanBalanceLastYear = (BigDecimal) map5.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                    map1.put("LOAN_BALANCE_LY",sub(loanBalance,loanBalanceLastYear));
                    BigDecimal BG1 = loanBalance.subtract(loanBalanceLastYear); //当前余额相对去年末增加多少
                    //对比去年末的增幅
                    if (loanBalanceLastYear.compareTo(BigDecimal.ZERO)!=0){
                        double d1 = div(loanBalanceLastYear,BG1,4)*100;
                        map1.put("INC_PEC",d1);
                    }else{
                        map1.put("INC_PEC",0);
                    }
                }
            }
            retAll.addAll(ret);
        }
        return retAll;
    }

    //信贷营销情况（按揭贷款）
    @ExportTo("信贷营销情况（按揭贷款）.xlsx")
    public Object report_13(
            Map<String,Object> paramMap, Pageable pageable
    ){
        //贷款机构代码
        String MAIN_BR_ID_13[][]={constant.BR2,constant.BR6,constant.BR9,constant.BR10,{"09131","09310","09323","09132","09133"}};
        //贷款机构中文名称
        String MAIN_BR_NAME_13[]={"惠城支行","惠阳支行","仲恺支行","大亚湾支行","合计"};
        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        //遍历所有贷款机构
        for(int i=0;i<MAIN_BR_NAME_13.length;i++) {      //公司部拆分为两个，所以+1
            Map map = new HashMap();
            map.put("MAIN_BR_ID", MAIN_BR_ID_13[i]);       //主管部门

            List<JSONObject> ret = sqlManager.select("report.report_13", JSONObject.class, map);
            for (JSONObject map1 : ret) {
                BigDecimal loanBalance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                map1.put("MAIN_BR_NAME",MAIN_BR_NAME_13[i]);

                //根据日期计算上月最后一天、上一年度最后一天，并从HIS表获取数据
                //查询上一个月最后一天的某个贷款机构的贷款余额，并写入map1中
                Date dt1 = dtSrcSysNow;
                String strYear = "";
                String strMonth = "";
                String strDate = "";
                if (dt1!=null){     //获取一个月前的时间
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(dt1);//把当前时间赋给日历
                    calendar.add(calendar.MONTH, -1);  //设置为前1月
                    dt1 = calendar.getTime();//获取1个月前的时间
                }
                SimpleDateFormat f1=new SimpleDateFormat("yyyy");
                SimpleDateFormat f2=new SimpleDateFormat("MM");
                strYear = f1.format(dt1);
                strMonth = f2.format(dt1);
                strDate = getLastDay(strYear,strMonth);
                Map map2 = new HashMap();
                map2.put("SRC_SYS_DATE",strDate);
                map2.put("MAIN_BR_ID",MAIN_BR_ID_13[i]);
                List<JSONObject> ret1 = sqlManager.select("report.report_13_1", JSONObject.class, map2);
                for (JSONObject map3 : ret1) {
                    BigDecimal loanBalanceLastMonth = (BigDecimal) map3.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                    map1.put("LOAN_BALANCE_LM",loanBalanceLastMonth);
                }
                //查询上一年的某个贷款机构的贷款余额，并写入map1中
                Date dt2 = dtSrcSysNow;
                if (dt2!=null){     //获取一年前的时间
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(dt2);//把当前时间赋给日历
                    calendar.add(calendar.YEAR, -1);  //设置为前1年
                    dt2 = calendar.getTime();//获取1年前的时间
                }
                strYear = f1.format(dt2);
                strMonth = "12";
                strDate = getLastDay(strYear,strMonth);
                Map map4 = new HashMap();
                map4.put("SRC_SYS_DATE",strDate);
                map4.put("MAIN_BR_ID",MAIN_BR_ID_13[i]);
                List<JSONObject> ret2 = sqlManager.select("report.report_13_1", JSONObject.class, map4);
                for (JSONObject map5 : ret2) {
                    BigDecimal loanBalanceLastYear = (BigDecimal) map5.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                    map1.put("LOAN_BALANCE_LY",loanBalanceLastYear);
                    BigDecimal BG1 = loanBalance.subtract(loanBalanceLastYear); //当前余额相对去年末增加多少
                    //对比去年末的增幅
                    if (loanBalanceLastYear.compareTo(BigDecimal.ZERO)!=0){
                        double d1 = div(loanBalanceLastYear,BG1,4)*100;
                        map1.put("INC_PEC",d1);
                    }else{
                        map1.put("INC_PEC",0);
                    }
                }
            }
            retAll.addAll(ret);
        }
        return retAll;
    }

    //发放及到期收回情况(月份）

    //信贷资产质量情况
    @ExportTo("信贷资产质量情况.xlsx")
    public Object report_17(
        Map<String,Object> paramMap, Pageable pageable
    ){
//        if(S.empty((CharSequence) paramMap.get("xxxx")));
        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        int iTypeFlag =0;
        int j = 0;
        String strTemp = "";
        //遍历所有贷款机构
        for(int i=0;i<MAIN_BR_NAME.length;i++) {
            Map map = new HashMap();
            map.put("MAIN_BR_ID", MAIN_BR_ID[i]);       //主管部门

            List<JSONObject> ret = sqlManager.select("report.report_17", JSONObject.class, map);
            for (JSONObject map1 : ret) {
                BigDecimal loanBalance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                BigDecimal loanBalance_20 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_20", new BigDecimal(0));
                BigDecimal loanBalance_30 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_30", new BigDecimal(0));
                BigDecimal loanBalance_40 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_40", new BigDecimal(0));
                map1.put("MAIN_BR_NAME",MAIN_BR_NAME[i]);

                //占比
                if (loanBalance.compareTo(BigDecimal.ZERO)!=0){
                    double d1 = div(loanBalance,loanBalance_20,4)*100;
                    double d2 = div(loanBalance,loanBalance_30,4)*100;
                    double d3 = div(loanBalance,loanBalance_40,4)*100;
                    double d4 = div(loanBalance,loanBalance_20.add(loanBalance_30).add(loanBalance_40),4)*100;
                    map1.put("IR_1",d1);
                    map1.put("IR_2",d2);
                    map1.put("IR_3",d3);
                    map1.put("IR_4",d4);
                }else{
                    map1.put("IR_1",0);
                    map1.put("IR_2",0);
                    map1.put("IR_3",0);
                    map1.put("IR_4",0);
                }

                //根据日期计算上一年度最后一天，并从HIS表获取数据
                String strYear = "";
                String strMonth = "";
                String strDate = "";
                SimpleDateFormat f1=new SimpleDateFormat("yyyy");
                SimpleDateFormat f2=new SimpleDateFormat("MM");
                //查询上一年的某个贷款机构的贷款余额，并写入map1中
                Date dt2 = dtSrcSysNow;
                if (dt2!=null){     //获取一年前的时间
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(dt2);//把当前时间赋给日历
                    calendar.add(calendar.YEAR, -1);  //设置为前1年
                    dt2 = calendar.getTime();//获取1年前的时间
                }
                strYear = f1.format(dt2);
                strMonth = "12";
                strDate = getLastDay(strYear,strMonth);
                Map map4 = new HashMap();
                map4.put("SRC_SYS_DATE",strDate);
                map4.put("MAIN_BR_ID", MAIN_BR_ID[i]);       //主管部门
                List<JSONObject> ret2 = sqlManager.select("report.report_17_1", JSONObject.class, map4);
                for (JSONObject map5 : ret2) {
                    BigDecimal loanBalanceLastYear = (BigDecimal) map5.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                    BigDecimal loanBalanceLastYear_20 = (BigDecimal) map5.getOrDefault("LOAN_BALANCE_20", new BigDecimal(0));
                    BigDecimal loanBalanceLastYear_30 = (BigDecimal) map5.getOrDefault("LOAN_BALANCE_30", new BigDecimal(0));
                    BigDecimal loanBalanceLastYear_40 = (BigDecimal) map5.getOrDefault("LOAN_BALANCE_40", new BigDecimal(0));
                    map1.put("LOAN_BALANCE_LY_20",sub(loanBalance_20,loanBalanceLastYear_20));
                    map1.put("LOAN_BALANCE_LY_30",sub(loanBalance_30,loanBalanceLastYear_30));
                    map1.put("LOAN_BALANCE_LY_40",sub(loanBalance_40,loanBalanceLastYear_40));
                }
            }
            retAll.addAll(ret);
        }
        return retAll;
    }

    //按担保类型划分
    @ExportTo("按担保类型划分.xlsx")
    public Object report_19(
            Map<String,Object> paramMap, Pageable pageable
    ){
        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        int iTypeFlag =0;
        int j = 0;
        String strTemp = "";
        //遍历所有贷款机构
        for(int i=0;i<MAIN_BR_NAME.length;i++) {
            Map map = new HashMap();
            map.put("MAIN_BR_ID", MAIN_BR_ID[i]);       //主管部门

            List<JSONObject> ret = sqlManager.select("report.report_19", JSONObject.class, map);
            for (JSONObject map1 : ret) {
                BigDecimal loanBalance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                BigDecimal loanBalance_00 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_00", new BigDecimal(0));
                BigDecimal loanBalance_10 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_10", new BigDecimal(0));
                BigDecimal loanBalance_20 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_20", new BigDecimal(0));
                BigDecimal loanBalance_30 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_30", new BigDecimal(0));
                BigDecimal loanBalance_00_bad = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_00_BAD", new BigDecimal(0));
                BigDecimal loanBalance_10_bad = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_10_BAD", new BigDecimal(0));
                BigDecimal loanBalance_20_bad = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_20_BAD", new BigDecimal(0));
                BigDecimal loanBalance_30_bad = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_30_BAD", new BigDecimal(0));
                double d0 = loanBalance.doubleValue();
                double d1 = loanBalance_10.doubleValue();
                double d2 = loanBalance_10_bad.doubleValue();
                double d3 = loanBalance_30.doubleValue();
                double d4 = loanBalance_30_bad.doubleValue();
                double d5 = loanBalance_20.doubleValue();
                double d6 = loanBalance_20_bad.doubleValue();
                double d7 = loanBalance_00.doubleValue();
                double d8 = loanBalance_00_bad.doubleValue();

                map1.put("MAIN_BR_NAME",MAIN_BR_NAME[i]);
                map1.put("LOAN_BALANCE_10",d1);
                map1.put("LOAN_BALANCE_10_BAD",d2);
                map1.put("LOAN_BALANCE_30",d1);
                map1.put("LOAN_BALANCE_30_BAD",d2);
                map1.put("LOAN_BALANCE_20",d1);
                map1.put("LOAN_BALANCE_20_BAD",d2);
                map1.put("LOAN_BALANCE_00",d1);
                map1.put("LOAN_BALANCE_00_BAD",d2);
                map1.put("LOAN_BALANCE",d0);
            }
            retAll.addAll(ret);
        }
        return retAll;
    }

    //前十大户和最大单户情况
    @ExportTo("前十大户和最大单户情况.xlsx")
    @UseSimpleSql(usePage = false)
    public void report_20(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //房地产开发贷款情况
    @ExportTo("房地产开发贷款情况.xlsx")
    public Object report_21(
            Map<String,Object> paramMap, Pageable pageable
    ){
        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        int iTypeFlag =0;
        int j = 0;
        String strTemp = "";
        //遍历所有贷款机构
        for(int i=0;i<MAIN_BR_NAME.length;i++) {
            Map map = new HashMap();
            map.put("MAIN_BR_ID", MAIN_BR_ID[i]);       //主管部门

            List<JSONObject> ret = sqlManager.select("report.report_21", JSONObject.class, map);
            for (JSONObject map1 : ret) {
                BigDecimal loanBalance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                BigDecimal loanBalance_00 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_00", new BigDecimal(0));
                BigDecimal loanBalance_10 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_10", new BigDecimal(0));
                BigDecimal loanBalance_20 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_20", new BigDecimal(0));
                BigDecimal loanBalance_30 = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_30", new BigDecimal(0));
                BigDecimal loanBalance_00_bad = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_00_BAD", new BigDecimal(0));
                BigDecimal loanBalance_10_bad = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_10_BAD", new BigDecimal(0));
                BigDecimal loanBalance_20_bad = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_20_BAD", new BigDecimal(0));
                BigDecimal loanBalance_30_bad = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_30_BAD", new BigDecimal(0));
                double d0 = loanBalance.doubleValue();
                double d1 = loanBalance_10.doubleValue();
                double d2 = loanBalance_10_bad.doubleValue();
                double d3 = loanBalance_30.doubleValue();
                double d4 = loanBalance_30_bad.doubleValue();
                double d5 = loanBalance_20.doubleValue();
                double d6 = loanBalance_20_bad.doubleValue();
                double d7 = loanBalance_00.doubleValue();
                double d8 = loanBalance_00_bad.doubleValue();

                map1.put("MAIN_BR_NAME",MAIN_BR_NAME[i]);
                map1.put("LOAN_BALANCE_10",d1);
                map1.put("LOAN_BALANCE_10_BAD",d2);
                map1.put("LOAN_BALANCE_30",d1);
                map1.put("LOAN_BALANCE_30_BAD",d2);
                map1.put("LOAN_BALANCE_20",d1);
                map1.put("LOAN_BALANCE_20_BAD",d2);
                map1.put("LOAN_BALANCE_00",d1);
                map1.put("LOAN_BALANCE_00_BAD",d2);
                map1.put("LOAN_BALANCE",d0);
            }
            retAll.addAll(ret);
        }
        return retAll;
    }

    //正常贷款的五大欠息户
    @ExportTo("正常贷款的五大欠息户.xlsx")
    @UseSimpleSql(usePage = false)
    public void report_25(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //新增贷款利率结构表
    @ExportTo("新增贷款利率结构表.xlsx")
    public Object report_26(
            Map<String,Object> paramMap, Pageable pageable
    ){
        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        //遍历所有贷款机构
        for(int i=0;i<TERM_TYPE.length;i++) {
            Map map = new HashMap();
            map.putAll(paramMap);
            //期限类型
            switch(i){
                case 0:
                    map.put("LOAN_TERM_MIN", 0);
                    map.put("LOAN_TERM_MAX", 12);
                    break;
                case 1:
                    map.put("LOAN_TERM_MIN", 12);
                    map.put("LOAN_TERM_MAX", 36);
                    break;
                case 2:
                    map.put("LOAN_TERM_MIN", 36);
                    map.put("LOAN_TERM_MAX", 60);
                    break;
                case 3:
                    map.put("LOAN_TERM_MIN", 60);
                    map.put("LOAN_TERM_MAX", 96);
                    break;
                case 4:
                    map.put("LOAN_TERM_MIN", 96);
                    map.put("LOAN_TERM_MAX", null);
                    break;
                default:
                    map.put("LOAN_TERM_MIN", null);
                    map.put("LOAN_TERM_MAX", null);
                    break;
            }

            List<JSONObject> ret = sqlManager.select("report.report_26", JSONObject.class, map);
            for (JSONObject map1 : ret) {
                BigDecimal total = (BigDecimal) map1.getOrDefault("TOTAL", new BigDecimal(0));
                BigDecimal total_1 = (BigDecimal) map1.getOrDefault("TOTAL_1", new BigDecimal(0));
                BigDecimal int_rate_g_1 = (BigDecimal) map1.getOrDefault("INT_RATE_G_1", new BigDecimal(0));
                BigDecimal int_rate_f_1 = (BigDecimal) map1.getOrDefault("INT_RATE_F_1", new BigDecimal(0));
                BigDecimal int_rate_f_2 = (BigDecimal) map1.getOrDefault("INT_RATE_F_2", new BigDecimal(0));
                BigDecimal int_rate_f_3 = (BigDecimal) map1.getOrDefault("INT_RATE_F_3", new BigDecimal(0));
                map1.put("LOAN_TERM",TERM_TYPE[i]);
                //double d0 = total.doubleValue();
                //map1.put("TOTAL",d0);

            }
            retAll.addAll(ret);
        }
        return retAll;
    }

    //新增贷款明细表
    @ExportTo("新增贷款明细表.xlsx")
    @UseSimpleSql
    public void report_27(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //新增不良贷款明细表
    @ExportTo("新增不良贷款明细表.xlsx")
    @UseSimpleSql
    public void report_28(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //逾期台帐明细表
    @ExportTo("逾期台帐明细表.xlsx")
    @UseSimpleSql
    public void report_29(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //逾期台帐统计表
    @ExportTo("逾期台帐统计表.xlsx")
    public Object report_30(
            Map<String,Object> paramMap, Pageable pageable
//            Integer LOAN_TERM_MIN,Integer LOAN_TERM_MAX
    ){
        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        String strTemp = "";
        //遍历所有贷款机构
        for(int i=0;i<MAIN_BR_NAME.length;i++) {
            Map map = new HashMap();
            map.put("MAIN_BR_ID", MAIN_BR_ID[i]);       //主管部门
            String strTerm = (String) paramMap.get("LOAN_TERM");
            switch(strTerm){
                case "0":
                    map.put("LOAN_TERM_MIN",null);
                    map.put("LOAN_TERM_MAX",null);
                    break;
                case "1":
                    map.put("LOAN_TERM_MIN",0);
                    map.put("LOAN_TERM_MAX",12);
                    break;
                case "2":
                    map.put("LOAN_TERM_MIN",12);
                    map.put("LOAN_TERM_MAX",36);
                    break;
                case "3":
                    map.put("LOAN_TERM_MIN",36);
                    map.put("LOAN_TERM_MAX",60);
                    break;
                case "4":
                    map.put("LOAN_TERM_MIN",60);
                    map.put("LOAN_TERM_MAX",96);
                    break;
                case "5":
                    map.put("LOAN_TERM_MIN",96);
                    map.put("LOAN_TERM_MAX",250);
                    break;
                default:
                    map.put("LOAN_TERM_MIN",null);
                    map.put("LOAN_TERM_MAX",null);
                    break;
            }
            map.putAll(paramMap);

            List<JSONObject> ret = sqlManager.select("report.report_30", JSONObject.class, map);
            for (JSONObject map1 : ret) {
//                BigDecimal loan_amount = (BigDecimal) map1.getOrDefault("LOAN_AMOUNT", new BigDecimal(0));
//                BigDecimal loan_balance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
//                BigDecimal unpd_prin_bal = (BigDecimal) map1.getOrDefault("UNPD_PRIN_BAL", new BigDecimal(0));
//                BigDecimal delay_int_cumu = (BigDecimal) map1.getOrDefault("DELAY_INT_CUMU", new BigDecimal(0));
//                BigDecimal total = (BigDecimal) map1.getOrDefault("TOTAL", new BigDecimal(0));
                map1.put("MAIN_BR_NAME",MAIN_BR_NAME[i]);
            }
            retAll.addAll(ret);
        }
        return retAll;
    }

    //新增贷款统计表
    @ExportTo("新增贷款统计表.xlsx")
    public Object report_31(
            Map<String,Object> paramMap, Pageable pageable
    ){
        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        String strTemp = "";
        //遍历所有贷款机构
        for(int i=0;i<MAIN_BR_NAME.length;i++) {
            Map map = new HashMap();
            map.put("MAIN_BR_ID", MAIN_BR_ID[i]);       //主管部门
            String strTerm = (String) paramMap.get("LOAN_TERM");
            switch(strTerm){
                case "0":
                    map.put("LOAN_TERM_MIN",null);
                    map.put("LOAN_TERM_MAX",null);
                    break;
                case "1":
                    map.put("LOAN_TERM_MIN",0);
                    map.put("LOAN_TERM_MAX",12);
                    break;
                case "2":
                    map.put("LOAN_TERM_MIN",12);
                    map.put("LOAN_TERM_MAX",36);
                    break;
                case "3":
                    map.put("LOAN_TERM_MIN",36);
                    map.put("LOAN_TERM_MAX",60);
                    break;
                case "4":
                    map.put("LOAN_TERM_MIN",60);
                    map.put("LOAN_TERM_MAX",96);
                    break;
                case "5":
                    map.put("LOAN_TERM_MIN",96);
                    map.put("LOAN_TERM_MAX",250);
                    break;
                default:
                    map.put("LOAN_TERM_MIN",null);
                    map.put("LOAN_TERM_MAX",null);
                    break;
            }
            map.putAll(paramMap);

            List<JSONObject> ret = sqlManager.select("report.report_31", JSONObject.class, map);
            for (JSONObject map1 : ret) {
                map1.put("MAIN_BR_NAME",MAIN_BR_NAME[i]);
            }
            retAll.addAll(ret);
        }
        return retAll;
    }

    //新增不良贷款统计表
    @ExportTo("新增不良贷款统计表.xlsx")
    public Object report_32(
            Map<String,Object> paramMap, Pageable pageable
    ){
//        long uid = AuthFilter.getUid();
//        paramMap.put("uid", uid);

        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        String strTemp = "";
        //遍历所有贷款机构
        for(int i=0;i<MAIN_BR_NAME.length;i++) {
            Map map = new HashMap();
            map.put("MAIN_BR_ID", MAIN_BR_ID[i]);       //主管部门
            String strTerm = (String) paramMap.get("LOAN_TERM");
            switch(strTerm){
                case "0":
                    map.put("LOAN_TERM_MIN","");
                    map.put("LOAN_TERM_MAX","");
                    break;
                case "1":
                    map.put("LOAN_TERM_MIN",0);
                    map.put("LOAN_TERM_MAX",12);
                    break;
                case "2":
                    map.put("LOAN_TERM_MIN",12);
                    map.put("LOAN_TERM_MAX",36);
                    break;
                case "3":
                    map.put("LOAN_TERM_MIN",36);
                    map.put("LOAN_TERM_MAX",60);
                    break;
                case "4":
                    map.put("LOAN_TERM_MIN",60);
                    map.put("LOAN_TERM_MAX",96);
                    break;
                case "5":
                    map.put("LOAN_TERM_MIN",96);
                    map.put("LOAN_TERM_MAX","");
                    break;
                default:
                    map.put("LOAN_TERM_MIN","");
                    map.put("LOAN_TERM_MAX","");
                    break;
            }
            map.putAll(paramMap);

            List<JSONObject> ret = sqlManager.select("report.report_32", JSONObject.class, map);
            for (JSONObject map1 : ret) {

                map1.put("MAIN_BR_NAME",MAIN_BR_NAME[i]);
            }
            retAll.addAll(ret);
        }

        return retAll;
    }

    //分期贷款单户明细表
    @ExportTo("分期贷款单户明细表.xlsx")
    public JSONObject report_33_head(
            Map<String,Object> paramMap, Pageable pageable
    ){
        JSONObject ret = sqlManager.selectSingle("report.report_33_1", paramMap, JSONObject.class);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        ret.put("PRINTDATE",df.format(new Date()));
        return ret;
    }

    public List<JSONObject> report_33_body(
            Map<String,Object> paramMap, Pageable pageable
    ){
        Date dtSrcSysNow = getSrcDateFromRpt();     //获取信贷中间表源系统日期
        List retAll = new ArrayList();
        String strTemp = "";
        Map map = new HashMap();
        //截取前16位
        String loan_account = ((String) paramMap.get("LOAN_ACCOUNT")).substring(0,16);
        map.put("LOAN_ACCOUNT",loan_account);
        //查询还款记录，并找出该台帐在历史表中的对应月份的贷款余额
        List<JSONObject> ret = sqlManager.select("report.report_33_2", JSONObject.class, map);
        //计算实还金额、实还本金、实还利息的合计
        BigDecimal BD_tot_amt = new BigDecimal(0);
        BigDecimal BD_prin_amt = new BigDecimal(0);
        BigDecimal BD_tot_int = new BigDecimal(0);
        double tot_amt = 0;
        double prin_amt = 0;
        double tot_int = 0;
        String str_repay_start_date = "";
        for (JSONObject map1 : ret) {
            //还款记录更新标志（是否补充还款计划）
            map1.put("UPDATE_FLAG",false);
            BigDecimal b1 = (BigDecimal) map1.getOrDefault("TOT_AMT", new BigDecimal(0));
            BigDecimal b2 = (BigDecimal) map1.getOrDefault("PRIN_AMT", new BigDecimal(0));
            BigDecimal b3 = (BigDecimal) map1.getOrDefault("TOT_INT", new BigDecimal(0));
            str_repay_start_date = (String) map1.get("REPAY_START_DATE");
            BD_tot_amt = BD_tot_amt.add(b1);
            BD_prin_amt = BD_prin_amt.add(b2);
            BD_tot_int = BD_tot_int.add(b3);
        }
        tot_amt = BD_tot_amt.doubleValue();
        prin_amt = BD_prin_amt.doubleValue();
        tot_int = BD_tot_int.doubleValue();

        //还款计划
        List<JSONObject> ret1 = sqlManager.select("report.report_33_3", JSONObject.class, paramMap);
        List<JSONObject> ret2 = new ArrayList();
        //还款计划还原
        String str_date = "";
        String str_times = "";
        String str_repay_cycle = "";
        String str_loan_account = "";
        int int_times = 0;
        int int_repay_cycle = 0;
        int int_addMonth = 0;
        for (JSONObject map3 : ret1) {
            str_date = (String) map3.get("REPAY_START_DATE");
            str_times = (String) map3.get("TIMES");
            str_repay_cycle = (String) map3.get("REPAY_CYCLE");
            str_loan_account = (String) map3.get("LOAN_ACCOUNT");
            BigDecimal repay_amount = (BigDecimal) map3.getOrDefault("REPAY_AMOUNT", new BigDecimal(0));
            //还款次数、还款周期转换为数字
            try {
                int_times = Integer.parseInt(str_times);
                int_repay_cycle = Integer.parseInt(str_repay_cycle);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            //还款周期转换：1-1个月 2-3个月 4-6个月 5-12个月
            switch(int_repay_cycle){
                case 1:
                    int_addMonth = 1;
                    break;
                case 2:
                    int_addMonth = 3;
                    break;
                case 4:
                    int_addMonth = 6;
                    break;
                case 5:
                    int_addMonth = 12;
                    break;
                default:
                    int_addMonth = 1;
                    break;
            }
//            //还款次数大于1，需要还原
//            if (int_times>1){
                //增加还款记录的还款日期
                String str_date_new = getAddMonth(str_date,0);
                for (int i = 0; i < int_times; i++) {
                    //增加一行（还款记录）
                    JSONObject map4 = new JSONObject();
                    map4.put("REPAY_START_DATE",getDateFormat(str_date_new));
                    map4.put("REPAY_AMOUNT",repay_amount);
                    map4.put("LOAN_ACCOUNT",str_loan_account);
                    ret2.add(map4);
                    str_date_new = getAddMonth(str_date_new,int_addMonth);
                }
//            }
        }
        //遍历还款计划，并入还款记录中
        List<JSONObject> ret3 = new ArrayList();
        for (JSONObject map5 : ret2) {
            String str_plan_time = (String) map5.get("REPAY_START_DATE");
            BigDecimal loan_balance = (BigDecimal) map5.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
            BigDecimal repay_amount = (BigDecimal) map5.getOrDefault("REPAY_AMOUNT", new BigDecimal(0));
            boolean b1=false;
            for(JSONObject map6 : ret){
                String str_real_time = (String) map6.get("REPAY_START_DATE");
                //判断还款日期是否相等，是则更新
                if (str_real_time !=null && str_plan_time.substring(0,6).equals(str_real_time.substring(0,6))){
                    map6.put("REPAY_AMOUNT",repay_amount);
                    map6.put("UPDATE_FLAG",true);
                    ret3.add(map6);
                    b1 = true;
                }

            }
            //遍历还款计划的还款日期没有在还款记录中，则插入新纪录
            if (b1==false) {
                JSONObject map7 = new JSONObject();
                map7.put("REPAY_START_DATE",str_plan_time);
                //map7.put("LOAN_BALANCE",loan_balance);
                map7.put("REPAY_AMOUNT",repay_amount);
                map7.put("TOT_AMT","0.00");
                map7.put("PRIN_AMT","0.00");
                map7.put("TOT_INT","0.00");
                map7.put("INT_AMT","0.00");
                map7.put("PIA_AMT","0.00");
                map7.put("IIA_AMT","0.00");
                map7.put("AFTER_INT_AMT","0.00");
                ret3.add(map7);
            }
        }
        //将还款记录中未更新的记录写到返回结果集中
        for(JSONObject map8 : ret){
            boolean bool = (boolean) map8.get("UPDATE_FLAG");
            if (bool==false){
                map8.put("REPAY_AMOUNT","0.00");
                ret3.add(map8);
            }
        }

        //排序
        Collections.sort(ret3, new Comparator<Map>() {
            @Override
            public int compare(Map o1, Map o2) {
                // 升序
                String a = (String) o1.get("REPAY_START_DATE");
                if(null == a){
                    a = "0";
                }
                String b = (String) o2.get("REPAY_START_DATE");
                if(null == b){
                    b = "0";
                }
                return a.compareTo(b);
            }
        });

        //增加一行（合计）
        JSONObject map2 = new JSONObject();
        map2.put("SERNO","合计");
        map2.put("TOT_AMT",tot_amt);
        map2.put("PRIN_AMT",prin_amt);
        map2.put("TOT_INT",tot_int);
        map2.put("UPDATE_FLAG",false);
        ret3.add(map2);

        return ret3;
    }

    //信贷中间表
    @ExportTo("信贷中间表.xlsx")
    @UseSimpleSql(usePage = false)
    public void rpt(
            Map<String,Object> paramMap, Pageable pageable
    ){
    }

    //抵押物明细（全）
    public void mortgage(
            Map<String,Object> paramMap, Pageable pageable
    ){
        List<JSONObject> ret = sqlManager.select("report.mortgage", JSONObject.class, paramMap);

        //return ret;
    }

    //抵押物明细
//    @ExportTo("抵押物明细.xlsx")
//    @UseSimpleSql
    public List<JSONObject> mortgage_g(
            Map<String,Object> paramMap, Pageable pageable
    ){
        List<JSONObject> ret = sqlManager.select("report.mortgage_g", JSONObject.class, paramMap);
        return ret;
    }

    //质押物明细
//    @ExportTo("质押物明细.xlsx")
//    @UseSimpleSql
    public List<JSONObject> mortgage_p(
            Map<String,Object> paramMap, Pageable pageable
    ){
        List<JSONObject> ret = sqlManager.select("report.mortgage_p", JSONObject.class, paramMap);
        return ret;
    }

    //保证人明细
//    @ExportTo("保证人明细.xlsx")
//    @UseSimpleSql
    public List<JSONObject> mortgage_er(
            Map<String,Object> paramMap, Pageable pageable
    ){
        List<JSONObject> ret = sqlManager.select("report.mortgage_er", JSONObject.class, paramMap);
        return ret;
    }

    public Object report(Pageable pageable){
        Map map = new HashMap();
        PageQuery pageQuery = new PageQuery<>(pageable.getPageNumber(),pageable.getPageSize());
        pageQuery.setParas(map);
        PageQuery pageQuery1 = sqlManager.pageQuery("report.report_1", Map.class, pageQuery);
        return new PageImpl<>(pageQuery1.getList(), pageable, pageQuery1.getTotalRow());
    }

    //获取信贷中间表源系统日期
    public Date getSrcDateFromRpt(){
        Date dtResult = new Date();
        String strResult = "";
        Map map = new HashMap();
        List<JSONObject> ret = sqlManager.select("report.get_rpt_now", JSONObject.class, map);
        for (JSONObject map1 : ret) {
            String srcSysDate = (String) map1.get("SRC_SYS_DATE");
/*            //设置小数位数，第一个变量是小数位数，第二个变量是取舍方法(四舍五入)
            srcSysDate=srcSysDate.setScale(0, BigDecimal.ROUND_HALF_UP);
            //转化为字符串输出
            strResult=srcSysDate.toString();*/
            //注意：SimpleDateFormat构造函数的样式与strDate的样式必须相符
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //加上时间
            //必须捕获异常
            try {
                dtResult=simpleDateFormat.parse(srcSysDate);
            } catch(ParseException px) {
                px.printStackTrace();
            }
        }
        return dtResult;
    }


    //APP接口-获取用户信息
    public List<JSONObject> app_userstatus(
            Map<String,Object> paramMap, Pageable pageable
    ){
        long uid = AuthFilter.getUid();
        paramMap.put("uid", uid);
        List retAll = new ArrayList();
        JSONObject map3 = new JSONObject();

        List<JSONObject> ret1 = sqlManager.select("report.app_userstatus_1", JSONObject.class, paramMap);
        int i = 0;
        for (JSONObject map1 : ret1) {
//            BigDecimal is_homebank = (BigDecimal) map1.getOrDefault("IS_HOMEBANK", new BigDecimal(0));
            int is_homebank = Integer.parseInt(map1.get("IS_HOMEBANK").toString());
            //
            switch(i){
                case 0:
                    if (is_homebank>0){
                        is_homebank =1;
                    }
                    map3.put("IS_HOMEBANK",is_homebank);
                    i++;
                    break;
                case 1:
                    map3.put("IS_LOANBANK",is_homebank);
                    i++;
                    break;
                case 2:
                    map3.put("IS_LOANBANK_M",is_homebank);
                    i++;
                    break;
                default:
                    map3.put("IS_NULL",is_homebank);
                    break;
            }
        }
        map3.put("MAIN_PARENT_ID","");
        map3.put("MAIN_PARENT_NAME","");

        List<JSONObject> ret2 = sqlManager.select("report.app_userstatus_2", JSONObject.class, paramMap);
        for (JSONObject map2 : ret2) {
            String str_parent_id = (String) map2.get("PARENT_ID");
            String str_main_br_name_new_lve_one = (String) map2.get("MAIN_BR_NAME_NEW_LVE_ONE");
            map3.put("MAIN_PARENT_ID",str_parent_id);
            map3.put("MAIN_PARENT_NAME",str_main_br_name_new_lve_one);
        }

        retAll.add(map3);

        return retAll;

    }

    //APP接口-获取台帐五级分类统计结果
    public List<JSONObject> app_cla(
            Map<String,Object> paramMap, Pageable pageable
    ){
        long uid = AuthFilter.getUid();
        paramMap.put("uid", uid);

        //判断请求是否填写LOAN_TYPE贷款类型参数
        if(S.empty((CharSequence) paramMap.get("LOAN_TYPE"))){
        }
        else {
            String str_type = (String) paramMap.get("LOAN_TYPE");
            switch(str_type){
                case "00":
                    break;
                case "01":
                    paramMap.put("LOAN_TYPE_C","3002");     //对公台帐
                    break;
                case "02":
                    paramMap.put("LOAN_TYPE_C","3001");     //对私台帐
                    break;
                default:    //参数填错返回所有客户
                    break;
            }
        }

        //判断请求是否填写LOAN_CLA_TYPE贷款五级类型参数
        if(S.empty((CharSequence) paramMap.get("LOAN_CLA_TYPE"))){
        }
        else {
            String str_type = (String) paramMap.get("LOAN_CLA_TYPE");
            switch(str_type){
                case "00":
                    break;
                case "01":
                    paramMap.put("LOAN_CLA_TYPE_01",1);     //正常
                    break;
                case "02":
                    paramMap.put("LOAN_CLA_TYPE_02",1);     //逾期
                    break;
                case "03":
                    paramMap.put("LOAN_CLA_TYPE_03",getPerFirstDayOfMonth());     //隐性不良,传入参数值：下个月1号 格式：yyyyMMdd
                    break;
                case "04":
                    paramMap.put("LOAN_CLA_TYPE_04",1);     //不良
                    break;
                default:    //参数填错返回所有客户
                    break;
            }
        }

        List retAll = new ArrayList();
        BigDecimal BD_tot_num = new BigDecimal(0);
        BigDecimal BD_tot_balance = new BigDecimal(0);

        List<JSONObject> ret = sqlManager.select("report.app_cla", JSONObject.class, paramMap);
        //求和（笔数、贷款余额）
        for (JSONObject map1 : ret) {
            BigDecimal loan_num = new BigDecimal(map1.get("LOAN_NUM").toString());
            BigDecimal loan_balance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
            BD_tot_num = BD_tot_num.add(loan_num);
            BD_tot_balance = BD_tot_balance.add(loan_balance);
        }

        //算占比
        for (JSONObject map2 : ret) {
            BigDecimal loan_num = new BigDecimal(map2.get("LOAN_NUM").toString());
            BigDecimal loan_balance = (BigDecimal) map2.getOrDefault("LOAN_BALANCE", new BigDecimal(0));

            BigDecimal ir_num_B = new BigDecimal(0);
            BigDecimal ir_balance_B = new BigDecimal(0);
            if (BD_tot_num.compareTo(BigDecimal.ZERO)!=0) {
                BigDecimal percent = new BigDecimal(100);
                ir_num_B = loan_num.multiply(percent);
                ir_num_B = ir_num_B.divide(BD_tot_num, 2, BigDecimal.ROUND_HALF_UP);
            }
            if (BD_tot_balance.compareTo(BigDecimal.ZERO)!=0) {
                BigDecimal percent = new BigDecimal(100);
                ir_balance_B = loan_balance.multiply(percent);
                ir_balance_B = ir_balance_B.divide(BD_tot_balance, 2, BigDecimal.ROUND_HALF_UP);
            }

            map2.put("IR_NUM",ir_num_B);
            map2.put("IR_BALANCE",ir_balance_B);
        }

        retAll.addAll(ret);

        //增加一行（合计）
        JSONObject map3 = new JSONObject();
        map3.put("IR_NUM",1);
        map3.put("IR_BALANCE",1);
        map3.put("CLA","合计");
        map3.put("LOAN_NUM",BD_tot_num);
        map3.put("LOAN_BALANCE",BD_tot_balance);
        retAll.add(map3);

        return retAll;

    }

    //APP接口-获取用户列表
//    public List<JSONObject> app_cus(
    public Object app_cus(
            Map<String,Object> paramMap, Pageable pageable
    ){
        long uid = AuthFilter.getUid();
        paramMap.put("uid", uid);

//        List<JSONObject> ret = sqlManager.select("report.app_cus_1", JSONObject.class, paramMap);
//        return ret;

        PageQuery pageQuery = new PageQuery<>(pageable.getPageNumber(),pageable.getPageSize());
        if(pageQuery.getPageNumber() == 0) pageQuery.setPageNumber(1);
        pageQuery.setParas(paramMap);
//        if(pageQuery.getPageSize() >= 2000){
//            pageQuery.setPageSize(5000);
//        }
        //参数不填返回所有客户
        if(S.empty((CharSequence) paramMap.get("CUS_TYPE"))){
            return sqlManager.pageQuery("report.app_cus_1", JSONObject.class, pageQuery);
        };
        String str_type = (String) paramMap.get("CUS_TYPE");
        switch(str_type){
            case "00":
                return sqlManager.pageQuery("report.app_cus_1", JSONObject.class, pageQuery);
//                break;
            case "01":
                return sqlManager.pageQuery("report.app_cus_2", JSONObject.class, pageQuery);
//                break;
            case "02":
                return sqlManager.pageQuery("report.app_cus_3", JSONObject.class, pageQuery);
//                break;
            case "03":
                return sqlManager.pageQuery("report.app_cus_4", JSONObject.class, pageQuery);
//                break;
            default:    //参数填错返回所有客户
                return sqlManager.pageQuery("report.app_cus_1", JSONObject.class, pageQuery);
//                break;
        }
//        return sqlManager.pageQuery("report.app_cus_3", JSONObject.class, pageQuery);
    }


    //APP接口-获取台帐列表
    public Object app_loan(
            Map<String,Object> paramMap, Pageable pageable
    ){
        long uid = AuthFilter.getUid();
        paramMap.put("uid", uid);

        //判断请求是否填写LOAN_TYPE贷款类型参数
        if(S.empty((CharSequence) paramMap.get("LOAN_TYPE"))){
        }
        else {
            String str_type = (String) paramMap.get("LOAN_TYPE");
            switch(str_type){
                case "00":
                    break;
                case "01":
                    paramMap.put("LOAN_TYPE_C","3002");     //对公台帐
                    break;
                case "02":
                    paramMap.put("LOAN_TYPE_C","3001");     //对私台帐
                    break;
                default:    //参数填错返回所有客户
                    break;
            }
        }

        //判断请求是否填写LOAN_CLA_TYPE贷款五级类型参数
        if(S.empty((CharSequence) paramMap.get("LOAN_CLA_TYPE"))){
        }
        else {
            String str_type = (String) paramMap.get("LOAN_CLA_TYPE");
            switch(str_type){
                case "00":
                    break;
                case "01":
                    paramMap.put("LOAN_CLA_TYPE_01",1);     //正常
                    break;
                case "02":
                    paramMap.put("LOAN_CLA_TYPE_02",1);     //逾期
                    break;
                case "03":
                    paramMap.put("LOAN_CLA_TYPE_03",getPerFirstDayOfMonth());     //隐性不良,传入参数值：下个月1号 格式：yyyyMMdd
                    break;
                case "04":
                    paramMap.put("LOAN_CLA_TYPE_04",1);     //不良
                    break;
                default:    //参数填错返回所有客户
                    break;
            }
        }

        //判断请求是否填写GET_FLAG取数标志参数
        if(S.empty((CharSequence) paramMap.get("GET_FLAG"))){
        }
        else {
            String str_type = (String) paramMap.get("GET_FLAG");
            //传入本月第一天和最后一天的日期
            paramMap.put("START_DATE",getFirstDayOfMonth());     //本月第一天
            paramMap.put("END_DATE",getLastDayOfMonth());     //本月最后一天
//            paramMap.put("START_DATE","20181201");     //测试
//            paramMap.put("END_DATE","20181231");     //测试
            switch(str_type){
                case "01":
                    paramMap.put("GET_FLAG_01",1);     //前十大户
                    return sqlManager.select("report.app_loan_1" , JSONObject.class, paramMap);
//                    break;
                case "02":
                    paramMap.put("GET_FLAG_02",1);     //本月新增
                    String temp = (String) paramMap.get("LOAN_CLA_TYPE");
                    if (temp.equals("04")){
                        PageQuery pageQuery = new PageQuery<>(pageable.getPageNumber(),pageable.getPageSize());
                        if(pageQuery.getPageNumber() == 0) pageQuery.setPageNumber(1);
                        pageQuery.setParas(paramMap);
                        return sqlManager.pageQuery("report.app_loan_2", JSONObject.class, pageQuery);
                    }
                    break;
                case "03":
                    paramMap.put("GET_FLAG_03",1);     //本月到期
                    break;
                case "04":
                    paramMap.put("GET_FLAG_04",1);     //本月新增催收
                    break;
                case "05":
                    paramMap.put("GET_FLAG_05",1);     //本月新增诉讼
                    break;
                case "06":
                    paramMap.put("GET_FLAG_06",1);     //本月新增利息减免
                    break;
                case "07":
                    paramMap.put("GET_FLAG_07",1);     //本月新增抵债资产接收
                    break;
                case "08":
                    paramMap.put("GET_FLAG_08",1);     //本月新增资产处置
                    break;
                default:    //参数填错返回所有客户
                    break;
            }
        }


        PageQuery pageQuery = new PageQuery<>(pageable.getPageNumber(),pageable.getPageSize());
        if(pageQuery.getPageNumber() == 0) pageQuery.setPageNumber(1);
        pageQuery.setParas(paramMap);
        return sqlManager.pageQuery("report.app_loan_1", JSONObject.class, pageQuery);
    }

    //APP接口-获取台帐分类(正常/正常逾期/隐性不良/不良)统计结果
    public List<JSONObject> app_loan_class_all(
            Map<String,Object> paramMap, Pageable pageable
    ){
        long uid = AuthFilter.getUid();
        paramMap.put("uid", uid);

        //判断请求是否填写LOAN_TYPE贷款类型参数
        if(S.empty((CharSequence) paramMap.get("LOAN_TYPE"))){
        }
        else {
            String str_type = (String) paramMap.get("LOAN_TYPE");
            switch(str_type){
                case "00":
                    break;
                case "01":
                    paramMap.put("LOAN_TYPE_C","3002");     //对公台帐
                    break;
                case "02":
                    paramMap.put("LOAN_TYPE_C","3001");     //对私台帐
                    break;
                default:    //参数填错返回所有客户
                    break;
            }
        }

        paramMap.put("LOAN_CLA_TYPE_03",getPerFirstDayOfMonth());     //隐性不良,传入参数值：下个月1号 格式：yyyyMMdd

        List retAll = new ArrayList();
        BigDecimal BD_tot_balance = new BigDecimal(0);

        List<JSONObject> ret = sqlManager.select("report.app_loan_class", JSONObject.class, paramMap);
        //求和（笔数、贷款余额）
        for (JSONObject map1 : ret) {
            BigDecimal loan_balance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
            BigDecimal loan_balance_zc = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_ZC", new BigDecimal(0));
            BigDecimal loan_balance_zcyq = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_ZCYQ", new BigDecimal(0));
            BigDecimal loan_balance_yxbl = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_YXBL", new BigDecimal(0));
            BigDecimal loan_balance_bl = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_BL", new BigDecimal(0));

            BigDecimal ir_balance_zc = new BigDecimal(0);
            BigDecimal ir_balance_zcyq = new BigDecimal(0);
            BigDecimal ir_balance_yxbl = new BigDecimal(0);
            BigDecimal ir_balance_bl = new BigDecimal(0);
            if (loan_balance.compareTo(BigDecimal.ZERO)!=0) {
                BigDecimal percent = new BigDecimal(100);
                ir_balance_zc = loan_balance_zc.multiply(percent);
                ir_balance_zcyq = loan_balance_zcyq.multiply(percent);
                ir_balance_yxbl = loan_balance_yxbl.multiply(percent);
                ir_balance_bl = loan_balance_bl.multiply(percent);
                ir_balance_zc = ir_balance_zc.divide(loan_balance, 2, BigDecimal.ROUND_HALF_UP);
                ir_balance_zcyq = ir_balance_zcyq.divide(loan_balance, 2, BigDecimal.ROUND_HALF_UP);
                ir_balance_yxbl = ir_balance_yxbl.divide(loan_balance, 2, BigDecimal.ROUND_HALF_UP);
                ir_balance_bl = ir_balance_bl.divide(loan_balance, 2, BigDecimal.ROUND_HALF_UP);
            }
            map1.put("IR_BALANCE_ZC",ir_balance_zc);
            map1.put("IR_BALANCE_ZCYQ",ir_balance_zcyq);
            map1.put("IR_BALANCE_YXBL",ir_balance_yxbl);
            map1.put("IR_BALANCE_BL",ir_balance_bl);
        }

        retAll.addAll(ret);

        return retAll;

    }

    //APP接口-获取台帐分类(正常/正常逾期/隐性不良/不良)统计结果
    public List<JSONObject> app_loan_class(
            Map<String,Object> paramMap, Pageable pageable
    ){
        long uid = AuthFilter.getUid();
        paramMap.put("uid", uid);

        //判断请求是否填写LOAN_TYPE贷款类型参数
        if(S.empty((CharSequence) paramMap.get("LOAN_TYPE"))){
        }
        else {
            String str_type = (String) paramMap.get("LOAN_TYPE");
            switch(str_type){
                case "00":
                    break;
                case "01":
                    paramMap.put("LOAN_TYPE_C","3002");     //对公台帐
                    break;
                case "02":
                    paramMap.put("LOAN_TYPE_C","3001");     //对私台帐
                    break;
                default:    //参数填错返回所有客户
                    break;
            }
        }

        List<JSONObject> ret = null;

        //判断请求是否填写LOAN_CLA_TYPE贷款五级类型参数
        if(S.empty((CharSequence) paramMap.get("LOAN_CLA_TYPE"))){
        }
        else {
            String str_type = (String) paramMap.get("LOAN_CLA_TYPE");
            switch(str_type){
                case "00":
                    ret = sqlManager.select("report.app_loan_class_zc", JSONObject.class, paramMap);
                    break;
                case "01":
                    ret = sqlManager.select("report.app_loan_class_zc", JSONObject.class, paramMap);
                    break;
                case "02":
                    ret = sqlManager.select("report.app_loan_class_zcyq", JSONObject.class, paramMap);
                    break;
                case "03":
                    paramMap.put("LOAN_CLA_TYPE_03",getPerFirstDayOfMonth());     //隐性不良,传入参数值：下个月1号 格式：yyyyMMdd
                    ret = sqlManager.select("report.app_loan_class_yxbl", JSONObject.class, paramMap);
                    break;
                case "04":
                    ret = sqlManager.select("report.app_loan_class_bl", JSONObject.class, paramMap);
                    break;
                default:    //参数填错返回所有客户
                    ret = sqlManager.select("report.app_loan_class_zc", JSONObject.class, paramMap);
                    break;
            }
        }

        List retAll = new ArrayList();
        BigDecimal BD_tot_balance = new BigDecimal(0);

        if (ret != null) {
            //求和（笔数、贷款余额）
            for (JSONObject map1 : ret) {
                BigDecimal loan_balance = (BigDecimal) map1.getOrDefault("LOAN_BALANCE", new BigDecimal(0));
                BigDecimal loan_balance_count = (BigDecimal) map1.getOrDefault("LOAN_BALANCE_COUNT", new BigDecimal(0));

                BigDecimal ir_balance = new BigDecimal(0);
                if (loan_balance.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal percent = new BigDecimal(100);
                    ir_balance = loan_balance_count.multiply(percent);
                    ir_balance = ir_balance.divide(loan_balance, 2, BigDecimal.ROUND_HALF_UP);
                }
                map1.put("IR_BALANCE_ZC", ir_balance);
            }
            retAll.addAll(ret);
        }


        return retAll;

    }


    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(BigDecimal v1,BigDecimal v2){
        return v1.subtract(v2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(BigDecimal v1,BigDecimal v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        //BigDecimal b1 = new BigDecimal(Double.toString(v1));
        //BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return v2.divide(v1,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
//        return v2.divide(v1,scale,BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v,int scale){
        if(scale<0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取某年某月最后一天的日期。
     * @param y 某年 yyyy
     * @param m 某月  mm
     * @return 某月最后一天的日期
     */
    public static String getLastDay(String y,String m){
        // 获取当月的天数（需完善）
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        // 定义当前期间的1号的date对象
        Date date = null;
        String strDate=y+m+"01";
        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,1);//月增加1天
        calendar.add(Calendar.DAY_OF_MONTH,-1);//日期倒数一日,既得到本月最后一天
        Date voucherDate = calendar.getTime();
        return dateFormat.format(voucherDate);
    }

    /**
     * 获取某年某月最后一天的日期。
     * @param dt1 日期
     * @return 某月最后一天的日期
     */
    public static String getLastDay(Date dt1){
        // 获取当月的天数（需完善）
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        // 定义当前期间的1号的date对象
        Date date = dt1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,1);//月增加1天
        calendar.add(Calendar.DAY_OF_MONTH,-1);//日期倒数一日,既得到本月最后一天
        Date voucherDate = calendar.getTime();
        return dateFormat.format(voucherDate);
    }

    /**
     * 获取当前年份
     * @return 当前年份
     */
    public static String getCurrentYear(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获取当前月份
     * @return 当前月份
     */
    public static String getCurrentMonth(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获取指定时间的年份
     * @return 指定时间的年份
     */
    public static String getCurrentYear(Date dt1){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(dt1);
    }

    /**
     * 获取指定时间加addMM个月后的时间
     * @return 指定时间加addMM个月后的时间
     */
    public static String getAddMonth(String strDate,int addMM){
        //注意：SimpleDateFormat构造函数的样式与strDate的样式必须相符
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd"); //加上时间

        //必须捕获异常
        try {
            Date date=simpleDateFormat.parse(strDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.MONTH, addMM);//把日期往后增加一个月.整数往后推,负数往前移动
            date=calendar.getTime();   //这个时间就是日期往后推一天的结果

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);

        } catch(ParseException px) {
            px.printStackTrace();
            return "0000-00-00";
        }
    }
    /**
     * 获取
     * @return
     */
    public static String getDateFormat(String strDate){
        //注意：SimpleDateFormat构造函数的样式与strDate的样式必须相符
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        //必须捕获异常
        try {
            Date date=simpleDateFormat.parse(strDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(date);

        } catch(ParseException px) {
            px.printStackTrace();
            return "0000-00-00";
        }
    }

    /**
     *
     * 描述:获取下一个月的第一天.
     *
     * @return
     */
    public static String getPerFirstDayOfMonth() {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }

    /**
     *
     * 描述:获取当前月的第一天.
     *
     * @return
     */
    public static String getFirstDayOfMonth() {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        return dft.format(calendar.getTime());
    }

    /**
     *
     * 描述:获取当前月的最后一天.
     *
     * @return
     */
    public static String getLastDayOfMonth() {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }


    //流水号长度为2
    private static final String STR_FORMAT = "00";

    public static String haoAddOne(String liuShuiHao){
        Integer intHao = Integer.parseInt(liuShuiHao);
        //intHao++;
        DecimalFormat df = new DecimalFormat(STR_FORMAT);
        return df.format(intHao);
    }

    private String sGetS(Map map, String key, String defaultValue){
        String value = (String) map.get(key);
        if(S.blank(value)){
            return defaultValue;
        }
        return value;
    }


    public PageQuery query(String no, Map params, Pageable pageable){
        PageQuery pageQuery = new PageQuery<>(pageable.getPageNumber(),pageable.getPageSize());
        if(pageQuery.getPageNumber() == 0) pageQuery.setPageNumber(1);
        pageQuery.setParas(params);
        if(pageQuery.getPageSize() >= 2000){
            pageQuery.setPageSize(5000);
        }
        return sqlManager.pageQuery("report." + no, JSONObject.class, pageQuery);
    }



    public class constant {
        //贷款机构代码
        public  String BR1[] = {"09129","09135"};               //中心支行
        public  String BR2[] = {"09131","09224","09214","09216","09213"};              //惠城支行
        public  String BR3[] = {"09137","09152","09188","09183"};               //小金口支行
        public  String BR4[] = {"09134","09208"};       //水口支行
        public  String BR5[] = {"09136","09229","09185","09159"};               //江南支行
        public  String BR6[] = {"09323","09357","09373","09348","09375","09371","09231"};      //惠阳支行
        public  String BR7[] = {"09139","09362","09366","09378"};              //新圩支行
        public  String BR8[] = {"09138","09358"};      //秋长支行
        public  String BR9[] = {"09132","09196","09166","09189","09202","09204"};              //仲恺支行
        public  String BR10[] = {"09133","09343","09346","09133"};              //大亚湾支行
        public  String BR11[] = {"09226"};               //惠城小微企业金融部
        public  String BR12[] = {"09225"};               //仲恺小微企业金融部
        public  String BR13[] = {"09142"};               //惠阳小微金融部
        public  String BR14[] = {"09129","09135","09131","09224","09214","09216","09213","09137",
                "09152","09188","09183","09134","09208","09136","09229","09185","09159","09323",
                "09357","09373","09348","09375","09371","09231","09139","09362","09366","09378",
                "09138","09358","09132","09196","09166","09189","09202","09204","09226","09225",
                "09142"};              //大亚湾支行
    }

//    @Getter
//    @Setter
//    public static class Test{
//        private int a;
//        private int b;
//        private int c;
//
//    }
}
