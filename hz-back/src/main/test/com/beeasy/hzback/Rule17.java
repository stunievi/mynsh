package com.beeasy.hzback;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.TmallApplicationTests;
import com.beeasy.hzback.entity.SysNotice;
import com.beeasy.hzdata.service.CheckService;
import org.beetl.sql.core.SQLManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Executable;
import java.util.List;


public class Rule17 extends TmallApplicationTests {

    @Autowired
    SQLManager sqlManager;
    @Autowired
    private CheckService checkService;


    @Before
    public void init(){
        checkService.clearTriggerLogs();
    }

    @After
    public void finish(){

    }

    @Test
    public void rule17(){
        List<JSONObject> res = sqlManager.select("task.规则17", JSONObject.class, C.newMap());
        Assert.assertTrue(res.size() > 0);
        try{
            long before = sqlManager.lambdaQuery(SysNotice.class)
                    .count();
            checkService.rule17(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
            });
            long after = sqlManager.lambdaQuery(SysNotice.class)
                    .count();
            Assert.assertTrue(after - before == res.size());
        }catch (Exception e){
            Assert.assertNull(e);
        }


    }
    @Test
    public void rule18(){
        checkService.rule18(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        });
    }

//    public void rule17(OutputStream os){
//        List<JSONObject> res = sqlManager.select("task.规则17", JSONObject.class, C.newMap());
//        println( "找到待处理条数%d", res.size());
//        JSONObject sent = new JSONObject();
//        List<SysNotice> notices = C.newList();
//        List<NoticeTriggerLog> logs = C.newList();
//        try {
//            for(JSONObject item : res){
//                Long uid = item.getLong("UID");
//                if(!sent.containsKey(uid)){
//                    sent.put("uid", 1);
//                    notices.add(
//                            noticeService2.makeNotice(SysNotice.Type.WORKFLOW, uid, String.format("你的对私账户即将过期",item.get("TITLE"), item.get("ID") + "", (int)item.get("EXPR_DAYS")),C.newMap("taskId", item.get("ID") + ""))
//                    );
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        sqlManager.insertBatch(SysNotice.class, notices);
//        sqlManager.insertBatch(NoticeTriggerLog.class, logs);
//    }

}
