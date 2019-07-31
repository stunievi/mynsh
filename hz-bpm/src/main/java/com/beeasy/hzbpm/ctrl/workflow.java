package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzbpm.entity.BpmInstance;
import com.beeasy.hzbpm.filter.Auth;
import com.beeasy.hzbpm.service.BpmService;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.engine.PageQuery;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.stream.Collectors;

import static com.beeasy.hzbpm.bean.MongoService.db;
import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.*;
import static com.github.llyb120.nami.server.Vars.$request;

public class workflow {


    private MongoCollection<Document> getCollection(){
        return db.getCollection("workflow");
    }


    /**
     * 菜单
     * @return
     */
    public Object menu(){
        MongoCollection<Document> collection = db.getCollection("cat");
        Arr cats = collection.aggregate(a(
                o(
                        "$lookup", o(
                                "from","workflow",
                                "localField", "_id",
                                "foreignField", "pid",
                                "as","wfs"
                        )
                ),
                o(
                        "$project", o(
                                "_id", o(
                                        "$toString", "$_id"
                                ),
                                "name",1,
                                "pid", o(
                                        "$toString", "$pid"
                                ),
                                "wfs._id", 1,
                                "wfs.modelName",1,
                                "wfs.arrangementData.listFields", 1
                        )
                )
        ).toBson()).into(a());
        List ret = (List) tree((Collection)cats, "pid", "_id");

//        Arr wfs = getCollection().aggregate(a(
//                o("$project",o("_id", o(
//                                        "$toString", "$_id"
//                                ), "modelName",1,
//                            "listFields", "$arrangementData.listFields"
//                        ))
//        ).toBson()).into(a());

//        MongoCollection<Document> catcol = db.getCollection("cat");
        for (Object o : ret) {
            walkCats((Map) o);
        }
        Result ok = Result.ok(ret);
        return ok;
    }

    private void walkCats(Map cat){
        List children = new ArrayList();
        //扔进去工作流
        List wfs = (List) cat.get("wfs");
        children.addAll(wfs);
        for (Object o : wfs) {
            Map map = (Map) o;
            map.put("_id", ((ObjectId)map.get("_id")).toString());
            map.put("name", map.get("modelName"));
            Map ar = (Map) map.get("arrangementData");
            map.put("href", "./htmlsrc/bpm/ins/ins.list.html?id=" + map.get("_id") + "&fields=" + URLUtil.encode(Json.stringify(ar.get("listFields"))));
        }
        List _children = (List) cat.get("children");
        if (_children != null) {
            children.addAll(_children);
            for (Object child : _children) {
                walkCats((Map) child);
            }
        }
        if(!children.isEmpty()){
            cat.put("children", children);
        } else {
            cat.remove("children");
        }
        cat.remove("wfs");

    }


    /**
     * 查询和我有关的流程
     * @param id
     * @return
     */
    public Object insList(String id, Integer page, Integer size){
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        MongoCollection<Document> col = db.getCollection("bpmInstance");
        Obj match =o(
                "bpmId", new ObjectId(id),
                "logs.uid", Auth.getUid() + ""
        );
        int count = (int) col.countDocuments(match.toBson());
        List list = (List)col.aggregate(a(
                o("$match", match),
                o("$project", o(
                        "_id", o("$toString", "$_id"),
                        "attrs",1,
                        "createTime", 1,
                        "lastModifyTime", 1
                )),
                o("$sort",o("lastModifyTime", -1)),
                o("$skip", (page - 1) * size),
                o("$limit", size)
        ).toBson()).into(a())
                .stream()
                .map(e -> {
                    Document doc = (Document) e;
                    Obj obj = o();
                    obj.put("_id",doc.get("_id"));
                    obj.put("lastModifyTime",doc.get("lastModifyTime"));
                    obj.put("createTime",doc.get("createTime"));
                    obj.putAll((Map)doc.get("attrs"));
                    return obj;
                })
                .collect(Collectors.toList());
        PageQuery pq = new PageQuery();
        pq.setPageNumber(page);
        pq.setPageSize(size);
        pq.setList(list);
        pq.setTotalRow(count);
        return Result.ok(pq);
    }

    public Object preparePub(String id){
        try{
            BpmService service = BpmService.ofModel(id);
            return Result.ok(service.preparePub(Auth.getUid() + ""));
        } catch (BpmService.BpmException e){
            return Result.error(e.error);
        }
    }


    /**
     * 创建任务实例
     * @param id
     * @param data
     * @return
     */
    public Object createIns(String id, Obj data){
        BpmService service = BpmService.ofModel(id);
        Document ins = service.createBpmInstance(Auth.getUid() + "", data);
        return Result.ok(ins.getObjectId("_id").toString());
//        service = BpmService.ofIns(ins);
//        return Result.ok(service.getNextNodePersons(Auth.getUid() + "", o()));
    }


    /**
     * 得到下一节点的可执行人
     * @param id
     * @return
     */
    public Object getNextDealers(String id){
        BpmService service = BpmService.ofIns(id);
        return Result.ok(service.getNextNodePersons(Auth.getUid() + "", o()));
    }


    /**
     * 保存选取的下一步处理人
     * @param id
     * @param nextUid
     * @return
     */
    public Object saveIns(String id, String nextUid){
        BpmService service = BpmService.ofIns(id);
        return Result.ok(service.saveIns(Auth.getUid() + "", o(), nextUid));
    }


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
