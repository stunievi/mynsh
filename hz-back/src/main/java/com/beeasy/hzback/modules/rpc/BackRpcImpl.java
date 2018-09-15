package com.beeasy.hzback.modules.rpc;

import com.alibaba.dubbo.config.annotation.Service;
import com.beeasy.hzback.modules.system.service.WorkflowService;
import com.beeasy.rpc.BackRpc;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//@RestController
//@RequestMapping("/rpc/back")
public class BackRpcImpl implements BackRpc {

    @Autowired
    WorkflowService workflowService;

    @RequestMapping(value = "/canPub")
    public Map<Long,Boolean> canPub(final long uid, final Long[] modelIds){
        Map<Long,Boolean> map = C.newMap();
        for (long l : modelIds) {
            map.put(l,workflowService.canPubOrPoint(l,uid));
        }
        return map;
    }
}
