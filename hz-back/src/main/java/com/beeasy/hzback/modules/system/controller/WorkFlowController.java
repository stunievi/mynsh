package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelDao;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import com.beeasy.hzback.modules.system.entity.WorkflowNode;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.node.BaseNode;
import com.beeasy.hzback.modules.system.node.CheckNode;
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
import java.util.Map;
import java.util.Optional;

@Api(tags = "工作流API", value = "编辑模型需要管理员权限")
@Transactional
@RestController
@RequestMapping("/api/workflow")
public class WorkFlowController {

    @Autowired
    IWorkflowModelDao workflowModelDao;

    @Autowired
    WorkflowService workflowService;

    @ApiOperation(value = "创建工作流", notes = "根据已有的模型创建一个新的工作流实体,可选模型: 资料收集")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "要创建的标准模型", required = true)
    })
    @PostMapping("/model")
    public Object createNewWorkFlow(
            String modelName,
            @Valid WorkflowModelAdd add,
            BindingResult bindingResult
            ) throws RestException {
        return workflowService.createWorkflow(modelName,add);
    }

    @ApiOperation(value = "删除工作流模型", notes = "当一个工作流启用后, 视为正式上线, 无法再进行删除, 只能停用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型ID")
    })
    @DeleteMapping("/model")
    public Object delete(
            Integer modelId
    ){
        return workflowService.deleteWorkflowModel(modelId,false);
    }

    @ApiOperation(value = "模型列表", notes = "查询已经存在的工作模型列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "模型名字")
    })
    @GetMapping("/models")
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

    @ApiOperation(value = "查找指定模型", notes = "")
    @GetMapping("/model/id")
    public Result getModelInfo(Long modelId){
        Optional<WorkflowModel> optional = workflowService.findModel(modelId);
        return optional.isPresent() ? Result.ok(optional.get()) : Result.error();
    }

//    @ApiOperation(value = "添加工作流节点", notes = "")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "modelId", value = "工作流模型ID", required = true),
//            @ApiImplicitParam(name = "node", value = "节点内容, map格式, 同名节点会直接覆盖(等同于编辑)", required = true),
//    })
//    @RequestMapping(value = "/model/node", method = {RequestMethod.POST,RequestMethod.PUT})
//    public Object createNode(
//            Integer modelId,
//            String node
//    ){
//        return workflowService.createNode(modelId,node);
//    }


    @ApiOperation(value = "添加审核节点", notes = "同名视为修改")
    @PostMapping("/model/node/addCheck")
    public Result<WorkflowNode> createCheckNode(
            @Valid @RequestBody CheckNodeModel node,
            BindingResult bindingResult
    ){
        return Result.finish(workflowService.createCheckNode(node));
    }


    @ApiOperation(value = "删除节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "工作流模型ID", required = true),
            @ApiImplicitParam(name = "nodeName", value = "要删除的名字数组", required = true, allowMultiple = true)
    })
    @DeleteMapping("/model/node")
    public Object deleteNode(
            Integer modelId,
            String[] nodeName
    ) throws CannotFindEntityException {
        return workflowService.deleteNode(modelId,nodeName);
    }


    @ApiOperation(value = "设置主办或者协办的岗位",notes = "目前协办只可以查看, 在资料节点, 只要有一个主办提交了资料, 就可以继续下一步")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型ID")
    })
    @PutMapping("/model/setPersons")
    public Result setQuarters(
            @Valid @RequestBody WorkflowQuartersEdit edit,
            BindingResult bindingResult
            ){
        return workflowService.setPersons(edit);
    }

    @ApiOperation(value = "编辑工作流")
    @PutMapping("/model")
    public Object edit(
            @Valid  WorkflowModelEdit edit,
            BindingResult bindingResult
    ) throws RestException {
        return workflowService.editWorkflowModel(edit.getId(),edit.getInfo(),edit.getOpen());
    }

//    @ApiOperation(value = "启用/停用工作流", notes = "一经启用, 禁止再编辑, 只能新增新版本")
//    @PutMapping("/model/open")
//    public Object changeOpen(
//            Integer modelId,
//            boolean open
//    ) throws CannotFindEntityException {
//        return workflowService.setOpen(modelId,open);
//    }


    @ApiOperation(value = "发起工作流", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "工作流模型ID",required = true
            )
    })
    @PostMapping("/newFlow")
    public Result newTask(
            Long modelId
    ) throws RestException {
       return workflowService.startNewInstance(Utils.getCurrentUser().getId(),modelId);
    }

    @ApiOperation(value = "向当前节点提交数据", notes = "不会进行下一步操作, 即使是节点的必填字段也可以为空, 重复提交视为草稿箱保存信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "instanceId", value = "工作流实例ID",required = true),
            @ApiImplicitParam(name = "data",value = "提交的map, 键和值都必须是字符串",required = true)
    })
    @PostMapping("/submitData")
    public Result submitData(
            Long instanceId,
            Map data
    ) throws RestException {
            workflowService.submitData(Utils.getCurrentUserId(),instanceId,data);
            return Result.ok();
    }

    @ApiOperation(value = "提交当前节点信息", notes = "会校验所有传入的信息, 例如节点的必填字段")
    @PostMapping("/goNextNode")
    public Result goNext(
            Long instanceId
    ) throws RestException {
        workflowService.goNext(Utils.getCurrentUserId(), instanceId);
        return Result.ok();
    }


    @ApiOperation(value = "创建一条任务", notes = "当处理人为自己的时候,自动开启相关流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "关联流程模型名称",required = true),
            @ApiImplicitParam(name = "dealUserId",value = "处理人")
    })
    @PostMapping("/task/create")
    public Result createInspectTask(String modelName, Long dealUserId){
        return workflowService.createInspectTask(Utils.getCurrentUserId(),modelName,dealUserId,false);
    }

    @ApiOperation(value = "接受一条任务")
    @PostMapping("/task/accept")
    public Result acceptInspectTask(Long taskId) {
        return workflowService.acceptInspectTask(Utils.getCurrentUserId(),taskId);
    }


    @ApiOperation(value = "当前应处理的节点")
    @GetMapping("/currentNode")
    public Result getCurrentNode(Long instanceId){
        Optional<WorkflowNodeInstance> optional = workflowService.getCurrentNodeInstance(Utils.getCurrentUserId(),instanceId);
        return optional.isPresent() ? Result.ok(optional.get()) : Result.error();
    }










}
