package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Date;

import static com.beeasy.hzbpm.service.MongoService.db;

public class workflow {

    // 生成最终的流程文件
    public Result saveWorkFlow(Obj body){

        MongoCollection<Document> collection = db.getCollection("workflow");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(body);
        Document doc = new Document();
        doc.put("createTime", new Date());
        doc.put("data", BsonArray.parse(jsonArray.toString()));
        String workflowName = body.s("workflowName");
        doc.put("workflowName", workflowName);

        Bson filter = Filters.eq("workflowName",workflowName);
        FindIterable<Document> lists = collection.find(filter);
        MongoCursor<Document> mongoCursor = lists.iterator();

        if(mongoCursor.hasNext()){
            collection.updateOne(Filters.eq("workflowName", workflowName), new Document("$set", doc), new UpdateOptions().upsert(true));
        }else{
            collection.insertOne(doc);

        }

        return (Result.ok(doc));
    }


}
