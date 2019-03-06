//package com.beeasy.hzback.modules.system.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.beeasy.hzback.entity.*;
//import com.beeasy.hzback.view.GPC;
//import com.beeasy.mscommon.RestException;
//import org.beetl.sql.core.SQLManager;
//import org.beetl.sql.ext.SnowflakeIDWorker;
//import org.osgl.$;
//import org.osgl.util.C;
//import org.osgl.util.S;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//import java.util.*;
//import java.util.regex.Pattern;
//
//import static java.util.stream.Collectors.joining;
//import static java.util.stream.Collectors.toList;
//
//@Service
//@Transactional
//public class WorkflowService2 {
//
//    @Autowired
//    SQLManager sqlManager;
//    @Autowired
//    NoticeService2 noticeService;
//    @Autowired
//    SnowflakeIDWorker snowflakeIDWorker;
//
//    private static ScriptEngine JsEngine = new ScriptEngineManager().getEngineByName("javascript");
//    private static Pattern RegexpGo = Pattern.compile("\\s*go\\([\'\"](.+?)[\'\"]\\)\\s*");
//
//
//
//
//
//
//
//
////    public
//
//    public JSONArray getFlow(WfModel wfModel){
//        JSONObject model = JSON.parseObject(wfModel.getModel());
//        JSONArray flow = model.getJSONArray("flow");
//        return flow;
//    }
//
//    public JSONObject fetchNode(WfModel wfModel, String name){
////        final String dename = URLDecoder.decode(name);
//        JSONObject model = JSON.parseObject(wfModel.getModel());
//        JSONArray flow = model.getJSONArray("flow");
//        JSONObject node =  (JSONObject) flow.stream()
//            .filter(i -> {
//                JSONObject object = (JSONObject) i;
//                return S.eq(object.getString("name"), name);
//            })
//            .findFirst()
//            .orElse(null);
//        List<String> names = flow.stream()
//            .map(i -> {
//                return ((JSONObject) i).getString("name");
//            })
//            .filter(S::notBlank)
//            .collect(toList());
//        JSONObject result = new JSONObject();
//        result.put("node", node);
//        result.put("names", names);
//        return result;
//    }
//
//
//    /**
//     * @param uid use uid to judge if this workflow information can be viewed
//     * @param id  instance id
//     * @return
//     */
////    public JSONObject fetchInstance(long uid, long id) {
////        //fetch main body
////        JSONObject instance = sqlManager.selectSingle("workflow.selectInsById", C.newMap("iid", id, "uid", uid), JSONObject.class);
////
////        //fetch all innates
////        List<WfInsAttr> innates = sqlManager.lambdaQuery(WfInsAttr.class)
////            .andEq(WfInsAttr::getInstanceId, id)
////            .andEq(WfInsAttr::getType, WfInsAttr.Type.INNATE)
////            .select();
////        //fetch all dealers
////        List<WfNodeDealer> dealers = sqlManager.select("workflow.selectWorkflowDealers", WfNodeDealer.class, C.newMap("iid", id));
////
////        //fetch all attributes
////        JSONObject attrObj = new JSONObject();
////        List<WfNodeAttr> attrs = sqlManager.select("workflow.selectWorkflowNodeAttributes", WfNodeAttr.class, C.newMap("iid", id));
////        for (WfNodeAttr attr : attrs) {
////            if (!attrObj.containsKey(attr.getNodeId() + "")) {
////                attrObj.put(attr.getNodeId() + "", new JSONArray());
////            }
////            JSONArray arr = attrObj.getJSONArray(attr.getNodeId() + "");
////            arr.add(attr);
////        }
////        //fetch all files
////        JSONObject fileObj = new JSONObject();
////        List<Long> fids = dealers.stream()
////            .map(item -> "")//todo: (String) item.get("files"))
////            .map(files -> files == null ? "" : files)
////            .flatMap(files -> Arrays.stream(files.split(",")))
////            .filter(S::isNotBlank)
////            .map(Long::parseLong)
////            .collect(toList());
////        if (fids.size() > 0) {
////            List<SystemFile> files = sqlManager.lambdaQuery(SystemFile.class).andIn(SystemFile::getId, fids).select();
////            for (SystemFile file : files) {
////                fileObj.put(file.getId() + "", file);
////            }
////        }
////        return instance;
//////        return new JSONObject() {{
//////            put("instance", instance);
//////            put("innates", innates);
//////            put("dealers", dealers);
//////            put("attrs", attrObj);
//////            put("files", fileObj);
//////        }};
////    }
//
//
////    public JSONObject fetchToDeal(long uid, long id){
////        JSONObject ins = sqlManager.selectSingle("workflow.查询任务(验证授权)", C.newMap("uid",uid, "iid", id), JSONObject.class);
////        if(null == ins){
////            return new JSONObject();
////        }
////        //fetch current node
////        ins.put(
////            "currentNode"
////            , sqlManager.lambdaQuery(WfNode.class)
////                .andEq(WfNode::getId, ins.getLong("currentNodeModelId"))
////                .single()
////        );
////
////        //files
////        List<SystemFile> files = C.newList();
////        List<Long> fids = Arrays.stream(ins.getString("files").split(","))
////            .filter(S::isIntOrLong)
////            .map(Long::parseUnsignedLong)
////            .collect(toList());
////        if(C.notEmpty(fids)){
////            files.addAll(
////                sqlManager.lambdaQuery(SystemFile.class)
////                .andIn(SystemFile::getId, fids)
////                .select()
////            );
////        }
////        ins.put("files", files);
////
////        //lock this task
////
////        return ins;
////    }
//
////    public List<GPC> getNodeDealUids(WfIns ins, WfNode node){
////        List<GPC> gpcs = sqlManager.select("workflow.查询节点可处理人", GPC.class, C.newMap("nid", node.getId(), "did", ins.getDepId()))
////            .stream()
////            .filter(item -> {
////                if(!item.getUserType().equals(GP.UserType.QUARTER)){
////                    return true;
////                }
////                int dcount = (int) item.get("dcount");
////                int online = (int) item.get("online");
////                return dcount == 1 || online > 0;
////            })
////            .collect(toList());
////        if(1 == node.getMaxPerson()){
////           List<GPC> newGpcs = gpcs.stream()
////               .filter(item -> Objects.equals(item.getUid(), ins.getDealUserId()))
////               .collect(toList());
////           gpcs = newGpcs;
////        }
////        return gpcs;
////    }
////
////    public List<JSONObject> getNodeDealUids(final long instanceId, final String nodeName) {
////        WfNode node = sqlManager.selectSingle("workflow.selectNodeByInsAndName", C.newMap("iid", instanceId, "name", nodeName), WfNode.class);
////        if (null == node) {
////            return C.newList();
////        }
////        List<JSONObject> objects = sqlManager.select("workflow.selectNodeDealUidsUL", JSONObject.class, C.newMap("nid", node.getId(), "did", node.get("depId"))).stream()
////            .filter(item -> {
////                if (S.neq(GlobalPermission.UserType.QUARTER.name(), item.getString("USER_TYPE"))) {
////                    return true;
////                }
////                int dcount = item.getInteger("DCOUNT");
////                int online = item.getInteger("ONLINE");
////                return dcount == 1 || online > 0;
////            }).collect(toList());
////        if (1 == node.getMaxPerson()) {
////            List newObjects = objects
////                .stream()
////                .filter(item -> ((item.getLong("UID"))).equals(node.get("dealUserId")))
////                .collect(Collectors.toList());
////            if (newObjects.size() > 0) {
////                objects = newObjects;
////            }
////        }
////        return objects;
////    }
//
//
//    public WfIns startNewInstance(Long uid, JSONObject object) {
//        WfIns wfIns = $.map(object).to(WfIns.class);
//        wfIns.setAddTime(new Date());
//        wfIns.setState(WfIns.State.DEALING);
//        User user = null;
//        if(null != uid){
//            user = sqlManager.unique(User.class, uid);
//        }
//
//        //如果任务ID已被占用
//        if(null != wfIns.getId()){
//            if(sqlManager.lambdaQuery(WfIns.class).andEq(WfIns::getId, wfIns.getId()).count() > 0) {
//                throw new RestException("该任务已被创建, 请不要重复提交");
//            }
//        }
//
//        //检索模型
//        WfModel model = null;
//        Long modelId = object.getLong("modelId");
//        String modelName = object.getString("modelName");
//        if(null != modelId){
//            model = sqlManager.lambdaQuery(WfModel.class)
//                .andEq(WfModel::getId, modelId)
//                .andEq(WfModel::getOpen, true)
//                .single();
//        }
//        else if(S.notBlank(modelName)){
//            model = sqlManager.lambdaQuery(WfModel.class)
//                .andEq(WfModel::getModelName, modelName)
//                .andEq(WfModel::getOpen, true)
//                .single();
//        }
//        if (null == model) {
//            throw new RestException("没有找到工作流模型");
//        }
////        wfIns.setModelId(model.getId());
//        wfIns.setModelName(model.getModelName());
//        wfIns.setModel(model.getModel());
////        request.setModelId(model.getId());
////        request.setModelName(model.getName());
//
////        WfIns instance = new WfIns();
//        //检查发布权限
//        //多个授权只取第一个
//        GPC gpc = sqlManager.selectSingle("workflow.查询任务发布权限(通过UID)", C.newMap("mid", model.getId(), "nname", model.getStartNodeName(), "uid", uid), GPC.class);
//        //search which department it belongs
////        GPC gpc = null;
////        if (request.isCommon()) {
////            gpc = sqlManager.selectSingle("workflow.selectWorkflowDepartmentByManager", C.newMap("mid", model.getId(), "uid", uid), GPC.class);
////        } else {
////            gpc = sqlManager.selectSingle("workflow.selectWorkflowDepartmentByUser", C.newMap("mid", model.getId(), "uid", request.getDealerId()), GPC.class);
////        }
//        if (null == gpc) {
//            throw new RestException("你没有权限发布这个任务");
//        }
//        if(gpc.getOtype().equals(Org.Type.QUARTERS)){
//            wfIns.setDepId(gpc.getPid());
//            wfIns.setDepName(gpc.getPname());
//        }
//
////        instance.setDepId(gpc.getOid());
////        instance.setDepName(gpc.getOname());
////        instance.setWorkflowModelId(model.getId());
////        instance.setTitle(request.getTitle());
////        instance.setInfo(request.getInfo());
////        instance.setModelName(model.getModelName());
////        instance.setPubUserId(uid);
////        instance.setAutoCreated(request.getAuthCreated());
//
//        //start date
//        if(null == wfIns.getPlanStartTime()){
//            wfIns.setPlanStartTime(new Date());
//        }
////        Date date = new Date();
////        instance.setAddTime(date);
////        instance.setPlanStartTime(date);
////        if (null != request.getPlanStartTime()) {
////            instance.setPlanStartTime(request.getPlanStartTime());
////        }
//
//        JSONObject innateMap = new JSONObject();
//        innateMap.putAll(object.getJSONObject("data"));
//        innateMap.putAll(object.getJSONObject("startNode"));
//        wfIns.setLoanAccount(innateMap.getString("LOAN_ACCOUNT"));
//        wfIns.setBillNo(innateMap.getString("BILL_NO"));
//
//        JSONObject m = JSON.parseObject(model.getModel());
//        //生成第一个节点
//        WfModel finalModel = model;
//        JSONObject node = getNodeModel(m, model.getStartNodeName());
//        if(null == node){
//            throw new RestException("找不到初始节点, 请联系管理员");
//        }
//        //创建节点列表
//        JSONArray arr = new JSONArray();
//        JSONObject startNode = createNodeIns(node);
//        //创建初始节点
//        arr.add(startNode);
//        wfIns.setCurrentNodeInstanceId(startNode.getLong("id"));
//        wfIns.setCurrentNodeName(model.getStartNodeName());
//        wfIns.setNodes(JSON.toJSONString(arr));
//        sqlManager.insert(wfIns, true);
//
//        //固有属性
//        List<WfInsAttr> attrs = m.getJSONArray("innates").stream()
//            .map(o -> {
//                JSONObject innate = (JSONObject) o;
//                return createAttribute(wfIns.getId(), null, uid, WfInsAttr.Type.INNATE, innate.getString("ename"), innateMap.getString(innate.getString("ename")), innate.getString("cname"));
//            })
////            .filter(item -> S.notEmpty(item.getAttrValue()))
//            .collect(toList());
//        sqlManager.insertBatch(WfInsAttr.class, attrs);
//
//        List<WfNodeDealer> dealers = C.newList();
//        //公共任务
//        if(Objects.equals(object.getBoolean("common"), true)) {
//
//        }
//        //指派
//        else if(!Objects.equals(object.getLong("dealerId"), uid)){
//
//        }
//        //自己执行
//        else{
//            //写入第一个节点属性
////            WfNodeDealer dl = (new WfNodeDealer(null, WfNodeDealer.Type.DID_DEAL, wfIns.getId(), startNode.getLong("id"), uid, user.getUsername(), user.getTrueName(), gpc.getOid(), gpc.getOname(), new Date()));
////            sqlManager.insert(dl,true);
////            //提交数据
////            submitData(uid, wfIns.getId(), new JSONObject(){{
////                put("data", innateMap);
////            }});
//        }
//
//        //写入处理人
////        sqlManager.insert(wfIns,true);
//        return wfIns;
//
//        //save dealer
////        List<WfNodeDealer> dealers = C.newList();
////        if (request.isCommon()) {
////            instance.setState(WfIns.State.COMMON);
////            //
////        } else {
////            if (Objects.equals(request.getDealerId(), uid)) {
////                instance.setState(WfIns.State.DEALING);
////            } else {
////                instance.setState(WfIns.State.PAUSE);
////                instance.setSecondState(WfIns.SecondState.POINT);
////            }
////            instance.setDealUserId(gpc.getUid());
//////            instance.setDealUserName(gpc.getUtname());
//////            dealers.add(createDealer(null, WfNodeDealer.Type.DID_DEAL, GP.UserType.QUARTER,gpc.getUid(),gpc.getUname(),gpc.getUtname(), instance.getDepId(), instance.getDepName(), gpc.getQid(), gpc.getQname(), null, null));
////        }
////        //other fields
////        JSONObject innateMap = new JSONObject();
////        innateMap.putAll(request.getData());
////        innateMap.putAll(request.getStartNode());
////        instance.setBillNo(innateMap.getString("BILL_NO"));
////        instance.setLoanAccount(innateMap.getString("LOAN_ACCOUNT"));
//////        instance.setCusName(innateMap.getString("CUS_NAME"));
////        sqlManager.insert(instance, true);
////
////        //create a point transaction
////        if (!request.isCommon() && !Objects.equals(request.getDealerId(), uid)) {
////            createTransaction(instance.getId(), request.getDealerId(), uid, WfIns.State.UNRECEIVED, WfIns.State.DEALING);
////        }
////
////        //innates
////        List<WfInsAttr> insAttrs = Optional.ofNullable((JSONArray) JSON.parse(model.getInnates()))
////            .orElse(new JSONArray())
////            .stream()
////            .map(o -> {
////                JSONObject obj = (JSONObject) o;
////                String value = innateMap.getString(obj.getString("ename"));
////                return createAttribute(instance.getId(), uid, WfInsAttr.Type.INNATE, obj.getString("ename"), value, obj.getString("cname"));
////            })
////            .collect(toList());
////        sqlManager.insertBatch(WfInsAttr.class, insAttrs);
////
////        //write first node data
////        WfNode fNode = sqlManager.lambdaQuery(WfNode.class)
////            .andEq(WfNode::getModelId, instance.getWorkflowModelId())
////            .andEq(WfNode::getStart, true)
////            .single();
////        WfNodeIns nodeIns = createNodeIns(instance, fNode, true);
////        sqlManager.updateById(instance);
////
////        //write dealers
////        for (WfNodeDealer dealer : dealers) {
////            dealer.setNodeInstanceId(nodeIns.getId());
////        }
////        sqlManager.insertBatch(WfNodeDealer.class, dealers);
////
////        //submit data
////        if (instance.getState().equals(WfIns.State.DEALING)) {
////            SubmitDataRequest submitDataRequest = new SubmitDataRequest();
////            submitDataRequest.setData(innateMap);
////            submitDataRequest.setIns(instance);
////            submitDataRequest.setNode(fNode);
////            submitDataRequest.setNodeIns(nodeIns);
////            submitData(uid, submitDataRequest);
////
////            if(request.isGoNext()){
////                goNext(uid,nodeIns.getId(), instance, nodeIns, fNode);
////            }
////        }
//
//
////        sqlManager.updateById(instance);
////        return instance;
//    }
//
//    public void submitData(long uid, long insId, JSONObject object) {
////        if (null == wfIns) {
//            WfIns wfIns = sqlManager.unique(WfIns.class, insId);
////        }
////        if(null == model){
//            JSONObject model = JSON.parseObject(wfIns.getModel());
////        }
//
//        JSONArray nodes = JSON.parseArray(wfIns.getNodes());
//        sqlManager.lock(WfIns.class, wfIns.getId());
////        WfNode node = null;
////        WfNodeIns nodeIns = null;
////        if (null != request.getIns()) {
////            ins = request.getIns();
////        }
////        if (null != request.getNodeIns()) {
////            nodeIns = request.getNodeIns();
////        }
////        if(null != request.getNode()){
////            node = request.getNode();
////        }
////        if (null == nodeIns) {
////            nodeIns = sqlManager.unique(WfNodeIns.class, request.getNodeId());
////        }
////        if (null == ins) {
////            ins = (WfIns) nodeIns.get("ins");
////        }
////        if(null == node){
////            node = (WfNode) nodeIns.get("node");
////        }
//        if (null == wfIns
//            || (!wfIns.getState().equals(WfIns.State.DEALING))
////            || !Objects.equals(ins.getCurrentNodeInstanceId(), nodeIns.getId())
//        ) {
//            throw new RestException("找不到符合条件的任务");
//        }
//         if(!canDealNode(uid, wfIns.getCurrentNodeInstanceId())){
//             throw new RestException("你没有权限提交数据");
//         }
//
//        long now = sqlManager.lambdaQuery(WfNodeDealer.class)
//            .andIn(WfNodeDealer::getType, C.list(WfNodeDealer.Type.DID_DEAL, WfNodeDealer.Type.OVER_DEAL))
//            .andEq(WfNodeDealer::getNodeId, wfIns.getCurrentNodeInstanceId())
//            .andNotEq(WfNodeDealer::getUid, uid)
//            .groupBy(WfNodeDealer::getUid)
//            .count();
//
//        JSONObject node = getNodeModel(model, wfIns.getCurrentNodeName());
//        JSONObject nIns = getNodeIns(nodes, wfIns.getCurrentNodeInstanceId());
//        if(null == node || null == nIns){
//            throw new RestException(String.format("%s节点配置异常", wfIns.getCurrentNodeName()));
//        }
//        Integer count = node.getInteger("count");
//        if(null == count){
//            count = 1;
//        }
//        if(now + 1 > count){
//            throw new RestException("该节点已经超过最大处理人数");
//        }
//        JSONObject data = object.getJSONObject("data");
//        List<WfInsAttr> attrs = C.newList();
//        switch (node.getString("type")){
//            case "input":
//                for (Object content : node.getJSONArray("content")) {
//                    JSONObject v = (JSONObject) content;
//                    String attrKey = v.getString("ename");
//                    if(!data.containsKey(attrKey)){
//                        continue;
//                    }
//                    String value = data.getString(attrKey);
//                    if(S.notBlank(value)){
//                        //valid
//                    }
//                    attrs.add(
//                        createAttribute(wfIns.getId(), nIns.getLong("id"), uid, WfInsAttr.Type.NODE, attrKey, data.getString(attrKey), v.getString("cname"))
//                    );
//                }
//                break;
//
//            case "check":
////                String optionKey = (String) nodeConfig.getOrDefault("key","key");
////                String optionPs = (String) nodeConfig.getOrDefault("ps","ps");
////                String option = request.getData().getString(optionKey);
////                String ps = request.getData().getString(optionPs);
////                if(nodeConfig.getJSONArray("states").stream()
////                    .map(o -> (JSONObject)o)
////                    .noneMatch(obj -> S.eq(obj.getString("item"), option))) {
////                    throw new RestException("选项内没有这个选项");
////                }
////                attrs.add(createNodeAttribute(uid, nodeIns.getId(), optionKey, option, nodeConfig.getString("question")));
////                attrs.add(createNodeAttribute(uid,nodeIns.getId(),optionPs,ps,"审核说明"));
////                break;
//        }
//        //save node attribute
//        sqlManager.lambdaQuery(WfInsAttr.class)
//            .andEq(WfInsAttr::getInsId, wfIns.getId())
//            .andEq(WfInsAttr::getNodeId, nIns.getLong("id"))
//            .andEq(WfInsAttr::getUid, uid)
//            .delete();
//        sqlManager.insertBatch(WfInsAttr.class, attrs);
////        if(C.notEmpty(attrs)){
////            //save ins attribute
////            WfIns finalIns = ins;
////            List<String> keys = C.newList();
////            List<WfInsAttr> insAttrs = attrs.stream().map(attr -> {
////                WfInsAttr insAttr = createAttribute(finalIns.getId(),uid, WfInsAttr.Type.NODE,attr.getAttrKey(),attr.getAttrValue(),attr.getAttrCname());
////                keys.add(insAttr.getAttrKey());
////                return insAttr;
////            }).collect(toList());
////            sqlManager.lambdaQuery(WfInsAttr.class)
////                .andEq(WfInsAttr::getInstanceId, ins.getId())
////                .andEq(WfInsAttr::getType, WfInsAttr.Type.NODE)
////                .andEq(WfInsAttr::getUid, uid)
////                .andIn(WfInsAttr::getAttrKey, keys)
////                .delete();
////            sqlManager.insertBatch(WfInsAttr.class, insAttrs);
////        }
//
//        //dealers
//        List<WfNodeDealer> dls = sqlManager.lambdaQuery(WfNodeDealer.class)
//            .andEq(WfNodeDealer::getInsId, wfIns.getId())
//            .andEq(WfNodeDealer::getNodeId, nIns.getLong("id"))
//            .andEq(WfNodeDealer::getType, WfNodeDealer.Type.CAN_DEAL)
//            .select();
//        List<Long> noticeUids = C.newList();
//        for (WfNodeDealer dl : dls) {
//             if(Objects.equals(dl.getUid(), uid)){
//                 dl.setType(WfNodeDealer.Type.DID_DEAL);
//                 dl.setLastModify(new Date());
//             }
//             else if(now + 1 >= count){
//                 dl.setType(WfNodeDealer.Type.NOT_DEAL);
//                 dl.setLastModify(new Date());
//                 noticeUids.add(dl.getUid());
//             }
//             else{
//                 //pass
//             }
//        }
//        //update dls
//        sqlManager.updateByIdBatch(dls);
//
//        //save upload files
////        if(C.notEmpty(request.getFiles())){
////            String files = request.getFiles().stream()
////                .distinct()
////                .map(fid -> fid + "")
////                .sorted()
////                .collect(Collectors.joining(","));
////            nodeIns.setFiles(files);
////        }
//
////        sqlManager.updateById(nodeIns);
//
//        //send notice
//        List<SysNotice> notices = C.newList();
//        notices.addAll(noticeService.makeNotice(SysNotice.Type.WORKFLOW, noticeUids, String.format("你的任务 %s 节点 %s 已被别人处理", wfIns.getTitle(), wfIns.getCurrentNodeName()), C.newMap(
//            "taskId", wfIns.getId(),
//            "taskName", wfIns.getTitle(),
//            "nodeId", nIns.getLong("id"),
//            "nodeName", nIns.getString("name")
//        )));
//        noticeService.saveNotices(notices);
//    }
//
//
//
////    public void goNext(long uid, Long niid, WfIns ins, WfNodeIns nodeIns, WfNode node){
////        //flag to judge if goes next
////        boolean flagContinue = true;
////
////        if(null == nodeIns){
////            nodeIns = sqlManager.unique(WfNodeIns.class, niid);
////            ins = (WfIns) nodeIns.get("ins");
////            node = (WfNode) nodeIns.get("node");
////        }
////        if(!canDealNode(uid,nodeIns.getId())){
////            throw new RestException("没有权限处理这个任务");
////        }
////
////        String nextNodeName = null;
////
////        //fetch all attributes
////        //include this node and instance
////        List<WfNodeAttr> attrs = sqlManager.select("workflow.selectNodeAttr", WfNodeAttr.class, C.newMap("uid",uid,"nid",nodeIns.getId(), "iid", ins.getId()));
////        Map<String,String> attrMap = C.newMap();
////        for (WfNodeAttr attr : attrs) {
////            attrMap.put(attr.getAttrKey(), attr.getAttrValue());
////        }
////
////        //check required fields
////        JSONObject nodeConfig = JSON.parseObject(node.getNode());
////
////        switch (node.getType()){
////            case input:
////                for (Map.Entry<String, Object> entry : nodeConfig.getJSONObject("content").entrySet()) {
////                    JSONObject obj = (JSONObject) entry.getValue();
////                    Boolean required = (Boolean) obj.getOrDefault("required",false);
////                    if(required && S.blank(attrMap.get(obj.getString("ename")))){
////                        throw new RestException(S.fmt("%s字段没有填写", obj.getString("cname")));
////                    }
////                }
////
////                //it only effects here
////                if(nodeConfig.containsKey("goNext")){
////                    String attrsJSON = JSON.toJSONString(attrMap);
////                    for (Object goNext : nodeConfig.getJSONArray("goNext")) {
////                        JSONObject next = (JSONObject) goNext;
////                        String ca = next.getString("case");
////                        try {
////                            //做成环境
////                            StringBuilder sb = new StringBuilder();
////                            sb.append(S.fmt("(function(){ var kv = %s; ", attrsJSON));
////                            sb.append(S.fmt("with(kv){ return %s; }", ca));
////                            sb.append("})()");
////                            //处理表达式
////                            boolean flag = (boolean) JsEngine.eval(sb.toString());
////                            if (flag) {
////                                nextNodeName = next.getString("go");
////                                break;
////                            }
////                        } catch (Exception e) {
////                            continue;
////                        }
////                    }
////                }
////                else{
////                    try{
////                        nextNodeName = node.getNext().split(",")[0];
////                    }
////                    catch (Exception e){
////                        //pass
////                    }
////                }
////                break;
////
////            case check:
////                if(S.empty(attrMap.get(nodeConfig.getString("key")))){
////                    throw new com.beeasy.mscommon.RestException("你还没有提交信息");
////                }
////                WfNodeIns finalNodeIns = nodeIns;
////                JSONObject state = nodeConfig.getJSONArray("states")
////                    .stream()
////                    .map(obj -> (JSONObject) obj)
////                    .filter(obj -> {
////                        try{
////                            return sqlManager.lambdaQuery(WfNodeAttr.class)
////                                .andEq(WfNodeAttr::getNodeId, finalNodeIns.getId())
////                                .andEq(WfNodeAttr::getAttrKey, nodeConfig.getString("key"))
////                                .andEq(WfNodeAttr::getAttrValue, obj.getString("item"))
////                                .count() >= obj.getInteger("condition");
////                        }
////                        catch (Exception e){
////                            return false;
////                        }
////                    })
////                    .findFirst()
////                    .orElse(null);
////                if($.isNull(state)){
////                    flagContinue = false;
////                }
////                else{
////                    String behavior = state.getString("behavior");
////                    Matcher m = RegexpGo.matcher(behavior);
////                    if(m.find()){
////                        nextNodeName = m.group(1);
////                    }
////                }
////                break;
////        }
////
////
////        //change status to over_deal
////        switchDealType(uid, nodeIns.getId(), WfNodeDealer.Type.OVER_DEAL);
////
////        if(!flagContinue){
////            return;
////        }
////
////        if(S.empty(nextNodeName)){
////            throw new RestException("没有合适的节点跳转");
////        }
////
////        WfNode nextNode = sqlManager.lambdaQuery(WfNode.class)
////            .andEq(WfNode::getModelId, ins.getModelId())
////            .andEq(WfNode::getName, nextNodeName)
////            .single();
////        if(null == nextNode){
////            throw new com.beeasy.mscommon.RestException(S.fmt("无法找到名为%s的节点", nextNodeName));
////        }
////
////        //go next node
////        //change current node to finished
////        nodeIns.setFinished(true);
////        nodeIns.setDealDate(new Date());
////        WfNodeIns newNodeIns = createNodeIns(ins, nextNode, true);
////
////        //run end behavior
////        if(null != nextNode.getNode() && nextNode.getType().equals(WfNode.Type.end)){
////            ins.setFinishedDate(new Date());
////            ins.setState(WfIns.State.FINISHED);
////            //
////
////            //close child tasks
////            closeChildrenTasks(ins.getId());
////        }
////        //input node rollback
////        else if(nextNode.getType().equals(WfNode.Type.input)){
////            if(
////                sqlManager.lambdaQuery(WfNodeIns.class)
////                .andEq(WfNodeIns::getInstanceId, ins.getId())
////                .andEq(WfNodeIns::getNodeName, newNodeIns.getNodeName())
////                .count() >= 2
////            ){
////                WfNodeIns lastNodeIns = sqlManager.lambdaQuery(WfNodeIns.class)
////                    .andEq(WfNodeIns::getInstanceId, ins.getId())
////                    .andEq(WfNodeIns::getNodeName, newNodeIns.getNodeName())
////                    .andLess(WfNodeIns::getAddTime, newNodeIns.getAddTime())
////                    .orderBy("add_time desc")
////                    .single();
////                if($.isNull(lastNodeIns)){
////
////                }
////                else{
////                    //rewrite all attributes
////                    List<WfNodeAttr> ats = sqlManager.lambdaQuery(WfNodeAttr.class)
////                        .andEq(WfNodeAttr::getNodeId, lastNodeIns.getId())
////                        .select();
////                    for (WfNodeAttr at : ats) {
////                        at.setId(null);
////                        at.setNodeId(newNodeIns.getId());
////                    }
////                    sqlManager.insertBatch(WfNodeAttr.class, ats);
////                }
////            }
////        }
////
////        //send notices
////        List<SysNotice> notices = C.newList();
////        switch (node.getType()){
////            case input:
////            case check:
////                List<Long> uids = sqlManager.lambdaQuery(WfNodeAttr.class)
////                    .andEq(WfNodeAttr::getNodeId, nodeIns.getId())
////                    .groupBy(WfNodeAttr::getDealUserId)
////                    .select(WfNodeAttr::getDealUserId)
////                    .stream()
////                    .map(WfNodeAttr::getDealUserId)
////                    .collect(toList());
////                notices.addAll(noticeService.makeNotice(SysNotice.Type.WORKFLOW, uids, S.fmt("你已处理完毕任务 %s 节点 %s", ins.getTitle(), nodeIns.getNodeName()), C.newMap(
////                    "taskId", ins.getId(),
////                    "taskName", ins.getTitle(),
////                    "nodeId", nodeIns.getId(),
////                    "nodeName", nodeIns.getNodeName()
////                )));
////                break;
////
////        }
////
////        //deal with next node
////        switch (nextNode.getType()){
////            case input:
////            case check:
////                List<GPC> gpcs = getNodeDealUids(ins, nextNode);
////                if(nextNode.getStart()){
////                    GPC gpc = sqlManager.lambdaQuery(GPC.class)
////                        .andEq(GPC::getType, GP.Type.WORKFLOW_PUB)
////                        .andEq(GPC::getObjectId, nextNode.getModelId())
////                        .andEq(GPC::getOid, ins.getDepId())
////                        .andEq(GPC::getUid, ins.getDealUserId())
////                        .single();
////                    if($.isNull(gpc)){
////                        throw new RestException("开始节点的处理人没有相应的权限");
////                    }
////                    gpcs.add(gpc);
////                }
////                List<WfNodeDealer> dealers = C.newList();
////                List<Long> toUids = C.newList();
////                for (GPC gpc : gpcs) {
//////                    WfNodeDealer dl = createDealer(
//////                        null
//////                        , Objects.equals(ins.getDealUserId(), gpc.getUid()) ? WfNodeDealer.Type.DID_DEAL : WfNodeDealer.Type.CAN_DEAL
//////                        , gpc.getUserType()
//////                        , gpc.getUid()
//////                        , gpc.getUname()
//////                        , gpc.getUtname()
//////                        , gpc.getOtype()
//////                        , gpc.getOid()
//////                        , gpc.getOname()
//////                    );
//////                    dl.setNodeInstanceId(newNodeIns.getId());
//////                    dealers.add(dl);
////
////                    toUids.add(gpc.getUid());
////                }
////
////                sqlManager.insertBatch(WfNodeDealer.class, dealers);
////                notices.addAll(
////                    noticeService.makeNotice(
////                        SysNotice.Type.WORKFLOW
////                        , toUids
////                        , S.fmt("你有新的任务 %s 节点 %s 可以处理", ins.getTitle(), nextNode.getName())
////                        , C.newMap(
////                            "taskId", ins.getId(),
////                            "taskName", ins.getTitle(),
////                            "nodeId", newNodeIns.getId(),
////                            "nodeName", newNodeIns.getNodeName()
////                        )
////                ));
////                break;
////
////            case end:
////                notices.add(
////                    noticeService.makeNotice(
////                        SysNotice.Type.WORKFLOW
////                        , ins.getDealUserId()
////                        , String.format("你的任务 %s 已经结束", ins.getTitle())
////                        , C.newMap(
////                        "taskId", ins.getId()
////                            ,"taskName", ins.getTitle()
////                        )
////                    )
////                );
////                break;
////        }
////
////        sqlManager.updateById(ins);
////        sqlManager.updateById(nodeIns);
////
////        noticeService.saveNotices(notices);
////    }
//
//
//
//    /******************************/
//
//    private void closeChildrenTasks(long insId){
//        List<Long> ids = sqlManager.select("workflow.查询所有子任务", JSONObject.class, C.newMap("iid", insId))
//            .stream()
//            .map(json -> json.getLong("id"))
//            .collect(toList());
//        if(C.notEmpty(ids)){
////            sqlManager.update()
//            sqlManager.lambdaQuery(WfIns.class)
//                .andIn(WfIns::getId, ids)
//                .updateSelective(C.newMap("state", WfIns.State.CANCELED.name()));
//        }
//    }
//
//    private boolean canDealNode(long uid, long niid) {
//        Map map = sqlManager.selectSingle("workflow.canDealNode", C.newMap("niid", niid, "uid", uid), Map.class);
//        return (int) map.getOrDefault("1", 0) > 0;
//    }
//
//
////    private WfNodeDealer createDealer(
////        Long insId,
////        Long niid,
////        WfNodeDealer.Type type,
////        long uid, String uname, String utname, Org.Type otype, Long oid, String oname) {
////        WfNodeDealer dealer = new WfNodeDealer();
////        dealer.setNodeInstanceId(niid);
////        dealer.setType(type);
////        dealer.setUserType(userType);
////        dealer.setUserId(uid);
////        dealer.setUserName(uname);
////        dealer.setUserTrueName(utname);
////        dealer.setOtype(otype);
////        dealer.setOid(oid);
////        dealer.setOname(oname);
//////        dealer.setDepId(did);
//////        dealer.setDepName(dname);
//////        dealer.setQuartersId(null == qid ? 0 : qid);
//////        dealer.setQuartersName(null == qname ? "" : qname);
//////        dealer.setRoleId(null == rid ? 0 : rid);
//////        dealer.setRoleName(null == rname ? "" : rname);
////        dealer.setLastModify(new Date());
////        if (null != niid) {
////            sqlManager.insert(dealer, true);
////        }
////        return dealer;
////    }
//
//    private JSONObject createNodeIns(JSONObject object){
//        JSONObject nodeIns = new JSONObject();
//        nodeIns.put("id", snowflakeIDWorker.nextId());
//        nodeIns.put("name", object.getString("name"));
//        nodeIns.put("files", new JSONArray());
//        nodeIns.put("addTime", new Date());
//        nodeIns.put("type", object.getString("type"));
//        return nodeIns;
//    }
//
////    private WfNodeIns createNodeIns(WfIns ins, WfNode node, boolean setcurrent) {
////        WfNodeIns nodeIns = new WfNodeIns();
////        nodeIns.setInstanceId(ins.getId());
////        nodeIns.setNodeModelId(node.getId());
////        nodeIns.setNodeName(node.getName());
////        nodeIns.setAddTime(new Date());
////        sqlManager.insert(nodeIns, true);
////        if (setcurrent) {
////            ins.setCurrentNodeName(node.getName());
////            ins.setCurrentNodeInstanceId(nodeIns.getId());
////            ins.setCurrentNodeModelId(node.getId());
////        }
////        return nodeIns;
////    }
//
//    private WorkflowTransaction createTransaction(long instanceId, long userId, long managerId, WfIns.State from, WfIns.State to) {
//        WorkflowTransaction transaction = new WorkflowTransaction();
//        transaction.setInstanceId(instanceId);
//        transaction.setState(WorkflowTransaction.State.DEALING);
//        transaction.setUserId(userId);
//        transaction.setManagerId(managerId);
//        transaction.setFromState(from);
//        transaction.setToState(to);
//        sqlManager.insert(transaction, true);
//        return transaction;
//    }
//
//    private WfInsAttr createAttribute(long instanceId, Long nInsId, long uid, WfInsAttr.Type type, String key, String value, String cname) {
//        WfInsAttr attr = new WfInsAttr();
//        attr.setUid(uid);
//        attr.setAttrKey(key);
//        attr.setAttrValue(value);
//        attr.setAttrCname(cname);
//        attr.setType(type);
//        attr.setInsId(instanceId);
//        attr.setNodeId(nInsId);
//        return attr;
//    }
//
////    private WfNodeAttr createNodeAttribute(long uid, long niid, String key, String value, String cname){
////        WfNodeAttr attr = new WfNodeAttr();
////        attr.setDealUserId(uid);
////        attr.setNodeId(niid);
////        attr.setAttrKey(key);
////        attr.setAttrValue(null == value ? "" : value);
////        attr.setAttrCname(cname);
////        return attr;
////    }
//
//    private void switchDealType(long uid, long nid, WfNodeDealer.Type type){
//        sqlManager.lambdaQuery(WfNodeDealer.class)
//            .andEq(WfNodeDealer::getUid, uid)
//            .andEq(WfNodeDealer::getInsId, nid)
//            .andEq(WfNodeDealer::getNodeId, nid)
//            .updateSelective(C.newMap(
//                "type",  type
//            ));
//    }
//
//    private JSONObject getNodeModel(JSONObject model, String name){
//        JSONObject node = model.getJSONArray("flow").stream()
//            .map(o -> (JSONObject)o)
//            .filter(o -> S.eq(o.getString("name"), name))
//            .findFirst()
//            .orElse(null);
//        return node;
//    }
//
//    private JSONObject getNodeIns(JSONArray nodes, long id){
//        return nodes.stream()
//            .map(o -> (JSONObject)o)
//            .filter(item -> Objects.equals(item.getLong("id"), id))
//            .findFirst()
//            .orElse(null);
//    }
//
//}
