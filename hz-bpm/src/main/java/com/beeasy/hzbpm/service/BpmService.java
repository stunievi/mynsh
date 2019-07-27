package com.beeasy.hzbpm.service;

import COM.ibm.db2.app.UDF;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzbpm.entity.BpmModel;
import com.beeasy.hzbpm.filter.Auth;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import org.beetl.sql.core.SQLReady;
import org.bson.BSONObject;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;
import static com.github.llyb120.nami.server.Vars.$request;

public class BpmService {

    //BpmModel
    private BpmModel model = null;

    //BpmInstance
    private Document ins = null;
//    public long uid;
    private BpmService(){
    }

    public static BpmService ofModel(Document document){
        return null;
//        bpmService.model = Json.cast(document, BpmModel.class);
//        return bpmService;
    }

    public static BpmService ofModel(final String modelId){
        MongoCollection<Document> col = db.getCollection("workflow");
//        col.mapReduce("function(){return 1}", "function(){return 2}");
        Document data = col.aggregate(
                a(
                        o("$match", o("_id", new ObjectId(modelId))),
                        o("$project", o("arrangementData", 1))
                ).toBson()
        ).first();
        if (data == null) {
            return null;
        }
        BpmService bpmService = new BpmService();
        bpmService.model = Json.cast(data.get("arrangementData"), BpmModel.class);
        return bpmService;
    }

    public static BpmService ofIns(String id, Obj data){

        JSONArray jsonArray = new JSONArray();
//        bpmService.model = document;

        long uid = Auth.getUid();
        Map<String,Object> attrs = new HashMap<>();
        BpmService bpmService = BpmService.ofModel(id);
        System.out.println(bpmService.model.nodes);

        // 开始节点
        String startNode = bpmService.model.start;


        List<Long> ql = bpmService.model.nodes.get(startNode).qids;
        // 通过人查询部门
        List<Obj> pList = sqlManager.execute(new SQLReady("select parent_id as deptId,name as deptName from t_org a where id in (select parent_id from t_org o inner join t_user_org uo on uo.oid=o.id where uo.uid=?)", uid), Obj.class);
        // 通过岗位查询部门
//            List<Obj> qlist = sqlManager.execute(new SQLReady("select parent_id as deptId,name as deptName from t_org a where id = ( select  * from t_org where id in ?)", pList), Obj.class);

//        attrs = (Map<String, Object>) data.get("data");

//        List<String> allFields = bpmService.model.nodes.get(startNode).ext.allFields;
//        for(String all : allFields){
//            attrs.put(all,formData.get(all));
//        }
        JSONObject dataLog = new JSONObject();
        dataLog.put("nodeId",startNode);
        dataLog.put("time",new Date());
        dataLog.put("uid", uid);
        dataLog.put("attrs", attrs);
        JSONArray logs = new JSONArray();
        logs.add(dataLog);

        JSONObject currentNode = new JSONObject();
        currentNode.put("nodeId",startNode);
        JSONArray uids = new JSONArray();
        uids.add(uid);
        currentNode.put("uids",uids);
        JSONArray currentNodes = new JSONArray();
        currentNodes.add(currentNode);

        MongoCollection<Document> collection = db.getCollection("bpmInstance");
        Obj obj = new Obj();
        obj.put("state","DEALING");
        obj.put("bpmId",id);
        obj.put("bpmName",bpmService.model.workflowName);
        obj.put("pubUid",uid);
        obj.put("pubUName","");
        obj.put("depId","");
        obj.put("depName","");
        obj.put("bpmModel",modelTOjson(bpmService.model));
        obj.put("createTime",new Date());
        obj.put("lastMoidfyTime",new Date());
        obj.put("currentNodes",currentNodes);
        obj.put("attrs",attrs);
        obj.put("logs",logs);

        Document doc = obj.toBson();
        collection.insertOne(doc);

        return bpmService;
    }

    private static BsonArray modelTOjson(BpmModel model){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("formId",new ObjectId(model.formId.toHexString()));
        jsonObject.put("workflowName",model.workflowName);
        jsonObject.put("template",model.template);
        jsonObject.put("rendered",model.rendered);
        jsonObject.put("fields",model.fields);
        jsonObject.put("nodes",model.nodes);
        jsonObject.put("start",model.start);
        jsonObject.put("end",model.end);
        jsonArray.add(jsonObject);
        return BsonArray.parse(jsonArray.toString());
    }

    /**
     * 检查一个用户是否可以发布该流程
     * @param uid
     * @return
     */
    public boolean canPub(long uid){
        BpmModel.Node node = getNode("start");
        List<Obj> list = sqlManager.execute(new SQLReady("select uid,oid,pid from t_org_user where uid = ?", uid), Obj.class);
        return list.stream().anyMatch(e -> {
            return (node.qids).contains(e.s("oid")) || (node.rids).contains(e.s("oid")) || (node.dids).contains(e.s("pid")) || (node.uids).contains(e.s("uid"));
        });
    }


    public Object createBpmInstance(long uid, Obj data){
//
        return null;
    }

    public Object getNextNodePersons(long uid, Obj attrs){
        //查找所有的属性
        Document oldAttrs = new Document((Document) ins.get("attrs"));
        oldAttrs.putAll(attrs);
        //查找下一个可用的节点
        return null;
    }


    public BpmModel.Node getNode(String nodeId){
        if(nodeId.equals("start")){
            return model.nodes.get(model.start);
        } else if(nodeId.equals("end")){
            return model.nodes.get(model.end);
        } else {
            return model.nodes.get(nodeId);
        }
    }





}
