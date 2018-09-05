package com.beeasy.rpc;

import act.Act;
import act.db.beetlsql.BeetlSqlService;
import com.beeasy.hzdata.entity.User;
import org.beetl.sql.core.SQLManager;

import javax.inject.Inject;

public class DataServiceImpl implements DataService {
    @Inject
    SQLManager sqlManager;

    @Override
    public String foo() {
        return "";
//        return sqlManager.single(User.class,1).username;
    }
}
