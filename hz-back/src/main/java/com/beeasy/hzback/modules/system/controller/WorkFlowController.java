package com.beeasy.hzback.modules.system.controller;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.IWorkflowInstanceDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelPersonsDao;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.form.Pager;
import com.beeasy.hzback.modules.system.form.WorkflowModelAdd;
import com.beeasy.hzback.modules.system.form.WorkflowQuartersEdit;
import com.beeasy.hzback.modules.system.service.WorkflowService;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
    IWorkflowInstanceDao workflowInstance;

    @Autowired
    WorkflowService workflowService;

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
        Map flow = (Map) model.get("flow");
        if(flow == null) return Result.error("没有这个模型!");

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
        if(workflowModel.isFirstOpen() || workflowModel.isOpen()) return Result.error("无法编辑该工作流");

        try{
            Map<String, Map> nodes = workflowModel.getModel();
            JSONObject newNodes = JSON.parseObject(node);
            //开始和结束节点禁止编辑
            AtomicReference<String> startName = new AtomicReference<>();
            AtomicReference<String> endName = new AtomicReference<>();
            nodes.forEach((k,v) -> {
                Map map = (Map) v;
                if(((Map) v).get("start") != null){
                    startName.set((String) ((Map) v).get("name"));
                }
                if(((Map) v).get("end") != null){
                    endName.set((String) ((Map) v).get("name"));
                }
            });
            newNodes.keySet().forEach(k -> {
                if(k.equals(startName.get()) || k.equals(endName.get())){
                    newNodes.remove(k);
                }
            });
            newNodes.forEach((k,v) -> {
                nodes.put(k,(Map)v);
            });
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
        boolean ret = workflowService.deleteNode(modelId,nodeName);
        return ret ? Result.ok() : Result.error();
    }


    @ApiOperation(value = "设置主办或者协办的岗位",notes = "目前协办只可以查看, 在资料节点, 只要有一个主办提交了资料, 就可以继续下一步")
    @PutMapping("/model/setPersons")
    public Result setQuarters(
            @Valid WorkflowQuartersEdit edit,
            BindingResult bindingResult
            ){
        boolean ret = workflowService.setPersons(edit);
        return ret ? Result.ok() : Result.error();
    }

    @ApiOperation(value = "启用/停用工作流", notes = "一经启用, 禁止再编辑, 只能新增新版本")
    @PutMapping("/model/open")
    public Result changeOpen(
            Integer modelId,
            boolean open
    ){
        boolean ret = workflowService.changeOpen(modelId,open);
        return ret ? Result.ok() : Result.error();
    }

    @ApiOperation(value = "发起工作流", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "工作流模型ID")
    })
    @PostMapping("/newTask")
    public Result newTask(
            Integer modelId
    ){
        boolean ret = workflowService.startNewTask(Utils.getCurrentUser(),modelId);
        return ret ? Result.ok() : Result.error();
    }



}
