package com.beeasy.hzback.modules.system.task;

import com.beeasy.hzback.core.exception.RestException;

import java.util.Map;

public interface ITask {

    public void doTask(String key, Map params) throws RestException;
}
