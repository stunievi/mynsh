package com.beeasy.rpc;

import com.alibaba.dubbo.config.annotation.Service;
import com.beeasy.hzdata.entity.User;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class DataServiceImpl implements DataService {
    @Autowired
    SQLManager sqlManager;

    @Override
    public String foo() {
        return sqlManager.single(User.class,1579).getUsername();
    }
}
