package com.beeasy.hzback;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.MsgTmpl;
import com.beeasy.hzdata.service.CheckService;
import com.beeasy.hzreport.service.ReportService;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
//由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
@WebAppConfiguration
public class TestRule {
    @Autowired
    SQLManager sqlManager;
    @Autowired
    CheckService checkService;

    @Autowired
    ReportService reportService;

    @Before
    public void init() {
        System.out.println("开始测试-----------------");
    }

    @After
    public void after() {
        System.out.println("测试结束-----------------");
    }
    @Test
    public void rule21(){
        checkService.rule21(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        });
    }
    @Test
    public void testUid(){
        reportService.getUidAndOname();
    }


}

