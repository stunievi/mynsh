//package com.beeasy.bpm;
//
//import com.github.llyb120.nami.core.Nami;
//import org.bson.Document;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import static com.beeasy.hzbpm.bean.MongoService.mongoClient;
//
//public class TestMongo {
//
//    @BeforeClass
//    public static void before(){
//        Nami.dev();
//    }
//
//
//    @Test
//    public void test(){
//        Document doc = new Document();
//        doc.put("a","b");
//        mongoClient.getDatabase("test").getCollection("a").insertOne(doc);
//      var  b =   mongoClient.getDatabase("test").getCollection("a").countDocuments();
//      var c = 1;
//    }
//
//    @Test
//    public void find(){
//        var ss = mongoClient.getDatabase("test").getCollection("bpmXML").find();
//        System.out.println(ss);
//    }
//}
