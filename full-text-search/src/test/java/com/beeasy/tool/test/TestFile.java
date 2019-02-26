package com.beeasy.tool.test;

import com.beeasy.tool.FileUtil;
import org.junit.Test;

public class TestFile {

    @Test
    public void textRead(){
        String str = FileUtil.readFile("/Users/bin/Downloads/0731测试.docx");
        int d = 1;
    }
}
