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
import org.beetl.sql.core.SQLReady;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.xml.crypto.Data;
import java.util.*;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;
import static com.github.llyb120.nami.server.Vars.$request;

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
     * 发起人所需填写表单
     * @param formId
     * @return
     */
    public Result startExt(String formId){
        Long uid = Auth.getUid();
        Arr list = a();

        Object startNode = startNode();

        Set<Long> uidSet = startList(startNode);

        if(!uidSet.contains(uid)){
            return Result.error("没有权限发起流程！");
        }
        Object ext = ((Document) startNode).get("ext");
        return Result.ok(ext);

    }

    /**
     * 获取最终生成的流程文件
     * @param formId 表单ID
     * @return
     */
    private Arr bpmWorkflow(String formId) {
        Arr list = a();
        MongoCollection<Document> collection = db.getCollection("workflow");
        collection.find(new Document(){{
//            put("data.formId", new ObjectId(formId));
            put("arrangementData.formId", formId);
        }}).iterator()
                .forEachRemaining(d -> {
                    String id = d.getObjectId("_id").toHexString();
                    d.put("_id", id);
                    list.add(d.get("arrangementData"));

                });
        return list;
    }

    /**
     * 发起节点信息
     */
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
     * 发起人名单
     */
    private Set<Long> startList(Object startNode){

        Set<Long> uidSet = new HashSet<Long>();
        List<Long> uidList = (List<Long>) ((Document) startNode).get("uids");
        List<Long> qidList = (List<Long>) ((Document) startNode).get("qids");
        List<Long> didList = (List<Long>) ((Document) startNode).get("dids");
        List<Long> ridList = (List<Long>) ((Document) startNode).get("rids");
        uidSet.addAll(uidList);
        uidSet.addAll(qidList);
        uidSet.addAll(didList);
        uidSet.addAll(ridList);
        return uidSet;
    }

    /**
     * 提交
     */
    public Result submitIns(Obj bady){
        System.out.println($request);
        Long uid = Auth.getUid();
        String formId = $request.s("formId");
        Arr bpmWorkflow = bpmWorkflow(formId);

        // 开始节点填入字段
        Object extArr = null;

        for(Object li : bpmWorkflow){
            Document a = (Document) li;
            String start = (String) a.get("start");
            Map<String, Object> nodeMap = (Map<String, Object>) a.get("node");
            Map startNodeMap = (Map) nodeMap.get(start);
            Iterator iter = startNodeMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                if("ext".equals(entry.getKey())){
                    List<Object> val = (List<Object>) entry.getValue();

                    for(Object va : val){
                        extArr = va;
                    }
                break;
                }
            }

        }
        System.out.println(extArr);

        Object startNode = startNode();
        Set<Long> uidSet = new HashSet<>();

        List<Long> uidList = (List<Long>) ((Document) startNode).get("uids");
        List<String> qidList = (List<String>) ((Document) startNode).get("qids");
        List<String> didList = (List<String>) ((Document) startNode).get("dids");
        List<String> ridList = (List<String>) ((Document) startNode).get("rids");


        // 岗位
        List<Long> qInfo = sqlManager.execute(new SQLReady("select id from t_user u where exists(select 1 from t_user_org where uid = u.id and oid in "+qidList+")"),Long.class);
//        for(String qid :qidList){
//            List<Long> qInfo = sqlManager.execute(new SQLReady("select id from t_user u where exists(select 1 from t_user_org where uid = u.id and oid = "+qid+")"),Long.class);
//            if(qInfo.contains(uid)){
//                for(Long u : qInfo){
//                    if(u.equals(uid)){
//                        List<Obj> dept = sqlManager.execute(new SQLReady("select parent_id as deptId,name as deptName from t_org where id=(select parent_id from t_org where id="+u+")"),Obj.class);
//                    }
//                }
//            }
//        }
        // 角色
        List<Long> rInfo = sqlManager.execute(new SQLReady("select id from t_user u where exists(select 1 from t_user_org where uid = u.id and oid in "+ridList+")"),Long.class);
        // 部门
        List<Long> dInfo = sqlManager.execute(new SQLReady("select uid from t_user_org where oid  in (select id from t_org where PARENT_ID in "+didList+")"),Long.class);

        uidSet.addAll(uidList);
        uidSet.addAll(rInfo);
        uidSet.addAll(dInfo);
        uidSet.addAll(qInfo);


        if(!uidSet.contains(uid)){
            return Result.error("没有权限发起流程！");
        }
        //任务所属部门


//        List<Obj> userInfo = sqlManager.execute(new SQLReady("select u.id,u.true_name as name,o.oid from t_user u inner join t_user_org o on o.uid=u.id and uid="+uid),Obj.class);
//
//        List<String> didList = (List<String>) ((Document) startNode).get("dids");
//        String userName = "";
//        List<String> orgSet = new ArrayList<>();
//        if(userInfo.size()>0){
//            userName = userInfo.get(0).s("name");
//            for(Obj list : userInfo){
//                orgSet.add(list.s("oid"));
//            }
//        }
        // 提交字段






        return Result.ok();
    }



    /***********************************/

}
