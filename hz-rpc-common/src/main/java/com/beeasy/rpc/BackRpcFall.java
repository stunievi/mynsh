package com.beeasy.rpc;

import org.osgl.util.C;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class BackRpcFall implements BackRpc {
    @Override
    public Map<Long,Boolean> canPub(long uid, Long[] modelIds) {
        Map<Long,Boolean> map = C.newMap();
        for (long modelId : modelIds) {
            map.put(modelId,false);
        }
        return map;
    }
}
