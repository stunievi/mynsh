package com.beeasy.hzreport.controller;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzreport.service.ExcelService;
import com.beeasy.hzreport.service.ReportService;
import com.beeasy.mscommon.filter.AuthFilter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/report/csv")
public class ExportCSVController {

    @Autowired
    ExcelService excelService;
    @Autowired
    ReportService reportService;
    @Autowired
    SQLManager sqlManager;

    private static class Point {
        int x;
        int y;
        String value;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @RequestMapping(value = "/credit")
    public void exportex(
            @RequestParam Map<String,Object> params
            , @PageableDefault(value = 5000, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, HttpServletResponse response
    ) throws ClassNotFoundException {
        String no = "rpt";
        try {
            params.put("uid", AuthFilter.getUid());
            Method method = reportService.getClass().getMethod(no, Map.class , Pageable.class);

            Object result;

            BufferedWriter csvWtriter = null;
            try{
                //文件下载
                response.setContentType("application/csv;charset=gb18030");
                response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode("信贷中间表.csv", "UTF-8"));
                ServletOutputStream out = response.getOutputStream();
                csvWtriter = new BufferedWriter(new OutputStreamWriter(out, "GBK"), 1024);

                boolean flag = true;
                int i = 0;

                while (flag){
                    i++;
                    result= query(i,no, params, pageable);

                    if(((PageQuery) result).getList().isEmpty()){
                        flag = false;
                    }else{
                        List<List<JSONObject>> list = C.newList();
                        list.add((List<JSONObject>) ((PageQuery) result).getList());
                        createCSV(i,no + ".xlsx", list,csvWtriter);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    csvWtriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*if(result instanceof List){
                List<List<JSONObject>> list = C.newList();
                list.add((List<JSONObject>) result);
//                bytes = createCSV(no + ".xlsx", list,rsponse);
            }*/
        } catch (NoSuchMethodException e) {
        }
    }

    private PageQuery query(int i, String no, Map params, Pageable pageable){
        PageQuery pageQuery = new PageQuery<>(pageable.getPageNumber(),pageable.getPageSize());

        pageQuery.setParas(params);
        pageQuery.setPageNumber(i);
        pageQuery.setPageSize(5000);
        return sqlManager.pageQuery("report." + no+"_export", JSONObject.class, pageQuery);
    }

    /**
     * 写入数据
     * @param num
     * @param tmpl
     * @param datas
     * @param csvWtriter
     * @return
     */
    private void createCSV(int num, String tmpl, List<List<JSONObject>> datas, BufferedWriter csvWtriter) {
        ClassPathResource resource = new ClassPathResource("excel/" + tmpl);

        // 表格头
        List<Object> headList = new ArrayList<>();

        //数据
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        List<Object> rowList = null;

//        String fileName = "testCSV.csv";//文件名称
//        String filePath = "e:/test/"; //文件路径
//
//        File csvFile = null;
//        BufferedWriter csvWtriter = null;
        try {
//            csvFile = new File(filePath + fileName);
//            File parent = csvFile.getParentFile();
//            if (parent != null && !parent.exists()) {
//                parent.mkdirs();
//            }
//            csvFile.createNewFile();

//            //文件下载
//            response.setContentType("application/csv;charset=gb18030");
//            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
//            ServletOutputStream out = response.getOutputStream();
//            csvWtriter = new BufferedWriter(new OutputStreamWriter(out, "GB2312"), 1024);

            InputStream fis = resource.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            int $count = 0;
            for (List<JSONObject> data : datas) {
                XSSFSheet sheet = workbook.getSheetAt($count++);
                Point start = null;
                int maxcol = 0;
                boolean isTarget = false;
                boolean flag = true;
                Map<Short, String> fields = new HashMap<>();
                scan:
                {
                    for (short i = 0; i <= sheet.getLastRowNum(); i++) {
                        XSSFRow row = sheet.getRow(i);
                        if (null == row) {
                            continue;
                        }
                        for (short j = 0; j <= row.getLastCellNum(); j++) {
                            XSSFCell cell = row.getCell(j);
                            if (null == cell) {
                                continue;
                            }
                            String value;
                            try{
                                value = cell.getStringCellValue().trim();
                                if(flag){
                                    headList.add(value);
                                }
                            }
                            catch (Exception e){
                                continue;
                            }
                            if (value.startsWith("$")) {
                                value = value.substring(1);
                                start = new Point(i, j);
                                isTarget = true;
                                flag = false;
                            }
                            if(isTarget){
                                if(!value.isEmpty()){
                                    fields.put(j, value);
                                    cell.setCellValue("");
                                }
                            }
                        }
                        //最长列
                        maxcol = Math.max(row.getLastCellNum(), maxcol);
                        if(isTarget){
                            break scan;
                        }
                    }
                }

                if (null == start) {
                    throw new IOException();
                }
                for (int i = 0; i < data.size(); i++) {
                    JSONObject d = data.get(i);
                    XSSFRow row = sheet.getRow(start.getX() + i);
                    if (null == row) {
                        row = sheet.createRow(start.getX() + i);
                    }
                    rowList = new ArrayList<Object>();
                    for (short j = (short) start.getY(); j <= maxcol; j++) {
                        XSSFCell cell = row.getCell(j);
                        if (null == cell) {
                            cell = row.createCell(j);
                        }
                        String key = fields.get(j);
                        if(null == key) continue;
                        String value = d.getString(key);
//                        cell.setCellValue(value);

                        rowList.add(value);
                    }
                    dataList.add(rowList);
                }
            }

            // 写入文件头部
            if(num==1){
                if(null == headList || !headList.isEmpty()){
                    headList.remove(headList.size()-1);
                }
                writeRow(headList, csvWtriter);
            }

            // 写入文件内容
            for (List<Object> row : dataList) {
                writeRow(row, csvWtriter);
            }

            csvWtriter.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            try {
//                csvWtriter.close();
//            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
    }

    /**
     * 写一行数据
     *
     * @param row       数据列表
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        for (Object data : row) {
            if(null == data || "null".equals(data)){
                data = "";
            }
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }
}