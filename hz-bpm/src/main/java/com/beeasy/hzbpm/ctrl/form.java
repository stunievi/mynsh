package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzbpm.util.Result;
import com.beeasy.hzbpm.util.U;
import com.github.llyb120.nami.json.Json;
import com.mongodb.BasicDBObject;
import com.mongodb.InsertOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.beetl.sql.core.engine.PageQuery;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;
import static com.github.llyb120.nami.server.Vars.$get;
import static com.github.llyb120.nami.server.Vars.$request;

public class form {

    public Result list(String pid){
        MongoCollection<Document> collection = db.getCollection("form");
        Collection list = collection.aggregate(
                a(
                        o("$match", o("pid", o("$eq", null == pid? null : new ObjectId(pid)))),
                        o("$sort", o("lastModify", -1)),
                        o("$project", o(
                                "_id", o("$toString", "$_id"),
                                "name",1,
//                                "form", 1,
                                "desc", 1,
                                "lastModify", 1
                        ))
                ).toBsonArray()
        ).into(new ArrayList());
        return Result.ok(list);
    }

    public Object one(String id){
        MongoCollection<Document> collection = db.getCollection("form");
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        doc.put("_id", doc.getObjectId("_id").toString());
        return Result.ok(doc);
    }


    public Object add(){
        MongoCollection<Document> collection = db.getCollection("form");
//        $request.put("createTime", new Date());
//        $request.put("lastModify", new Date());
        String id = $request.s("_id");
        $request.remove("_id");
        //名字
        if(StrUtil.isBlank($request.s("name"))){
            return Result.error("表单名不能为空");
        }
//        long same = 0;
//        if(StrUtil.isBlank(id)){
//            //不能有同名的
//            same = collection.countDocuments(new BasicDBObject("name", $request.s("name")));
//        } else {
//            //除我之外不能有同名的
//            same = collection.countDocuments(
//                    new BasicDBObject()
//                    .append("_id", new BasicDBObject("$ne", new ObjectId(id)))
//                    .append("name", $request.s("name"))
//            );
//        }
//        if(same > 0){
//            return Result.error("已经有同名的表单");
//        }
        Document doc = $request.toBsonDoc();
        doc.put("createTime", new Date());
        doc.put("lastModify", new Date());
        ObjectId mid;
        if(StrUtil.isBlank(id)){
            mid = new ObjectId();
            //add
//            doc = U.toDoc($request);
//            collection.insertOne(doc);
        } else {
            mid = new ObjectId(id);
//            doc = U.toDoc($request);
//            doc.put("createTime", new Date());
//            doc.put("lastModify", new Date());
//            collection.replaceOne(new BasicDBObject("_id", new ObjectId(id)), doc);
        }
        collection.updateOne(Filters.eq("_id", mid), new Document("$set", doc), new UpdateOptions().upsert(true));
        return (Result.ok(doc));
    }

    public Result delete(String _id){
        MongoCollection<Document> collection = db.getCollection("form");
        Document ret = collection.findOneAndDelete(new Document() {{
            put("_id", new ObjectId(_id));
        }});
        return Result.ok(ret != null);
    }


//    private void analyzeForm(String xml){
//       var doc =  XmlUtil.parseXml(xml);
//        doc.getChildNodes().getLength();
//    }
//
//    private void walk(org.w3c.dom.Document node){
//
//    }
}
