package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzbpm.entity.BpmInstance;
import com.beeasy.hzbpm.filter.Auth;
import com.beeasy.hzbpm.service.BpmService;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.core.MultipartFile;
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
import static com.beeasy.hzbpm.bean.FileStorage.storage;

public class workflow {


    private MongoCollection<Document> getCollection(){
        return db.getCollection("workflow");
    }



    public Object uploadImage(String base64){
        return Result.ok(storage.upload(base64));
    }

    public MultipartFile download(String path){
        return storage.download(path);
    }

    public Object logList(String id, String nodeId){
        MongoCollection<Document> collection = db.getCollection("bpmInstance");
        Arr condition = a(
                o(
                        "$match", o(
                                "_id", new ObjectId(id)
                        )

                )
        );
        if(StrUtil.isBlank(nodeId)){
            condition.add(o(
                    "$project", o("logs", 1)
            ));
        } else {
            condition.add(
                    o(
                            "$project", o(
                                    "logs", o(
                                            "$filter", o(
                                                    "input", "$logs",
                                                    "as", "item",
                                                    "cond", o("$eq", a("$$item.nodeId", nodeId))
                                            )
                                    )
                            )
                    )
            );
        }
        return Result.ok(collection.aggregate(condition.toBson()).first().get("logs"));
    }

    /**
     * 菜单
     * @return
     */
    public Object menu(){
        MongoCollection<Document> collection = db.getCollection("cat");
        Arr cats = collection.aggregate(a(
                o("$match",o("type","1")),
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
        Obj match = o(
                "$and", a(
                        o("bpmId", new ObjectId(id)),
                        o("$or", a(
                                        o("logs.uid", Auth.getUid() + ""),
                                        o("currentNodes", o(
                                            "$elemMatch", o(
                                                    "uids",Auth.getUid() + ""
                                                )
                                        ))
                        ))
                )
        );
        int count = (int) col.countDocuments(match.toBson());
        List list = (List)col.aggregate(a(
                o("$match", match),
                o("$project", o(
                        "_id", o("$toString", "$_id"),
                        "attrs",1,
                        "createTime", 1,
                        "lastModifyTime", 1,
                        "currentNodes", 1
                )),
                o("$sort",o("lastModifyTime", -1)),
                o("$skip", (page - 1) * size),
                o("$limit", size)
        ).toBson()).into(a())
                .stream()
                .map(e -> {
                    Document doc = (Document) e;
                    String uname = "";

                    List<String> curr = (List<String>) doc.get("currentNodes");
                    Object ob = curr.get(0);
                    List<String> uids = (List<String>) ((Document) ob).get("uids");
                    if(null != uids && uids.size()>0){
                        uname = sqlManager.execute(new SQLReady("select true_name from t_user where id = ?", uids.get(0)), Obj.class).get(0).s("true_name");
                    }
                    String su = sqlManager.execute(new SQLReady("select su from t_user where id = ?", Auth.getUid()), Obj.class).get(0).s("su");
                    Obj obj = o();
                    obj.put("id",doc.get("_id"));
                    obj.put("lastModifyTime",doc.get("lastModifyTime"));
                    obj.put("createTime",doc.get("createTime"));
//                    obj.put("uName",uname);
                    obj.put("isAdmin",su);
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


    public Object getInsInfo(String id){
        BpmService service = BpmService.ofIns(id);
        return Result.ok(service.getInsInfo(Auth.getUid() + ""));
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
    public Object nextApprover(String id, String nextUid){
        BpmService service = BpmService.ofIns(id);
        return Result.ok(service.nextApprover(Auth.getUid() + "",  nextUid,o()));
    }

    /**
     * 保存节点数据
     * @param id
     * @param data
     * @return
     */
    public Result saveIns(String id,Obj data,String mode){
        BpmService service = BpmService.ofIns(id);
        return Result.ok(service.saveIns(Auth.getUid() + "", data, false, mode));
    }

    /**
     * 提交节点数据
     * @param id
     * @param data
     * @return
     */
    public Result subIns(String id,Obj data){
        BpmService service = BpmService.ofIns(id);
        Object bl = service.submitIns(Auth.getUid() + "", data);
        Map<Object, Object> res = new HashMap<>();
        res.put("id",id);
        res.put("res",bl);
        return Result.ok(res);
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
     * 删除流程
     */
    public Object deleteIns(String id){
        if(null == id){
            return Result.error("流程id为空！");
        }
        MongoCollection<Document> collection = db.getCollection("bpmInstance");
        Document ret = collection.findOneAndDelete(new Document() {{
            put("_id", new ObjectId(id));
        }});
        return Result.ok(ret != null);

    }


    /***********************************/

}
