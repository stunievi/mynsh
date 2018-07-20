package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.util.SqlUtils;
import com.beeasy.hzback.modules.mobile.request.ApplyTaskRequest;
import com.beeasy.hzback.modules.mobile.request.SubmitDataRequest;
import com.beeasy.hzback.modules.mobile.request.WorkflowPositionAddRequest;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.node.*;
import com.beeasy.hzback.modules.system.response.FetchWorkflowInstanceResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.util.internal.StringUtil;
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
import javax.persistence.criteria.*;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    IInfoCollectLinkDao linkDao;
    @Autowired
    DataSearchService searchService;
    @Autowired
    IWorkflowNodeInstanceDealerDao nodeInstanceDealersDao;
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
     * 自动开启任务
     *
     * @param uid     任务开启人ID
     * @param request 任务请求明细
     * @return 标准REST返回
     */
    public Result autoStartTask(final long uid, final AutoStartTaskRequest request) {
        //查找符合条件的任务
        ApplyTaskRequest taskRequest = new ApplyTaskRequest();
        List<Long> mids = modelDao.findModelId(request.getModelName());
        if (mids.size() == 0) {
            return Result.error("任务执行人没有权限");
        }
        taskRequest.setModelId(mids.get(0));
        return Result.ok();
    }

    @Data
    public static class AutoStartTaskRequest {
        long dealerId;
        String title;
        String modelName;
        //固有数据源
        ApplyTaskRequest.DataSource dataSource;
        //固有数据源绑定值
        String dataId;
        Date planStartTime;

    }

    /**
     * 开始一个新的工作流任务
     *
     * @param uid 启动该任务的用户ID
     * @return
     */
    public Result startNewInstance(final long uid, ApplyTaskRequest request) {
        User pubUser = userService.findUser(uid).orElse(null);
        WorkflowModel workflowModel = null;
        WorkflowInstance parentInstance = null;
        if (null == pubUser) {
            return Result.error("任务发布人ID错误");
        }
        //如果使用了ID, 那么以ID为准
        if(null != request.getModelId()){
            workflowModel = findModel(request.getModelId()).orElse(null);
        }
        //如果使用了模型名, 那么自行查找一个合法的模型
        else if(!StringUtils.isEmpty(request.getModelName())){
            workflowModel = modelDao.findTopByModelNameAndOpenIsTrue(request.getModelName()).orElse(null);
        }
        if (null == workflowModel || !workflowModel.isOpen()) {
            return Result.error("工作流没有开启");
        }
        //fix
        updateWorkflowModelDeps(workflowModel.getId());


        //校验任务特殊权限
        //同一时间只能同时存在一个不良资产登记流程
        switch (workflowModel.getModelName()){
            case "不良资产登记":
            case "不良资产管理流程":
                if(StringUtils.isEmpty(request.getDataId())){
                    return Result.error("没有传台账编号");
                }
                //如果已经有一个相同的流程在运行
                if(instanceDao.countByModelNameAndBillNoAndStateNotIn(workflowModel.getModelName(), request.getDataId(), Arrays.asList(WorkflowInstance.State.CANCELED, WorkflowInstance.State.FINISHED)) > 0){
                    return Result.error("该台账已经有一个正在运行的任务");
                }
                break;

            case "诉讼":
            case "利息减免":
            case "抵债资产接收":
            case "资产处置":
            case "催收":
                if(StringUtils.isEmpty(request.getDataId())){
                    return Result.error("没有传台账编号");
                }
                //检查是否有相同的不良资产管理任务
                parentInstance = instanceDao.findTopByModelNameAndBillNoAndStateNotIn("不良资产管理流程", request.getDataId(), Arrays.asList(WorkflowInstance.State.CANCELED, WorkflowInstance.State.FINISHED)).orElse(null);
                if(null == parentInstance){
                    return Result.error("没有找到符合条件的不良资产管理流程");
                }
                break;
        }

        //计算任务归属部门
        //命中岗位权限的直属上级
        List<GlobalPermission> globalPermissions = globalPermissionDao.findAllByTypeInAndObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), request.getModelId());
        Department department = null;
        //公共任务的情况下, 直接确认自己所在的部门
        if(request.isCommon()){
            department = globalPermissions.stream()
                    .filter(item -> item.getUserType().equals(GlobalPermission.UserType.QUARTER))
                    .map(item -> userService.findQuarters(item.getLinkId()).orElse(null))
                    .filter(item -> null != item)
                    .map(item -> item.getDepartment())
                    .filter(dep -> {
                        return pubUser.getQuarters()
                                .stream().anyMatch(q -> q.getDepartmentId().equals(dep.getId()) && q.isManager());
                    })
                    .findFirst()
                    .orElse(null);
        }
        else{
            department = globalPermissions.stream()
                    .filter(item -> item.getUserType().equals(GlobalPermission.UserType.QUARTER))
                    .filter(item -> userService.hasQuarters(uid, item.getLinkId()))
                    .findFirst()
                    .flatMap(item -> userService.findQuarters(item.getLinkId()))
                    .map(item -> item.getDepartment())
                    .orElse(null);
        }
        if (null == department) {
            return Result.error("无法确定该任务所属的部门");
        }

//        //如果是手动任务, 但是
//        if (request.isManual() && !workflowModel.isManual()) {
//            return Result.error("该任务不能手动开启");
//        }

        User dealerUser = null;
        Map<String,String> innateMap = new HashMap<>();

        //任务主体
        WorkflowInstance workflowInstance = new WorkflowInstance();
        workflowInstance.setDepId(department.getId());
        workflowInstance.setDepName(department.getName());
        //绑定父任务的情况
        if(null != parentInstance){
            workflowInstance.setParentId(parentInstance.getId());
            workflowInstance.setParentInstance(parentInstance);
            workflowInstance.setParentTitle(parentInstance.getTitle());
            workflowInstance.setParentModelName(parentInstance.getModelName());
        }

        //公共任务
        if (request.isCommon()) {
            if (!canPoint(workflowModel.getId(), uid)) {
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
            if (dealerUser.getId().equals(uid)) {
                if (!canPub(workflowModel.getId(), uid)) {
                    return Result.error("你无权发布该任务");
                }
                workflowInstance.setDealUserId(dealerUser.getId());
            }
            //指派给别人执行
            else {
                if (!canPoint(workflowModel.getId(), uid)) {
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
        workflowInstance.setWorkflowModel(workflowModel);
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
        workflowInstance.setPubUserId(uid);
//        workflowInstance.setPubUser(pubUser);

//        //子任务开启
//        if(null != request.getParentId() && request.getParentId() > 0){
//            //废除数据源
//            request.setDataSource(null);
//            request.setDataId(null);
//            //废除固有字段数据和开始节点的数据
//
//            //子任务有明确的归属, 禁止设为公共任务
//        }

        workflowInstance = saveWorkflowInstance(workflowInstance);


        //固有字段检查
        List<WorkflowModelInnate> innates = workflowModel.getInnates();

        //插入固有字段
        //根据datasource和dataid整理固有字段
        innateMap.putAll(request.getData());
        innateMap.putAll(request.getStartNode());
        Result r = null;
        if(null != request.getDataSource()) {
            if(workflowModel.getModelName().indexOf("资料收集") > -1){
                if(request.getDataSource().equals(ApplyTaskRequest.DataSource.CLIENT)){
                    if(searchService.isCusCom(request.getDataId())){
                        r = searchService.searchInnateCusComData(uid,request.getDataId());
                    }
                    else if(searchService.isCusIndiv(request.getDataId())){
                        r = searchService.searchInnateCusIndivData(uid,request.getDataId());
                    }
                }
                else{
                    r = searchService.searchInnateAccloanData(uid, request.getDataId());
                }
            }
            else{
                r = searchService.searchInnateAccloanData(uid,request.getDataId());
            }
        }
        if(null != r && r.isSuccess()){
            innateMap.putAll((Map<? extends String, ? extends String>) r.getData());
        }
        //固有字段复写
        for (WorkflowModelInnate innate : innates) {
            if(innateMap.containsKey(innate.getContent().getString("ename"))){
                WorkflowInstanceAttribute attribute = addAttribute(
                        workflowInstance,
                        WorkflowInstanceAttribute.Type.INNATE,
                        innate.getFieldName(),
                        innateMap.getOrDefault(innate.getFieldName(), ""),
                        innate.getContent().getString("cname"));
                instanceAttributeDao.save(attribute);
            }
        }


        if (request.isCommon()) {
            //公共任务
            workflowInstance.setState(WorkflowInstance.State.COMMON);
        } else {
            if (dealerUser.getId().equals(uid)) {
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
        WorkflowNodeInstance nodeInstance = addNode(workflowInstance, firstNode, false);
        nodeInstance.setDealerId(workflowInstance.getDealUserId());

        //节点文件复写
        for (Long aLong : request.getFiles()) {
            WorkflowNodeFile nodeFile = nodeFileDao.findTopByIdAndNodeInstanceIsNull(aLong).orElse(null);
            if(null == nodeFile){
                continue;
            }
            //如果已经有归属
            nodeFile.setNodeInstance(nodeInstance);
            nodeFile = nodeFileDao.save(nodeFile);
            nodeInstance.getFileList().add(nodeFile);
        }

        //写入第一个节点的属性
        nodeInstance = nodeInstanceDao.save(nodeInstance);

        //写入处理人
        if (null != dealerUser) {
            WorkflowNodeInstanceDealer dealers = new WorkflowNodeInstanceDealer();
            dealers.setDepId(workflowInstance.getDepId());
            dealers.setDepName(workflowInstance.getDepName());
            dealers.setNodeInstanceId(nodeInstance.getId());
            dealers.setType(WorkflowNodeInstanceDealer.Type.DID_DEAL);
            dealers.setUserId(dealerUser.getId());
            dealers.setUserTrueName(dealerUser.getTrueName());
            nodeInstanceDealersDao.save(dealers);
        }


        //记录日志
        logService.addLog(SystemTextLog.Type.WORKFLOW, workflowInstance.getId(), uid, "发布了任务");
        if (null != dealerUser) {
            logService.addLog(SystemTextLog.Type.WORKFLOW, workflowInstance.getId(), dealerUser.getId(), "接受了任务");
        }


        //补充字段
        WorkflowInstanceAttribute attribute = instanceAttributeDao.findTopByInstanceIdAndAttrKey(workflowInstance.getId(), "BILL_NO").orElse(null);
        if (null != attribute) {
            workflowInstance.setBillNo(attribute.getAttrValue());
            workflowInstance = instanceDao.save(workflowInstance);
        }

        //第一个节点数据复写
        if(!request.isCommon()){

        WorkflowInstance finalWorkflowInstance = workflowInstance;
        WorkflowNodeInstance finalNodeInstance = nodeInstance;
        Result submitResult = submitData(uid, new SubmitDataRequest(){{
            setData((JSONObject)JSON.toJSON(innateMap));
            setInstanceId(finalWorkflowInstance.getId());
            setNodeId(finalNodeInstance.getId());
        }});
        if(!submitResult.isSuccess()){
            throw new RuntimeException(submitResult.getErrMessage());
        }
        if(request.isGoNext()){
            Result goNextResult = goNext(uid,workflowInstance.getId(),nodeInstance.getId());
            if(!goNextResult.isSuccess()){
                throw new RuntimeException(goNextResult.getErrMessage());
            }
        }
        }

        //善后工作
        switch (workflowModel.getModelName()){
            case "资料收集":
                if(!StringUtils.isEmpty(workflowInstance.getBillNo())){
                    InfoCollectLink infoCollectLink = new InfoCollectLink();
                    infoCollectLink.setBillNo(workflowInstance.getBillNo());
                    infoCollectLink.setInstanceId(workflowInstance.getId());
                    linkDao.save(infoCollectLink);
                }
                break;
        }
        return workflowInstance.getId() == null ? Result.error() : Result.ok(workflowInstance);
    }

    /**
     * 给实例增加节点
     *
     * @param instance 任务实例
     * @param node 节点模型
     * @param add 是否追加到队列
     */
    public WorkflowNodeInstance addNode(WorkflowInstance instance, WorkflowNode node, boolean add) {
        WorkflowNodeInstance workflowNodeInstance = new WorkflowNodeInstance();
        workflowNodeInstance.setNodeModel(node);
        workflowNodeInstance.setNodeModelId(node.getId());
        workflowNodeInstance.setNodeName(node.getName());
        workflowNodeInstance.setType(node.getType());
        workflowNodeInstance.setInstanceId(instance.getId());
        workflowNodeInstance.setInstance(instance);
        workflowNodeInstance.setFinished(false);
        if (add) {
            instance.getNodeList().add(workflowNodeInstance);
        }
        return workflowNodeInstance;
    }

    public WorkflowInstanceAttribute addAttribute(WorkflowInstance instance,WorkflowInstanceAttribute.Type type, String key, String value, String cname){
        WorkflowInstanceAttribute attribute = new WorkflowInstanceAttribute();
        attribute.setInstanceId(instance.getId());
        attribute.setType(type);
        attribute.setAttrKey(key);
        attribute.setAttrValue(value);
        attribute.setAttrCName(cname);
        return instanceAttributeDao.save(attribute);
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
                    switch (request.getDataSource()) {
                        case ACC_LOAN:
                            sql = "select BILL_NO,CONT_NO,CUS_ID,CUS_NAME from ACC_LOAN where BILL_NO=?";
                            break;
                    }
                    break;

                case "贷后跟踪-小微部公司类":
                    switch (request.getDataSource()) {
                        case ACC_LOAN:
                            sql = "select ACC_LOAN.BILL_NO,ACC_LOAN.CONT_NO,ACC_LOAN.CUS_ID,ACC_LOAN.CUS_NAME,ACC_LOAN.ASSURE_MEANS_MAIN,ACC_LOAN.LOAN_AMOUNT,ACC_LOAN.LOAN_BALANCE,CTR_LOAN_CONT.USE_DEC,CTR_LOAN_CONT.LOAN_TERM from ACC_LOAN left join CTR_LOAN_CONT on ACC_LOAN.CONT_NO=CTR_LOAN_CONT.CONT_NO where ACC_LOAN.BILL_NO=?";
                            break;
                    }
                    break;

                case "贷后跟踪-小微部个人类":
                    switch (request.getDataSource()) {
                        case ACC_LOAN:
                            sql = "select ACC_LOAN.BILL_NO,ACC_LOAN.CONT_NO,ACC_LOAN.CUS_ID,ACC_LOAN.CUS_NAME,ACC_LOAN.ASSURE_MEANS_MAIN,ACC_LOAN.LOAN_AMOUNT,ACC_LOAN.LOAN_BALANCE,CTR_LOAN_CONT.USE_DEC,CTR_LOAN_CONT.LOAN_TERM,GRT_GUAR_CONT.GUAR_NAME from ACC_LOAN left join CTR_LOAN_CONT on ACC_LOAN.CONT_NO=CTR_LOAN_CONT.CONT_NO left join GRT_LOANGUAR_INFO on CTR_LOAN_CONT.CONT_NO=GRT_LOANGUAR_INFO.CONT_NO left join GRT_GUAR_CONT on GRT_LOANGUAR_INFO.GUAR_CONT_NO=GRT_GUAR_CONT.GUAR_CONT_NO where ACC_LOAN.BILL_NO='20019740660711799'select ACC_LOAN.BILL_NO,ACC_LOAN.CONT_NO,ACC_LOAN.CUS_ID,ACC_LOAN.CUS_NAME,ACC_LOAN.ASSURE_MEANS_MAIN,ACC_LOAN.LOAN_AMOUNT,ACC_LOAN.LOAN_BALANCE,CTR_LOAN_CONT.USE_DEC,CTR_LOAN_CONT.LOAN_TERM,GRT_GUAR_CONT.GUAR_NAME from ACC_LOAN left join CTR_LOAN_CONT on ACC_LOAN.CONT_NO=CTR_LOAN_CONT.CONT_NO left join GRT_LOANGUAR_INFO on CTR_LOAN_CONT.CONT_NO=GRT_LOANGUAR_INFO.CONT_NO left join GRT_GUAR_CONT on GRT_LOANGUAR_INFO.GUAR_CONT_NO=GRT_GUAR_CONT.GUAR_CONT_NO where ACC_LOAN.BILL_NO=?";
                            break;
                    }
                    break;

                case "催收":
                    switch (request.getDataSource()) {
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
                    switch (request.getDataSource()) {
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
                        if (!workflowModelInnate.getContent().getString("type").equals("EXT_DATA")) {
                            continue;
                        }
                        //插入固有字段
                        JSONObject content = workflowModelInnate.getContent();
                        WorkflowInstanceAttribute attribute = addAttribute(
                                instance,
                                WorkflowInstanceAttribute.Type.INNATE,
                                content.getString("ename"),
                                rs.get(0).getOrDefault(content.getString("ename"),""),
                                content.getString("cname")
                                );
//                        WorkflowInstanceAttribute attribute = new WorkflowInstanceAttribute();
//                        attribute.setInstanceId(instance.getId());
//                        attribute.setType(WorkflowInstanceAttribute.Type.INNATE);
//                        attribute.setAttrCName(workflowModelInnate.getContent().getString("cname"));
//                        attribute.setAttrKey(workflowModelInnate.getContent().getString("ename"));
//                        attribute.setAttrValue(rs.get(0).getOrDefault(attribute.getAttrKey(), ""));
                        instanceAttributeDao.save(attribute);
                    }
                    //更新第一个节点里的对应字段
                    if (updateFirstNode) {
                        List<WorkflowNodeInstance> nodeInstances = nodeInstanceDao.findAllByInstanceIdAndNodeModel_StartIsTrue(instance.getId());
                        for (WorkflowNodeInstance nodeInstance : nodeInstances) {
                            WorkflowNode node = findNode(nodeInstance.getNodeModelId()).orElse(null);
                            if (null == node) {
                                continue;
                            }
                            JSONObject object = node.getNode();
                            if (object.containsKey("content")) {
                                for (Map.Entry<String, Object> entry : object.getJSONObject("content").entrySet()) {
                                    JSONObject field = (JSONObject) entry.getValue();
                                    String value = rs.get(0).getOrDefault(field.getString("ename"), "");
                                    if (!StringUtils.isEmpty(value)) {

                                        WorkflowNodeAttribute attribute = addAttribute(instance.getDealUserId(), nodeInstance, field.getString("ename"), value, field.getString("cname"));
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
    @Deprecated
    public Result startChildInstance(Long uid, StartChildInstanceRequest request) {
        User user = userService.findUser(uid).orElse(null);
        WorkflowNodeInstance nodeInstance = findNodeInstance(request.getNodeInstanceId()).orElse(null);
        if (null == user || null == nodeInstance || nodeInstance.isFinished()) {
            return Result.error("找不到合适的节点");
        }
        //允许开启的子流程是否存在
//        if (!nodeInstance.getNodeModel().getAllowChildTask().contains(request.getModelName())) {
//            return Result.error("禁止开启子流程");
//        }
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
     * @param ids 任务ID列表
     * @return 成功返回空字符串, 失败返回对应的失败信息
     */
    public Map<Object, Object> acceptInstance(long uid, Collection<Long> ids) {
        if (!userService.exists(uid)) {
            return new HashMap<>();
        }
        return ids.stream()
                .map(id -> {
                    WorkflowInstance instance = findInstance(id).orElse(null);
                    if (null == instance) {
                        return new Object[]{id, "该任务不符合接受条件"};
                    }
                    if (!canAccept(instance, uid)) {
                        return new Object[]{id, "用户没有权限接受任务"};
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
                    noticeService.addNotice(SystemNotice.Type.WORKFLOW, uid, "你已经接受任务${taskId}", ImmutableMap.of("taskId", instance.getId(), "taskName", instance.getTitle()));
                    //写log
                    logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "接受了任务");
                    return new Object[]{id, "接受成功"};
                })
                .collect(Collectors.toMap(item -> item[0], item -> item[1]));
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
     * @param uid        撤回人ID
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
    private boolean canDeal(final WorkflowInstance instance, final User user) {
        WorkflowNodeInstance nodeInstance = findCurrentNodeInstance(instance.getId()).orElse(null);
        return null != nodeInstance &&
                instance.getState().equals(WorkflowInstance.State.DEALING) &&
                !nodeInstance.isFinished() &&
                checkNodeAuth(nodeInstance, user);
    }
//    public boolean canDeal(final long instanceId, final long uid){
//        WorkflowNodeInstance nodeInstance = nodeInstanceDao.getCurrentNodeInstance(instanceId).orElse(null);
//        return null != nodeInstance && checkNodeAuth(nodeInstance, uid);
//    }


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
        return instance.getPubUserId().equals(user.getId())
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
     * @param uid      与用户ID
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
     * @param uid     指派的用户
     * @return
     */
    public boolean canPoint(final long modelId, final long uid) {
        WorkflowModel model = findModel(modelId).orElse(null);
        User user = userService.findUser(uid).orElse(null);
        return null != model && null != user && canPoint(model, user);
    }

    public boolean canPoint(final WorkflowModel model, final User user) {
        if(StringUtils.isEmpty(model.getDepIds())){
            return false;
        }
        try{
            List<Long> depIds = Utils.convertIdsToList(model.getDepIds());
            List<Quarters> qs = user.getQuarters();
            return qs.stream()
                    .anyMatch(item -> item.isManager() && depIds.contains(item.getDepartmentId()));
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * 是否可以指派这个实例
     *
     * @param instance
     * @param user
     * @return
     */
    public boolean canPoint(final WorkflowInstance instance, final User user) {
        return user.getQuarters()
                .stream()
                .anyMatch(item -> item.isManager() && null != instance.getDepId() && instance.getDepId().equals(item.getDepartmentId()));
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
                        canPoint(instance, user);
//                        globalPermissionDao.hasPermission(user.getId(), Collections.singleton(GlobalPermission.Type.WORKFLOW_POINTER), instance.getModelId()) > 0;
//                        && instance.getWorkflowModel().getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
//                && firstNode.getPersons().stream().anyMatch(p -> p.getType().equals(Type.MAIN_QUARTERS) && dealer.hasQuarters(p.getUid()))
    }


    /**
     * 得到某些用户未执行的任务
     *
     * @param uid
     * @param lessId
     * @param pageable
     * @return
     */
    public Page getUserUndealedWorks(final long uid, Long lessId, Pageable pageable) {
        if (lessId == null) {
            lessId = Long.MAX_VALUE;
        }
        return instanceDao.findNeedToDealWorks(uid,lessId,pageable);
        //得到所有我可以处理的模型
//        List<Long> oids = globalPermissionDao.getObjectIds(Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER),uids);
//        if(oids.size() == 0){
//            oids.add(-1L);
//        }
//        System.out.println(JSON.toJSONString(oids));
//        String hql = "select distinct i from " +
//                "WorkflowInstance i " +
//                "left join i.nodeList nl " +
//                "left join nl.dealers dls " +
////                "left join u.quarters uq " +
//                "where " +
//                "(" +
//                "   (dls.type = 'CAN_DEAL' and dls.userId in :uids) or " +
//                "   (dls.type = 'DID_DEAL' and dls.userId in :uids)" +
//                ") and " +
//                //节点处理人是我自己
////                "( (nl.dealerId is not null and nl.dealerId in :uids) or " +
////                为空的情况,寻找可以处理的人
////                "(nl.dealerId is null and nl.nodeModelId in :oids ) ) and " +
//                //该节点任务未完成
//                "nl.finished = false and " +
//                //任务进行中
//                "i.state = 'DEALING' and " +
//                //分页
//                "i.id <= :lessId";
//        return sqlUtils.hqlQuery(hql, ImmutableMap.of(
////                "oids", oids,
//                "lessId", lessId,
//                "uids", uids
//        ), "distinct i", pageable);
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
     * @param uid 主管用户ID
     * @param lessId app加载用
     * @param pageable 分页
     * @return
     */
    public Page<WorkflowInstance> getDepartmentUndealedWorks(long uid, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        User user = userService.findUser(uid).orElse(null);
        if(null == user){
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<Long> dids = getManagerDepIds(user);
        return instanceDao.findNeedToDealWorksFromDepartments(dids, WorkflowInstance.State.DEALING, lessId, pageable);
    }


    /**
     * 得到部门已执行完毕的任务
     *
     * @param uid  主管用户ID
     * @param lessId app加载用
     * @param pageable 分页
     * @return
     */
    public Page<WorkflowInstance> getDepartmentDealedWorks(long uid, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        User user = userService.findUser(uid).orElse(null);
        if(null == user){
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<Long> dids = getManagerDepIds(user);
        return instanceDao.findNeedToDealWorksFromDepartments(dids, WorkflowInstance.State.FINISHED, lessId, pageable);
    }


    /**
     * 得到用户可以接受的公共任务列表
     *
     * @param uid
     * @param lessId
     * @param pageable
     * @return
     */
    public Page<WorkflowInstance> getUserCanAcceptCommonWorks(final long uid, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        return instanceDao.findCommonWorks(uid, lessId, pageable);
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
            List<WorkflowInstance> instances = instance.getChildInstances();
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


    public List<Long> getManagerDepIds(final User user){
        List<Long> dids =  user.getQuarters()
                .stream()
                .filter(q -> q.isManager())
                .map(q -> q.getDepartmentId())
                .flatMap(did -> userService.getDidsFromDepartment(did).stream())
                .collect(Collectors.toList());
        if(dids.size() == 0){
            dids.add(-1L);
        }
        return dids;
    }


    /**
     * 主管得到管理的任务
     *
     * @param uid
     * @param request
     * @param lessId
     * @param pageable
     * @return
     */
    public Page getUnreceivedWorks(long uid, UnreceivedWorksSearchRequest request, Long lessId, Pageable pageable) {
        if (null == lessId) lessId = Long.MAX_VALUE;
        User user = userService.findUser(uid).orElse(null);
        if(null == user){
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        //得到主管岗位
        //得到下属所有岗位
        List<Long> dids = getManagerDepIds(user);

        //得到我监管的所有模型ID
//        List oids = globalPermissionDao.getManagerObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), uids);
        Long finalLessId = lessId;
        Specification query = new Specification<WorkflowInstance>() {

            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(root.get("state").in(WorkflowInstance.State.UNRECEIVED,
                        WorkflowInstance.State.DEALING,
                        WorkflowInstance.State.COMMON,
                        WorkflowInstance.State.PAUSE));
                predicates.add(cb.lessThan(root.get("id"), finalLessId));
//                Join join = root.join("workflowModel");
                //关联模型
                //select count(*) from Department d where d.id = instance.depId and d.code in :codes > 0
                //select count(*) from Department d where d.id = instance.depId and d.code in :codes and
//                if (oids.size() > 0) {
//                    predicates.add(join.get("id").in(oids));
//                }
                predicates.add(root.get("depId").in(dids));

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
                if (!StringUtils.isEmpty(request.getModelName())) {
                    String[] modelNames = request.getModelName().split(",");
                    if(modelNames.length > 0){
                        predicates.add(root.get("modelName").in(modelNames));
                    }
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
            if (null != instance.getDealUserId() && instance.getDealUserId().equals(toUid)) {
                errMessages.add("任务无法给相同的人");
                continue;
            }

            //任务状态是否合法
            if (!instance.getState().equals(WorkflowInstance.State.COMMON) && !instance.getState().equals(WorkflowInstance.State.UNRECEIVED) || !instance.getState().equals(WorkflowInstance.State.DEALING)) {
                errMessages.add("该任务状态无法移交");
                continue;
            }

            //子任务禁止移交
            if (instance.getParentId() != null) {
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
            if (instance.getState().equals(WorkflowInstance.State.DEALING)) {
                instance.setSecondState(WorkflowInstance.SecondState.TRANSFORM);
            } else {
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

        if (errMessages.size() > 0) {
            return Result.error(StringUtils.join(errMessages.toArray(), ","));
        } else {
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
        if (null == instance ||
                (!instance.getState().equals(WorkflowInstance.State.DEALING) && !instance.getState().equals(WorkflowInstance.State.COMMON))) {
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
        //当前处理人数
        int now = nodeInstanceDealersDao.countByTypeInAndNodeInstanceIdAndUserIdNot(Collections.singleton(WorkflowNodeInstanceDealer.Type.DID_DEAL), nodeInstance.getId(), uid);
        if(now + 1 > nodeModel.getMaxPerson()){
            return Result.error("该节点已经超过最大处理人数");
        }

        String errMessage = null;
        switch (nodeModel.getType()) {
            case input:
                errMessage = submitInputNode(uid, nodeInstance, request.getData());
                break;

            case check:
                errMessage = submitCheckNode(uid, nodeInstance, request.getData());
                break;
        }
        switch (nodeModel.getType()){
            case input:
            case check:
                if (!StringUtils.isEmpty(errMessage)) {
                    break;
                }
                //消息发送逻辑, 如果这个节点的仍然有可用的处理人, 那么不发送消息
                List<WorkflowNodeInstanceDealer> dls = nodeInstanceDealersDao.findAllByTypeAndNodeInstanceId(WorkflowNodeInstanceDealer.Type.CAN_DEAL, nodeInstance.getId());
                List<Long> notDealUids = dls
                        .stream()
                        .map(dealer -> {
                            long id = 0;
                            if(dealer.getUserId().equals(uid)){
                                dealer.setType(WorkflowNodeInstanceDealer.Type.DID_DEAL);
                            }
                            //如果正好符合条件, 那么将所有未执行的人标记
                            else if(now + 1 >= nodeModel.getMaxPerson()){
                                dealer.setType(WorkflowNodeInstanceDealer.Type.NOT_DEAL);
                                id = dealer.getUserId();
                            }
                            //如果位置还有剩余, 那么就只标记自己的
                            else{
                                //pass
                            }
                            nodeInstanceDealersDao.save(dealer);
                            return id;
                        })
                        .filter(item -> item > 0)
                        .distinct()
                        .collect(Collectors.toList());
                noticeService.addNotice(SystemNotice.Type.WORKFLOW, notDealUids, "你的任务${taskId}节点${nodeId}已被别人处理", ImmutableMap.of(
                        "taskId", instance.getId(),
                        "taskName", instance.getTitle(),
                        "nodeId", nodeInstance.getId(),
                        "nodeName", nodeInstance.getNodeName()
                ));
                break;

        }
        if (!StringUtils.isEmpty(errMessage)) {
            return Result.error(errMessage);
        }

        logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "向 " + nodeInstance.getNodeModel().getName() + " 节点提交了数据");
        return Result.ok();
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
        WorkflowNodeInstance nodeInstance = nodeInstanceDao.findFirstByIdAndFinishedIsFalse(nodeId).orElse(null);
        if (null == nodeInstance) {
            return Result.error("没有找到可以处理的任务, 该任务可能已经变动");
        }
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return Result.error("请求用户错误");
        }
        WorkflowInstance instance = nodeInstance.getInstance();
        //如果是第一个节点, 并且授权人已经确认, 那么只检查pub权限即可
//        if (null != nodeInstance.getDealerId() && nodeInstance.getDealerId().equals(uid)) {
//
//        }
        //验证权限
        if (!checkNodeAuth(nodeInstance, user)) {
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
        if(null != nodeFile.getNodeInstance()){
            if (!checkNodeAuth(nodeFile.getNodeInstance(), user)) {
                return false;
            }
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
    public Result uploadNodeFile(final long uid, final Long instanceId, final Long nodeId, WorkflowNodeFile.Type fileType, MultipartFile file, String content, String tag) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return Result.error();
        }
        WorkflowNodeInstance nodeInstance = null;
        if(null != nodeId){
            nodeInstance = nodeInstanceDao.getCurrentNodeInstance(instanceId).orElse(null);
            if (null == nodeInstance || !nodeInstance.getId().equals(nodeId)) {
                return Result.error("找不到对应的节点实例");
            }
            //是否该我处理
            if (!checkNodeAuth(nodeInstance, user)) {
                return Result.error("权限错误");
            }
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
        if (!StringUtils.isEmpty(tag)) {
            List<String> list = Arrays.stream(tag.split(" "))
                    .filter(item -> !StringUtils.isEmpty(item))
                    .distinct()
                    .collect(Collectors.toList());
            nodeFile.setTags(StringUtils.join(list.toArray(), " "));
        }
        nodeFile = nodeFileDao.save(nodeFile);
        nodeFile.setToken(applyDownload(uid,nodeFile.getId()));
        return Result.ok(nodeFile);
    }

    /**
     * 设置节点标签
     *
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
     *
     * @param uid
     * @param nodeId
     * @param name
     * @return
     */
    public boolean setNodeFileName(long uid, long nodeId, String name) {
        //处理名字
        WorkflowNodeFile nodeFile = findNodeFile(nodeId).orElse(null);
        if (null == nodeFile) {
            return false;
        }
        name = FilenameUtils.removeExtension(name);
        if (!StringUtils.isEmpty(nodeFile.getExt())) {
            name = name + "." + nodeFile.getExt();
        }
        return nodeFileDao.updateNodeFileName(uid, nodeId, name) > 0;
    }

    /**
     * 申请下载文件
     *
     * @param uid
     * @param id
     * @return
     */
    public String applyDownload(long uid, long id) {
        WorkflowNodeFile nodeFile = findNodeFile(id).orElse(null);
        if (null == nodeFile) {
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
     *
     * @param edit 模型编辑详情
     * @return
     */
    public Result editWorkflowModel(WorkflowModelEdit edit) {
        WorkflowModel model = findModel(edit.getId()).orElse(null);
        if (null == model) {
            return Result.error("找不到这个工作流");
        }
        if (edit.isOpen()) {
            //查找是否有同名已开启的流程
            if (modelDao.countByNameAndOpenIsTrue(model.getName()) > 0) {
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
        if (null != edit.getProcessCycle()) {
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
     * @param nodeId  节点ID
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
     * @param add       请求明细
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
            v = v;
            boolean start = (boolean) ((Map) v).getOrDefault("start", false);
            WorkflowNode.Type type = WorkflowNode.Type.valueOf(String.valueOf(((Map) v).get("type")).toLowerCase());

//            BaseNode baseNode = BaseNode.create(workflowModel,(Map) v);

            JSONObject baseNode = null;
            WorkflowNode node = new WorkflowNode();
            switch (type) {
                case input:
                    baseNode = buildInputNode(workflowModel, (Map) v);
                    break;
//                    baseNode = new JSONObject();

                case check:
                    baseNode = buildCheckNode((Map) v);
                    node.setMaxPerson(baseNode.getInteger("count"));
                    break;

                case logic:
                    baseNode = buildLogicNode((Map) v);
                    break;

                case end:
                    baseNode = buildNormalNode((Map) v);
                    break;
            }


            //nodelist
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
//            node.setAllowChildTask(allowChildTask);
            node.setNode(baseNode);
            nodeDao.save(node);

            workflowModel.getNodeModels().add(node);
        });

        //允许开放的子流程
        List<String> allowChildTask = (List<String>) ((Map) model).getOrDefault("allow_child_task", new ArrayList<>());
        workflowModel.setAllowChildTask(allowChildTask);
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
        start = true;
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
     *
     * @param id    模型ID
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
     * 更新工作流可能所属的部门
     *
     * @param modelId
     */
    public void updateWorkflowModelDeps(long modelId) {
        WorkflowModel model = findModel(modelId).orElse(null);
        if (null == model) {
            return;
        }
        List<GlobalPermission> globalPermissions = globalPermissionDao.findAllByTypeInAndObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), modelId);
        List<Long> dids = globalPermissions.stream()
                .filter(item -> item.getUserType().equals(GlobalPermission.UserType.QUARTER))
                .map(item -> userService.findQuarters(item.getLinkId()).orElse(null))
                .filter(item -> null != item)
                .map(item -> item.getDepartmentId())
                .collect(Collectors.toList());
        if (dids.size() > 0) {
            modelDao.updateWorkflowModelDeps(modelId, StringUtils.join(dids.toArray(), ","));
        }
    }

    /**
     * 设置模型关联字段
     *
     * @param modelId  模型ID
     * @param requests 请求明细
     * @return 设置结果
     */
    public Result setModelFields(long modelId, Collection<ModelFieldRequest> requests) {
        if (!modelDao.existsById(modelId)) {
            return Result.error("不存在该工作流模型");
        }
        List<String> names = new ArrayList<>();
        for (ModelFieldRequest request : requests) {
            WorkflowModelField field = fieldDao.findTopByModelIdAndName(modelId, request.getName()).orElse(new WorkflowModelField());
            field.setType(request.getType());
            field.setName(request.getName());
            field.setHint(request.getHint());
            field.setModelId(modelId);
            field.setExt(request.getExt());
            fieldDao.save(field);
            names.add(request.getName());
        }
        //清除无用的数据
        if (names.size() == 0) {
            fieldDao.deleteAllByModelId(modelId);
        } else {
            fieldDao.deleteAllByModelIdAndNameNotIn(modelId, names);
        }
        return Result.ok();
    }

    public List<WorkflowModelField> getModelFields(long modelId) {
        return fieldDao.findAllByModelId(modelId);
    }

    /**
     * 模型列表获取
     *
     * @param request
     * @param pageable
     * @return
     */
    public Page getModelList(ModelSearchRequest request, Pageable pageable) {
        Specification query = ((root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));
            if (!StringUtils.isEmpty(request.getModelName())) {
                predicates.add(cb.equal(root.get("modelName"), request.getModelName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        });
        return modelDao.findAll(query, pageable);
    }

    /**
     * 工作流实例列表查询
     *
     * @param uid
     * @param object
     * @param pageable
     * @return
     */
    public Page getInstanceList(long uid, Map<String, String> object, Pageable pageable) {
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            return new PageImpl(new ArrayList(), pageable, 0);
        }
        Specification query = ((root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            //限制我自己的任务
            predicates.add(cb.equal(root.get("dealUserId"), uid));
            for (Map.Entry<String, String> entry : object.entrySet()) {
                if (StringUtils.isEmpty(entry.getValue())) {
                    continue;
                }
                switch (entry.getKey()) {
                    case "modelName":
                        String[] modelNames = entry.getValue().split(",");
                        predicates.add(root.get("modelName").in(modelNames));
                        break;

                    case "parentId":
                        predicates.add(cb.equal(root.get("parentId"),object.get("parentId")));
                        break;

                    case "page":
                    case "size":
                    case "sort":
                    case "Authorization":
                    case "fields":
                        break;

                    //默认字段查询
                    default:
//                        Join nl = root.join("nodeList");
//                        Join attr = nl.join("attributeList");
//                        if (entry.getKey().startsWith("$")) {
//                            predicates.add(
//                                    cb.and(
//                                            cb.equal(attr.get("attrKey"), entry.getKey()),
//                                            cb.like(attr.get("attrValue"), "%" + entry.getValue().substring(1) + "%")
//                                    )
//                            );
//                        } else {
//                            predicates.add(
//                                    cb.and(
//                                            cb.equal(attr.get("attrKey"), entry.getKey()),
//                                            cb.equal(attr.get("attrValue"), entry.getValue())
//                                    )
//                            );
//                        }
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        });
        Page<WorkflowInstance> instances = instanceDao.findAll(query, pageable);
        //默认增加五个字段
        String fields = object.getOrDefault("fields","");
        if(!StringUtils.isEmpty(fields)){
            fields += ",";
        }
        fields += "BILL_NO,CONT_NO,CUS_ID,CUS_NAME,LOAN_ACCOUNT";
        object.put("fields",fields);
        return instances.map(o -> {
            WorkflowInstance instance = o;
            instance.setAttributes(null);
            instance.setNodeList(null);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(o);
            if(object.containsKey("fields")) {
                for (String field : Utils.splitByComma(object.get("fields"))) {
                    jsonObject.put(field,instance.getAttributeMap().getOrDefault(field,""));
                }
            }
            return jsonObject;
        });
    }

    public Page getRejectCollectList(final long uid, RejectCollectRequest request, Pageable pageable){
        Specification query = ((root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("state"), WorkflowInstance.State.FINISHED));
            predicates.add(cb.equal(root.get("modelName"),"资料收集"));

            Join join = root.join("attributes",JoinType.LEFT);
            predicates.add(cb.and(
                    cb.equal(join.get("attrCName"),"是否拒贷"),
                    cb.equal(join.get("attrValue"),"是")
            ));
            if(!StringUtils.isEmpty(request.getCUS_NAME())){
                Join j1 = root.join("attributes",JoinType.LEFT);
                predicates.add(cb.and(
                    cb.equal(j1.get("attrKey"),"CUS_NAME"),
                    cb.like(j1.get("attrValue"), "%" + request.getCUS_NAME() + "%")
                ));
            }
            if(!StringUtils.isEmpty(request.getPHONE())){
                Join j2 = root.join("attributes",JoinType.LEFT);
                predicates.add(cb.and(
                        cb.equal(j2.get("attrKey"),"PHONE"),
                        cb.like(j2.get("attrValue"), "%" + request.getPHONE() + "%")
                ));
            }
            if(!StringUtils.isEmpty(request.getCERT_CODE())){
                Join j3 = root.join("attributes",JoinType.LEFT);
                predicates.add(cb.and(
                        cb.equal(j3.get("attrKey"),"CERT_CODE"),
                        cb.like(j3.get("attrValue"), "%" + request.getCERT_CODE() + "%")
                ));
            }
            if(null != request.getStartDate()){
                predicates.add(
                        cb.greaterThan(root.get("finishedDate"), new Date(request.getStartDate()))
                );
            }
            if(null != request.getEndDate()){
                predicates.add(
                        cb.lessThan(root.get("finishedDate"), new Date(request.getEndDate()))
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        });
        Page page = instanceDao.findAll(query,pageable);
        page = page.map(obj -> {
            WorkflowInstance ins = (WorkflowInstance) obj;
            JSONObject object = new JSONObject();
            object.put("id",ins.getId());
            object.put("CUS_NAME", ins.getAttributeMap().getOrDefault("CUS_NAME",""));
            object.put("PHONE", ins.getAttributeMap().getOrDefault("PHONE",""));
            object.put("CERT_CODE", ins.getAttributeMap().getOrDefault("CERT_CODE",""));
            object.put("rejectDate", ins.getFinishedDate());
            object.put("dealerId", ins.getDealUserId());
            object.put("depId", ins.getDepId());
            object.put("ps", ins.getAttributeMap().getOrDefault("ps",""));
            return object;
        });
        return page;
    }

    public Optional<WorkflowInstanceAttribute> getAttributeByCname(final long instanceId, final String cname){
        return instanceAttributeDao.findTopByInstanceIdAndAttrCName(instanceId, cname);
    }
    public Optional<WorkflowInstanceAttribute> getAttributeByKey(final long instanceId, final String key){
        return instanceAttributeDao.findTopByInstanceIdAndAttrKey(instanceId, key);
    }
    public String getAttributeValueByKey(final long instanceId, final String key){
        return instanceAttributeDao.findTopByInstanceIdAndAttrKey(instanceId, key)
                .map(WorkflowInstanceAttribute::getAttrValue)
                .orElse("");
    }

    @Data
    public static class RejectCollectRequest{
        String CUS_NAME;
        String PHONE;
        String CERT_CODE;

        Long startDate;
        Long endDate;
    }

    /**
     * 得到我可以发布的模型列表
     *
     * @param uid
     * @return
     */
    public List<WorkflowModel> getUserModelList(long uid) {
        //可以发布的模型ID
        List<Long> pubIds = globalPermissionDao.getObjectIds(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), Collections.singleton(uid));
        //可以指派的模型ID
        List<Long> pointIds = globalPermissionDao.getManagerObjectId(Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB), Collections.singleton(uid));
        List<WorkflowModel> models = modelDao.getAllWorkflows()
                .stream()
                .filter(model -> pubIds.contains(model.getId()) || pointIds.contains(model.getId()))
                .peek(model -> {
                    model.setPub(pubIds.contains(model.getId()));
                    model.setPoint(pointIds.contains(model.getId()));
                    model.setNodeModels(ImmutableList.of(nodeDao.findFirstByModel_IdAndStartIsTrue(model.getId()).orElse(null)));
                }).collect(Collectors.toList());
        return models;
    }

    @Data
    public static class ModelSearchRequest {
        String modelName;
    }


    /**
     * 创建审核节点
     *
     * @param nodeModel
     * @return
     */
    public Optional<WorkflowNode> createCheckNode(CheckNodeModel nodeModel) {
        return findModel(nodeModel.getModelId()).map(model -> {
            //无法自定义的流程不允许自定义
            if (!model.isCustom()) {
                return null;
            }
            if (model.isFirstOpen()) {
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
            node.setMaxPerson(checkNode.getInteger("count"));
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

        WorkflowNodeInstance newNode = addNode(instance, nextNode, false);
        newNode.setFinished(nextNode.isEnd());
        newNode = nodeInstanceDao.save(newNode);
        instance.getNodeList().add(newNode);

        //更新本节点所有值到主表属性里
        for (WorkflowNodeAttribute attr : currentNode.getAttributeList()) {
            instanceAttributeDao.deleteByInstanceIdAndAttrKey(instance.getId(), attr.getAttrKey());
            addAttribute(instance, WorkflowInstanceAttribute.Type.NODE, attr.getAttrKey(),attr.getAttrValue(), attr.getAttrCname());
        }

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

            //关闭所有子任务
            currentNode.getInstance().getChildInstances()
                    .stream()
                    .filter(ins -> !ins.getState().equals(WorkflowInstance.State.FINISHED))
                    .forEach(ins -> {
                        ins.setState(WorkflowInstance.State.CANCELED);
                        instanceDao.save(ins);
                    });
        }
        //资料节点的回卷, 如果曾经存在提交, 那么重新附上所有值以及文件
        else if (newNode.getType().equals(WorkflowNode.Type.input)) {
            if (nodeInstanceDao.countByInstanceIdAndNodeModelId(newNode.getInstanceId(), newNode.getNodeModelId()) >= 2) {
                WorkflowNodeInstance lastNode = nodeInstanceDao.findTopByInstanceIdAndNodeModelIdAndIdLessThanOrderByIdDesc(newNode.getInstanceId(), newNode.getNodeModelId(), newNode.getId()).orElse(null);
                if (null == lastNode) {

                } else {
                    if (null != lastNode.getDealerId()) {
                        newNode.setDealerId(lastNode.getDealerId());
                        nodeInstanceDao.save(newNode);
                    }
                    //资料节点重新赋值
                    if (newNode.getType().equals(WorkflowNode.Type.input)) {
                        for (WorkflowNodeAttribute attribute : lastNode.getAttributeList()) {
                            WorkflowNodeAttribute newAttr = JSON.toJavaObject((JSON) JSON.toJSON(attribute), WorkflowNodeAttribute.class);
                            newAttr.setId(null);
                            newAttr.setNodeInstanceId(newNode.getId());
                            newAttr.setNodeInstance(newNode);
                            attributeDao.save(newAttr);
                        }
                    }
//                    for (WorkflowNodeFile nodeFile : lastNode.getFileList()) {
//                        nodeFile.setId(null);
//                        nodeFile.setNodeInstance(newNode);
//                        nodeFileDao.save(nodeFile);
//                    }
                    //取该节点曾经的执行人重新写入
//                    WorkflowNodeInstance finalNewNode1 = newNode;
//                    lastNode.getDealers().stream()
//                            .filter(dealer -> dealer.getType().equals(WorkflowNodeInstanceDealer.Type.DID_DEAL))
//                            .forEach(dealer -> {
//                                WorkflowNodeInstanceDealer newDealer = JSON.toJavaObject((JSON) JSON.toJSON(dealer), WorkflowNodeInstanceDealer.class);
//                                newDealer.setUserId(dealer.getUserId());
//                                newDealer.setNodeInstanceId(finalNewNode1.getId());
//                                nodeInstanceDao.save(finalNewNode1);
//                            });
                }
            }
        }
        //如果是逻辑节点
        else if (nextNode.getType().equals(WorkflowNode.Type.logic)) {
            runLogicNode(instance, newNode);
        }

        //查找旧节点的处理人, 发送消息
        switch (currentNode.getType()) {
            case input:
            case check:
                List<Long> uids = attributeDao.getUidsByNodeInstnce(currentNode.getId());
                Map map = ImmutableMap.of(
                        "taskId", currentNode.getInstanceId(),
                        "taskName", instance.getTitle(),
                        "nodeId", currentNode.getId(),
                        "nodeName", currentNode.getNodeName()
                );
                noticeService.addNotice(SystemNotice.Type.WORKFLOW, uids, "你已处理完毕任务${taskId}节点${nodeId}", map);

                break;

        }

        //查找当前节点的可处理人, 发送消息
        switch (newNode.getType()) {
            case input:
            case check:
                List<Object> objects = getCanDealUids(newNode.getId());
                if(newNode.getNodeModel().isStart()){
                    objects.add(new Object[]{
                            null,
                            GlobalPermission.UserType.QUARTER,
                            newNode.getInstance().getDealUserId(),
                            newNode.getInstance().getDealUserName(),
                            newNode.getInstance().getDepId(),
                            newNode.getInstance().getDepName()
                    });
                }
                WorkflowNodeInstance finalNewNode = newNode;
                //强制施加规则, 如果该节点只允许一个人处理, 且任务执行人拥有这个节点的权限, 那么直接确立为该用户执行
                //如果存在多人执行的情况, 那么仍然确认该用户, 且保存多余的可执行人
                if(newNode.getNodeModel().getMaxPerson() == 1){
                    List<Object> newObjects = objects
                            .stream()
                            .filter(item -> ((Long)((Object[])item)[2]).equals(finalNewNode.getInstance().getDealUserId()) )
                            .collect(Collectors.toList());
                    if(newObjects.size() > 0){
                        objects = newObjects;
                    }
                }
                List<Long> uids = objects.stream()
                        .map(item -> {
                            Object[] objs = (Object[]) item;
                            Long uid = (Long) objs[2];
                            String trueName = (String) objs[3];
                            GlobalPermission.UserType userType = (GlobalPermission.UserType) objs[1];
                            WorkflowNodeInstanceDealer dealers = new WorkflowNodeInstanceDealer();
                            //已经确定执行的情况
                            if(null != finalNewNode.getInstance().getDealUserId() && finalNewNode.getInstance().getDealUserId().equals(uid)){
                                dealers.setType(WorkflowNodeInstanceDealer.Type.DID_DEAL);
                            }
                            else{
                                dealers.setType(WorkflowNodeInstanceDealer.Type.CAN_DEAL);
                            }
                            dealers.setNodeInstanceId(finalNewNode.getId());
                            dealers.setUserId(uid);
                            dealers.setUserType(userType);
                            dealers.setUserTrueName(trueName);
                            nodeInstanceDealersDao.save(dealers);
                            switch (userType) {
                                case QUARTER:
                                    dealers.setDepId((Long) objs[4]);
                                    dealers.setDepName((String) objs[5]);
                                    break;
                                case USER:
                                    break;
                                case ROLE:
                                    break;
                            }
                            return uid;
                        })
                        .distinct()
                        .collect(Collectors.toList());
//                List<Long> uids = globalPermissionDao.getUids(Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), newNode.getNodeModelId());
                noticeService.addNotice(SystemNotice.Type.WORKFLOW, uids, "你有新的任务${taskId}节点${nodeId}可以处理", ImmutableMap.of(
                        "taskId", newNode.getInstanceId(),
                        "taskName", instance.getTitle(),
                        "nodeId", newNode.getId(),
                        "nodeName", newNode.getNodeName()));
                break;
            case logic:
                break;
            case end:
                noticeService.addNotice(SystemNotice.Type.WORKFLOW, instance.getDealUserId(), "你的任务${taskId}已经结束", ImmutableMap.of(
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


    private String validField(JSONArray rules, String value) {
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
                    if (null == min) min = 0;
                    Integer max = rule.getInteger("max");
                    if (null == max) max = 999;
                    if (notEmpty && value.length() >= min && value.length() <= max) {
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
        }).filter(item -> !org.apache.commons.lang.StringUtils.isEmpty(item)).collect(Collectors.toList());
        return org.apache.commons.lang.StringUtils.join(errorMessages.toArray(), "\n");
    }

    private WorkflowInstance fromInputNodeToGo(WorkflowInstance instance, WorkflowNodeInstance currentNode, JSONObject nodeModel) throws RestException {
        //检查所有字段, 如果有必填字段没有填写, 那么抛出异常
        for (Map.Entry<String, Object> entry : nodeModel.getJSONObject("content").entrySet()) {
            String k = entry.getKey();
            JSONObject v = (JSONObject) entry.getValue();
            //只需要校验必填属性
            if (v.getBoolean("required")) {
                Optional<WorkflowNodeAttribute> target = attributeDao.findFirstByNodeInstanceIdAndAttrKey(currentNode.getId(), v.getString("ename"))
                        .filter(a -> !StringUtils.isEmpty(a.getAttrValue()));
//                Optional<WorkflowNodeAttribute> target = currentNode.getAttributeList().stream()
//                        .filter(a -> a.getAttrKey().equals(v.getString("ename")))
//                        .findAny();
                if (!target.isPresent()) {
                    throw new RestException(String.format("%s字段没有填写", v.getString("cname")));
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
            applyTaskRequest.setDataId(instance.getBillNo());
            applyTaskRequest.setDataSource(ApplyTaskRequest.DataSource.ACC_LOAN);
            applyTaskRequest.setGoNext(false);
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


    /**
     * 检查用户是否可以处理这个节点
     *
     * @param nodeInstance
     * @param user
     * @return
     */
    public boolean checkNodeAuth(final WorkflowNodeInstance nodeInstance, final User user) {
        return checkNodeAuth(nodeInstance.getId(), user.getId());
    }

    public boolean checkNodeAuth(final long nodeInstanceId, final long uid) {
        return nodeInstanceDealersDao.countByTypeInAndNodeInstanceIdAndUserId(Arrays.asList(WorkflowNodeInstanceDealer.Type.CAN_DEAL, WorkflowNodeInstanceDealer.Type.DID_DEAL), nodeInstanceId, uid) > 0;
    }

    private Map<String, String> convertInnates(List<WorkflowInstanceAttribute> attributes) {
        return attributes.stream().filter(item -> item.getType().equals(WorkflowInstanceAttribute.Type.INNATE))
                .collect(Collectors.toMap(item -> item.getAttrKey(), item -> item.getAttrValue()));
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

    /**
     * 查找一个任务的实例
     *
     * @param uid 查找人ID
     * @param id  任务ID
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
            response.setTransformUsers(getTransformUids(workflowInstance.getId()));

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

    public int countByModelNameAndBillNo(String modelName, String BILL_NO){
        return instanceDao.countByModelNameAndBillNo(modelName,BILL_NO);
    }
    public int countByModelNameAndBillNoAndStateNotIn(String modelName, String BILL_NO, Collection<WorkflowInstance.State> states){
        return instanceDao.countByModelNameAndBillNoAndStateNotIn(modelName, BILL_NO, states);
    }

    /**
     * 查找一个任务
     *
     * @param id 任务ID
     * @return 任务实例
     */
    public Optional<WorkflowInstance> findInstance(final long id) {
        return instanceDao.findById(id);
    }

    /**
     * 查找一个公共任务
     *
     * @param id 任务ID
     * @return 任务实例
     */
    public Optional<WorkflowInstance> findCommonInstance(final long id) {
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


    public String submitInputNode(long uid, WorkflowNodeInstance nodeInstance, Map<String, Object> data) {
        //当前处理的
//        WorkflowNodeInstance nodeInstance = nodeInstanceDao.findFirstByIdAndFinishedIsFalse(nodeInstanceId).orElse(null);
//        if(null == nodeInstance){
//            return;
//        }
        JSONObject node = getCurrentNodeModelNode(nodeInstance.getId()).orElse(null);
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
            if (!StringUtils.isEmpty(value)) {
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
            WorkflowNodeAttribute attribute = addAttribute(uid, nodeInstance, attrKey, String.valueOf(data.get(attrKey)), v.getString("cname"));
//            attribute.setAttrKey(attrKey);
//            attribute.setAttrValue(String.valueOf(data.get(attrKey)));
//            attribute.setAttrCname(v.getString("cname"));
//            attribute.setDealUserId(uid);
//            attribute.setNodeInstanceId(nodeInstanceId);
            attributeDao.save(attribute);
            nodeInstance.getAttributeList().add(attribute);
        }
        nodeInstanceDao.save(nodeInstance);
        return null;
    }

    public String submitCheckNode(long uid, WorkflowNodeInstance nodeInstance, Map data) {
        JSONObject node = getCurrentNodeModelNode(nodeInstance.getId()).orElse(null);
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
        WorkflowNodeAttribute attribute = addAttribute(uid, nodeInstance, node.getString("key"), item, node.getString("question"));
        attributeDao.save(attribute);

        //如果填写了审核说明
        //valueof会格式化null
        if (ps.equals("null")) {
            ps = "";
        }
        attribute = addAttribute(uid, nodeInstance, node.getString("ps"), ps, "审核说明");
        attributeDao.save(attribute);

        return "";
    }

//    public WorkflowNodeAttribute addAttribute(Long uid, long nodeInstanceId, String key, String value, String cname) {
//        WorkflowNodeAttribute attribute = attributeDao.findFirstByNodeInstanceIdAndAttrKey(nodeInstanceId, key).orElse(new WorkflowNodeAttribute());
//        attribute.setDealUserId(uid);
//        attribute.setAttrKey(key);
//        attribute.setAttrValue(value == null ? "" : value);
//        attribute.setNodeInstanceId(nodeInstanceId);
////        attribute.setNodeInstance();
//        attribute.setAttrCname(cname);
//        return attribute;
//    }
    public WorkflowNodeAttribute addAttribute(long uid, WorkflowNodeInstance nodeInstance, String key, String value, String cname){
        WorkflowNodeAttribute attribute = attributeDao.findFirstByNodeInstanceIdAndAttrKey(nodeInstance.getId(), key).orElse(new WorkflowNodeAttribute());
        attribute.setDealUserId(uid);
        attribute.setAttrKey(key);
        attribute.setAttrValue(value == null ? "" : value);
        attribute.setNodeInstance(nodeInstance);
        attribute.setNodeInstanceId(nodeInstance.getId());
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

    /**
     * 得到可以指派移交的用户ID
     *
     * @param instanceId
     * @return
     */
    public List<Long> getTransformUids(long instanceId) {
        String sql = "select distinct user.id from GlobalPermission gp, User user, WorkflowInstance ins " +
                "left join user.quarters uq " +
                "where ins.id = :instanceId and gp.objectId = ins.modelId and gp.type = 'WORKFLOW_PUB' and " +
                "(" +
                "(gp.userType = 'QUARTER' and uq.id = gp.linkId and " +
                "(" +
                "(select count(d) from Department d where uq.department.code like concat(d.code,'%') and d.id = ins.depId) > 0 or " +
                "(uq.departmentId = ins.depId) or " +
                "(select count(d) from Department d where d.code like concat(uq.department.code,'%') and d.id = ins.depId) > 0" +
                ") " +
                "))";
        return sqlUtils.hqlQuery(sql, ImmutableMap.of(

                "instanceId", instanceId
        )).stream()
                .map(item -> (Long) item)
                .collect(Collectors.toList());
    }


    public List<Object> getCanDealUids(final long nodeInstanceId) {
        String sql =
                "select gp.type, gp.userType, user.id, user.trueName, uq.departmentId, uq.department.name, uq.id, uq.name, " +
                        "   gp.linkId, ni.instance.depId, uq.department.code, ni.nodeModelId " +
                        "from GlobalPermission gp, User user " +
                        "left join user.quarters uq " +
                        ", WorkflowNodeInstance ni " +
                        "where ni.id = :instanceId and gp.objectId = ni.nodeModelId and " +
                        "   gp.type = 'WORKFLOW_MAIN_QUARTER' " +
                        "group by user,gp,ni,uq having " +
                        "   (" +
                        "       (gp.userType = 'QUARTER' and uq.id = gp.linkId and ( " +
                                //如果只配置了一个部门, 那么无视这个权限
                        "       ( select count(distinct qq.departmentId) from GlobalPermission gpgp, Quarters qq where gpgp.userType = 'QUARTER' and gpgp.objectId = ni.nodeModelId and gpgp.linkId = qq.id) = 1 or " +
                        "       (" +
                        "           (select count(d) from Department d where uq.department.code like concat(d.code,'%') and d.id = ni.instance.depId) > 0 or" +
//                        "           (uq.departmentId = ni.instance.depId) or " +
                        "           (select count(d) from Department d where d.code like concat(uq.department.code,'%') and d.id = ni.instance.depId) > 0" +
                        "       )) ) or " +
                        "(gp.userType = 'USER' and user.id = gp.linkId) " +
                        "" +
                        "   )";
        return sqlUtils.hqlQuery(sql, ImmutableMap.of(
                "instanceId", nodeInstanceId
        ));
    }

    public List<Object> getNodeDealUids(final long instanceId, final String nodeName){
        WorkflowInstance instance = findInstance(instanceId).orElse(null);
        if(null == instance){
            return new ArrayList<>();
        }
        WorkflowNode node = nodeDao.findTopByModelIdAndName(instance.getModelId(), nodeName).orElse(null);
        if(null == node){
            return new ArrayList<>();
        }
        String sql =
                "select gp.type, gp.userType, user.id, user.trueName, uq.departmentId, uq.department.name, uq.id, uq.name, " +
                        "   gp.linkId, ins.depId, uq.department.code, node.id as nodeModelId " +
                        "from GlobalPermission gp, User user " +
                        "left join user.quarters uq " +
                        ", WorkflowInstance ins, " +
                        "  WorkflowNode node " +
                        "where ins.id = :instanceId and gp.objectId = node.id and " +
                        "   gp.type = 'WORKFLOW_MAIN_QUARTER' and node.name = :nodeName " +
                        "group by user,gp,ins,node,uq having " +
                        "   (" +
                        "       (gp.userType = 'QUARTER' and uq.id = gp.linkId and ( " +
                        //如果只配置了一个部门, 那么无视这个权限
                        "       ( select count(distinct qq.departmentId) from GlobalPermission gpgp, Quarters qq where gpgp.userType = 'QUARTER' and gpgp.objectId = node.id and gpgp.linkId = qq.id) = 1 or " +
                        "       (" +
                        "           (select count(d) from Department d where uq.department.code like concat(d.code,'%') and d.id = ins.depId) > 0 or" +
//                        "           (uq.departmentId = ni.instance.depId) or " +
                        "           (select count(d) from Department d where d.code like concat(uq.department.code,'%') and d.id = ins.depId) > 0" +
                        "       )) ) or " +
                        "(gp.userType = 'USER' and user.id = gp.linkId) " +
                        "" +
                        "   )";
        List<Object> objects = sqlUtils.hqlQuery(sql, Utils.newMap(
                "instanceId", instanceId,
                "nodeName", nodeName
        ));
        //强制施加规则, 如果该节点只允许一个人处理, 且任务执行人拥有这个节点的权限, 那么直接确立为该用户执行
        //如果存在多人执行的情况, 那么仍然确认该用户, 且保存多余的可执行人
        if(node.getMaxPerson() == 1){
            List<Object> newObjects = objects
                    .stream()
                    .filter(item -> ((Long)((Object[])item)[2]).equals(instance.getDealUserId()) )
                    .collect(Collectors.toList());
            if(newObjects.size() > 0){
                objects = newObjects;
            }
        }
        return objects;
    }

    public Page getPubUsers(long modelId) {
        //TODO: BUG
        List<Long> uids = getTransformUids(modelId);
        PageRequest pageRequest = new PageRequest(0, 200);
        List<User> users = userService.findUser(uids);
        return new PageImpl(users, pageRequest, users.size());
    }


    /******** 内部类 *********/
    @Data
    public static class ModelFieldRequest {
        @NotEmpty
        String name;
        @NotNull
        WorkflowModelField.Type type;
        String hint;

        JSONObject ext = new JSONObject();
    }
}
