package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzbpm.filter.Auth;
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

import javax.xml.crypto.Data;
import java.util.*;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;

public class workflow {

    /**
     * 生成最终的流程文件
      */
    public Result saveWorkFlow(Obj body){

        MongoCollection<Document> collection = db.getCollection("workflow");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(body);
        Document doc = new Document();
        doc.put("createTime", new Date());
        doc.put("data", BsonArray.parse(jsonArray.toString()));
        String workflowName = body.s("workflowName");

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

    /**
     * 验证发起权限
     * @param formId
     * @return
     */
    public Result verifica(String formId){

        Arr list = a();
        MongoCollection<Document> collection = db.getCollection("workflow");
        collection.find(new Document(){{
//            put("data.formId", new ObjectId(formId));
            put("data.workflowName", "名称");
        }}).iterator()
                .forEachRemaining(d -> {
                    String id = d.getObjectId("_id").toHexString();
                    d.put("_id", id);
                    List<Object> data = (List<Object>) d.get("data");
                    for(Object li : data){
                        Document a = (Document) li;
                        String start = (String) a.get("start");
                        Map<String, Object> nodeMap = (Map<String, Object>) a.get("node");
                        Object startNode = nodeMap.get(start);


                        System.out.println(startNode);
                        // TODO 判断当前登录人是否在发起节点权限设置中

                        list.add(((Document) startNode).get("taskId"));

                    }
                });


        return Result.ok(list);
    }

    private Object startNode(){
        Arr list = a();
        MongoCollection<Document> collection = db.getCollection("workflow");
        collection.find(new Document(){{
//            put("data.formId", new ObjectId(formId));
            put("data.workflowName", "名称");
        }}).iterator()
                .forEachRemaining(d -> {
                    String id = d.getObjectId("_id").toHexString();
                    d.put("_id", id);
                    List<Object> data = (List<Object>) d.get("data");
                    for(Object li : data){
                        Document a = (Document) li;
                        String start = (String) a.get("start");
                        Map<String, Object> nodeMap = (Map<String, Object>) a.get("node");
                        Object startNode = nodeMap.get(start);
                        list.add(startNode);

                    }
                });
        return list;
    }

    /**
     * 提交
     */
    public void submitIns(Obj bady){
        Long uid = Auth.getUid();
        String formId = bady.s("formId");

        Object startNode = startNode();


    }



    /***********************************/

}
