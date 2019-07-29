package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.core.R;
import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;
import static com.github.llyb120.nami.server.Vars.$request;

public class BpmXmlController {

    public Result saveBPMXML(String xml, String name, String _id, Obj query, Obj bady){
        /*if(null != name || "" != name){
            Bson filter = Filters.eq("b_name",name);
            mongoClient.getDatabase("hz-bpm").getCollection("workflow").deleteOne(filter);

        }*/
        MongoCollection<Document> collection = db.getCollection("workflow");
        if(StrUtil.isBlank($request.s("modelName"))){
            return Result.error("工作流名称不能为空!");
        }

        $request.put("createTime", new Date());
        $request.put("b_id", next());
//        JSONObject arrangementData = (JSONObject) $request.get("arrangementData");
//        String formId= arrangementData.getString("formId");
//        arrangementData.put("formId",new ObjectId(formId));
//        $request.put("arrangementData",arrangementData);

        String id = $request.s("_id");
        String pid = $request.s("pid");
        $request.remove("_id");
        $request.remove("_");
        $request.remove("pid");
//        $request.put("data", JSON.parse($request.get("data").toString()));

        long same = 0;
        if(StrUtil.isBlank(id)){
            //不能有同名的
            same = collection.countDocuments(new BasicDBObject("modelName", $request.s("modelName")));
        } else {
            same = collection.countDocuments(
                    new BasicDBObject()
                            .append("_id", new BasicDBObject("$ne", new ObjectId(id)))
                            .append("modelName", $request.s("modelName"))
            );
        }
        if(same > 0){
            return Result.error("已经有同名的工作流名称!");
        }
        JSONObject jsonObject = (JSONObject) JSON.parse($request.get("data").toString());
        JSONArray jsonArray = new JSONArray();
//        if(jsonObject.getJSONArray("startEvent").size()>0){
//            JSONArray startEvent = jsonObject.getJSONArray("startEvent");
//            JSONObject start = saveStartEvent((JSONObject) startEvent.get(0));
//            jsonArray.add(start);
//
//        }
        Set<Object> task = new HashSet<>();
        JSONArray jr = jsonObject.getJSONArray("taskList");
        for (Object obj : jr) {
            JSONObject jo = (JSONObject) obj;
            task.add(jo.get("taskId"));
        }

        if(jsonObject.getJSONObject("ext").size()>0){
            for(Object t : task){
                String [] startFlag = t.toString().split("_");

                JSONObject ext = (JSONObject) jsonObject.getJSONObject("ext").get(t);
                JSONObject object = saveTask(ext, startFlag[0]);
                jsonArray.add(object);
            }

        }
        /*if(jsonObject.getJSONArray("ext").size()>0){
            JSONArray ext = jsonObject.getJSONArray("ext");
            for (Object obj : ext) {
                JSONObject jo = (JSONObject) obj;
                JSONObject object = saveTask(jo);
                jsonArray.add(object);
            }
        }*/
        System.out.println("json:"+jsonArray);


        /*doc.put("b_xml",xml);
        doc.put("b_name",next());
        doc.put("createTime",new Date());
        doc.put("data",query.get("data"));*/

//        mongoClient.getDatabase("hz-bpm").getCollection("bpmXML").insertOne(doc);

        /*Document doc;
        if(StrUtil.isBlank(id)){
            $request.put("data", BsonArray.parse(jsonArray.toString()));
            doc = new Document($request);
            collection.insertOne(doc);
        } else {
            doc = new Document($request);
            doc.put("data", BsonArray.parse(jsonArray.toString()));
            collection.replaceOne(new BasicDBObject("_id", new ObjectId(id)), doc);
        }*/
        Document doc = $request.toBson();
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
        collection.updateOne(Filters.eq("_id", mid), new Document("$set", doc), new UpdateOptions().upsert(true));

        return Result.ok();
    }

    /**
     * 查询
     * @return
     */
    public Result queryBPMXML(String _id, String pid){

        MongoCollection<Document> collection = db.getCollection("workflow");
        Collection list = collection.aggregate(
                a(
                        o("$match", o("pid", StrUtil.isBlank(pid)? null : new ObjectId(pid))),
                        o("$sort", o("createTime", -1)),
                        o("$project", o(
                                "_id", o("$toString", "$_id"),
                                "modelName",1,
//                                "data", 1,
                                "createTime", 1
                        ))
                ).toBson()
        ).into(new ArrayList());
        return Result.ok(list);


        /*Arr list = a();
        if(null == _id){
            collection.find().iterator()
                    .forEachRemaining(d -> {
                        String id = d.getObjectId("_id").toHexString();
                        d.put("_id", id);
                        list.add(d);
                    });
        }else{
            collection.find(new Document(){{
                put("_id", new ObjectId(_id));
            }}).iterator()
                    .forEachRemaining(d -> {
                        String id = d.getObjectId("_id").toHexString();
                        d.put("_id", id);
                        list.add(d);
                    });
        }*/

        /*FindIterable<Document> lists;
        if(null == _id){
            lists = mongoClient.getDatabase("hz-bpm").getCollection("workflow").find();
        }else{
            Bson filter = Filters.eq("b_name",bName);
            lists = mongoClient.getDatabase("hz-bpm").getCollection("workflow").find(filter);
        }

        MongoCursor<Document> mongoCursor = lists.iterator();
        int i=0;
        JSONArray jsonArray = new JSONArray();
        while(mongoCursor.hasNext()){
            i++;
            var obj = mongoCursor.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", obj.get("_id").toString());
            jsonObject.put("name", obj.get("b_name"));
            jsonObject.put("workflow", obj.get("b_xml"));
            jsonArray.add(jsonObject);
        }

        System.out.println("i="+i);*/
//        return R.ok(list);
    }

    public R one(String _id){
        MongoCollection<Document> collection = db.getCollection("workflow");
        Arr list = a();
        if(null == _id){
            collection.find().iterator()
                    .forEachRemaining(d -> {
                        String id = d.getObjectId("_id").toHexString();
                        d.put("_id", id);
                        list.add(d);
                    });
        }else{
            collection.find(new Document(){{
                put("_id", new ObjectId(_id));
            }}).iterator()
                    .forEachRemaining(d -> {
                        String id = d.getObjectId("_id").toHexString();
                        d.put("_id", id);
                        list.add(d);
                    });
        }
        return R.ok(list);
    }

    /**
     * 删除
     * @param _id 流程id
     * @return
     */
    public R deleteBPMXML(String _id){
        /*Bson filter = Filters.eq("b_name",bName);
        mongoClient.getDatabase("hz-bpm").getCollection("workflow").deleteOne(filter);
        return R.ok();*/

        MongoCollection<Document> collection = db.getCollection("workflow");
        Document ret = collection.findOneAndDelete(new Document() {{
            put("_id", new ObjectId(_id));
        }});
        return R.ok(ret != null);
    }

    public static synchronized String next() {
        UUID uuid = UUID.randomUUID();
        String str = "sid" + uuid.toString();
        // 去掉"-"符号
        //	        String temp = str.substring(0, 8) + str.substring(9, 13)
        //	                + str.substring(14, 18) + str.substring(19, 23)
        //	                + str.substring(24);
        return str;
    }

    /**
     * 保存节点信息
     */
    public JSONObject saveTask(JSONObject query, String isStart){
//        JSONArray jsonArray = new JSONArray();
//        JSONObject task = new JSONObject();
//        task.put("taskId",query.get("taskId"));
//        jsonArray.add(task);
//
//        JSONObject object = new JSONObject();
//
//        var quarters = query.get("quarters").toString().split(",");
//        var depts = query.get("dept").toString().split(",");
//        var roles = query.get("role").toString().split(",");
//        var uid = query.get("uid").toString().split(",");
//        var dataId = query.get("dataId").toString().split(",");
//        var dataTitle = query.get("dataTitle").toString().split(",");
//        var dataType = query.get("dataType").toString().split(",");
//
//        // 审批节点
//        JSONObject permission = new JSONObject();
//        JSONArray quarter = new JSONArray();
//        quarter.add(quarters);
//        permission.put("quarters",quarter);
//        JSONArray dept = new JSONArray();
//        dept.add(depts);
//        permission.put("dept",dept);
//        JSONArray role = new JSONArray();
//        role.add(roles);
//        permission.put("role",role);
//        JSONArray user = new JSONArray();
//        user.add(uid);
//        permission.put("user",user);
//        object.put("permission",permission);
//
//        // 表单
//        JSONObject form = new JSONObject();
//        form.put("formId",query.get("formId"));
//        JSONArray item = new JSONArray();
//        form.put("items",item);
//        object.put("form",form);
//
//        /*for(int i = 0;i< quarters.length;i++){
//            JSONArray obj = new JSONArray();
//            obj.add(quarters[i]);
//            quarter.add(obj);
//        }*/
//
//        for(int i = 0;i< dataId.length;i++){
//            JSONObject nodeJson = new JSONObject();
//            nodeJson.put("name",dataId[i]);
//            nodeJson.put("title",dataTitle[i]);
//            if(dataType.length>0){
//
//                nodeJson.put("type",dataType[i]);
//            }
//            item.add(nodeJson);
//        }
//
//        JSONObject node = new JSONObject();
//        JSONArray nodeArr = new JSONArray();
//        nodeArr.add(object);
//        node.put("node",nodeArr);
//        jsonArray.add(node);
//        System.out.println(jsonArray);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskId",query.get("taskId"));

        JSONObject object = new JSONObject();

//        var quarters = query.get("quarters").toString().split(",");
//        var depts = query.get("dept").toString().split(",");
//        var roles = query.get("role").toString().split(",");
//        var uid = query.get("uid").toString().split(",");
//        var dataId = query.get("dataId").toString().split(",");
//        var dataTitle = query.get("dataTitle").toString().split(",");
//        var dataType = query.get("dataType").toString().split(",");
//
//        // 审批节点
//        JSONObject permission = new JSONObject();
//        JSONArray quarter = new JSONArray();
//        quarter.add(quarters);
//        permission.put("quarters",quarter);
//        JSONArray dept = new JSONArray();
//        dept.add(depts);
//        permission.put("dept",dept);
//        JSONArray role = new JSONArray();
//        role.add(roles);
//        permission.put("role",role);
//        JSONArray user = new JSONArray();
//        user.add(uid);
//        permission.put("user",user);
//        object.put("permission",permission);
//
//        // 表单
//        JSONObject form = new JSONObject();
//        form.put("formId",query.get("formId"));
//        JSONArray item = new JSONArray();
//
//        /*for(int i = 0;i< quarters.length;i++){
//            JSONArray obj = new JSONArray();
//            obj.add(quarters[i]);
//            quarter.add(obj);
//        }*/
//
//        for(int i = 0;i< dataId.length;i++){
//            JSONObject nodeJson = new JSONObject();
//            nodeJson.put("name",dataId[i]);
//            nodeJson.put("title",dataTitle[i]);
//            if(dataType.length>0){
//                nodeJson.put("type",dataType[i]);
//            }
//            item.add(nodeJson);
//        }
//
//        form.put("items",item);
        JSONObject permission = permissionObj(query);
        JSONObject form = formObj(query);
        object.put("permission",permission);
        object.put("form",form);

        JSONArray nodeArr = new JSONArray();
        nodeArr.add(object);
        if("StartEvent".equals(isStart)){
            jsonObject.put("start",nodeArr);
        }else{
            jsonObject.put("node",nodeArr);
        }
        System.out.println(jsonObject);

        return jsonObject;
    }

    /*public R getId(String _id){
        var list = a();
        var collection = db.getCollection("workflow");
        collection.find(new Document(){{
            put("_id", new ObjectId(_id));
        }}).iterator()
                .forEachRemaining(d -> {
                    var id = d.getObjectId("_id").toHexString();
                    d.put("_id", id);
                    list.add(d);
                });


        return R.ok(list);
    }*/

    private JSONObject permissionObj( JSONObject object){
        String[] quarters = object.get("quarters").toString().split(",");
        String[] depts = object.get("dept").toString().split(",");
        String[] roles = object.get("role").toString().split(",");
        String[] uid = object.get("uid").toString().split(",");
        JSONObject permission = new JSONObject();
        JSONArray quarter = new JSONArray();
        quarter.add(quarters);
        permission.put("quarters",quarter);
        JSONArray dept = new JSONArray();
        dept.add(depts);
        permission.put("dept",dept);
        JSONArray role = new JSONArray();
        role.add(roles);
        permission.put("role",role);
        JSONArray user = new JSONArray();
        user.add(uid);
        permission.put("user",user);

        return permission;
    }

    private JSONObject formObj(JSONObject object){
        String[] dataId = object.get("dataId").toString().split(",");
        String[] dataTitle = object.get("dataTitle").toString().split(",");
        String[] dataType = object.get("dataType").toString().split(",");

        // 表单
        JSONObject form = new JSONObject();
        form.put("formId",object.get("formId"));
        JSONArray item = new JSONArray();


        for(int i = 0;i< dataId.length;i++){
            JSONObject nodeJson = new JSONObject();
            nodeJson.put("name",dataId[i]);
            nodeJson.put("title",dataTitle[i]);
            if(dataType.length == dataId.length){
                nodeJson.put("type",dataType[i]);
            }
            item.add(nodeJson);
        }

        form.put("items",item);
        return form;
    }
    /**
     * 开始节点
     */
    public JSONObject saveStartEvent(JSONObject query){
        JSONObject jsonObject = new JSONObject();
        JSONObject start = new JSONObject();
//        JSONObject permission = permissionObj(query);
//        JSONObject form = formObj(query);
//        start.put("permission",permission);
//        start.put("form",form);
//
////        JSONArray startArr = new JSONArray();
////        startArr.add(start);
//        start.put("start",start);


        JSONObject permission = permissionObj(query);
        JSONObject form = formObj(query);
        start.put("permission",permission);
        start.put("form",form);

        JSONArray nodeArr = new JSONArray();
        nodeArr.add(start);
        jsonObject.put("start",nodeArr);
        jsonObject.put("taskId",query.get("taskId"));

        return jsonObject;
    }
}
