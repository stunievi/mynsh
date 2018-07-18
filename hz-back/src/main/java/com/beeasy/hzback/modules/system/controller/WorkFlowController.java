package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.mobile.request.ApplyTaskRequest;
import com.beeasy.hzback.modules.mobile.request.SubmitDataRequest;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.log.NotSaveLog;
import com.beeasy.hzback.modules.system.log.SaveLog;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    IWorkflowNodeInstanceDao nodeInstanceDao;
    @Autowired
    IWorkflowNodeAttributeDao attributeDao;
    @Autowired
    IWorkflowNodeDao nodeDao;


    @Autowired
    IWorkflowModelDao workflowModelDao;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    UserService userService;
    private Result.DisallowEntry[] myworkEntries = new Result.DisallowEntry[]{
            new Result.DisallowEntry(WorkflowInstance.class,"nodeList","simpleChildInstances")
    };

    private Result.DisallowEntry[] entries = new Result.DisallowEntry[]{
            new Result.DisallowEntry(WorkflowInstance.class,"dealUser"),
            new Result.DisallowEntry(WorkflowNode.class,"model"),
            new Result.DisallowEntry(WorkflowNodeInstance.class,"nodeModel","instance")
    };

    @ApiOperation(value = "创建工作流", notes = "根据已有的模型创建一个新的工作流实体,可选模型: 资料收集")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "要创建的标准模型", required = true)
    })
    @PostMapping("/model")
    public Object createNewWorkFlow(
            String modelName,
            @Valid WorkflowModelAdd add
    ){
        return workflowService.createWorkflow(modelName,add).toJson(
                new Result.DisallowEntry(WorkflowModel.class, "nodeModels")
        );
    }

    @ApiOperation(value = "设置工作流模型关联字段")
    @RequestMapping(value = "/model/setFields",method = RequestMethod.POST)
    public Result setWorkflowModelFields(
            @RequestParam long modelId,
            @Valid @RequestBody List<WorkflowService.ModelFieldRequest> requests
    ){
        return workflowService.setModelFields(modelId,requests);
    }

    @ApiOperation(value = "得到工作流模型的关联字段")
    @RequestMapping(value = "/model/getFields", method = RequestMethod.GET)
    public Result getWorkflowModelFields(
            @RequestParam long modelId
    ){
        return Result.ok(workflowService.getModelFields(modelId));
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
            WorkflowService.ModelSearchRequest request
    ){

        return Result.ok(workflowService.getModelList(request, pageable));
//        if(StringUtils.isEmpty(modelName)){
//            return Result.ok(workflowModelDao.findAll(pageable));
//        }
//        return Result.ok(workflowModelDao.findAllByName(modelName,pageable));
    }


    @ApiOperation(value = "工作流实例列表", notes = "查询已经存在的工作模型列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelName", value = "原型model名字")
    })
    @RequestMapping(value = "/instances", method = RequestMethod.GET)
    public String instanceList(
            @RequestParam Map<String,String> object,
            Pager pager,
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(workflowService.getInstanceList(Utils.getCurrentUserId(), object,pageable)).toJson(
                new Result.DisallowEntry(WorkflowInstance.class,"nodeList")
        );
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


    @ApiOperation(value = "删除节点")
    @PostMapping("/model/node/delete")
    public Result deleteNode(
            @RequestParam Long modelId,
            @RequestParam Long nodeId
    ) {
        return Result.finish(workflowService.deleteNode(modelId,nodeId));
    }


    @ApiOperation(value = "编辑工作流")
    @PutMapping("/model/edit")
    public Result edit2(
            @Valid @RequestBody WorkflowModelEdit edit
    ){
        return (workflowService.editWorkflowModel(edit));
    }

    @SaveLog(value = "设置工作流权限")
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
        if(Arrays.stream(requests).map(item -> item.getType()).distinct().count() != 1){
            return Result.error("参数错误");
        }
        GlobalPermission.Type[] types = {
                GlobalPermission.Type.WORKFLOW_PUB,
                GlobalPermission.Type.WORKFLOW_OBSERVER,
                GlobalPermission.Type.WORKFLOW_MAIN_QUARTER,
                GlobalPermission.Type.WORKFLOW_SUPPORT_QUARTER
        };
        List<GlobalPermission.Type> list = Arrays.asList(types);
        if(!list.contains(requests[0].getType())){
            return Result.error("参数错误");
        }
        //清空授权
        userService.deleteGlobalPermissionByTypeAndObjectId(requests[0].getType(),requests[0].getObjectId());
        for (GlobalPermissionEditRequest request : requests) {
            userService.addGlobalPermission(request.getType(),request.getObjectId(), request.getUserType(), request.getLinkIds(),null);
        }
        //更新工作流所属部门
        if(requests[0].getType().equals(GlobalPermission.Type.WORKFLOW_PUB)){
            workflowService.updateWorkflowModelDeps(requests[0].getObjectId());
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


//    @Deprecated
//    @ApiOperation(value = "当前应处理的节点")
//    @GetMapping("/currentNode")
//    public Result getCurrentNode(Long instanceId){
//        Optional<WorkflowNodeInstance> optional = workflowService.getCurrentNodeInstance(Utils.getCurrentUserId(),instanceId);
//        return optional.isPresent() ? Result.ok(optional.get()) : Result.error();
//    }

    @SaveLog(value = "获取工作流模型列表")
    @GetMapping("/all")
    public Result getAllWorkflows(){
        return Result.ok(workflowModelDao.getAllWorkflows());
    }

    @ApiOperation(value = "得到用户相关联的模型ID")
    @RequestMapping(value = "/model/validList", method = RequestMethod.GET)
    public String getUserWorkflows(){
        return Result.ok(workflowService.getUserModelList(Utils.getCurrentUserId())).toJson(
        );
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
        return Result.ok(
                formatNormalWorks(workflowService.getUserUndealedWorks(Utils.getCurrentUserId(),null, pageable))
        );
    }

    @ApiOperation(value = "已处理任务")
    @RequestMapping(value = "/myDealedWorks", method = RequestMethod.GET)
    public Result getMyDealedWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(
                formatNormalWorks(workflowService.getUserDealedWorks(Collections.singleton(Utils.getCurrentUserId()),null, pageable))
        );
    }

    @ApiOperation(value = "我观察的任务")
    @GetMapping("/myObserveredWorks")
    public Result getMyObserveredWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){

        return Result.ok(
                formatDepartmentWorks(workflowService.getUserObservedWorks(Collections.singleton(Utils.getCurrentUserId()),null, pageable))
        );
    }

    @ApiOperation(value = "部门待处理任务")
    @RequestMapping(value = "/deptUndealedWorks", method = RequestMethod.GET)
    public Result getDepartmentUndealedWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){

        return Result.ok(
                formatDepartmentWorks(workflowService.getDepartmentUndealedWorks((Utils.getCurrentUserId()),null, pageable))
        );
    }

    @ApiOperation(value = "部门已处理任务")
    @RequestMapping(value = "/deptDealedWorks", method = RequestMethod.GET)
    public Result getDepartmentDealedWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){

        return Result.ok(
                formatDepartmentWorks(workflowService.getDepartmentDealedWorks((Utils.getCurrentUserId()),null, pageable))
        );
    }

    @ApiOperation(value = "查询一个任务的所有明细")
    @GetMapping("/instance/fetch/{id}")
    public String fetchInstance(@PathVariable Long id){
        return Result.ok(workflowService.fetchInstance(Utils.getCurrentUserId(),id)).toJson(
                new Result.DisallowEntry(User.class,"departments","quarters"),
                new Result.DisallowEntry(WorkflowNode.class,"persons"));
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

    @SaveLog(value = "发布新任务")
    @ApiOperation(value = "发布新任务")
    @PostMapping("/apply")
    public String applyTask(
            @Valid @RequestBody ApplyTaskRequest request
    ){
        Result result = workflowService.startNewInstance(Utils.getCurrentUserId(),request);
        return result.toJson(entries);
    }


    @NotSaveLog
    @ApiOperation(value = "给脚本调用的自动创建任务接口, 客户端禁止调用")
    @PostMapping("/task/autoStart")
    public Result startNewTask(
            @RequestParam long uid,
            @Valid @RequestBody ApplyTaskRequest request
    ){
        return workflowService.startNewInstance(uid, request);
    }

    @ApiOperation(value = "保存草稿")
    @PostMapping("/submitData")
    public String submitData(
            @Valid @RequestBody SubmitDataRequest request
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

//    @Deprecated
//    @ApiOperation(value = "移交任务")
//    @PostMapping("/transform")
//    public String transformTask(
//            @RequestParam Long instanceId,
//            @RequestParam Long dealerId
//    ){
//        boolean flag = workflowService.transformInstance(Utils.getCurrentUserId(),instanceId,dealerId);
//        if(!flag){
//            return Result.error("移交任务失败, 没有权限或该任务已变动").toJson();
//        }
//        else{
//            return Result.ok().toJson();
//        }
//    }

    @NotSaveLog
    @ApiOperation(value = "上传节点附件")
    @PostMapping("/file/upload")
    public String uploadNodeFile(
            @RequestParam Long instanceId,
            @RequestParam Long nodeId,
            MultipartFile file,
            @RequestParam WorkflowNodeFile.Type fileType,
            String content,
            String tag
    ){
        return workflowService.uploadNodeFile(Utils.getCurrentUserId(),instanceId,nodeId,fileType,file,content, tag).toJson();
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

    @ApiOperation(value = "得到一个模型的任务可以指派的对象")
    @RequestMapping(value = "/model/dealers", method = RequestMethod.GET)
    public Result getModelDealers(
            @RequestParam long id
    ){
        return Result.ok(workflowService.getPubUsers(id));
    }


    @ApiOperation(value = "得到用户可接受的任务")
    @RequestMapping(value = "/canAccpetWorks", method = RequestMethod.GET)
    public Result getUserCanAcceptWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(workflowService.getUserCanAcceptWorks(Collections.singleton(Utils.getCurrentUserId()),null,pageable));
    }

    @ApiOperation(value = "得到用户可以接受的公共任务列表")
    @RequestMapping(value = "/common/getList", method = RequestMethod.GET)
    public Result getUserCanAcceptCommonWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(
                formatDepartmentWorks(workflowService.getUserCanAcceptCommonWorks(Collections.singleton(Utils.getCurrentUserId()),null,pageable)));
    }

    @ApiOperation(value = "拒贷列表")
    @RequestMapping(value = "/getRejectCollectList", method = RequestMethod.GET)
    public Result getRejectCollectList(
        Pager pager,
        @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable,
        WorkflowService.RejectCollectRequest request
    ){
        return Result.ok(workflowService.getRejectCollectList(Utils.getCurrentUserId(), request, pageable));
    }

    @ApiOperation(value = "接受公共任务")
    @RequestMapping(value = "/common/accept", method = RequestMethod.GET)
    public Result acceptCommonInstance(
            @RequestParam String instanceId
    ){
        return Result.ok(workflowService.acceptInstance(Utils.getCurrentUserId(),Utils.convertIdsToList(instanceId)));
    }

    @ApiOperation(value = "接受指派/移交")
    @RequestMapping(value = "/acceptWorks", method = RequestMethod.GET)
    public Result acceptTasks(
            @RequestParam String id
    ){
        return (workflowService.acceptTask(Utils.getCurrentUserId(), Utils.convertIds(id)));
    }

    @ApiOperation(value = "拒绝指派/移交")
    @RequestMapping(value = "/rejectWorks", method = RequestMethod.POST)
    public Result rejectTasks(
            @Valid @RequestBody RejectTaskRequest request
    ){
        return workflowService.rejectTask(Utils.getCurrentUserId(), request.getInfo(), request.getIds());
    }


    @ApiOperation(value = "指派列表")
    @RequestMapping(value = "/getUnreceivedWorks", method = RequestMethod.GET)
    public String getUnreceivedWorks(
            UnreceivedWorksSearchRequest request,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(workflowService.getUnreceivedWorks(Utils.getCurrentUserId(), request, null, pageable)).toJson(
                new Result.DisallowEntry(WorkflowInstance.class, "nodeList")
        );
    }

    @ApiOperation(value = "预任务列表")
    @RequestMapping(value = "/getPlanWorks", method = RequestMethod.GET)
    public String getPlanWorks(
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(workflowService.getPlanWorks(Collections.singleton(Utils.getCurrentUserId()), null, pageable)).toJson(
                new Result.DisallowEntry(WorkflowInstance.class, "nodeList")
        );
    }

    @ApiOperation(value = "任务重指派/移交")
    @RequestMapping(value = "/pointTask", method = RequestMethod.GET)
    public Result pointTask(
            @RequestParam String id,
            @RequestParam long toUid
    ){
        return (workflowService.pointTask(Utils.getCurrentUserId(), Utils.convertIdsToList(id), toUid));
    }

    @ApiOperation(value = "设置节点文件标签")
    @GetMapping("/node/file/setTags")
    public String setNodeFileTags(
            @RequestParam long id,
            @RequestParam String tags
    ){
        return Result.finish(workflowService.setNodeFileTags(Utils.getCurrentUserId(), id, tags)).toJson();
    }

    @ApiOperation(value = "设置节点文件名")
    @RequestMapping(value = "/node/file/rename", method = RequestMethod.POST)
    public Result setNodeFileName(
            @RequestParam long id,
            @RequestParam String name
    ){
        return Result.finish(workflowService.setNodeFileName(Utils.getCurrentUserId(), id, name));
    }

    @ApiOperation(value = "申请下载文件的令牌")
    @RequestMapping(value = "/node/file/downloadApply", method = RequestMethod.GET)
    public Result applyDownload(
            @RequestParam long id
    ){
        String token = workflowService.applyDownload(Utils.getCurrentUserId(), id);
        if(StringUtils.isEmpty(token)){
            return Result.error();
        }
        return Result.ok(token);
    }


    @ApiOperation(value = "查询台账相关的绑定")
    @RequestMapping(value = "/binds", method = RequestMethod.GET)
    public Result searchBindedWorks(
            @RequestParam String billNo,
            @RequestParam String modelName,
            Pager pager,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(instanceDao.getBindedWorks(billNo, modelName, pageable));
    }


    @ApiOperation(value = "查找指定模型的开始节点")
    @RequestMapping(value = "/node/getStartNode", method = RequestMethod.GET)
    public Result searchFirstNode(
            @RequestParam String modelId
    ){
        return Result.ok(nodeDao.findAllByModelIdInAndStartIsTrue(Utils.convertIdsToList(modelId))
                .stream()
                .collect(Collectors.toMap(item -> item.getId() + "", item -> item))
        );
    }

//    public Result searchInfoLink(){
//
//    }
//
//    public Result addInfoLink(){
//
//    }
//
//    public Result deleteInfoLink(){
//
//    }

    @Data
    public static class RejectTaskRequest{
        @NotNull
        Long[] ids;

        @NotEmpty(message = "拒绝说明不能为空")
        String info;
    }


    public Page formatNormalWorks(Page page){
        return page.map(o -> {
                    WorkflowInstance ins = (WorkflowInstance) o;
                    Map<String,String> map = ins.getAttributeMap();
                    return Utils.newMap(
                            "id", ins.getId(),
                            "CUS_NAME",map.get("CUS_NAME"),
                            "LOAN_ACCOUNT",map.get("LOAN_ACCOUNT"),
                            "modelName", ins.getModelName(),
                            "state", ins.getState(),
                            "addTime", ins.getAddTime(),
                            "finishedDate", ins.getFinishedDate()
                    );
        });
    }

    public Page formatDepartmentWorks(Page page){
        return page.map(o -> {
                    WorkflowInstance ins = (WorkflowInstance) o;
                    Map<String,String> map = ins.getAttributeMap();
                    return Utils.newMap(
                            "id", ins.getId(),
                            "CUS_NAME",map.get("CUS_NAME"),
                            "LOAN_ACCOUNT",map.get("LOAN_ACCOUNT"),
                            "modelName", ins.getModelName(),
                            "state", ins.getState(),
                            "addTime", ins.getAddTime(),
                            "finishedDate", ins.getFinishedDate(),
                            "dealer",ins.getDealUserId(),
                            "dealerName", ins.getDealUserName()
                    );
                });
    }

}
