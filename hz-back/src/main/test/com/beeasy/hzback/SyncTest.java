package com.beeasy.hzback;


import com.beeasy.hzback.modules.system.service.TaskSyncService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

//@RunWith(SpringRunner.class)
//@SpringBootTest
////由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
//@WebAppConfiguration
//public class SyncTest extends TmallApplicationTests {
//
//    @Autowired
//    TaskSyncService syncService;
//    @Test
//    public void Test() throws IOException {
//        int a = 1;
//        syncService.syncDataToDataPool("T_RELATED_PARTY_LIST");
//    }
//}
