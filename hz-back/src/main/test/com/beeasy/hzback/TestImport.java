package com.beeasy.hzback;

import com.beeasy.hzback.modules.system.controller.BackExcelController;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestImport {

    @Test
    public void test() throws IOException {
        new BackExcelController().importLM(new MockMultipartFile("rilegou", new FileInputStream("C:\\Users\\bin\\Desktop\\按揭类贷款新增字段.xlsx")));
    }

    @Test
    public void test2() throws IOException {
        new BackExcelController().importDefination(new MockMultipartFile("fuck", new FileInputStream("C:/Users/bin/Desktop/sls_acct_define.xlsx")));
    }
}
