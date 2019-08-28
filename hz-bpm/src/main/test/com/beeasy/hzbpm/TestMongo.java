package com.beeasy.hzbpm;

import com.beeasy.hzbpm.service.BpmService;
import com.github.llyb120.nami.core.Nami;
import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.beeasy.hzbpm.bean.MongoService.db;
import static com.github.llyb120.nami.func.Function.func;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;

public class TestMongo {

    @BeforeClass
    public static void before(){
        Nami.dev();
    }


    @Test
    public void test(){
        BpmService.getIncrementId();
//        var doc = new Document();
//        doc.put("a","b");
//        db.getCollection("fuck").inserton
//        db.getCollection("fuck");
//      var  b =  db.getCollection("a").countDocuments();
//      var c = 1;
    }


    @Test
    public void fixDealers(){
        Arr wfs = db.getCollection("workflow").find().into(a());
        for (Object wf : wfs) {
            Document doc = Obj.get((Map)wf, "bpmData.nodes");
            Document bpmData = Obj.get((Map) wf, "bpmData");
            bpmData.put("listFields", doc.get("arrangementData.listFields"));
            for (Map.Entry<String, Object> entry : doc.entrySet()) {
                Document permission = Obj.get((Map) entry.getValue(), "permission");
                Obj obj = o();
                func(() -> {
                    List<String> rids = (List<String>) permission.get("rids");
                    List<String> rs = (List<String>) permission.get("rs");
                    Document users = new Document();
                    for (int i = 0; i < rids.size(); i++) {
                        users.put(rids.get(i), rs.get(i));
                    }
                    obj.put("roles", users);
                });
                func(() -> {
                    List<String> rids = (List<String>) permission.get("dids");
                    List<String> rs = (List<String>) permission.get("ds");
                    Document users = new Document();
                    for (int i = 0; i < rids.size(); i++) {
                        users.put(rids.get(i), rs.get(i));
                    }
                    obj.put("deps", users);
                });
                func(() -> {
                    List<String> rids = (List<String>) permission.get("pids");
                    List<String> rs = (List<String>) permission.get("ps");
                    Document users = new Document();
                    for (int i = 0; i < rids.size(); i++) {
                        users.put(rids.get(i), rs.get(i));
                    }
                    obj.put("users", users);
                });
                ((Map) entry.getValue()).put("dealers", obj);
                ((Map) entry.getValue()).put("smart", ((Map) entry.getValue()).get("intelligence"));
            }
            ObjectId id = ((Document) wf).getObjectId("_id");
            db.getCollection("workflow").replaceOne(Filters.eq("_id", id), (Document)wf);
        }
    }
}
