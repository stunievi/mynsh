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
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.FileNameMap;
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
    IWorkflowModelFieldDao fieldDao;
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
    @Autowired
    SystemNoticeService noticeService;

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
     * @param uid 启动该任务的用户ID
     * @return
     */
    public Result startNewInstance(Long uid, ApplyTaskRequest request) {
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
            if (!canPoint(workflowModel.getId(), pubUser.getId())) {
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
                if (!canPoint(workflowModel.getId(), pubUser.getId())) {
                    return Result.error("你无权发布该任务");
                }
                workflowInstance.setDealUserId(null);
            }

            //验证执行人是否有权限
            //TODO:
        }


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
        workflowInstance.setModelName(workflowModel.getModelName());
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
                workflowInstance.setSecondState(WorkflowInstance.SecondState.POINT);
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
                case "诉讼":
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
//                    for (Map.DisallowEntry<String, String> entry : rs.get(0).entrySet()) {
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
     * @param uid 接受人ID
     * @param instanceId 任务ID
     * @return 成功返回空字符串, 失败返回对应的失败信息
     */
    public String acceptInstance(long uid, long instanceId) {
        if(!userService.exists(uid)){
            return ("不存在该用户");
        }
        WorkflowInstance instance = findInstance(instanceId).orElse(null);
        if (null == instance){
            return ("该任务不符合接受条件");
        }
        if (!canAccept(instance, uid)) {
            return ("用户没有权限接受任务");
        }
        //锁定该任务
        entityManager.refresh(instance, PESSIMISTIC_WRITE);
        instance.setDealUserId(uid);
        instance.setState(WorkflowInstance.State.DEALING);
        entityManager.merge(instance);

        //绑定第一个节点
        WorkflowNodeInstance nodeInstance = instance.getNodeList().get(0);
        nodeInstance.setDealerId(uid);
//        nodeInstance.setDealer(user);
        nodeInstanceDao.save(nodeInstance);

        //message
        noticeService.addNotice(SystemNotice.Type.WORKFLOW, uid, "你已经接受任务${taskId}", ImmutableMap.of("taskId",instance.getId(),"taskName",instance.getTitle()));
        //写log
        logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "接受了任务");
        return "";
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
                instanceDao.deleteById(workflowInstance.getId());
                return true;
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
     * @param uid 撤回人ID
     * @param instanceId 任务ID
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
    public boolean canDeal(long instanceId, long uid){
        WorkflowNodeInstance nodeInstance = nodeInstanceDao.getCurrentNodeInstance(instanceId).orElse(null);
        return null != nodeInstance && checkNodeAuth(nodeInstance, uid);
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
        return null != instance.getDealUserId() && instance.getDealUserId().equals(user.getId()) &&
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
                && canPoint(instance.getModelId(), user.getId());
//        instance.getWorkflowModel().getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
    }


    /**
     * 是否可以接受任务
     *
     * @param instance 任务
     * @param uid 与用户ID
     * @return
     */
    public boolean canAccept(WorkflowInstance instance, long uid) {
        return instance.getState().equals(WorkflowInstance.State.COMMON) && canPub(instance.getModelId(), uid);
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
        return canPoint(model.getId(), user.getId()) || canPub(model, user);
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
     * @param modelId 模型ID
     * @param uid 指派的用户
     * @return
     */
    public boolean canPoint(final long modelId, final long uid) {
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
//        WorkflowNode firstNode = nodeDao.findAllByModelAndStartIsTrue(instance.getWorkflowModel()).get(0);
        return
                //1.任务正在进行中
                instance.getState().equals(WorkflowInstance.State.DEALING) &&
                        //2.拥有指派该任务的权利
                        canPoint(instance.getModelId(), user.getId());
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
        return canPoint(model.getId(), user.getId());
    }



    /**
     * 得到某些用户未执行的任务
     *
     * @param uids
     * @param lessId
     * @param pageable
     * @return
     */
    public Page getUserUndealedWorks(Collection<Long> uids, Long lessId, Pageable pageable) {
        if (lessId == null) {
            lessId = Long.MAX_VALUE;
        }
        //得到所有我可以处理的模型
        List<Long> oids = globalPermissionDao.getObjectIds(Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER),uids);
        if(oids.size() == 0){
            oids.add(-1L);
        }
        System.out.println(JSON.toJSONString(oids));
        String hql = "select distinct i from WorkflowInstance i, User u " +
                "join i.nodeList nl " +
                "where " +
                //节点处理人是我自己
                "( (nl.dealerId is not null and nl.dealerId in :uids) or " +
                //为空的情况,寻找可以处理的人
                "(nl.dealerId is null and nl.nodeModelId in :oids ) ) and " +
                //该节点任务未完成
                "nl.finished = false and " +
                //任务进行中
                "i.state = 'DEALING' and " +
                //分页
                "i.id <= :lessId ";
        return sqlUtils.hqlQuery(hql,ImmutableMap.of(
                "oids", oids,
                "lessId", lessId,
                "uids",uids
        ),"distinct i",pageable);
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
        List oids = globalPermissionDao.getManagerObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), uids);
        Long finalLessId = lessId;
        Specification query = new Specification() {

            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(root.get("state").in(new Object[]{
                        WorkflowInstance.State.UNRECEIVED,
                        WorkflowInstance.State.DEALING,
                        WorkflowInstance.State.COMMON,
                        WorkflowInstance.State.PAUSE,
                }));
                predicates.add(cb.lessThan(root.get("id"), finalLessId));
                Join join = root.join("workflowModel");
                //关联模型
                if(oids.size() > 0){
                    predicates.add(join.get("id").in(oids));
                }
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
                //模型名称
                if(!StringUtils.isEmpty(request.getModelName())){
                    String[] modelNames = request.getModelName().split(",");
                    predicates.add(root.get("modelName").in(modelNames));
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
    public Result pointTask(long uid, Collection<Long> iids, long toUid) {
        List<Long> success = new ArrayList<>();
        List<String> errMessages = new ArrayList<>();
        for (Long iid : iids) {
            WorkflowInstance instance = findInstance(iid).orElse(null);
            if (null == instance) {
                errMessages.add("找不到该任务");
                continue;
            }

            //检查是否有指派权限
            if (!canPoint(instance.getModelId(), uid)) {
                errMessages.add("没有指派/移交权限");
                continue;
            }

            //接受人是否有执行权限
            if (!canPub(instance.getModelId(), toUid)) {
                errMessages.add("被指定的人没有接受权限");
                continue;
            }

            //不能自己移交给自己
            if(null != instance.getDealUserId() && instance.getDealUserId().equals(toUid)){
                errMessages.add("任务无法给相同的人");
                continue;
            }

            //任务状态是否合法
            if (!instance.getState().equals(WorkflowInstance.State.COMMON) && !instance.getState().equals(WorkflowInstance.State.UNRECEIVED) || !instance.getState().equals(WorkflowInstance.State.DEALING)) {
                errMessages.add("该任务状态无法移交");
                continue;
            }

            //子任务禁止移交
            if (instance.getParentNodeId() != null) {
                errMessages.add("子任务禁止移交");
                continue;
            }

            WorkflowInstanceTransaction transaction = new WorkflowInstanceTransaction();
            transaction.setFromState(instance.getState());
            transaction.setToState(WorkflowInstance.State.DEALING);
            transaction.setInstanceId(instance.getId());
            transaction.setUserId(toUid);
            transactionDao.save(transaction);
            instance.setState(WorkflowInstance.State.PAUSE);
            if(instance.getState().equals(WorkflowInstance.State.DEALING)){
                instance.setSecondState(WorkflowInstance.SecondState.TRANSFORM);
            }
            else{
                instance.setSecondState(WorkflowInstance.SecondState.POINT);
            }
            saveWorkflowInstance(instance);

            success.add(instance.getId());

            //notice
            noticeService.addNotice(SystemNotice.Type.WORKFLOW, toUid, "你有一条新的任务指派/移交${taskId}", ImmutableMap.of(
                    "taskId", instance.getId(),
                    "taskName", instance.getTitle()
            ));
        }

        if(errMessages.size() > 0){
            return Result.error(StringUtils.join(errMessages.toArray(),","));
        }
        else{
            return Result.ok(success);
        }
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

//        boolean needToSendMessage = ;

        WorkflowNode nodeModel = nodeInstance.getNodeModel();
        String errMessage = null;
        switch (nodeModel.getType()) {
            case input:
                errMessage = submitInputNode(uid, request.getNodeId(), request.getData());
                //如果这个任务之前处于争抢的任务
                //如果你在处理列表中却没有处理, 那么给你发送消息
                if(null == nodeInstance.getDealerId()){
                    List<Long> notDealedUids = globalPermissionDao.getUids(Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), nodeModel.getId()).stream()
                            .filter(uuid -> !uuid.equals(uid))
                            .collect(Collectors.toList());
                    noticeService.addNotice(SystemNotice.Type.WORKFLOW,notDealedUids, "你的任务${taskId}节点${nodeId}已被别人处理",ImmutableMap.of(
                            "taskId",instance.getId(),
                            "taskName",instance.getTitle(),
                            "nodeId", nodeInstance.getId(),
                            "nodeName", nodeInstance.getNodeName()
                    ));
                }
                if(StringUtils.isEmpty(errMessage)){
                    nodeInstance.setDealerId(uid);
                    nodeInstanceDao.save(nodeInstance);
                }
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
//        User user = userService.findUser(uid).orElse(null);
//        if (null == user) {
//            return Result.error("授权错误");
//        }
        WorkflowInstance instance = nodeInstance.getInstance();
        //如果是第一个节点, 并且授权人已经确认, 那么只检查pub权限即可
        if (null != nodeInstance.getDealerId() && nodeInstance.getDealerId().equals(uid)) {

        }
        //验证权限
        else if (!checkNodeAuth(nodeInstance, uid)) {
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
                    fromCheckNodeToGo(uid, instance, nodeInstance, nodeModel.getNode());
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
    public Result addPosition(final long uid, WorkflowPositionAddRequest request) {
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
    public boolean deleteNodeFile(final long uid, final long nodeFileId) {
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
        nodeFileDao.deleteById(nodeFileId);
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
    public Result uploadNodeFile(final long uid, final long instanceId, final long nodeId, WorkflowNodeFile.Type fileType, MultipartFile file, String content, String tag) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return Result.error();
        }
        WorkflowNodeInstance nodeInstance = nodeInstanceDao.getCurrentNodeInstance(instanceId).orElse(null);
        if (null == nodeInstance || !nodeInstance.getId().equals(nodeId) ) {
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
        systemFile.setExt(FilenameUtils.getExtension(systemFile.getFileName()));
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
        if(!StringUtils.isEmpty(tag)){
            List<String> list = Arrays.stream(tag.split(" "))
                    .filter(item -> !StringUtils.isEmpty(item))
                    .distinct()
                    .collect(Collectors.toList());
            nodeFile.setTags(StringUtils.join(list.toArray()," "));
        }
        nodeFile = nodeFileDao.save(nodeFile);
        return Result.ok(nodeFile);
    }

    /**
     * 设置节点标签
     * @param uid
     * @param nodeId
     * @param tags
     * @return
     */
    public boolean setNodeFileTags(final long uid, final long nodeId, String tags) {
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

    /**
     * 节点文件重命名
     * @param uid
     * @param nodeId
     * @param name
     * @return
     */
    public boolean setNodeFileName(long uid, long nodeId, String name){
        //处理名字
        WorkflowNodeFile nodeFile = findNodeFile(nodeId).orElse(null);
        if(null == nodeFile){
            return false;
        }
        name = FilenameUtils.removeExtension(name);
        if(!StringUtils.isEmpty(nodeFile.getExt())){
            name = name + "." + nodeFile.getExt();
        }
        return nodeFileDao.updateNodeFileName(uid, nodeId, name) > 0;
    }

    /**
     * 申请下载文件
     * @param uid
     * @param id
     * @return
     */
    public String applyDownload(long uid, long id){
        WorkflowNodeFile nodeFile = findNodeFile(id).orElse(null);
        if(null == nodeFile){
            return "";
        }
        //TODO: 检查是否拥有查看该节点的权限
        DownloadFileToken token = new DownloadFileToken();
        token.setExprTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        token.setToken(UUID.randomUUID().toString());
        token.setFileId(nodeFile.getFileId());
        downloadFileTokenDao.save(token);
        return token.getToken();
    }


    /**
     * 编辑工作流模型
     * @param edit 模型编辑详情
     * @return
     */
    public Result editWorkflowModel(WorkflowModelEdit edit) {
        WorkflowModel model = findModel(edit.getId()).orElse(null);
        if(null == model){
            return Result.error("找不到这个工作流");
        }
        if (edit.isOpen()) {
            //查找是否有同名已开启的流程
            if(modelDao.countByNameAndOpenIsTrue(model.getName()) > 0){
                return Result.error("还有同名的工作流没有关闭, 无法开启");
            }
            model.setFirstOpen(true);
        }
        //流程名字修改
        if (!StringUtils.isEmpty(edit.getName())) {
            model.setName(edit.getName());
        }
        //描述可以随意修改
        if (!StringUtils.isEmpty(edit.getInfo())) {
            model.setInfo(edit.getInfo());
        }
        if(null != edit.getProcessCycle()){
            model.setProcessCycle(edit.getProcessCycle());
        }
        //开关也可以
        model.setOpen(edit.isOpen());

        modelDao.save(model);
        return Result.ok();
    }


    /**
     * 删除节点
     *
     * @param modelId 模型ID
     * @param nodeId 节点ID
     * @return 是否删除成功
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
     * @param modelName 要创建的模型名称
     * @param add 请求明细
     * @return 是否创建成功
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
                if (!StringUtils.isEmpty((String) next)) {
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
        workflowModel.setProcessCycle(add.getProcessCycle());

        if (workflowModel.getInnates().size() > 0) {
            for (WorkflowModelInnate workflowModelInnate : workflowModel.getInnates()) {
                innateDao.save(workflowModelInnate);
            }
//            innateDao.save(workflowModel.getInnates());
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


    /**
     * 删除工作流模型
     * @param id 模型ID
     * @param force 强制删除
     * @return 是否成功
     */
    public boolean deleteWorkflowModel(long id, boolean force) {
        if (force) {
            modelDao.deleteById(id);
            return true;
        }
        return modelDao.deleteWorkflowModel(id) > 0;
    }


    /**
     * 设置模型关联字段
     * @param modelId 模型ID
     * @param requests 请求明细
     * @return 设置结果
     */
    public Result setModelFields(long modelId, Collection<ModelFieldRequest> requests){
        if(!modelDao.existsById(modelId)){
            return Result.error("不存在该工作流模型");
        }
        List<String> names = new ArrayList<>();
        for (ModelFieldRequest request : requests) {
            WorkflowModelField field = fieldDao.findTopByModelIdAndName(modelId,request.getName()).orElse(new WorkflowModelField());
            field.setType(request.getType());
            field.setName(request.getName());
            field.setHint(request.getHint());
            field.setModelId(modelId);
            field.setExt(request.getExt());
            fieldDao.save(field);
            names.add(request.getName());
        }
        //清除无用的数据
        if(names.size() == 0){
            fieldDao.deleteAllByModelId(modelId);
        }
        else{
            fieldDao.deleteAllByModelIdAndNameNotIn(modelId, names);
        }
        return Result.ok();
    }
    public List<WorkflowModelField> getModelFields(long modelId){
        return fieldDao.findAllByModelId(modelId);
    }

    /**
     * 模型列表获取
     * @param request
     * @param pageable
     * @return
     */
    public Page getModelList(ModelSearchRequest request, Pageable pageable){
        Specification query = ((root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"),false));
            if(!StringUtils.isEmpty(request.getModelName())){
                predicates.add(cb.equal(root.get("modelName"),request.getModelName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        });
        return modelDao.findAll(query,pageable);
    }

    /**
     * 工作流实例列表查询
     * @param uid
     * @param object
     * @param pageable
     * @return
     */
    public Page getInstanceList(long uid, Map<String,String> object, Pageable pageable){
        Specification query = ((root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            //限制我自己的任务
            predicates.add(cb.equal(root.get("dealUserId"), uid));
            for (Map.Entry<String, String> entry : object.entrySet()) {
                if(StringUtils.isEmpty(entry.getValue())){
                    continue;
                }
                switch (entry.getKey()){
                    case "modelName":
                        String[] modelNames = entry.getValue().split(",");
                        predicates.add(root.get("modelName").in(modelNames));
                        break;
                    case "page":
                    case "size":
                    case "sort":
                    case "Authorization":
                        break;


                    //默认字段查询
                    default:
                        Join nl = root.join("nodeList");
                        Join attr = nl.join("attributeList");
                        if(entry.getKey().startsWith("$")){
                            predicates.add(
                                    cb.and(
                                            cb.equal(attr.get("attrKey"),entry.getKey()),
                                            cb.like(attr.get("attrValue"),"%" + entry.getValue().substring(1) + "%")
                                    )
                            );
                        }
                        else{
                            predicates.add(
                                    cb.and(
                                            cb.equal(attr.get("attrKey"),entry.getKey()),
                                            cb.equal(attr.get("attrValue"),entry.getValue())
                                    )
                            );
                        }
                        break;
                }
            }
//            if(object.containsKey("modelName") && !StringUtils.isEmpty(object.getString("modelName"))){
//            }
//            //资料收集是否拒贷
//            Integer reject = object.getInteger("reject");
//            if(null != reject){
//                if(reject == 1){
//                    predicates.add(cb.and(cb.equal(attr.get("attrKey"),"key"), cb.equal(attr.get("attrValue"), "是")));
//                }
//                else{
//                    predicates.add(cb.and(cb.equal(attr.get("attrKey"),"key"), cb.equal(attr.get("attrValue"), "否")));
//                }
//            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));

        });
        Page<WorkflowInstance> instances = instanceDao.findAll(query,pageable);
        instances.getContent().forEach(instance -> {
            instance.setCanDeal(canDeal(instance.getId(),uid));
        });
        return instances;
    }

    /**
     * 得到我可以发布的模型列表
     * @param uid
     * @return
     */
    public List<WorkflowModel> getUserModelList(long uid){
        List<Long> pubIds = globalPermissionDao.getObjectIds(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB),Collections.singleton(uid));
        List<Long> pointIds = globalPermissionDao.getManagerObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB),Collections.singleton(uid));
        List<WorkflowModel> models = modelDao.getAllWorkflows()
                .stream()
                .filter(model -> pubIds.contains(model.getId()) || pointIds.contains(model.getId()))
                .peek(model -> {
                    model.setPub(pubIds.contains(model.getId()));
                    model.setPoint(pointIds.contains(model.getId()));
                }).collect(Collectors.toList());
        return models;
    }

    @Data
    public static class ModelSearchRequest{
        String modelName;
    }



    /**
     * 创建审核节点
     * @param nodeModel
     * @return
     */
    public Optional<WorkflowNode> createCheckNode(CheckNodeModel nodeModel) {
        return findModel(nodeModel.getModelId()).map(model -> {
            //无法自定义的流程不允许自定义
            if (!model.isCustom()) {
                return null;
            }
            if(model.isFirstOpen()){
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


    private void goNextNode(WorkflowInstance instance, WorkflowNodeInstance currentNode, WorkflowNode nextNode) {
        WorkflowNode currentNodeModel = findNode(currentNode.getNodeModelId()).orElse(null);

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
        //资料节点的回卷, 如果曾经存在提交, 那么重新附上所有值以及文件
        else if(newNode.getType().equals(WorkflowNode.Type.input) || newNode.getType().equals(WorkflowNode.Type.check)){
            if(nodeInstanceDao.countByInstanceIdAndNodeModelId(newNode.getInstanceId(),newNode.getNodeModelId()) >= 2){
                WorkflowNodeInstance lastNode = nodeInstanceDao.findTopByInstanceIdAndNodeModelIdAndIdLessThanOrderByIdDesc(newNode.getInstanceId(),newNode.getNodeModelId(), newNode.getId()).orElse(null);
                if(null == lastNode){

                }
                else{
                    if(null != lastNode.getDealerId()){
                        newNode.setDealerId(lastNode.getDealerId());
                        nodeInstanceDao.save(newNode);
                    }
                    //资料节点重新赋值
                    if(newNode.getType().equals(WorkflowNode.Type.input)){
                        for (WorkflowNodeAttribute attribute : lastNode.getAttributeList()) {
                            attribute.setId(null);
                            attribute.setNodeInstanceId(newNode.getId());
                            attribute.setNodeInstance(null);
                            attributeDao.save(attribute);
                        }
                    }
                    for (WorkflowNodeFile nodeFile : lastNode.getFileList()) {
                        nodeFile.setId(null);
                        nodeFile.setNodeInstance(newNode);
                        nodeFileDao.save(nodeFile);
                    }
                }
            }
        }
        //如果是逻辑节点
        else if (nextNode.getType().equals(WorkflowNode.Type.logic)) {
            runLogicNode(instance, newNode);
        }


        //查找旧节点的处理人, 发送消息
        switch (currentNode.getType()){
            case input:
            case check:
                List<Long> uids = attributeDao.getUidsByNodeInstnce(currentNode.getId());
                Map map = ImmutableMap.of(
                        "taskId",currentNode.getInstanceId(),
                        "taskName", instance.getTitle(),
                        "nodeId", currentNode.getId(),
                        "nodeName", currentNode.getNodeName()
                );
                noticeService.addNotice(SystemNotice.Type.WORKFLOW,uids ,"你已处理完毕任务${taskId}节点${nodeId}",map);

                break;

        }

        //查找当前节点的可处理人, 发送消息
        switch (newNode.getType()){
            case input:
            case check:
                List<Long> uids = globalPermissionDao.getUids(Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), newNode.getNodeModelId());
                noticeService.addNotice(SystemNotice.Type.WORKFLOW, uids, "你有新的任务${taskId}节点${nodeId}可以处理",ImmutableMap.of(
                        "taskId",newNode.getInstanceId(),
                        "taskName", instance.getTitle(),
                        "nodeId", newNode.getId(),
                        "nodeName", newNode.getNodeName()));
                break;
            case end:
                noticeService.addNotice(SystemNotice.Type.WORKFLOW,instance.getDealUserId(), "您的任务${taskId}已经结束",ImmutableMap.of(
                        "taskId", newNode.getInstanceId(),
                        "taskName", instance.getTitle()
                ));
                break;
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
                case Size:
                    Integer min = rule.getInteger("min");
                    if(null == min) min = 0;
                    Integer max = rule.getInteger("max");
                    if(null == max) max = 999;
                    if(notEmpty && value.length() >= min && value.length() <= max){
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
            //只需要校验必填属性
            if (v.getBoolean("required")) {
                Optional<WorkflowNodeAttribute> target = currentNode.getAttributeList().stream()
                        .filter(a -> a.getAttrKey().equals(v.getString("ename")))
                        .findAny();
                if (!target.isPresent()) {
                    throw new RestException("有必填字段没有填写");
                }
            }
        }
        //找不到下一个就结束了吧
        WorkflowNode nextNode = findNextNodeModel(instance.getWorkflowModel(), currentNode.getNodeModel()).orElse(null);
        if (null == nextNode) return saveWorkflowInstance(instance);
        goNextNode(instance, currentNode, nextNode);
        return saveWorkflowInstance(instance);
    }

    private WorkflowInstance fromCheckNodeToGo(long uid, WorkflowInstance instance, WorkflowNodeInstance currentNode, JSONObject nodeModel) throws RestException {

        //检查当前角色是否已经提交信息
        if (currentNode.getAttributeList().stream().filter(a -> a.getDealUser().getId().equals(uid)).count() == 0) {
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
            WorkflowInstance newInstance = (WorkflowInstance) startNewInstance(dealerId, applyTaskRequest).orElse(null);
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


    public boolean checkNodeAuth(WorkflowNodeInstance nodeInstance, User user) {
        return checkNodeAuth(nodeInstance,user.getId());
    }
    public boolean checkNodeAuth(WorkflowNodeInstance nodeInstance, long uid){
        boolean flag1 = (null == nodeInstance.getDealerId() && globalPermissionDao.hasPermission(uid, Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), nodeInstance.getNodeModelId()) > 0);
        //2. 处理人不确认的时候, 拥有该权限的人都可以处理
        boolean flag2 = (null != nodeInstance.getDealerId() && nodeInstance.getDealerId().equals(uid));
        return flag1 || flag2;
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

    /**
     * 查找一个任务的实例
     * @param uid 查找人ID
     * @param id 任务ID
     * @return
     */
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
            response.setAccept(canAccept(workflowInstance, uid));
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
        return modelDao.findById(id);
    }


    public Optional<WorkflowNode> findNode(long id) {
        return nodeDao.findById(id);
    }

    public Optional<WorkflowNodeInstance> findNodeInstance(long id) {
        return nodeInstanceDao.findById(id);
    }

    public Optional<WorkflowNodeFile> findNodeFile(long id) {
        return nodeFileDao.findById(id);
    }

    public Optional<GPSPosition> findNodeGPSPosition(long nodeFileId) {
        return findNodeFile(nodeFileId)
                .filter(nodeFile -> nodeFile.getType().equals(WorkflowNodeFile.Type.POSITION))
                .map(nodeFile -> gpsPositionDao.getOne(nodeFile.getFileId()));
    }

    /**
     * 查找一个任务
     * @param id 任务ID
     * @return 任务实例
     */
    public Optional<WorkflowInstance> findInstance(final long id) {
        return instanceDao.findById(id);
    }

    /**
     * 查找一个公共任务
     * @param id 任务ID
     * @return 任务实例
     */
    public Optional<WorkflowInstance> findCommonInstance(final long id){
        return instanceDao.findById(id).filter(instance -> instance.getState().equals(WorkflowInstance.State.COMMON));
    }






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
            String value = String.valueOf(data.get(attrKey));
            //验证属性格式
            //校验该字段的规则
            //只要填写了就校验
            if(!StringUtils.isEmpty(value)){
                if (v.containsKey("rules")) {
                    try {
                        String err = validField(v.getJSONArray("rules"), value);
                        if (!StringUtils.isEmpty(err)) {
                            throw new RestException(err);
                        }
                    } catch (Exception e) {
                        return v.getString("cname") + "格式校验错误";
                    }
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

    public Page getPubUsers(long modelId){
        List<Long> uids = getPubUids(modelId);
        PageRequest pageRequest = new PageRequest(0,200);
        List<User> users = userService.findUser(uids);
        return new PageImpl(users,pageRequest,users.size());
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


    /******** 内部类 *********/
    @Data
    public static class ModelFieldRequest{
        @NotEmpty
        String name;
        @NotNull
        WorkflowModelField.Type type;
        String hint;

        JSONObject ext = new JSONObject();
    }
}
