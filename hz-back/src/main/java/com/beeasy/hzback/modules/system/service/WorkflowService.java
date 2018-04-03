package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.WorkflowModelAdd;
import com.beeasy.hzback.modules.system.form.WorkflowQuartersEdit;
import com.beeasy.hzback.modules.system.node.BaseNode;
import com.beeasy.hzback.modules.system.node.CheckNode;
import com.beeasy.hzback.modules.system.node.EndNode;
import com.beeasy.hzback.modules.system.node.InputNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class WorkflowService implements IWorkflowService {


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


    /**
     * 开始一个新的工作流任务
     *
     * @param uid
     * @param modelId
     * @return
     */
    public WorkflowInstance startNewInstance(long uid, long modelId) {
        User user = userService.find(uid);
        WorkflowModel workflowModel = modelDao.findOne(modelId);
        if (workflowModel == null || !workflowModel.isOpen()) {
            return null;
        }
        String firstName = getFirstNodeName(workflowModel);
        if (!checkAuth(workflowModel, firstName, user)) {
            return null;
        }

        //任务主体
        WorkflowInstance workflowInstance = new WorkflowInstance();
        workflowInstance.setWorkflowModel(workflowModel);
        workflowInstance = instanceDao.save(workflowInstance);

        //插入第一个节点
        WorkflowNodeInstance workflowNodeInstance = new WorkflowNodeInstance();
        workflowNodeInstance.setNodeName(firstName);
        workflowNodeInstance.setInstance(workflowInstance);
        workflowNodeInstance.setFinished(false);
        nodeInstanceDao.save(workflowNodeInstance);

        return workflowInstance;
    }

    /**
     * 向一个节点提交数据
     *
     * @param instanceId
     * @param data
     * @return
     */
    @Override
    public WorkflowInstance submitData(long uid, long instanceId, Object data, String ps) throws RestException {
        User user = userService.find(uid);
        WorkflowInstance workflowInstance = instanceDao.findOne(instanceId);
        //已完成的禁止再提交
        if(workflowInstance.isFinished()){
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
        if (nodeModel instanceof InputNode) {
            return submitInputData(user, workflowInstance, workflowNodeInstance, (InputNode) nodeModel, (Map) data);
        } else if (nodeModel instanceof CheckNode) {
            return submitCheckData(user, workflowInstance, workflowNodeInstance, (CheckNode) nodeModel, (String) data, ps);
        }

        return workflowInstance;
    }


    @Override
    public WorkflowInstance goNext(long uid, long instanceId) throws RestException {
        WorkflowInstance instance = findInstance(instanceId);
        if(instance.isFinished()){
            return instance;
        }
        //得到当前节点
        WorkflowNodeInstance currentNode = (instance.getCurrentNode());
        User user = userService.find(uid);

        //验证权限
        if (!checkAuth(instance.getWorkflowModel(), currentNode.getNodeName(), user)) {
            throw new RestException("授权失败");
        }

        //如果当前节点是资料节点
        BaseNode nodeModel = currentNode.getNodeModel();
        if (nodeModel instanceof InputNode) {
            //检查所有字段, 如果有必填字段没有填写, 那么抛出异常
            for (Map.Entry<String, InputNode.Content> entry : ((InputNode) currentNode.getNodeModel()).getContent().entrySet()) {
                String k = entry.getKey();
                InputNode.Content v = entry.getValue();
                if(v.isRequired()){
                    Optional<WorkflowNodeAttribute> target = currentNode.getAttributeList().stream()
                            .filter(a -> a.getAttrKey().equals(v.getEname()))
                            .findAny();
                    if (!target.isPresent()) {
                        throw new RestException("有必填字段没有填写");
                    }
                }
            }

        } else if (nodeModel instanceof CheckNode) {
            //检查当前角色是否已经提交信息
            if (!currentNode.getAttributeList().stream().filter(a -> a.getDealUser().getId().equals(user.getId())).findAny().isPresent()) {
                return null;
            }
            //如果是审核节点, 那么判断审核人数是否符合条件
            long successCount = currentNode.getAttributeList()
                    .stream()
                    .filter(a -> !a.getAttrKey().equals("ps") && !a.getAttrValue().equals(((CheckNode) nodeModel).getContent().getPassItem().equals(a.getAttrValue())))
                    .count();

            long failedCount = currentNode.getAttributeList()
                    .stream()
                    .filter(a -> !a.getAttrKey().equals("ps") && !a.getAttrValue().equals(((CheckNode) nodeModel).getContent().getPassItem().equals(a.getAttrValue())))
                    .count();

            //无论如何, 当前角色如果处理了, 都标记为处理完成
//            currentNode.setDealDate(new Date());
//            currentNode.setFinished(true);

            //如果通过
            if (successCount >= ((CheckNode) nodeModel).getContent().getPass()) {
                log.info("success");
            }
            //如果禁止通过
            else if (failedCount >= ((CheckNode) nodeModel).getContent().getFail()) {
                log.info("failed");
            }
            else{
                return instance;
            }

        }

        BaseNode nextNode = instance.getNextNode();

        //当前节点处理完毕, 一定要在getnext之后, 否则会找不到对应的节点
        currentNode.setFinished(true);
        currentNode.setDealDate(new Date());

        //如果找不到下一个节点, 那么走最后的结束节点
        if(nextNode.isEnd()){
            instance.setFinished(true);
            instance.setFinishedDate(new Date());
            return saveWorkflowInstance(instance);
        }

        WorkflowNodeInstance newNode = new WorkflowNodeInstance();
        newNode.setNodeName(nextNode.getName());
        newNode.setInstance(instance);
        newNode.setFinished(false);
        instance.getNodeList().add(newNode);

        return instanceDao.save(instance);
    }


    @Override
    public boolean setOpen(long modelId, boolean open) {
        WorkflowModel workflowModel = modelDao.findOne(modelId);
        workflowModel.setOpen(open);
        if (open) {
            workflowModel.setFirstOpen(true);
        }
        modelDao.save(workflowModel);
        return true;
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

            addWorkflowPersons(edit.getMainQuarters(),workflowModel, edit.getName(), Type.MAIN_QUARTERS);
            addWorkflowPersons(edit.getMainUser(),workflowModel, edit.getName(), Type.MAIN_USER);
            addWorkflowPersons(edit.getSupportQuarters(),workflowModel, edit.getName(), Type.SUPPORT_QUARTERS);
            addWorkflowPersons(edit.getSupportUser(),workflowModel, edit.getName(), Type.SUPPORT_USER);

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
    public boolean deleteNode(long modelId, String[] nodeName) {
        WorkflowModel workflowModel = modelDao.findOne(modelId);
        if (workflowModel == null) return false;
        if (workflowModel.isFirstOpen() || workflowModel.isOpen()) return false;
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
        modelDao.save(workflowModel);
        return true;
    }

    /**
     * 根据已有模型创建新工作流模型
     *
     * @param modelName
     * @param add
     * @return
     */
    public WorkflowModel createWorkflow(String modelName, WorkflowModelAdd add) throws RestException {
        Map<String, Map> map = (Map) cache.getConfig();
        Map model = (Map) map.get("workflow").get(modelName);
        Map flow = (Map) model.get("flow");
        if (flow == null) return null;
        WorkflowModel same = modelDao.findFirstByNameAndVersion(add.getName(), add.getVersion());
        if (same != null) throw new RestException("已经有相同版本的工作流");

        Map<String, BaseNode> nodes = new HashMap<>();
        flow.forEach((k, v) -> {
            v = (Map) v;
            BaseNode baseNode = parseNode(String.valueOf(k), (Map) v);
            if (null == baseNode) return;
            nodes.put(String.valueOf(k), baseNode);

        });

        WorkflowModel workflowModel = Transformer.transform(add, WorkflowModel.class);
        workflowModel.setModel(nodes);
        workflowModel.setOpen(false);
        workflowModel.setFirstOpen(false);

        WorkflowModel result = modelDao.save(workflowModel);
        return result;
    }


    public boolean deleteWorkflow(long id, boolean force) {
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
            BaseNode b = null;
            switch ((String) ((Map) v).get("type")) {
                case "check":
                    b = JSON.toJavaObject((JSONObject) v, CheckNode.class);
                    break;

                case "input":
                    InputNode inputNode = JSON.toJavaObject((JSONObject) v, InputNode.class);
                    break;
            }
            if (b == null) return;
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
    public Page<WorkflowInstance> getUserWorkflows(long uid, Status status, Pageable pageable) {
        User user = userService.find(uid);
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


    /**
     * 提交资料节点的数据
     *
     * @return
     */
    private WorkflowInstance submitInputData(User user, WorkflowInstance wInstance, WorkflowNodeInstance wNInstance, InputNode nodeModel, Map data) {
        Map<String, InputNode.Content> model = nodeModel.getContent();
        for (Map.Entry<String, InputNode.Content> entry : model.entrySet()) {
            String k = entry.getKey();
            InputNode.Content v = entry.getValue();

            //如果是必填字段, 却没有传递
            String attrKey = v.getEname();
            //不论是否必填, 空属性就略过
            if (StringUtils.isEmpty(data.get(attrKey))) {
                continue;
            }
            //验证属性格式
            //TODO: 这里需要验证属性的格式


            //覆盖旧节点的信息
            Optional<WorkflowNodeAttribute> target = wNInstance.getAttributeList()
                    .stream()
                    .filter(attr -> attr.getAttrKey().equals(attrKey))
                    .findFirst();
            WorkflowNodeAttribute attribute = target.orElse(new WorkflowNodeAttribute());
            attribute.setAttrKey(attrKey);
            attribute.setAttrValue((String) data.get(attrKey));
            attribute.setDealUser(user);
            attribute.setNodeInstance(wNInstance);
            attributeDao.save(attribute);

            wNInstance.getAttributeList().add(attribute);
        }
//        nodeInstanceDao.save(wNInstance);
        return instanceDao.save(wInstance);
    }

    private WorkflowInstance submitCheckData(User user, WorkflowInstance wInstance, WorkflowNodeInstance wNInstance, CheckNode nodeModel, String item, String ps) {
        CheckNode.Content content = nodeModel.getContent();
        //如果可选项不在选项里面, 那么无视
        if (!content.getItems().contains(item)) {
            return null;
        }
        //每个审批节点只允许审批一次
        WorkflowNodeAttribute attribute = wNInstance.getAttributeList()
                .stream()
                .filter(a -> a.getDealUser().getId().equals(user.getId()) && !a.getAttrKey().equals("ps"))
                .findAny()
                .orElse(new WorkflowNodeAttribute());
        attribute.setDealUser(user);
        attribute.setAttrKey(content.getKey());
        attribute.setAttrValue(item);
        attribute.setNodeInstance(wNInstance);
        attributeDao.save(attribute);

        //如果填写了审核说明
        if (!StringUtils.isEmpty(ps)) {
            attribute = wNInstance.getAttributeList()
                    .stream()
                    .filter(a -> a.getDealUser().getId().equals(user.getId()) && a.getAttrKey().equals("ps"))
                    .findAny()
                    .orElse(new WorkflowNodeAttribute());
            attribute.setDealUser(user);
            attribute.setAttrKey("ps");
            attribute.setAttrValue(ps);
            attribute.setNodeInstance(wNInstance);
            attributeDao.save(attribute);
        }

        return instanceDao.save(wInstance);
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
                                    (p.getType().equals(IWorkflowService.Type.MAIN_USER) && p.getUid() == user.getId()) ||
                                            //该人所属的岗位符合条件
                                            (p.getType().equals(IWorkflowService.Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()))
                            );

                });
    }

    private BaseNode parseNode(String k, Map v) {
        switch ((String) ((Map) v).get("type")) {
            case "check":
                return (parseCheckNode(k, v));
//
            case "input":
                return (parseInputNode(k, v));

            case "end":
                return (JSON.parseObject(JSON.toJSONString(v), EndNode.class));
        }
        return null;
    }

    private CheckNode parseCheckNode(String name, Map v) {
        v.put("name", name);
        return JSON.parseObject(JSON.toJSONString(v), CheckNode.class);
    }

    private InputNode parseInputNode(String name, Map<String, Object> v) {
        v.put("name", name);
        InputNode inputNode = new InputNode();
        inputNode.setName(name);
        inputNode.setType("input");
        Object order = v.get("order");
        if (null != order) {
            if (order instanceof Integer) {
                inputNode.setOrder((Integer) order);
            }
        }
        //起始和结束禁止编辑
        //start和end不能同时存在
        if (v.containsKey("start")) {
            inputNode.setStart(true);
        } else if (v.containsKey("end")) {
            inputNode.setEnd(true);
        }
        Map content = (Map) v.get("content");
        content.forEach((ck, cv) -> {
            InputNode.Content cnt = new InputNode.Content();
            cnt.setCname(String.valueOf(ck));

            if (cv instanceof Map) {
                cnt.setType((String) ((Map) cv).get("map"));
                cnt.setEname((String) ((Map) cv).get("name"));
                cnt.setRequired(((Map) cv).get("required").equals("y"));
            } else if (cv instanceof String) {
                List<String> args = Utils.splitByComma(String.valueOf(cv));
                if (args.size() != 3) {
                    return;
                }
                cnt.setEname(args.get(0));
                cnt.setType(args.get(1));
                cnt.setRequired(args.get(2).equals("y"));
            } else {
                return;
            }
            inputNode.getContent().put(cnt.getEname(), cnt);
        });
        return inputNode;
    }


    private void addWorkflowPersons(Set<Long> list,WorkflowModel workflowModel, String name, Type type){
        list.forEach(l -> {
            workflowModel.getPersons().add(new WorkflowModelPersons(null,name, workflowModel, type, l));
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

    public WorkflowInstance saveWorkflowInstance(WorkflowInstance workflowInstance){
        return instanceDao.save(workflowInstance);
    }

    @Override
    public WorkflowModel findModel(long id) {
        return modelDao.findOne(id);
    }

    public WorkflowInstance findInstance(long id) {
        return (instanceDao.findOne(id));
    }

}
