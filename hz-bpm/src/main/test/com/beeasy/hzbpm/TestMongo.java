package com.beeasy.hzbpm;

import com.github.llyb120.nami.core.Nami;
import org.bson.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.beeasy.hzbpm.service.MongoService.mongoClient;

public class TestMongo {

    @BeforeClass
    public static void before(){
        Nami.test();
    }


    @Test
    public void test(){
        var doc = new Document();
        doc.put("a","b");
//        db.getCollection("fuck").inserton
//        db.getCollection("fuck");
//      var  b =  db.getCollection("a").countDocuments();
      var c = 1;
    }
}