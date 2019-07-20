package com.beeasy.hzbpm.ctrl;

import com.beeasy.hzbpm.util.Result;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.beeasy.hzbpm.util.U.toDoc;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;

public class cat {

    public Result all(){
        MongoCollection<Document> collection = db.getCollection("cat");
        ArrayList<Document> ret = collection.find().into(new ArrayList<>());
        walk(ret);
        return Result.ok(ret);
    }

    public void walk(Collection<Document> docs){
        if (docs == null) {
            return;
        }
        for (Document doc : docs) {
//            doc.put("_id", doc.getObjectId("_id").toString());
            doc.put("text", doc.getString("name"));
            doc.remove("name");
            walk(doc.getList("children", Document.class));
        }
    }

    public Result add(String pid){
        MongoCollection<Document> collection = db.getCollection("cat");
        Document doc = Document.parse(o(
                "name", "新分类",
                "children", a()
        ).toString());
         if("top".equalsIgnoreCase(pid)){
             doc.put("_id", new ObjectId().toString());
             collection.insertOne(doc);
             doc.put("text", doc.getString("name"));
             return Result.ok(doc);
         } else {
             doc.put("_id", new ObjectId().toString());
             UpdateResult ret = collection.updateOne(
                     Filters.or(
                             Filters.eq("children._id", (pid)),
                             Filters.eq("_id", (pid))
                     ), toDoc(o(
                             "$push", o(
                                     "$children", doc
                             )
                     )));
             if(ret.getModifiedCount() > 0){
                 doc.put("text", doc.getString("name"));
                 return Result.ok(doc);
             }
         }
         return Result.error("失败");
    }


}
