package com.beeasy.rpc;

import java.util.Date;

public interface DubboService {
    String autoStartTask(String dataSource, String dataId, String modelName, long dealerId, Date date);
}
