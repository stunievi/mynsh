package com.beeasy.hzbpm.service;

import cn.hutool.core.util.StrUtil;
import com.beeasy.hzbpm.entity.BpmInstance;
import com.beeasy.hzbpm.entity.BpmModel;
import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.client.MongoCollection;
import org.beetl.sql.core.SQLReady;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;

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

    public static BpmService ofIns(String id){
        BpmService bpmService = new BpmService();
//        bpmService.model = document;
        return bpmService;
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
