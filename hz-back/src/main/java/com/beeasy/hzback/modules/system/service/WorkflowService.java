package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.WorkflowModelAdd;
import com.beeasy.hzback.modules.system.form.WorkflowQuartersEdit;
import com.beeasy.hzback.modules.system.node.BaseNode;
import com.beeasy.hzback.modules.system.node.CheckNode;
import com.beeasy.hzback.modules.system.node.InputNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WorkflowService {

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
    SystemConfigCache cache;


    /**
     * 开始一个新的工作流任务
     * @param user
     * @param modelId
     * @return
     */
    public boolean startNewTask(User user, Integer modelId){
        WorkflowModel workflowModel = modelDao.findOne(modelId);
        if(workflowModel == null || !workflowModel.isOpen()){
            return false;
        }
        String firstName = getFirstNodeName(workflowModel);
        if(firstName == null) return false;
        if(!checkAuth(workflowModel,firstName,user)){
            return false;
        }

//        if(checkAuth(workflowModel,workflowModel.getModel().keySet()))


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

        return true;
    }

    /**
     * 向一个节点提交数据
     * @param instanceId
     * @param data
     * @return
     */
    public boolean submitData(User user, Long instanceId, Object data){
        WorkflowInstance workflowInstance = instanceDao.findOne(instanceId);
        if(workflowInstance == null) return false;
        //得到当前应处理的节点
        WorkflowNodeInstance workflowNodeInstance = workflowInstance.getNodeList().get(workflowInstance.getNodeList().size() - 1);
        if(workflowNodeInstance.isFinished()){
            return false;
        }
        Map nodeModel = workflowInstance.getWorkflowModel().getModel().get(workflowNodeInstance.getNodeName());
        if(nodeModel == null) return false;
        //验证是否有权限处理
        List<WorkflowModelPersons> persons = workflowInstance.getWorkflowModel().getPersons();
        if(checkAuth(workflowInstance.getWorkflowModel(),workflowNodeInstance.getNodeName(),user)){
            return false;
        }

        //处理必填数据
        //对于资料节点来讲, 必填数据的全部传递视为走向下一个节点
        switch ((String)nodeModel.get("type")){
            case "input":
                return submitInputData(user, workflowInstance, workflowNodeInstance, nodeModel, (Map) data);

            case "check":
                return submitCheckData(user, workflowInstance, workflowNodeInstance, nodeModel, (String) data);
        }

        return false;
    }

//    public boolean goNext(User user, )


    /**
     * 一经启用, 禁止再编辑
     * @param modelId
     * @param open
     * @return
     */
    public boolean changeOpen(Integer modelId, boolean open){
        WorkflowModel workflowModel = modelDao.findOne(modelId);
        if(workflowModel == null) return false;
        workflowModel.setOpen(open);
        if(open){
            workflowModel.setFirstOpen(true);
        }
        modelDao.save(workflowModel);
        return true;
    }

    public boolean setPersons(WorkflowQuartersEdit edit){
        WorkflowModel workflowModel = modelDao.findOne(edit.getModelId());
        if(workflowModel == null || workflowModel.isFirstOpen() || workflowModel.isOpen()) return false;
        //如果没有这个节点
        if(!workflowModel
                .getModel()
                .containsKey(edit.getName())
                ){
            return false;
        }
        personsDao.deleteAllByWorkflowModel(workflowModel);
        for (Integer integer : edit.getMainQuarters()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,edit.getName(),workflowModel, WorkflowModelPersons.Type.MAIN_QUARTERS,integer);
            personsDao.save(persons);
        }
        for (Integer integer : edit.getMainUser()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,edit.getName(),workflowModel, WorkflowModelPersons.Type.MAIN_USER,integer);
            personsDao.save(persons);
        }
        for (Integer integer : edit.getSupportQuarters()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,edit.getName(),workflowModel, WorkflowModelPersons.Type.SUPPORT_QUARTERS,integer);
            personsDao.save(persons);
        }
        for (Integer integer : edit.getSupportUser()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null, edit.getName(), workflowModel, WorkflowModelPersons.Type.SUPPORT_USER,integer);
            personsDao.save(persons);
        }
        return true;
    }


    public boolean deleteNode(Integer modelId, String[] nodeName){
        WorkflowModel workflowModel = modelDao.findOne(modelId);
        if(workflowModel.isFirstOpen() || workflowModel.isOpen()) return false;
        Map<String, Map> nodes = workflowModel.getModel();
//        List deleteList = Arrays.asList(nodeName);
        //开始和结束禁止删除
        for (String name : nodeName) {
            if((nodes.containsKey(name) && (nodes.get(name).get("start") != null) ||
                    nodes.get(name).get("end") != null )
                    ){
                continue;
            }
            nodes.remove(name);
        }
        modelDao.save(workflowModel);
        return true;
    }

    public boolean createWorkflow(String modelName, WorkflowModelAdd add){
        Map<String,Map> map = (Map) cache.getConfig();
        Map model = (Map) map.get("workflow").get(modelName);
        Map flow = (Map) model.get("flow");
        if(flow == null) return false;
        WorkflowModel same = modelDao.findFirstByNameAndVersion(add.getName(),add.getVersion());
        if(same != null) return false;

        Map<String,BaseNode> nodes = new HashMap<>();
        flow.forEach((k,v) -> {
            v = (Map)v;
            switch ((String)((Map) v).get("type")){
                case "check":
                    nodes.put((String) k, JSON.parseObject(JSON.toJSONString(v), CheckNode.class));
                    break;

                case "input":
                    nodes.put((String) k,JSON.parseObject(JSON.toJSONString(v), InputNode.class));
            }
        });

        WorkflowModel workflowModel = Transformer.transform(add,WorkflowModel.class);
        workflowModel.setModel(flow);
        workflowModel.setOpen(false);
        workflowModel.setFirstOpen(false);

        WorkflowModel result = modelDao.save(workflowModel);
        return result.getId() > 0;

    }



    /**
     * 提交资料节点的数据
     * @return
     */
    private boolean submitInputData(User user, WorkflowInstance wInstance, WorkflowNodeInstance wNInstance, Map nodeModel, Map data){
        Map<String,Object> model = (Map<String, Object>) nodeModel.get("content");
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            List<String> args = Utils.splitByComma((String) v);
            if(args.size() != 2) throw new RuntimeException();
            //如果是必填字段, 却没有传递
            String attrKey = args.get(0);
            if(args.get(2).equals("y")){
                if(!data.containsKey(attrKey) || StringUtils.isEmpty(data.get(attrKey))) {
                    throw new RuntimeException();
                }
            }
            //不论是否必填, 空属性就略过
            if(StringUtils.isEmpty(data.get(attrKey))){
                continue;
            }
            //验证属性格式
            //TODO: 这里需要验证属性的格式

            WorkflowNodeAttribute attribute = attributeDao.findFirstByNodeInstanceAndAttrKey(wNInstance,attrKey).orElse(new WorkflowNodeAttribute());
            attribute.setAttrKey(attrKey);
            attribute.setAttrValue((String) data.get(attrKey));
            attribute.setDealUser(user);
            attribute.setNodeInstance(wNInstance);
            attributeDao.save(attribute);
        }

        return true;
    }

    private boolean submitCheckData(User user, WorkflowInstance wInstance, WorkflowNodeInstance wNInstance, Map nodeModel, String item){
        Map<String,Object> model = (Map<String, Object>) nodeModel.get("content");

        return false;
    }


    private String getFirstNodeName(WorkflowModel workflowModel){
        for (Map.Entry<String, Map> entry : workflowModel.getModel().entrySet()) {
            if(entry.getValue().containsKey("start")){
                return entry.getKey();
            }
        }
        return null;
    }

    private String getLastNodeName(WorkflowModel workflowModel){
        for (Map.Entry<String, Map> entry : workflowModel.getModel().entrySet()) {
            if(entry.getValue().containsKey("end")){
                return entry.getKey();
            }
        }
        return null;
    }


    private boolean checkAuth(WorkflowModel workflowModel, String nodeName ,User user){
        List<WorkflowModelPersons> persons = workflowModel.getPersons();
        return persons
                .stream()
                .anyMatch(p -> {
                    return
                            p.getNodeName().equals(nodeName) && (
                                    //该人符合个人用户的条件
                                    (p.getType().equals(WorkflowModelPersons.Type.MAIN_USER) && p.getUid() == user.getId()) ||
                                            //该人所属的岗位符合条件
                                            (p.getType().equals(WorkflowModelPersons.Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()))
                            );

                } );
    }

}
