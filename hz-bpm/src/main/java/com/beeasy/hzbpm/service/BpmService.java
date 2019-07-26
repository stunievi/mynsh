package com.beeasy.hzbpm.service;

import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Obj;
import org.beetl.sql.core.SQLReady;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.a;

public class BpmService {

    private Document bpm = null;
//    public long uid;
    public BpmService(Document document){
        this.bpm = document;
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


//    public Object createBpmInstance(long uid, Obj data){

//    }
    




    public Document getNode(String nodeId){
        Document nodes = (Document) bpm.get("nodes");
        if(nodeId.equals("start")){
            return (Document) nodes.get(bpm.getString("start"));
        } else if(nodeId.equals("end")){
            return (Document) nodes.get(bpm.getString("end"));
        } else {
            return (Document) nodes.get(nodeId);
        }
    }





}
