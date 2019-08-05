package com.beeasy.hzbpm.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzbpm.bean.JsEngine;
import com.beeasy.hzbpm.bean.Notice;
import com.beeasy.hzbpm.entity.BpmInstance;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzbpm.entity.BpmModel;
import com.beeasy.hzbpm.entity.FormEntity;
import com.beeasy.hzbpm.exception.BpmException;
import com.beeasy.hzbpm.filter.Auth;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.beetl.sql.core.SQLReady;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.beeasy.hzbpm.bean.MongoService.db;
import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;
import static com.github.llyb120.nami.server.Vars.$request;

public class BpmService {

    //BpmModel
    private BpmModel model = null;
    private String modelId;
    private String xml;

    private FormEntity formEntity = null;
    private String formId;

    private Document arrangementData = null;
    private Document bpmData = null;

    //BpmInstance
    public BpmInstance ins = null;

    private Map<String,Obj> userCache = new HashMap<>();

    //    public long uid;
    private BpmService() {
    }


    public static BpmService ofModel(final String modelId) {
        MongoCollection<Document> col = db.getCollection("workflow");
//        col.mapReduce("function(){return 1}", "function(){return 2}");
        Document data = col.aggregate(
                a(
                        o("$match", o("_id", new ObjectId(modelId))),
                        o("$project", o("bpmData", 1, "xml", 1,"formId", 1, "arrangementData",1))
                ).toBson()
        ).first();
        if (data == null) {
            error("查询不到当前的流程模型");
        }
        BpmService bpmService = new BpmService();
        bpmService.modelId = modelId;


        bpmService.bpmData = (Document) data.get("bpmData");
        bpmService.arrangementData = (Document) data.get("arrangementData");
        bpmService.model = Json.cast(data.get("arrangementData"), BpmModel.class);
        bpmService.xml = data.getString("xml");
        bpmService.formId = data.getObjectId("formId").toHexString();
        bpmService.model = arrangementData(bpmService.bpmData, bpmService.model, bpmService.formId,bpmService.xml,bpmService.modelId);
        return bpmService;
    }

    public static BpmModel arrangementData(Document data, BpmModel arrData, String formId, String xml, String modelId){
        BpmService bpmService = new BpmService();
        Document doc = getForm(formId);
        bpmService.formEntity = Json.cast(doc.get("form"), FormEntity.class);
        System.out.println(bpmService.formEntity.data);

        BpmModel bpmModel = new BpmModel();
        bpmModel.template = bpmService.formEntity.template;
        bpmModel.rendered = bpmService.formEntity.parse;
        bpmModel.formId = new ObjectId(formId);
//        bpmService.model.workflowName = (String) data.get("workflowName");

        Map<String, Map> fieldMap = new HashMap<>();
//        for(Map list : bpmService.formEntity.data){
//            fieldMap.put((String) list.get(list),list);
//
//        }
        for(int i = 0;i < bpmService.formEntity.data.size();i++)
        {
            Map<String,Object> map = bpmService.formEntity.data.get(i);
            String title = map.get("title").toString();
            fieldMap.put(title,map);
        }
        bpmModel.fields = fieldMap;

        bpmModel.workflowName = arrData.workflowName;

        bpmModel.listFields = arrData.listFields;

        bpmModel.nodes = arrData.nodes;
        bpmModel.start = arrData.start;
        bpmModel.end = arrData.end;

        bpmService.model = bpmModel;

//        Obj update = o();
//        update.put("arrangementData",bpmService.model);

        //配置节点，超时提醒时间，最大超时提醒时间/ 文本框，        select,小时，天


//        System.out.println(xml.replaceAll("\\\\\"","\""));
//        int pos = xml.indexOf(">");
//        xml = xml.substring(pos + 1);
//        org.w3c.dom.Document doc2 = XmlUtil.readXML(xml);
//        NodeList nodeList = doc2.getElementsByTagName("process");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Node item = nodeList.item(i);
//            NamedNodeMap attrs = item.getAttributes();
//            Obj obj = o();
//            for (int j = 0; j < attrs.getLength(); j++) {
//                Node attrItem = attrs.item(j);
//                obj.put(attrItem.getNodeName(), attrItem.getNodeValue());
//            }
//        }


        System.out.println(bpmService.model);
        return bpmService.model;

    }

    private static Document getForm(String formId){
        Document doc = db.getCollection("form").find(new BasicDBObject("_id", new ObjectId(formId))).first();
        doc.put("_id", doc.getObjectId("_id").toString());
        return doc;
    }

    public static BpmService ofIns(Document data) {
        BpmService bpmService = new BpmService();
//        bpmService.arrangementData = (Document) data.get("bpmModel");
        bpmService.ins = Json.cast(data, BpmInstance.class);
        bpmService.model = bpmService.ins.bpmModel;
        if(bpmService.ins.bpmId != null){
            bpmService.modelId = bpmService.ins.bpmId.toString();
        }
        bpmService.xml = bpmService.ins.xml;
        return bpmService;
    }

    public static BpmService ofIns(String insId) {
        if (StrUtil.isBlank(insId)) {
            error("流程ID不能为空");
        }
        MongoCollection<Document> col = db.getCollection("bpmInstance");
//        col.mapReduce("function(){return 1}", "function(){return 2}");
        Document data = col.aggregate(
                a(
                        o("$match", o("_id", new ObjectId(insId)))
                ).toBson()
        ).first();
        if (data == null) {
            error("无法找到ID为%s的流程", insId);
        }
        return ofIns(data);
    }

    public boolean forceResume(String uid){
        if(!canForceResume(uid)){
            error("无权强制恢复任务");
        }
        MongoCollection<Document> col = db.getCollection("bpmInstance");
        UpdateResult result = col.updateOne(
                Filters.eq("_id", ins._id),
                o(
                        "$set", o(
                                "state", "流转中"
                        )
                ).toBson()
        );
        return result.getModifiedCount() > 0;
    }

    public boolean forceEnd(String uid){
        if(!canForceEnd(uid)){
            error("无权强制结束任务");
        }
        MongoCollection<Document> col = db.getCollection("bpmInstance");
        UpdateResult result = col.updateOne(
                Filters.eq("_id", ins._id),
                o(
                        "$set", o(
                                "state", "强制结束"
                        )
                ).toBson()
        );
        return result.getModifiedCount() > 0;
    }

    public boolean resume(String uid){
        if(!canResume(uid)){
            error("无权恢复任务");
        }
        MongoCollection<Document> col = db.getCollection("bpmInstance");
        UpdateResult result = col.updateOne(
                Filters.eq("_id", ins._id),
                o(
                        "$set", o(
                                "state", "流转中"
                        )
                ).toBson()
        );
        return result.getModifiedCount() > 0;
    }

    public boolean pause(String uid){
        if(!canPause(uid)){
            error("无权暂停任务");
        }
        MongoCollection<Document> col = db.getCollection("bpmInstance");
        UpdateResult result = col.updateOne(
                Filters.eq("_id", ins._id),
                o(
                        "$set", o(
                                "state", "已暂停"
                        )
                ).toBson()
        );
        return result.getModifiedCount() > 0;
    }

    public void urge(String uid, String msg){
        if(!canUrge(uid)){
            error("无权催办");
        }
        for (BpmInstance.CurrentNode currentNode : ins.currentNodes) {
            for (String s : currentNode.uids) {
                Notice.sendSystem(s, "来自流程 %s 的催办消息: %s", ins._id.toString(), msg);
            }
        }
    }

    public boolean delete(String uid){
        if(!canDelete(uid)){
            error("无权删除");
        }
        MongoCollection<Document> collection = db.getCollection("bpmInstance");
        DeleteResult result = collection.deleteOne(Filters.eq("_id", ins._id));
        return result.getDeletedCount() > 0;
    }

//    public static BpmService ofIns(String id, Obj data, String uid){
////
//
//
//        return bpmService;
//    }

    /**
     * 检查一个用户是否可以发布该流程
     *
     * @param uid
     * @return
     */
    public boolean canPub(String uid) {
        return canDeal(uid, "start");
//        BpmModel.Node node = getNode("start");
//        List<Obj> list = sqlManager.execute(new SQLReady("select uid,oid,pid from t_org_user where uid = ?", uid), Obj.class);
//        return list.stream().anyMatch(e -> {
//            return (node.qids).contains(e.s("oid")) || (node.rids).contains(e.s("oid")) || (node.dids).contains(e.s("pid")) || (node.uids).contains(e.s("uid"));
//        });
    }

    /**
     * 是否可以处理某些节点
     * 使用model上的字段
     *
     * @param uid
     * @param nodeIds
     * @return
     */
    public boolean canDeal(String uid, String... nodeIds) {
//        if (!ins.state.equalsIgnoreCase("流转中")) {
//            return false;
//        }
        List<Obj> list = sqlManager.execute(new SQLReady("select uid,oid,pid from t_org_user where uid = ?", uid), Obj.class);
        for (String nodeId : nodeIds) {
            BpmModel.Node node = getNode(nodeId);
            if (list.stream().anyMatch(e -> {
                return (node.qids).contains(e.s("oid")) || (node.rids).contains(e.s("oid")) || (node.dids).contains(e.s("pid")) || (node.uids).contains(e.s("uid"));
            })) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否可以处理当前节点
     *
     * @param uid
     * @return
     */
    public boolean canDealCurrent(final String uid) {
        return isRunning() && ins.currentNodes.stream().anyMatch(e -> e.uids.contains(uid));
    }

    /**
     * 某个用户是否可以查看当前的这个流程
     *
     * @return
     */
    public boolean canSee(String uid) {
        return ins.logs.stream()
                .anyMatch(e -> e.uid.equals(uid)) ||
                ins.currentNodes.stream()
                .anyMatch(e -> e.uids.contains(uid));
    }

    /**
     * 是否可以催办当前任务
     * @param uid
     * @return
     */
    public boolean canUrge(String uid){
        //发起者或者管理员
        return isRunning() && (uid.equals(ins.pubUid) || isSu(uid));
    }

    /**
     * 是否可以编辑这个流程
     * @param uid
     * @return
     */
    public boolean canEdit(String uid){
        return !isFinished() && isSu(uid);
    }

    /**
     * 是否可以挂起任务
     * @param uid
     * @return
     */
    public boolean canPause(String uid){
        return isRunning() && isSu(uid);
    }

    /**
     * 是否可以恢复被挂起的任务
     * @param uid
     * @return
     */
    public boolean canResume(String uid){
        return ins.state.equalsIgnoreCase("已暂停") && isSu(uid);
    }

    /**
     * 是否可以强制结束任务
     * @param uid
     * @return
     */
    public boolean canForceEnd(String uid){
        return isRunning() && isSu(uid);
    }

    public boolean canForceResume(String uid){
        return ins.state.equalsIgnoreCase("强制结束") && isSu(uid);
    }

    /**
     * 是否可以删除任务
     * @param uid
     * @return
     */
    public boolean canDelete(String uid){
        return isSu(uid);
    }

    /**
     * 将任务更新到最新的状态
     *
     * @return
     */
    public void sync() {
        BpmService service = ofIns(ins._id.toString());
        model = service.model;
        modelId = service.modelId;
        ins = service.ins;
        arrangementData = service.arrangementData;
        xml = service.xml;
    }

    /**
     * 发布前数据整理
     *
     * @param uid
     * @return
     */
    public Obj preparePub(String uid) {
        if (!canPub(uid)) {
            error("你没有权限发布该任务");
        }
        BpmModel.Node node = getNode("start");
        Obj attrs = o();
        addMacroFields(uid, node, attrs);
        return o(
                "allFields", model.fields,
                "formFields", node.allFields,
                "requiredFields", node.requiredFields,
                "form", model.template,
                "attrs", attrs,
                "xml", xml,
                "current", a(model.start)
        );
    }


    /**
     * 查询当前流程的详细信息
     *
     * @param uid
     * @return
     */
    public Obj getInsInfo(String uid) {
        if (!canSee(uid)) {
            error("无权查看这个流程");
        }
        BpmModel.Node currentNode = getCurrentNode(uid);
        //是否有权限查看

        Obj ret = o(
                "allFields", model.fields,
                "formFields", (null != currentNode) ? currentNode.allFields : a(),
                "requiredFields", (null != currentNode) ? currentNode.requiredFields : a(),
                "form", model.template,
                "attrs", ins.attrs,
                "xml", xml,
                "current", null != currentNode ? a(currentNode.id) : getCurrentNodeIds(),
                "deal", (null != currentNode && canDeal(uid, currentNode.id)) ? true : false
        );
        return ret;
    }

    /**
     * 校验所填写的属性是否合法
     *
     * @param uid
     * @param node
     * @param attrs
     */
    public void validateAttrs(String uid, BpmModel.Node node, Obj attrs) {
        //过滤不该我填的属性

        //补充宏字段
//        addMacroFields(uid, node, attrs);
        //验证必填字段
        for (String requiredField : node.requiredFields) {
            Map<String, String> field = model.fields.get(requiredField);
            if (field == null) {
                continue;
            }
            if (StrUtil.isBlank(attrs.s(requiredField))) {
                error("字段[%s]不能为空", requiredField);
            }
        }
    }

    /**
     * 补充宏控件指向的字段
     *
     * @param uid
     * @param node
     * @param attrs
     */
    private void addMacroFields(String uid, BpmModel.Node node, Obj attrs) {
        for (String allField : node.allFields) {
            Map<String, String> field = model.fields.get(allField);
            if (field == null) {
                continue;
            }
            if ("macros".equals(field.get("leipiplugins"))) {
                attrs.put(field.get("title"), calMacro(uid, field));
            }
        }
    }


    public Object calMacro(String uid, Map<String, String> node) {
        String expression = node.get("expression");
        if (StrUtil.isBlank(expression)) {
            switch (node.get("orgtype")) {
                case "当前用户姓名":
                    return getUserName(uid);
                case "当前用户ID":
                    return uid;

                case "当前用户部门":
                    return sqlManager.execute(new SQLReady("select pname from t_org_user where uid = ? and otype <> 'ROLE'", uid), Obj.class)
                            .stream()
                            .map(e -> e.s("pname"))
                            .collect(Collectors.joining(","));

                case "当前日期+时间":
                    return DateUtil.format(new Date(), "yyyy-MM-dd hh:mm:ss");
                case "当前日期":
                    return DateUtil.format(new Date(), "yyyy-MM-dd");
                case "当前年份":
                    return DateUtil.format(new Date(), "yyyy");
                case "当前时间":
                    return DateUtil.format(new Date(), "hh:mm");
                case "当前星期":
                    LocalDate date = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E");
                    return date.format(formatter);

            }
            return null;
        } else {
            return null;
        }
    }


    /**
     * @param uid
     * @param data
     * @return
     */
    public Document createBpmInstance(String uid, Obj data) {
        Map<String, Object> attrs = new HashMap<>();
        Map<String, Object> allAttrs = new HashMap<>();
        BpmService bpmService = this;
//        BpmService bpmService = BpmService.ofModel(modelId);

        if (!canPub(uid)) {
            error("用户没有权限发布任务");
        }

        BpmModel.Node startNode = getNode("start");
        addMacroFields(uid, startNode, data);
        validateAttrs(uid, startNode, data);

        // 开始节点
//        String startNodeId = bpmService.model.start;
//        List<String> qids = bpmService.model.nodes.get(startNodeId).qids;
//        List<String> rids = bpmService.model.nodes.get(startNodeId).rids;
//        List<String> dids = bpmService.model.nodes.get(startNodeId).dids;
//
//        String qid = qids.stream().map(q -> "'" + q + "'").collect(Collectors.joining(","));
//        String rid = rids.stream().map(r -> "'" + r + "'").collect(Collectors.joining(","));
//        String did = dids.stream().map(d -> "'" + d + "'").collect(Collectors.joining(","));
//
//
//        List<Obj> list = sqlManager.execute(new SQLReady(String.format("select uid,utname,pid,pname from t_org_user where (oid in (%s) or oid in (%s) or pid in (%s)) and uid='%s'", qids.isEmpty() ? "-1" : qid, rids.isEmpty() ? "-1" : rid, dids.isEmpty() ? "-1" : did, uid)), Obj.class);
//        List<Obj> list2 = sqlManager.execute(new SQLReady(String.format("select uid,utname,pid,pname from t_org_user where uid='%s'", uid)), Obj.class);
//        list.addAll(list2);

//        List<Long> ql = bpmService.model.nodes.get(startNode).qids;

        Map<String, Map> fields = bpmService.model.fields;
        for(Map.Entry<String, Map> field : fields.entrySet()){
            allAttrs.put(field.getKey(),"");
        }

        List<String> allFields = startNode.allFields;
        for (String all : allFields) {
            allAttrs.put(all, data.get(all));
            attrs.put(all, data.get(all));
        }
//        String deptName = "";
//        long deptId = 0L;
        String uName = getUserName(uid);
//        if (list.size() > 0) {
//            for (Obj li : list) {
//                if (null != li.get("pid")) {
//                    deptId = (long) li.get("pid");
//                    deptName = (String) li.get("pname");
//                    uName = (String) li.get("utname");
//                    break;
//                }
//            }
//        }

        JSONObject dataLog = new JSONObject();
        dataLog.put("id", new ObjectId());
        dataLog.put("nodeId", startNode.id);
        dataLog.put("msg", startNode.name);
        dataLog.put("time", new Date());
        dataLog.put("uid", uid);
        dataLog.put("attrs", attrs);
        dataLog.put("uname", uName);
        JSONArray logs = new JSONArray();
        logs.add(dataLog);

        JSONObject currentNode = new JSONObject();
        currentNode.put("nodeId", startNode.id);
        JSONArray uids = new JSONArray();
        uids.add(uid);
        currentNode.put("uids", uids);
        JSONArray currentNodes = new JSONArray();
        currentNodes.add(currentNode);

        MongoCollection<Document> collection = db.getCollection("bpmInstance");
//        BpmInstance ins = new BpmInstance();
        Obj obj = new Obj();
        obj.put("state", "流转中");
        obj.put("bpmId", new ObjectId(modelId));
        obj.put("bpmName", bpmService.model.workflowName);
        obj.put("pubUid", uid);
        obj.put("pubUName", uName);
//        obj.put("depId", deptId);
//        obj.put("depName", deptName);
        obj.put("bpmModel", bpmService.model);
        obj.put("currentNodes", currentNodes);
        obj.put("attrs", allAttrs);
        obj.put("logs", logs);
        obj.put("xml", xml);
        obj.put("createTime", new Date());
        obj.put("lastModifyTime", new Date());

        Document doc = obj.toBson();
        collection.insertOne(doc);
        return doc;
//        return Json.cast(doc, BpmInstance.class);
    }

    /**
     * 通过当前提交的属性查询下一个应该移交的节点
     *
     * @param uid   提交属性的人，必须是当前节点的经办人
     * @param attrs 提交到该任务上的属性
     * @return
     */
    public BpmModel.Node getNextNode(String uid, Obj attrs) {
        //查找所有的属性
        Obj oldAttrs = new Obj(ins.attrs);
        oldAttrs.putAll(attrs);
        //当前处理的节点
        BpmInstance.CurrentNode currentNode = ins.currentNodes.stream()
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
            if(JsEngine.runExpression(oldAttrs, nextNode.expression)){
                target = getNode(nextNode.node);
                break;
            }
//            if (StrUtil.isBlank(nextNode.expression)) {
//                target = getNode(nextNode.node);
//                break;
//            } else if (runExpression(nextNode.expression)) {
//                target = getNode(nextNode.node);
//                break;
//            }
        }
        if (target == null) {
            error("找不到符合跳转条件的下一节点");
        }
        return target;
    }

    /**
     * 查询下一个节点的可处理人，以本部门的为最优先
     *
     * @param uid
     * @param attrs
     * @return
     */
    public List<Obj> getNextNodePersons(String uid, Obj attrs) {
        BpmModel.Node target = getNextNode(uid, attrs);
        //查询这个节点所有命中的人
        return sqlManager.select("workflow.查找节点处理人员", Obj.class, o(
                "uid", uid,
                "uids", target.uids.isEmpty() ? a(-1) : target.uids,
                "qids", target.qids.isEmpty() ? a(-1) : target.qids,
                "rids", target.rids.isEmpty() ? a(-1) : target.rids,
                "dids", target.dids.isEmpty() ? a(-1) : target.dids
        ));
    }

    /**
     * 保存节点数据
     * @param data
     */
    public boolean saveIns(String uid, Obj data, boolean validate, String mode){
        //candeal
        //编辑模式，只验证是否有编辑权限
        if(mode.equals("edit")){
            if(!canEdit(uid)){
                error("没有权限编辑该任务");
            }
        }
        else if (!canDealCurrent(uid)) {
            error("用户没有权限处理任务");
        }

        addMacroFields(uid, getCurrentNode(uid), data);
        if(validate){
            validateAttrs(uid, getCurrentNode(uid), data);
        }

        BpmService bpmService = this;
        String nodeId = bpmService.ins.currentNodes.get(0).nodeId;
        BpmModel.Node node = getNode(nodeId);

        Map<String , Object> attrs = new HashMap<>();
        List<String> allFields = null;
        if(mode.equalsIgnoreCase("edit")){
            //编辑模式下，开放所有字段
            allFields = new ArrayList<>();
            for (Map.Entry<String, Map> entry : ins.bpmModel.fields.entrySet()) {
                allFields.add(entry.getKey());
            }
        } else {
            //一般模式下， 只处理允许处理的字段
            allFields = bpmService.ins.bpmModel.nodes.get(nodeId).allFields;
        }
        for (String all : allFields) {
            attrs.put(all, data.get(all));
        }

        BpmInstance.DataLog dataLog = new BpmInstance.DataLog();
        dataLog.id = new ObjectId();
        dataLog.nodeId = nodeId;
        dataLog.time = new Date();
        dataLog.uid = uid;
        dataLog.attrs = attrs;
        dataLog.uname = getUserName(uid);
        if(mode.equalsIgnoreCase("edit")){
            dataLog.msg = "编辑流程";
        } else {
            if(StrUtil.isNotBlank(node.name)) {
                dataLog.msg = String.format("提交【%s】", node.name);
            }
        }

//        bpmService.ins.logs.add(dataLog);
//        bpmService.ins.lastModifyTime = new Date();

        Obj update = o();
//        if (nextPersonId != null) {
//            nextApprover(uid, nextPersonId, update);
//        }
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            update.put("attrs." + entry.getKey(), entry.getValue());
        }
        Obj set = o(
//                "$set", update,
//                "$set", o(
//                        "lastModifyTime",new Date()
//                ),
                "$push",o(
                        "logs", dataLog
                ),
                "$set", update
        );

        //更新数据库
        MongoCollection<Document> collection = db.getCollection("bpmInstance");
        UpdateResult res = collection.updateOne(Filters.eq("_id", bpmService.ins._id),set.toBson());
        return res.getModifiedCount() > 0;

    }

    /**
     * 提交节点信息
     *
     * @param uid
     * @param data
     */
    public Object submitIns(String uid, Obj data) {
        Object bl;
//        if (!canPub(uid)) {
//            error("用户没有权限发布任务");
//        }

        BpmService bpmService = this;
        String nodeId = bpmService.ins.currentNodes.get(0).nodeId;

        bl = saveIns(uid, data, true, "deal");

        BpmModel.Node nextNode = getNextNode(uid, data);

        // 判断是否为结束节点
        if(nextNode.id.equals(bpmService.ins.bpmModel.end)){
            Obj state = o();
            state.put("state", "已办结");
//            state.put("currentNodes", a(uid, nextNode.id));
            MongoCollection<Document> collection = db.getCollection("bpmInstance");
            UpdateResult res = collection.updateOne(Filters.eq("_id", bpmService.ins._id),new Document("$set", state.toBson()));
//            bl = res.getModifiedCount()>0;
            if(res.getModifiedCount()>0){
                bl = "提交成功，该流程已结束！";
            }

        }
//        if(bpmService.ins.bpmModel.end == bpmService.ins.currentNodes.get(0).nodeId){
//            bl = "该流程已完结！";
//        }
        return bl;
    }

    /**
     * 保存选取的下一步处理人
     * @param uid 提交人
     * @param nextUid 下一步处理人
     */
    public boolean nextApprover(String uid, String nextUid, Obj update){
        BpmService bpmService = this;

        // 下一节点
        BpmModel.Node nextNode = getNextNode(uid, o());
        if(!canDeal(nextUid, nextNode.id)){
            error("此处理人无权限处理任务！");
        }

        BpmInstance.CurrentNode currentNode = new BpmInstance.CurrentNode();
        currentNode.nodeId = nextNode.id;
        List<String> uids = new ArrayList<>();
        uids.add(nextUid);
        currentNode.uids = uids;
        List<String> unames = new ArrayList<>();
        String uName = getUserName(nextUid);
        unames.add(uName);
        currentNode.unames = unames;


        // 得到下一节点超时提醒配置信息
        BpmModel.TimeoutSet timeoutSet = bpmService.ins.bpmModel.nodes.get(nextNode.id).timeoutSet;
        LocalDateTime nowTime = LocalDateTime.now();
        if(null != timeoutSet){
            LocalDateTime timeout = dateTime(timeoutSet.timeout, nowTime);
            LocalDateTime maxTimeout = dateTime(timeoutSet.maxTimeout, timeout);

            Date nowDate = toDate(nowTime);
            currentNode.nowTime = nowDate;
            currentNode.timeout = toDate(timeout);
            currentNode.maxTimeout = toDate(maxTimeout);
        }


        update.put("currentNodes", a(currentNode));

        MongoCollection<Document> collection = db.getCollection("bpmInstance");

        UpdateResult res = collection.updateOne(Filters.eq("_id", bpmService.ins._id),new Document("$set", update.toBson()));

        return res.getModifiedCount() > 0;
    }


    private LocalDateTime dateTime(String dateTime, LocalDateTime nowDateTime){
        if(null == dateTime){
            return null;
        }
        String [] dateArr = dateTime.split("_");
        if(dateArr.length!=2){
            return null;
        }
        if(dateArr[1].equals("day")){
            nowDateTime = nowDateTime.plusDays(Long.parseLong(dateArr[0]));

        }else if(dateArr[1].equals("hours")){
            nowDateTime = nowDateTime.plusHours(Long.parseLong(dateArr[0]));
        }

        return nowDateTime;
    }

    /**
     * 日期格式转换
     * @param localDateTime
     * @return
     */
    private Date toDate(LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);

        Date date = Date.from(zdt.toInstant());
        return date;
    }


    private static void error(String errMessage, Object... objects) {
        throw new BpmException(String.format(errMessage, objects));
    }


    private synchronized String getUserName(String uid){
        Obj obj = initUserCache(uid);
        if (!obj.containsKey("true_name")) {
            String trueName = sqlManager.execute(new SQLReady("select true_name from t_user where id = ?", uid), Obj.class)
                    .stream()
                    .map(e -> e.s("true_name"))
                    .findFirst()
                    .orElse(null);
            obj.put("true_name", trueName);
        }
        return obj.s("true_name");
    }

    private synchronized boolean isSu(String uid){
        Obj obj = initUserCache(uid);
        if(!obj.containsKey("is_su")){
            Boolean su = sqlManager.execute(new SQLReady("select su from t_user where id = ?", uid), Obj.class)
                    .stream()
                    .map(e -> e.b("su"))
                    .findFirst()
                    .orElse(null);
            obj.put("is_su", su != null && su.equals(true));
        }
        return obj.b("is_su");
    }

    private synchronized Obj initUserCache(String uid){
        Obj obj = userCache.get(uid);
        if (obj == null) {
            obj = o();
            userCache.put(uid, obj);
        }
        return obj;
    }


    /**
     * 通过节点ID查询节点
     *
     * @param nodeId start表示开始，end表示结束，其余情况使用ID查询
     * @return
     */
    public BpmModel.Node getNode(String nodeId) {
        if (nodeId.equals("start")) {
            return model.nodes.get(model.start);
        } else if (nodeId.equals("end")) {
            return model.nodes.get(model.end);
        } else {
            return model.nodes.get(nodeId);
        }
    }


    public boolean isRunning(){
        return  ins.state.equalsIgnoreCase("流转中");
    }

    public boolean isFinished(){
        return ins != null && ("已办结".equalsIgnoreCase(ins.state) || "强制结束".equalsIgnoreCase(ins.state));
    }

    /**
     * 得到某个用户当前的节点
     *
     * @param uid
     * @return
     */
    public BpmModel.Node getCurrentNode(String uid) {
        if(isFinished()){
            return getNode("end");
        }
        String nodeId = ins.currentNodes.stream()
                .filter(e -> e.uids.contains(uid))
                .map(e -> e.nodeId)
                .findFirst()
                .orElse(null);
        if (nodeId == null) {
            return null;
        }
        return getNode(nodeId);
    }

    public List<String> getCurrentNodeIds() {
        if(isFinished()){
            return (List)a("end");
        }
        return ins.currentNodes
                .stream()
                .map(e -> e.nodeId)
                .collect(Collectors.toList());
    }

    public List<BpmModel.Node> getCurrentNodes() {
        if(isFinished()){
            return (List)a(getNode("end"));
        }
        return ins.currentNodes
                .stream()
                .map(e -> getNode(e.nodeId))
                .filter(e -> e != null)
                .collect(Collectors.toList());
    }

}
