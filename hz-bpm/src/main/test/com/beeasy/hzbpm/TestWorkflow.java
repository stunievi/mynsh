package com.beeasy.hzbpm;

import com.beeasy.hzbpm.entity.BpmEntity;
import com.beeasy.hzbpm.service.BpmService;
import com.github.llyb120.nami.core.Nami;
import com.github.llyb120.nami.json.Json;
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
    public void test() {
        long uid = 522;


        MongoCollection<Document> col = db.getCollection("workflow");
//        col.mapReduce("function(){return 1}", "function(){return 2}");
        Document data = col.aggregate(
                a(
                        o("$match", o("_id", new ObjectId("5d3aab8fda77c245f138fd2d"))),
                        o("$project", o("arrangementData", 1))
                ).toBson()
        ).first();
        Document arrangementData = (Document) data.get("arrangementData");
        BpmService service = new BpmService(arrangementData);
        service.canPub(1069519488812056576l);
//        BpmEntity dd = Json.cast(arrangementData, BpmEntity.class);
        int d = 2;
    }
}
