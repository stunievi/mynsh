package com.beeasy.hzbpm.service;

import cn.hutool.core.util.StrUtil;
import com.beeasy.hzbpm.entity.BpmInstance;
import COM.ibm.db2.app.UDF;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzbpm.entity.BpmModel;
import com.github.llyb120.nami.json.Arr;
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
    private BpmInstance ins = null;
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

    /**
     * 通过当前提交的属性查询下一个应该移交的节点
     * @param uid 提交属性的人，必须是当前节点的经办人
     * @param attrs 提交到该任务上的属性
     * @return
     */
    public BpmModel.Node getNextNode(long uid, Obj attrs){
        //查找所有的属性
        Obj oldAttrs = new Obj(ins.attrs);
        oldAttrs.putAll(attrs);
        //当前处理的节点
        BpmInstance.CurrentNode currentNode  = ins.currentNodes.stream()
                .filter(e -> e.uids.contains(uid))
                .findFirst()
                .orElse(null);
        if (currentNode == null) {
            error("当前节点查询失败");
        }
        //查找下一个可用的节点
        BpmModel.Node node = getNode(currentNode.nodeId);
        if (node == null) {
            error("当前节点查询失败");
        }
        BpmModel.Node target = null;
        for (BpmModel.NextNode nextNode : node.nextNodes) {
            //如果表达式位空，则直接使用该节点
            if(StrUtil.isBlank(nextNode.expression)){
                target = getNode(nextNode.node);
                break;
            } else if(runExpression(nextNode.expression)){
                target = getNode(nextNode.node) ;
                break;
            }
        }
        if (target == null) {
            error("找不到符合跳转条件的下一节点");
        }
        return target;
    }

    /**
     * 查询下一个节点的可处理人，以本部门的为最优先
     * @param uid
     * @param attrs
     * @return
     */
    public List<Obj> getNextNodePersons(long uid, Obj attrs){
        BpmModel.Node target = getNextNode(uid, attrs);
        //查询这个节点所有命中的人
        return sqlManager.select("workflow.查找节点处理人员", Obj.class, o(
            "uid", uid,
            "uids", target.uids.isEmpty() ? a(-1) : target.uids    ,
                "qids", target.qids.isEmpty() ? a(-1) : target.qids    ,
                "rids", target.rids.isEmpty() ? a(-1) : target.rids    ,
                "dids", target.dids.isEmpty() ? a(-1) : target.dids
        ));
    }

    private void error(String errMessage){
        throw new RuntimeException(errMessage);
    }
    private boolean runExpression(String expression){
        return false;
    }

    /**
     * 通过节点ID查询节点
     * @param nodeId start表示开始，end表示结束，其余情况使用ID查询
     * @return
     */
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
