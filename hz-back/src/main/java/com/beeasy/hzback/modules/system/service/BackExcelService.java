package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BackExcelService {

    @Data
    @AllArgsConstructor
    private static class Point {
        int x;
        int y;
        String value;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private Point findEmptyPoint(int row, String[][] blocks) {
        for (int x = row; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++) {
                if (null == blocks[x][y]) {
                    return new Point(x, y);
                }
            }
        }
        throw new RuntimeException();
    }

    private void fillRect(String[][] blocks, Point p, int rowspan, int colspan, String str) {
        for (int i = p.getX(); i < p.getX() + rowspan; i++) {
            for (int j = p.getY(); j < p.getY() + colspan; j++) {
                blocks[i][j] = str;
            }
        }
    }

    public byte[] exportTableToExcel(JSONArray colss, JSONArray data) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        String[][] blocks = new String[100][100];
        String[] fields = new String[100];
        int count = 0;
        int maxcols = 0;
        for (Object o : colss) {
            JSONArray cols = (JSONArray) o;
            //找到一个空的格子
            for (Object oo : cols) {
                JSONObject col = (JSONObject) oo;
                Integer rowspan = (Integer) col.getOrDefault("rowspan", 1);
                Integer colspan = (Integer) col.getOrDefault("colspan", 1);
                String field = col.getString("field");
                Point p = findEmptyPoint(count, blocks);
                //合并单元格
                sheet.addMergedRegion(new CellRangeAddress(p.getX(), p.getX() + rowspan - 1, p.getY(), p.getY() + colspan - 1));
                //标记为已用格子
                fillRect(blocks, p, rowspan, colspan, col.getString("title"));
                //标记字段
                if (null != field && !field.isEmpty()) {
                    fields[p.getY()] = field;
                }

                maxcols = Math.max(maxcols, p.getY() + colspan);
            }
            count++;
        }

        //写入表头
        for (int i = 0; i < colss.size(); i++) {
            XSSFRow row = sheet.createRow(i);
            for (int j = 0; j < blocks[i].length; j++) {
                if (null != blocks[i][j]) {
                    row.createCell(j).setCellValue(blocks[i][j]);
                }
            }
        }

        //写入数据
        int rowPtr = colss.size();
        for (Object obj : data) {
            XSSFRow row = sheet.createRow(rowPtr++);
            JSONObject line = (JSONObject) obj;
            for (int i = 0; i < maxcols; i++) {
                if (null == fields[i]) {
                    continue;
                }
                String value = line.getString(fields[i]);
                if (null != value) {
                    row.createCell(i).setCellValue(value);
                }
            }
        }

        try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ) {
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public File exportTableToExcel(String fileName, JSONArray colss, JSONArray data) {
        byte[] bytes = exportTableToExcel(colss, data);
        File file = new File(fileName);
        try (
            FileOutputStream fos = new FileOutputStream(file);
        ) {
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public File exportTableByTemplate(String tmpl, String dest, JSONArray data){
        File file = new File(dest);
        try(
            FileOutputStream fos = new FileOutputStream(file);
            ){
            byte[] bytes = exportTableByTemplate(tmpl,data);
            fos.write(bytes);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return file;
    }

    public byte[] exportTableByTemplate(String tmpl, JSONArray data) {
        Point start = null;
        int maxcol = 0;
        boolean isTarget = false;
        Map<Short, String> fields = new HashMap<>();
        try (
            FileInputStream fis = new FileInputStream(tmpl);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ) {
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
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
                        String value = cell.getStringCellValue().trim();
                        if (value.startsWith("$")) {
                            value = value.substring(1);
                            start = new Point(i, j);
                            isTarget = true;
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
                JSONObject d = data.getJSONObject(i);
                XSSFRow row = sheet.getRow(start.getX() + i);
                if (null == row) {
                    row = sheet.createRow(start.getX() + i);
                }
                for (short j = (short) start.getY(); j <= maxcol; j++) {
                    XSSFCell cell = row.getCell(j);
                    if (null == cell) {
                        cell = row.createCell(j);
                    }
                    String key = fields.get(j);
                    if(null == key) continue;
                    String value = d.getString(key);
                    cell.setCellValue(value);
                }
            }
            workbook.write(bos);
            return bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }


    public byte[] exportExcelByTemplate(String tmpl, JSONObject data){
        Pattern p = Pattern.compile("\\$(.+?)\\$");

        ClassPathResource resource = new ClassPathResource(tmpl);
        try(
            InputStream is = resource.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ){
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for(short i = 0; i <= sheet.getLastRowNum(); i++){
                XSSFRow row = sheet.getRow(i);
                for(short j = 0; j <= row.getLastCellNum(); j++){
                    XSSFCell cell = row.getCell(j);
                    if(null == cell){
                        continue;
                    }
                    String value = cell.getStringCellValue().trim();
                    if(value.indexOf("$") >= -1){
                        Matcher m = p.matcher(value);
                        StringBuffer sb = new StringBuffer();
                        while(m.find()){
                            String key = m.group(1);
                            String v = data.getString(key);
                            if(null == v) v = "";
                            m.appendReplacement(sb, v);
                        }
                        m.appendTail(sb);
                        cell.setCellValue(sb.toString());
                    }
                }
            }
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static void main(String[] args) {
        String json = "[[{\"field\":\"MAIN_BR_NAME\",\"title\":\"机构\",\"rowspan\":2},{\"field\":\"\",\"title\":\"抵押贷款\",\"colspan\":2,\"align\":\"center\"},{\"field\":\"\",\"title\":\"保证贷款\",\"colspan\":2,\"align\":\"center\"},{\"field\":\"\",\"title\":\"质押贷款\",\"colspan\":2,\"align\":\"center\"},{\"field\":\"\",\"title\":\"信用贷款\",\"colspan\":2,\"align\":\"center\"},{\"field\":\"\",\"title\":\"农商行合计\",\"align\":\"center\"}],[{\"field\":\"LOAN_BALANCE_10\",\"title\":\"贷款余额\"},{\"field\":\"LOAN_BALANCE_10_BAD\",\"title\":\"不良余额\"},{\"field\":\"LOAN_BALANCE_30\",\"title\":\"贷款余额\"},{\"field\":\"LOAN_BALANCE_30_BAD\",\"title\":\"不良余额\"},{\"field\":\"LOAN_BALANCE_20\",\"title\":\"贷款余额\"},{\"field\":\"LOAN_BALANCE_20_BAD\",\"title\":\"不良余额\"},{\"field\":\"LOAN_BALANCE_00\",\"title\":\"贷款余额\"},{\"field\":\"LOAN_BALANCE_00_BAD\",\"title\":\"不良余额\"},{\"field\":\"LOAN_BALANCE\",\"title\":\"贷款余额\"}]]";

        String data = "[{\"MAIN_BR_NAME\":\"公司部\",\"LOAN_BALANCE_10\":68,\"LOAN_BALANCE_10_BAD\":68,\"LOAN_BALANCE_30\":68,\"LOAN_BALANCE_30_BAD\":68,\"LOAN_BALANCE_20\":68,\"LOAN_BALANCE_20_BAD\":68,\"LOAN_BALANCE_00\":68,\"LOAN_BALANCE_00_BAD\":68,\"LOAN_BALANCE\":68},{\"MAIN_BR_NAME\":\"中心支行\",\"LOAN_BALANCE_10\":0,\"LOAN_BALANCE_10_BAD\":0,\"LOAN_BALANCE_30\":0,\"LOAN_BALANCE_30_BAD\":0,\"LOAN_BALANCE_20\":0,\"LOAN_BALANCE_20_BAD\":0,\"LOAN_BALANCE_00\":0,\"LOAN_BALANCE_00_BAD\":0,\"LOAN_BALANCE\":0},{\"MAIN_BR_NAME\":\"惠城支行\",\"LOAN_BALANCE_10\":6717.79,\"LOAN_BALANCE_10_BAD\":716.47,\"LOAN_BALANCE_30\":6717.79,\"LOAN_BALANCE_30_BAD\":716.47,\"LOAN_BALANCE_20\":6717.79,\"LOAN_BALANCE_20_BAD\":716.47,\"LOAN_BALANCE_00\":6717.79,\"LOAN_BALANCE_00_BAD\":716.47,\"LOAN_BALANCE\":6777.79},{\"MAIN_BR_NAME\":\"小金口支行\",\"LOAN_BALANCE_10\":1078.11,\"LOAN_BALANCE_10_BAD\":510,\"LOAN_BALANCE_30\":1078.11,\"LOAN_BALANCE_30_BAD\":510,\"LOAN_BALANCE_20\":1078.11,\"LOAN_BALANCE_20_BAD\":510,\"LOAN_BALANCE_00\":1078.11,\"LOAN_BALANCE_00_BAD\":510,\"LOAN_BALANCE\":1235.74},{\"MAIN_BR_NAME\":\"水口支行\",\"LOAN_BALANCE_10\":3000,\"LOAN_BALANCE_10_BAD\":0,\"LOAN_BALANCE_30\":3000,\"LOAN_BALANCE_30_BAD\":0,\"LOAN_BALANCE_20\":3000,\"LOAN_BALANCE_20_BAD\":0,\"LOAN_BALANCE_00\":3000,\"LOAN_BALANCE_00_BAD\":0,\"LOAN_BALANCE\":3032.51},{\"MAIN_BR_NAME\":\"江南支行\",\"LOAN_BALANCE_10\":748.96,\"LOAN_BALANCE_10_BAD\":48.96,\"LOAN_BALANCE_30\":748.96,\"LOAN_BALANCE_30_BAD\":48.96,\"LOAN_BALANCE_20\":748.96,\"LOAN_BALANCE_20_BAD\":48.96,\"LOAN_BALANCE_00\":748.96,\"LOAN_BALANCE_00_BAD\":48.96,\"LOAN_BALANCE\":776.16},{\"MAIN_BR_NAME\":\"惠阳支行\",\"LOAN_BALANCE_10\":21685.03,\"LOAN_BALANCE_10_BAD\":4188.37,\"LOAN_BALANCE_30\":21685.03,\"LOAN_BALANCE_30_BAD\":4188.37,\"LOAN_BALANCE_20\":21685.03,\"LOAN_BALANCE_20_BAD\":4188.37,\"LOAN_BALANCE_00\":21685.03,\"LOAN_BALANCE_00_BAD\":4188.37,\"LOAN_BALANCE\":21800.24},{\"MAIN_BR_NAME\":\"新圩支行\",\"LOAN_BALANCE_10\":2308.99,\"LOAN_BALANCE_10_BAD\":0,\"LOAN_BALANCE_30\":2308.99,\"LOAN_BALANCE_30_BAD\":0,\"LOAN_BALANCE_20\":2308.99,\"LOAN_BALANCE_20_BAD\":0,\"LOAN_BALANCE_00\":2308.99,\"LOAN_BALANCE_00_BAD\":0,\"LOAN_BALANCE\":2320.98},{\"MAIN_BR_NAME\":\"秋长支行\",\"LOAN_BALANCE_10\":517.26,\"LOAN_BALANCE_10_BAD\":17.26,\"LOAN_BALANCE_30\":517.26,\"LOAN_BALANCE_30_BAD\":17.26,\"LOAN_BALANCE_20\":517.26,\"LOAN_BALANCE_20_BAD\":17.26,\"LOAN_BALANCE_00\":517.26,\"LOAN_BALANCE_00_BAD\":17.26,\"LOAN_BALANCE\":517.26},{\"MAIN_BR_NAME\":\"仲恺支行\",\"LOAN_BALANCE_10\":967.18,\"LOAN_BALANCE_10_BAD\":66.05,\"LOAN_BALANCE_30\":967.18,\"LOAN_BALANCE_30_BAD\":66.05,\"LOAN_BALANCE_20\":967.18,\"LOAN_BALANCE_20_BAD\":66.05,\"LOAN_BALANCE_00\":967.18,\"LOAN_BALANCE_00_BAD\":66.05,\"LOAN_BALANCE\":1015.84},{\"MAIN_BR_NAME\":\"大亚湾支行\",\"LOAN_BALANCE_10\":4521.96,\"LOAN_BALANCE_10_BAD\":12.34,\"LOAN_BALANCE_30\":4521.96,\"LOAN_BALANCE_30_BAD\":12.34,\"LOAN_BALANCE_20\":4521.96,\"LOAN_BALANCE_20_BAD\":12.34,\"LOAN_BALANCE_00\":4521.96,\"LOAN_BALANCE_00_BAD\":12.34,\"LOAN_BALANCE\":4536.96},{\"MAIN_BR_NAME\":\"惠城小微企业金融部\",\"LOAN_BALANCE_10\":80,\"LOAN_BALANCE_10_BAD\":80,\"LOAN_BALANCE_30\":80,\"LOAN_BALANCE_30_BAD\":80,\"LOAN_BALANCE_20\":80,\"LOAN_BALANCE_20_BAD\":80,\"LOAN_BALANCE_00\":80,\"LOAN_BALANCE_00_BAD\":80,\"LOAN_BALANCE\":240},{\"MAIN_BR_NAME\":\"仲恺小微企业金融部\",\"LOAN_BALANCE_10\":90,\"LOAN_BALANCE_10_BAD\":0,\"LOAN_BALANCE_30\":90,\"LOAN_BALANCE_30_BAD\":0,\"LOAN_BALANCE_20\":90,\"LOAN_BALANCE_20_BAD\":0,\"LOAN_BALANCE_00\":90,\"LOAN_BALANCE_00_BAD\":0,\"LOAN_BALANCE\":390},{\"MAIN_BR_NAME\":\"惠阳小微金融部\",\"LOAN_BALANCE_10\":0,\"LOAN_BALANCE_10_BAD\":0,\"LOAN_BALANCE_30\":0,\"LOAN_BALANCE_30_BAD\":0,\"LOAN_BALANCE_20\":0,\"LOAN_BALANCE_20_BAD\":0,\"LOAN_BALANCE_00\":0,\"LOAN_BALANCE_00_BAD\":0,\"LOAN_BALANCE\":0},{\"MAIN_BR_NAME\":\"合计\",\"LOAN_BALANCE_10\":37261.35,\"LOAN_BALANCE_10_BAD\":5695.11,\"LOAN_BALANCE_30\":37261.35,\"LOAN_BALANCE_30_BAD\":5695.11,\"LOAN_BALANCE_20\":37261.35,\"LOAN_BALANCE_20_BAD\":5695.11,\"LOAN_BALANCE_00\":37261.35,\"LOAN_BALANCE_00_BAD\":5695.11,\"LOAN_BALANCE\":38174.56}]";


        //通过表头配置导出
        new BackExcelService().exportTableToExcel("ttt.xlsx",
            JSON.parseArray(json)
//            , new JSONArray()
            , JSON.parseArray(data)
        );

        //通过模板渲染导出
        new BackExcelService().exportTableByTemplate("ttt.xlsx", "ttt2.xlsx", JSONArray.parseArray(data));
    }
}
