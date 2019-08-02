package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.StrUtil;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.json.Json;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;

import static com.beeasy.hzbpm.bean.MongoService.db;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;

public class cat {

    public Result all(String type) {
        MongoCollection<Document> collection = db.getCollection("cat");
        Collection ret = collection.aggregate(a(
                o("$match",o("type",type)),
                o(
                        "$project", o(
                                "_id", o(
                                        "$toString", "$_id"
                                ),
                                "text", "$name",
                                "pid", o(
                                        "$toString", "$pid"
                                )
                        )
                )
        ).toBson()).into(new ArrayList());
        return Result.ok(Json.tree(ret, "pid", "_id"));
    }


    public Result add(String pid, String type) {
        MongoCollection<Document> collection = db.getCollection("cat");
        Document doc = o(
                "name", "新分类",
                "type", type,
                "pid", null
        ).toBson();
        if ("top".equalsIgnoreCase(pid)) {
        } else {
            doc.put("pid", new ObjectId(pid));
        }
        collection.insertOne(doc);
        doc.put("text", doc.getString("name"));
        doc.put("_id", doc.getObjectId("_id").toString());
        if (!"top".equalsIgnoreCase(pid)) {
            doc.put("pid", doc.getObjectId("pid").toString());
        }
        return Result.ok(doc);
    }


    public Result delete(String id) {
        MongoCollection<Document> col = db.getCollection("cat");
        //检查个数
        Bson where = Filters.eq("pid", new ObjectId(id));
        long count = col.countDocuments(where);
        if (count > 0) {
            return Result.error("不能删除一个非空菜单！");
        }
        col.deleteOne(Filters.eq("_id", new ObjectId(id)));
        return Result.ok();
    }

    public Result rename(String id, String name) {
        if (StrUtil.isBlank(name)) {
            return Result.error("分类名不能为空");
        }
        MongoCollection<Document> col = db.getCollection("cat");
        col.findOneAndUpdate(Filters.eq("_id", new ObjectId(id)), Updates.set("name", name));
        return Result.ok();
    }


}
