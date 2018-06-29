package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.util.SqlUtils;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.mobile.request.ApplyTaskRequest;
import com.beeasy.hzback.modules.mobile.request.SubmitDataRequest;
import com.beeasy.hzback.modules.mobile.request.WorkflowPositionAddRequest;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.node.*;
import com.beeasy.hzback.modules.system.response.FetchWorkflowInstanceResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

//import com.beeasy.hzback.core.helper.Rest;

@Slf4j
@Service
@Transactional
public class WorkflowService {

    @Value("${workflow.timeStep}")
    private int timeStep = 5;

    @Value("permission.type")
    private String permissionType;

    @Autowired
    IDownloadFileTokenDao downloadFileTokenDao;
    @Autowired
    SqlUtils sqlUtils;
    @Autowired
    IWorkflowInstanceTransactionDao transactionDao;
    @Autowired
    IGlobalPermissionDao globalPermissionDao;
    @Autowired
    IWorkflowInstanceAttributeDao instanceAttributeDao;
    @Autowired
    IWorkflowModelInnateDao innateDao;
    @Autowired
    EntityManager entityManager;
    @Autowired
    IGPSPositionDao gpsPositionDao;
    @Autowired
    IWorkflowNodeFileDao nodeFileDao;
    @Autowired
    ISystemFileDao systemFileDao;
    //    @Autowired
//    IWorkflowExtPermissionDao extPermissionDao;
    @Autowired
    Utils utils;
    @Autowired
    ScriptEngine engine;

    @Autowired
    IWorkflowModelDao modelDao;

    @Autowired
    IWorkflowInstanceDao instanceDao;

    @Autowired
    IWorkflowNodeInstanceDao nodeInstanceDao;

    @Autowired
    IWorkflowNodeDao nodeDao;

//    @Autowired
//    IWorkflowModelPersonsDao personsDao;

    @Autowired
    ITaskCheckLogDao taskCheckLogDao;

    @Autowired
    IWorkflowNodeAttributeDao attributeDao;

    @Autowired
    UserService userService;

    @Autowired
    SystemConfigCache cache;

    @Autowired
    ISystemTaskDao systemTaskDao;
//    @Autowired
//    IInspectTaskDao inspectTaskDao;

    @Autowired
    ScriptEngine scriptEngine;
    @Autowired
    ISystemTextLogDao systemTextLogDao;
    @Autowired
    SystemTextLogService logService;
//    @Autowired
//    WorkflowModelStart.Dao startDao;
//    @Autowired
//    WorkflowModelStart.QuartersDao quartersDao;


    /**
     * 开始一个新的工作流任务
     *
     * @param uid
     * @return
     */
    public Result<WorkflowInstance> startNewInstance(Long uid, ApplyTaskRequest request) {
        User pubUser = uid != null ? userService.findUser(uid).orElse(null) : null;
        WorkflowModel workflowModel = findModel(request.getModelId()).orElse(null);
        if (null == workflowModel || !workflowModel.isOpen()) {
            return Result.error("工作流没有开启");
        }

        //如果是手动任务, 但是
        if (request.isManual() && !workflowModel.isManual()) {
            return Result.error("该任务不能手动开启");
        }

        User dealerUser = null;
        //任务主体
        WorkflowInstance workflowInstance = new WorkflowInstance();

        //公共任务
        if (request.isCommon()) {
            if (!canPoint(workflowModel.getId(), pubUser)) {
                return Result.error("你无权发布公共任务");
            }
            workflowInstance.setDealUserId(null);
        }
        //不是公共任务的情况下,指定任务执行人
        else {
            dealerUser = userService.findUser(request.getDealerId()).orElse(null);
            //如果仍然为空,那么报错
            if (null == dealerUser) {
                return Result.error("找不到任务执行人");
            }
            //如果是给自己执行
            if (dealerUser.getId().equals(pubUser.getId())) {
                if (!canPub(workflowModel, pubUser)) {
                    return Result.error("你无权发布该任务");
                }
                workflowInstance.setDealUserId(dealerUser.getId());
            }
            //指派给别人执行
            else {
                if (!canPoint(workflowModel.getId(), pubUser)) {
                    return Result.error("你无权发布该任务");
                }
                workflowInstance.setDealUserId(null);
            }

            //验证执行人是否有权限
            //TODO:
        }
//        if (!request.isCommon()) {
//        }
//        if (request.getDealerId() == -1 || request.getDealerId().equals(uid)) {
//            dealerUser = pubUser;
//        }
//        //发布为公共任务
//        else if (request.getDealerId() == 0) {
//            //默认为null
//        } else {
//        }

        //如果发布人和执行人不一样, 检查是否拥有发布该任务的权利
//        if (null == dealerUser || (null != dealerUser && dealerUser.getId() != uid)) {
//            if (!canPubOrPoint(workflowModel, pubUser)) {
//                return Result.error("你无权发布该任务");
//            }
//        }
//        //如果发布了公共任务
//        if (null == dealerUser && !canPoint(workflowModel.getId(), pubUser)) {
//        }

        WorkflowNode firstNode = findFirstNodeModel(workflowModel.getId()).orElse(null);
        //非公共任务
        //检查执行人是否拥有第一个节点的处理权限
//        if (!request.isCommon()) {
//            if (!checkAuth(workflowModel, firstNode, dealerUser)) {
//                return Result.error("选择的执行人没有权限处理该任务");
//            }
//        }


        workflowInstance.setModelId(workflowModel.getId());
        workflowInstance.setTitle(request.getTitle());
        workflowInstance.setInfo(request.getInfo());
        //开始时间
        Date date = new Date();
        workflowInstance.setAddTime(date);
        if (null != request.getPlanStartTime()) {
            workflowInstance.setPlanStartTime(request.getPlanStartTime());
        } else {
            workflowInstance.setPlanStartTime(date);
        }
//        workflowInstance.setDealUser(dealerUser);
//        workflowInstance.setDealUserId(null == dealerUser ? null : dealerUser.getId());
        workflowInstance.setPubUserId(pubUser.getId());
//        workflowInstance.setPubUser(pubUser);
        workflowInstance = saveWorkflowInstance(workflowInstance);


        //滞后处理的字段
        Map<String, WorkflowModelInnate> afters = new HashMap<>();
        //固有字段检查
        List<WorkflowModelInnate> innates = workflowModel.getInnates();
        for (WorkflowModelInnate innate : innates) {
            //特殊固有字段的处理
            if (innate.getContent().getString("type").equals("taskId")) {
                afters.put(innate.getFieldName(), innate);
                continue;
            }
            if (innate.getContent().getString("type").equalsIgnoreCase("EXT_DATA")) {
                afters.put(innate.getFieldName(), innate);
                continue;
            }
            if (!request.getData().containsKey(innate.getFieldName())) {
                return Result.error("请填写对应的字段");
            }
        }

        //插入固有字段
        for (WorkflowModelInnate innate : innates) {
            if (afters.containsValue(innate)) {
                continue;
            }
            WorkflowInstanceAttribute attribute = new WorkflowInstanceAttribute();
            attribute.setInstance(workflowInstance);
            attribute.setType(WorkflowInstanceAttribute.Type.INNATE);
            attribute.setAttrCName(innate.getContent().getString("cname"));
            attribute.setAttrKey(innate.getContent().getString("ename"));
            attribute.setAttrValue(request.getData().getOrDefault(attribute.getAttrKey(), ""));
            attribute = instanceAttributeDao.save(attribute);
            workflowInstance.getAttributes().add(attribute);
        }

        if (request.isCommon()) {
            //公共任务
            workflowInstance.setState(WorkflowInstance.State.COMMON);
        } else {
            if (dealerUser.getId().equals(pubUser.getId())) {
                //任务默认处于执行中的状态
                workflowInstance.setState(WorkflowInstance.State.DEALING);
            } else {
                workflowInstance.setState(WorkflowInstance.State.PAUSE);
                //增加一条指派申请
                WorkflowInstanceTransaction transcation = new WorkflowInstanceTransaction();
                transcation.setFromState(WorkflowInstance.State.UNRECEIVED);
                transcation.setToState(WorkflowInstance.State.DEALING);
                transcation.setInstanceId(workflowInstance.getId());
                transcation.setUserId(dealerUser.getId());
                transactionDao.save(transcation);
            }
        }
        workflowInstance = saveWorkflowInstance(workflowInstance);

        //第一个执行人已经确定
//        workflowInstance.getNodeList().get(0).setDealer(dealerUser);
//        workflowInstance.getNodeList().get(0).setDealerId(null == dealerUser ? null : dealerUser.getId());

        //插入第一个节点
        WorkflowNodeInstance nodeInstance = workflowInstance.addNode(firstNode, false);
        nodeInstance.setDealerId(workflowInstance.getDealUserId());
        nodeInstanceDao.save(nodeInstance);

        //记录日志
        logService.addLog(SystemTextLog.Type.WORKFLOW, workflowInstance.getId(), uid, "发布了任务");
        if (null != dealerUser) {
            logService.addLog(SystemTextLog.Type.WORKFLOW, workflowInstance.getId(), dealerUser.getId(), "接受了任务");
        }

        //滞后处理
        if (null != workflowInstance.getId() && workflowInstance.getId() > 0) {
            for (Map.Entry<String, WorkflowModelInnate> entry : afters.entrySet()) {
                WorkflowModelInnate after = entry.getValue();
                switch (after.getContent().getString("type")) {
                    case "taskId":
                        WorkflowInstanceAttribute attribute = new WorkflowInstanceAttribute();
                        attribute.setInstance(workflowInstance);
                        attribute.setType(WorkflowInstanceAttribute.Type.INNATE);
                        attribute.setAttrCName(after.getContent().getString("cname"));
                        attribute.setAttrKey(after.getContent().getString("ename"));
                        attribute.setAttrValue(workflowInstance.getId() + "");
                        instanceAttributeDao.save(attribute);
                        break;

                }
            }
            addExtData(workflowInstance, workflowModel, request, true);
        }

        return workflowInstance.getId() == null ? Result.error() : Result.ok(workflowInstance);
    }



    public void addExtData(WorkflowInstance instance, WorkflowModel model, ApplyTaskRequest request, boolean updateFirstNode) {
        //补足系统自动产生的固有字段
        if (null != request.getDataSource()) {
            String sql = null;
            switch (model.getModelName()) {
                case "资料收集":
                    switch (request.getDataSource()) {
                        case CLIENT:
                            sql = "select CUS_ID,CUS_NAME,PHONE,CERT_TYPE,CERT_CODE from CUS_BASE where CUS_ID = ? ";
                            break;

                        case ACC_LOAN:
                            sql = "select ACC_LOAN.BILL_NO,ACC_LOAN.CONT_NO,ACC_LOAN.CUS_ID,ACC_LOAN.CUS_NAME,CUS_BASE.PHONE,CUS_BASE.CERT_TYPE,CUS_BASE.CERT_CODE,ACC_LOAN.CUS_MANAGER from ACC_LOAN left join CUS_BASE on ACC_LOAN.CUS_ID=CUS_BASE.CUS_ID where ACC_LOAN.BILL_NO = ? ";
                            break;
                    }
                    break;

                case "菜单权限申请":
                    switch (request.getDataSource()){
                        case ACC_LOAN:
                            sql = "select BILL_NO,CONT_NO,CUS_ID,CUS_NAME from ACC_LOAN where BILL_NO=?";
                            break;
                    }
                    break;

                case "贷后跟踪-小微部公司类":
                    switch (request.getDataSource()){
                        case ACC_LOAN:
                            sql = "select ACC_LOAN.BILL_NO,ACC_LOAN.CONT_NO,ACC_LOAN.CUS_ID,ACC_LOAN.CUS_NAME,ACC_LOAN.ASSURE_MEANS_MAIN,ACC_LOAN.LOAN_AMOUNT,ACC_LOAN.LOAN_BALANCE,CTR_LOAN_CONT.USE_DEC,CTR_LOAN_CONT.LOAN_TERM from ACC_LOAN left join CTR_LOAN_CONT on ACC_LOAN.CONT_NO=CTR_LOAN_CONT.CONT_NO where ACC_LOAN.BILL_NO=?";
                            break;
                    }
                    break;

                case "贷后跟踪-小微部个人类":
                    switch (request.getDataSource()){
                        case ACC_LOAN:
                            sql = "select ACC_LOAN.BILL_NO,ACC_LOAN.CONT_NO,ACC_LOAN.CUS_ID,ACC_LOAN.CUS_NAME,ACC_LOAN.ASSURE_MEANS_MAIN,ACC_LOAN.LOAN_AMOUNT,ACC_LOAN.LOAN_BALANCE,CTR_LOAN_CONT.USE_DEC,CTR_LOAN_CONT.LOAN_TERM,GRT_GUAR_CONT.GUAR_NAME from ACC_LOAN left join CTR_LOAN_CONT on ACC_LOAN.CONT_NO=CTR_LOAN_CONT.CONT_NO left join GRT_LOANGUAR_INFO on CTR_LOAN_CONT.CONT_NO=GRT_LOANGUAR_INFO.CONT_NO left join GRT_GUAR_CONT on GRT_LOANGUAR_INFO.GUAR_CONT_NO=GRT_GUAR_CONT.GUAR_CONT_NO where ACC_LOAN.BILL_NO='20019740660711799'select ACC_LOAN.BILL_NO,ACC_LOAN.CONT_NO,ACC_LOAN.CUS_ID,ACC_LOAN.CUS_NAME,ACC_LOAN.ASSURE_MEANS_MAIN,ACC_LOAN.LOAN_AMOUNT,ACC_LOAN.LOAN_BALANCE,CTR_LOAN_CONT.USE_DEC,CTR_LOAN_CONT.LOAN_TERM,GRT_GUAR_CONT.GUAR_NAME from ACC_LOAN left join CTR_LOAN_CONT on ACC_LOAN.CONT_NO=CTR_LOAN_CONT.CONT_NO left join GRT_LOANGUAR_INFO on CTR_LOAN_CONT.CONT_NO=GRT_LOANGUAR_INFO.CONT_NO left join GRT_GUAR_CONT on GRT_LOANGUAR_INFO.GUAR_CONT_NO=GRT_GUAR_CONT.GUAR_CONT_NO where ACC_LOAN.BILL_NO=?";
                            break;
                    }
                    break;

                case "催收":
                    switch (request.getDataSource()){
                        case ACC_LOAN:
                            sql = "select BILL_NO,CONT_NO,CUS_ID,CUS_NAME,LOAN_ACCOUNT from ACC_LOAN where BILL_NO=?";
                            break;
                    }
                    break;

                case "不良资产登记":
                case "利息减免":
                case "抵债资产接收":
                case "资产处置":
                    switch (request.getDataSource()){
                        case ACC_LOAN:
                            sql = "select BILL_NO,CONT_NO,CUS_ID,CUS_NAME,LOAN_ACCOUNT from ACC_LOAN where BILL_NO=?";
                            break;
                    }
                    break;
            }
            if (null != sql) {
                List<Map<String, String>> rs = sqlUtils.query(sql, Collections.singleton(request.getDataId()));
                if (rs.size() > 0) {
                    //只用第一条
                    for (WorkflowModelInnate workflowModelInnate : model.getInnates()) {
                        if(!workflowModelInnate.getContent().getString("type").equals("EXT_DATA")){
                            continue;
                        }
                        //插入固有字段
                        WorkflowInstanceAttribute attribute = new WorkflowInstanceAttribute();
                        attribute.setInstance(instance);
                        attribute.setType(WorkflowInstanceAttribute.Type.INNATE);
                        attribute.setAttrCName(workflowModelInnate.getContent().getString("cname"));
                        attribute.setAttrKey(workflowModelInnate.getContent().getString("ename"));
                        attribute.setAttrValue(rs.get(0).getOrDefault(attribute.getAttrKey(),""));
                        instanceAttributeDao.save(attribute);
                    }
                    //更新第一个节点里的对应字段
                    if(updateFirstNode){
                        List<WorkflowNodeInstance> nodeInstances = nodeInstanceDao.findAllByInstanceIdAndNodeModel_StartIsTrue(instance.getId());
                        for (WorkflowNodeInstance nodeInstance : nodeInstances) {
                            WorkflowNode node = findNode(nodeInstance.getNodeModelId()).orElse(null);
                            if(null == node){
                                continue;
                            }
                            JSONObject object = node.getNode();
                            if(object.containsKey("content")){
                                for (Map.Entry<String, Object> entry : object.getJSONObject("content").entrySet()) {
                                    JSONObject field = (JSONObject) entry.getValue();
                                    String value =rs.get(0).getOrDefault(field.getString("ename"),"");
                                    if(!StringUtils.isEmpty(value)){

                                        WorkflowNodeAttribute attribute = addAttribute(instance.getDealUserId(), nodeInstance.getId(), field.getString("ename"), value, field.getString("cname"));
                                        attributeDao.save(attribute);
                                    }
                                }
                            }
                        }
                    }
//                    for (Map.Entry<String, String> entry : rs.get(0).entrySet()) {
//                        WorkflowModelInnate after = modelInnates.get(entry.getKey());
//                        if (null == after) {
//                            continue;
//                        }
//                        //插入固有字段
//                        WorkflowInstanceAttribute attribute = new WorkflowInstanceAttribute();
//                        attribute.setInstance(instance);
//                        attribute.setType(WorkflowInstanceAttribute.Type.INNATE);
//                        attribute.setAttrCName(after.getContent().getString("cname"));
//                        attribute.setAttrKey(after.getContent().getString("ename"));
//                        attribute.setAttrValue(entry.getValue());
//                        instanceAttributeDao.save(attribute);
//                    }
                }
            }
        }
    }

    /**
     * 开启子任务
     *
     * @param uid
     * @param request
     * @return
     */
    public Result startChildInstance(Long uid, StartChildInstanceRequest request) {
        User user = userService.findUser(uid).orElse(null);
        WorkflowNodeInstance nodeInstance = findNodeInstance(request.getNodeInstanceId()).orElse(null);
        if (null == user || null == nodeInstance || nodeInstance.isFinished()) {
            return Result.error("找不到合适的节点");
        }
        //允许开启的子流程是否存在
        if (!nodeInstance.getNodeModel().getAllowChildTask().contains(request.getModelName())) {
            return Result.error("禁止开启子流程");
        }
        if (!checkNodeAuth(nodeInstance, user)) {
            return Result.error("权限错误");
        }
        //查找该流程的id
        List<Long> modelIds = modelDao.findModelId(request.getModelName());
        if (modelIds.size() == 0) {
            return Result.error("找不到对应的流程");
        }
        Long modelId = modelIds.get(0);

        ApplyTaskRequest applyTaskRequest = new ApplyTaskRequest();
        applyTaskRequest.setModelId(modelId);
        applyTaskRequest.setTitle(request.getModelName());
        applyTaskRequest.setDealerId(user.getId());
        applyTaskRequest.setCommon(false);
        applyTaskRequest.setManual(false);

        Result<WorkflowInstance> result = startNewInstance(uid, applyTaskRequest);
        if (!result.isSuccess()) {
            return result;
        }
        WorkflowInstance instance = result.getData();
        instance.setParentNodeId(nodeInstance.getId());
        instanceDao.save(instance);

        //补充父任务的固有字段
//        long pid = nodeInstance.getInstanceId();
//        WorkflowInstanceAttribute attribute = new WorkflowInstanceAttribute();
//        attribute.setInstance(instance);
//        attribute.setType(WorkflowInstanceAttribute.Type.INNATE);
//        attribute.setAttrCName(after.getContent().getString("cname"));
//        attribute.setAttrKey(after.getContent().getString("ename"));
//        attribute.setAttrValue(workflowInstance.getId() + "");
//        instanceAttributeDao.save(attribute);

        logService.addLog(SystemTextLog.Type.WORKFLOW, nodeInstance.getInstance().getId(), user.getId(), "接受了子任务 " + applyTaskRequest.getTitle());

        return Result.ok(instance.getId());
    }

    /**
     * 接受公共任务
     *
     * @param uid
     * @param instanceId
     * @return
     */
    public boolean acceptInstance(long uid, long instanceId) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) return false;
        WorkflowInstance instance = findInstance(instanceId).orElse(null);
        if (null == instance) return false;
        if (!canAccept(instance, user)) {
            return false;
        }
//        if (!instance.getState().equals(WorkflowInstance.State.UNRECEIVED)) {
//            return false;
//        }
//        //没有权限接受
//        if (!canPub(instance.getWorkflowModel(), user)) {
//            return false;
//        }
        //锁定该任务
        entityManager.refresh(instance, PESSIMISTIC_WRITE);
        instance.setDealUserId(user.getId());
        instance.setState(WorkflowInstance.State.DEALING);
        entityManager.merge(instance);

        //绑定第一个节点
        WorkflowNodeInstance nodeInstance = instance.getNodeList().get(0);
        nodeInstance.setDealerId(user.getId());
//        nodeInstance.setDealer(user);
        nodeInstanceDao.save(nodeInstance);

        //写log
        logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), user.getId(), "接受了任务");
        return true;
    }


    /**
     * 取消公共任务
     *
     * @param uid
     * @param instanceId
     * @return
     */
//    public boolean cancelCommonInstance(long uid, long instanceId) {
//        User user = userService.findUser(uid).orElse(null);
//        if (null == user) return false;
//        WorkflowInstance instance = findInstance(instanceId).orElse(null);
//        if (null == instance) return false;
//        //不是公共任务
//        if (!instance.isCommon()) return false;
//        //不是我自己发布的
//        if (instance.getPubUserId() != uid) {
//            return false;
//        }
//        entityManager.refresh(instance, PESSIMISTIC_WRITE);
//        instance.setState(WorkflowInstance.State.CANCELED);
//        entityManager.merge(instance);
//        return true;
//    }

    /**
     * 自己取消任务
     *
     * @param uid
     * @param instanceId
     * @return
     */
    public boolean closeInstance(long uid, long instanceId) {
        User user = userService.findUser(uid).orElse(null);
        return findInstance(instanceId).filter(workflowInstance -> {
            if (canCancel(workflowInstance, user)) {
                return instanceDao.deleteById(workflowInstance.getId()) > 0;
//                if (null != user) {
//                    logService.addLog(SystemTextLog.Type.WORKFLOW, workflowInstance.getId(), uid, "取消了任务");
//                }
//                return true;
            }
            return false;
        }).isPresent();
    }

    /**
     * 撤回任务
     *
     * @param uid
     * @param instanceId
     * @return
     */
    public boolean recallInstance(long uid, long instanceId) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) return false;
        return findInstance(instanceId).filter(workflowInstance -> {
            //撤回任务的限制
            if (canRecall(workflowInstance, user)) {
                workflowInstance.setState(WorkflowInstance.State.CANCELED);
                instanceDao.save(workflowInstance);
                logService.addLog(SystemTextLog.Type.WORKFLOW, workflowInstance.getId(), uid, "撤回了任务");
                return true;
            }
            return false;
        }).isPresent();
    }


    /**
     * 移交任务
     *
     * @param uid
     * @param instanceId
     * @param dealerId
     * @return
     */
    public boolean transformInstance(long uid, long instanceId, long dealerId) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) return false;
        User dealer = userService.findUser(dealerId).orElse(null);
        if (null == dealer) return false;
        WorkflowInstance instance = findInstance(instanceId).orElse(null);
        if (null == instance) return false;
        //移交任务的限制
        if (canTransform(instance, user)
            //3.被指派的人在任务的第一个节点的主办岗位上

                ) {
//            instance.setDealUser(dealer);
            instance.setDealUserId(dealerId);
            instanceDao.save(instance);
            logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "移交任务给 " + dealer.getTrueName());
            return true;
        }
        return false;
    }

    /**
     * 是否可以处理
     *
     * @param instance
     * @param user
     * @return
     */
    private boolean canDeal(WorkflowInstance instance, User user) {
        WorkflowNodeInstance nodeInstance = findCurrentNodeInstance(instance.getId()).orElse(null);
        return null != nodeInstance && instance.getState().equals(WorkflowInstance.State.DEALING) && checkNodeAuth(nodeInstance, user);
    }


    /**
     * 是否可以取消
     *
     * @param instance
     * @param user
     * @return
     */
    private boolean canCancel(WorkflowInstance instance, User user) {
        //取消任务的限制
        //1.这是我自己的任务
        //2.任务只有一个节点
        //3.任务也是我发布的
        //4.任务在进行中
        return instance.getDealUserId().equals(user.getId()) &&
                (null != instance.getPubUserId() && instance.getPubUserId().equals(user.getId())) &&
                        instance.getNodeList().size() <= 1 &&
                        instance.getState().equals(WorkflowInstance.State.DEALING);
    }

    /**
     * 是否可以撤回
     *
     * @param instance
     * @param user
     * @return
     */
    private boolean canRecall(WorkflowInstance instance, User user) {
        //1.这是我发布的任务
        return instance.getPubUserId() == (user.getId())
                //2.任务只有一个节点
                && instance.getNodeList().size() <= 1
                //3.任务在进行中 或者 未被领取
                && (instance.getState().equals(WorkflowInstance.State.DEALING) || instance.getState().equals(WorkflowInstance.State.UNRECEIVED))
                //4.拥有撤回的权限
                && canPoint(instance.getModelId(), user);
//        instance.getWorkflowModel().getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
    }

    /**
     * 是否可以接受任务
     *
     * @param instance
     * @param user
     * @return
     */
    public boolean canAccept(WorkflowInstance instance, User user) {
        return instance.getState().equals(WorkflowInstance.State.COMMON) && canPub(instance.getWorkflowModel(), user);
    }

    /**
     * 是否可以发布这个模型的任务
     *
     * @param model
     * @param user
     * @return
     */
    public boolean canPubOrPoint(WorkflowModel model, User user) {
        //1.拥有该任务的指派权限
        return canPoint(model.getId(), user) || canPub(model, user);
    }

    /**
     * 单独检查是否可以发布这个任务(只针对执行者而言)
     *
     * @param model
     * @param user
     * @return
     */
    public boolean canPub(WorkflowModel model, User user) {
        return globalPermissionDao.hasPermission(user.getId(), Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), model.getId()) > 0;
//        //2.在第一个节点上
//        List<WorkflowNode> fNodes = nodeDao.findAllByModelAndStartIsTrue(model);
//        boolean flag2 = true;
//
//        for (WorkflowNode fNode : fNodes) {
//            flag2 = flag2 && fNode.getPersons().stream().anyMatch(p -> p.getType().equals(Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()));
//        }
//        return flag2;
    }

    public boolean canPub(long modelId, long uid) {
        return globalPermissionDao.hasPermission(uid, Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), modelId) > 0;
    }


    /**
     * 是否可以指派这个模型的任务
     *
     * @param modelId
     * @param user
     * @return
     */
    public boolean canPoint(long modelId, User user) {
        return modelDao.isManagerForWorkflow(user.getId(), modelId) > 0;
//        return globalPermissionDao.hasPermission(user.getId(), Collections.singleton(GlobalPermission.Type.WORKFLOW_POINTER), modelId) > 0;
//        return model.getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
    }

    public boolean canPoint(long modelId, long uid) {
        return modelDao.isManagerForWorkflow(uid, modelId) > 0;
    }

    /**
     * 是否可以移交
     *
     * @param instance
     * @param user
     * @return
     */
    public boolean canTransform(WorkflowInstance instance, User user) {
        WorkflowNode firstNode = nodeDao.findAllByModelAndStartIsTrue(instance.getWorkflowModel()).get(0);
        return
                //1.任务正在进行中
                instance.getState().equals(WorkflowInstance.State.DEALING) &&
                        //2.拥有指派该任务的权利
                        canPoint(instance.getModelId(), user);
//                        globalPermissionDao.hasPermission(user.getId(), Collections.singleton(GlobalPermission.Type.WORKFLOW_POINTER), instance.getModelId()) > 0;
//                        && instance.getWorkflowModel().getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
//                && firstNode.getPersons().stream().anyMatch(p -> p.getType().equals(Type.MAIN_QUARTERS) && dealer.hasQuarters(p.getUid()))
    }

    /**
     * 该模型是否可以指派
     *
     * @param model
     * @param user
     * @return
     */
    public boolean canTransform(WorkflowModel model, User user) {
        return canPoint(model.getId(), user);
//        return model.getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
    }

    /**
     * 得到用户所执行的工作流(所有人)
     *
     * @return
     */
    public List<WorkflowInstance> getMyOwnWorks(long uid, Long lessId) {
        PageRequest pageRequest = new PageRequest(0, 20);
        if (lessId == null) {
            return instanceDao.findAllByDealUserIdOrderByAddTimeDesc(uid, pageRequest);
        } else {
            return instanceDao.findAllByDealUserIdAndIdLessThanOrderByAddTimeDesc(uid, lessId, pageRequest);
        }
    }

    /**
     * 得到某些用户未执行的任务
     *
     * @param uids
     * @param lessId
     * @param pageable
     * @return
     */
    public Page<WorkflowInstance> getUserUndealedWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
        if (lessId == null) {
            lessId = Long.MAX_VALUE;
        }
        return instanceDao.findNeedToDealWorks(Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), uids, lessId, pageable);
    }

    /**
     * 得到某些用户执行过的任务
     *
     * @param uids
     * @param lessId
     * @param pageable
     * @return
     */
    public Page<WorkflowInstance> getUserDealedWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
        if (null == lessId) {
            lessId = Long.MAX_VALUE;
        }
        return instanceDao.findDealedWorks(uids, lessId, pageable);
    }

    /**
     * 得到部门未执行的任务
     *
     * @param uids
     * @param lessId
     * @param pageable
     * @return
     */
    public Page<WorkflowInstance> getDepartmentUndealedWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        return instanceDao.findNeedToDealWorksFromDepartments(uids, WorkflowInstance.State.DEALING, lessId, pageable);
    }


    /**
     * 得到部门已执行完毕的任务
     *
     * @param uids
     * @param lessId
     * @param pageable
     * @return
     */
    public Page<WorkflowInstance> getDepartmentDealedWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        return instanceDao.findNeedToDealWorksFromDepartments(uids, WorkflowInstance.State.FINISHED, lessId, pageable);
    }


    /**
     * 得到用户可以接受的公共任务列表
     *
     * @param uids
     * @param lessId
     * @param pageable
     * @return
     */
    public Page<WorkflowInstance> getUserCanAcceptCommonWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        return instanceDao.findCommonWorks(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), uids, lessId, pageable);
    }


    /**
     * 得到用户观察的任务列表
     *
     * @param uids
     * @param lessId
     * @param pageable
     * @return
     */
    public Page<WorkflowInstance> getUserObservedWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        return instanceDao.findObserveredWorks(Collections.singleton(GlobalPermission.Type.WORKFLOW_OBSERVER), uids, lessId, pageable);
    }


    /**
     * 得到用户可以接受的任务
     *
     * @param uids
     * @param lessId
     * @param pageRequest
     * @return
     */
    public Page<WorkflowInstance> getUserCanAcceptWorks(Collection<Long> uids, Long lessId, Pageable pageRequest) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        return instanceDao.findUserCanAcceptWorks(uids, lessId, pageRequest);
    }


    /**
     * 接受指派/移交
     *
     * @param uid
     * @param taskIds
     * @return
     */
    public Result acceptTask(long uid, Long... taskIds) {
        List<Long> successIds = new ArrayList<>();
        List<WorkflowInstanceTransaction> transactions = transactionDao.findAllByUserIdAndInstanceIdInAndStateIn(uid, Arrays.asList(taskIds), Collections.singleton(WorkflowInstanceTransaction.State.DEALING));
        for (WorkflowInstanceTransaction transaction : transactions) {
            transaction.setState(WorkflowInstanceTransaction.State.ACCEPT);
            transaction.getInstance().setState(transaction.getToState());
            updateInstanceBelongs(transaction.getInstance(), uid);

            //更新事务
            transactionDao.save(transaction);
            successIds.add(transaction.getId());
            //TODO: 发送消息
        }
        return Result.ok(successIds);
    }

    /**
     * 更新任务归属
     *
     * @param instance
     * @param uid
     */
    private void updateInstanceBelongs(WorkflowInstance instance, long uid) {
        Long fromUid = instance.getDealUserId();
        instance.setDealUserId(uid);
        //更新所有起始节点的经办人
        List ids = nodeInstanceDao.getStartNodeIds(instance.getId());
        nodeInstanceDao.updateNodeInstanceDealer(uid, ids);
        //更新所有子流程的归属
        if (null != fromUid) {
            List<WorkflowInstance> instances = nodeInstanceDao.getAllChildInstance(instance.getId(), fromUid);
            for (WorkflowInstance workflowInstance : instances) {
                updateInstanceBelongs(workflowInstance, uid);
            }
        }
        //保存状态
        saveWorkflowInstance(instance);
    }

    /**
     * 拒绝指派/移交
     *
     * @param uid
     * @param info
     * @param taskIds
     * @return
     */
    public Result rejectTask(long uid, String info, Long... taskIds) {
        List<Long> successIds = new ArrayList<>();
        List<WorkflowInstanceTransaction> transactions = transactionDao.findAllByUserIdAndInstanceIdInAndStateIn(uid, Arrays.asList(taskIds), Collections.singleton(WorkflowInstanceTransaction.State.DEALING));
        for (WorkflowInstanceTransaction transaction : transactions) {
            transaction.setState(WorkflowInstanceTransaction.State.REJECT);
            transaction.getInstance().setState(transaction.getFromState());
//            transaction.getInstance().setDealUserId(uid);
//            //保存主体
            instanceDao.save(transaction.getInstance());
//            //更新所有起始节点的经办人
//            nodeInstanceDao.updateNodeInstanceDealer(transaction.getInstanceId(), uid);
//            //更新事务
            transactionDao.save(transaction);
            successIds.add(transaction.getId());
            //TODO: 发送消息
        }
        return Result.ok(successIds);
    }


    /**
     * 主管得到管理的任务
     *
     * @param uids
     * @param request
     * @param lessId
     * @param pageable
     * @return
     */
    public Page<WorkflowInstance> getUnreceivedWorks(Collection<Long> uids, UnreceivedWorksSearchRequest request, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        //得到我监管的所有模型ID
        List oids = globalPermissionDao.getObjectIds(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), uids);
        Long finalLessId = lessId;
        Specification query = new Specification() {

            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(cb.lessThan(root.get("id"), finalLessId));
                Join join = root.join("workflowModel");
                //关联模型
                predicates.add(join.get("id").in(oids));
                if (!StringUtils.isEmpty(request.getId())) {
                    predicates.add(cb.like(root.get("id"), "%" + request.getId() + "%"));
                }
                //限制状态
                if (null != request.getState()) {
                    predicates.add(cb.equal(root.get("state"), request.getState()));
                }
                //类型
                if (!StringUtils.isEmpty(request.getType())) {

                }
                //客户经理编号
                if (null != request.getUserId()) {
                    predicates.add(cb.equal(root.get("dealUserId"), request.getUserId()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        return instanceDao.findAll(query, pageable);
    }


    /**
     * 得到用户的预任务列表
     *
     * @param uids
     * @param lessId
     * @param pageable
     * @return
     */
    public Page<WorkflowInstance> getPlanWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        return instanceDao.findAllByDealUserIdInAndPlanStartTimeGreaterThanAndIdLessThan(uids, new Date(), lessId, pageable);
    }

    /**
     * 指派/移交 任务
     *
     * @param uid
     * @param iids
     * @param toUid
     * @return 处理成功的任务ID
     */
    public List<Long> pointTask(long uid, Collection<Long> iids, long toUid) {
        List<Long> success = new ArrayList<>();
        for (Long iid : iids) {
            WorkflowInstance instance = findInstance(iid).orElse(null);
            if (null == instance) {
                continue;
            }

            //检查是否有指派权限
            if (!canPoint(instance.getModelId(), uid)) {
                continue;
            }

            //接受人是否有执行权限
            if (!canPub(instance.getModelId(), toUid)) {
                continue;
            }

            //任务状态是否合法
            if (!instance.getState().equals(WorkflowInstance.State.COMMON) && !instance.getState().equals(WorkflowInstance.State.UNRECEIVED)) {
                continue;
            }

            //子任务禁止移交
            if (instance.getParentNodeId() != null) {
                continue;
            }

            WorkflowInstanceTransaction transaction = new WorkflowInstanceTransaction();
            transaction.setFromState(instance.getState());
            transaction.setToState(WorkflowInstance.State.DEALING);
            transaction.setInstanceId(instance.getId());
            transaction.setUserId(toUid);
            transactionDao.save(transaction);
            instance.setState(WorkflowInstance.State.PAUSE);
            saveWorkflowInstance(instance);

            success.add(instance.getId());
        }

        return success;
    }


    /**
     * 向一个节点提交数据
     *
     * @param instanceId
     * @param data
     * @return
     */
    @Deprecated
    public WorkflowInstance submitData(long uid, long instanceId, Map data) throws RestException {
        return null;
//        User user = userService.findUserE(uid);
//        WorkflowInstance workflowInstance = findInstance(instanceId).orElseThrow(() -> new CannotFindEntityException(WorkflowInstance.class, instanceId));
//        //已完成的禁止再提交
//        //只有正在处理中的才可以提交
//        if (!workflowInstance.getState().equals(WorkflowInstance.State.DEALING)) {
//            return workflowInstance;
//        }
////        if (workflowInstance.isFinished()) {
////            return workflowInstance;
////        }
//        //得到当前应处理的节点
//        WorkflowNodeInstance workflowNodeInstance = findCurrentNodeInstance(instanceId).orElse(null);
////        WorkflowNodeInstance workflowNodeInstance = workflowInstance.getCurrentNode();
//        WorkflowNode nodeModel = workflowNodeInstance.getNodeModel();
//        //验证是否有权限处理
//        if (!checkAuth(workflowInstance.getWorkflowModel(), workflowNodeInstance.getNodeModel(), user)) {
//            throw new RestException("该用户没有权限提交数据");
//        }
//
//        //处理必填数据
//        //对于资料节点来讲, 必填数据的全部传递视为走向下一个节点
//        switch (nodeModel.getType()){
//            case input:
//                submitInputNode();
//                break;
//
//            case check:
//                break;
//        }
////        switch (nodeModel.getType()) {
////            case "input":
////                nodeModel.getNode().submit(user, workflowNodeInstance, data);
////                break;
////
////            case "check":
////                nodeModel.getNode().submit(user, workflowNodeInstance, data);
////                break;
////
////            case "universal":
////                nodeModel.getNode().submit(user, workflowNodeInstance, data);
////                break;
////
////            case "checkprocess":
////                nodeModel.getNode().submit(user, workflowNodeInstance, data);
////                break;
////        }
//
//        return saveWorkflowInstance(workflowInstance);
    }

    /**
     * 提交数据
     *
     * @param uid
     * @param request
     * @return
     */
    public Result submitData(long uid, SubmitDataRequest request) {
        WorkflowInstance instance = findInstance(request.getInstanceId()).orElse(null);
        if (null == instance || !instance.getState().equals(WorkflowInstance.State.DEALING)) {
            return Result.error("找不到符合条件的任务");
        }
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return Result.error("权限错误");
        }
        WorkflowNodeInstance nodeInstance = findNodeInstance(request.getNodeId()).orElse(null);
        if (null == nodeInstance || nodeInstance.isFinished()) {
            return Result.error("找不到符合条件的节点");
        }
        if (!checkNodeAuth(nodeInstance, user)) {
            return Result.error("权限验证错误");
        }

        WorkflowNode nodeModel = nodeInstance.getNodeModel();
        String errMessage = null;
        switch (nodeModel.getType()) {
            case input:
                errMessage = submitInputNode(uid, request.getNodeId(), request.getData());
                break;

            case check:
                errMessage = submitCheckNode(uid, request.getNodeId(), request.getData());
                break;
//            case "input":
//                //绑定节点处理人
//                nodeInstance.setDealerId(user.getId());
////                nodeInstance.setDealer(user);
//                nodeInstance = nodeInstanceDao.save(nodeInstance);
//                nodeModel.getNode().submit(user, nodeInstance, request.getData(), attributeDao);
//                break;
//
//            case "check":
//                nodeModel.getNode().submit(user, nodeInstance, request.getData(), attributeDao);
//                break;
//
//            case "universal":
//                nodeModel.getNode().submit(user, nodeInstance, request.getData(), attributeDao);
//                break;
//
//            case "checkprocess":
//                nodeModel.getNode().submit(user, nodeInstance, request.getData(), attributeDao);
//                break;
        }
        if (!StringUtils.isEmpty(errMessage)) {
            return Result.error(errMessage);
        }

        logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "向 " + nodeInstance.getNodeModel().getName() + " 节点提交了数据");
        return Result.ok();
//        return Result.ok(findInstance(instance.getId()).orElse(null));
    }


    @Deprecated
    public WorkflowInstance goNext(long uid, long instanceId) throws RestException {
        return null;
//        WorkflowInstance instance = findInstanceE(instanceId);
//        //正在处理中的才可以提交
//        if (!instance.getState().equals(WorkflowInstance.State.DEALING)) {
//            return instance;
//        }
////        if (instance.isFinished()) {
////            return instance;
////        }
//        //得到当前节点
//        WorkflowNodeInstance currentNode = findCurrentNodeInstance(instanceId).orElse(null);
//        User user = userService.findUserE(uid);
//
//        //验证权限
//        if (!checkAuth(instance.getWorkflowModel(), currentNode.getNodeModel(), user)) {
//            throw new RestException("授权失败");
//        }
//
//        //如果当前节点是资料节点
//        WorkflowNode nodeModel = currentNode.getNodeModel();
//        switch (nodeModel.getType()) {
//            case "input":
//                return fromInputNodeToGo(instance, currentNode, (InputNode) nodeModel.getNode());
//            case "check":
//                return fromCheckNodeToGo(user, instance, currentNode, (CheckNode) nodeModel.getNode());
//            case "universal":
//                return fromUniversalNodeToGo(instance, currentNode, (UniversalNode) nodeModel.getNode());
//        }
//
//        return instance;
    }


    /**
     * 提交节点到下一步
     *
     * @param uid
     * @param instanceId
     * @param nodeId
     * @return
     */
    public Result goNext(long uid, long instanceId, long nodeId) {
        WorkflowNodeInstance nodeInstance = findNodeInstance(nodeId).orElse(null);
        if (null == nodeInstance || nodeInstance.isFinished()) {
            return Result.error("没有找到符合条件的任务");
        }
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return Result.error("授权错误");
        }
        WorkflowInstance instance = nodeInstance.getInstance();
        //如果是第一个节点, 并且授权人已经确认, 那么只检查pub权限即可
        if (null != nodeInstance.getDealerId() && nodeInstance.getDealerId().equals(uid)) {

        }
        //验证权限
        else if (!checkAuth(instance.getWorkflowModel(), nodeInstance.getNodeModel(), user)) {
            return Result.error("授权失败");
        }

        //如果当前节点是资料节点
        WorkflowNode nodeModel = nodeInstance.getNodeModel();
        try {
            switch (nodeModel.getType()) {
                case input:
                    fromInputNodeToGo(instance, nodeInstance, nodeModel.getNode());
                    break;

                case check:
                    fromCheckNodeToGo(user, instance, nodeInstance, nodeModel.getNode());
                    break;

//                case "universal":
//                    fromUniversalNodeToGo(instance, nodeInstance, (UniversalNode) nodeModel.getNode());
//                    break;
            }
        } catch (RestException e) {
            e.printStackTrace();
            return Result.error(e.getSimpleMessage());
        }

        logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "向" + nodeModel.getName() + "提交了下一步");
        return Result.ok();
    }

    /**
     * 上传定位信息
     *
     * @param uid
     * @param request
     * @return
     */
    public Result addPosition(long uid, WorkflowPositionAddRequest request) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return Result.error();
        }
        WorkflowNodeInstance nodeInstance = findNodeInstance(request.getNodeInstanceId()).orElse(null);
        if (null == nodeInstance || !nodeInstance.getId().equals(request.getNodeInstanceId()) || !nodeInstance.getInstance().getState().equals(WorkflowInstance.State.DEALING)) {
            return Result.error("找不到对应的节点实例");
        }
        //是否该我处理
        if (!checkNodeAuth(nodeInstance, user)) {
            return Result.error("权限错误");
        }
        WorkflowNodeFile file = new WorkflowNodeFile();
        file.setUserId(uid);
        file.setType(WorkflowNodeFile.Type.POSITION);
        file.setNodeInstance(nodeInstance);
        file.setFileName(request.getPosition());

        GPSPosition gpsPosition = new GPSPosition();
        gpsPosition.setLat(request.getLat());
        gpsPosition.setLng(request.getLng());
        gpsPosition.setPosition(request.getPosition());
        gpsPosition.setUserId(uid);
        gpsPosition = gpsPositionDao.save(gpsPosition);

        file.setFileId(gpsPosition.getId());
        return Result.ok(nodeFileDao.save(file));
    }

    /**
     * 删除节点附件
     *
     * @param uid
     * @param nodeFileId
     * @return
     */
    public boolean deleteNodeFile(long uid, long nodeFileId) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return false;
        }
        WorkflowNodeFile nodeFile = findNodeFile(nodeFileId).orElse(null);
        if (null == nodeFile) {
            return false;
        }
        if (!checkNodeAuth(nodeFile.getNodeInstance(), user)) {
            return false;
        }
        nodeFileDao.delete(nodeFileId);
        return true;
    }

    /**
     * 上传节点文件
     *
     * @param uid
     * @param instanceId
     * @param nodeId
     * @param fileType
     * @param file
     * @return
     * @throws IOException
     */
    public Result uploadNodeFile(long uid, long instanceId, long nodeId, WorkflowNodeFile.Type fileType, MultipartFile file, String content) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return Result.error();
        }
        WorkflowNodeInstance nodeInstance = instanceDao.getCurrentNodeInstance(instanceId).orElse(null);
        if (null == nodeInstance || !nodeInstance.getId().equals(nodeId) || !nodeInstance.getInstance().getState().equals(WorkflowInstance.State.DEALING)) {
            return Result.error("找不到对应的节点实例");
        }
        //是否该我处理
        if (!checkNodeAuth(nodeInstance, user)) {
            return Result.error("权限错误");
        }
        SystemFile systemFile = new SystemFile();
        try {
            systemFile.setBytes(file.getBytes());
        } catch (IOException e) {
            return Result.error("保存文件错误");
        }
        systemFile.setType(SystemFile.Type.WORKFLOW);
        systemFile.setFileName(file.getOriginalFilename());
        systemFile.setExt(Utils.getExt(systemFile.getFileName()));
        systemFile = systemFileDao.save(systemFile);

        WorkflowNodeFile nodeFile = new WorkflowNodeFile();
        nodeFile.setFileName(systemFile.getFileName());
        nodeFile.setFileId(systemFile.getId());
        nodeFile.setNodeInstance(nodeInstance);
        nodeFile.setType(fileType);
        nodeFile.setUserId(user.getId());
        nodeFile.setExt(systemFile.getExt());
        if (fileType.equals(WorkflowNodeFile.Type.SIGN)) {
            nodeFile.setContent(content);
        }

        return Result.ok(nodeFileDao.save(nodeFile));
    }

    public boolean setNodeFileTags(long uid, long nodeId, String tags) {
        val list = Arrays.asList(tags.split(" ")).stream().filter(item -> !StringUtils.isEmpty(item))
                .distinct()
                .collect(Collectors.toList());
        String str = org.apache.commons.lang.StringUtils.join(list.toArray(), " ");
        if (null == str) {
            str = "";
        }
        //查找是否存在这个节点
        return nodeFileDao.updateNodeFileTags(uid, nodeId, str) > 0;
    }

    public boolean setNodeFileName(long uid, long nodeId, String name){
        //处理名字
        WorkflowNodeFile nodeFile = findNodeFile(nodeId).orElse(null);
        if(null == nodeFile){
            return false;
        }
        name = name.replace("(\\..+)$", "");
        if(!StringUtils.isEmpty(nodeFile.getExt())){
            name = name + "." + nodeFile.getExt();
        }
        return nodeFileDao.updateNodeFileName(uid, nodeId, name) > 0;
    }

    public String applyDownload(long uid, long id){
        WorkflowNodeFile nodeFile = findNodeFile(id).orElse(null);
        if(null == nodeFile){
            return "";
        }
        DownloadFileToken token = new DownloadFileToken();
        token.setExprTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        token.setToken(UUID.randomUUID().toString());
        token.setFileId(nodeFile.getFileId());
        downloadFileTokenDao.save(token);
        return token.getToken();
    }

    @Deprecated
    public boolean editWorkflowModel(long modelId, String info, Boolean open) {
//        Utils.validate(edit);
        return findModel(modelId)
                .filter(model -> {
                    //描述可以随意修改
                    if (!StringUtils.isEmpty(info)) {
                        model.setInfo(info);
                    }
                    //开关也可以
                    if (null != open) {
                        model.setOpen(open);
                        if (open) {
                            model.setFirstOpen(true);
                        }
                    }
                    modelDao.save(model);
                    return true;
                }).isPresent();
    }

    public boolean editWorkflowModel(WorkflowModelEdit edit) {
        return findModel(edit.getId()).filter(model -> {
            //流程名字修改
            if (!StringUtils.isEmpty(edit.getName())) {
                model.setName(edit.getName());
            }
            //描述可以随意修改
            if (!StringUtils.isEmpty(edit.getInfo())) {
                model.setInfo(edit.getInfo());
            }
            //开关也可以
            model.setOpen(edit.isOpen());
            if (edit.isOpen()) {
                model.setFirstOpen(true);
            }

            //工作流所属部门
//            if(edit.getDepartmentIds().size() > 0){
//                modelDao.deleteDepartments(Collections.singleton(edit.getId()));
//                model.getDepartments().clear();
//                for (Long aLong : edit.getDepartmentIds()) {
//                    Department department = new Department();
//                    department.setId(aLong);
//                    model.getDepartments().add(department);
//                }
//            }
            //特殊权限
//            if (null != edit.getPermissionEdits()) {
//                setExtPermissions(edit.getPermissionEdits());
//            }
            modelDao.save(model);
            return true;
        }).isPresent();
    }


    @Deprecated
    public Result<Set<WorkflowNode>> setPersons(WorkflowQuartersEdit... edits) {
        if (true) return Result.error();
        Set<WorkflowNode> set = new LinkedHashSet<>();
        for (WorkflowQuartersEdit edit : edits) {
            WorkflowNode node = nodeDao.findOne(edit.getNodeId());
            if (null == node) continue;

            if (node.getModel().isFirstOpen()) {
                return Result.error("已经上线的工作流无法编辑");
            }

            if (node.isEnd()) continue;

            //解除关联
//            personsDao.deleteAllByWorkflowNode(node);

//            addWorkflowPersons(edit.getMainQuarters(), node, edit.getName(), Type.MAIN_QUARTERS);
//            addWorkflowPersons(edit.getMainUser(),node, edit.getName(), Type.MAIN_USER);
//            addWorkflowPersons(edit.getSupportQuarters(), node, edit.getName(), Type.SUPPORT_QUARTERS);
//            addWorkflowPersons(edit.getSupportUser(),  node,edit.getName(), Type.SUPPORT_USER);

            set.add(nodeDao.save(node));
        }
        return Result.ok(set);
    }


    /**
     * 设置工作流的额外权限, 因为不涉及更改工作流内容, 所以任何时候都可以进行
     *
     * @param edits
     * @return
     */
//    @Deprecated
//    public Set<Long> setExtPermissions(WorkflowExtPermissionEdit... edits) {
//        Set<Long> result = new HashSet<>();
//        for (WorkflowExtPermissionEdit edit : edits) {
//            extPermissionDao.deleteAllByWorkflowModel_IdAndType(edit.getModelId(), edit.getType());
//            WorkflowModel workflowModel = findModel(edit.getModelId()).orElse(null);
//            if (null == workflowModel) continue;
//            for (Long qid : edit.getQids()) {
//                WorkflowExtPermission extPermission = new WorkflowExtPermission(null, workflowModel, edit.getType(), qid);
//                extPermissionDao.save(extPermission);
//            }
//            result.add(workflowModel.getId());
//        }
//        return result;
//    }


    /**
     * 删除节点(已废弃)
     *
     * @param modelId
     * @param nodeName
     * @return
     */
    @Deprecated

    public WorkflowModel deleteNode(long modelId, String[] nodeName) throws CannotFindEntityException {
        WorkflowModel workflowModel = findModelE(modelId);
        if (workflowModel.isFirstOpen() || workflowModel.isOpen()) {
            return workflowModel;
        }
//        Map<String, BaseNode> nodes = workflowModel.getModel();
//        List deleteList = Arrays.asList(nodeName);
        //开始和结束禁止删除
        for (String name : nodeName) {
            nodeDao.findFirstByModelAndName(workflowModel, name)
                    .filter(n -> !n.isStart() && !n.isEnd())
                    .ifPresent(n -> nodeDao.delete(n));
        }
        return saveWorkflowModel(workflowModel);
    }

    /**
     * 删除节点
     *
     * @param modelId
     * @param nodeId
     * @return
     */
    public boolean deleteNode(long modelId, Long nodeId) {
        try {
            nodeDao.deleteAllByModel_IdAndIdAndStartIsFalseAndEndIsFalse(modelId, nodeId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据已有模型创建新工作流模型
     *
     * @param modelName
     * @param add
     * @return
     */
    public Result<WorkflowModel> createWorkflow(String modelName, WorkflowModelAdd add) {
        Map<String, Map> map = (Map) cache.getWorkflowConfig();
        if (!map.containsKey(modelName)) {
            return Result.error("没找到工作流模型");
        }
        Map model = map.get(modelName);
        Map flow = (Map) model.getOrDefault("flow", new HashMap<>());
        WorkflowModel same = modelDao.findFirstByNameAndVersion(add.getName(), add.getVersion());
        if (same != null) return Result.error("已经有相同版本的工作流");

        WorkflowModel workflowModel = Transformer.transform(add, WorkflowModel.class);
        Map<String, BaseNode> nodes = new HashMap<>();
        flow.forEach((k, v) -> {
            v = (Map) v;
            boolean start = (boolean) ((Map) v).getOrDefault("start", false);
            WorkflowNode.Type type = WorkflowNode.Type.valueOf(String.valueOf(((Map) v).get("type")).toLowerCase());

//            BaseNode baseNode = BaseNode.create(workflowModel,(Map) v);

            JSONObject baseNode = null;
            switch (type) {
                case input:
                    baseNode = buildInputNode(workflowModel, (Map) v);
                    break;
//                    baseNode = new JSONObject();

                case check:
                    baseNode = buildCheckNode((Map) v);
                    break;

                case logic:
                    baseNode = buildLogicNode((Map) v);
                    break;

                case end:
                    baseNode = buildNormalNode((Map) v);
                    break;
            }


            //nodelist
            WorkflowNode node = new WorkflowNode();
            node.setModel(workflowModel);
            node.setName(String.valueOf(k));
            node.setType(type);
            Object next = ((Map) v).get("next");
            //start
            node.setStart(start);
            if (((Map) v).containsKey("end")) {
                node.setEnd((Boolean) ((Map) v).getOrDefault("end", false));
            }
            if (node.getType().equals("end")) {
                node.setEnd(true);
            }
            //next
            if (next instanceof Collection) {
                ((Collection) next).forEach(n -> node.getNext().add(String.valueOf(n)));
            } else if (next instanceof String) {
                if (!StringUtils.isEmpty(next)) {
                    node.getNext().add(String.valueOf(next));
                }
            }
            //允许开放的子流程
            List<String> allowChildTask = (List<String>) ((Map) v).getOrDefault("allow_child_task", new ArrayList<>());
            node.setAllowChildTask(allowChildTask);
            node.setNode(baseNode);
            nodeDao.save(node);

            workflowModel.getNodeModels().add(node);
        });

        workflowModel.setOpen(false);
        workflowModel.setFirstOpen(false);
        workflowModel.setModelName(modelName);

        if (workflowModel.getInnates().size() > 0) {
            innateDao.save(workflowModel.getInnates());
        }

        //补充额外信息
        boolean start = (boolean) model.getOrDefault("manual", false);
        workflowModel.setManual(start);
        boolean custom = (boolean) model.getOrDefault("custom", true);
        workflowModel.setCustom(custom);

        WorkflowModel result = modelDao.save(workflowModel);
        if (null != result.getId()) {
            return Result.ok(result);
        }
        return Result.error();
    }


    public boolean deleteWorkflowModel(long id, boolean force) {
        if (force) {
            modelDao.delete(id);
            return true;
        }
        WorkflowModel model = modelDao.findOne(id);
        if (model.isFirstOpen() || model.isOpen()) return false;
        modelDao.delete(id);
        return true;
    }

    /**
     * 创建节点, 已废弃
     *
     * @return
     */
    @Deprecated
    public Optional<WorkflowNode> createNode(long modelId, String node) {
        return Optional.empty();
    }

    public Optional<WorkflowNode> createCheckNode(CheckNodeModel nodeModel) {
        return findModel(nodeModel.getModelId()).map(model -> {
            //无法自定义的流程不允许自定义
            if (!model.isCustom()) {
                return null;
            }
            //验证是否有必填的属性
//            CheckNode checkNode = new CheckNode();
//            checkNode.setQuestion(nodeModel.getQuestion());
//            checkNode.setCount(nodeModel.getCount());
//            checkNode.setKey(nodeModel.getKey());
//            checkNode.setPs(nodeModel.getPs());
//            checkNode.setStates(nodeModel.getStates());
            JSONObject checkNode = (JSONObject) JSON.toJSON(nodeModel);

            WorkflowNode node = new WorkflowNode();
            node.setNode(checkNode);
            node.setEnd(false);
            node.setStart(false);
            node.setNext(nodeModel.getNext());
            node.setType(WorkflowNode.Type.check);
            node.setName(nodeModel.getName());
            node.setModel(model);
            //如果有ID, 则为修改
            node.setId(nodeModel.getNodeId());

            //移除不必要的属性
            checkNode.remove("next");
            checkNode.remove("name");
            checkNode.remove("nodeId");

            node = nodeDao.save(node);
            return node.getId() == null ? null : node;
        });

    }

    /**
     * 列出指定用户的所有工作流
     */
//    @Deprecated
//    public Page<WorkflowInstance> getUserWorkflows(long uid, Status status, Pageable pageable) throws CannotFindEntityException {
//        return null;
//        User user = userService.findUser(uid).orElse(null);
//        boolean isFinished = false;
//        switch (status) {
//            case DID:
//                isFinished = true;
//                break;
//
//            case DOING:
//                isFinished = false;
//                break;
//        }
//        Set<WorkflowModelPersons> persons = personsDao.findPersonsByUser(user.getQuarters().stream().map(q -> q.getId()).collect(Collectors.toList()), Arrays.asList(user.getId()));
//
//        Set<WorkflowModel> workflowModels = persons
//                .stream()
//                .map(p -> p.getWorkflowNode().getModel())
//                .collect(Collectors.toSet());
//        Set<String> nodeNames = persons
//                .stream()
//                .map(p -> p.getNodeName())
//                .collect(Collectors.toSet());
//        if (workflowModels.size() == 0) {
//            return null;
//        }
//
//        Page<WorkflowInstance> list = nodeInstanceDao.getInstanceList(workflowModels, nodeNames, isFinished, pageable);
//
//        return list;
//    }


//    @Deprecated
//    
//    public Result<InspectTask> createInspectTask(long createUserId, String modelName, long userId, boolean isAuto) {
////        AtomicReference<User> createUser = new AtomicReference<>();
//        return userService.findUser(userId)
//                .map(user -> {
//                    InspectTask task = new InspectTask();
//                    task.setModelName(modelName);
//                    task.setDealUser(user);
//                    task.setState(InspectTaskState.CREATED);
//                    task.setType(isAuto ? InspectTaskType.AUTO : InspectTaskType.MANUAL);
//                    task = inspectTaskDao.save(task);
//                    if (task.getId() == null) {
//                        return Result.error("找不到对应的任务");
//                    }
//                    //如果是自己创建的任务,那么自己接受任务
//                    if (createUserId == userId) {
//                        Result result = acceptInspectTask(userId, task.getId());
//                        if (!result.isSuccess()) {
//                            return result;
//                        }
//                    }
//                    return Result.ok(findInspectTask(task.getId()).orElse(null));
//                }).orElse(Result.error());
//    }
    @Deprecated
    public Optional<WorkflowNodeInstance> getCurrentNodeInstance(long userId, long instanceId) {
        return Optional.empty();
//        AtomicReference<User> userAtomicReference = new AtomicReference<>();
//        return userService.findUser(userId)
//                .flatMap(user -> {
//                    userAtomicReference.set(user);
//                    return findInstance(instanceId);
//                })
//                .map(instance -> {
//
//                    //是否我处理
//                    if (!checkAuth(instance.getWorkflowModel(), instance.getCurrentNode().getNodeModel(), userAtomicReference.get())) {
//                        return null;
//                    }
//
//                    return nodeInstanceDao.findFirstByInstanceAndFinishedIsFalse(instance).orElse(null);
//                });
    }


//    @Deprecated
//    
//    public Result<InspectTask> acceptInspectTask(long userId, long taskId) {
//        AtomicReference<User> userAtomicReference = new AtomicReference<>();
//        return userService.findUser(userId)
//                .flatMap(user -> {
//                    userAtomicReference.set(user);
//                    return findInspectTask(taskId);
//                })
//                .map(task -> {
//                    //如果这个任务已经指定了执行人员
//                    if (task.getDealUser() != null) {
//                        if (!task.getDealUser().getId().equals(userId)) {
//                            return Result.error("这个任务不属于你");
//                        }
//                    }
//
//                    task.setDealUser(userAtomicReference.get());
//                    task.setState(InspectTaskState.RECEIVED);
//                    task.setAcceptDate(new Date());
//
//                    //创建一条新的工作流任务
//                    List<WorkflowModel> models = modelDao.findAllByModelNameAndOpenIsTrueOrderByVersionDesc(task.getModelName());
//                    if (models.size() == 0) {
//                        return Result.error("没找到符合条件的工作流模型");
//                    }
//                    WorkflowInstance instance = null;
////                    WorkflowInstance instance = startNewInstance(userId, models.get(0).getId()).orElse(null);
//                    if (null == instance) {
//                        return Result.error("自动创建任务失败");
//                    }
//                    task.setInstance(instance);
//                    task = inspectTaskDao.save(task);
//                    return Result.ok(task);
//                }).orElse(Result.error());
//    }


//    @Deprecated
//    //如果userid为0,则指定为公共任务
//    public Page<InspectTask> getInspectTaskList(long uid, String modelName, InspectTaskState state, Pageable pageable) {
//        Specification querySpecifi = new Specification() {
//            
//            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
//                List<Predicate> predicates = new ArrayList<>();
//                predicates.add(cb.equal(root.get("dealUser"), uid == 0 ? null : uid));
//                if (!StringUtils.isEmpty(modelName)) {
//                    predicates.add(cb.equal(root.get("modelName"), modelName));
//                }
//                if (state != null) {
//                    predicates.add(cb.equal(root.get("modelName"), state));
//                }
//                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
//            }
//        };
//
//        return inspectTaskDao.findAll(querySpecifi, pageable);
//
//    }


    /**
     * 提交资料节点的数据
     *
     * @return
     */
//    private WorkflowInstance submitInputData(User user, WorkflowInstance wInstance, WorkflowNodeInstance wNInstance, InputNode nodeModel, Map data) {
////        nodeModel.submit(data);
//
////        nodeInstanceDao.save(wNInstance);
////        return instanceDao.save(wInstance);
//    }
//
//    private WorkflowInstance submitCheckData(User user, WorkflowInstance wInstance, WorkflowNodeInstance wNInstance, CheckNode nodeModel, String item, String ps) {
//
//        return instanceDao.save(wInstance);
//    }
    private void goNextNode(WorkflowInstance instance, WorkflowNodeInstance currentNode, WorkflowNode nextNode) {
        //本节点完毕
        currentNode.setFinished(true);
        currentNode.setDealDate(new Date());

        WorkflowNodeInstance newNode = instance.addNode(nextNode, false);
//        WorkflowNodeInstance newNode = new WorkflowNodeInstance();
//        newNode.setNodeModelId(nextNode.getId());
//        newNode.setNodeModel(nextNode);
//        newNode.setNodeName(nextNode.getName());
//        newNode.setType(nextNode.getType());
        newNode.setFinished(nextNode.isEnd());
//        newNode.setInstance(instance);
        newNode = nodeInstanceDao.save(newNode);
        instance.getNodeList().add(newNode);

        if (null != nextNode.getNode() && nextNode.getType().equals(WorkflowNode.Type.end)) {
            instance.setFinishedDate(new Date());
            instance.setState(WorkflowInstance.State.FINISHED);
            //归档整理
            //处理自定义行为
            JSONObject end = nextNode.getNode();
            if (!StringUtils.isEmpty(end.getString("behavior"))) {
                try {
                    runJSCode(instance, currentNode, end.getString("behavior"));
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
        }
        //如果是逻辑节点
        else if (nextNode.getType().equals(WorkflowNode.Type.logic)) {
            runLogicNode(instance, newNode);
        }
    }


    private Object runJSCode(WorkflowInstance instance, WorkflowNodeInstance currentNode, String behavior) throws ScriptException {
        SimpleScriptContext context = prepareJSContext(instance, currentNode);
        return engine.eval(behavior, context);
    }

    private SimpleScriptContext prepareJSContext(WorkflowInstance instance, WorkflowNodeInstance currentNode) {
        SimpleScriptContext context = new SimpleScriptContext();
        context.setAttribute("tools", new JSInterface(instance, currentNode), ScriptContext.ENGINE_SCOPE);
        buildJSLibrary(context);
        return context;
    }

    private void buildJSLibrary(ScriptContext context) {
        try {
            engine.eval(cache.getBehaviorString(), context);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void runLogicNode(WorkflowInstance instance, WorkflowNodeInstance node) {
        String key = "logic_node_" + node.getId();
        try {
            JSONObject logicNode = getCurrentNodeModelNode(node.getId()).orElse(null);
            if (null == logicNode) {
                return;
            }

            //检查上一次执行是否在时间范围内
            Optional<TaskCheckLog> optional = taskCheckLogDao.findFirstByTypeAndLinkIdOrderByLastCheckDateDesc(TaskCheckLog.Type.LOGIC_NODE, node.getId());
            if (optional.isPresent()) {
                if (new Date().getTime() - optional.get().getLastCheckDate().getTime() < logicNode.getInteger("interval") * 1000) {
                    return;
                }
            }
            //分布锁
            if (utils.isLockingOrLockFailed(key, 5)) {
                return;
            }

            SimpleScriptContext context = prepareJSContext(instance, findCurrentNodeInstance(instance.getId()).orElse(null));
            Object obj = engine.eval(logicNode.getString("condition"), context);
            //如果没取到值的情况, 等待下一次检测
            if (obj == null) {
                delayRunLogicNodeTask(instance, node);
            } else {
                String behavior = logicNode.getJSONObject("result").getString(String.valueOf(obj));
                //都没有命中的情况下, 走else
                if (behavior == null && logicNode.getJSONObject("result").containsKey("else")) {
                    behavior = logicNode.getJSONObject("reuslt").getString("else");
                }
                if (behavior != null) {
                    engine.eval(behavior, context);
                }
                saveWorkflowInstance(instance);
            }
        } catch (ScriptException e) {
            e.printStackTrace();
            delayRunLogicNodeTask(node.getInstance(), node);
        } finally {
            utils.unlock(key);
        }
    }

    private void delayRunLogicNodeTask(WorkflowInstance instance, WorkflowNodeInstance node) {
        //只插入这次的检查记录即可
        TaskCheckLog checkLog = new TaskCheckLog();
        checkLog.setLinkId(node.getId());
        checkLog.setType(TaskCheckLog.Type.LOGIC_NODE);
        taskCheckLogDao.save(checkLog);
    }


    private String validField(JSONArray rules, String value) throws RestException {
        final String ERROR_MESSAGE = "message";
        List<String> errorMessages = rules.stream().map(item -> {
            JSONObject rule = (JSONObject) item;
            boolean flag = false;
            boolean notEmpty = null != value && !StringUtils.isEmpty(value);
            switch (WorkflowModel.FieldRule.valueOf(rule.getString("rule"))) {
                case Null:
                    if (notEmpty) {
                        flag = true;
                    }
                    break;

                case NotNull:
                    if (notEmpty) {
                        flag = true;
                    }
                    break;

                case NotEmpty:
                    if (notEmpty) {
                        flag = true;
                    }
                    break;

                case AssertTrue:
                    if (notEmpty && value.equals("true")) {
                        flag = true;
                    }
                    break;

                case AssertFalse:
                    if (notEmpty && value.equals("false")) {
                        flag = true;
                    }
                    break;

                case Min:
                    if (NumberUtils.isNumber(value) && NumberUtils.toInt(value, 0) >= rule.getInteger("value")) {
                        flag = true;
                    }
                    break;

                case Max:
                    if (NumberUtils.isNumber(value) && NumberUtils.toInt(value, 0) <= rule.getInteger("value")) {
                        flag = true;
                    }
                    break;

                case DecimalMax:
                    if (NumberUtils.isNumber(value) && NumberUtils.toDouble(value, 0.00) >= rule.getDouble("value")) {
                        flag = true;
                    }
                    break;

                case DecimalMin:
                    if (NumberUtils.isNumber(value) && NumberUtils.toDouble(value, 0.00) <= rule.getDouble("value")) {
                        flag = true;
                    }
                    break;

                case Past:
                    break;

                case Future:
                    break;

                case Length:
                    if (notEmpty && value.length() <= rule.getInteger("value")) {
                        flag = true;
                    }
                    break;

                case Pattern:
                    if (notEmpty) {
                        Pattern p = Pattern.compile(rule.getString("value"));
                        Matcher m = p.matcher(value);
                        if (m.find()) {
                            flag = true;
                        }
                    }
                    break;

            }
            if (!flag) {
                return rule.getString(ERROR_MESSAGE);
            } else {
                return "";
            }
        }).filter(item -> !org.apache.commons.lang.StringUtils.isEmpty((String) item)).collect(Collectors.toList());
        return org.apache.commons.lang.StringUtils.join(errorMessages.toArray(), "\n");
    }

    private WorkflowInstance fromInputNodeToGo(WorkflowInstance instance, WorkflowNodeInstance currentNode, JSONObject nodeModel) throws RestException {
        //检查所有字段, 如果有必填字段没有填写, 那么抛出异常
        for (Map.Entry<String, Object> entry : nodeModel.getJSONObject("content").entrySet()) {
            String k = entry.getKey();
            JSONObject v = (JSONObject) entry.getValue();
            WorkflowNodeAttribute attribute = attributeDao.findFirstByDealUserIdAndAttrKey(currentNode.getDealerId(), k).orElse(null);
//            if(null)
//            if (v.getString("required").equals("y")) {
//                Optional<WorkflowNodeAttribute> target = currentNode.getAttributeList().stream()
//                        .filter(a -> a.getAttrKey().equals(v.getString("ename")))
//                        .findAny();
//                if (!target.isPresent()) {
//                    throw new RestException("有必填字段没有填写");
//                }
//            }
        }
        //找不到下一个就结束了吧
//        BaseNode nextNode = instance.getWorkflowModel().getNextNode(currentNode.getNodeName());
        WorkflowNode nextNode = findNextNodeModel(instance.getWorkflowModel(), currentNode.getNodeModel()).orElse(null);
        if (null == nextNode) return saveWorkflowInstance(instance);
        goNextNode(instance, currentNode, nextNode);
        return saveWorkflowInstance(instance);
    }

    private WorkflowInstance fromCheckNodeToGo(User user, WorkflowInstance instance, WorkflowNodeInstance currentNode, JSONObject nodeModel) throws RestException {

        //检查当前角色是否已经提交信息
        if (currentNode.getAttributeList().stream().filter(a -> a.getDealUser().getId().equals(user.getId())).count() == 0) {
            throw new RestException("你还没有提交信息");
        }

        JSONObject targetState = (JSONObject) nodeModel.getJSONArray("states")
                .stream()
                .filter(obj -> {
                    JSONObject state = (JSONObject) obj;
                    return currentNode.getAttributeList()
                            .stream()
                            .filter(a -> a.getAttrKey().equals(nodeModel.getString("key")) && a.getAttrValue().equals(state.getString("item")))
                            .count() >= state.getInteger("condition");
                })
                .findAny()
                .orElse(null);
//        for (CheckNodeState state : nodeModel.getStates()) {
//            long count = currentNode.getAttributeList()
//                    .stream()
//                    .count();
//            if (count >= state.getCondition()) {
//                targetState = state;
//                break;
//            }
//        n}

        if (targetState != null) {
            try {
                runJSCode(instance, findCurrentNodeInstance(instance.getId()).orElse(null), targetState.getString("behavior"));
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            return saveWorkflowInstance(instance);
        }
        return instance;
    }

//    private WorkflowInstance fromUniversalNodeToGo(WorkflowInstance instance, WorkflowNodeInstance currentNode, UniversalNode nodeModel) throws RestException {
//        //检查所有字段, 要不你就别填, 填了就必须填完
//        Set<User> users = new HashSet<>();
//        currentNode.getAttributeList().forEach(attribute -> {
//            users.add(attribute.getDealUser());
//        });
//        for (User user : users) {
//            for (Map.Entry<String, InputNode.Content> entry : nodeModel.getContent().entrySet()) {
//                InputNode.Content v = entry.getValue();
//                if (v.isRequired()) {
//                    Optional<WorkflowNodeAttribute> target = currentNode.getAttributeList().stream()
//                            .filter(a -> a.getDealUser().getId().equals(user.getId()) && a.getAttrKey().equals(v.getEname()))
//                            .findAny();
//                    if (!target.isPresent()) {
//                        return instance;
//                    }
//                }
//            }
//        }
//
//        return instance;
//    }


    /***js binding**/
    @AllArgsConstructor
    public class JSInterface {
        private WorkflowInstance instance;
        private WorkflowNodeInstance currentNode;
//        private Long instanceId;

        public void go(String nodeName) {
            WorkflowNode target = findNode(instance.getWorkflowModel(), nodeName).orElse(null);
            if (null == target) return;
            goNextNode(instance, currentNode, target);
        }


        /**
         * 开启后续任务
         *
         * @param modelName  模型名
         * @param dealerId   执行人ID
         * @param innateData 固有信息
         */
        public void start_next_task(String modelName, long dealerId, Map<String, String> innateData) {
            //查找该模型的最新版本
//            WorkflowModel model = modelDao.findFirstByModelNameAndOpenIsTrueOrderByVersionDesc(modelName).orElse(null);
            long modelId = 0L;
            List<Long> ids = modelDao.findModelId(modelName);
            if (ids.size() > 0) {
                modelId = ids.get(0);
            }
//            long modelId = modelDao.findModelId(modelName).orElse(0L);
            if (0 == modelId) return;
//            if(null == model) return;
            //
            if (0 == dealerId) return;
            //
            if (null == innateData) {
                innateData = new HashMap<>();
            }

            Map<String, String> data = convertInnates(instance.getAttributes());
            data.putAll(innateData);

            ApplyTaskRequest applyTaskRequest = new ApplyTaskRequest();
            applyTaskRequest.setModelId(modelId);
            applyTaskRequest.setCommon(false);
            applyTaskRequest.setManual(false);
            applyTaskRequest.setDealerId(dealerId);
            applyTaskRequest.setTitle(modelName);
            applyTaskRequest.setData(data);
            WorkflowInstance newInstance = startNewInstance(dealerId, applyTaskRequest).orElse(null);
            if (null != newInstance) {
                logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), dealerId, "创建了后续任务 " + applyTaskRequest.getTitle());
                newInstance.setPrevInstanceId(instance.getId());
                instanceDao.save(newInstance);
            }
        }

        public long get_task_dealer_id() {
            return instance.getDealUserId();
        }

        public Map<String, String> get_task_innate_data() {
            return instance.getAttributes().stream().filter(item -> item.getType().equals(WorkflowInstanceAttribute.Type.INNATE)).collect(Collectors.toMap(item -> item.getAttrKey(), item -> item.getAttrValue()));
        }

        public Map getLastNode(String nodeName) {
            int idex = instance.getNodeList().indexOf(currentNode);
            WorkflowNodeInstance nodeInstance = null;
            while (idex-- > 0) {
                nodeInstance = instance.getNodeList().get(idex);
                if (nodeInstance.getNodeName().equals(nodeName)) {
                    break;
                }
                nodeInstance = null;
            }
            if (nodeInstance == null) {
                return null;
            }
            Map<String, String> ret = new HashMap();
            nodeInstance.getAttributeList().forEach(attribute -> {
                ret.put(attribute.getAttrKey(), attribute.getAttrValue());
            });
            return ret;
        }
    }


    private boolean checkNodeAuth(WorkflowNodeInstance nodeInstance, User user) {
//        List<WorkflowModelPersons> persons = nodeInstance.getNodeModel().getPersons();
        //1. 处理人确认的时候, 只有该处理人可以处理
//        boolean flag1 = (null == nodeInstance.getDealerId() && persons.stream().anyMatch(p -> p.getType().equals(Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid())));
        boolean flag1 = (null == nodeInstance.getDealerId() && globalPermissionDao.hasPermission(user.getId(), Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), nodeInstance.getNodeModelId()) > 0);
        //2. 处理人不确认的时候, 拥有该权限的人都可以处理
        boolean flag2 = (null != nodeInstance.getDealerId() && user.getId().equals(nodeInstance.getDealerId()));
        return flag1 || flag2;
    }

//    private boolean CheckNodeAuth(WorkflowNodeInstance nodeInstance, long uid){
//
//    }

    @Deprecated
    private boolean checkAuth(WorkflowModel workflowModel, WorkflowNode node, User user) {
        return globalPermissionDao.hasPermission(user.getId(), Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), node.getId()) > 0;
//        List<WorkflowModelPersons> persons = node.getPersons();
//        return persons
//                .stream()
//                .anyMatch(p -> {
//                    return p.getWorkflowNode().getId().equals(node.getId()) && (p.getType().equals(Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()));

//                    return
//                            p.getNodeName().equals(node.getName()) && (
//                                    //该人符合个人用户的条件
////                                    (p.getType().equals(IWorkflowService.Type.MAIN_USER) && p.getUid().equals(user.getId())) ||
//                                            //该人所属的岗位符合条件
//                                            (p.getType().equals(IWorkflowService.Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()))
//                            );

//                });
    }

    private Map<String, String> convertInnates(List<WorkflowInstanceAttribute> attributes) {
        return attributes.stream().filter(item -> item.getType().equals(WorkflowInstanceAttribute.Type.INNATE))
                .collect(Collectors.toMap(item -> item.getAttrKey(), item -> item.getAttrValue()));
    }


//    @Deprecated
//    private void addWorkflowPersons(Set<Long> list, WorkflowNode node, String name, Type type) {
//        if(true){
//            return;
//        }
//        list.forEach(l -> {
//            String title = "";
//            switch (type) {
////                case MAIN_USER:
////                case SUPPORT_USER:
////                    User user = userService.findUser(l).orElse(null);
////                    if(null == user) return;
////                    title = user.getTrueName();
////                    break;
//                case MAIN_QUARTERS:
//                case SUPPORT_QUARTERS:
//                    Quarters quarters = userService.findQuarters(l).orElse(null);
//                    if (null == quarters) return;
//                    title = quarters.getName();
//                    break;
//            }
//            personsDao.save(new WorkflowModelPersons(null, name, node, type, l, title));
//        });
//    }
//    private WorkflowNodeInstance getCurrentNode(WorkflowInstance instance){
//        Optional<WorkflowNodeInstance> currentNode = instance.getNodeList()
//                .stream()
//                .filter(n -> !n.isFinished())
//                .findAny();
//        if(!currentNode.isPresent()) return null;
//        return currentNode.get();
//    }


    public WorkflowModel saveWorkflowModel(WorkflowModel workflowModel) {
        return modelDao.save(workflowModel);
    }

    public WorkflowInstance saveWorkflowInstance(WorkflowInstance workflowInstance) {
        return instanceDao.save(workflowInstance);
    }

    public Optional<WorkflowNode> findNextNodeModel(WorkflowModel model, WorkflowNode nodeModel) {
        if (nodeModel.isEnd()) return Optional.ofNullable(nodeModel);
        if (nodeModel.getNext().size() == 0) return Optional.empty();
        if (nodeModel.getType().equals(WorkflowNode.Type.input)) {
            String nextName = nodeModel.getNext().get(0);
            return model.getNodeModels().stream().filter(nm -> {
                return nm.getName().equals(nextName);
            }).findAny();
        }
        return Optional.empty();

    }

    public List<String> getAvaliableModelNames() {
        return modelDao.findAllByOpenIsTrue().stream()
                .map(item -> item.getName())
                .distinct()
                .collect(Collectors.toList());
    }

    @Deprecated
    public Optional<FetchWorkflowInstanceResponse> fetchWorkflowInstance(long uid, long id) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) return Optional.empty();
        return findInstance(id).map(workflowInstance -> {
            FetchWorkflowInstanceResponse response = new FetchWorkflowInstanceResponse();
            response.setInstance(workflowInstance);
            response.setCurrentNodeModel(findCurrentNodeModel(id).orElse(null));
            response.setDeal(canDeal(workflowInstance, user));
            response.setCancel(canCancel(workflowInstance, user));
            response.setRecall(canRecall(workflowInstance, user));
            response.setTransform(canTransform(workflowInstance, user));
            response.setAccept(canAccept(workflowInstance, user));
            response.setLogs(systemTextLogDao.findLogs(SystemTextLog.Type.WORKFLOW, id));
            response.setTransformUsers(getPubUids(workflowInstance.getModelId()));

            return response;
        });
    }

    public Optional<FetchWorkflowInstanceResponse> fetchInstance(long uid, long id) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) return Optional.empty();
        return findInstance(id).map(workflowInstance -> {
            FetchWorkflowInstanceResponse response = new FetchWorkflowInstanceResponse();
            response.setInstance(workflowInstance);
            response.setCurrentNodeModel(findCurrentNodeModel(id).orElse(null));
            response.setDeal(canDeal(workflowInstance, user));
            response.setCancel(canCancel(workflowInstance, user));
            response.setRecall(canRecall(workflowInstance, user));
            response.setTransform(canTransform(workflowInstance, user));
            response.setAccept(canAccept(workflowInstance, user));
//            response.setLogs(systemTextLogDao.findLogs(SystemTextLog.Type.WORKFLOW, id));
            response.setTransformUsers(getPubUids(workflowInstance.getModelId()));

            return response;
        });
    }

    public Optional<WorkflowNodeInstance> findCurrentNodeInstance(Long instanceId) {
        return nodeInstanceDao.findFirstByInstance_IdAndFinishedIsFalse(instanceId);
    }

    public Optional<WorkflowNode> findCurrentNodeModel(long instanceId) {
        return nodeInstanceDao.findFirstByInstance_IdAndFinishedIsFalse(instanceId).map(WorkflowNodeInstance::getNodeModel);
    }

    public Optional<WorkflowNode> findFirstNodeModel(long modelId) {
        return nodeDao.findFirstByModel_IdAndStartIsTrue(modelId);
    }

    public Optional<WorkflowNode> findNode(WorkflowModel model, String nodeName) {
        return nodeDao.findFirstByModelAndName(model, nodeName);
    }


    public Optional<WorkflowModel> findModel(long id) {
        return Optional.ofNullable(modelDao.findOne(id));
    }


    public Optional<WorkflowNode> findNode(long id) {
        return Optional.ofNullable(nodeDao.findOne(id));
    }

    public Optional<WorkflowNodeInstance> findNodeInstance(long id) {
        return Optional.ofNullable(nodeInstanceDao.findOne(id));
    }

    public Optional<WorkflowNodeFile> findNodeFile(long id) {
        return Optional.ofNullable(nodeFileDao.findOne(id));
    }

    public Optional<GPSPosition> findNodeGPSPosition(long nodeFileId) {
        return findNodeFile(nodeFileId)
                .filter(nodeFile -> nodeFile.getType().equals(WorkflowNodeFile.Type.POSITION))
                .map(nodeFile -> gpsPositionDao.getOne(nodeFile.getFileId()));
    }


    public WorkflowModel findModelE(long id) throws CannotFindEntityException {
        return findModel(id).orElseThrow(() -> new CannotFindEntityException(WorkflowModel.class, id));
    }


    public Optional<WorkflowInstance> findInstance(long id) {
        return Optional.ofNullable(instanceDao.findOne(id));
    }

    public WorkflowInstance findInstanceE(long id) throws CannotFindEntityException {
        return findInstance(id).orElseThrow(() -> new CannotFindEntityException(WorkflowInstance.class, id));
    }

//    
//    public Optional<InspectTask> findInspectTask(long id) {
//        return Optional.ofNullable(inspectTaskDao.findOne(id));
//    }
//
//    
//    public InspectTask findInspectTaskE(long id) throws CannotFindEntityException {
//        return findInspectTask(id).orElseThrow(() -> new CannotFindEntityException(InspectTask.class, id));
//    }


    /**
     * 得到固有字段
     * @param node
     * @return
     */

//    public Map<String,InputNode.Content> findInnateFields(long modelId){
//        WorkflowNode node = findFirstNodeModel(modelId).orElse(null);
//        if(null == node) return new HashMap<>();
//        if(node.getNode() instanceof InputNode){
//            return findInnateFields((InputNode) node.getNode());
//        }
//        else{
//            return new HashMap<>();
//        }
//    }


    /************ 节点提交 *************/

    private JSONObject buildInputNode(WorkflowModel workflowModel, Map v) {
        JSONObject result = new JSONObject();
        JSONObject cnt = new JSONObject();
        result.put("content", cnt);
        Map<String, Object> content = (Map) v.get("content");
        if (content != null) {
            content.entrySet().stream().forEach(entry -> {
                String ck = entry.getKey();
                Object cv = entry.getValue();

                JSONObject object = new JSONObject();
                if (cv instanceof Map) {
                    Map cvmap = (Map) cv;
                    object = (JSONObject) JSON.toJSON(cv);

                    //ename
                    object.put("ename", object.getString("name"));
                    object.remove("name");

                    object.put("cname", String.valueOf(ck));

//                    object.put("type",cvmap.get("type"));
//                    object.put("type",cvmap.get("type"));
//                    cnt.setType((String) ((Map) cv).get("type"));
//                    cnt.setEname((String) ((Map) cv).get("name"));
//
//                    String required = (String) cvmap.getOrDefault("required","n");
//                    cnt.setRequired(required.equals("y"));
//                    //特殊类型
//                    List items = (List) ((Map) cv).getOrDefault("items",new ArrayList<>());
//                    cnt.getItems().addAll((Collection<? extends String>) items.stream().map(item -> String.valueOf(item)).collect(Collectors.toList()));

                    //固有字段
                    boolean innate = (boolean) cvmap.getOrDefault("innate", false);
                    if (innate) {
                        WorkflowModelInnate workflowModelInnate = new WorkflowModelInnate();
                        workflowModelInnate.setContent(object);
                        workflowModelInnate.setFieldName(object.getString("ename"));
                        workflowModelInnate.setModel(workflowModel);
                        workflowModel.getInnates().add(workflowModelInnate);
                        return;
                    }

                } else if (cv instanceof String) {
                    List<String> args = Utils.splitByComma(String.valueOf(cv));
                    if (args.size() != 3) {
                        return;
                    }
                    object.put("ename", args.get(0));
                    object.put("type", args.get(1));
                    object.put("required", args.get(0).equals("y"));
                    object.put("cname", String.valueOf(ck));
//                    cnt.setEname(args.get(0));
//                    cnt.setType(args.get(1));
//                    cnt.setRequired(args.get(2).equals("y"));
                } else {
                    return;
                }
                cnt.put(object.getString("ename"), object);
            });
        }
        return result;
    }

    private JSONObject buildCheckNode(Map v) {
        return (JSONObject) JSON.toJSON(v);
    }

    private JSONObject buildLogicNode(Map v) {
        return (JSONObject) JSON.toJSON(v);
    }

    private JSONObject buildNormalNode(Map v) {
        return (JSONObject) JSON.toJSON(v);
    }


    public String submitInputNode(long uid, long nodeInstanceId, Map<String, Object> data) {
        //当前处理的
//        WorkflowNodeInstance nodeInstance = nodeInstanceDao.findFirstByIdAndFinishedIsFalse(nodeInstanceId).orElse(null);
//        if(null == nodeInstance){
//            return;
//        }
        JSONObject node = getCurrentNodeModelNode(nodeInstanceId).orElse(null);
        if (null == node) {
            return null;
        }
        //TODO: 权限校验
        JSONObject content = node.getJSONObject("content");
        for (Map.Entry<String, Object> entry : content.entrySet()) {
            JSONObject v = (JSONObject) entry.getValue();
            String attrKey = v.getString("ename");
            if (!data.containsKey(attrKey)) {
                continue;
            }
            //不论是否必填, 空属性就略过
//            if (StringUtils.isEmpty(String.valueOf(data.get(attrKey)))) {
//                continue;
//            }
            //验证属性格式
            //校验该字段的规则
            if (v.containsKey("rules")) {
                try {
                    String value = String.valueOf(data.get(attrKey));
                    String err = validField(v.getJSONArray("rules"), value);
                    if (!StringUtils.isEmpty(err)) {
                        throw new RestException(err);
                    }
                } catch (Exception e) {
                    return v.getString("cname") + "格式校验错误";
                }
            }

            //覆盖旧节点的信息
//            Optional<WorkflowNodeAttribute> target = nodeInstance.getAttributeList()
//                    .stream()
//                    .filter(attr -> attr.getAttrKey().equals(attrKey))
//                    .findFirst();
//            WorkflowNodeAttribute attribute = target.orElse(new WorkflowNodeAttribute());
            WorkflowNodeAttribute attribute = addAttribute(uid, nodeInstanceId, attrKey, String.valueOf(data.get(attrKey)), v.getString("cname"));
//            attribute.setAttrKey(attrKey);
//            attribute.setAttrValue(String.valueOf(data.get(attrKey)));
//            attribute.setAttrCname(v.getString("cname"));
//            attribute.setDealUserId(uid);
//            attribute.setNodeInstanceId(nodeInstanceId);
            attributeDao.save(attribute);
        }

        return null;
    }

    public String submitCheckNode(long uid, long nodeInstanceId, Map data) {
        JSONObject node = getCurrentNodeModelNode(nodeInstanceId).orElse(null);
        if (null == node) {
            return "找不到该节点";
        }
        String item = String.valueOf(data.get(node.getString("key")));
        String ps = String.valueOf(data.get(node.getString("ps")));
        //如果可选项不在选项里面, 那么无视
        boolean hasOption = node.getJSONArray("states")
                .stream()
                .anyMatch(state -> ((JSONObject) state).getString("item").equals(item));
        if (!hasOption) {
            return "选项内没有这个选项";
        }
        //每个审批节点只允许审批一次
        WorkflowNodeAttribute attribute = addAttribute(uid, nodeInstanceId, node.getString("key"), item, node.getString("question"));
        attributeDao.save(attribute);

        //如果填写了审核说明
        //valueof会格式化null
        if (ps.equals("null")) {
            ps = "";
        }
        if (!StringUtils.isEmpty(ps)) {
            attribute = addAttribute(uid, nodeInstanceId, node.getString("ps"), ps, "备注");
            attributeDao.save(attribute);
        }

        return "";
    }

    public WorkflowNodeAttribute addAttribute(Long uid, long nodeInstanceId, String key, String value, String cname) {
        WorkflowNodeAttribute attribute = attributeDao.findFirstByNodeInstanceIdAndAttrKey(nodeInstanceId, key).orElse(new WorkflowNodeAttribute());
        attribute.setDealUserId(uid);
        attribute.setAttrKey(key);
        attribute.setAttrValue(value == null ? "" : value);
        attribute.setNodeInstanceId(nodeInstanceId);
        attribute.setAttrCname(cname);
        return attribute;
    }


    private boolean isGlobalPermission() {
        return permissionType.equalsIgnoreCase("global");
    }


    /*** 工具类函数 ***/


    /**
     * 当前正在执行的模型工作流节点模型
     *
     * @param nodeInstanceId
     * @return
     */
    public Optional<JSONObject> getCurrentNodeModelNode(long nodeInstanceId) {
        String hql = "select ni.nodeModel.node from WorkflowNodeInstance ni where ni.id = :id and ni.finished = false";
        List result = entityManager.createQuery(hql)
                .setParameter("id", nodeInstanceId)
                .setMaxResults(1)
                .getResultList();
//                .getSingleResult();
        JSONObject ret = result.size() > 0 ? (JSONObject) result.get(0) : null;
        return Optional.ofNullable(ret);
    }

    public List<Long> getPubUids(long modelId) {
        return globalPermissionDao.getUids(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), modelId);
    }


//    public Optional<WorkflowNodeAttribute> getNodeAttribute(long uid, String key){
//
//        log.info(key);
//        log.info(uid + "");
//        List<WorkflowNodeAttribute> list = entityManager
//                .createQuery("select attr from WorkflowNodeAttribute attr where attr.attrKey = :k and attr.dealUserId = :uid",WorkflowNodeAttribute.class)
//                .setParameter("uid",uid)
//                .setParameter("k",key)
//                .setMaxResults(1)
//                .getResultList();
//        return Optional.ofNullable(list.size() > 0 ? list.get(0) : null);
//    }
}
