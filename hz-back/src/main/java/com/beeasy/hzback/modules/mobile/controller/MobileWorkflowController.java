package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.mobile.request.ApplyTaskRequest;
import com.beeasy.hzback.modules.mobile.request.SubmitDataRequest;
import com.beeasy.hzback.modules.mobile.request.WorkflowPositionAddRequest;
import com.beeasy.hzback.modules.system.dao.ISystemTextLogDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowInstanceDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelDao;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.StartChildInstanceRequest;
import com.beeasy.hzback.modules.system.response.FetchWorkflowInstanceResponse;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RequestMapping("/api/mobile/workflow")
@RestController
public class MobileWorkflowController {

    @Autowired
    WorkflowService workflowService;
    @Autowired
    UserService userService;

    @Autowired
    IWorkflowModelDao workflowModelDao;
    @Autowired
    IWorkflowInstanceDao instanceDao;
    @Autowired
    ISystemTextLogDao systemTextLogDao;

    private Result.DisallowEntry[] entries = new Result.DisallowEntry[]{
            new Result.DisallowEntry(WorkflowInstance.class,"dealUser"),
            new Result.DisallowEntry(WorkflowNode.class,"model"),
            new Result.DisallowEntry(WorkflowNodeInstance.class,"nodeModel","instance")
    };

    private Result.DisallowEntry[] myworkEntries = new Result.DisallowEntry[]{
        new Result.DisallowEntry(WorkflowInstance.class,"nodeList","simpleChildInstances")
    };

    @ApiOperation("列出可用的工作流模型")
    @GetMapping("/all")
    public String getAllWorkflows(){
        List<WorkflowModel> models = workflowModelDao.getAllWorkflows();
        User user = userService.findUser(Utils.getCurrentUserId()).orElse(null);
        if(null == user){
            return Result.ok().toMobile();
        }
        for (WorkflowModel model : models) {
            model.setPubOrPoint(workflowService.canPubOrPoint(model,user) && model.isManual());
        }
        return Result.ok(models).toJson(
                new Result.DisallowEntry(WorkflowModel.class, "nodeModels","permissions")
                );
    }


    @ApiOperation(value = "批量查找")
    @PostMapping("/findMany")
    public String findWorkflows(
            @RequestBody List<Long> ids
    ){
        return Result.ok(workflowModelDao.findAllByIdIn(ids)).toMobile();
    }

    @ApiOperation(value = "批量查找实例")
    @PostMapping("/instance/findMany")
    public String findWorkflowInstances(
            @RequestBody List<Long> ids
    ){
        //TODO: 需要验证授权, 只有自己的任务才可以查找
        return Result.ok(instanceDao.findAllByIdIn(ids)).toMobile();
    }


    @ApiOperation(value = "发布新任务")
    @PostMapping("/apply")
    public String applyTask(
            @Valid @RequestBody ApplyTaskRequest request,
            BindingResult bindingResult
            ){
        Result result = workflowService.startNewInstance(Utils.getCurrentUserId(),request);
        return result.toMobile(entries);
    }

    @ApiOperation(value = "开启子任务")
    @PostMapping("/child/apply")
    public String startChildTask(
            @Valid @RequestBody StartChildInstanceRequest request
            ){
        return workflowService.startChildInstance(Utils.getCurrentUserId(),request).toMobile();
    }

    @ApiOperation(value = "取消任务")
    @PostMapping("/cancel")
    public String cancelTask(
            @RequestParam Long instanceId
    ){
        boolean flag = workflowService.closeInstance(Utils.getCurrentUserId(),instanceId);
        if(!flag){
            return Result.error("取消任务失败, 没有权限或该任务已经变动").toMobile();
        }
        else{
            return Result.ok().toMobile();
        }
    }

    @ApiOperation(value = "撤回任务")
    @PostMapping("/recall")
    public String recallTask(
            @RequestParam Long instanceId
    ){
        boolean flag = workflowService .recallInstance(Utils.getCurrentUserId(),instanceId);
        if(!flag){
            return Result.error("撤回任务失败, 没有权限或该任务已经变动").toMobile();
        }
        else{
            return Result.ok().toMobile();
        }
    }

//    @ApiOperation(value = "移交任务")
//    @PostMapping("/transform")
//    public String transformTask(
//            @RequestParam Long instanceId,
//            @RequestParam Long dealerId
//    ){
//        boolean flag = workflowService.transformInstance(Utils.getCurrentUserId(),instanceId,dealerId);
//        if(!flag){
//            return Result.error("移交任务失败, 没有权限或该任务已变动").toMobile();
//        }
//        else{
//            return Result.ok().toMobile();
//        }
//    }


    @ApiOperation(value = "接受公共任务")
    @PostMapping("/common/accept/{instanceId}")
    public Result acceptCommonTask(
            @PathVariable Long instanceId
    ){
        return Result.ok(workflowService.acceptInstance(Utils.getCurrentUserId(),Collections.singleton(instanceId)));
    }


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
        return workflowService.uploadNodeFile(Utils.getCurrentUserId(),instanceId,nodeId,fileType,file,content,tag).toMobile();
    }

    @ApiOperation(value = "删除节点附件")
    @PostMapping("/file/delete")
    public String deleteNodeFile(
            @RequestParam Long nodeFileId
    ){
        return Result.finish(workflowService.deleteNodeFile(Utils.getCurrentUserId(),nodeFileId)).toMobile();
    }

    @ApiOperation(value = "设置节点标签")
    @GetMapping("/node/file/setTags/{id}/{tags}")
    public String setNodeFileTags(
            @PathVariable long id,
            @PathVariable String tags
    ){
        return Result.finish(workflowService.setNodeFileTags(Utils.getCurrentUserId(), id, tags)).toJson();
    }

    @ApiOperation(value = "节点添加定位信息")
    @PostMapping("/position/add")
    public String addPosition(
            @Valid @RequestBody WorkflowPositionAddRequest request,
            BindingResult bindingResult
    ){
        return workflowService.addPosition(Utils.getCurrentUserId(),request).toMobile();
    }

    @ApiOperation(value = "得到节点的定位属性")
    @GetMapping("/position/{nodeFileId}")
    public String getNodePosition(
            @PathVariable Long nodeFileId
    ){
        return Result.finish(workflowService.findNodeGPSPosition(nodeFileId)).toMobile();
    }

    @ApiOperation(value = "保存草稿")
    @PostMapping("/submitData")
    public String submitData(
            @Valid @RequestBody SubmitDataRequest request,
            BindingResult bindingResult
            ){
        return workflowService.submitData(Utils.getCurrentUserId(),request).toJson();
    }

    //可能会变动
    @ApiOperation(value = "提交下一步")
    @GetMapping("/goNext/{nodeId}")
    public String goNext(
//            @RequestParam Long instanceId,
//            @RequestParam Long nodeId
            @PathVariable Long nodeId
    ){
            return workflowService.goNext(Utils.getCurrentUserId(),0L,nodeId).toJson();
    }

    @ApiOperation(value = "我执行的任务")
    @GetMapping("/myOwnWorks")
    public String getMyOwnWorks(
            Long lessId
    ){
        PageRequest pageRequest = new PageRequest(0,10);
        if(lessId == null) lessId = 0L;
        if(lessId == 0) lessId = Long.MAX_VALUE;
        return Result.ok(instanceDao.findAllByDealUserIdAndIdLessThanOrderByAddTimeDesc(Utils.getCurrentUserId(),lessId,pageRequest)).toMobile(myworkEntries);
    }


    @ApiOperation(value = "我发布的任务")
    @GetMapping("/myPubWorks")
    public String getMyPubWorks(Long lessId){
        PageRequest pageRequest = new PageRequest(0,10);
        if(lessId == null){
            lessId = 0L;
        }
        if(lessId == 0){
            lessId = Long.MAX_VALUE;
        }
        return Result.ok(instanceDao.findAllByPubUserIdAndIdLessThanOrderByAddTimeDesc(Utils.getCurrentUserId(),lessId,pageRequest)).toJson(myworkEntries);
    }


    @ApiOperation(value = "需要我处理的任务")
    @RequestMapping(value = "/myNeedingWorks", method = RequestMethod.GET)
    public String getMyNeedToDealWorks(Long lessId){
        PageRequest pageRequest = new PageRequest(0,10);
        return Result.ok(workflowService.getUserUndealedWorks(Collections.singleton(Utils.getCurrentUserId()),lessId,pageRequest)).toMobile(myworkEntries);
    }

    @ApiOperation(value = "我处理过的任务")
    @GetMapping("/myDealedWorks")
    public String getMyDealedWorks(Long lessId){
        PageRequest pageRequest = new PageRequest(0,10);
        if(null == lessId){
            lessId = 0L;
        }
        if(lessId == 0){
            lessId = Long.MAX_VALUE;
        }
        return Result.ok(instanceDao.findDealedWorks(Collections.singletonList(Utils.getCurrentUserId()),lessId,pageRequest)).toMobile(myworkEntries);

    }

    @ApiOperation(value = "我观察的任务")
    @GetMapping("/myObserveredWorks")
    public String getMyObserveredWorks(Long lessId){
        PageRequest pageRequest = new PageRequest(0,10);
        if(null == lessId){
            lessId = 0L;
        }
        if(lessId == 0){
            lessId = Long.MAX_VALUE;
        }
        return Result.ok().toJson();
//        return Result.ok(instanceDao.findObserveredWorks(Collections.singletonList(Utils.getCurrentUserId()),lessId,pageRequest)).toMobile(myworkEntries);
    }

    @ApiOperation(value = "我可以执行的公共任务")
    @GetMapping("/getCommonWorks")
    public String getCommonWorks(Long lessId){
        PageRequest pageRequest = new PageRequest(0,10);
        if(null == lessId){
            lessId = 0L;
        }
        if(lessId == 0){
            lessId = Long.MAX_VALUE;
        }
        return Result.ok().toJson();
//        return Result.ok(instanceDao.findCommonWorks(Collections.singletonList(Utils.getCurrentUserId()),lessId,pageRequest)).toMobile(myworkEntries);
    }


    @ApiOperation(value = "任务处理日志")
    @GetMapping("/logs/{instanceId}")
    public String getWorkflowLogs(
            @PathVariable Long instanceId
    ){
        return Result.ok(systemTextLogDao.findLogs(SystemTextLog.Type.WORKFLOW,instanceId)).toMobile();
    }


    @ApiOperation(value = "查询一个任务的所有明细")
    @GetMapping("/instance/fetch/{id}")
    public String fetchInstance(@PathVariable Long id){
        return Result.finish(workflowService.fetchInstance(Utils.getCurrentUserId(),id)).toMobile();
    }

    @ApiOperation(value = "得到某个模型第一个节点的可执行人")
    @GetMapping("/persons/fetch/{id}")
    public String fetchModelFirstNoePersons(@PathVariable Long id){
        WorkflowModel model = workflowService.findModel(id).orElse(null);
        User user = userService.findUser(Utils.getCurrentUserId()).orElse(null);
        if(null == model || null == user){
            return Result.error().toMobile();
        }
        FetchWorkflowInstanceResponse response = new FetchWorkflowInstanceResponse();
        response.setTransformUsers(workflowService.getPubUids(id));
        response.setTransform(workflowService.canPoint(model.getId(),user.getId()));
        return Result.ok(response).toMobile(
                new Result.DisallowEntry(User.class,"quarters","departments","permissions","externalPermissions")
        );
    }
}
