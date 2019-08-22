package com.beeasy.hzbpm;

import com.beeasy.hzbpm.service.BpmService;
import com.github.llyb120.nami.core.Nami;
import org.junit.BeforeClass;
import org.junit.Test;

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
}
