package com.beeasy.bpm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzbpm.service.BpmService;
import com.github.llyb120.nami.core.Nami;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;

public class TestWorkflow {

    @BeforeClass
    public static void before() {
        Nami.dev();
    }

    @Test
    public void test1() {

        Obj obj = new Obj();
        obj.put("data","测试");
        obj.put("请假类型","1");
        obj.put("请假原因","2");
        obj.put("天数","3");

        String uid = "520";

//        BpmService service = BpmService.ofIns("5d3e96abbd581a0c9cb8d4d8",obj,uid);
//        BpmService service1 = BpmService.getXML("5d3c1f16b1e015024916dbd8");
        int d = 2;
    }
}
