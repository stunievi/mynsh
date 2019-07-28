package com.beeasy.hzbpm.service;

import cn.hutool.core.util.StrUtil;
import com.beeasy.hzbpm.entity.BpmInstance;
import COM.ibm.db2.app.UDF;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzbpm.entity.BpmModel;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.client.MongoCollection;
import org.beetl.sql.core.SQLReady;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;

public class BpmService {

    //BpmModel
    private BpmModel model = null;
    private String modelId;

    private Document arrangementData = null;

    //BpmInstance
    private BpmInstance ins = null;
//    public long uid;
    private BpmService(){
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
        bpmService.modelId = modelId;
        bpmService.arrangementData = (Document) data.get("arrangementData");
        bpmService.model = Json.cast(data.get("arrangementData"), BpmModel.class);
        return bpmService;
    }

    public static BpmService ofIns(String insId){
        MongoCollection<Document> col = db.getCollection("bpmInstance");
//        col.mapReduce("function(){return 1}", "function(){return 2}");
        Document data = col.aggregate(
                a(
                        o("$match", o("_id", new ObjectId(insId)))
                ).toBson()
        ).first();
        if (data == null) {
            return null;
        }
        BpmService bpmService = new BpmService();
        bpmService.arrangementData = (Document) data.get("bpmModel");
        bpmService.ins = Json.cast(data, BpmInstance.class);
        bpmService.model = bpmService.ins.bpmModel;
        bpmService.modelId = bpmService.ins.bpmId.toString();
        return bpmService;
    }

//    public static BpmService ofIns(String id, Obj data, String uid){
////
//
//
//        return bpmService;
//    }

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


    public BpmInstance createBpmInstance(long uid,  Obj data){
        Map<String,Object> attrs = new HashMap<>();
        BpmService bpmService = this;
//        BpmService bpmService = BpmService.ofModel(modelId);

        // 开始节点
        String startNode = bpmService.model.start;
        List<String> qids = bpmService.model.nodes.get(startNode).qids;
        List<String> rids = bpmService.model.nodes.get(startNode).rids;
        List<String> dids = bpmService.model.nodes.get(startNode).dids;

        String qid =  qids.stream().map(q -> "'" + q + "'").collect(Collectors.joining(","));
        String rid =  rids.stream().map(r -> "'" + r + "'").collect(Collectors.joining(","));
        String did =  dids.stream().map(d -> "'" + d + "'").collect(Collectors.joining(","));

        List<Obj> list = sqlManager.execute(new SQLReady(String.format("select uid,utname,pid,pname from t_org_user where (oid in (%s) or oid in (%s) or pid in (%s)) and uid='%s'", qid,rid,did,uid)), Obj.class);
        List<Obj> list2 = sqlManager.execute(new SQLReady(String.format("select uid,utname,pid,pname from t_org_user where uid='%s'", uid)), Obj.class);
        list.addAll(list2);

//        List<Long> ql = bpmService.model.nodes.get(startNode).qids;

        List<String> allFields = bpmService.model.nodes.get(startNode).allFields;
        for(String all : allFields){
            attrs.put(all,data.get(all));
        }
        String deptName = "";
        long deptId = 0L;
        String uName = "";
        if(list.size()>0){
            for(Obj li :list){
                if(null != li.get("pid")){
                    deptId = (long) li.get("pid");
                    deptName = (String) li.get("pname");
                    uName = (String) li.get("utname");
                    break;
                }
            }
        }
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
//        BpmInstance ins = new BpmInstance();
        Obj obj = new Obj();
        obj.put("state","DEALING");
        obj.put("bpmId",new ObjectId(modelId));
        obj.put("bpmName",bpmService.model.workflowName);
        obj.put("pubUid",uid);
        obj.put("pubUName",uName);
        obj.put("depId",deptId);
        obj.put("depName",deptName);
        obj.put("bpmModel",bpmService.arrangementData);
        obj.put("currentNodes",currentNodes);
        obj.put("attrs",attrs);
        obj.put("logs",logs);
        obj.put("createTime",new Date());
        obj.put("lastMoidfyTime",new Date());

        Document doc = obj.toBson();
        collection.insertOne(doc);
        return Json.cast(doc,BpmInstance.class);
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
