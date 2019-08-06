package com.beeasy.hzbpm;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Map;

public class TestExcel {


    @Test
    public void test() throws IOException {
        File target = new File("/Users/bin/work/sql.txt");
        FileOutputStream fos = new FileOutputStream(target);
        ExcelReader reader = ExcelUtil.getReader("/Users/bin/work/3.xlsx");
        String value = String.valueOf(reader.readCellValue(3,1));
//        List<Map<String, Object>> data = reader.readAll();
//        for (Map<String, Object> entry : reader.readAll()) {
//
//        }

        fos.write("fuck".getBytes());

        fos.close();
    }
}
