package com.beeasy.hzbpm.ctrl;



import cn.hutool.core.util.StrUtil;
import com.beeasy.hzbpm.bean.Log;
import com.beeasy.hzbpm.filter.Auth;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.core.R;
import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.Struct;
import java.util.*;

import static com.beeasy.hzbpm.bean.MongoService.db;
import static com.github.llyb120.nami.json.Json.*;
import static com.github.llyb120.nami.server.Vars.$request;

public class form {

    public Object listex(){
        MongoCollection<Document> collection = db.getCollection("cat");
        Arr cats = collection.aggregate(a(
                o("$match", o("type", "0")),
                o(
                        "$lookup", o(
                                "from", "form",
                                "localField", "_id",
                                "foreignField", "pid",
                                "as", "children"
                        )
                ),
                o(
                        "$project", o(
                                "id", o(
                                        "$toString", "$_id"
                                ),
                                "text", "$name",
                                "pid", o(
                                        "$toString", "$pid"
                                ),
                                "type", "cat",
                                "children",o(
                                        "$map",o(
                                               "input", "$children" ,
                                                "as", "item",
                                                "in", o(
                                                        "id", o("$toString", "$$item._id"),
                                                        "text", "$$item.name",
                                                        "type", "form"
                                                )
                                        )
                                )
                        )
                )
        ).toBson()).into(a());
        Json tree = tree((List)cats, "pid", "id");
        return Result.ok(tree);
    }

    public Object one(String id){
        MongoCollection<Document> collection = db.getCollection("form");
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        doc.put("_id", doc.getObjectId("_id").toString());
        return Result.ok(doc);
    }


    public Object add(){
        MongoCollection<Document> collection = db.getCollection("form");
        String id = $request.s("id");
        String pid = $request.s("pid");
        $request.remove("id");
        $request.remove("pid");

        //名字
        if(StrUtil.isBlank($request.s("name"))){
            return Result.error("表单名不能为空");
        }
        Document doc = $request.toBson();
        doc.put("createTime", new Date());
        doc.put("lastModify", new Date());
        ObjectId mid;
        if(StrUtil.isBlank(id)){
            mid = new ObjectId();
            //只在新增的时候放入pid
            if(StrUtil.isNotBlank(pid)){
                doc.put("pid", new ObjectId(pid));
            } else {
                doc.put("pid", null);
            }
        } else {
            mid = new ObjectId(id);
        }
        BsonValue nid = collection.updateOne(Filters.eq("_id", mid), new Document("$set", doc), new UpdateOptions().upsert(true)).getUpsertedId();
        //log
        if(StrUtil.isBlank(id)){
            Log.log(Auth.getUid() + "", "创建表单 %s", doc.getString("name"));
        } else {
            Log.log(Auth.getUid() + "", "编辑表单 %s", doc.getString("name"));
        }
        return (Result.ok(o(
                "id", nid == null ? id : nid.asObjectId().getValue().toString(),
                "text", doc.get("name"),
                "pid", pid,
                "type", "form"
        )));
    }

    public Object move(String id, String pid){
        MongoCollection<Document> collection = db.getCollection("form");
        UpdateResult ret = collection.updateOne(Filters.eq("_id", new ObjectId(id)), o("$set", o("pid", new ObjectId(pid))).toBson());
        return Result.ok(ret.getModifiedCount() > 0);
    }

    public Object copy(String id){
        MongoCollection<Document> collection = db.getCollection("form");
        Document item = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        if (item == null) {
            return Result.error();
        }
        item.remove("_id");
        item.put("name", item.getString("name") + "副本");
        collection.insertOne(item);
        return Result.ok(o(
                "id", item.getObjectId("_id").toString() ,
                "text", item.get("name"),
                "pid", item.getObjectId("pid").toString(),
                "type", "form"
        ));
    }

    public Result delete(String id){
        MongoCollection<Document> collection = db.getCollection("form");
        Document ret = collection.findOneAndDelete(new Document() {{
            put("_id", new ObjectId(id));
        }});
        Log.log(Auth.getUid() + "", "删除表单 %s", ret.getString("name"));
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

    public R queryForm(){
        Arr li = a();
        db.getCollection("form").find().projection(new Document("name",1).append("_id", 1).append("form.data",1)).into(li);
        li.forEach(o -> ((Document)o).put("_id", ((Document) o).get("_id").toString()));

        return R.ok(li);
    }

    public R queryFormData(String _id){
        Arr li = a();
        db.getCollection("form").find(new BasicDBObject("_id", new ObjectId(_id))).into(li);
        li.forEach(o -> ((Document)o).put("_id", ((Document) o).get("_id").toString()));

        return R.ok(li);
    }
}
