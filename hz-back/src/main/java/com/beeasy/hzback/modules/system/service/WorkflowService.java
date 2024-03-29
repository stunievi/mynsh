//package com.beeasy.hzback.modules.system.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.beeasy.common.entity.*;
//import com.beeasy.mscommon.RestException;
//import com.beeasy.mscommon.Result;
//import com.beeasy.hzback.core.helper.Utils;
//import com.beeasy.hzback.core.util.SqlUtils;
//import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
//import com.beeasy.hzback.modules.system.dao.*;
//import com.beeasy.hzback.modules.system.form.UnreceivedWorksSearchRequest;
//import com.beeasy.hzback.modules.system.form.WorkflowModelAdd;
//import com.beeasy.hzback.modules.system.form.WorkflowModelEdit;
//import com.beeasy.hzback.modules.system.request.ApplyTaskRequest;
//import com.beeasy.hzback.modules.system.request.WorkflowPositionAddRequest;
//import com.beeasy.hzback.modules.system.response.FetchWorkflowInstanceResponse;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.math.NumberUtils;
//import org.beetl.sql.core.SQLManager;
//import org.osgl.$;
//import org.osgl.util.C;
//import org.osgl.util.S;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.persistence.EntityManager;
//import javax.persistence.criteria.*;
//import javax.script.*;
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//import static com.beeasy.common.entity.WorkflowInstance.State.*;
//import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;
//
////import com.beeasy.hzback.modules.system.request.SubmitDataRequest;
////import org.apache.camel.util.TimeUtils;
//
////import com.beeasy.hzback.core.helper.Rest;
//
//@Slf4j
//@Service
//@Transactional
//public class WorkflowService {
//
//    @Autowired
//    IInfoCollectLinkDao linkDao;
//    @Autowired
//    DataSearchService searchService;
//    @Autowired
//    IWorkflowNodeInstanceDealerDao nodeInstanceDealersDao;
//    @Autowired
//    IWorkflowModelFieldDao fieldDao;
//    @Autowired
//    IDownloadFileTokenDao downloadFileTokenDao;
//    @Autowired
//    SqlUtils sqlUtils;
//    @Autowired
//    IWorkflowInstanceTransactionDao transactionDao;
//    @Autowired
//    IGlobalPermissionDao globalPermissionDao;
//    @Autowired
//    IWorkflowInstanceAttributeDao instanceAttributeDao;
//    @Autowired
//    IWorkflowModelInnateDao innateDao;
//    @Autowired
//    EntityManager entityManager;
//    @Autowired
//    IGPSPositionDao gpsPositionDao;
//    @Autowired
//    IWorkflowNodeFileDao nodeFileDao;
//    @Autowired
//    ISystemFileDao systemFileDao;
//    //    @Autowired
////    IWorkflowExtPermissionDao extPermissionDao;
//    @Autowired
//    Utils utils;
//    @Autowired
//    ScriptEngine engine;
//
//    @Autowired
//    IWorkflowModelDao modelDao;
//
//    @Autowired
//    IWorkflowInstanceDao instanceDao;
//
//    @Autowired
//    IWorkflowNodeInstanceDao nodeInstanceDao;
//
//    @Autowired
//    IWorkflowNodeDao nodeDao;
//    @Autowired
//    NoticeService2 noticeService;
//
////    @Autowired
////    IWorkflowModelPersonsDao personsDao;
//
//    @Autowired
//    ITaskCheckLogDao taskCheckLogDao;
//
//    @Autowired
//    IWorkflowNodeAttributeDao attributeDao;
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    SystemConfigCache cache;
//
//    @Autowired
//    SQLManager sqlManager;
//    @Autowired
//    WorkflowService2 workflowService2;
//
////    @Autowired
////    ISystemTaskDao systemTaskDao;
////    @Autowired
////    IInspectTaskDao inspectTaskDao;
//
//    @Autowired
//    ScriptEngine scriptEngine;
//    @Autowired
//    ISystemTextLogDao systemTextLogDao;
//    @Autowired
//    SystemTextLogService logService;
////    @Autowired
////    WorkflowModelStart.Dao startDao;
////    @Autowired
////    WorkflowModelStart.QuartersDao quartersDao;
//
//    @Autowired
//    RedisTemplate<String, Integer> redisTemplate;
//
//    private static ScriptEngine JsEngine = new ScriptEngineManager().getEngineByName("javascript");
//    private static Pattern ExpressionPattern = Pattern.compile("");
//
//    public static String LOCK_INS_KEY = "workflow:ins:lock:%d";
//
//    /**
//     * start an auto-check task which is only being called by SuperUser
//     *
//     * @param uid
//     * @param request
//     * @return
//     */
//    public Result autoStartTask(final long uid, final ApplyTaskRequest request) {
////        if (!userService.isSu(uid)) {
////            return Result.error("没有发布该任务的权限");
////        }
////        String modelName = searchService.getAutoTaskModelName(request.getDataId());
//        if (StringUtils.isEmpty(request.getModelName())) {
//            return Result.error("找不到任务模型");
//        }
//        //查找执行人
////        long duid = searchService.getAutoTaskDealer(request.getDataId());
////        if (duid == 0) {
////            return Result.error("没有找到对应的客户经理");
////        }
////        request.setDealerId(duid);
////        request.setManual(false);
//        request.setTitle("贷后跟踪任务 " + new SimpleDateFormat("yyyy-MM-dd").format(request.getPlanStartTime()));
//        return startNewInstance(uid, request);
//    }
//
//
//    /**
//     * start an new instance using a named model
//     *
//     * @param uid
//     * @return
//     */
//    public Result startNewInstance(final long uid, ApplyTaskRequest request) {
//        System.out.println(System.currentTimeMillis());
//        if(true){
////            return Result.ok(workflowService2.startNewInstance(uid,request));
//        }
//        System.out.println(System.currentTimeMillis());
//        User pubUser = userService.findUser(uid);
//        WorkflowModel workflowModel = null;
//        WorkflowInstance parentInstance = null;
//        if (null == pubUser) {
//            return Result.error("任务发布人ID错误");
//        }
//        //如果使用了ID, 那么以ID为准
//        if (null != request.getModelId()) {
//            workflowModel = findModel(request.getModelId()).orElse(null);
//        }
//        //如果使用了模型名, 那么自行查找一个合法的模型
//        else if (!StringUtils.isEmpty(request.getModelName())) {
//            workflowModel = modelDao.findTopByModelNameAndOpenIsTrue(request.getModelName()).orElse(null);
//        }
//        if (null == workflowModel || !workflowModel.isOpen()) {
//            return Result.error("工作流没有开启");
//        }
//        request.setModelId(workflowModel.getId());
//        request.setModelName(workflowModel.getModelName());
//        //fix
////        updateWorkflowModelDeps(workflowModel.getId());
//
//
//        //校验任务特殊权限
//        //同一时间只能同时存在一个不良资产登记流程
//        switch (workflowModel.getModelName()) {
//            case "不良资产登记":
//            case "不良资产管理流程":
//                if (StringUtils.isEmpty(request.getDataId())) {
//                    return Result.error("没有传台账编号");
//                }
//                //如果已经有一个相同的流程在运行
//                if (instanceDao.countByModelNameAndLoanAccountAndStateNotIn(workflowModel.getModelName(), request.getDataId(), Arrays.asList(WorkflowInstance.State.CANCELED, WorkflowInstance.State.FINISHED)) > 0) {
//                    return Result.error("该台账已经有一个正在运行的任务");
//                }
//                break;
//
//            case "诉讼":
//            case "利息减免":
//            case "抵债资产接收":
//            case "资产处置":
//            case "催收":
//                if (StringUtils.isEmpty(request.getDataId())) {
//                    return Result.error("没有传台账编号");
//                }
//                //检查是否有相同的不良资产管理任务
//                parentInstance = instanceDao.findTopByModelNameAndLoanAccountAndStateNotIn("不良资产管理流程", request.getDataId(), Arrays.asList(WorkflowInstance.State.CANCELED, WorkflowInstance.State.FINISHED)).orElse(null);
//                if (null == parentInstance) {
//                    return Result.error("没有找到符合条件的不良资产管理流程");
//                }
//                break;
//        }
//
//        //计算任务归属部门
//        //命中岗位权限的直属上级
//        List<GlobalPermission> globalPermissions = globalPermissionDao.findAllByTypeInAndObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), request.getModelId());
//        Department department = null;
//        //公共任务的情况下, 直接确认自己所在的部门
//        if (request.isCommon()) {
//            department = globalPermissions.stream()
//                    .filter(item -> item.getUserType().equals(GlobalPermission.UserType.QUARTER))
//                    .map(item -> userService.findQuarters(item.getLinkId()))
//                    .filter(item -> null != item)
//                    .map(Quarters::getDepartment)
//                    .filter(dep -> {
//                        return pubUser.getQuarters()
//                                .stream().anyMatch(q -> q.getDepartmentId().equals(dep.getId()) && q.isManager());
//                    })
//                    .findFirst()
//                    .orElse(null);
//        } else {
//            department = globalPermissions.stream()
//                    .filter(item -> item.getUserType().equals(GlobalPermission.UserType.QUARTER))
//                    .filter(item -> userService.hasQuarters(request.getDealerId(), item.getLinkId()))
//                    .findFirst()
//                    .map(item -> userService.findQuarters(item.getLinkId()))
//                    .map(Quarters::getDepartment)
//                    .orElse(null);
//        }
//        if (null == department) {
//            return Result.error("无法确定该任务所属的部门");
//        }
//
//        User dealerUser = null;
//        Map<String, String> innateMap = new HashMap<>();
//
//        //任务主体
//        WorkflowInstance workflowInstance = new WorkflowInstance();
//        workflowInstance.setDepId(department.getId());
//        workflowInstance.setDepName(department.getName());
//        //绑定父任务的情况
//        if (null != parentInstance) {
//            workflowInstance.setParentId(parentInstance.getId());
//            workflowInstance.setParentInstance(parentInstance);
//            workflowInstance.setParentTitle(parentInstance.getTitle());
//            workflowInstance.setParentModelName(parentInstance.getModelName());
//        }
//
//        //公共任务
//        if (request.isCommon()) {
//            if (!canPoint(workflowModel.getId(), uid)) {
//                return Result.error("你无权发布公共任务");
//            }
//            workflowInstance.setDealUserId(null);
//        }
//        //不是公共任务的情况下,指定任务执行人
//        else {
//            dealerUser = userService.findUser(request.getDealerId());
//            //如果是给自己执行
//            if (dealerUser.getId().equals(uid)) {
//                if (!canPub(workflowModel.getId(), uid)) {
//                    return Result.error("你无权发布该任务");
//                }
//            }
//            //指派给别人执行
//            else {
//                if (!canPoint(workflowModel.getId(), uid)) {
//                    return Result.error("你无权发布该任务");
//                }
//            }
//
//            workflowInstance.setDealUserId(dealerUser.getId());
//            workflowInstance.setDealUserName(dealerUser.getTrueName());
//            //验证执行人是否有权限
//            //TODO:
//        }
//
//
//        WorkflowNode firstNode = findFirstNodeModel(workflowModel.getId()).orElse(null);
//        //非公共任务
//        //检查执行人是否拥有第一个节点的处理权限
////        if (!request.isCommon()) {
////            if (!checkAuth(workflowModel, firstNode, dealerUser)) {
////                return Result.error("选择的执行人没有权限处理该任务");
////            }
////        }
//
//
//        workflowInstance.setModelId(workflowModel.getId());
//        workflowInstance.setWorkflowModel(workflowModel);
//        workflowInstance.setTitle(request.getTitle());
//        workflowInstance.setInfo(request.getInfo());
//        workflowInstance.setModelName(workflowModel.getModelName());
//        workflowInstance.setAutoCreated(!request.getAuthCreated());
//
//        //开始时间
//        Date date = new Date();
//        workflowInstance.setAddTime(date);
//        workflowInstance.setPlanStartTime(date);
//        //如果设定了预任务时间
//        if (null != request.getPlanStartTime()) {
//            workflowInstance.setPlanStartTime(request.getPlanStartTime());
//        }
////        workflowInstance.setDealUser(dealerUser);
////        workflowInstance.setDealUserId(null == dealerUser ? null : dealerUser.getId());
//        workflowInstance.setPubUserId(uid);
//
//        workflowInstance = saveWorkflowInstance(workflowInstance);
//
//
//        //固有字段检查
//        List<WorkflowModelInnate> innates = workflowModel.getInnates();
//
//        //插入固有字段
//        //根据datasource和dataid整理固有字段
//        innateMap.putAll(request.getData());
//        innateMap.putAll(request.getStartNode());
//
//        //固有字段复写
//        for (WorkflowModelInnate innate : innates) {
//            if (innateMap.containsKey(innate.getContent().getString("ename"))) {
//                WorkflowInstanceAttribute attribute = addAttribute(
//                        workflowInstance,
//                        WorkflowInstanceAttribute.Type.INNATE,
//                        innate.getFieldName(),
//                        innateMap.getOrDefault(innate.getFieldName(), ""),
//                        innate.getContent().getString("cname"));
//                instanceAttributeDao.save(attribute);
//            }
//        }
//
//
//        if (request.isCommon()) {
//            //公共任务
//            workflowInstance.setState(COMMON);
//        } else {
//            if (dealerUser.getId().equals(uid)) {
//                //任务默认处于执行中的状态
//                workflowInstance.setState(DEALING);
//            } else {
//                workflowInstance.setState(WorkflowInstance.State.PAUSE);
//                workflowInstance.setSecondState(WorkflowInstance.SecondState.POINT);
//                //增加一条指派申请
//                WorkflowInstanceTransaction transcation = new WorkflowInstanceTransaction();
//                transcation.setManager(pubUser);
//                transcation.setFromState(UNRECEIVED);
//                transcation.setToState(DEALING);
//                transcation.setInstanceId(workflowInstance.getId());
//                transcation.setUserId(dealerUser.getId());
//                transactionDao.save(transcation);
//            }
//        }
//        workflowInstance = saveWorkflowInstance(workflowInstance);
//
//        //第一个执行人已经确定
////        workflowInstance.getNodeList().get(0).setDealer(dealerUser);
////        workflowInstance.getNodeList().get(0).setDealerId(null == dealerUser ? null : dealerUser.getId());
//
//        //插入第一个节点
//        WorkflowNodeInstance nodeInstance = addNode(workflowInstance, firstNode, false);
//        nodeInstance.setDealerId(workflowInstance.getDealUserId());
//
//        //写入第一个节点的属性
//        nodeInstance = nodeInstanceDao.save(nodeInstance);
//
//
//        //写入处理人
//        if (null != dealerUser) {
//            WorkflowNodeInstanceDealer dealers = new WorkflowNodeInstanceDealer();
//            dealers.setDepId(workflowInstance.getDepId());
//            dealers.setDepName(workflowInstance.getDepName());
//            dealers.setNodeInstanceId(nodeInstance.getId());
//            dealers.setType(WorkflowNodeInstanceDealer.Type.DID_DEAL);
//            dealers.setUserId(dealerUser.getId());
//            dealers.setUserTrueName(dealerUser.getTrueName());
//            dealers.setUserType(GlobalPermission.UserType.QUARTER);
//            dealers.setLastModify(new Date());
//            nodeInstanceDealersDao.save(dealers);
//        }
//
//        //记录日志
////        logService.addLog(SystemTextLog.Type.WORKFLOW, workflowInstance.getId(), uid, "发布了任务");
////        if (null != dealerUser) {
////            logService.addLog(SystemTextLog.Type.WORKFLOW, workflowInstance.getId(), dealerUser.getId(), "接受了任务");
////        }
//
//        //补充字段
//        String extValue = null;
//        if ((extValue = innateMap.get("BILL_NO")) != null) {
//            workflowInstance.setBillNo(extValue);
//        }
//        if ((extValue = innateMap.get("LOAN_ACCOUNT")) != null) {
//            workflowInstance.setLoanAccount(extValue);
//        }
//        workflowInstance = instanceDao.save(workflowInstance);
//
//        //第一个节点数据复写
//        //检查任务是否可以继续
//        if (workflowInstance.getState().equals(DEALING)) {
//            WorkflowInstance finalWorkflowInstance = workflowInstance;
//            WorkflowNodeInstance finalNodeInstance = nodeInstance;
////            Result submitResult = submitData(uid, new SubmitDataRequest() {{
////                setData((JSONObject) JSON.toJSON(innateMap));
////                setInstanceId(finalWorkflowInstance.getId());
////                setNodeId(finalNodeInstance.getId());
////                setFiles(request.getFiles());
////            }});
////            if (!submitResult.isSuccess()) {
////                throw new RuntimeException(submitResult.getErrMessage());
////            }
//            if (request.isGoNext()) {
//                Result goNextResult = goNext(uid, workflowInstance.getId(), nodeInstance.getId());
//                if (!goNextResult.isSuccess()) {
//                    throw new RuntimeException(goNextResult.getErrMessage());
//                }
//            }
//        }
//
//        //善后工作
//        if (!StringUtils.isEmpty(workflowInstance.getLoanAccount())) {
//            InfoCollectLink infoCollectLink = new InfoCollectLink();
//            infoCollectLink.setLoanAccount(workflowInstance.getLoanAccount());
//            infoCollectLink.setInstanceId(workflowInstance.getId());
//            linkDao.save(infoCollectLink);
//        }
//        return workflowInstance.getId() == null ? Result.error() : Result.ok(workflowInstance);
//    }
//
//    /**
//     * add a new nodeInstance to task
//     *
//     * @param instance
//     * @param node
//     * @param add
//     */
//    public WorkflowNodeInstance addNode(WorkflowInstance instance, WorkflowNode node, boolean add) {
//        WorkflowNodeInstance workflowNodeInstance = new WorkflowNodeInstance();
//        workflowNodeInstance.setNodeModel(node);
//        workflowNodeInstance.setNodeModelId(node.getId());
//        workflowNodeInstance.setNodeName(node.getName());
//        workflowNodeInstance.setType(node.getType());
//        workflowNodeInstance.setInstanceId(instance.getId());
//        workflowNodeInstance.setInstance(instance);
//        workflowNodeInstance.setFinished(false);
//        if (add) {
//            instance.getNodeList().add(workflowNodeInstance);
//        }
//        instance.setCurrentNodeInstance(workflowNodeInstance);
//        instance.setCurrentNodeModelId(node.getId());
//        instance.setCurrentNodeName(node.getName());
//        return workflowNodeInstance;
//    }
//
//    /**
//     * add new attribute to task
//     * notice: it doesn't save anything, save it manually if you really want
//     *
//     * @param instance
//     * @param type
//     * @param key
//     * @param value
//     * @param cname
//     * @return
//     */
//    public WorkflowInstanceAttribute addAttribute(WorkflowInstance instance, WorkflowInstanceAttribute.Type type, String key, String value, String cname) {
//        WorkflowInstanceAttribute attribute = new WorkflowInstanceAttribute();
//        attribute.setInstanceId(instance.getId());
//        attribute.setType(type);
//        attribute.setAttrKey(key);
//        attribute.setAttrValue(value);
//        attribute.setAttrCName(cname);
//        return instanceAttributeDao.save(attribute);
//    }
//
//
//    /**
//     * accept a comon task
//     *
//     * @param uid
//     * @param ids
//     * @return
//     */
//    public Map<Object, Object> acceptInstance(long uid, Collection<Long> ids) {
//        User user = userService.findUser(uid);
//        return ids.stream()
//                .map(id -> {
//                    WorkflowInstance instance = findInstance(id);
//                    if (!canAccept(instance, uid)) {
//                        return new Object[]{id, "用户没有权限接受任务"};
//                    }
//                    //锁定该任务
//                    entityManager.refresh(instance, PESSIMISTIC_WRITE);
//                    instance.setDealUserId(uid);
//                    instance.setDealUserName(user.getTrueName());
//                    instance.setState(DEALING);
//                    entityManager.merge(instance);
//
//                    //绑定第一个节点
//                    WorkflowNodeInstance nodeInstance = instance.getNodeList().get(0);
//                    nodeInstance.setDealerId(uid);
////        nodeInstance.setDealer(user);
//                    nodeInstanceDao.save(nodeInstance);
//
//                    //写入权限
//                    WorkflowNodeInstanceDealer dealer = new WorkflowNodeInstanceDealer();
//                    dealer.setLastModify(new Date());
////                    dealer.set
//
//                    //message
//                    noticeService.addNotice(SysNotice.Type.WORKFLOW, uid, String.format("你已经接受任务 %s", instance.getTitle()), C.newMap("taskId", instance.getId(), "taskName", instance.getTitle()));
//                    //写log
//                    logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "接受了任务");
//                    return new Object[]{id, "接受成功"};
//                })
//                .collect(Collectors.toMap(item -> item[0] + "", item -> item[1]));
//    }
//
//
//    /**
//     * 自己取消任务
//     *
//     * @param uid
//     * @param instanceId
//     * @return
//     */
//    public boolean closeInstance(final long uid, final long instanceId, final boolean hasSuOption) {
//        User user = userService.findUser(uid);
//        WorkflowInstance workflowInstance = findInstance(instanceId);
//        if (null == user) {
//            return false;
//        }
//        //if has su option and user is superuser, the ignore judgement of cancel permission
//        if (hasSuOption && user.getSu()) {
//            instanceDao.deleteById(workflowInstance.getId());
//            return true;
//        } else if (canCancel(workflowInstance, user)) {
//            instanceDao.deleteById(workflowInstance.getId());
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * check if the manager of this task can recall
//     *
//     * @param uid
//     * @param instanceId
//     * @return
//     */
//    public boolean recallInstance(long uid, long instanceId) {
//        User user = userService.findUser(uid);
//        if (null == user) return false;
//        WorkflowInstance workflowInstance = findInstance(instanceId);
//        //撤回任务的限制
//        if (canRecall(workflowInstance, user)) {
//            workflowInstance.setState(WorkflowInstance.State.CANCELED);
//            instanceDao.save(workflowInstance);
//            logService.addLog(SystemTextLog.Type.WORKFLOW, workflowInstance.getId(), uid, "撤回了任务");
//            return true;
//        }
//        return false;
//    }
//
//
//    public boolean lock(long uid, long instanceId) {
//        //if it has already be locked by someone
//        Integer saveUid = redisTemplate.opsForValue().get(S.fmt(LOCK_INS_KEY, instanceId));
//        if (null != saveUid && saveUid != uid) {
//            return false;
//        }
//        //it just affects when the node's dealer count is 1
//        WorkflowInstance ins = findInstance(instanceId);
//        WorkflowNode node = findNode(ins.getCurrentNodeModelId());
//        if (node.getMaxPerson() != 1) {
//            return false;
//        }
//        if (!canDeal(instanceId, uid)) {
//            return false;
//        }
//        redisTemplate.opsForValue().set(S.fmt(LOCK_INS_KEY, instanceId), Math.toIntExact(uid), 10, TimeUnit.MINUTES);
//        return true;
//    }
//
//    public boolean unlock(long uid, long instanceId) {
//        if (!redisTemplate.hasKey(S.fmt(LOCK_INS_KEY, instanceId))) {
//            return false;
//        }
//        Integer saveUid = redisTemplate.opsForValue().get(S.fmt(LOCK_INS_KEY, instanceId));
//        if (uid != saveUid) {
//            return false;
//        }
//        return redisTemplate.delete(S.fmt(LOCK_INS_KEY, instanceId));
//    }
//
//    public User fetchLock(long instanceId) {
//        Integer uid = redisTemplate.opsForValue().get(S.fmt(LOCK_INS_KEY, instanceId));
//        if (null == uid) {
//            return null;
//        }
//        return userService.findUser(uid);
//    }
//
//    /**
//     * check if someone can deal with this task
//     *
//     * @param instance
//     * @param user
//     * @return
//     */
////    private boolean canDeal(final WfIns instance, final User user) {
////        WorkflowNodeInstance nodeInstance = instance.getCurrentNodeInstance();
////        return null != nodeInstance &&
////                instance.getState().equals(WfIns.State.DEALING) &&
////                checkNodeAuth(nodeInstance, user);
////    }
//    private boolean canDeal(long instancId, long uid) {
//        String sql = "select count(ins.id) from t_workflow_instance ins\n" +
//                "left join t_workflow_node_instance_dealer dl on ins.current_node_instance_id = dl.node_instance_id\n" +
//                "where ins.id = ? and dl.user_id = ? and dl.TYPE in ('CAN_DEAL','DID_DEAL') and ins.state = 'DEALING'";
//        List<Map<String, String>> ret = sqlUtils.query(sql, instancId, uid);
//        if (C.empty(ret)) {
//            return false;
//        }
//        return Integer.parseInt(ret.get(0).getOrDefault("1", "0")) > 0;
//    }
//
//
//    /**
//     * check if someone can cancel a useless task
//     * notice: it only affects when the task is not submit to next dealer
//     *
//     * @param instance
//     * @param user
//     * @return
//     */
//    private boolean canCancel(WorkflowInstance instance, User user) {
//        //取消任务的限制
//        //1.这是我自己的任务
//        //2.任务只有一个节点
//        //3.任务也是我发布的
//        //4.任务在进行中
//        return null != instance.getDealUserId() && instance.getDealUserId().equals(user.getId()) &&
//                (null != instance.getPubUserId() && instance.getPubUserId().equals(user.getId())) &&
//                instance.getNodeList().size() <= 1 &&
//                instance.getState().equals(DEALING);
//    }
//
//    /**
//     * 是否可以撤回
//     *
//     * @param instance
//     * @param user
//     * @return
//     */
//    @Deprecated
//    private boolean canRecall(WorkflowInstance instance, User user) {
//        //1.这是我发布的任务
//        return instance.getPubUserId().equals(user.getId())
//                //2.任务只有一个节点
//                && instance.getNodeList().size() <= 1
//                //3.任务在进行中 或者 未被领取
//                && (instance.getState().equals(DEALING) || instance.getState().equals(UNRECEIVED))
//                //4.拥有撤回的权限
//                && canPoint(instance.getModelId(), user.getId());
////        instance.getWorkflowModel().getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
//    }
//
//
//    /**
//     * 是否可以接受任务
//     *
//     * @param instance 任务
//     * @param uid      与用户ID
//     * @return
//     */
//    public boolean canAccept(WorkflowInstance instance, long uid) {
//        return instance.getState().equals(COMMON) && canPub(instance.getModelId(), uid);
//    }
//
//    /**
//     * 是否可以发布这个模型的任务
//     *
//     * @param model
//     * @param user
//     * @return
//     */
//    public boolean canPubOrPoint(WorkflowModel model, User user) {
//        //1.拥有该任务的指派权限
//        return canPoint(model.getId(), user.getId()) || canPub(model, user);
//    }
//
//    public boolean canPubOrPoint(final long modelId, final long uid) {
//        return canPoint(modelId, uid) || canPub(modelId, uid);
//    }
//
//    /**
//     * 单独检查是否可以发布这个任务(只针对执行者而言)
//     *
//     * @param model
//     * @param user
//     * @return
//     */
//    public boolean canPub(WorkflowModel model, User user) {
//        return canPub(model.getId(), user.getId());
//    }
//
//    public boolean canPub(long modelId, long uid) {
//        String sql = "select count(1) from T_GLOBAL_PERMISSION_CENTER where type = 'WORKFLOW_PUB' and object_id = ? and uid = ?";
//        List<Map<String, String>> ret = sqlUtils.query(sql, modelId, uid);
//        return Integer.valueOf(ret.get(0).getOrDefault("1", "0")) > 0;
////        return globalPermissionDao.hasPermission(uid, Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), modelId) > 0;
//    }
//
//
//    /**
//     * 是否可以指派这个模型的任务
//     *
//     * @param modelId 模型ID
//     * @param uid     指派的用户
//     * @return
//     */
//    public boolean canPoint(final long modelId, final long uid) {
//        String sql = "SELECT\n" +
//                "\tcount(1)\n" +
//                "\n" +
//                "FROM\n" +
//                "\tt_global_permission_center gpc , t_department_manager dm \n" +
//                "\t\twhere dm.id = gpc.did AND\n" +
//                "gpc.TYPE = 'WORKFLOW_PUB' and gpc.object_id = ? and dm.uid = ?\n" +
//                "\t\t\n" +
//                "\t\t\n" +
//                "\t\t";
//        List<Map<String, String>> ret = sqlUtils.query(sql, modelId, uid);
//        if (C.empty(ret)) {
//            return false;
//        }
//        return Integer.valueOf(ret.get(0).getOrDefault("1", "0")) > 0;
//
////        WfModel model = findModel(modelId).orElse(null);
////        User user = userService.findUser(uid);
////        return null != model && null != user && canPoint(model, user);
//    }
//
//    public boolean canPoint(final WorkflowModel model, final User user) {
//        return canPoint(model.getId(), user.getId());
////        if (StringUtils.isEmpty(model.getDepIds())) {
////            return false;
////        }
////        try {
////            List<Long> depIds = Utils.convertIdsToList(model.getDepIds());
////            List<Quarters> qs = user.getQuarters();
////            return qs.stream()
////                    .anyMatch(item -> item.isManager() && depIds.contains(item.getDepartmentId()));
////        } catch (Exception e) {
////            return false;
////        }
//    }
//
//    /**
//     * 是否可以指派这个实例
//     *
//     * @param instance
//     * @param user
//     * @return
//     */
//    public boolean canPoint(final WorkflowInstance instance, final User user) {
//        return canPoint(instance.getModelId(), user.getId());
////        return user.getQuarters()
////                .stream()
////                .anyMatch(item -> item.isManager() && null != instance.getDepId() && instance.getDepId().equals(item.getDepartmentId()));
//    }
//
////    public List<User> getManagersFromInstance(final WfIns instance){
////
////    }
//
//    /**
//     * 是否可以移交
//     *
//     * @param instance
//     * @param user
//     * @return
//     */
//    public boolean canTransform(WorkflowInstance instance, User user) {
////        WfNode firstNode = nodeDao.findAllByModelAndStartIsTrue(instance.getWorkflowModel()).get(0);
//        return
//                //1.任务正在进行中
//                instance.getState().equals(DEALING) &&
//                        //2.拥有指派该任务的权利
//                        canPoint(instance, user);
//    }
//
//
//
//    /**
//     * 得到某些用户执行过的任务
//     *
//     * @param uids
//     * @param lessId
//     * @param pageable
//     * @return
//     */
//    public Page<WorkflowInstance> getUserDealedWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
//        if (null == lessId) {
//            lessId = Long.MAX_VALUE;
//        }
//        return null;
////        return instanceDao.findDealedWorks(uids, lessId, pageable);
//    }
//
//    /**
//     * 得到部门未执行的任务
//     *
//     * @param uid      主管用户ID
//     * @param lessId   app加载用
//     * @param pageable 分页
//     * @return
//     */
//    public Page<WorkflowInstance> getDepartmentUndealedWorks(long uid, Long lessId, Pageable pageable) {
//        if (null == lessId) lessId = Long.MAX_VALUE;
//        User user = userService.findUser(uid);
//        if (null == user) {
//            return new PageImpl<>(new ArrayList<>(), pageable, 0);
//        }
//        List<Long> dids = getManagerDepIds(user);
//        return instanceDao.findNeedToDealWorksFromDepartments(dids, DEALING, lessId, pageable);
//    }
//
//
//    /**
//     * 得到部门已执行完毕的任务
//     *
//     * @param uid      主管用户ID
//     * @param lessId   app加载用
//     * @param pageable 分页
//     * @return
//     */
//    public Page<WorkflowInstance> getDepartmentDealedWorks(long uid, Long lessId, Pageable pageable) {
//        if (null == lessId) lessId = Long.MAX_VALUE;
//        User user = userService.findUser(uid);
//        if (null == user) {
//            return new PageImpl<>(new ArrayList<>(), pageable, 0);
//        }
//        List<Long> dids = getManagerDepIds(user);
//        return instanceDao.findNeedToDealWorksFromDepartments(dids, WorkflowInstance.State.FINISHED, lessId, pageable);
//    }
//
//
//    /**
//     * 得到用户可以接受的公共任务列表
//     *
//     * @param uid
//     * @param lessId
//     * @param pageable
//     * @return
//     */
//    public Page<WorkflowInstance> getUserCanAcceptCommonWorks(final long uid, Long lessId, Pageable pageable) {
//        if (null == lessId) lessId = Long.MAX_VALUE;
//        return instanceDao.findCommonWorks(uid, lessId, pageable);
//    }
//
//
//    /**
//     * 得到用户观察的任务列表
//     *
//     * @param uids
//     * @param lessId
//     * @param pageable
//     * @return
//     */
//    public Page<WorkflowInstance> getUserObservedWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
//        if (null == lessId) lessId = Long.MAX_VALUE;
//        return instanceDao.findObserveredWorks(Collections.singleton(GlobalPermission.Type.WORKFLOW_OBSERVER), uids, lessId, pageable);
//    }
//
//
//    /**
//     * 得到用户可以接受的任务
//     *
//     * @param uids
//     * @param lessId
//     * @param pageRequest
//     * @return
//     */
//    public Page<WorkflowInstance> getUserCanAcceptWorks(Collection<Long> uids, Long lessId, Pageable pageRequest) {
//        if (null == lessId) lessId = Long.MAX_VALUE;
//        return instanceDao.findUserCanAcceptWorks(uids, lessId, pageRequest);
//    }
//
//
//    /**
//     * 接受指派/移交
//     *
//     * @param uid
//     * @param taskIds
//     * @return
//     */
//    public Result acceptTask(long uid, Long... taskIds) {
//        User user = userService.findUser(uid);
//        List<Long> successIds = new ArrayList<>();
//        List<WorkflowInstanceTransaction> transactions = transactionDao.findAllByUserIdAndInstanceIdInAndStateIn(uid, Arrays.asList(taskIds), Collections.singleton(WorkflowInstanceTransaction.State.DEALING));
//        for (WorkflowInstanceTransaction transaction : transactions) {
//            transaction.setState(WorkflowInstanceTransaction.State.ACCEPT);
//            transaction.getInstance().setState(transaction.getToState());
//            updateInstanceBelongs(transaction.getInstance(), user);
//
//            //更新事务
//            transactionDao.save(transaction);
//            successIds.add(transaction.getId());
//
//            //得到任务主管
//            if (null != transaction.getManagerId()) {
//                noticeService.addNotice(SysNotice.Type.WORKFLOW,
//                        transaction.getManagerId(),
//                        String.format("任务 %s 已被 %s 接受", transaction.getInstance().getTitle(), user.getTrueName()),
//                        C.newMap(
//                                "taskId", transaction.getInstanceId()
//                        )
//                );
//            }
//        }
//        return Result.ok(successIds);
//    }
//
//    /**
//     * 更新任务归属
//     *
//     * @param instance
//     */
//    private void updateInstanceBelongs(final WorkflowInstance instance, final User user) {
//        Long fromUid = instance.getDealUserId();
//        instance.setDealUserId(user.getId());
//        instance.setDealUserName(user.getTrueName());
//        //更新所有起始节点的经办人
//        List ids = nodeInstanceDao.getStartNodeIds(instance.getId());
//        nodeInstanceDao.updateNodeInstanceDealer(user.getId(), ids);
//        //更新所有子流程的归属
//        if (null != fromUid) {
//            List<WorkflowInstance> instances = instance.getChildInstances();
//            for (WorkflowInstance workflowInstance : instances) {
//                updateInstanceBelongs(workflowInstance, user);
//            }
//        }
//        //保存状态
//        saveWorkflowInstance(instance);
//    }
//
//    /**
//     * 拒绝指派/移交
//     *
//     * @param uid
//     * @param info
//     * @param taskIds
//     * @return
//     */
//    public Result rejectTask(final long uid, final String info, Long... taskIds) {
//        User user = userService.findUser(uid);
//        List<Long> successIds = new ArrayList<>();
//        List<WorkflowInstanceTransaction> transactions = transactionDao.findAllByUserIdAndInstanceIdInAndStateIn(uid, Arrays.asList(taskIds), Collections.singleton(WorkflowInstanceTransaction.State.DEALING));
//        for (WorkflowInstanceTransaction transaction : transactions) {
//            transaction.setState(WorkflowInstanceTransaction.State.REJECT);
//            transaction.getInstance().setState(transaction.getFromState());
////            transaction.getInstance().setDealUserId(uid);
////            //保存主体
//            instanceDao.save(transaction.getInstance());
////            //更新所有起始节点的经办人
////            nodeInstanceDao.updateNodeInstanceDealer(transaction.getInstanceId(), uid);
////            //更新事务
//            transactionDao.save(transaction);
//            successIds.add(transaction.getId());
//
//            //发送消息
//            noticeService.addNotice(SysNotice.Type.WORKFLOW,
//                    transaction.getManagerId(),
//                    String.format("%s 拒绝接受任务 %s ，拒绝理由：%s", user.getTrueName(), transaction.getInstance().getTitle(), info),
//                    C.newMap(
//                            "taskId", transaction.getInstanceId()
//                    )
//            );
//        }
//        return Result.ok(successIds);
//    }
//
//
//    public List<Long> getManagerDepIds(final User user) {
//        List<Long> dids = user.getQuarters()
//                .stream()
//                .filter(q -> q.isManager())
//                .map(q -> q.getDepartmentId())
//                .flatMap(did -> userService.getDidsFromDepartment(did).stream())
//                .collect(Collectors.toList());
//        if (dids.size() == 0) {
//            dids.add(-1L);
//        }
//        return dids;
//    }
//
//
//    /**
//     * 主管得到管理的任务
//     *
//     * @param uid
//     * @param request
//     * @param lessId
//     * @param pageable
//     * @return
//     */
//    public Page getUnreceivedWorks(long uid, UnreceivedWorksSearchRequest request, Long lessId, Pageable pageable) {
//        if (null == lessId) lessId = Long.MAX_VALUE;
//        User user = userService.findUser(uid);
//        if (null == user) {
//            return new PageImpl<>(new ArrayList<>(), pageable, 0);
//        }
//        //得到主管岗位
//        //得到下属所有岗位
//        List<Long> dids = getManagerDepIds(user);
//
//        //得到我监管的所有模型ID
////        List oids = globalPermissionDao.getManagerObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), uids);
//        Long finalLessId = lessId;
//        Specification query = new Specification<WorkflowInstance>() {
//
//            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
//                List<Predicate> predicates = new ArrayList<>();
//
//                predicates.add(root.get("state").in(UNRECEIVED,
//                        DEALING,
//                        COMMON,
//                        WorkflowInstance.State.PAUSE));
//                predicates.add(cb.lessThan(root.get("id"), finalLessId));
////                Join join = root.join("workflowModel");
//                //关联模型
//                //select count(*) from Department d where d.id = instance.depId and d.code in :codes > 0
//                //select count(*) from Department d where d.id = instance.depId and d.code in :codes and
////                if (oids.size() > 0) {
////                    predicates.add(join.get("id").in(oids));
////                }
//                predicates.add(root.get("depId").in(dids));
//
//                if (!StringUtils.isEmpty(request.getId())) {
//                    predicates.add(cb.like(root.get("id"), "%" + request.getId() + "%"));
//                }
//                //限制状态
//                if (null != request.getState()) {
//                    predicates.add(cb.equal(root.get("state"), request.getState()));
//                }
//                //模型名称
//                if (!StringUtils.isEmpty(request.getModelName())) {
//                    predicates.add(
//                            cb.like(
//                                    root.get("modelName"), "%" + request.getModelName() + "%"
//                            )
//                    );
//                }
//                //客户经理编号
//                if (null != request.getUserId()) {
//                    predicates.add(cb.equal(root.get("dealUserId"), request.getUserId()));
//                }
//                //开始时间
//                if (null != request.getStartDate()) {
//                    predicates.add(
//                            cb.greaterThanOrEqualTo(root.get("addTime"), new Date(request.getStartDate()))
//                    );
//                }
//                //结束时间
//                if (null != request.getEndDate()) {
//                    predicates.add(
//                            cb.lessThanOrEqualTo(root.get("addTime"), new Date(request.getEndDate()))
//                    );
//                }
//
//                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
//            }
//        };
//
//        return instanceDao.findAll(query, pageable);
//    }
//
//
//    /**
//     * 得到用户的预任务列表
//     *
//     * @param uids
//     * @param lessId
//     * @param pageable
//     * @return
//     */
//    public Page<WorkflowInstance> getPlanWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
//        if (null == lessId) lessId = Long.MAX_VALUE;
//        return instanceDao.findAllByDealUserIdInAndPlanStartTimeGreaterThanAndIdLessThan(uids, new Date(), lessId, pageable);
//    }
//
//    /**
//     * 指派/移交 任务
//     *
//     * @param uid
//     * @param iids
//     * @param toUid
//     * @return 处理成功的任务ID
//     */
//    public Result pointTask(long uid, Collection<Long> iids, long toUid) {
//        User manager = userService.findUser(uid);
//        List<Long> success = new ArrayList<>();
//        List<String> errMessages = new ArrayList<>();
//
//        Set<WorkflowInstance.State> states = C.newSet(COMMON, UNRECEIVED, DEALING);
//
//        for (Long iid : iids) {
//            WorkflowInstance instance = findInstance(iid);
//
//            //检查是否有指派权限
//            if (!canPoint(instance.getModelId(), uid)) {
//                errMessages.add("没有指派/移交权限");
//                continue;
//            }
//
//            //接受人是否有执行权限
//            if (!canPub(instance.getModelId(), toUid)) {
//                errMessages.add("被指定的人没有接受权限");
//                continue;
//            }
//
//            //不能自己移交给自己
//            if (null != instance.getDealUserId() && instance.getDealUserId().equals(toUid)) {
//                errMessages.add("任务无法给相同的人");
//                continue;
//            }
//
//            //任务状态是否合法
//            if (!states.contains(instance.getState())) {
//                errMessages.add("该任务无法做此操作");
//                continue;
//            }
//
//            //子任务禁止移交
//            if (instance.getParentId() != null) {
//                errMessages.add("子任务禁止操作");
//                continue;
//            }
//
//            WorkflowInstanceTransaction transaction = new WorkflowInstanceTransaction();
//            transaction.setFromState(instance.getState());
//            transaction.setToState(DEALING);
//            transaction.setInstanceId(instance.getId());
//            transaction.setUserId(toUid);
//            transaction.setManager(manager);
//            transactionDao.save(transaction);
//            instance.setState(WorkflowInstance.State.PAUSE);
//            if (instance.getState().equals(DEALING)) {
//                instance.setSecondState(WorkflowInstance.SecondState.TRANSFORM);
//            } else {
//                instance.setSecondState(WorkflowInstance.SecondState.POINT);
//            }
//            saveWorkflowInstance(instance);
//
//            success.add(instance.getId());
//
//            //notice
//            noticeService.addNotice(SysNotice.Type.WORKFLOW, toUid, String.format("你有一条新的任务指派/移交 %s", instance.getTitle()), C.newMap(
//                    "taskId", instance.getId(),
//                    "taskName", instance.getTitle()
//            ));
//        }
//
//        if (errMessages.size() > 0) {
//            return Result.error(StringUtils.join(errMessages.toArray(), ","));
//        } else {
//            return Result.ok(success);
//        }
//    }
//
//
//    /**
//     * 提交数据
//     *
//     * @param uid
//     * @param request
//     * @return
//     */
////    private Result submitData(WorkflowInstance instance, WorkflowNodeInstance nodeInstance, SubmitDataRequest request){
////        return Result.error();
////    }
////    public Result submitData(long uid, SubmitDataRequest request) {
////        WorkflowInstance instance = findInstance(request.getInstanceId());
////        if (null == instance ||
////                (!instance.getState().equals(DEALING) && !instance.getState().equals(COMMON))) {
////            return Result.error("找不到符合条件的任务");
////        }
////        User user = userService.findUser(uid);
////        if (null == user) {
////            return Result.error("权限错误");
////        }
////        WorkflowNodeInstance nodeInstance = findNodeInstance(request.getNodeId()).orElse(null);
////        if (null == nodeInstance || nodeInstance.isFinished()) {
////            return Result.error("找不到符合条件的节点");
////        }
////        if (!checkNodeAuth(nodeInstance, user)) {
////            return Result.error("权限验证错误");
////        }
////
////        WorkflowNode nodeModel = nodeInstance.getNodeModel();
////        //当前处理人数
////        int now = nodeInstanceDealersDao.countByTypeInAndNodeInstanceIdAndUserIdNot(Collections.singleton(WorkflowNodeInstanceDealer.Type.DID_DEAL), nodeInstance.getId(), uid);
////        if (now + 1 > nodeModel.getMaxPerson()) {
////            return Result.error("该节点已经超过最大处理人数");
////        }
////
////        switch (nodeModel.getType()) {
////            case input:
////                nodeInstance = submitInputNode(uid, nodeInstance, request.getData());
////                break;
////
////            case check:
////                nodeInstance = submitCheckNode(uid, nodeInstance, request.getData());
////                break;
////        }
////        switch (nodeModel.getType()) {
////            case input:
////            case check:
////                //消息发送逻辑, 如果这个节点的仍然有可用的处理人, 那么不发送消息
////                List<WorkflowNodeInstanceDealer> dls = nodeInstanceDealersDao.findAllByTypeAndNodeInstanceId(WorkflowNodeInstanceDealer.Type.CAN_DEAL, nodeInstance.getId());
////                List<Long> notDealUids = dls
////                        .stream()
////                        .map(dealer -> {
////                            long id = 0;
////                            if (dealer.getUserId().equals(uid)) {
////                                dealer.setType(WorkflowNodeInstanceDealer.Type.DID_DEAL);
////                            }
////                            //如果正好符合条件, 那么将所有未执行的人标记
////                            else if (now + 1 >= nodeModel.getMaxPerson()) {
////                                dealer.setType(WorkflowNodeInstanceDealer.Type.NOT_DEAL);
////                                id = dealer.getUserId();
////                            }
////                            //如果位置还有剩余, 那么就只标记自己的
////                            else {
////                                //pass
////                            }
////                            dealer.setLastModify(new Date());
////                            nodeInstanceDealersDao.save(dealer);
////                            return id;
////                        })
////                        .filter(item -> item > 0)
////                        .distinct()
////                        .collect(Collectors.toList());
////                noticeService.addNotice(SysNotice.Type.WORKFLOW, notDealUids, String.format("你的任务 %s 节点 %s 已被别人处理", instance.getTitle(), nodeInstance.getNodeName()), C.newMap(
////                        "taskId", instance.getId(),
////                        "taskName", instance.getTitle(),
////                        "nodeId", nodeInstance.getId(),
////                        "nodeName", nodeInstance.getNodeName()
////                ));
////                break;
////
////        }
//////        if (!StringUtils.isEmpty(errMessage)) {
//////            return Result.error(errMessage);
//////        }
////
////        //保存上传的文件
////        if (C.notEmpty(request.getFiles())) {
////            String files = (String) request.getFiles().stream()
////                    .filter(fid -> systemFileDao.countById(fid) > 0)
////                    .sorted()
////                    .map(fid -> fid + "")
////                    .collect(Collectors.joining(","));
////            nodeInstance.setFiles(files);
////            nodeInstanceDao.save(nodeInstance);
////        }
//
//
////        logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "向 " + nodeInstance.getNodeModel().getName() + " 节点提交了数据");
////        return Result.ok();
////    }
//
//
//    /**
//     * 提交节点到下一步
//     *
//     * @param uid
//     * @param instanceId
//     * @param nodeId
//     * @return
//     */
//    public Result goNext(long uid, long instanceId, long nodeId) {
//        WorkflowNodeInstance nodeInstance = nodeInstanceDao.findFirstByIdAndFinishedIsFalse(nodeId).orElse(null);
//        if (null == nodeInstance) {
//            return Result.error("没有找到可以处理的任务, 该任务可能已经变动");
//        }
//        User user = userService.findUser(uid);
//        if (null == user) {
//            return Result.error("请求用户错误");
//        }
//        WorkflowInstance instance = nodeInstance.getInstance();
//        //如果是第一个节点, 并且授权人已经确认, 那么只检查pub权限即可
////        if (null != nodeInstance.getDealerId() && nodeInstance.getDealerId().equals(uid)) {
////
////        }
//        //验证权限
//        if (!checkNodeAuth(nodeInstance, user)) {
//            return Result.error("授权失败");
//        }
//
//        //如果当前节点是资料节点
//        WorkflowNode nodeModel = nodeInstance.getNodeModel();
//        switch (nodeModel.getType()) {
//            case input:
//                fromInputNodeToGo(uid, instance, nodeInstance, nodeModel.getNode());
//                break;
//
//            case check:
//                fromCheckNodeToGo(uid, instance, nodeInstance, nodeModel.getNode());
//                break;
//        }
//
//        logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "向" + nodeModel.getName() + "提交了下一步");
//        return Result.ok();
//    }
//
//    /**
//     * 上传定位信息
//     *
//     * @param uid
//     * @param request
//     * @return
//     */
//    public Result addPosition(final long uid, WorkflowPositionAddRequest request) {
//        User user = userService.findUser(uid);
//        if (null == user) {
//            return Result.error();
//        }
//        WorkflowNodeInstance nodeInstance = findNodeInstance(request.getNodeInstanceId()).orElse(null);
//        if (null == nodeInstance || !nodeInstance.getId().equals(request.getNodeInstanceId()) || !nodeInstance.getInstance().getState().equals(DEALING)) {
//            return Result.error("找不到对应的节点实例");
//        }
//        //是否该我处理
//        if (!checkNodeAuth(nodeInstance, user)) {
//            return Result.error("权限错误");
//        }
//        WorkflowNodeFile file = new WorkflowNodeFile();
//        file.setUserId(uid);
//        file.setType(WorkflowNodeFile.Type.POSITION);
//        file.setNodeInstance(nodeInstance);
//        file.setFileName(request.getPosition());
//
//        GPSPosition gpsPosition = new GPSPosition();
//        gpsPosition.setLat(request.getLat());
//        gpsPosition.setLng(request.getLng());
//        gpsPosition.setPosition(request.getPosition());
//        gpsPosition.setUserId(uid);
//        gpsPosition = gpsPositionDao.save(gpsPosition);
//
//        file.setFileId(gpsPosition.getId());
//        return Result.ok(nodeFileDao.save(file));
//    }
//
//    /**
//     * 删除节点附件
//     *
//     * @param uid
//     * @param nodeFileId
//     * @return
//     */
//    public boolean deleteNodeFile(final long uid, final long nodeFileId) {
//        User user = userService.findUser(uid);
//        if (null == user) {
//            return false;
//        }
//        WorkflowNodeFile nodeFile = findNodeFile(nodeFileId).orElse(null);
//        if (null == nodeFile) {
//            return false;
//        }
//        if (null != nodeFile.getNodeInstance()) {
//            if (!checkNodeAuth(nodeFile.getNodeInstance(), user)) {
//                return false;
//            }
//        }
//        nodeFileDao.deleteById(nodeFileId);
//        return true;
//    }
//
//    /**
//     * 上传节点文件
//     *
//     * @param uid
//     * @param instanceId
//     * @param nodeId
//     * @param fileType
//     * @param file
//     * @return
//     * @throws IOException
//     */
//    public Result uploadNodeFile(final long uid, final Long instanceId, final Long nodeId, WorkflowNodeFile.Type fileType, MultipartFile file, String content, String tag) {
//        User user = userService.findUser(uid);
//        if (null == user) {
//            return Result.error();
//        }
//        WorkflowNodeInstance nodeInstance = null;
//        if (null != nodeId) {
//            nodeInstance = nodeInstanceDao.getCurrentNodeInstance(instanceId).orElse(null);
//            if (null == nodeInstance || !nodeInstance.getId().equals(nodeId)) {
//                return Result.error("找不到对应的节点实例");
//            }
//            //是否该我处理
//            if (!checkNodeAuth(nodeInstance, user)) {
//                return Result.error("权限错误");
//            }
//        }
//        SystemFile systemFile = new SystemFile();
//        try {
//            systemFile.setBytes(file.getBytes());
//        } catch (IOException e) {
//            return Result.error("保存文件错误");
//        }
//        systemFile.setType(SystemFile.Type.WORKFLOW);
//        systemFile.setFileName(file.getOriginalFilename());
//        systemFile.setExt(FilenameUtils.getExtension(systemFile.getFileName()));
//        systemFile = systemFileDao.save(systemFile);
//
//        WorkflowNodeFile nodeFile = new WorkflowNodeFile();
//        nodeFile.setFileName(systemFile.getFileName());
//        nodeFile.setFileId(systemFile.getId());
//        nodeFile.setNodeInstance(nodeInstance);
//        nodeFile.setType(fileType);
//        nodeFile.setUserId(user.getId());
//        nodeFile.setExt(systemFile.getExt());
//        if (fileType.equals(WorkflowNodeFile.Type.SIGN)) {
//            nodeFile.setContent(content);
//        }
//        if (!StringUtils.isEmpty(tag)) {
//            List<String> list = Arrays.stream(tag.split(" "))
//                    .filter(item -> !StringUtils.isEmpty(item))
//                    .distinct()
//                    .collect(Collectors.toList());
//            nodeFile.setTags(StringUtils.join(list.toArray(), " "));
//        }
//        nodeFile = nodeFileDao.save(nodeFile);
//        nodeFile.setToken(applyDownload(uid, nodeFile.getId()));
//        return Result.ok(nodeFile);
//    }
//
//    /**
//     * 设置节点标签
//     *
//     * @param uid
//     * @param nodeId
//     * @param tags
//     * @return
//     */
//    public boolean setNodeFileTags(final long uid, final long nodeId, String tags) {
//        val list = Arrays.asList(tags.split(" ")).stream().filter(item -> !StringUtils.isEmpty(item))
//                .distinct()
//                .collect(Collectors.toList());
//        String str = org.apache.commons.lang.StringUtils.join(list.toArray(), " ");
//        if (null == str) {
//            str = "";
//        }
//        //查找是否存在这个节点
//        return nodeFileDao.updateNodeFileTags(uid, nodeId, str) > 0;
//    }
//
//    /**
//     * 节点文件重命名
//     *
//     * @param uid
//     * @param nodeId
//     * @param name
//     * @return
//     */
//    public boolean setNodeFileName(long uid, long nodeId, String name) {
//        //处理名字
//        WorkflowNodeFile nodeFile = findNodeFile(nodeId).orElse(null);
//        if (null == nodeFile) {
//            return false;
//        }
//        name = FilenameUtils.removeExtension(name);
//        if (!StringUtils.isEmpty(nodeFile.getExt())) {
//            name = name + "." + nodeFile.getExt();
//        }
//        return nodeFileDao.updateNodeFileName(uid, nodeId, name) > 0;
//    }
//
//    /**
//     * 申请下载文件
//     *
//     * @param uid
//     * @param id
//     * @return
//     */
//    public String applyDownload(long uid, long id) {
//        WorkflowNodeFile nodeFile = findNodeFile(id).orElse(null);
//        if (null == nodeFile) {
//            return "";
//        }
//        //TODO: 检查是否拥有查看该节点的权限
//        DownloadFileToken token = new DownloadFileToken();
//        token.setExprTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
//        token.setToken(UUID.randomUUID().toString());
//        token.setFileId(nodeFile.getFileId());
//        downloadFileTokenDao.save(token);
//        return token.getToken();
//    }
//
//
//    /**
//     * 编辑工作流模型
//     *
//     * @param edit 模型编辑详情
//     * @return
//     */
//    public Result editWorkflowModel(WorkflowModelEdit edit) {
//        WorkflowModel model = findModel(edit.getId()).orElse(null);
//        if (null == model) {
//            return Result.error("找不到这个工作流");
//        }
//        if (edit.isOpen()) {
//            //查找是否有同名已开启的流程
//            if (modelDao.countByModelNameAndOpenIsTrue(model.getModelName()) > 0) {
//                return Result.error("还有同名的工作流没有关闭, 无法开启");
//            }
//            model.setFirstOpen(true);
//        }
//        //流程名字修改
//        if (!StringUtils.isEmpty(edit.getName())) {
//            model.setName(edit.getName());
//        }
//        //描述可以随意修改
//        if (!StringUtils.isEmpty(edit.getInfo())) {
//            model.setInfo(edit.getInfo());
//        }
////        if (null != edit.getProcessCycle()) {
////            model.setProcessCycle(edit.getProcessCycle());
////        }
//        //开关也可以
//        model.setOpen(edit.isOpen());
//
//        modelDao.save(model);
//        return Result.ok();
//    }
//
//
//    /**
//     * 删除节点
//     *
//     * @param modelId 模型ID
//     * @param nodeId  节点ID
//     * @return 是否删除成功
//     */
//    public boolean deleteNode(long modelId, Long nodeId) {
//        try {
//            nodeDao.deleteAllByModel_IdAndIdAndStartIsFalseAndEndIsFalse(modelId, nodeId);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * create a new model of workflow by pre-defined model
//     *
//     * @param modelName 要创建的模型名称
//     * @param add       请求明细
//     * @return 是否创建成功
//     */
//    public WorkflowModel createWorkflow(String modelName, WorkflowModelAdd add) {
//        Map<String, Map> map = (Map) cache.getWorkflowConfig();
//        if (!map.containsKey(modelName)) {
//            throw new RestException("没找到工作流模型");
//        }
//        Map model = map.get(modelName);
//        Map<String, Map> flow = (Map) model.getOrDefault("flow", new HashMap<>());
////        WfModel same = modelDao.findTopByName(add.getName()).orElse(null);
////        if (same != null) throw new RestException("已经有同名的工作流");
//
//        WorkflowModel workflowModel = $.map(add).to(WorkflowModel.class);
////        WfModel workflowModel = Transformer.transform(add, WfModel.class);
//        Map<String, BaseNode> nodes = new HashMap<>();
//        for (Map.Entry<String, Map> entry : flow.entrySet()) {
//            String k = entry.getKey();
//            Map v = entry.getValue();
//            boolean start = (boolean) ((Map) v).getOrDefault("start", false);
//            WorkflowNode.Type type = WorkflowNode.Type.valueOf(String.valueOf(((Map) v).get("type")).toLowerCase());
//
////            BaseNode baseNode = BaseNode.create(workflowModel,(Map) v);
//
//            JSONObject baseNode = null;
//            WorkflowNode node = new WorkflowNode();
//            switch (type) {
//                case input:
//                    baseNode = buildInputNode(workflowModel, (Map) v);
//                    break;
////                    baseNode = new JSONObject();
//
//                case check:
//                    baseNode = buildCheckNode((Map) v);
//                    node.setMaxPerson(baseNode.getInteger("count"));
//                    break;
//
////                case logic:
////                    baseNode = buildLogicNode((Map) v);
////                    break;
//
//                case end:
//                    baseNode = buildNormalNode((Map) v);
//                    break;
//            }
//
//
//            //nodelist
//            node.setModel(workflowModel);
//            node.setName(k);
//            node.setType(type);
//            Object next = ((Map) v).get("next");
//            //start
//            node.setStart(start);
//            if (((Map) v).containsKey("end")) {
//                node.setEnd((Boolean) ((Map) v).getOrDefault("end", false));
//            }
//            if (node.getType().equals("end")) {
//                node.setEnd(true);
//            }
//            //next
//            if (next instanceof Collection) {
//                ((Collection) next).forEach(n -> node.getNext().add(String.valueOf(n)));
//            } else if (next instanceof String) {
//                if (!StringUtils.isEmpty((String) next)) {
//                    node.getNext().add(String.valueOf(next));
//                }
//            }
//            node.setNode(baseNode);
//            nodeDao.save(node);
//
//            workflowModel.getNodeModels().add(node);
//        }
//
//        //允许开放的子流程
//        List<String> allowChildTask = (List<String>) ((Map) model).getOrDefault("allow_child_task", new ArrayList<>());
////        workflowModel.setAllowChildTask(allowChildTask);
////        workflowModel.setOpen(false);
////        workflowModel.setFirstOpen(false);
//        workflowModel.setModelName(modelName);
//        workflowModel.setProcessCycle(add.getProcessCycle());
//
//        if (workflowModel.getInnates().size() > 0) {
//            for (WorkflowModelInnate workflowModelInnate : workflowModel.getInnates()) {
//                innateDao.save(workflowModelInnate);
//            }
////            innateDao.save(workflowModel.getInnates());
//        }
//
//        //补充额外信息
//        boolean start = (boolean) model.getOrDefault("manual", false);
//        start = true;
//        workflowModel.setManual(start);
//        boolean custom = (boolean) model.getOrDefault("custom", true);
//        workflowModel.setCustom(custom);
//
//        return modelDao.save(workflowModel);
//    }
//
//
//    /**
//     * delete a workflow's model
//     *
//     * @param id
//     * @param force
//     * @return
//     */
////    public boolean deleteWorkflowModel(long id, boolean force) {
////        if (force) {
////            modelDao.deleteById(id);
////            return true;
////        }
////        return modelDao.deleteWorkflowModel(id) > 0;
////    }
//
//    /**
//     * update departments of workflow which might belong to
//     *
//     * @param modelId
//     */
//    public void updateWorkflowModelDeps(long modelId) {
//        WorkflowModel model = findModel(modelId).orElse(null);
//        if (null == model) {
//            return;
//        }
//        List<GlobalPermission> globalPermissions = globalPermissionDao.findAllByTypeInAndObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), modelId);
//        List<Long> dids = globalPermissions.stream()
//                .filter(item -> item.getUserType().equals(GlobalPermission.UserType.QUARTER))
//                .map(item -> userService.findQuarters(item.getLinkId()))
//                .filter(item -> null != item)
//                .map(item -> item.getDepartmentId())
//                .collect(Collectors.toList());
//        if (dids.size() > 0) {
//            modelDao.updateWorkflowModelDeps(modelId, StringUtils.join(dids.toArray(), ","));
//        }
//    }
//
//    /**
//     * set fields of workflow's model
//     *
//     * @param modelId
//     * @param requests
//     * @return 设置结果
//     */
//    public Result setModelFields(long modelId, Collection<ModelFieldRequest> requests) {
//        if (!modelDao.existsById(modelId)) {
//            return Result.error("不存在该工作流模型");
//        }
//        List<String> names = new ArrayList<>();
//        for (ModelFieldRequest request : requests) {
//            WorkflowModelField field = fieldDao.findTopByModelIdAndName(modelId, request.getName()).orElse(new WorkflowModelField());
//            field.setType(request.getType());
//            field.setName(request.getName());
//            field.setHint(request.getHint());
//            field.setModelId(modelId);
//            field.setExt(request.getExt());
//            fieldDao.save(field);
//            names.add(request.getName());
//        }
//        //清除无用的数据
//        if (names.size() == 0) {
//            fieldDao.deleteAllByModelId(modelId);
//        } else {
//            fieldDao.deleteAllByModelIdAndNameNotIn(modelId, names);
//        }
//        return Result.ok();
//    }
//
//    public List<WorkflowModelField> getModelFields(long modelId) {
//        return fieldDao.findAllByModelId(modelId);
//    }
//
//    /**
//     * search list of models
//     *
//     * @param request
//     * @param pageable
//     * @return
//     */
////    public Page getModelList(Map<String,Object> params, Pageable pageable) {
////        PageQuery pageQuery = Utils.convertPageable(pageable);
////        pageQuery.setParas(params);
////        sqlManager.pageQuery("", Map.class, pageQuery);
////        return Utils.convertPage(pageQuery, pageable);
////    }
//
//    /**
//     * search information of an instance
//     *
//     * @param uid
//     * @param object
//     * @param pageable
//     * @return
//     */
//    public Page getInstanceList(long uid, Map<String, String> object, Pageable pageable) {
//        User user = userService.findUser(uid);
//        if (null == user) {
//            return new PageImpl(new ArrayList(), pageable, 0);
//        }
//        Specification query = ((root, criteriaQuery, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            //限制我自己的任务
//            //if the params has key su, and this user is super user, will ignore this option
//            if (object.containsKey("su") && user.getSu()) {
//
//            } else {
//                predicates.add(cb.equal(root.get("dealUserId"), uid));
//            }
//            for (Map.Entry<String, String> entry : object.entrySet()) {
//                if (StringUtils.isEmpty(entry.getValue())) {
//                    continue;
//                }
//                try {
//                    switch (entry.getKey()) {
//                        case "modelName":
//                            String str = "%" + Arrays.stream(entry.getValue().split(","))
//                                    .collect(Collectors.joining("|")) + "%";
//                            predicates.add(cb.like(root.get("modelName"), str));
//                            break;
//
//                        case "parentId":
//                            predicates.add(cb.equal(root.get("parentId"), object.get("parentId")));
//                            break;
//
//                        case "id":
//                            predicates.add(
//                                    cb.equal(root.get("id"), entry.getValue())
//                            );
//                            break;
//
//                        case "state":
//                            predicates.add(
//                                    cb.equal(root.get("state"), WorkflowInstance.State.valueOf(entry.getValue()))
//                            );
//                            break;
//
//                        case "start_date":
//                            predicates.add(
//                                    cb.greaterThanOrEqualTo(root.get("addTime"), new Date(Long.valueOf(entry.getValue())))
//                            );
//                            break;
//
//                        case "end_date":
//                            predicates.add(
//                                    cb.lessThanOrEqualTo(root.get("addTime"), new Date(Long.valueOf(entry.getValue())))
//                            );
//                            break;
//
//                        case "CUS_NAME":
//                            Join join = root.join("attributes", JoinType.LEFT);
//                            predicates.add(
//                                    cb.and(
//                                            cb.equal(join.get("attrKey"), entry.getKey()),
//                                            cb.like(join.get("attrValue"), "%" + entry.getValue() + "%")
//                                    )
//                            );
//                            break;
//                    }
//                } catch (Exception e) {
//                    continue;
//                }
//            }
//
//            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
//        });
//        Page<WorkflowInstance> instances = instanceDao.findAll(query, pageable);
//        //默认增加五个字段
//        String fields = object.getOrDefault("fields", "");
//        if (!StringUtils.isEmpty(fields)) {
//            fields += ",";
//        }
//        fields += "BILL_NO,CONT_NO,CUS_ID,CUS_NAME,LOAN_ACCOUNT";
//        object.put("fields", fields);
//        return instances.map(o -> {
//            WorkflowInstance instance = o;
//            instance.setAttributes(null);
//            instance.setNodeList(null);
//            instance.setCanDeal(canDeal(instance.getId(), user.getId()));
//            JSONObject jsonObject = (JSONObject) JSON.toJSON(o);
////            jsonObject.put("currentNodeInstanceId", findCurrentNodeInstance(instance.getId()).map(item -> item.getId()).orElse(0l));
//            if (object.containsKey("fields")) {
//                for (String field : Utils.splitByComma(object.get("fields"))) {
//                    jsonObject.put(field, instance.getAttributeMap().getOrDefault(field, ""));
//                }
//            }
//            return jsonObject;
//        });
//    }
//
//    public Optional<WorkflowInstanceAttribute> getAttributeByCname(final long instanceId, final String cname) {
//        return instanceAttributeDao.findTopByInstanceIdAndAttrCName(instanceId, cname);
//    }
//
//    public Optional<WorkflowInstanceAttribute> getAttributeByKey(final long instanceId, final String key) {
//        return instanceAttributeDao.findTopByInstanceIdAndAttrKey(instanceId, key);
//    }
//
//    public String getAttributeValueByKey(final long instanceId, final String key) {
//        return instanceAttributeDao.findTopByInstanceIdAndAttrKey(instanceId, key)
//                .map(WorkflowInstanceAttribute::getAttrValue)
//                .orElse("");
//    }
//
//
//
//    /**
//     * 得到我可以发布的模型列表
//     *
//     * @param uid
//     * @return
//     */
//    public List<WorkflowModel> getUserModelList(long uid) {
//        //可以发布的模型ID
//        List<Long> pubIds = globalPermissionDao.getObjectIds(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), Collections.singleton(uid));
//        //可以指派的模型ID
//        List<Long> pointIds = globalPermissionDao.getManagerObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), Collections.singleton(uid));
//        List<WorkflowModel> models = modelDao.getAllWorkflows()
//                .stream()
//                .filter(model -> pubIds.contains(model.getId()) || pointIds.contains(model.getId()))
//                .peek(model -> {
//                    model.setPub(pubIds.contains(model.getId()));
//                    model.setPoint(pointIds.contains(model.getId()));
//                    model.setNodeModels(C.newList(nodeDao.findFirstByModel_IdAndStartIsTrue(model.getId()).orElse(null)));
//                }).collect(Collectors.toList());
//        return models;
//    }
//
////    @Data
////    public static class ModelSearchRequest {
////        String modelName;
////    }
//
//
//    /**
//     * 创建审核节点
//     *
//     * @param nodeModel
//     * @return
//     */
//    public Optional<WorkflowNode> createCheckNode(CheckNodeModel nodeModel) {
//        return findModel(nodeModel.getModelId()).map(model -> {
//            //无法自定义的流程不允许自定义
//            if (!model.isCustom() || model.isFirstOpen()) {
//                throw new RestException("一经开启, 流程禁止再编辑");
//            }
//            //验证是否有必填的属性
////            CheckNode checkNode = new CheckNode();
////            checkNode.setQuestion(nodeModel.getQuestion());
////            checkNode.setCount(nodeModel.getCount());
////            checkNode.setKey(nodeModel.getKey());
////            checkNode.setPs(nodeModel.getPs());
////            checkNode.setStates(nodeModel.getStates());
//            JSONObject checkNode = (JSONObject) JSON.toJSON(nodeModel);
//
//            WorkflowNode node = new WorkflowNode();
//            node.setNode(checkNode);
//            node.setEnd(false);
//            node.setStart(false);
//            node.setNext(nodeModel.getNext());
//            node.setType(WorkflowNode.Type.check);
//            node.setName(nodeModel.getName());
//            node.setModel(model);
//            node.setMaxPerson(checkNode.getInteger("count"));
//            //如果有ID, 则为修改
//            node.setId(nodeModel.getNodeId());
//
//            //移除不必要的属性
//            checkNode.remove("next");
//            checkNode.remove("name");
//            checkNode.remove("nodeId");
//
//            node = nodeDao.save(node);
//            return node.getId() == null ? null : node;
//        });
//
//    }
//
//
//    private void goNextNode(WorkflowInstance instance, WorkflowNodeInstance currentNode, WorkflowNode nextNode) {
//        WorkflowNode currentNodeModel = findNode(currentNode.getNodeModelId());
//
//        //本节点完毕
//        currentNode.setFinished(true);
//        currentNode.setDealDate(new Date());
//
//        WorkflowNodeInstance newNode = addNode(instance, nextNode, false);
//        newNode.setFinished(nextNode.isEnd());
//        newNode = nodeInstanceDao.save(newNode);
//        instance.getNodeList().add(newNode);
//
//        //需要存储的东西
//        List<SysNotice> notices = C.newList();
//
//        //更新本节点所有值到主表属性里
//        for (WorkflowNodeAttribute attr : currentNode.getAttributeList()) {
//            instanceAttributeDao.deleteByInstanceIdAndAttrKey(instance.getId(), attr.getAttrKey());
//            addAttribute(instance, WorkflowInstanceAttribute.Type.NODE, attr.getAttrKey(), attr.getAttrValue(), attr.getAttrCname());
//        }
//
//        if (null != nextNode.getNode() && nextNode.getType().equals(WorkflowNode.Type.end)) {
//            instance.setFinishedDate(new Date());
//            instance.setState(WorkflowInstance.State.FINISHED);
//            //归档整理
//            //处理自定义行为
//            JSONObject end = nextNode.getNode();
//            if (!StringUtils.isEmpty(end.getString("behavior"))) {
//                try {
//                    runJSCode(instance, currentNode, end.getString("behavior"));
//                } catch (ScriptException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //关闭所有子任务
//            currentNode.getInstance().getChildInstances()
//                    .stream()
//                    .filter(ins -> !ins.getState().equals(WorkflowInstance.State.FINISHED))
//                    .forEach(ins -> {
//                        ins.setState(WorkflowInstance.State.CANCELED);
//                        instanceDao.save(ins);
//                    });
//        }
//        //资料节点的回卷, 如果曾经存在提交, 那么重新附上所有值以及文件
//        else if (newNode.getType().equals(WorkflowNode.Type.input)) {
//            if (nodeInstanceDao.countByInstanceIdAndNodeModelId(newNode.getInstanceId(), newNode.getNodeModelId()) >= 2) {
//                WorkflowNodeInstance lastNode = nodeInstanceDao.findTopByInstanceIdAndNodeModelIdAndIdLessThanOrderByIdDesc(newNode.getInstanceId(), newNode.getNodeModelId(), newNode.getId()).orElse(null);
//                if (null == lastNode) {
//
//                } else {
//                    if (null != lastNode.getDealerId()) {
//                        newNode.setDealerId(lastNode.getDealerId());
//                        nodeInstanceDao.save(newNode);
//                    }
//                    //资料节点重新赋值
//                    if (newNode.getType().equals(WorkflowNode.Type.input)) {
//                        for (WorkflowNodeAttribute attribute : lastNode.getAttributeList()) {
//                            WorkflowNodeAttribute newAttr = JSON.toJavaObject((JSON) JSON.toJSON(attribute), WorkflowNodeAttribute.class);
//                            newAttr.setId(null);
//                            newAttr.setNodeInstanceId(newNode.getId());
//                            newAttr.setNodeInstance(newNode);
//                            attributeDao.save(newAttr);
//                        }
//                    }
//                }
//            }
//        }
//        //如果是逻辑节点
//        else if (nextNode.getType().equals(WorkflowNode.Type.logic)) {
//            runLogicNode(instance, newNode);
//        }
//
//        //查找旧节点的处理人, 发送消息
//        switch (currentNode.getType()) {
//            case input:
//            case check:
//                List<Long> uids = attributeDao.getUidsByNodeInstnce(currentNode.getId());
//                Map map = C.newMap(
//                        "taskId", currentNode.getInstanceId(),
//                        "taskName", instance.getTitle(),
//                        "nodeId", currentNode.getId(),
//                        "nodeName", currentNode.getNodeName()
//                );
//                notices.addAll(noticeService.makeNotice(SysNotice.Type.WORKFLOW, uids, String.format("你已处理完毕任务 %s 节点 %s", instance.getTitle(), currentNode.getNodeName()), map));
//
//                break;
//
//        }
//
//        //查找当前节点的可处理人, 发送消息
//        switch (newNode.getType()) {
//            case input:
//            case check:
//                List<JSONObject> objects = getNodeDealUids(newNode.getInstanceId(), newNode.getNodeName());
////                List<Object> objects = getCanDealUids(newNode.getId());
//                if (newNode.getNodeModel().isStart()) {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("ID",null);
//                    jsonObject.put("UID",newNode.getInstance().getDealUserId());
//                    jsonObject.put("UNAME", newNode.getInstance().getDealUserName());
//                    jsonObject.put("DID", newNode.getInstance().getDepId());
//                    jsonObject.put("DNAME", newNode.getInstance().getDepName());
//                    jsonObject.put("USER_TYPE", GlobalPermission.UserType.QUARTER.name());
//                    objects.add(jsonObject);
//                }
//                WorkflowNodeInstance finalNewNode = newNode;
//                //强制施加规则, 如果该节点只允许一个人处理, 且任务执行人拥有这个节点的权限, 那么直接确立为该用户执行
//                //如果存在多人执行的情况, 那么仍然确认该用户, 且保存多余的可执行人
//                if (newNode.getNodeModel().getMaxPerson() == 1) {
//                    List newObjects = objects
//                            .stream()
//                            .filter(item -> {
//                                return item.getLongValue("UID") == finalNewNode.getInstance().getDealUserId();
////                                Object obj = item.get("UID");
////                                if (obj instanceof Long) {
////                                    return obj.equals(finalNewNode.getInstance().getDealUserId());
////                                } else {
////                                    try {
////                                        return Long.parseLong(String.valueOf(obj)) == finalNewNode.getInstance().getDealUserId();
////                                    } catch (Exception e) {
////                                        return false;
////                                    }
////                                }
////                                return Long.valueOf(item.get("UID")).equals(finalNewNode.getInstance().getDealUserId());
////                                ((Long) ((Object[]) item)[2]).equals(finalNewNode.getInstance().getDealUserId())
//                            })
//                            .collect(Collectors.toList());
//                    if (newObjects.size() > 0) {
//                        objects = newObjects;
//                    }
//                }
//                List<Long> uids = objects.stream()
//                        .map(item -> {
////                            Object[] objs = (Object[]) item;
////                            Long uid = (Long) objs[2];
//                            Long uid = (item.getLong("UID"));
//                            String trueName = item.getString("UNAME");
////                            String trueName = (String) objs[3];
////                            GlobalPermission.UserType userType = (GlobalPermission.UserType) objs[1];
//                            GlobalPermission.UserType userType = GlobalPermission.UserType.valueOf(item.getString("USER_TYPE"));
//                            WorkflowNodeInstanceDealer dealers = new WorkflowNodeInstanceDealer();
//                            //已经确定执行的情况
//                            if (null != finalNewNode.getInstance().getDealUserId() && finalNewNode.getInstance().getDealUserId().equals(uid)) {
//                                dealers.setType(WorkflowNodeInstanceDealer.Type.DID_DEAL);
//                            } else {
//                                dealers.setType(WorkflowNodeInstanceDealer.Type.CAN_DEAL);
//                            }
//                            dealers.setNodeInstanceId(finalNewNode.getId());
//                            dealers.setUserId(uid);
//                            dealers.setUserType(userType);
//                            dealers.setUserTrueName(trueName);
//                            dealers.setLastModify(new Date());
////                            dealers.setUserName();
//                            switch (userType) {
//                                case QUARTER:
//                                    dealers.setDepId(Utils.toLong(item.get("DID")));
//                                    dealers.setDepName(item.getString("DNAME"));
////                                    dealers.setDepId((Long) objs[4]);
////                                    dealers.setDepName((String) objs[5]);
////                                    dealers.setQuartersId((Long) objs[6]);
////                                    dealers.setQuartersName((String) objs[7]);
//                                    break;
//                                case USER:
//                                    break;
//                                case ROLE:
//                                    dealers.setRoleId(Utils.toLong(item.get("RID")));
//                                    dealers.setRoleName(item.getString("RNAME"));
//                                    break;
//                            }
//                            nodeInstanceDealersDao.save(dealers);
//                            return uid;
//                        })
//                        .distinct()
//                        .collect(Collectors.toList());
////                List<Long> uids = globalPermissionDao.getUids(Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), newNode.getNodeModelId());
//                notices.addAll(noticeService.makeNotice(SysNotice.Type.WORKFLOW, uids, String.format("你有新的任务 %s 节点 %s 可以处理", instance.getTitle(), newNode.getNodeName()), C.newMap(
//                        "taskId", newNode.getInstanceId(),
//                        "taskName", instance.getTitle(),
//                        "nodeId", newNode.getId(),
//                        "nodeName", newNode.getNodeName())));
//                break;
//            case logic:
//                break;
//            case end:
//                notices.add(noticeService.makeNotice(SysNotice.Type.WORKFLOW, instance.getDealUserId(), String.format("你的任务 %s 已经结束", instance.getTitle()), C.newMap(
//                        "taskId", newNode.getInstanceId(),
//                        "taskName", instance.getTitle()
//                )));
//                break;
//        }
//
//        noticeService.saveNotices(notices);
//    }
//
//
//    private Object runJSCode(WorkflowInstance instance, WorkflowNodeInstance currentNode, String behavior) throws ScriptException {
//        SimpleScriptContext context = prepareJSContext(instance, currentNode);
//        return engine.eval(behavior, context);
//    }
//
//    private SimpleScriptContext prepareJSContext(WorkflowInstance instance, WorkflowNodeInstance currentNode) {
//        SimpleScriptContext context = new SimpleScriptContext();
//        context.setAttribute("tools", new JSInterface(instance, currentNode), ScriptContext.ENGINE_SCOPE);
//        buildJSLibrary(context);
//        return context;
//    }
//
//    private void buildJSLibrary(ScriptContext context) {
//        try {
//            engine.eval(cache.getBehaviorString(), context);
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public synchronized void runLogicNode(WorkflowInstance instance, WorkflowNodeInstance node) {
//        String key = "logic_node_" + node.getId();
//        try {
//            JSONObject logicNode = getCurrentNodeModelNode(node.getId()).orElse(null);
//            if (null == logicNode) {
//                return;
//            }
//
//            //检查上一次执行是否在时间范围内
////            Optional<TaskCheckLog> optional = taskCheckLogDao.findFirstByTypeAndLinkIdOrderByLastCheckDateDesc(TaskCheckLog.Type.LOGIC_NODE, node.getId());
////            if (optional.isPresent()) {
////                if (new Date().getTime() - optional.get().getLastCheckDate().getTime() < logicNode.getInteger("interval") * 1000) {
////                    return;
////                }
////            }
//            //分布锁
////            if (utils.isLockingOrLockFailed(key, 5)) {
////                return;
////            }
//
//            SimpleScriptContext context = prepareJSContext(instance, node);
//            Object obj = engine.eval(logicNode.getString("condition"), context);
//            //如果没取到值的情况, 等待下一次检测
//            if (obj == null) {
//                delayRunLogicNodeTask(instance, node);
//            } else {
//                String behavior = logicNode.getJSONObject("result").getString(String.valueOf(obj));
//                //都没有命中的情况下, 走else
//                if (behavior == null && logicNode.getJSONObject("result").containsKey("else")) {
//                    behavior = logicNode.getJSONObject("reuslt").getString("else");
//                }
//                if (behavior != null) {
//                    engine.eval(behavior, context);
//                }
//                saveWorkflowInstance(instance);
//            }
//        } catch (ScriptException e) {
//            e.printStackTrace();
//
////            delayRunLogicNodeTask(node.getInstance(), node);
//        } finally {
////            utils.unlock(key);
//        }
//    }
//
//    private void delayRunLogicNodeTask(WorkflowInstance instance, WorkflowNodeInstance node) {
//        //只插入这次的检查记录即可
//        TaskCheckLog checkLog = new TaskCheckLog();
//        checkLog.setLinkId(node.getId());
//        checkLog.setType(TaskCheckLog.Type.LOGIC_NODE);
//        taskCheckLogDao.save(checkLog);
//    }
//
//
//    /**
//     * check if values are legal using JSR-303
//     * when failed, throw RestException with error messages
//     *
//     * @param rules
//     * @param value
//     * @return
//     */
//    private String validField(JSONArray rules, String value) {
//        final String ERROR_MESSAGE = "message";
//        List<String> errorMessages = rules.stream().map(item -> {
//            JSONObject rule = (JSONObject) item;
//            boolean flag = false;
//            boolean notEmpty = null != value && !StringUtils.isEmpty(value);
//            switch (WorkflowModel.FieldRule.valueOf(rule.getString("rule"))) {
//                case Null:
//                    if (notEmpty) {
//                        flag = true;
//                    }
//                    break;
//
//                case NotNull:
//                    if (notEmpty) {
//                        flag = true;
//                    }
//                    break;
//
//                case NotEmpty:
//                    if (notEmpty) {
//                        flag = true;
//                    }
//                    break;
//
//                case AssertTrue:
//                    if (notEmpty && value.equals("true")) {
//                        flag = true;
//                    }
//                    break;
//
//                case AssertFalse:
//                    if (notEmpty && value.equals("false")) {
//                        flag = true;
//                    }
//                    break;
//
//                case Min:
//                    if (NumberUtils.isNumber(value) && NumberUtils.toInt(value, 0) >= rule.getInteger("value")) {
//                        flag = true;
//                    }
//                    break;
//
//                case Max:
//                    if (NumberUtils.isNumber(value) && NumberUtils.toInt(value, 0) <= rule.getInteger("value")) {
//                        flag = true;
//                    }
//                    break;
//
//                case DecimalMax:
//                    if (NumberUtils.isNumber(value) && NumberUtils.toDouble(value, 0.00) >= rule.getDouble("value")) {
//                        flag = true;
//                    }
//                    break;
//
//                case DecimalMin:
//                    if (NumberUtils.isNumber(value) && NumberUtils.toDouble(value, 0.00) <= rule.getDouble("value")) {
//                        flag = true;
//                    }
//                    break;
//
//                case Past:
//                    break;
//
//                case Future:
//                    break;
//
//                case Length:
//                    if (notEmpty && value.length() <= rule.getInteger("value")) {
//                        flag = true;
//                    }
//                    break;
//                case Size:
//                    Integer min = rule.getInteger("min");
//                    if (null == min) min = 0;
//                    Integer max = rule.getInteger("max");
//                    if (null == max) max = 999;
//                    if (notEmpty && value.length() >= min && value.length() <= max) {
//                        flag = true;
//                    }
//                    break;
//
//                case Pattern:
//                    if (notEmpty) {
//                        Pattern p = Pattern.compile(rule.getString("value"));
//                        Matcher m = p.matcher(value);
//                        if (m.find()) {
//                            flag = true;
//                        }
//                    }
//                    break;
//
//            }
//            if (!flag) {
//                return rule.getString(ERROR_MESSAGE);
//            } else {
//                return "";
//            }
//        }).filter(item -> !org.apache.commons.lang.StringUtils.isEmpty(item)).collect(Collectors.toList());
//        return org.apache.commons.lang.StringUtils.join(errorMessages.toArray(), "\n");
//    }
//
//    private WorkflowInstance fromInputNodeToGo(final long uid, WorkflowInstance instance, WorkflowNodeInstance currentNode, JSONObject nodeModel) {
//        //检查所有字段, 如果有必填字段没有填写, 那么抛出异常
//        JSONObject kv = getNodeAttributes(uid, instance.getId(), currentNode.getId());
//        //追加固有属性
////        kv.putAll(sqlManager.selectSingle("workflow.selectInsAttrMapUL", C.newMap("keys")));
//        //check if some fields are required
//        for (Map.Entry<String, Object> content : nodeModel.getJSONObject("content").entrySet()) {
//            JSONObject obj = (JSONObject) content.getValue();
//            if(obj.getBoolean("required") && S.blank(kv.getString(content.getKey()))){
//                throw new RestException(String.format("%s字段没有填写", obj.getString("cname")));
//            }
//        }
//
//        //标记为已处理节点
//        swithDealType(uid, currentNode.getId(), WorkflowNodeInstanceDealer.Type.OVER_DEAL);
//
//        WorkflowNode nextNode = null;
//        //如果存在gonext
//        if(nodeModel.containsKey("goNext")){
//            String nextNodeName = null;
//            for (Object obj : nodeModel.getJSONArray("goNext")) {
//                JSONObject next = (JSONObject) obj;
//                String ca = next.getString("case");
//                try {
//                    //做成环境
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(S.fmt("(function(){ var kv = %s; ", JSON.toJSONString(kv)));
//                    sb.append(S.fmt("with(kv){ return %s; }", ca));
//                    sb.append("})()");
//                    //处理表达式
//                    boolean flag = (boolean) JsEngine.eval(sb.toString());
//                    if (flag) {
//                        nextNodeName = next.getString("go");
//                        break;
//                    }
//                } catch (Exception e) {
//                    continue;
//                }
//            }
//            if(S.empty(nextNodeName)){
//                throw new RestException("没有合适的节点跳转");
//            }
//            nextNode = nodeDao.findTopByModelIdAndName(instance.getModelId(), nextNodeName).orElse(null);
//            if(null == nextNode){
//                throw new RestException(S.fmt("无法找到名为%s的节点", nextNodeName));
//            }
//            goNextNode(instance,currentNode, nextNode);
//        }
//        else{
//            //找不到下一个就结束了吧
//            nextNode = findNextNodeModel(instance.getWorkflowModel(), currentNode.getNodeModel()).orElse(null);
//            if (null == nextNode) return saveWorkflowInstance(instance);
//            goNextNode(instance, currentNode, nextNode);
//        }
//        return saveWorkflowInstance(instance);
//    }
//
//    private WorkflowInstance fromCheckNodeToGo(long uid, WorkflowInstance instance, WorkflowNodeInstance currentNode, JSONObject nodeModel) {
//        //检查当前角色是否已经提交信息
//        if (attributeDao.countByNodeInstanceIdAndDealUserIdAndAttrKey(currentNode.getId(), uid, nodeModel.getString("key")) == 0) {
//            throw new RestException("你还没有提交信息");
//        }
//
//        JSONObject state = (JSONObject) nodeModel.getJSONArray("states")
//                .stream()
//                .filter(obj -> {
//                    if (!(obj instanceof JSONObject)) {
//                        return false;
//                    }
//                    return attributeDao.countByNodeInstanceIdAndAttrKeyAndAttrValue(currentNode.getId(), nodeModel.getString("key"), ((JSONObject) obj).getString("item")) >= ((JSONObject) obj).getInteger("condition");
//                })
//                .findFirst()
//                .orElse(null);
//        //如果没找到, 则标记为OVER_DEAL
//        if (null == state) {
//            swithDealType(uid, currentNode.getId(), WorkflowNodeInstanceDealer.Type.OVER_DEAL);
//        } else {
//            Optional.of(state)
//                    .map(item -> ((JSONObject) item).getString("behavior"))
//                    .filter(StringUtils::isNotEmpty)
//                    .filter(behavior -> {
//                        try {
//                            runJSCode(instance, currentNode, behavior);
//                            swithDealType(uid, currentNode.getId(), WorkflowNodeInstanceDealer.Type.OVER_DEAL);
//                            return true;
//                        } catch (ScriptException e) {
//                            e.printStackTrace();
//                            return false;
//                        }
//                    })
//                    .orElseThrow(new RestException("节点跳转失败, 请检查工作流配置"));
//
//        }
//        return instanceDao.save(instance);
//    }
//
//    /**
//     * set a dealer type to target type
//     *
//     * @param uid
//     * @param nid
//     * @param targetType
//     */
//    private void swithDealType(final long uid, final long nid, final WorkflowNodeInstanceDealer.Type targetType) {
//        nodeInstanceDealersDao.findTopByUserIdAndNodeInstanceId(uid, nid).ifPresent(dealer -> {
//            dealer.setType(WorkflowNodeInstanceDealer.Type.OVER_DEAL);
//            dealer.setLastModify(new Date());
//            nodeInstanceDealersDao.save(dealer);
//        });
//    }
//
//    /***js binding**/
//    @AllArgsConstructor
//    public class JSInterface {
//        private WorkflowInstance instance;
//        private WorkflowNodeInstance currentNode;
////        private Long instanceId;
//
//        public void go(String nodeName) {
//            WorkflowNode target = findNode(instance.getWorkflowModel(), nodeName).orElse(null);
//            if (null == target) return;
//            goNextNode(instance, currentNode, target);
//        }
//
//
//        /**
//         * 开启后续任务
//         *
//         * @param modelName  模型名
//         * @param dealerId   执行人ID
//         * @param innateData 固有信息
//         */
//        public void start_next_task(String modelName, long dealerId, Map<String, String> innateData) {
//            //查找该模型的最新版本
////            WfModel model = modelDao.findFirstByModelNameAndOpenIsTrueOrderByVersionDesc(modelName).orElse(null);
//            long modelId = 0L;
//            List<Long> ids = modelDao.findModelId(modelName);
//            if (ids.size() > 0) {
//                modelId = ids.get(0);
//            }
////            long modelId = modelDao.findModelId(modelName).orElse(0L);
//            if (0 == modelId) return;
////            if(null == model) return;
//            //
//            if (0 == dealerId) return;
//            //
//            if (null == innateData) {
//                innateData = new HashMap<>();
//            }
//
//            Map<String, String> data = convertInnates(instance.getAttributes());
//            data.putAll(innateData);
//
//            ApplyTaskRequest applyTaskRequest = new ApplyTaskRequest();
//            applyTaskRequest.setModelId(modelId);
//            applyTaskRequest.setCommon(false);
//            applyTaskRequest.setAuthCreated(true);
//            applyTaskRequest.setDealerId(dealerId);
//            applyTaskRequest.setTitle(modelName);
//            applyTaskRequest.setData(data);
//            applyTaskRequest.setDataId(instance.getBillNo());
//            applyTaskRequest.setDataSource(ApplyTaskRequest.DataSource.ACC_LOAN);
//            applyTaskRequest.setGoNext(false);
//            WorkflowInstance newInstance = (WorkflowInstance) startNewInstance(dealerId, applyTaskRequest).orElse(null);
//            if (null != newInstance) {
//                logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), dealerId, "创建了后续任务 " + applyTaskRequest.getTitle());
//                newInstance.setPrevInstanceId(instance.getId());
//                instanceDao.save(newInstance);
//            }
//        }
//
//        public long get_task_dealer_id() {
//            return instance.getDealUserId();
//        }
//
//        public Map<String, String> get_task_innate_data() {
//            return instance.getAttributes().stream().filter(item -> item.getType().equals(WorkflowInstanceAttribute.Type.INNATE)).collect(Collectors.toMap(item -> item.getAttrKey(), item -> item.getAttrValue()));
//        }
//
//        public Map getLastNode(String nodeName) {
//            int idex = instance.getNodeList().indexOf(currentNode);
//            WorkflowNodeInstance nodeInstance = null;
//            while (idex-- > 0) {
//                nodeInstance = instance.getNodeList().get(idex);
//                if (nodeInstance.getNodeName().equals(nodeName)) {
//                    break;
//                }
//                nodeInstance = null;
//            }
//            if (nodeInstance == null) {
//                return null;
//            }
//            Map<String, String> ret = new HashMap();
//            nodeInstance.getAttributeList().forEach(attribute -> {
//                ret.put(attribute.getAttrKey(), attribute.getAttrValue());
//            });
//            return ret;
//        }
//    }
//
//
//    /**
//     * 检查用户是否可以处理这个节点
//     *
//     * @param nodeInstance
//     * @param user
//     * @return
//     */
//    public boolean checkNodeAuth(final WorkflowNodeInstance nodeInstance, final User user) {
//        return checkNodeAuth(nodeInstance.getId(), user.getId());
//    }
//
//    public boolean checkNodeAuth(final long nodeInstanceId, final long uid) {
//        return nodeInstanceDealersDao.countByTypeInAndNodeInstanceIdAndUserId(Arrays.asList(WorkflowNodeInstanceDealer.Type.CAN_DEAL, WorkflowNodeInstanceDealer.Type.DID_DEAL), nodeInstanceId, uid) > 0;
//    }
//
//    private Map<String, String> convertInnates(List<WorkflowInstanceAttribute> attributes) {
//        return attributes.stream().filter(item -> item.getType().equals(WorkflowInstanceAttribute.Type.INNATE))
//                .collect(Collectors.toMap(item -> item.getAttrKey(), item -> item.getAttrValue()));
//    }
//
//
//    public WorkflowInstance saveWorkflowInstance(WorkflowInstance workflowInstance) {
//        return instanceDao.save(workflowInstance);
//    }
//
//    public Optional<WorkflowNode> findNextNodeModel(WorkflowModel model, WorkflowNode nodeModel) {
//        if (nodeModel.isEnd()) return Optional.ofNullable(nodeModel);
//        if (nodeModel.getNext().size() == 0) return Optional.empty();
//        if (nodeModel.getType().equals(WorkflowNode.Type.input)) {
//            String nextName = nodeModel.getNext().get(0);
//            return model.getNodeModels().stream().filter(nm -> {
//                return nm.getName().equals(nextName);
//            }).findAny();
//        }
//        return Optional.empty();
//
//    }
//
//    /**
//     * 查找一个任务的实例
//     *
//     * @param uid 查找人ID
//     * @param id  任务ID
//     * @return
//     */
//    public JSONObject fetchInstanceObject(long uid, long id){
//        User user = userService.findUser(uid);
//        //任务主体
//        WorkflowInstance instance = findInstance(id);
//        return (JSONObject) JSON.toJSON(instance);
////        return new JSONObject();
//    }
//
//    public FetchWorkflowInstanceResponse fetchInstance(long uid, long id) {
//        User user = userService.findUser(uid);
//        if (null == user) return null;
//        WorkflowInstance workflowInstance = findInstance(id);
//        workflowInstance.setNodeList(workflowInstance.getNodeList().stream().sorted((a, b) -> {
//            return (int) (a.getId() - b.getId());
//        }).collect(Collectors.toList()));
//        JSONObject instance = (JSONObject) JSON.toJSON(workflowInstance);
//        instance.getJSONArray("nodeList").
//                stream()
//                .forEach(nobj -> {
//                    JSONObject ni = (JSONObject) nobj;
//                    //处理属性
//                    JSONArray attrs = ni.getJSONArray("attributeList");
//                    ni.put("attrs", ni.getJSONArray("dealers")
//                            .stream()
//                            .filter(dobj -> {
//                                JSONObject dealer = (JSONObject) dobj;
//                                WorkflowNodeInstanceDealer.Type type = dealer.getObject("type", WorkflowNodeInstanceDealer.Type.class);
//                                return type.equals(WorkflowNodeInstanceDealer.Type.DID_DEAL) || type.equals(WorkflowNodeInstanceDealer.Type.OVER_DEAL);
//                            })
//                            .map(dobj -> {
//                                JSONObject dealer = (JSONObject) dobj;
//                                dealer.put("attrs", attrs
//                                        .stream()
//                                        .filter(aobj -> {
//                                            JSONObject attr = (JSONObject) aobj;
//                                            return attr.getLong("dealUserId").equals(dealer.getLong("userId"));
//                                        })
//                                        .collect(Collectors.toList())
//                                );
//
//                                return new Object[]{dealer.getLong("userId") + "", dealer};
//                            })
//                            .collect(Collectors.toMap(item -> item[0], item -> item[1], (v1, v2) -> v2))
//                    );
//                    ni.put("dealers", null);
//                    ni.put("attributeList", null);
//
//
//                    //处理文件
//                    if (StringUtils.isNotBlank((String) ni.get("files"))) {
//                        String fs = (String) ni.get("files");
//                        List<SystemFile> files = Arrays.stream(fs.split(","))
//                                .map(fid -> systemFileDao.findById(Long.valueOf(fid)).orElse(null)).filter(item -> null != item)
//                                .collect(Collectors.toList());
//                        for (SystemFile file : files) {
//                            if (null == file.getExprTime() || file.getExprTime().before(new Date())) {
//                                file.setToken(UUID.randomUUID().toString());
//                                file.setExprTime(new Date(System.currentTimeMillis() + 3600000));
//                                systemFileDao.save(file);
//                            }
//                            //延后1个小时
//                            else if (file.getExprTime().after(new Date())) {
//                                file.setExprTime(new Date(System.currentTimeMillis() + 3600000));
//                                systemFileDao.save(file);
//                            }
//                        }
//
//                        ni.put("fileList", files);
//                    }
//                });
//        FetchWorkflowInstanceResponse response = new FetchWorkflowInstanceResponse();
//        response.setInstance(instance);
//        response.setCurrentNodeModel(workflowInstance.getCurrentNodeInstance().getNodeModel());
//        response.setDeal(canDeal(workflowInstance.getId(), user.getId()));
//        response.setCancel(canCancel(workflowInstance, user));
//        response.setRecall(canRecall(workflowInstance, user));
//        response.setTransform(canTransform(workflowInstance, user));
//        response.setAccept(canAccept(workflowInstance, uid));
////            response.setLogs(systemTextLogDao.findLogs(SystemTextLog.Type.WORKFLOW, id));
//        response.setTransformUsers(getTransformUids(workflowInstance.getId()));
//
//        return response;
//    }
//
//
//    public Optional<WorkflowNode> findFirstNodeModel(long modelId) {
//        return nodeDao.findFirstByModel_IdAndStartIsTrue(modelId);
//    }
//
//    public Optional<WorkflowNode> findNode(WorkflowModel model, String nodeName) {
//        return nodeDao.findFirstByModelAndName(model, nodeName);
//    }
//
//
//    public Optional<WorkflowModel> findModel(long id) {
//        return modelDao.findById(id);
//    }
//
//
//    public WorkflowNode findNode(long id) {
//        return nodeDao.findById(id).orElseThrow(new RestException("找不到id为" + id + "的模型节点"));
//    }
//
//    public Optional<WorkflowNodeInstance> findNodeInstance(long id) {
//        return nodeInstanceDao.findById(id);
//    }
//
//    public Optional<WorkflowNodeFile> findNodeFile(long id) {
//        return nodeFileDao.findById(id);
//    }
//
//    public Optional<GPSPosition> findNodeGPSPosition(long nodeFileId) {
//        return findNodeFile(nodeFileId)
//                .filter(nodeFile -> nodeFile.getType().equals(WorkflowNodeFile.Type.POSITION))
//                .map(nodeFile -> gpsPositionDao.getOne(nodeFile.getFileId()));
//    }
//
//    public int countByModelNameAndBillNo(String modelName, String BILL_NO) {
//        return 0;
////        return instanceDao.countByModelNameAndBillNo(modelName, BILL_NO);
//    }
//
//    public int countByModelNameAndBillNoAndStateNotIn(String modelName, String BILL_NO, Collection<WorkflowInstance.State> states) {
//        return 0;
////        return instanceDao.countByModelNameAndBillNoAndStateNotIn(modelName, BILL_NO, states);
//    }
//
//    /**
//     * 查找一个任务
//     *
//     * @param id 任务ID
//     * @return 任务实例
//     */
//    public WorkflowInstance findInstance(final long id) {
//        return instanceDao.findById(id).orElseThrow(new RestException("找不到id为" + id + "的任务"));
//    }
//
//    /**
//     * 查找一个公共任务
//     *
//     * @param id 任务ID
//     * @return 任务实例
//     */
//    public Optional<WorkflowInstance> findCommonInstance(final long id) {
//        return instanceDao.findById(id).filter(instance -> instance.getState().equals(COMMON));
//    }
//
//
//    /************ 节点提交 *************/
//
//    private JSONObject buildInputNode(WorkflowModel workflowModel, Map v) {
//        JSONObject result = new JSONObject();
//        JSONObject cnt = new JSONObject();
//        result.put("content", cnt);
//        Map<String, Object> content = (Map) v.getOrDefault("content", C.newMap());
//        for (Map.Entry<String, Object> entry : content.entrySet()) {
//            String ck = entry.getKey();
//            Object cv = entry.getValue();
//
//            JSONObject object = new JSONObject();
//            if (!(cv instanceof Map)) {
//                continue;
//            }
//
//            Map cvmap = (Map) cv;
//            object = (JSONObject) JSON.toJSON(cv);
//
//            //ename
//            object.put("ename", object.getString("name"));
//            object.remove("name");
//            object.put("cname", String.valueOf(ck));
//
//            //固有字段
//            boolean innate = (boolean) cvmap.getOrDefault("innate", false);
//            if (innate) {
//                WorkflowModelInnate workflowModelInnate = new WorkflowModelInnate();
//                workflowModelInnate.setContent(object);
//                workflowModelInnate.setFieldName(object.getString("ename"));
//                workflowModelInnate.setModel(workflowModel);
//                workflowModel.getInnates().add(workflowModelInnate);
//                continue;
//            }
//
//            cnt.put(object.getString("ename"), object);
//        }
//        //gonext
//        if(v.containsKey("goNext")){
//            result.put("goNext", v.get("goNext"));
//        }
//        return result;
//    }
//
//    private JSONObject buildCheckNode(Map v) {
//        return (JSONObject) JSON.toJSON(v);
//    }
//
//    private JSONObject buildLogicNode(Map v) {
//        return (JSONObject) JSON.toJSON(v);
//    }
//
//    private JSONObject buildNormalNode(Map v) {
//        return (JSONObject) JSON.toJSON(v);
//    }
//
//
//    public WorkflowNodeInstance submitInputNode(long uid, WorkflowNodeInstance nodeInstance, Map<String, Object> data) {
//        //当前处理的
////        WorkflowNodeInstance nodeInstance = nodeInstanceDao.findFirstByIdAndFinishedIsFalse(nodeInstanceId).orElse(null);
////        if(null == nodeInstance){
////            return;
////        }
//        JSONObject node = getCurrentNodeModelNode(nodeInstance.getId()).orElse(null);
//        if (null == node) {
//            return null;
//        }
//        //TODO: 权限校验
//        JSONObject content = node.getJSONObject("content");
//        for (Map.Entry<String, Object> entry : content.entrySet()) {
//            JSONObject v = (JSONObject) entry.getValue();
//            String attrKey = v.getString("ename");
//            if (!data.containsKey(attrKey)) {
//                continue;
//            }
//            //不论是否必填, 空属性就略过
////            if (StringUtils.isEmpty(String.valueOf(data.get(attrKey)))) {
////                continue;
////            }
//            String value = String.valueOf(data.get(attrKey));
//            //验证属性格式
//            //校验该字段的规则
//            //只要填写了就校验
//            if (!StringUtils.isEmpty(value)) {
//                if (v.containsKey("rules")) {
//                    try {
//                        String err = validField(v.getJSONArray("rules"), value);
//                        if (!StringUtils.isEmpty(err)) {
//                            throw new RestException(err);
//                        }
//                    } catch (Exception e) {
//                        throw new RestException(v.getString("cname") + "格式校验错误");
//                    }
//                }
//            }
//
//            //覆盖旧节点的信息
//            WorkflowNodeAttribute attribute = addAttribute(uid, nodeInstance, attrKey, String.valueOf(data.get(attrKey)), v.getString("cname"));
//            attributeDao.save(attribute);
//            nodeInstance.getAttributeList().add(attribute);
//        }
//        return nodeInstanceDao.save(nodeInstance);
//    }
//
//    public WorkflowNodeInstance submitCheckNode(long uid, WorkflowNodeInstance nodeInstance, Map data) {
//        JSONObject node = getCurrentNodeModelNode(nodeInstance.getId()).orElse(null);
//        if (null == node) {
//            throw new RestException("找不到该节点");
//        }
//        String item = String.valueOf(data.get(node.getString("key")));
//        String ps = String.valueOf(data.get(node.getString("ps")));
//        //如果可选项不在选项里面, 那么无视
//        boolean hasOption = node.getJSONArray("states")
//                .stream()
//                .anyMatch(state -> ((JSONObject) state).getString("item").equals(item));
//        if (!hasOption) {
//            throw new RestException("选项内没有这个选项");
//        }
//        //每个审批节点只允许审批一次
//        WorkflowNodeAttribute attribute = addAttribute(uid, nodeInstance, node.getString("key"), item, node.getString("question"));
//        attributeDao.save(attribute);
//
//        //如果填写了审核说明
//        //valueof会格式化null
//        if (ps.equals("null")) {
//            ps = "";
//        }
//        attribute = addAttribute(uid, nodeInstance, node.getString("ps"), ps, "审核说明");
//        attributeDao.save(attribute);
//        return nodeInstance;
//    }
//
//
//    /**
//     * 添加节点数据
//     *
//     * @param uid
//     * @param nodeInstance
//     * @param key
//     * @param value
//     * @param cname
//     * @return
//     */
//    public WorkflowNodeAttribute addAttribute(final Long uid, WorkflowNodeInstance nodeInstance, String key, String value, String cname) {
//        WorkflowNodeAttribute attribute = attributeDao.findTopByNodeInstanceIdAndDealUserIdAndAttrKey(nodeInstance.getId(), uid, key).orElse(new WorkflowNodeAttribute());
//        attribute.setDealUserId(uid);
//        attribute.setAttrKey(key);
//        attribute.setAttrValue(value == null ? "" : value);
//        attribute.setNodeInstance(nodeInstance);
//        attribute.setNodeInstanceId(nodeInstance.getId());
//        attribute.setAttrCname(cname);
//        attribute.setDealUserId(uid);
//        return attribute;
//    }
//
//
//    /*** 工具类函数 ***/
//
//
//    /**
//     * 当前正在执行的模型工作流节点模型
//     *
//     * @param nodeInstanceId
//     * @return
//     */
//    public Optional<JSONObject> getCurrentNodeModelNode(long nodeInstanceId) {
//        String hql = "select ni.nodeModel.node from WorkflowNodeInstance ni where ni.id = :id and ni.finished = false";
//        List result = entityManager.createQuery(hql)
//                .setParameter("id", nodeInstanceId)
//                .setMaxResults(1)
//                .getResultList();
////                .getSingleResult();
//        JSONObject ret = result.size() > 0 ? (JSONObject) result.get(0) : null;
//        return Optional.ofNullable(ret);
//    }
//
//    /**
//     * 得到可以指派移交的用户ID
//     *
//     * @param instanceId
//     * @return
//     */
//    public List<Long> getTransformUids(long instanceId) {
//        String sql = "select distinct user.id from GlobalPermission gp, User user, WfIns ins " +
//                "left join user.quarters uq " +
//                "where ins.id = :instanceId and gp.objectId = ins.modelId and gp.type = 'WORKFLOW_PUB' and " +
//                "(" +
//                "(gp.userType = 'QUARTER' and uq.id = gp.linkId and " +
//                "(" +
//                "(select count(d) from Department d where uq.department.code like concat(d.code,'%') and d.id = ins.depId) > 0 or " +
//                "(uq.departmentId = ins.depId) or " +
//                "(select count(d) from Department d where d.code like concat(uq.department.code,'%') and d.id = ins.depId) > 0" +
//                ") " +
//                "))";
//        return sqlUtils.hqlQuery(sql, C.newMap(
//
//                "instanceId", instanceId
//        )).stream()
//                .map(item -> (Long) item)
//                .collect(Collectors.toList());
//    }
//
//    public List getPubPointUids(final long uid, final long modelId) {
//        User user = userService.findUser(uid);
//        List<Long> dids = user.getQuarters().stream()
//                .filter(Quarters::isManager)
//                .map(Quarters::getDepartmentId)
//                .distinct()
//                .collect(Collectors.toList());
//        if (dids.size() == 0) {
//            return new ArrayList();
//        }
//        return sqlUtils.query("SELECT DISTINCT uid as id,uname as trueName,phone,username FROM t_global_permission_center gpc WHERE gpc.type = ? AND gpc.did IN ? and gpc.object_id = ?", "WORKFLOW_PUB", dids, modelId).stream()
//                .map(item -> {
//                    return C.newMap(
//                            "id", item.get("ID"),
//                            "phone", item.get("PHONE"),
//                            "trueName", item.get("TRUENAME"),
//                            "username", item.get("USERNAME")
//                    );
//                }).collect(Collectors.toList());
//    }
//
//
//    public List<JSONObject> getNodeDealUids(final long instanceId, final String nodeName) {
//        return workflowService2.getNodeDealUids(instanceId,nodeName);
////        WfIns instance = findInstance(instanceId);
////        WfNode node = nodeDao.findTopByModelIdAndName(instance.getModelId(), nodeName).orElse(null);
////        if (null == node) {
////            return new ArrayList<>();
////        }
////        List<Map<String, String>> objects = sqlUtils.query("select * from t_global_permission_center where type = 'WORKFLOW_MAIN_QUARTER' and object_id = ?", node.getId());
////        //过滤不合法的条件
////        Map<Long, Integer> cacheMap = C.newMap();
////        objects = objects.stream().filter(obj -> {
////            switch (GlobalPermission.UserType.valueOf(obj.get("USER_TYPE"))) {
////                case QUARTER:
////                    //如果只配了一个部门, 那么直接通过
////                    Integer cacheNum = cacheMap.get(node.getId());
////                    if (null == cacheNum) {
////                        String sql = "select count(distinct id) as NUM from t_global_permission where type = 'WORKFLOW_MAIN_QUARTER' and object_id = ?";
////                        List<Map<String, String>> objss = sqlUtils.query(sql, node.getId());
////                        cacheNum = Integer.valueOf(objss.get(0).get("NUM"));
////                        cacheMap.put(node.getId(), cacheNum);
////                    }
////                    //因为被格式化成了字符串
////                    if (cacheNum == 1) {
////                        return true;
////                    }
////                    //不在同一条线上的, pass
////                    Long depId = Long.valueOf(obj.get("DID"));
////                    if (!userService.isChildDepartment(depId, instance.getDepId()) && !userService.isChildDepartment(instance.getDepId(), depId)) {
////                        return false;
////                    }
////                    break;
////            }
////            return true;
////        }).collect(Collectors.toList());
////        //强制施加规则, 如果该节点只允许一个人处理, 且任务执行人拥有这个节点的权限, 那么直接确立为该用户执行
////        //如果存在多人执行的情况, 那么仍然确认该用户, 且保存多余的可执行人
////        if (node.getMaxPerson() == 1) {
////            List newObjects = objects
////                    .stream()
////                    .filter(item -> (Long.valueOf(item.get("UID"))).equals(instance.getDealUserId()))
////                    .collect(Collectors.toList());
////            if (newObjects.size() > 0) {
////                objects = newObjects;
////            }
////        }
////        return objects;
//    }
//
//    private JSONObject getNodeAttributes(long uid, long iid, long nid){
//        List<JSONObject> attrs = sqlManager.select("workflow.selectNodeAttrUL", JSONObject.class, C.newMap("uid",uid,"nid", nid, "iid", iid));
//        JSONObject ret = new JSONObject();
//        for (JSONObject attr : attrs) {
//            ret.put(attr.getString("ATTR_KEY"), attr.getString("ATTR_VALUE"));
//        }
//        return ret;
//    }
//
//    public Page getPubUsers(final long uid, final long modelId) {
//        //TODO: BUG
//        List uids = getPubPointUids(uid, modelId);
//        PageRequest pageRequest = PageRequest.of(0, 2000);
//        return new PageImpl(uids, pageRequest, uids.size());
//    }
//
//
//    /******** 内部类 *********/
//    @Data
//    public static class ModelFieldRequest {
//        @NotEmpty
//        String name;
//        @NotNull
//        WorkflowModelField.Type type;
//        String hint;
//
//        JSONObject ext = new JSONObject();
//    }
//}
