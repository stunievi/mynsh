package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelPersonsDao;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.entity.WorkflowModelPersons;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.form.WorkflowModelAdd;
import com.beeasy.hzback.modules.system.form.WorkflowQuartersEdit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(tags = "工作流API", value = "编辑模型需要管理员权限")
@Transactional
@RestController
@RequestMapping("/api/workflow")
public class WorkFlowController {

    @Autowired
    IWorkflowModelDao workflowModelDao;

    @Autowired
    IWorkflowModelPersonsDao workflowModelPersonsDao;

    @Autowired
    SystemConfigCache cache;


    @ApiOperation(value = "创建工作流", notes = "根据已有的模型创建一个新的工作流实体,可选模型: 资料收集")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "要创建的标准模型", required = true)
    })
    @PostMapping("/model")
    public Result createNewWorkFlow(
            String modelName,
            @Valid WorkflowModelAdd add,
            BindingResult bindingResult
            ){
        Map<String,Map> map = (Map) cache.getConfig();
        Map model = (Map) map.get("workflow").get(modelName);
        if(model == null) return Result.error("没有这个模型!");
        List flow = (List) model.get("flow");
        if(model == null) return Result.error("没有这个模型!");

        List same = workflowModelDao.findAllByNameAndVersion(add.getName(),add.getVersion());
        if(same.size() > 0){
            return Result.error("已经有同名同版本的工作流");
        }
        WorkflowModel workflowModel = Transformer.transform(add,WorkflowModel.class);
        workflowModel.setModel(flow);
        workflowModel.setOpen(false);
        workflowModel.setFirstOpen(false);


        WorkflowModel result = workflowModelDao.save(workflowModel);
        return result.getId() > 0 ? Result.ok(result.getId()) : Result.error();
    }

    @ApiOperation(value = "删除工作流模型", notes = "当一个工作流启用后, 视为正式上线, 无法再进行删除, 只能停用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型ID")
    })
    @DeleteMapping("/model")
    public Result delete(
            Integer modelId
    ){
        if(modelId == null) return Result.error();
        WorkflowModel workflowModel = workflowModelDao.findOne(modelId);
        if(workflowModel.isFirstOpen() || workflowModel.isOpen()) return Result.ok("无法删除该工作流");
        workflowModelDao.delete(modelId);
        return Result.ok();
    }

    @ApiOperation(value = "模型列表", notes = "查询已经存在的工作模型列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "模型名字")
    })
    @GetMapping("/model")
    public Result<Page<WorkflowModel>> list(
            Pager pager,
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            String modelName
    ){
        if(StringUtils.isEmpty(modelName)){
            return Result.ok(workflowModelDao.findAll(pageable));
        }
        return Result.ok(workflowModelDao.findAllByName(modelName,pageable));
    }

    @ApiOperation(value = "添加工作流节点", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "工作流模型ID", required = true),
            @ApiImplicitParam(name = "node", value = "节点内容, map格式, 同名节点会直接覆盖(等同于编辑)", required = true)
    })
    @RequestMapping(value = "/model/node", method = {RequestMethod.POST,RequestMethod.PUT})
    public Result createNode(
            Integer modelId,
            String node
    ){
        if(modelId == null) return Result.error("无法编辑该工作流");
        WorkflowModel workflowModel = workflowModelDao.findOne(modelId);
        if(workflowModel.isFirstOpen() || workflowModel.isOpen()) return Result.ok("无法编辑该工作流");

        try{
            List<Map<String, Object>> nodes = workflowModel.getModel();
            JSONArray newNodes = JSON.parseArray(node);
            //开始和结束节点禁止编辑
            String startName = (String) nodes.stream().filter(n -> n.containsKey("start") && n.get("start").equals(true)).findFirst().get().get("name");
            String endName = (String) nodes.stream().filter(n -> n.containsKey("end") && n.get("end").equals(true)).findFirst().get().get("name");

            List newNodesList = newNodes
                    .stream()
                    .filter(n -> {
                        if(!((JSONObject)n).containsKey("name")){
                            return false;
                        }
                        String name = ((JSONObject)n).getString("name");
                        if(name.equals(startName) || name.equals(endName)){
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            nodes = nodes
                    .stream()
                    .filter(n -> {
                        return newNodesList
                                .stream()
                                .anyMatch(nn -> !((Map)nn).get("name").equals( ((Map)n).get("name") ));
                    })
                    .collect(Collectors.toList());
            nodes.addAll(newNodesList);
            workflowModel.setModel(nodes);
            workflowModelDao.save(workflowModel);
            return Result.ok();
        }catch (Exception e){
        }
        return Result.error();
    }

    @ApiOperation(value = "删除节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "工作流模型ID", required = true),
            @ApiImplicitParam(name = "nodeName", value = "要删除的名字数组", required = true, allowMultiple = true)
    })
    @DeleteMapping("/model/node")
    public Result deleteNode(
            Integer modelId,
            String[] nodeName
    ){
        if(modelId == null) return Result.ok("无法编辑该工作流");
        WorkflowModel workflowModel = workflowModelDao.findOne(modelId);
        if(workflowModel.isFirstOpen() || workflowModel.isOpen()) return Result.ok("无法编辑该工作流");
        List<Map<String, Object>> nodes = workflowModel.getModel();
        List deleteList = Arrays.asList(nodeName);
        //开始和结束禁止删除
        List<Map<String, Object>> finalNodes = nodes;
        deleteList = (List) deleteList
                .stream()
                .filter(nName -> {
                    Optional<Map<String, Object>> target = finalNodes
                            .stream()
                            .filter(n -> n.get("name").equals(nName))
                            .findFirst();
                    if (target.isPresent()) {
                        if((target.get().get("start")) != null || target.get().get("end") != null){
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        List finalDeleteList = deleteList;
        nodes = nodes
                .stream()
                .filter(n -> {
                    String name = (String) n.get("name");
                    if(!finalDeleteList.contains(name.trim())){
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        workflowModel.setModel(nodes);
        workflowModelDao.save(workflowModel);
        return Result.ok();
    }


    @ApiOperation(value = "设置主办或者协办的岗位",notes = "目前协办只可以查看, 在资料节点, 只要有一个主办提交了资料, 就可以继续下一步")
    @PutMapping("/model/setPersons")
    public Result setQuarters(
            @Valid WorkflowQuartersEdit edit,
            BindingResult bindingResult
            ){
        WorkflowModel workflowModel = workflowModelDao.findOne(edit.getModelId());
        if(workflowModel == null || workflowModel.isFirstOpen() || workflowModel.isOpen()) return Result.error();
        //如果没有这个节点
        if(workflowModel
                .getModel()
                .stream()
                .allMatch(n -> !n.equals(edit.getName()))
                ){
            return Result.error("没有找到这个节点");
        }
        workflowModelPersonsDao.deleteAllByWorkflowModel(workflowModel);
        for (Integer integer : edit.getMainQuarters()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,edit.getName(),workflowModel, WorkflowModelPersons.Type.MAIN_QUARTERS,integer);
            workflowModelPersonsDao.save(persons);
        }
        for (Integer integer : edit.getMainUser()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,edit.getName(),workflowModel, WorkflowModelPersons.Type.MAIN_USER,integer);
            workflowModelPersonsDao.save(persons);
        }
        for (Integer integer : edit.getSupportQuarters()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,edit.getName(),workflowModel, WorkflowModelPersons.Type.SUPPORT_QUARTERS,integer);
            workflowModelPersonsDao.save(persons);
        }
        for (Integer integer : edit.getSupportUser()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null, edit.getName(), workflowModel, WorkflowModelPersons.Type.SUPPORT_USER,integer);
            workflowModelPersonsDao.save(persons);
        }
        return Result.error();
    }

    @ApiOperation(value = "启用/停用工作流", notes = "一经启用, 禁止再编辑, 只能新增新版本")
    @PutMapping("/model/open")
    public Result changeOpen(
            Integer modelId,
            boolean open
    ){
        WorkflowModel workflowModel = workflowModelDao.findOne(modelId);
        if(workflowModel == null) return Result.error();
        workflowModel.setOpen(open);
        if(open){
            workflowModel.setFirstOpen(true);
        }
        workflowModelDao.save(workflowModel);
        return Result.ok();
    }

    @ApiOperation(value = "发起工作流", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "工作流模型ID")
    })
    @PostMapping("/newTask")
    public Result newTask(
            Integer modelId
    ){
        if(modelId == null) return Result.error("无法找到这个模型");
        WorkflowModel workflowModel = workflowModelDao.findOne(modelId);
        //没有开启的流程无法执行
        if(!workflowModel.isOpen()){
            return Result.error("无法开始没有打开的模型");
        }
        //检查是否符合发起人的条件
        User user = Utils.getCurrentUser();
        List<WorkflowModelPersons> persons = workflowModel.getPersons();
        if(!persons
                .stream()
                .anyMatch(p -> {
                    return
                            //该人符合个人用户的条件
                            (p.getType().equals(WorkflowModelPersons.Type.MAIN_USER) && p.getUid() == user.getId())
                            ||
                            //该人所属的岗位符合条件
                            (p.getType().equals(WorkflowModelPersons.Type.MAIN_QUARTERS) && user.hasQuarters(p.getUid()));
                } )){

            return Result.error("你没有权限发起这个业务流程");
        }

        return Result.error();

    }



}
