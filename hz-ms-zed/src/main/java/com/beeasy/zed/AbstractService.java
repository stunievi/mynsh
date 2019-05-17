package com.beeasy.zed;

import cn.hutool.core.thread.ThreadUtil;

import java.util.concurrent.Future;

public abstract class AbstractService implements IService{
    protected Future inited = null;

    @Override
    public void initAsync() {
        inited = ThreadUtil.execAsync((Runnable) this::initSync);
    }

    @Override
    public void await(){
        if (inited == null) {
            return;
        }
        try{
            inited.get();
        }
        catch (Exception e){
        }
    }

    @Override
    public void destroy(){ }

}
