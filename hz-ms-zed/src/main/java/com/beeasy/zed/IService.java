package com.beeasy.zed;

public interface IService {
    void await();
    void initAsync();
    void initSync();
    void destroy();
}
