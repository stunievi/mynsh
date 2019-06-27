package com.beeasy.hzlink.ctrl;

import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzlink.model.Link111;
import com.beeasy.hzlink.model.TSystemVariable;
import com.github.llyb120.nami.core.MultipartFile;
import com.github.llyb120.nami.excel.ExportUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.beetl.core.resource.ClasspathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.llyb120.nami.core.DBService.sqlManager;
import static com.github.llyb120.nami.core.Json.a;
import static com.github.llyb120.nami.core.Json.o;

public class FileController {

    // 任务产生条件
    public MultipartFile ruleTask(){

        try {
            var lists = sqlManager.lambdaQuery(TSystemVariable.class).andLike(TSystemVariable::getVar_name,"ACC_%").select();

            var data = a();

            JSONArray resp = new JSONArray();
            // 数据处理
            for (int i=0;i<lists.size();i++){
                JSONObject jsonObject = new JSONObject();
                for (TSystemVariable list : lists) {
                    String regEx="[^0-9]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(list.getVar_name());
                    if(i == Integer.parseInt(m.replaceAll("").trim())){
                        jsonObject.put(list.getVar_name().replaceAll("\\d+", ""), list.getVar_value());
                    }
                }
                if(!jsonObject.isEmpty()){
                    resp.add(jsonObject);
                }
            }

            for(int i=0;i<resp.size();i++){

                JSONObject job = resp.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                map.put("BIZ_TYPE",job.getString("ACC__BIZ_TYPE"));
                map.put("LOAN_CHECK",job.getString("ACC__LOAN_CHECK"));
                map.put("LOAN_AMOUNT_MAX",job.getString("ACC__LOAN_AMOUNT_MAX"));
                map.put("LOAN_AMOUNT_MIN",job.getString("ACC__LOAN_AMOUNT_MIN"));
                map.put("EXPECT_DAY",job.getString("ACC__EXPECT_DAY"));
                data.add(map);

            }

            return ExportUtil.toXls("贷后检查任务产生规则.xlsx", new ClassPathResource("excel/report_34.xlsx").getStream(), o(
                    "values", data
            ));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 关联方清单模板下载
    public MultipartFile linkListDownload(){
        File file1 = null;
        try{
            file1 = new ClassPathResource("excel/report_1.xlsx").getFile();
//            file1 = new File("src/main/resources/excel/report_1.xlsx");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new MultipartFile("惠州农村商业银行股份有限公司关联方名单模板.xlsx", file1);
    }
    // 股东清单模板下载
    public MultipartFile shareholderListDownload(){
        File file1 = null;
        try{
            file1 = new ClassPathResource("excel/report_2.xls").getFile();
//            file1 = new File("src/main/resources/excel/report_2.xls");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new MultipartFile("股东明细导入格式.xls", file1);
    }

    // 企业名单模板下载
    public MultipartFile enterprisesListDownload(){
        File file1 = null;
        try{
            file1 = new ClassPathResource("excel/report_3.xlsx").getFile();
//            file1 = new File("src/main/resources/excel/report_3.xlsx");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new MultipartFile("惠州农村商业银行股份有限公司重点企业名单.xlsx", file1);
    }

    // 集团客户清单导出
    public MultipartFile rule11(){

        try {
            var lists = sqlManager.lambdaQuery(Link111.class).andEq(Link111::getLink_rule,"11.1").orEq(Link111::getLink_rule,"11.2").orEq(Link111::getLink_rule,"11.3").orEq(Link111::getLink_rule,"11.4").orEq(Link111::getLink_rule,"11.5").orEq(Link111::getLink_rule,"11.6").select();

            var data = a();
            int i=0;
            for (Link111 list : lists) {
                i++;
                Map<String, Object> map = new HashMap<>();
                map.put("number",i);
                map.put("cusName",list.getOrigin_name());
                map.put("enterpriseName",list.getLink_left());
                data.add(map);
            }
            return ExportUtil.toXls("集团客户清单.xls", new FileInputStream("e:/附件6-集团客户明细导出模板.xls"), o(
                    "values", data
            ));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
