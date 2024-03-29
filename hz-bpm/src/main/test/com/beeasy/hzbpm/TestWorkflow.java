package com.beeasy.hzbpm;

import com.beeasy.hzbpm.bean.Notice;
import com.beeasy.hzbpm.service.BpmService;
import com.github.llyb120.nami.core.Nami;
import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Obj;
import org.beetl.sql.core.SQLReady;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.a;

public class TestWorkflow {

    @BeforeClass
    public static void before() {
        Nami.dev();
    }

    @Test
    public void test() {
        long uid = 522;

//        MongoCollection<Document> col = db.getCollection("workflow");
////        col.mapReduce("function(){return 1}", "function(){return 2}");
//        Document data = col.aggregate(
//                a(
//                        o("$match", o("_id", new ObjectId("5d3aab8fda77c245f138fd2d"))),
//                        o("$project", o("arrangementData", 1))
//                ).toBson()
//        ).first();
//        Document arrangementData = (Document) data.get("arrangementData");
//        BpmService service = BpmService.ofModel(arrangementData);
        BpmService service = BpmService.ofModel("5d3ab883da77c245f138fd30");
        boolean flag = service.canPub("1069519488812056576l");
        Assert.assertTrue(flag);
//        BpmModel dd = Json.cast(arrangementData, BpmModel.class);
//        int d = 2;
    }


    @Test
    public void test02(){
        Arr uid = a(1,2,3);
        List<Obj> list = sqlManager.execute(new SQLReady(String.format("select * from t_org_user where uid in (%s)",  uid.join(",","'")) ), Obj.class);
        int d = 2;
//        BpmService service =  BpmService.ofModel("5d3ab883da77c245f138fd30");

    }



    @Test
    public void test03_createInstance() {
        Obj obj = new Obj();
        obj.put("data","测试");
        obj.put("请假类型","1");
        obj.put("请假原因","2");
        obj.put("天数","3");
        BpmService service = BpmService.ofModel("5d46529788eb634bcf82a36f");
//        Document bpmInstance = service.createBpmInstance(520+"",  obj);
//        Assert.assertNotNull(bpmInstance._id);
    }


    @Test
    public void test04_() throws InterruptedException {
        Notice.sendSystem("522", "你今天吃饭了吗","");
        Thread.sleep(10000);
    }

}
