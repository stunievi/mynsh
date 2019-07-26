package com.beeasy.hzbpm.service;

import com.beeasy.hzbpm.entity.BpmModel;
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
        bpmService.model = Json.cast(o("nodes", data.get("arrangementData")), BpmModel.class);
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

    public Object getNextNodePersons(long uid, Obj attrs){
        //查找所有的属性
        Document oldAttrs = new Document((Document) ins.get("attrs"));
        oldAttrs.putAll(attrs);
        //查找下一个可用的节点

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
