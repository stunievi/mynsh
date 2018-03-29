package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.Result;
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
import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import javax.validation.Valid;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Api(tags = "工作流API", value = "编辑模型需要管理员权限")
@RestController
@RequestMapping("/api/workflow")
public class WorkFlowController {

    @Autowired
    IWorkflowModelDao workflowModelDao;

    @Autowired
    IWorkflowModelPersonsDao workflowModelPersonsDao;

    static Map<String,Map> map;

    @ApiOperation(value = "创建工作流", notes = "根据已有的模型创建一个新的工作流实体,可选模型: 资料收集")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "要创建的标准模型")
    })
    @PostMapping("/model")
    public Result createNewWorkFlow(
            String modelName,
            @Valid WorkflowModelAdd add,
            BindingResult bindingResult
            ){
        if(map == null){
            try {
                @Cleanup Reader r = new FileReader("/Users/bin/work/configlist.yaml");
                Yaml yaml = new Yaml();
                map = (Map) yaml.load(r);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map model = (Map) map.get("workflow").get(modelName);
        model = (Map) model.get("flow");
        if(model == null) return Result.error("没有这个模型!");

        List same = workflowModelDao.findAllByNameAndVersion(add.getName(),add.getVersion());
        if(same.size() > 0){
            return Result.error("已经有同名同版本的工作流");
        }
        WorkflowModel workflowModel = Transformer.transform(add,WorkflowModel.class);
        workflowModel.setModel(model);
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
        WorkflowModel workflowModel = workflowModelDao.findOne(modelId);
        if(modelId == null || workflowModel.isFirstOpen() || workflowModel.isOpen()) return Result.ok("无法编辑该工作流");

        try{
            Map nodes = workflowModel.getModel();
            JSONObject map = JSON.parseObject(node);
            map.forEach((k,v) -> {
                nodes.put(k,v);
            });
//            workflowModel.setModel(nodes);
            workflowModelDao.save(workflowModel);
            return Result.ok();
        }catch (Exception e){
        }
        return Result.error();
    }

    @ApiOperation(value = "删除节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "工作流模型ID", required = true),
            @ApiImplicitParam(name = "nodeName", value = "要删除的名字数组", required = true)
    })
    @DeleteMapping("/model/node")
    public Result deleteNode(
            Integer modelId,
            String[] nodeName
    ){
        WorkflowModel workflowModel = workflowModelDao.findOne(modelId);
        if(modelId == null || workflowModel.isFirstOpen() || workflowModel.isOpen()) return Result.ok("无法编辑该工作流");
        Map<String,Map> nodes = workflowModel.getModel();
//        JSONObject nodes = JSON.parseObject(workflowModel.getModel());
        JSONObject newNodes = new JSONObject();
        nodes.forEach((k,v) -> {
            if(!Arrays.asList(nodeName).contains(k)){
                newNodes.put(k,v);
            }
        });
//        workflowModel
//        workflowModel.setModel(JSON.toJSONString(newNodes));
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
        workflowModelPersonsDao.deleteAllByWorkflowModel(workflowModel);
        for (Integer integer : edit.getMainQuarters()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,workflowModel, WorkflowModelPersons.Type.MAIN_QUARTERS,integer);
            workflowModelPersonsDao.save(persons);
        }
        for (Integer integer : edit.getMainUser()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,workflowModel, WorkflowModelPersons.Type.MAIN_USER,integer);
            workflowModelPersonsDao.save(persons);
        }
        for (Integer integer : edit.getSupportQuarters()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,workflowModel, WorkflowModelPersons.Type.SUPPORT_QUARTERS,integer);
            workflowModelPersonsDao.save(persons);
        }
        for (Integer integer : edit.getSupportUser()) {
            WorkflowModelPersons persons = new WorkflowModelPersons(null,workflowModel, WorkflowModelPersons.Type.SUPPORT_USER,integer);
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


}
