package com.beeasy.hzreport.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.hzback.entity.SysNotice;
import com.beeasy.hzback.entity.SystemFile;
import com.beeasy.hzback.modules.system.controller.FileController;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.hzreport.service.ExcelService;
import com.beeasy.hzreport.service.ReportService;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@Controller
@RequestMapping("/api/report/csv")
public class ExportCSVController {

    @Autowired
    ExcelService excelService;
    @Autowired
    ReportService reportService;
    @Autowired
    SQLManager sqlManager;

    @Autowired
    FileController fileController;
    @Autowired
    NoticeService2 noticeService2;

    public String test(File file, long uid){
        //要上传的文件
//        File file = null;
        try(
                FileInputStream fis = new FileInputStream(file);
        ) {
            String re = fileController.upload(new MockMultipartFile("信贷中间表zzz.csv", "信贷中间表zzz.csv", MediaType.APPLICATION_OCTET_STREAM_VALUE, fis), "信贷中间表zzz.csv", null, SystemFile.Type.MESSAGE, null, S.uuid(), uid);
            JSONObject jsonObject = JSONObject.parseObject(re);
            JSONObject data = jsonObject.getJSONObject("data");
            return data.getString("id");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

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
    public Result export(
            @RequestParam Map<String,Object> params
            , @PageableDefault(value = 5000, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        long uid = AuthFilter.getUid();
//        new Thread(() -> {
//            exportCSV(params,pageable,uid);
//        }).start();
        ThreadUtil.execAsync(() -> exportCSV(params,pageable,uid));
        return Result.ok();
    }

    public void exportCSV(@RequestParam Map<String,Object> params
            , Pageable pageable,long uid
    ){
        String no = "rpt";
        File file = createFile();
        params.put("uid", uid);
        Object result;
        List<List<JSONObject>> list = C.newList();

            try(
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
            ){
                int i = 0;
                while (++i > 0){
                    result= query(i,no, params, pageable);

                    if(((PageQuery) result).getList().isEmpty()){
                        break;
                    }else{
                        list.clear();
                        list.add(((PageQuery) result).getList());
                        createCSV(file, i,no + ".xlsx", list, raf);
                    }
                }
                Log.log("贷款台账导出csv文件");

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                File compressed = ZipUtil.zip(file);
                String fid = test(compressed, uid);
                List<JSONObject> objs = sqlManager.execute(new SQLReady("SELECT src_sys_date FROM RPT_M_RPT_SLS_ACCT FETCH FIRST 1 ROWS ONLY"),JSONObject.class);
                String sysDate = "";
                try{
                    sysDate = objs.get(0).getString("srcSysDate");
                }catch (Exception e){
                    e.printStackTrace();
                }
                noticeService2.addNotice(SysNotice.Type.SYSTEM, uid, String.format("贷款台账  "+sysDate+"导出成功！<a href=\"/api/file/download?fid="+fid+"&name=贷款台账.zip\" target=\"_Blank\" class=\"forExportCsv_z\">点击下载</a>"), null);

                compressed.delete();
                file.delete();
                //file.delete();
            }

            /*if(result instanceof List){
                List<List<JSONObject>> list = C.newList();
                list.add((List<JSONObject>) result);
//                bytes = createCSV(no + ".xlsx", list,rsponse);
            }*/
    }

    private PageQuery query(int i, String no, Map params, Pageable pageable){
        PageQuery pageQuery = new PageQuery<>(pageable.getPageNumber(),pageable.getPageSize());

        pageQuery.setParas(params);
        pageQuery.setPageNumber(i);
        pageQuery.setPageSize(1000);
        return sqlManager.pageQuery("report." + no+"_export", JSONObject.class, pageQuery);
    }

    private File createFile(){
        File file = null;
        try {
            file = Files.createTempFile("a", ".csv").toFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        return file;
    }
    /**
     * 写入数据
     * @param num
     * @param tmpl
     * @param datas
     * @return
     */
    private void createCSV(File file, int num, String tmpl, List<List<JSONObject>> datas, RandomAccessFile raf) throws IOException {

        raf.seek(raf.length());



        ClassPathResource resource = new ClassPathResource("excel/" + tmpl);

        // 表格头
        List<Object> headList = new ArrayList<>();

        //数据
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        List<Object> rowList = null;

//        BufferedWriter csvWtriter = null;

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

//            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GB2312"), 1024);

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
                writeRow(headList, raf);
            }

            // 写入文件内容
            for (List<Object> row : dataList) {
                writeRow(row, raf);
            }


            //csvWtriter.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写一行数据
     *
     * @param row       数据列表
     * @param
     * @throws IOException
     */
    private static void writeRow(List<Object> row, RandomAccessFile raf) throws IOException {
        for (Object data : row) {
            if(null == data || "null".equals(data)){
                data = "";
            }
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            raf.write(rowStr.replaceAll("\r|\n", "").getBytes(CharsetUtil.GBK));
        }
        raf.writeChar('\n');
    }
}