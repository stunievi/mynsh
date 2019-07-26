package com.beeasy.hzbpm.service;

import com.github.llyb120.nami.json.Obj;
import com.mongodb.client.MongoCollection;
import org.beetl.sql.core.SQLReady;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;

public class BpmService {

    //BpmModel
    private Document model = null;

    //BpmInstance
    private Document ins = null;
//    public long uid;
    private BpmService(){
    }

    public static BpmService ofModel(Document document){
        BpmService bpmService = new BpmService();
        bpmService.model = document;
        return bpmService;
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
        return ofModel((Document)data.get("arrangementData"));
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
        Document node = getNode("start");
        List<Obj> list = sqlManager.execute(new SQLReady("select uid,oid,pid from t_org_user where uid = ?", uid), Obj.class);
        return list.stream().anyMatch(e -> {
            return ((List)node.get("qids")).contains(e.s("oid")) || ((List)node.get("rids")).contains(e.s("oid")) || ((List)node.get("dids")).contains(e.s("pid")) || ((List)node.get("uids")).contains(e.s("uid"));
        });
    }


    public Object createBpmInstance(long uid, Obj data){
//
        return null;
    }





    public Document getNode(String nodeId){
        Document nodes = (Document) model.get("nodes");
        if(nodeId.equals("start")){
            return (Document) nodes.get(model.getString("start"));
        } else if(nodeId.equals("end")){
            return (Document) nodes.get(model.getString("end"));
        } else {
            return (Document) nodes.get(nodeId);
        }
    }





}
