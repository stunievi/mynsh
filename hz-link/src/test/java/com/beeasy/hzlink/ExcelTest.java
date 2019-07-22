package com.beeasy.hzlink;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static com.github.llyb120.nami.core.Json.a;
import static com.github.llyb120.nami.core.Json.o;

public class ExcelTest {

    @Test
    public void test() throws IOException {
        Context context = new Context();
        context.putVar("values", a(
            o("name", "fuck")
        ));
        var bs = IoUtil.read(new FileInputStream("d:/template.xls"));
        var sw = new StringWriter();
        var baos = new ByteArrayOutputStream();
        JxlsHelper.getInstance().processTemplate(new FileInputStream("d:/template.xls"), new FileOutputStream("d:/export.xls"), context);
//        XLSTransformer transformer = new XLSTransformer();
//        var bean = o();
//        bean.put("values",
//        transformer.transformXLS("C:\\Users\\bin\\Documents\\WeChat Files\\llyb120\\FileStorage\\File\\2019-06\\软需附件(1)\\软需附件\\附件7-股东关联明细导出模板.xls", bean, ("d:/export.xls"));
    }

    @Test
    public void test2() throws FileNotFoundException {
        JSONArray departments = JSONObject.parseArray("[{\"name\":\"d\",\"chief\":{\"name\":\"a\"},\"headcount\":\"s\",\"link\":\"www\",\"staff\":[{\"name\":\"a\"}]},{\"name\":\"dd\",\"chief\":{\"name\":\"a\"},\"headcount\":\"s\",\"link\":\"www\",\"staff\":[{\"name\":\"a\"}]}]");
        try (FileInputStream is = new FileInputStream("D:/java projects/hznsh/hz-link/src/main/resources/excel/t.xlsx")) {
                OutputStream os = new FileOutputStream(new File("D:/java projects/hznsh/hz-link/src/main/resources/excel/test.xlsx"));
                Context context = PoiTransformer.createInitialContext();
                context.putVar("departments", departments);
                context.putVar("sheetNames", Arrays.asList(
                        departments.getJSONObject(0).get("name"),
                        departments.getJSONObject(1).get("name")
                    )
                );
                JxlsHelper.getInstance().setUseFastFormulaProcessor(false).setDeleteTemplateSheet(true).processTemplate(is, os, context);

            FileInputStream fis = new FileInputStream("D:/java projects/hznsh/hz-link/src/main/resources/excel/test.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(fis);

            //删除Sheet
            wb.removeSheetAt(wb.getSheetIndex("template"));

            FileOutputStream fileOut = new FileOutputStream("D:/java projects/hznsh/hz-link/src/main/resources/excel/test.xlsx");
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();

            fis.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
