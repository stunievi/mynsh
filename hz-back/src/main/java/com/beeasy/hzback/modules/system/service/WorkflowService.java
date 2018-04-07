package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.WorkflowModelAdd;
import com.beeasy.hzback.modules.system.form.WorkflowQuartersEdit;
import com.beeasy.hzback.modules.system.node.BaseNode;
import com.beeasy.hzback.modules.system.node.CheckNode;
import com.beeasy.hzback.modules.system.node.InputNode;
import com.beeasy.hzback.modules.system.node.LogicNode;
import com.beeasy.hzback.modules.system.task.ITask;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class WorkflowService implements IWorkflowService, ITask {

    @Value("${workflow.timeStep}")
    private int timeStep = 5;

    @Autowired
    ScriptEngine engine;

    @Autowired
    IWorkflowModelDao modelDao;

    @Autowired
    IWorkflowInstanceDao instanceDao;

    @Autowired
    IWorkflowNodeInstanceDao nodeInstanceDao;

    @Autowired
    IWorkflowModelPersonsDao personsDao;

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
    public WorkflowInstance startNewInstance(long uid, long modelId) throws RestException {
        User user = userService.findUserE(uid);
        WorkflowModel workflowModel = findModel(modelId).orElseThrow(() -> new CannotFindEntityException(WorkflowModel.class, modelId));
        if (!workflowModel.isOpen()) {
            throw new RestException("工作流没有开启");
        }
        String firstName = getFirstNodeName(workflowModel);
        if (!checkAuth(workflowModel, firstName, user)) {
            throw new RestException("找不到工作流节点");
        }

        //任务主体
        WorkflowInstance workflowInstance = new WorkflowInstance();
        workflowInstance.setWorkflowModel(workflowModel);

        //插入第一个节点
        WorkflowNodeInstance workflowNodeInstance = new WorkflowNodeInstance();
        workflowNodeInstance.setNodeName(firstName);
        workflowNodeInstance.setInstance(workflowInstance);
        workflowNodeInstance.setFinished(false);
        workflowInstance.getNodeList().add(workflowNodeInstance);
//        nodeInstanceDao.save(workflowNodeInstance);

        return saveWorkflowInstance(workflowInstance);
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
        BaseNode nodeModel = workflowInstance.getWorkflowModel().getModel().get(workflowNodeInstance.getNodeName());
        //验证是否有权限处理
        if (!checkAuth(workflowInstance.getWorkflowModel(), workflowNodeInstance.getNodeName(), user)) {
            throw new RestException("该用户没有权限提交数据");
        }

        //处理必填数据
        //对于资料节点来讲, 必填数据的全部传递视为走向下一个节点
        switch (nodeModel.getType()){
            case "input":
                nodeModel.submit(user,workflowNodeInstance,  data);
                break;

            case "check":
                nodeModel.submit(user,workflowNodeInstance,  data);
                break;

            case "checkprocess":
                nodeModel.submit(user,workflowNodeInstance,data);
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
        if (!checkAuth(instance.getWorkflowModel(), currentNode.getNodeName(), user)) {
            throw new RestException("授权失败");
        }

        //如果当前节点是资料节点
        BaseNode nodeModel = currentNode.getNodeModel();
        if (nodeModel.getType().equals("input")) {
            return fromInputNodeToGo(instance, currentNode, (InputNode) nodeModel);
        } else if (nodeModel.getType().equals("check")) {
            return fromCheckNodeToGo(user, instance, currentNode, (CheckNode) nodeModel);
        }

        return instance;
    }


    @Override
    public WorkflowModel setOpen(long modelId, boolean open) throws CannotFindEntityException {
        WorkflowModel workflowModel = findModelE(modelId);
        workflowModel.setOpen(open);
        if (open) {
            workflowModel.setFirstOpen(true);
        }
        return saveWorkflowModel(workflowModel);
    }

    @Override
    public WorkflowModel setPersons(long modelId, WorkflowQuartersEdit... edits) {
        WorkflowModel workflowModel = modelDao.findOne(modelId);
        if (workflowModel == null || workflowModel.isFirstOpen() || workflowModel.isOpen()) return null;
        for (WorkflowQuartersEdit edit : edits) {
            //如果没有这个节点
            if (!workflowModel
                    .getModel()
                    .containsKey(edit.getName())
                    ) {
                continue;
            }
            //删除没用的部分
            workflowModel.getPersons().removeIf(p -> p.getNodeName().equals(edit.getName()));

            addWorkflowPersons(edit.getMainQuarters(), workflowModel, edit.getName(), Type.MAIN_QUARTERS);
            addWorkflowPersons(edit.getMainUser(), workflowModel, edit.getName(), Type.MAIN_USER);
            addWorkflowPersons(edit.getSupportQuarters(), workflowModel, edit.getName(), Type.SUPPORT_QUARTERS);
            addWorkflowPersons(edit.getSupportUser(), workflowModel, edit.getName(), Type.SUPPORT_USER);

        }
        return saveWorkflowModel(workflowModel);
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
        if (workflowModel.isFirstOpen() || workflowModel.isOpen()){
            return workflowModel;
        }
        Map<String, BaseNode> nodes = workflowModel.getModel();
//        List deleteList = Arrays.asList(nodeName);
        //开始和结束禁止删除
        for (String name : nodeName) {
            if ((nodes.containsKey(name) && (nodes.get(name).isStart()) ||
                    nodes.get(name).isEnd())
                    ) {
                continue;
            }
            nodes.remove(name);
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
    public WorkflowModel createWorkflow(String modelName, WorkflowModelAdd add) throws RestException {
        Map<String, Map> map = (Map) cache.getWorkflowConfig();
        if(!map.containsKey(modelName)){
            throw new RestException("没找到工作流模型");
        }
        Map model = map.get(modelName);
        Map flow = (Map) model.get("flow");
        WorkflowModel same = modelDao.findFirstByNameAndVersion(add.getName(), add.getVersion());
        if (same != null) throw new RestException("已经有相同版本的工作流");

        Map<String, BaseNode> nodes = new HashMap<>();
        flow.forEach((k, v) -> {
            v = (Map) v;
            BaseNode baseNode = BaseNode.create(String.valueOf(k), (Map) v);
            nodes.put(String.valueOf(k), baseNode);
        });

        WorkflowModel workflowModel = Transformer.transform(add, WorkflowModel.class);
        workflowModel.setModel(nodes);
        workflowModel.setOpen(false);
        workflowModel.setFirstOpen(false);
        workflowModel.setBaseName(modelName);

        WorkflowModel result = modelDao.save(workflowModel);
        return result;
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
    public boolean createNode(long modelId, String node) {
        WorkflowModel workflowModel = modelDao.findOne(modelId);
        if (workflowModel.isFirstOpen() || workflowModel.isOpen()) return false;
        JSONObject newNodes = JSON.parseObject(node);
        newNodes.forEach((k, v) -> {
            v = (Map) v;
            BaseNode b = BaseNode.create(k, (Map) v);
            if (b.isEnd() || b.isStart()) return;
            workflowModel.getModel().put(k, b);
        });
        modelDao.save(workflowModel);
        return true;
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
                .map(p -> p.getWorkflowModel())
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
    public InspectTask createInspectTask(String modelName, long userId, boolean isAuto){
        User user = userService.findUser(userId).orElse(null);
        InspectTask task = new InspectTask();
        task.setModelName(modelName);
        task.setDealUser(user);
        task.setState(InspectTaskState.CREATED);
        task.setType(isAuto ? InspectTaskType.AUTO : InspectTaskType.MANUAL);
        return inspectTaskDao.save(task);
    }

    @Override
    public WorkflowInstance acceptInspectTask(long userId, long taskId) throws RestException {
        User user = userService.findUserE(userId);
        InspectTask task = findInspectTaskE(taskId);

        //如果这个任务已经指定了执行人员
        if(task.getDealUser() != null){
            if(!task.getDealUser().getId().equals(userId)){
                throw new RestException("这个任务不属于你, 无法领取");
            }
        }

        task.setDealUser(user);
        task.setState(InspectTaskState.RECEIVED);
        task.setAcceptDate(new Date());

        //创建一条新的工作流任务
        List<WorkflowModel> models = modelDao.findRecentVersions(task.getModelName());
        if(models.size() == 0){
            throw new RestException("没找到符合条件的工作流模型");
        }
        WorkflowInstance instance = startNewInstance(user.getId(),models.get(0).getId());
        task.setInstance(instance);
        task = inspectTaskDao.save(task);
        return task.getInstance();
    }


    //如果userid为0,则指定为公共任务
    public Page<InspectTask> getInspectTaskList(long uid, String modelName, InspectTaskState state, Pageable pageable){
        Specification querySpecifi = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("dealUser"), uid == 0 ? null : uid));
                if(!StringUtils.isEmpty(modelName)){
                    predicates.add(cb.equal(root.get("modelName"),modelName));
                }
                if(state != null){
                    predicates.add(cb.equal(root.get("modelName"),state));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        return inspectTaskDao.findAll(querySpecifi,pageable);

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

    private void goNextNode(WorkflowInstance instance, WorkflowNodeInstance currentNode, BaseNode nextNode) {
        //本节点完毕
        currentNode.setFinished(true);
        currentNode.setDealDate(new Date());

        WorkflowNodeInstance newNode = new WorkflowNodeInstance();
        newNode.setNodeName(nextNode.getName());
        newNode.setFinished(nextNode.isEnd());
        newNode.setInstance(instance);
        instance.getNodeList().add(newNode);

        if (nextNode.isEnd()) {
            instance.setFinishedDate(new Date());
            instance.setFinished(true);
//            newNode.setFinished(true);
        }
//        } else {
//            WorkflowNodeInstance newNode = new WorkflowNodeInstance();
//            newNode.setNodeName(nextNode.getName());
//            newNode.setFinished(false);
//            newNode.setInstance(instance);
//            instance.getNodeList().add(newNode);
//        }

        //如果是逻辑节点
        if (nextNode instanceof LogicNode) {
            runLogicNode(instance, (LogicNode) nextNode,null);
        }
    }

    private void buildJSLibrary(ScriptContext context) {
        try {
            engine.eval(cache.getbehaviorLibrary(), context);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    private void runLogicNodeTask(WorkflowInstance instance,LogicNode logicNode){
        SimpleScriptContext context = new SimpleScriptContext();
        context.setAttribute("tools", new JSInterface(instance, instance.getNodeList().stream().filter(n -> !n.isFinished()).findFirst().get()), ScriptContext.ENGINE_SCOPE);
        buildJSLibrary(context);
        try {
            Object obj = engine.eval(logicNode.getCondition(), context);
            //如果没取到值的情况, 等待下一次检测
            if (obj == null) {
                delayRunLogicNodeTask(instance,logicNode);
            } else {
                String behavior = logicNode.getResult().get(obj);
//                for (Map.Entry<String, String> entry : logicNode.getResult().entrySet()) {
//                    String key = entry.getKey();
//                    if (key.equals("else")) continue;
//                    if (key.equals(obj)) {
//                        behavior = entry.getValue();
//                        break;
//                    }
//                }
                //都没有命中的情况下, 走else
                if(behavior == null && logicNode.getResult().containsKey("else")){
                    behavior = logicNode.getResult().get("else");
                }
                if(behavior != null){
                    engine.eval(behavior,context);
                }
                saveWorkflowInstance(instance);
            }
        } catch (ScriptException e) {
            e.printStackTrace();
            delayRunLogicNodeTask(instance, logicNode);
        }
    }

    private void delayRunLogicNodeTask(WorkflowInstance instance, LogicNode logicNode){
        int delay = logicNode.getTime();
        if(delay < timeStep){
            delay = timeStep;
        }
        SystemTask task = new SystemTask();
        task.setRunTime(new Date(System.currentTimeMillis() + delay));
        task.setClassName(WorkflowService.class.getName());
        task.setTaskKey("logic");
        task.setThreadLock(false);
        task.getParams().put("instanceId",instance.getId());
        task.getParams().put("nodeName",logicNode.getName());
        systemTaskDao.save(task);
    }

    private void runLogicNode(WorkflowInstance instance, LogicNode logicNode, Integer delay) {
        //检查时间
        int time = ((LogicNode) logicNode).getTime();
        if(delay != null){
            time = delay;
        }
        //立刻执行
        if (time == 0) {
            runLogicNodeTask(instance,logicNode);
        } else {
            delayRunLogicNodeTask(instance,logicNode);
        }
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
        BaseNode nextNode = instance.getNextNode().orElseGet(() -> instance.getEndNode());
        goNextNode(instance, currentNode, nextNode);
        return saveWorkflowInstance(instance);
    }

    private WorkflowInstance fromCheckNodeToGo(User user, WorkflowInstance instance, WorkflowNodeInstance currentNode, CheckNode nodeModel) throws RestException {

        //检查当前角色是否已经提交信息
        if (currentNode.getAttributeList().stream().filter(a -> a.getDealUser().getId().equals(user.getId())).count() == 0) {
            throw new RestException("你还没有提交信息");
        }

        CheckNode.State targetState = null;
        for (Map.Entry<String, CheckNode.State> entry : ((CheckNode) nodeModel).getStates().entrySet()) {
            String name = entry.getKey();
            CheckNode.State state = entry.getValue();
            long count = currentNode.getAttributeList()
                    .stream()
                    .filter(a -> a.getAttrKey().equals(nodeModel.getKey()) && a.getAttrValue().equals(state.getItem()))
                    .count();
            if (count >= state.getCondition()) {
                targetState = state;
                break;
            }
        }

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

    @Override
    public void doTask(String key, Map params) throws RestException {
        log.info("doTask called");
        switch (key){
            case "logic":
                doLogicTask(params);
                return;
        }
    }

    private void doLogicTask(Map params) throws RestException {
        Long instanceId = (Long) params.get("instanceId");
        String nodeName = (String) params.get("nodeName");
        WorkflowInstance instance = findInstance(instanceId).orElseThrow(() -> new CannotFindEntityException(WorkflowInstance.class,instanceId));
        BaseNode node = instance.findNode(nodeName).orElseThrow(() -> new RestException(""));
        runLogicNode(instance, (LogicNode) node,0);
    }


    /***js binding**/
    @AllArgsConstructor
    public class JSInterface {
        private WorkflowInstance instance;
        private WorkflowNodeInstance currentNode;

        public void go(String nodeName) {
            BaseNode target = instance.findNode(nodeName).orElseGet(() -> instance.getEndNode());
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


    private String getFirstNodeName(WorkflowModel workflowModel) {
        for (Map.Entry<String, BaseNode> entry : workflowModel.getModel().entrySet()) {
            if (entry.getValue().isStart()) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getLastNodeName(WorkflowModel workflowModel) {
        for (Map.Entry<String, BaseNode> entry : workflowModel.getModel().entrySet()) {
            if (entry.getValue().isEnd()) {
                return entry.getKey();
            }
        }
        return null;
    }


    private boolean checkAuth(WorkflowModel workflowModel, String nodeName, User user) {
        List<WorkflowModelPersons> persons = workflowModel.getPersons();
        return persons
                .stream()
                .anyMatch(p -> {
                    return
                            p.getNodeName().equals(nodeName) && (
                                    //该人符合个人用户的条件
                                    (p.getType().equals(IWorkflowService.Type.MAIN_USER) && p.getUid().equals(user.getId())) ||
                                            //该人所属的岗位符合条件
                                            (p.getType().equals(IWorkflowService.Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()))
                            );

                });
    }




    private void addWorkflowPersons(Set<Long> list, WorkflowModel workflowModel, String name, Type type) {
        list.forEach(l -> {
            workflowModel.getPersons().add(new WorkflowModelPersons(null, name, workflowModel, type, l));
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

    @Override
    public Optional<WorkflowModel> findModel(long id) {
        return Optional.ofNullable(modelDao.findOne(id));
    }

    @Override
    public WorkflowModel findModelE(long id) throws CannotFindEntityException {
        return findModel(id).orElseThrow(() -> new CannotFindEntityException(WorkflowModel.class,id));
    }

    @Override
    public Optional<WorkflowInstance> findInstance(long id) {
        return Optional.ofNullable(instanceDao.findOne(id));
    }

    @Override
    public WorkflowInstance findInstanceE(long id) throws CannotFindEntityException {
        return findInstance(id).orElseThrow(() -> new CannotFindEntityException(WorkflowInstance.class,id));
    }

    @Override
    public Optional<InspectTask> findInspectTask(long id){
        return Optional.ofNullable(inspectTaskDao.findOne(id));
    }

    @Override
    public InspectTask findInspectTaskE(long id) throws CannotFindEntityException {
        return findInspectTask(id).orElseThrow(() -> new CannotFindEntityException(InspectTask.class,id));
    }

}
