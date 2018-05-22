package com.beeasy.hzback.modules.mobile.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.mobile.request.ApplyTaskRequest;
import com.beeasy.hzback.modules.mobile.request.SubmitDataRequest;
import com.beeasy.hzback.modules.system.dao.ISystemTextLogDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowInstanceDao;
import com.beeasy.hzback.modules.system.dao.IWorkflowModelDao;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/mobile/workflow")
@RestController
public class MobileWorkflowController {

    @Autowired
    WorkflowService workflowService;

    @Autowired
    IWorkflowModelDao workflowModelDao;
    @Autowired
    IWorkflowInstanceDao instanceDao;
    @Autowired
    ISystemTextLogDao systemTextLogDao;

    private Result.Entry[] entries = new Result.Entry[]{
            new Result.Entry(WorkflowInstance.class,"dealUser"),
            new Result.Entry(WorkflowNode.class,"model"),
            new Result.Entry(WorkflowNodeInstance.class,"nodeModel","instance")
    };

    @GetMapping("/availableModelNames")
    public Result getAvaliableModelList(){
         return Result.ok(workflowService.getAvaliableModelNames());
    }

    @GetMapping("/all")
    public String getAllWorkflows(){
        return Result.okJson(workflowModelDao.getAllWorkflows(),
                entries
                );
    }


    @ApiOperation(value = "批量查找")
    @PostMapping("/findMany")
    public Result findWorkflows(
            @RequestBody List<Long> ids
    ){
        return Result.ok(workflowModelDao.findAllByIdIn(ids));
    }

    @ApiOperation(value = "批量查找实例")
    @PostMapping("/instance/findMany")
    public Result findWorkflowInstances(
            @RequestBody List<Long> ids
    ){
        //TODO: 需要验证授权, 只有自己的任务才可以查找
        return Result.ok(instanceDao.findAllByIdIn(ids));
    }


    @ApiOperation(value = "发布新任务")
    @PostMapping("/apply")
    public String applyTask(
            @Valid @RequestBody ApplyTaskRequest request
            ){
        Result result = workflowService.startNewInstance(Utils.getCurrentUserId(),request);
        return result.toJson(entries);
    }

    @ApiOperation(value = "取消任务")
    @PostMapping("/cancel")
    public Result cancelTask(
            @RequestParam Long instanceId
    ){
        boolean flag = workflowService.closeInstance(Utils.getCurrentUserId(),instanceId);
        if(!flag){
            return Result.error("取消任务失败, 没有权限或该任务已经变动");
        }
        else{
            return Result.ok();
        }
    }

    @ApiOperation(value = "撤回任务")
    @PostMapping("/recall")
    public Result recallTask(
            @RequestParam Long instanceId
    ){
        boolean flag = workflowService .recallInstance(Utils.getCurrentUserId(),instanceId);
        if(!flag){
            return Result.error("撤回任务失败, 没有权限或该任务已经变动");
        }
        else{
            return Result.ok();
        }
    }

    @ApiOperation(value = "移交任务")
    @PostMapping("/transform")
    public Result transformTask(
            @RequestParam Long instanceId,
            @RequestParam Long dealerId
    ){
        boolean flag = workflowService.transformInstance(Utils.getCurrentUserId(),instanceId,dealerId);
        if(!flag){
            return Result.error("移交任务失败, 没有权限或该任务已变动");
        }
        else{
            return Result.ok();
        }
    }

    @ApiOperation(value = "保存草稿")
    @PostMapping("/submitData")
    public Result submitData(
            @Valid @RequestBody SubmitDataRequest request,
            BindingResult bindingResult
            ){
        return workflowService.submitData(Utils.getCurrentUserId(),request);
    }

    //可能会变动
    @ApiOperation(value = "提交下一步")
    @PostMapping("/goNext")
    public Result goNext(
            @RequestParam Long instanceId,
            @RequestParam Long nodeId
    ){
            return workflowService.goNext(Utils.getCurrentUserId(),instanceId,nodeId);
    }

    @ApiOperation(value = "我执行的任务")
    @GetMapping("/myOwnWorks")
    public Result getMyOwnWorks(
            Long lessId
    ){
        PageRequest pageRequest = new PageRequest(0,20);
        if(lessId == null) lessId = 0L;
        if(lessId == 0) lessId = Long.MAX_VALUE;
        return Result.ok(instanceDao.findAllByDealUser_IdAndIdLessThanOrderByAddTimeDesc(Utils.getCurrentUserId(),lessId,pageRequest));
    }


    @ApiOperation(value = "我发布的任务")
    @GetMapping("/myPubWorks")
    public Result getMyPubWorks(Long lessId){
        PageRequest pageRequest = new PageRequest(0,20);
        if(lessId == null){
            lessId = 0L;
        }
        if(lessId == 0){
            lessId = Long.MAX_VALUE;
        }
        return Result.ok(instanceDao.findAllByPubUser_IdAndIdLessThanOrderByAddTimeDesc(Utils.getCurrentUserId(),lessId,pageRequest));
    }


    @ApiOperation(value = "需要我处理的任务")
    @GetMapping("/myNeedingWorks")
    public Result getMyNeedToDealWorks(Long lessId){
        PageRequest pageRequest = new PageRequest(0,20);
        if(lessId == null){
            lessId = 0L;
        }
        if(lessId == 0){
            lessId = Long.MAX_VALUE;
        }
        List<Long> qids = Utils.getCurrentUser().getQuarters().stream().map(q -> q.getId()).collect(Collectors.toList());
        return Result.ok(instanceDao.findNeedToDealWorks(Collections.singletonList(Utils.getCurrentUserId()),qids,lessId,pageRequest));
    }



    @ApiOperation(value = "任务处理日志")
    @GetMapping("/logs")
    public Result getWorkflowLogs(
            @RequestParam Long instanceId
    ){
        return Result.ok(systemTextLogDao.findAllByTypeAndLinkIdOrderByAddTimeDesc(SystemTextLog.Type.WORKFLOW,instanceId));
    }



    @ApiOperation(value = "查询一个任务的所有明细")
    @GetMapping("/instance/fetch/{id}")
    public String fetchInstance(@PathVariable Long id){
        return Result.finish(workflowService.fetchWorkflowInstance(Utils.getCurrentUserId(),id)).toJson(
                new Result.Entry(User.class,"departments","quarters"),
                new Result.Entry(WorkflowNode.class,"persons"));
    }
}
