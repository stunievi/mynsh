package com.beeasy.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "back-rpc-service", url = "${rpc.url}", configuration = RPCConfig.class,fallback = BackRpcFall.class)
public interface BackRpc {

    @RequestMapping(value = "/rpc/back/canPub", method = RequestMethod.GET)
    Map<Long,Boolean> canPub(
            @RequestParam(value = "uid") final long uid,
            @RequestParam(value = "modelIds") final Long[] modelId);

}
