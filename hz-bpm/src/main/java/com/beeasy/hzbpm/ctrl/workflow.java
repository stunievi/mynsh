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
import org.beetl.sql.core.engine.PageQuery;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.beeasy.hzbpm.bean.FileStorage.storage;
import static com.beeasy.hzbpm.bean.MongoService.db;
import static com.github.llyb120.nami.func.Function.func;
import static com.github.llyb120.nami.json.Json.*;

public class workflow {


    private MongoCollection<Document> getCollection() {
        return db.getCollection("workflow");
    }



    public Result upload(MultipartFile file) throws IOException {
        return Result.ok(
                o(
                        "name", file.fileName,
                        "type", file.contentType,
                        "id", storage.upload(file),
                        "creator", Auth.getUid() + "",
                        "action", "upload"
                )
        );
    }

    /**
     * 任务模型树列表
     * @return
     */
    public Object modelList(){
        MongoCollection<Document> collection = db.getCollection("cat");
        Arr cats = collection.aggregate(a(
                o("$match", o("type", "1")),
                o(
                        "$lookup", o(
                                "from", "workflow",
                                "localField", "_id",
                                "foreignField", "pid",
                                "as", "children"
                        )
                ),
                o(
                        "$project", o(
                                "id", o(
                                        "$toString", "$_id"
                                ),
                                "text", "$name",
                                "pid", o(
                                        "$toString", "$pid"
                                ),
                                "type", "cat",
                                "state.opened", "true",
                                "children",o(
                                        "$map",o(
                                                "input", "$children" ,
                                                "as", "item",
                                                "in", o(
                                                        "id", o("$toString", "$$item._id"),
                                                        "text", "$$item.modelName",
                                                        "type", "form"
                                                )
                                        )
                                )
                        )
                )
        ).toBson()).into(a());
        Json tree = tree((List)cats, "pid", "id");
        return Result.ok(tree);
    }

    public Object pause(String id) {
        BpmService service = BpmService.ofIns(id);
        service.pause(Auth.getUid() + "");
        return Result.ok();
    }

    public Object resume(String id) {
        BpmService service = BpmService.ofIns(id);
        service.resume(Auth.getUid() + "");
        return Result.ok();
    }

    public Object forceEnd(String id) {
        BpmService service = BpmService.ofIns(id);
        service.forceEnd(Auth.getUid() + "");
        return Result.ok();
    }

    public Object forceResume(String id) {
        BpmService service = BpmService.ofIns(id);
        service.forceResume(Auth.getUid() + "");
        return Result.ok();
    }

    public Object goBack(String id){
        BpmService service = BpmService.ofIns(id);
        service.goBack(Auth.getUid() + "");
        return Result.ok();
    }


    public Object uploadImage(String base64) {
        return Result.ok(storage.upload(base64));
    }

    public MultipartFile download(String path, String name) {
        MultipartFile file = storage.download(path);
        if(StrUtil.isNotBlank(name)){
            file.fileName = name;
        }
        return file;

    }

    public Object urge(String id, String msg) {
        BpmService service = BpmService.ofIns(id);
        service.urge(Auth.getUid() + "", msg);
        return Result.ok();
    }

    public Object logList(String id, String nodeId) {
        MongoCollection<Document> collection = db.getCollection("bpmInstance");
        Arr condition = a(
                o(
                        "$match", o(
                                "_id", new ObjectId(id)
                        )

                )
        );
        if (StrUtil.isBlank(nodeId)) {
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
        Object logs = ((Map) collection.aggregate(condition.toBson()).first()).get("logs");
        return Result.ok(logs);
//        return Result.ok(((List<Map>) collection.aggregate(condition.toBson()).first().get("logs")));
    }


    public Object copyBpm(String id) {
        MongoCollection<Document> col = db.getCollection("workflow");
        Document doc = col.find(Filters.eq("_id", new ObjectId(id))).first();
        if (doc == null) {
            return Result.error("复制失败");
        }
        doc.put("modelName", doc.getString("modelName") + "副本");
        doc.remove("_id");
        col.insertOne(doc);
        return Result.ok();
    }

    /**
     * 菜单
     *
     * @return
     */
    public Object menu() {
        MongoCollection<Document> collection = db.getCollection("cat");
        Arr cats = collection.aggregate(a(
                o("$match", o("type", "1")),
                o(
                        "$lookup", o(
                                "from", "workflow",
                                "localField", "_id",
                                "foreignField", "pid",
                                "as", "wfs"
                        )
                ),
                o(
                        "$project", o(
                                "_id", o(
                                        "$toString", "$_id"
                                ),
                                "name", 1,
                                "pid", o(
                                        "$toString", "$pid"
                                ),
                                "wfs._id", 1,
                                "wfs.modelName", 1,
                                "wfs.arrangementData.listFields", 1
                        )
                )
        ).toBson()).into(a());
        List ret = (List) tree((Collection) cats, "pid", "_id");


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

    private void walkCats(Map cat) {
        List children = new ArrayList();
        //扔进去工作流
        List wfs = (List) cat.get("wfs");
        children.addAll(wfs);
        for (Object o : wfs) {
            Map map = (Map) o;
            map.put("_id", ((ObjectId) map.get("_id")).toString());
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
        if (!children.isEmpty()) {
            cat.put("children", children);
        } else {
            cat.remove("children");
        }
        cat.remove("wfs");

    }


    /**
     * 查询和我有关的流程
     *
     * @param id
     * @return
     */
    public Object insList(String id, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        BpmService util = new BpmService();
        String uid = Auth.getUid() + "";
        MongoCollection<Document> col = db.getCollection("bpmInstance");
        Obj match = null;
        if (util.isSu(uid)) {
            match = o("$and", a(
                            o("bpmId", new ObjectId(id))
                    )
            );
        } else {
            match = o("$and", a(
                            o("bpmId", new ObjectId(id)),
                            o("$or", a(
                                    o("logs.uid", Auth.getUid() + ""),
                                    o("currentNodes", o(
                                            "$elemMatch", o(
                                                    "uids", Auth.getUid() + ""
                                            )
                                    ))
                            ))
                    )
            );
        }
        int count = (int) col.countDocuments(
                //match.toBson()
        );
        List list = (List) col.aggregate(a(
                o("$match", match),
                o("$project", o(
                        "_id", o("$toString", "$_id"),
                        "id", 1,
                        "attrs", 1,
                        "pubUid", 1,
                        "pubUName", 1,
                        "createTime", 1,
                        "lastModifyTime", 1,
                        "currentNodes", 1,
                        "state", 1
                )),
                o("$sort", o("lastModifyTime", -1)),
                o("$skip", (page - 1) * size),
                o("$limit", size)
        ).toBson()).into(a())
                .stream()
                .map(e -> {
                    Document doc = (Document) e;
                    Obj obj = o();
                    obj.putAll(doc);
                    obj.putAll((Map) doc.get("attrs"));
                    obj.remove("attrs");
                    BpmService bpmService = BpmService.ofIns(doc);
                    obj.put("deal", bpmService.canDealCurrent(uid));
                    obj.put("edit", bpmService.canEdit(uid));
                    obj.put("forceEnd", bpmService.canForceEnd(uid));
                    obj.put("forceResume", bpmService.canForceResume(uid));
                    obj.put("pause", bpmService.canPause(uid));
                    obj.put("resume", bpmService.canResume(uid));
                    obj.put("urge", bpmService.canUrge(uid));
                    obj.put("del", bpmService.canDelete(uid));

                    String names = bpmService.ins.currentNodes.stream()
                            .filter(ee -> ee.unames != null)
                            .flatMap(ee -> ee.unames.stream())
                            .collect(Collectors.joining(","));
                    obj.put("uName", names);

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

    public Object preparePub(String id) {
        BpmService service = BpmService.ofModel(id);
        return Result.ok(service.preparePub(Auth.getUid() + ""));
    }


    public Object getInsInfo(String id) {
        BpmService service = BpmService.ofIns(id);
        return Result.ok(service.getInsInfo(Auth.getUid() + ""));
    }


    /**
     * 创建任务实例
     *
     * @param id
     * @param data
     * @return
     */
    public Object createIns(String id, Obj data, Arr files) {
        BpmService service = BpmService.ofModel(id);
        Document ins = service.createBpmInstance(Auth.getUid() + "", data, files);
        return Result.ok(ins.getObjectId("_id").toString());
//        service = BpmService.ofIns(ins);
//        return Result.ok(service.getNextNodePersons(Auth.getUid() + "", o()));
    }


    /**
     * 得到下一节点的可执行人
     *
     * @param id
     * @return
     */
    public Object getNextDealers(String id, Obj data) {
        BpmService service = BpmService.ofIns(id);

        Object object = service.getNextNodePersons(Auth.getUid() + "", data);
        BpmInstance.CurrentNode currentNode = service.getCurrent(Auth.getUid() + "");
        return Result.ok(
                o(
                        "node", currentNode,
                        "next",                 object,
                        "ins", o(
                                "id", service.ins.id,
                                "state",service.ins.state,
                                "bpmName",service.ins.bpmName
                        )

                ));
    }


    /**
     * 保存选取的下一步处理人
     *
     * @param id
     * @return
     */
    public Object nextApprover(String id, Obj data, Arr files, Obj body) {
        BpmService service = BpmService.ofIns(id);
        return Result.ok(service.nextApprover(Auth.getUid() + "", o(), body, data,files));
    }

    /**
     * 保存节点数据
     *
     * @param id
     * @param data
     * @return
     */
    public Result saveIns(String id, Obj data, String mode, Arr files) {
        BpmService service = BpmService.ofIns(id);
        return Result.ok(service.saveIns(Auth.getUid() + "", data, false, mode, files));
    }

    /**
     * 提交节点数据
     *
     * @param id
     * @param data
     * @return
     */
    public Result subIns(String id, Obj data, Arr files) {
        BpmService service = BpmService.ofIns(id);
        Object bl = service.submitIns(Auth.getUid() + "", data, files);
        Map<Object, Object> res = new HashMap<>();
        res.put("id", id);
        res.put("res", bl);
        return Result.ok(res);
    }


    /**
     * 生成最终的流程文件
     */
    public Result saveWorkFlow(Obj body) {

        MongoCollection<Document> collection = db.getCollection("workflow");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(body);
        Document doc = new Document();
        doc.put("createTime", new Date());
        doc.put("data", BsonArray.parse(jsonArray.toString()));
        String workflowName = body.s("workflowName");

        Bson filter = Filters.eq("workflowName", workflowName);
        FindIterable<Document> lists = collection.find(filter);
        MongoCursor<Document> mongoCursor = lists.iterator();

        if (mongoCursor.hasNext()) {
            collection.updateOne(Filters.eq("workflowName", workflowName), new Document("$set", doc), new UpdateOptions().upsert(true));
        } else {
            collection.insertOne(doc);

        }

        return (Result.ok(doc));
    }

    /**
     * 删除流程
     */
    public Object deleteIns(String id) {
        BpmService service = BpmService.ofIns(id);
        service.delete(Auth.getUid() + "");
        return Result.ok();
    }

    public Object deleteInss(Obj ids,Obj data, Obj body){
        BpmService service = new BpmService();
        service.delete(Auth.getUid() + "",body);
        return Result.ok();
    }

    public Object getInsList(String type, String id, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        BpmService util = new BpmService();
        String uid = Auth.getUid() + "";
        MongoCollection<Document> col = db.getCollection("bpmInstance");
        Obj match = null;

        Obj types = o(
            "todo", a("流转中", "已暂停"),
            "processed", a("流转中", "已办结", "已暂停"),
            "over", a("已办结", "强制结束")
        );
        match = o(
                "state", o("$in", types.get(type)),
                "bpmId", StrUtil.isNotBlank(id) ? new ObjectId(id) : undefined,
                "$and", func(() -> {
                    if(util.isSu(uid)){
                        return undefined;
                    }
                    switch (type){
                        case "todo":
                            return a(o("currentNodes", o(
                                                "$elemMatch", o(
                                                        "uids",uid
                                                )
                                        )));

                        case "processed":
                            return a(
                                o("logs.uid", uid),
                                o("currentNodes", o(
                                        "$not", o(
                                                "$elemMatch", o(
                                                        "uids", uid
                                                )
                                        )
                                ))
                            );

                        case "over":
                            return a(
                                    "logs.uid", uid
                            );
                    }
                    return undefined;
                })
        );


        int count = (int) col.countDocuments(match.toBson());
        List list = (List) col.aggregate(a(
                o("$match", match),
                o("$project", o(
                        "_id", o("$toString", "$_id"),
                        "id", 1,
                        "attrs", 1,
                        "pubUid", 1,
                        "pubUName", 1,
                        "createTime", 1,
                        "bpmName", 1,
                        "lastModifyTime", 1,
                        "currentNodes", 1,
                        "bpmModel.listFields", 1,
                        "state", 1
                )),
                o("$sort", o("lastModifyTime", -1)),
                o("$skip", (page - 1) * size),
                o("$limit", size)
        ).toBson()).into(a())
                .stream()
                .map(e -> {
                    Document doc = (Document) e;

                    Obj obj = o();
                    obj.putAll(doc);
                    obj.putAll((Map) doc.get("attrs"));
                    obj.remove("attrs");
                    BpmService bpmService = BpmService.ofIns(doc);
                    obj.put("deal", bpmService.canDealCurrent(uid));
                    obj.put("edit", bpmService.canEdit(uid));
                    obj.put("forceEnd", bpmService.canForceEnd(uid));
                    obj.put("forceResume", bpmService.canForceResume(uid));
                    obj.put("pause", bpmService.canPause(uid));
                    obj.put("resume", bpmService.canResume(uid));
                    obj.put("urge", bpmService.canUrge(uid));
                    obj.put("del", bpmService.canDelete(uid));

                    String names = bpmService.ins.currentNodes.stream()
                            .filter(ee -> ee.unames != null)
                            .flatMap(ee -> ee.unames.stream())
                            .collect(Collectors.joining(","));
                    obj.put("uName", names);

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


    // 获取表单字段
    public Object getFields(String id) {
        MongoCollection<Document> collection = db.getCollection("workflow");
        Arr workflows = collection.aggregate(a(
                o("$match", o("_id", new ObjectId(id))),
                o(
                    "$project", o(
                            "_id", o(
                                    "$toString", "$_id"
                            ),
                            "arrangementData.listFields", 1
                    )
                )
        ).toBson()).into(a());

        return Result.ok(workflows);
    }
    /***********************************/

}
