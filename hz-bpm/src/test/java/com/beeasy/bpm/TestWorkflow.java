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

import static com.beeasy.hzbpm.service.MongoService.db;
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

        BpmService service = BpmService.ofIns("5d3ab883da77c245f138fd30",obj);
        int d = 2;
    }
}
