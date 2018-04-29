package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.CheckNodeModel;
import com.beeasy.hzback.modules.system.form.WorkflowModelAdd;
import com.beeasy.hzback.modules.system.form.WorkflowQuartersEdit;
import com.beeasy.hzback.modules.system.node.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//import com.beeasy.hzback.core.helper.Rest;

@Slf4j
@Service
@Transactional
public class WorkflowService implements IWorkflowService {

    @Value("${workflow.timeStep}")
    private int timeStep = 5;

    private int c = 1;

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


    /**
     * 开始一个新的工作流任务
     *
     * @param uid
     * @param modelId
     * @return
     */
    @Override
    public Result<WorkflowInstance> startNewInstance(long uid, long modelId) {
        AtomicReference<User> userAtomicReference = new AtomicReference<>();
        return userService.findUser(uid)
                .flatMap(user -> {
                    userAtomicReference.set(user);
                    return findModel(modelId);
                })
                .map(workflowModel -> {
                    if (!workflowModel.isOpen()) {
                        return Result.error("工作流没有开启");
                    }

                    WorkflowNode firstNode = nodeDao.findAllByModelAndStartIsTrue(workflowModel).get(0);
                    if (!checkAuth(workflowModel, firstNode, userAtomicReference.get())) {
                        return Result.error("你没有权限发起这个工作流");
                    }

                    //任务主体
                    WorkflowInstance workflowInstance = new WorkflowInstance();
                    workflowInstance.setWorkflowModel(workflowModel);

                    //插入第一个节点
                    workflowInstance.addNode(firstNode);

                    workflowInstance = saveWorkflowInstance(workflowInstance);
                    return workflowInstance.getId() == null ? Result.error() : Result.ok(workflowInstance);
                }).orElse(Result.error());
    }

    @Override
    public WorkflowInstance closeInstance(long instanceId) throws CannotFindEntityException {
        return null;
//        WorkflowInstance instance = findInstanceE(instanceId);
//        BaseNode endNode = instance.getWorkflowModel().getEndNode();
//        WorkflowNodeInstance currentNode = instance.getCurrentNode();
//        currentNode.setFinished(true);
//        instance.addNode(endNode);
//        return saveWorkflowInstance(instance);

    }

    /**
     * 向一个节点提交数据
     *
     * @param instanceId
     * @param data
     * @return
     */
    @Override
    public WorkflowInstance submitData(long uid, long instanceId, Map data) throws RestException {
        User user = userService.findUserE(uid);
        WorkflowInstance workflowInstance = findInstance(instanceId).orElseThrow(() -> new CannotFindEntityException(WorkflowInstance.class, instanceId));
        //已完成的禁止再提交
        if (workflowInstance.isFinished()) {
            return workflowInstance;
        }
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


    @Override
    public WorkflowInstance goNext(long uid, long instanceId) throws RestException {
        WorkflowInstance instance = findInstanceE(instanceId);
        if (instance.isFinished()) {
            return instance;
        }
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
            addWorkflowPersons(edit.getMainUser(),node, edit.getName(), Type.MAIN_USER);
            addWorkflowPersons(edit.getSupportQuarters(),  node,edit.getName(), Type.SUPPORT_QUARTERS);
            addWorkflowPersons(edit.getSupportUser(),  node,edit.getName(), Type.SUPPORT_USER);

            set.add(nodeDao.save(node));
        }
        return Result.ok(set);
    }



    /**
     * 删除节点
     *
     * @param modelId
     * @param nodeName
     * @return
     */
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
                node.setStart(true);
            }
            if(((Map) v).containsKey("end") || node.getType().equals("end")){
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
     * 创建节点
     *
     * @return
     */
    public Optional<WorkflowNode> createNode(long modelId, String node) {
        return Optional.empty();
//       return findModel(modelId)

//        WorkflowModel workflowModel = modelDao.findOne(modelId);
//        if (workflowModel.isFirstOpen() || workflowModel.isOpen()) return false;
//        JSONObject newNodes = JSON.parseObject(node);
//        newNodes.forEach((k, v) -> {
//            v = (Map) v;
//            BaseNode b = BaseNode.create(k, (Map) v);
//            if (b.isEnd() || b.isStart()) return;
//            workflowModel.getModel().put(k, b);
//        });
//        modelDao.save(workflowModel);
//        return true;
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
            node = nodeDao.save(node);
            return node.getId() == null ? null: node;
        });

    }

    /**
     * 列出指定用户的所有工作流
     */
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
                    WorkflowInstance instance = startNewInstance(userId, models.get(0).getId()).orElse(null);
                    if (null == instance) {
                        return Result.error("自动创建任务失败");
                    }
                    task.setInstance(instance);
                    task = inspectTaskDao.save(task);
                    return Result.ok(task);
                }).orElse(Result.error());
    }


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
        newNode.setNodeName(nextNode.getName());
        newNode.setFinished(nextNode.isEnd());
        newNode.setInstance(instance);
        newNode = nodeInstanceDao.save(newNode);

        instance.getNodeList().add(newNode);

        if (nextNode.isEnd()) {
            instance.setFinishedDate(new Date());
            instance.setFinished(true);
        }

        //如果是逻辑节点
        if (nextNode.getType().equals("logic")) {
            runLogicNode(instance, newNode);
        }
    }

    private void buildJSLibrary(ScriptContext context) {
        try {
            engine.eval(cache.getBehaviorLibrary(), context);
        } catch (ScriptException e) {
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
        WorkflowNode nextNode = findNextNode(instance.getWorkflowModel(), currentNode.getNodeName()).orElse(null);
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


    private boolean checkAuth(WorkflowModel workflowModel, WorkflowNode node, User user) {
        List<WorkflowModelPersons> persons = node.getPersons();
        return persons
                .stream()
                .anyMatch(p -> {
                    return
                            p.getNodeName().equals(node.getName()) && (
                                    //该人符合个人用户的条件
                                    (p.getType().equals(IWorkflowService.Type.MAIN_USER) && p.getUid().equals(user.getId())) ||
                                            //该人所属的岗位符合条件
                                            (p.getType().equals(IWorkflowService.Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()))
                            );

                });
    }


    private void addWorkflowPersons(Set<Long> list, WorkflowNode node, String name, Type type) {
        list.forEach(l -> {
            String title = "";
            switch (type){
                case MAIN_USER:
                case SUPPORT_USER:
                    User user = userService.findUser(l).orElse(null);
                    if(null == user) return;
                    title = user.getTrueName();
                    break;
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

    public Optional<WorkflowNode> findNextNode(WorkflowModel model, String nodeName){
        return nodeDao.findFirstByModelAndName(model,nodeName).flatMap(node -> {
            if(node.isEnd()) return Optional.ofNullable(node);
            if(node.getNext().size() == 0) return Optional.empty();
            if(node.getType().equals("input")){
                return nodeDao.findFirstByModelAndName(model,node.getNext().get(0));
            }
            return Optional.empty();
        });

    }

    public List<String> getAvaliableModelNames(){
        return modelDao.findAllByOpenIsTrue().stream()
                .map(item -> item.getName())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<WorkflowModel> getAllWorkflows(){
        List<WorkflowModel> models = modelDao.findAllByOpenIsTrue();
        Map<String,WorkflowModel> map = new HashMap<>();
        models.forEach(model -> {
            WorkflowModel exist = map.get(model.getName());
            if(null == exist){
                map.put(model.getName(),model);
            }
            else{
                if(model.getVersion().compareTo(exist.getVersion()) > 0){
                    map.put(model.getName(),model);
                }
            }
        });

        return map.entrySet().stream()
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
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
