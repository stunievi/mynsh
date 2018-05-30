package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

//import com.beeasy.hzback.core.helper.Rest;

@Slf4j
@Service
@Transactional
public class WorkflowService implements IWorkflowService {

    @Value("${workflow.timeStep}")
    private int timeStep = 5;


    @Autowired
    EntityManager entityManager;
    @Autowired
    IGPSPositionDao gpsPositionDao;
    @Autowired
    IWorkflowNodeFileDao nodeFileDao;
    @Autowired
    ISystemFileDao systemFileDao;
    @Autowired
    IWorkflowExtPermissionDao extPermissionDao;
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
    IWorkflowModelPersonsDao personsDao;
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
    @Autowired
    IInspectTaskDao inspectTaskDao;

    @Autowired
    ScriptEngine scriptEngine;
    @Autowired
    ISystemTextLogDao systemTextLogDao;
    @Autowired
    SystemTextLogService logService;


    /**
     * 开始一个新的工作流任务
     *
     * @param uid
     * @return
     */
    @Override
    public Result<WorkflowInstance> startNewInstance(long uid, ApplyTaskRequest request) {
        AtomicReference<User> userAtomicReference = new AtomicReference<>();
        return userService.findUser(uid)
                .flatMap(user -> {
                    userAtomicReference.set(user);
                    return findModel(request.getModelId());
                })
                .map(workflowModel -> {
                    if (!workflowModel.isOpen()) {
                        return Result.error("工作流没有开启");
                    }

                    //如果任务执行人为空,那么默认为自己
                    User dealerUser = null;
                    if(request.getDealerId() == -1 || request.getDealerId().equals(uid)){
                        dealerUser = userAtomicReference.get();
                    }
                    //发布为公共任务
                    else if(request.getDealerId() == 0){
                        //默认为null
                    }
                    else{
                        dealerUser = userService.findUser(request.getDealerId()).orElse(null);
                        //如果仍然为空,那么报错
                        if(null == dealerUser){
                            return Result.error("找不到任务执行人");
                        }
                    }

                    //如果发布人和执行人不一样, 检查是否拥有发布该任务的权利
                    if(null == dealerUser || (null != dealerUser && dealerUser.getId() != uid)){
                        if(!canPubOrPoint(workflowModel,userAtomicReference.get())){
                            return Result.error("你无权发布该任务");
                        }
                    }
                    //如果发布了公共任务
                    if(null == dealerUser && !canPoint(workflowModel,userAtomicReference.get())){
                        return Result.error("你无权发布公共任务");
                    }

                    //检查执行人是否拥有第一个节点的处理权限
                    WorkflowNode firstNode = nodeDao.findAllByModelAndStartIsTrue(workflowModel).get(0);
                    if (!checkAuth(workflowModel, firstNode, dealerUser)) {
                        return Result.error("选择的执行人没有权限处理该任务");
                    }

                    //任务主体
                    WorkflowInstance workflowInstance = new WorkflowInstance();
                    workflowInstance.setWorkflowModel(workflowModel);
                    workflowInstance.setTitle(request.getTitle());
                    workflowInstance.setInfo(request.getInfo());
                    workflowInstance.setDealUser(dealerUser);
                    workflowInstance.setPubUser(userAtomicReference.get());

                    if(null == dealerUser){
                       //公共任务
                        workflowInstance.setState(WorkflowInstance.State.UNRECEIVED);
                    }
                    else{
                        //任务默认处于执行中的状态
                        workflowInstance.setState(WorkflowInstance.State.DEALING);
                    }

                    //插入第一个节点
                    workflowInstance.addNode(firstNode);
                    //第一个执行人已经确定
                    workflowInstance.getNodeList().get(0).setDealer(dealerUser);

                    workflowInstance = saveWorkflowInstance(workflowInstance);

                    //记录日志
                    logService.addLog(SystemTextLog.Type.WORKFLOW,workflowInstance.getId(),uid,"发布了任务");
                    if(null != dealerUser){
                        logService.addLog(SystemTextLog.Type.WORKFLOW,workflowInstance.getId(),dealerUser.getId(),"接受了任务");
                    }

                    return workflowInstance.getId() == null ? Result.error() : Result.ok(workflowInstance);
                }).orElse(Result.error());
    }

    /**
     * 接受公共任务
     * @param uid
     * @param instanceId
     * @return
     */
    public boolean acceptInstance(long uid, long instanceId){
        User user = userService.findUser(uid).orElse(null);
        if(null == user) return false;
        WorkflowInstance instance = findInstance(instanceId).orElse(null);
        if(null == instance) return false;
        if(!instance.isCommon()){
            return false;
        }
        //没有权限接受
        if(!canPub(instance.getWorkflowModel(),user)){
            return false;
        }
        //锁定该任务
        entityManager.refresh(instance,PESSIMISTIC_WRITE);
        instance.setDealUser(user);
        instance.setState(WorkflowInstance.State.DEALING);
        entityManager.merge(instance);
        //写log
        logService.addLog(SystemTextLog.Type.WORKFLOW,instance.getId(),user.getId(),"接受了任务");
        return true;
    }


    /**
     * 取消公共任务
     * @param uid
     * @param instanceId
     * @return
     */
    public boolean cancelCommonInstance(long uid, long instanceId){
        User user = userService.findUser(uid).orElse(null);
        if(null == user) return false;
        WorkflowInstance instance = findInstance(instanceId).orElse(null);
        if(null == instance) return false;
        //不是公共任务
        if(!instance.isCommon()) return false;
        //不是我自己发布的
        if(instance.getPubUserId() != uid){
            return false;
        }
        entityManager.refresh(instance,PESSIMISTIC_WRITE);
        instance.setState(WorkflowInstance.State.CANCELED);
        entityManager.merge(instance);
        return true;
    }

    /**
     * 自己取消任务
     * @param uid
     * @param instanceId
     * @return
     */
    public boolean closeInstance(long uid, long instanceId) {
        User user = userService.findUser(uid).orElse(null);
        return findInstance(instanceId).filter(workflowInstance -> {
            if(canCancel(workflowInstance,user)){
                workflowInstance.setState(WorkflowInstance.State.CANCELED);
                instanceDao.save(workflowInstance);
                if(null != user){
                    logService.addLog(SystemTextLog.Type.WORKFLOW,workflowInstance.getId(),uid,"取消了任务");
                }
                return true;
            }
            return false;
        }).isPresent();
    }

    /**
     * 撤回任务
     * @param uid
     * @param instanceId
     * @return
     */
    public boolean recallInstance(long uid, long instanceId){
        User user = userService.findUser(uid).orElse(null);
        if(null == user) return false;
        return findInstance(instanceId).filter(workflowInstance -> {
            //撤回任务的限制
            if(canRecall(workflowInstance,user)){
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
     * @param uid
     * @param instanceId
     * @param dealerId
     * @return
     */
    public boolean transformInstance(long uid, long instanceId, long dealerId){
        User user = userService.findUser(uid).orElse(null);
        if(null == user) return false;
        User dealer = userService.findUser(dealerId).orElse(null);
        if(null == dealer) return false;
        WorkflowInstance instance = findInstance(instanceId).orElse(null);
        if(null == instance) return false;
        //移交任务的限制
        if(canTransform(instance,user)
                //3.被指派的人在任务的第一个节点的主办岗位上

                ){
            instance.setDealUser(dealer);
            instanceDao.save(instance);
            logService.addLog(SystemTextLog.Type.WORKFLOW, instance.getId(), uid, "移交任务给 " + dealer.getTrueName());
            return true;
        }
        return false;
    }

    /**
     * 是否可以处理
     * @param instance
     * @param user
     * @return
     */
    private boolean canDeal(WorkflowInstance instance, User user){
        return instance.getState().equals(WorkflowInstance.State.DEALING) && checkNodeAuth(instance.getCurrentNode(),user);
    }


    /**
     * 是否可以取消
     * @param instance
     * @param user
     * @return
     */
    private boolean canCancel(WorkflowInstance instance, User user){
        //取消任务的限制
        //1.这是我自己的任务
        //2.任务只有一个节点
        //3.任务也是我发布的
        //4.任务在进行中
        return instance.getDealUserId().equals(user.getId()) && instance.getPubUser().getId().equals(user.getId()) && instance.getNodeList().size() <= 1 && instance.getState().equals(WorkflowInstance.State.DEALING);
    }

    /**
     * 是否可以撤回
     * @param instance
     * @param user
     * @return
     */
    private boolean canRecall(WorkflowInstance instance, User user){
        //1.这是我发布的任务
        return instance.getPubUserId() == (user.getId())
                //2.任务只有一个节点
                && instance.getNodeList().size() <= 1
                //3.任务在进行中
                && instance.getState().equals(WorkflowInstance.State.DEALING)
                //4.拥有撤回的权限
                && instance.getWorkflowModel().getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
    }

    /**
     * 是否可以发布这个模型的任务
     * @param model
     * @param user
     * @return
     */
    public boolean canPubOrPoint(WorkflowModel model, User user){
        //1.拥有该任务的指派权限
        return canPoint(model,user) || canPub(model,user);
    }

    /**
     * 单独检查是否可以发布这个任务(只针对执行者而言)
     * @param model
     * @param user
     * @return
     */
    public boolean canPub(WorkflowModel model, User user){
        //2.在第一个节点上
        List<WorkflowNode> fNodes = nodeDao.findAllByModelAndStartIsTrue(model);
        boolean flag2 = true;
        for (WorkflowNode fNode : fNodes) {
            flag2 = flag2 && fNode.getPersons().stream().anyMatch(p -> p.getType().equals(Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()));
        }
        return flag2;
    }


    /**
     * 是否可以指派这个模型的任务
     * @param model
     * @param user
     * @return
     */
    public boolean canPoint(WorkflowModel model, User user){
       return  model.getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
    }

    /**
     * 是否可以移交
     * @param instance
     * @param user
     * @return
     */
    public boolean canTransform(WorkflowInstance instance, User user){
        WorkflowNode firstNode = nodeDao.findAllByModelAndStartIsTrue(instance.getWorkflowModel()).get(0);
        return
                //1.任务正在进行中
                instance.getState().equals(WorkflowInstance.State.DEALING)
                //2.拥有指派该任务的权利
                && instance.getWorkflowModel().getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
//                && firstNode.getPersons().stream().anyMatch(p -> p.getType().equals(Type.MAIN_QUARTERS) && dealer.hasQuarters(p.getUid()))
    }

    /**
     * 该模型是否可以指派
     * @param model
     * @param user
     * @return
     */
    public boolean canTransform(WorkflowModel model, User user){
        return model.getPermissions().stream().anyMatch(p -> p.getType().equals(WorkflowExtPermission.Type.POINTER) && user.hasQuarters(p.getQid()));
    }

    /**
     * 得到用户所执行的工作流(所有人)
     * @return
     */
    public List<WorkflowInstance> getMyOwnWorks(long uid, Long lessId){
        PageRequest pageRequest = new PageRequest(0,20);
        if(lessId == null){
            return instanceDao.findAllByDealUser_IdOrderByAddTimeDesc(uid,pageRequest);
        }
        else{
            return instanceDao.findAllByDealUser_IdAndIdLessThanOrderByAddTimeDesc(uid,lessId,pageRequest);
        }
    }


    /**
     * 向一个节点提交数据
     *
     * @param instanceId
     * @param data
     * @return
     */
    @Deprecated
    @Override
    public WorkflowInstance submitData(long uid, long instanceId, Map data) throws RestException {
        User user = userService.findUserE(uid);
        WorkflowInstance workflowInstance = findInstance(instanceId).orElseThrow(() -> new CannotFindEntityException(WorkflowInstance.class, instanceId));
        //已完成的禁止再提交
        //只有正在处理中的才可以提交
        if(!workflowInstance.getState().equals(WorkflowInstance.State.DEALING)){
            return workflowInstance;
        }
//        if (workflowInstance.isFinished()) {
//            return workflowInstance;
//        }
        //得到当前应处理的节点
        WorkflowNodeInstance workflowNodeInstance = workflowInstance.getCurrentNode();
        WorkflowNode nodeModel = workflowNodeInstance.getNodeModel();
        //验证是否有权限处理
        if (!checkAuth(workflowInstance.getWorkflowModel(), workflowNodeInstance.getNodeModel(), user)) {
            throw new RestException("该用户没有权限提交数据");
        }

        //处理必填数据
        //对于资料节点来讲, 必填数据的全部传递视为走向下一个节点
        switch (nodeModel.getType()) {
            case "input":
                nodeModel.getNode().submit(user, workflowNodeInstance, data);
                break;

            case "check":
                nodeModel.getNode().submit(user, workflowNodeInstance, data);
                break;

            case "universal":
                nodeModel.getNode().submit(user, workflowNodeInstance, data);
                break;

            case "checkprocess":
                nodeModel.getNode().submit(user, workflowNodeInstance, data);
                break;
        }

        return saveWorkflowInstance(workflowInstance);
    }

    /**
     * 提交数据
     * @param uid
     * @param request
     * @return
     */
    public Result submitData(long uid, SubmitDataRequest request){
        WorkflowInstance instance = findInstance(request.getInstanceId()).orElse(null);
        if(null == instance || !instance.getState().equals(WorkflowInstance.State.DEALING)){
            return Result.error("找不到符合条件的任务");
        }
        User user = userService.findUser(uid).orElse(null);
        if(null == user){
            return Result.error("权限错误");
        }
        WorkflowNodeInstance nodeInstance = findNodeInstance(request.getNodeId()).orElse(null);
        if(null == nodeInstance || nodeInstance.isFinished()){
            return Result.error("找不到符合条件的节点");
        }
        if(!checkNodeAuth(nodeInstance,user)){
            return Result.error("权限验证错误");
        }

        WorkflowNode nodeModel = nodeInstance.getNodeModel();
        switch (nodeModel.getType()) {
            case "input":
                nodeModel.getNode().submit(user, nodeInstance, request.getData(),attributeDao);
                break;

            case "check":
                nodeModel.getNode().submit(user, nodeInstance, request.getData(),attributeDao);
                break;

            case "universal":
                nodeModel.getNode().submit(user, nodeInstance, request.getData(),attributeDao);
                break;

            case "checkprocess":
                nodeModel.getNode().submit(user, nodeInstance, request.getData(),attributeDao);
                break;
        }

        logService.addLog(SystemTextLog.Type.WORKFLOW,instance.getId(),uid,"向 "+nodeInstance.getNodeModel().getName()+" 节点提交了数据");
        return Result.ok();
//        return Result.ok(findInstance(instance.getId()).orElse(null));
    }


    @Deprecated
    public WorkflowInstance goNext(long uid, long instanceId) throws RestException {
        WorkflowInstance instance = findInstanceE(instanceId);
        //正在处理中的才可以提交
        if(!instance.getState().equals(WorkflowInstance.State.DEALING)){
            return instance;
        }
//        if (instance.isFinished()) {
//            return instance;
//        }
        //得到当前节点
        WorkflowNodeInstance currentNode = (instance.getCurrentNode());
        User user = userService.findUserE(uid);

        //验证权限
        if (!checkAuth(instance.getWorkflowModel(), currentNode.getNodeModel(), user)) {
            throw new RestException("授权失败");
        }

        //如果当前节点是资料节点
        WorkflowNode nodeModel = currentNode.getNodeModel();
        switch (nodeModel.getType()) {
            case "input":
                return fromInputNodeToGo(instance, currentNode, (InputNode) nodeModel.getNode());
            case "check":
                return fromCheckNodeToGo(user, instance, currentNode, (CheckNode) nodeModel.getNode());
            case "universal":
                return fromUniversalNodeToGo(instance, currentNode, (UniversalNode) nodeModel.getNode());
        }

        return instance;
    }


    /**
     * 提交节点到下一步
     * @param uid
     * @param instanceId
     * @param nodeId
     * @return
     */
    public Result goNext(long uid, long instanceId, long nodeId){
        WorkflowNodeInstance nodeInstance = findNodeInstance(nodeId).orElse(null);
        if(null == nodeInstance){
            return Result.error("没有找到符合条件的任务");
        }
        User user = userService.findUser(uid).orElse(null);
        if(null == user){
            return Result.error("授权错误");
        }
        WorkflowInstance instance = nodeInstance.getInstance();
        //验证权限
        if (!checkAuth(instance.getWorkflowModel(), nodeInstance.getNodeModel(), user)) {
            return Result.error("授权失败");
        }

        //如果当前节点是资料节点
        WorkflowNode nodeModel = nodeInstance.getNodeModel();
        try{
            switch (nodeModel.getType()) {
                case "input":
                    fromInputNodeToGo(instance, nodeInstance, (InputNode) nodeModel.getNode());
                    break;

                case "check":
                    fromCheckNodeToGo(user, instance, nodeInstance, (CheckNode) nodeModel.getNode());
                    break;

                case "universal":
                    fromUniversalNodeToGo(instance, nodeInstance, (UniversalNode) nodeModel.getNode());
                    break;
            }
        }
        catch (RestException e){
            e.printStackTrace();
            return Result.error(e.getSimpleMessage());
        }

        logService.addLog(SystemTextLog.Type.WORKFLOW,instance.getId(),uid,"向" + nodeModel.getName() + "提交了下一步" );
        return Result.ok();
    }

    /**
     * 上传定位信息
     * @param uid
     * @param request
     * @return
     */
    public Result addPosition(long uid, WorkflowPositionAddRequest request){
        User user = userService.findUser(uid).orElse(null);
        if(null == user){
            return Result.error();
        }
        WorkflowNodeInstance nodeInstance = findNodeInstance(request.getNodeInstanceId()).orElse(null);
        if(null == nodeInstance || !nodeInstance.getId().equals(request.getNodeInstanceId()) || !nodeInstance.getInstance().getState().equals(WorkflowInstance.State.DEALING)){
            return Result.error("找不到对应的节点实例");
        }
        //是否该我处理
        if(!checkNodeAuth(nodeInstance,user)){
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
     * @param uid
     * @param nodeFileId
     * @return
     */
    public boolean deleteNodeFile(long uid, long nodeFileId){
        User user = userService.findUser(uid).orElse(null);
        if(null == user){
            return false;
        }
        WorkflowNodeFile nodeFile = findNodeFile(nodeFileId).orElse(null);
        if(null == nodeFile){
            return false;
        }
        if(!checkNodeAuth(nodeFile.getNodeInstance(),user)){
            return false;
        }
        nodeFileDao.delete(nodeFileId);
        return true;
    }

    /**
     * 上传节点文件
     * @param uid
     * @param instanceId
     * @param nodeId
     * @param fileType
     * @param file
     * @return
     * @throws IOException
     */
    public Result uploadNodeFile(long uid, long instanceId, long nodeId, WorkflowNodeFile.Type fileType, MultipartFile file,String content)  {
        User user = userService.findUser(uid).orElse(null);
        if(null == user){
            return Result.error();
        }
        WorkflowNodeInstance nodeInstance = instanceDao.getCurrentNodeInstance(instanceId).orElse(null);
        if(null == nodeInstance || !nodeInstance.getId().equals(nodeId) || !nodeInstance.getInstance().getState().equals(WorkflowInstance.State.DEALING)){
            return Result.error("找不到对应的节点实例");
        }
        //是否该我处理
        if(!checkNodeAuth(nodeInstance,user)){
            return Result.error("权限错误");
        }
        SystemFile systemFile = new SystemFile();
        try {
            systemFile.setFile(file.getBytes());
        } catch (IOException e) {
            return Result.error("保存文件错误");
        }
        systemFile.setType(SystemFile.Type.WORKFLOW);
        systemFile.setFileName(file.getOriginalFilename());
        systemFile = systemFileDao.save(systemFile);

        WorkflowNodeFile nodeFile = new WorkflowNodeFile();
        nodeFile.setFileName(systemFile.getFileName());
        nodeFile.setFileId(systemFile.getId());
        nodeFile.setNodeInstance(nodeInstance);
        nodeFile.setType(fileType);
        nodeFile.setUserId(user.getId());
        if(fileType.equals(WorkflowNodeFile.Type.SIGN)){
            nodeFile.setContent(content);
        }

        return Result.ok(nodeFileDao.save(nodeFile));
    }


    @Deprecated
    @Override
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

    public boolean editWorkflowModel(WorkflowModelEdit edit){
        return findModel(edit.getId()).filter(model -> {
            //描述可以随意修改
            if (!StringUtils.isEmpty(edit.getInfo())) {
                model.setInfo(edit.getInfo());
            }
            //开关也可以
            model.setOpen(edit.isOpen());
            if (edit.isOpen()) {
                model.setFirstOpen(true);
            }
            //特殊权限
            if(null != edit.getPermissionEdits()){
                setExtPermissions(edit.getPermissionEdits());
            }
            modelDao.save(model);
            return true;
        }).isPresent();
    }


    @Override
    public Result<Set<WorkflowNode>> setPersons(WorkflowQuartersEdit... edits) {
        Set<WorkflowNode> set = new LinkedHashSet<>();
        for (WorkflowQuartersEdit edit : edits) {
            WorkflowNode node = nodeDao.findOne(edit.getNodeId());
            if(null == node) continue;

            if(node.getModel().isFirstOpen()){
                return Result.error("已经上线的工作流无法编辑");
            }

            if(node.isEnd()) continue;

            //解除关联
            personsDao.deleteAllByWorkflowNode(node);

            addWorkflowPersons(edit.getMainQuarters(), node, edit.getName(), Type.MAIN_QUARTERS);
//            addWorkflowPersons(edit.getMainUser(),node, edit.getName(), Type.MAIN_USER);
            addWorkflowPersons(edit.getSupportQuarters(),  node,edit.getName(), Type.SUPPORT_QUARTERS);
//            addWorkflowPersons(edit.getSupportUser(),  node,edit.getName(), Type.SUPPORT_USER);

            set.add(nodeDao.save(node));
        }
        return Result.ok(set);
    }


    /**
     * 设置工作流的额外权限, 因为不涉及更改工作流内容, 所以任何时候都可以进行
     * @param edits
     * @return
     */
    public Set<Long> setExtPermissions(WorkflowExtPermissionEdit ...edits){
        Set<Long> result = new HashSet<>();
        for (WorkflowExtPermissionEdit edit : edits) {
            extPermissionDao.deleteAllByWorkflowModel_IdAndType(edit.getModelId(),edit.getType());
            WorkflowModel workflowModel = findModel(edit.getModelId()).orElse(null);
            if(null == workflowModel) continue;
            for (Long qid : edit.getQids()) {
                WorkflowExtPermission extPermission = new WorkflowExtPermission(null,workflowModel,edit.getType(),qid);
                extPermissionDao.save(extPermission);
            }
            result.add(workflowModel.getId());
        }
        return result;
    }




    /**
     * 删除节点(已废弃)
     *
     * @param modelId
     * @param nodeName
     * @return
     */
    @Deprecated
    @Override
    public WorkflowModel deleteNode(long modelId, String[] nodeName) throws CannotFindEntityException {
        WorkflowModel workflowModel = findModelE(modelId);
        if (workflowModel.isFirstOpen() || workflowModel.isOpen()) {
            return workflowModel;
        }
//        Map<String, BaseNode> nodes = workflowModel.getModel();
//        List deleteList = Arrays.asList(nodeName);
        //开始和结束禁止删除
        for (String name : nodeName) {
            nodeDao.findFirstByModelAndName(workflowModel,name)
                    .filter(n -> !n.isStart() && !n.isEnd())
                    .ifPresent(n -> nodeDao.delete(n));
        }
        return saveWorkflowModel(workflowModel);
    }

    /**
     * 删除节点
     * @param modelId
     * @param nodeId
     * @return
     */
    public boolean deleteNode(long modelId, Long nodeId){
        try{
           nodeDao.deleteAllByModel_IdAndIdAndStartIsFalseAndEndIsFalse(modelId,nodeId);
           return true;
        }
        catch (Exception e){
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
        Map flow = (Map) model.get("flow");
        WorkflowModel same = modelDao.findFirstByNameAndVersion(add.getName(), add.getVersion());
        if (same != null) return Result.error("已经有相同版本的工作流");

        WorkflowModel workflowModel = Transformer.transform(add, WorkflowModel.class);
        Map<String, BaseNode> nodes = new HashMap<>();
        flow.forEach((k, v) -> {
            v = (Map) v;
            BaseNode baseNode = BaseNode.create((Map) v);
//            nodes.put(String.valueOf(k), baseNode);

            //nodelist
            WorkflowNode node = new WorkflowNode();
            node.setModel(workflowModel);
            node.setName(String.valueOf(k));
            node.setType(String.valueOf(((Map) v).get("type")));
            Object next = ((Map)v).get("next");
            //start
            if(((Map) v).containsKey("start")){
                node.setStart((Boolean) ((Map) v).getOrDefault("start",false));
            }
            if(((Map) v).containsKey("end")){
                node.setEnd((Boolean) ((Map) v).getOrDefault("end",false));
            }
            if(node.getType().equals("end")){
                node.setEnd(true);
            }
            //next
            if(next instanceof Collection){
                ((Collection) next).forEach(n -> node.getNext().add(String.valueOf(n)));
            }
            else if(next instanceof String){
                if(!StringUtils.isEmpty(next)){
                    node.getNext().add(String.valueOf(next));
                }
            }
            node.setNode(baseNode);
            nodeDao.save(node);

            workflowModel.getNodeModels().add(node);
        });

//        workflowModel.setModel(nodes);
        workflowModel.setOpen(false);
        workflowModel.setFirstOpen(false);
        workflowModel.setModelName(modelName);


        WorkflowModel result = modelDao.save(workflowModel);
        if (null != result.getId()) {
            return Result.ok(result);
        }
        return Result.error();
    }


    @Override
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

    public Optional<WorkflowNode> createCheckNode(CheckNodeModel nodeModel){
        return findModel(nodeModel.getModelId()).map(model -> {
            CheckNode checkNode = new CheckNode();
            checkNode.setQuestion(nodeModel.getQuestion());
            checkNode.setCount(nodeModel.getCount());
            checkNode.setKey(nodeModel.getKey());
            checkNode.setPs(nodeModel.getPs());
            checkNode.setStates(nodeModel.getStates());

            WorkflowNode node = new WorkflowNode();
            node.setNode(checkNode);
            node.setEnd(false);
            node.setStart(false);
            node.setNext(nodeModel.getNext());
            node.setType("check");
            node.setName(nodeModel.getName());
            node.setModel(model);
            //如果有ID, 则为修改
            node.setId(nodeModel.getNodeId());
            node = nodeDao.save(node);
            return node.getId() == null ? null: node;
        });

    }

    /**
     * 列出指定用户的所有工作流
     */
    @Deprecated
    @Override
    public Page<WorkflowInstance> getUserWorkflows(long uid, Status status, Pageable pageable) throws CannotFindEntityException {
        User user = userService.findUserE(uid);
        boolean isFinished = false;
        switch (status) {
            case DID:
                isFinished = true;
                break;

            case DOING:
                isFinished = false;
                break;
        }
        Set<WorkflowModelPersons> persons = personsDao.findPersonsByUser(user.getQuarters().stream().map(q -> q.getId()).collect(Collectors.toList()), Arrays.asList(user.getId()));

        Set<WorkflowModel> workflowModels = persons
                .stream()
                .map(p -> p.getWorkflowNode().getModel())
                .collect(Collectors.toSet());
        Set<String> nodeNames = persons
                .stream()
                .map(p -> p.getNodeName())
                .collect(Collectors.toSet());
        if (workflowModels.size() == 0) {
            return null;
        }

        Page<WorkflowInstance> list = nodeInstanceDao.getInstanceList(workflowModels, nodeNames, isFinished, pageable);

        return list;
    }


    @Deprecated
    @Override
    public Result<InspectTask> createInspectTask(long createUserId, String modelName, long userId, boolean isAuto) {
//        AtomicReference<User> createUser = new AtomicReference<>();
        return userService.findUser(userId)
                .map(user -> {
                    InspectTask task = new InspectTask();
                    task.setModelName(modelName);
                    task.setDealUser(user);
                    task.setState(InspectTaskState.CREATED);
                    task.setType(isAuto ? InspectTaskType.AUTO : InspectTaskType.MANUAL);
                    task = inspectTaskDao.save(task);
                    if (task.getId() == null) {
                        return Result.error("找不到对应的任务");
                    }
                    //如果是自己创建的任务,那么自己接受任务
                    if (createUserId == userId) {
                        Result result = acceptInspectTask(userId, task.getId());
                        if (!result.isSuccess()) {
                            return result;
                        }
                    }
                    return Result.ok(findInspectTask(task.getId()).orElse(null));
                }).orElse(Result.error());
    }

    public Optional<WorkflowNodeInstance> getCurrentNodeInstance(long userId, long instanceId) {
        AtomicReference<User> userAtomicReference = new AtomicReference<>();
        return userService.findUser(userId)
                .flatMap(user -> {
                    userAtomicReference.set(user);
                    return findInstance(instanceId);
                })
                .map(instance -> {

                    //是否我处理
                    if (!checkAuth(instance.getWorkflowModel(), instance.getCurrentNode().getNodeModel(), userAtomicReference.get())) {
                        return null;
                    }

                    return nodeInstanceDao.findFirstByInstanceAndFinishedIsFalse(instance).orElse(null);
                });
    }


    @Deprecated
    @Override
    public Result<InspectTask> acceptInspectTask(long userId, long taskId) {
        AtomicReference<User> userAtomicReference = new AtomicReference<>();
        return userService.findUser(userId)
                .flatMap(user -> {
                    userAtomicReference.set(user);
                    return findInspectTask(taskId);
                })
                .map(task -> {
                    //如果这个任务已经指定了执行人员
                    if (task.getDealUser() != null) {
                        if (!task.getDealUser().getId().equals(userId)) {
                            return Result.error("这个任务不属于你");
                        }
                    }

                    task.setDealUser(userAtomicReference.get());
                    task.setState(InspectTaskState.RECEIVED);
                    task.setAcceptDate(new Date());

                    //创建一条新的工作流任务
                    List<WorkflowModel> models = modelDao.findAllByModelNameAndOpenIsTrueOrderByVersionDesc(task.getModelName());
                    if (models.size() == 0) {
                        return Result.error("没找到符合条件的工作流模型");
                    }
                    WorkflowInstance instance = null;
//                    WorkflowInstance instance = startNewInstance(userId, models.get(0).getId()).orElse(null);
                    if (null == instance) {
                        return Result.error("自动创建任务失败");
                    }
                    task.setInstance(instance);
                    task = inspectTaskDao.save(task);
                    return Result.ok(task);
                }).orElse(Result.error());
    }


    @Deprecated
    //如果userid为0,则指定为公共任务
    public Page<InspectTask> getInspectTaskList(long uid, String modelName, InspectTaskState state, Pageable pageable) {
        Specification querySpecifi = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("dealUser"), uid == 0 ? null : uid));
                if (!StringUtils.isEmpty(modelName)) {
                    predicates.add(cb.equal(root.get("modelName"), modelName));
                }
                if (state != null) {
                    predicates.add(cb.equal(root.get("modelName"), state));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        return inspectTaskDao.findAll(querySpecifi, pageable);

    }


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

        WorkflowNodeInstance newNode = new WorkflowNodeInstance();
        newNode.setNodeModel(nextNode);
        newNode.setNodeName(nextNode.getName());
        newNode.setFinished(nextNode.isEnd());
        newNode.setInstance(instance);
        newNode = nodeInstanceDao.save(newNode);

        instance.getNodeList().add(newNode);

        if (nextNode.isEnd()) {
            instance.setFinishedDate(new Date());
            instance.setState(WorkflowInstance.State.FINISHED);
        }

        //如果是逻辑节点
        if (nextNode.getType().equals("logic")) {
            runLogicNode(instance, newNode);
        }
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
            LogicNode logicNode = (LogicNode) node.getNodeModel().getNode();

            //检查上一次执行是否在时间范围内
            Optional<TaskCheckLog> optional = taskCheckLogDao.findFirstByTypeAndLinkIdOrderByLastCheckDateDesc("logic", node.getId());
            if (optional.isPresent()) {
                if (new Date().getTime() - optional.get().getLastCheckDate().getTime() < logicNode.getInterval() * 1000) {
                    return;
                }
            }
            //分布锁
            if (utils.isLockingOrLockFailed(key, 5)) {
                return;
            }

            SimpleScriptContext context = new SimpleScriptContext();
            context.setAttribute("tools", new JSInterface(instance, instance.getNodeList().stream().filter(n -> !n.isFinished()).findFirst().get()), ScriptContext.ENGINE_SCOPE);
            buildJSLibrary(context);
            Object obj = engine.eval(logicNode.getCondition(), context);
            //如果没取到值的情况, 等待下一次检测
            if (obj == null) {
                delayRunLogicNodeTask(instance, node);
            } else {
                String behavior = logicNode.getResult().get(obj);
                //都没有命中的情况下, 走else
                if (behavior == null && logicNode.getResult().containsKey("else")) {
                    behavior = logicNode.getResult().get("else");
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
        checkLog.setType("logic_node");
        taskCheckLogDao.save(checkLog);
    }


    private WorkflowInstance fromInputNodeToGo(WorkflowInstance instance, WorkflowNodeInstance currentNode, InputNode nodeModel) throws RestException {
        //检查所有字段, 如果有必填字段没有填写, 那么抛出异常
        for (Map.Entry<String, InputNode.Content> entry : nodeModel.getContent().entrySet()) {
            String k = entry.getKey();
            InputNode.Content v = entry.getValue();
            if (v.isRequired()) {
                Optional<WorkflowNodeAttribute> target = currentNode.getAttributeList().stream()
                        .filter(a -> a.getAttrKey().equals(v.getEname()))
                        .findAny();
                if (!target.isPresent()) {
                    throw new RestException("有必填字段没有填写");
                }
            }
        }
        //找不到下一个就结束了吧
//        BaseNode nextNode = instance.getWorkflowModel().getNextNode(currentNode.getNodeName());
        WorkflowNode nextNode = findNextNodeModel(instance.getWorkflowModel(),currentNode.getNodeModel()).orElse(null);
        if(null == nextNode) return saveWorkflowInstance(instance);
        goNextNode(instance, currentNode, nextNode);
        return saveWorkflowInstance(instance);
    }

    private WorkflowInstance fromCheckNodeToGo(User user, WorkflowInstance instance, WorkflowNodeInstance currentNode, CheckNode nodeModel) throws RestException {

        //检查当前角色是否已经提交信息
        if (currentNode.getAttributeList().stream().filter(a -> a.getDealUser().getId().equals(user.getId())).count() == 0) {
            throw new RestException("你还没有提交信息");
        }

        CheckNodeState targetState = nodeModel.getStates()
                .stream()
                .filter(state -> {
                    return currentNode.getAttributeList()
                            .stream()
                            .filter(a -> a.getAttrKey().equals(nodeModel.getKey()) && a.getAttrValue().equals(state.getItem()))
                            .count() >= state.getCondition();
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
//        }

        if (targetState != null) {

            SimpleScriptContext context = new SimpleScriptContext();
            context.setAttribute("tools", new JSInterface(instance, currentNode), ScriptContext.ENGINE_SCOPE);
            buildJSLibrary(context);
            try {
                engine.eval(targetState.getBehavior(), context);
            } catch (ScriptException e) {
                e.printStackTrace();
            }

            return saveWorkflowInstance(instance);
        }

        return instance;
    }

    private WorkflowInstance fromUniversalNodeToGo(WorkflowInstance instance, WorkflowNodeInstance currentNode, UniversalNode nodeModel) throws RestException {
        //检查所有字段, 要不你就别填, 填了就必须填完
        Set<User> users = new HashSet<>();
        currentNode.getAttributeList().forEach(attribute -> {
            users.add(attribute.getDealUser());
        });
        for (User user : users) {
            for (Map.Entry<String, InputNode.Content> entry : nodeModel.getContent().entrySet()) {
                InputNode.Content v = entry.getValue();
                if (v.isRequired()) {
                    Optional<WorkflowNodeAttribute> target = currentNode.getAttributeList().stream()
                            .filter(a -> a.getDealUser().getId().equals(user.getId()) && a.getAttrKey().equals(v.getEname()))
                            .findAny();
                    if (!target.isPresent()) {
                        return instance;
                    }
                }
            }
        }

        return instance;
    }


    /***js binding**/
    @AllArgsConstructor
    public class JSInterface {
        private WorkflowInstance instance;
        private WorkflowNodeInstance currentNode;

        public void go(String nodeName) {
            WorkflowNode target = findNode(instance.getWorkflowModel(),nodeName).orElse(null);
            if(null == target) return;
            goNextNode(instance, currentNode, target);
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


    private boolean checkNodeAuth(WorkflowNodeInstance nodeInstance, User user){
        List<WorkflowModelPersons> persons = nodeInstance.getNodeModel().getPersons();
        //1. 处理人确认的时候, 只有该处理人可以处理
        boolean flag1 = (null == nodeInstance.getDealer() && persons.stream().anyMatch(p -> p.getType().equals(Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid())));
        //2. 处理人不确认的时候, 拥有该权限的人都可以处理
        boolean flag2 = (null != nodeInstance.getDealer() && user.getId().equals(nodeInstance.getDealer().getId()));
        return flag1 || flag2;
    }

    @Deprecated
    private boolean checkAuth(WorkflowModel workflowModel, WorkflowNode node, User user) {
        List<WorkflowModelPersons> persons = node.getPersons();
        return persons
                .stream()
                .anyMatch(p -> {
                    return p.getWorkflowNode().getId().equals(node.getId()) && (p.getType().equals(Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()));

//                    return
//                            p.getNodeName().equals(node.getName()) && (
//                                    //该人符合个人用户的条件
////                                    (p.getType().equals(IWorkflowService.Type.MAIN_USER) && p.getUid().equals(user.getId())) ||
//                                            //该人所属的岗位符合条件
//                                            (p.getType().equals(IWorkflowService.Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()))
//                            );

                });
    }


    private void addWorkflowPersons(Set<Long> list, WorkflowNode node, String name, Type type) {
        list.forEach(l -> {
            String title = "";
            switch (type){
//                case MAIN_USER:
//                case SUPPORT_USER:
//                    User user = userService.findUser(l).orElse(null);
//                    if(null == user) return;
//                    title = user.getTrueName();
//                    break;
                case MAIN_QUARTERS:
                case SUPPORT_QUARTERS:
                    Quarters quarters = userService.findQuarters(l).orElse(null);
                    if(null == quarters) return;
                    title = quarters.getName();
                    break;
            }
            personsDao.save(new WorkflowModelPersons(null, name,node, type, l,title));
        });
    }
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

    public Optional<WorkflowNode> findNextNodeModel(WorkflowModel model, WorkflowNode nodeModel){
            if(nodeModel.isEnd()) return Optional.ofNullable(nodeModel);
            if(nodeModel.getNext().size() == 0) return Optional.empty();
            if(nodeModel.getType().equals("input")){
                String nextName = nodeModel.getNext().get(0);
                return model.getNodeModels().stream().filter(nm -> {
                    return nm.getName().equals(nextName);
                }).findAny();
            }
            return Optional.empty();

    }

    public List<String> getAvaliableModelNames(){
        return modelDao.findAllByOpenIsTrue().stream()
                .map(item -> item.getName())
                .distinct()
                .collect(Collectors.toList());
    }


    public Optional<FetchWorkflowInstanceResponse> fetchWorkflowInstance(long uid, long id){
        User user = userService.findUser(uid).orElse(null);
        if(null == user) return Optional.empty();
        return findInstance(id).map(workflowInstance -> {
            FetchWorkflowInstanceResponse response = new FetchWorkflowInstanceResponse();
            response.setInstance(workflowInstance);
            response.setCurrentNodeModel(workflowInstance.getCurrentNode().getNodeModel());
            response.setDeal(canDeal(workflowInstance,user));
            response.setCancel(canCancel(workflowInstance,user));
            response.setRecall(canRecall(workflowInstance,user));
            response.setTransform(canTransform(workflowInstance,user));
            response.setLogs(systemTextLogDao.findAllByTypeAndLinkIdOrderByAddTimeDesc(SystemTextLog.Type.WORKFLOW,id));
            response.setTransformUsers(modelDao.getFirstNodeUsers(workflowInstance.getWorkflowModel().getId()));

            return response;
        });
    }



    public Optional<WorkflowNode> findNode(WorkflowModel model, String nodeName){
        return nodeDao.findFirstByModelAndName(model,nodeName);
    }

    @Override
    public Optional<WorkflowModel> findModel(long id) {
        return Optional.ofNullable(modelDao.findOne(id));
    }


    public Optional<WorkflowNode> findNode(long id){
        return Optional.ofNullable(nodeDao.findOne(id));
    }

    public Optional<WorkflowNodeInstance> findNodeInstance(long id){
        return Optional.ofNullable(nodeInstanceDao.findOne(id));
    }

    public Optional<WorkflowNodeFile> findNodeFile(long id){
        return Optional.ofNullable(nodeFileDao.findOne(id));
    }

    public Optional<GPSPosition> findNodeGPSPosition(long nodeFileId){
        return findNodeFile(nodeFileId)
                .filter(nodeFile -> nodeFile.getType().equals(WorkflowNodeFile.Type.POSITION))
                .map(nodeFile -> gpsPositionDao.getOne(nodeFile.getFileId()));
    }

    @Override
    public WorkflowModel findModelE(long id) throws CannotFindEntityException {
        return findModel(id).orElseThrow(() -> new CannotFindEntityException(WorkflowModel.class, id));
    }

    @Override
    public Optional<WorkflowInstance> findInstance(long id) {
        return Optional.ofNullable(instanceDao.findOne(id));
    }

    @Override
    public WorkflowInstance findInstanceE(long id) throws CannotFindEntityException {
        return findInstance(id).orElseThrow(() -> new CannotFindEntityException(WorkflowInstance.class, id));
    }

    @Override
    public Optional<InspectTask> findInspectTask(long id) {
        return Optional.ofNullable(inspectTaskDao.findOne(id));
    }

    @Override
    public InspectTask findInspectTaskE(long id) throws CannotFindEntityException {
        return findInspectTask(id).orElseThrow(() -> new CannotFindEntityException(InspectTask.class, id));
    }

}
