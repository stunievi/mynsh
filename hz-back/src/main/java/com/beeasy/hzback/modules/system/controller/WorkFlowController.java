package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.mobile.request.ApplyTaskRequest;
import com.beeasy.hzback.modules.mobile.request.SubmitDataRequest;
import com.beeasy.hzback.modules.system.dao.IWorkflowInstanceDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelDao;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.log.SaveLog;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.objects.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "工作流API", value = "编辑模型需要管理员权限")
@Transactional
@RestController
@RequestMapping("/api/workflow")
public class WorkFlowController {

    @Autowired
    IWorkflowInstanceDao instanceDao;


    @Autowired
    IWorkflowModelDao workflowModelDao;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    UserService userService;
    private Result.Entry[] myworkEntries = new Result.Entry[]{
            new Result.Entry(WorkflowInstance.class,"nodeList","simpleChildInstances")
    };

    private Result.Entry[] entries = new Result.Entry[]{
            new Result.Entry(WorkflowInstance.class,"dealUser"),
            new Result.Entry(WorkflowNode.class,"model"),
            new Result.Entry(WorkflowNodeInstance.class,"nodeModel","instance")
    };

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
    @GetMapping("/model/delete")
    public Result delete(
            @RequestParam long modelId
    ){
        return Result.finish(workflowService.deleteWorkflowModel(modelId,false));
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


    @ApiOperation(value = "工作流实例列表", notes = "查询已经存在的工作模型列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "原型model名字")
    })
    @GetMapping("/instances")
    public Result<Page<WorkflowInstance>> instanceList(
            Pager pager,
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
            String modelName
    ){
        if(StringUtils.isEmpty(modelName)){
            return Result.ok(instanceDao.getAllIns(Utils.getCurrentUserId(), pageable));
        }else{
            return Result.ok(instanceDao.getInsByModelName(modelName, Utils.getCurrentUserId(), pageable));
        }
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


    @ApiOperation(value = "添加审核节点", notes = "传递节点ID的情况下, 视为修改")
    @PostMapping("/model/node/addCheck")
    public Result<WorkflowNode> createCheckNode(
            @Valid @RequestBody CheckNodeModel node,
            BindingResult bindingResult
    ){
        return Result.finish(workflowService.createCheckNode(node));
    }

    @Deprecated
    @ApiOperation(value = "删除节点")
    @DeleteMapping("/model/node")
    public Object deleteNode(
            @RequestParam Long modelId,
            @RequestParam String[] nodeName
    ) throws CannotFindEntityException {
        return workflowService.deleteNode(modelId,nodeName);
    }

    @ApiOperation(value = "删除节点")
    @PostMapping("/model/node/delete")
    public Result deleteNode(
            @RequestParam Long modelId,
            @RequestParam Long nodeId
    ) {
        return Result.finish(workflowService.deleteNode(modelId,nodeId));
    }


//    @ApiOperation(value = "设置主办或者协办的岗位",notes = "目前协办只可以查看, 在资料节点, 只要有一个主办提交了资料, 就可以继续下一步")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "modelId", value = "模型ID")
//    })
//    @PutMapping("/model/setPersons")
//    public Result setQuarters(
//            @Valid @RequestBody WorkflowQuartersEdit edit,
//            BindingResult bindingResult
//            ){
//        return workflowService.setPersons(edit);
//    }


    @Deprecated
    @ApiOperation(value = "编辑工作流")
    @PutMapping("/model")
    public Object edit(
            @Valid  WorkflowModelEdit edit,
            BindingResult bindingResult
    ) throws RestException {
        return workflowService.editWorkflowModel(edit.getId(),edit.getInfo(),edit.isOpen());
    }

    @ApiOperation(value = "编辑工作流")
    @PutMapping("/model/edit")
    public Result edit2(
            @Valid @RequestBody WorkflowModelEdit edit
    ){
        return Result.finish(workflowService.editWorkflowModel(edit));
    }

    @ApiOperation(value = "授权设置")
    @RequestMapping(value = "/model/permission/set", method = RequestMethod.POST)
    public Result setPermission(
            @Valid @RequestBody GlobalPermissionEditRequest[] requests
    ){
        if(0 == requests.length){
            return Result.ok();
        }
        if(Arrays.stream(requests).map(item -> item.getObjectId()).distinct().count() != 1){
            return Result.error("参数错误");
        }
        //清空授权
        userService.deleteGlobalPermissionByObjectId(requests[0].getObjectId());
        GlobalPermission.Type[] types = {
                GlobalPermission.Type.WORKFLOW_PUB,
                GlobalPermission.Type.WORKFLOW_OBSERVER,
                GlobalPermission.Type.WORKFLOW_MAIN_QUARTER,
                GlobalPermission.Type.WORKFLOW_SUPPORT_QUARTER
        };
        List<GlobalPermission.Type> list = Arrays.asList(types);
        for (GlobalPermissionEditRequest request : requests) {
            if(!list.contains(request.getType())){
                continue;
            }
        userService.addGlobalPermission(request.getType(),request.getObjectId(), request.getUserType(), request.getLinkIds(),null);
        }
        return Result.ok();
    }

    @ApiOperation(value = "授权查询")
    @RequestMapping(value = "/model/permission/get", method = RequestMethod.GET)
    public Result getPermissions(
            @RequestParam String types,
            @RequestParam long objectId
    ){
        GlobalPermission.Type[] limitTypes = {
                GlobalPermission.Type.WORKFLOW_PUB,
                GlobalPermission.Type.WORKFLOW_OBSERVER,
                GlobalPermission.Type.WORKFLOW_MAIN_QUARTER,
                GlobalPermission.Type.WORKFLOW_SUPPORT_QUARTER
        };
        //如果不包括这些授权, 那么抛出错误
        List<GlobalPermission.Type> limitList = Arrays.asList(limitTypes);
        List<GlobalPermission.Type> list = Arrays.stream(types.trim().split(","))
                .map(str -> GlobalPermission.Type.valueOf(str))
                .filter(item -> null != item)
                .filter(item -> limitList.contains(item))
                .collect(Collectors.toList());
        return Result.ok(userService.getGlobalPermissions(list,objectId));
    }




//    @ApiOperation(value = "启用/停用工作流", notes = "一经启用, 禁止再编辑, 只能新增新版本")
//    @PutMapping("/model/open")
//    public Object changeOpen(
//            Integer modelId,
//            boolean open
//    ) throws CannotFindEntityException {
//        return workflowService.setOpen(modelId,open);
//    }


//    @ApiOperation(value = "发起工作流", notes = "")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "modelId", value = "工作流模型ID",required = true
//            )
//    })
//    @PostMapping("/newFlow")
//    public Result newTask(
//            Long modelId
//    ) throws RestException {
//       return workflowService.startNewInstance(Utils.getCurrentUser().getId(),modelId);
//    }

//    @Deprecated
//    @ApiOperation(value = "向当前节点提交数据", notes = "不会进行下一步操作, 即使是节点的必填字段也可以为空, 重复提交视为草稿箱保存信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "instanceId", value = "工作流实例ID",required = true),
//            @ApiImplicitParam(name = "data",value = "提交的map, 键和值都必须是字符串",required = true)
//    })
//    @PostMapping("/submitData")
//    public Result submitData(
//            Long instanceId,
//            Map data
//    ) throws RestException {
//            workflowService.submitData(Utils.getCurrentUserId(),instanceId,data);
//            return Result.ok();
//    }

//    @Deprecated
//    @ApiOperation(value = "提交当前节点信息", notes = "会校验所有传入的信息, 例如节点的必填字段")
//    @PostMapping("/goNextNode")
//    public Result goNext(
//            Long instanceId
//    ) throws RestException {
//        workflowService.goNext(Utils.getCurrentUserId(), instanceId);
//        return Result.ok();
//    }


//    @Deprecated
//    @ApiOperation(value = "创建一条任务"w, notes = "当处理人为自己的时候,自动开启相关流程")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "modelName", value = "关联流程模型名称",required = true),
//            @ApiImplicitParam(name = "dealUserId",value = "处理人")
//    })
//    @PostMapping("/task/create")
//    public Result createInspectTask(String modelName, Long dealUserId){
//        return workflowService.createInspectTask(Utils.getCurrentUserId(),modelName,dealUserId,false);
//    }
//
//    @Deprecated
//    @ApiOperation(value = "接受一条任务")
//    @PostMapping("/task/accept")
//    public Result acceptInspectTask(Long taskId) {
//        return workflowService.acceptInspectTask(Utils.getCurrentUserId(),taskId);
//    }


    @Deprecated
    @ApiOperation(value = "当前应处理的节点")
    @GetMapping("/currentNode")
    public Result getCurrentNode(Long instanceId){
        Optional<WorkflowNodeInstance> optional = workflowService.getCurrentNodeInstance(Utils.getCurrentUserId(),instanceId);
        return optional.isPresent() ? Result.ok(optional.get()) : Result.error();
    }

    @SaveLog(value = "获取工作流模型列表")
    @GetMapping("/all")
    public Result getAllWorkflows(){
        return Result.ok(workflowModelDao.getAllWorkflows());
    }

//    @ApiOperation(value = "我发布的任务")
//    @GetMapping("/myPubWorks")
//    public Result getMyPubWorks(
//            Pager pager,
//            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(instanceDao.findAllByPubUserIdOrderByAddTimeDesc(Utils.getCurrentUserId(), pageable));
//    }


//    @ApiOperation(value = "我执行的任务")
//    @GetMapping("/myOwnWorks")
//    public Result getMyOwnWorks(
//            Pager pager,
//            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
//    ){
//        return Result.ok(workflowService.getUserDealedWorks(Collections.singleton(Utils.getCurrentUserId()),null, pageable));
//    }

    @ApiOperation(value = "待处理任务")
    @GetMapping("/myNeedingWorks")
    public Result getMyNeedToDealWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(workflowService.getUserUndealedWorks(Collections.singleton(Utils.getCurrentUserId()),null, pageable));
    }

    @ApiOperation(value = "已处理任务")
    @GetMapping("/myDealedWorks")
    public Result getMyDealedWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(workflowService.getUserDealedWorks(Collections.singleton(Utils.getCurrentUserId()),null, pageable));
    }

    @ApiOperation(value = "我观察的任务")
    @GetMapping("/myObserveredWorks")
    public Result getMyObserveredWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){

        return Result.ok(workflowService.getUserObservedWorks(Collections.singleton(Utils.getCurrentUserId()),null, pageable));
    }

    @ApiOperation(value = "部门待处理任务")
    @GetMapping("/deptUndealedWorks")
    public Result getDepartmentUndealedWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){

        return Result.ok(workflowService.getDepartmentUndealedWorks(Collections.singleton(Utils.getCurrentUserId()),null, pageable));
    }

    @ApiOperation(value = "部门已处理任务")
    @GetMapping("/deptDealedWorks")
    public Result getDepartmentDealedWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){

        return Result.ok(workflowService.getDepartmentDealedWorks(Collections.singleton(Utils.getCurrentUserId()),null, pageable));
    }

    @ApiOperation(value = "查询一个任务的所有明细")
    @GetMapping("/instance/fetch/{id}")
    public String fetchInstance(@PathVariable Long id){
        return Result.ok(workflowService.fetchWorkflowInstance(Utils.getCurrentUserId(),id)).toJson(
                new Result.Entry(User.class,"departments","quarters"),
                new Result.Entry(WorkflowNode.class,"persons"));
    }

    @ApiOperation(value = "取消任务")
    @PostMapping("/cancel")
    public String cancelTask(
            @RequestParam Long instanceId
    ){
        boolean flag = workflowService.closeInstance(Utils.getCurrentUserId(),instanceId);
        if(!flag){
            return Result.error("取消任务失败, 没有权限或该任务已经变动").toJson();
        }
        else{
            return Result.ok().toJson();
        }
    }

    @ApiOperation(value = "撤回任务")
    @PostMapping("/recall")
    public String recallTask(
            @RequestParam Long instanceId
    ){
        boolean flag = workflowService .recallInstance(Utils.getCurrentUserId(),instanceId);
        if(!flag){
            return Result.error("撤回任务失败, 没有权限或该任务已经变动").toJson();
        }
        else{
            return Result.ok().toJson();
        }
    }

    @ApiOperation(value = "发布新任务")
    @PostMapping("/apply")
    public String applyTask(
            @Valid @RequestBody ApplyTaskRequest request
    ){
        Result result = workflowService.startNewInstance(Utils.getCurrentUserId(),request);
        return result.toJson(entries);
    }

    @ApiOperation(value = "保存草稿")
    @PostMapping("/submitData")
    public String submitData(
            @Valid @RequestBody SubmitDataRequest request,
            BindingResult bindingResult
    ){
        return workflowService.submitData(Utils.getCurrentUserId(),request).toJson();
    }

    @ApiOperation(value = "提交下一步")
    @PostMapping("/goNext")
    public String goNext(
            @RequestParam Long instanceId,
            @RequestParam Long nodeId
    ){
        return workflowService.goNext(Utils.getCurrentUserId(),instanceId,nodeId).toJson();
    }

    @ApiOperation(value = "移交任务")
    @PostMapping("/transform")
    public String transformTask(
            @RequestParam Long instanceId,
            @RequestParam Long dealerId
    ){
        boolean flag = workflowService.transformInstance(Utils.getCurrentUserId(),instanceId,dealerId);
        if(!flag){
            return Result.error("移交任务失败, 没有权限或该任务已变动").toJson();
        }
        else{
            return Result.ok().toJson();
        }
    }

    @ApiOperation(value = "上传节点附件")
    @PostMapping("/file/upload")
    public String uploadNodeFile(
            @RequestParam Long instanceId,
            @RequestParam Long nodeId,
            MultipartFile file,
            @RequestParam WorkflowNodeFile.Type fileType,
            String content
    ){
        return workflowService.uploadNodeFile(Utils.getCurrentUserId(),instanceId,nodeId,fileType,file,content).toJson();
    }

    @ApiOperation(value = "删除节点附件")
    @PostMapping("/file/delete")
    public String deleteNodeFile(
            @RequestParam Long nodeFileId
    ){
        return Result.finish(workflowService.deleteNodeFile(Utils.getCurrentUserId(),nodeFileId)).toJson();
    }

    @ApiOperation(value = "开启子任务")
    @PostMapping("/child/apply")
    public String startChildTask(
            @Valid @RequestBody StartChildInstanceRequest request
    ){
        return workflowService.startChildInstance(Utils.getCurrentUserId(),request).toJson();
    }

    @ApiOperation(value = "设置观察岗")
    @PostMapping("/setObserverQuarters")
    public Result setObserverQuarters(
            @RequestParam Long workFlowId,
            @RequestParam List<Long> quarters
    ){
        return Result.ok(userService.addGlobalPermission(GlobalPermission.Type.WORKFLOW_OBSERVER,workFlowId,GlobalPermission.UserType.QUARTER,quarters,null));
//        GlobalPermission gp = new GlobalPermission();
//        gp.setType(GlobalPermission.Type.WORKFLOW_OBSERVER);
//        gp.setObjectId(workFlowId);
//        gp.setUserType(GlobalPermission.UserType.QUARTER);
//        gp.setLinkId();
//        WorkflowExtPermissionEdit edit = new WorkflowExtPermissionEdit();
//        edit.setType(WorkflowExtPermission.Type.OBSERVER);
//        edit.setModelId(workFlowId);
//        edit.setQids(quarters);
//        workflowService.setExtPermissions(edit);
//        return Result.ok();
    }

}
