package com.beeasy.hzdata.module;

import act.Act;
import act.db.beetlsql.BeetlSqlService;
import org.beetl.sql.core.SQLManager;
import org.osgl.inject.Module;

public class BeetlSqlModule extends Module {
    @Override
    protected void configure() {
        registerGenericTypedBeanLoader(SQLManager.class, spec -> {
            BeetlSqlService beetlSqlService = Act.app().dbServiceManager().dbService("default");
            return beetlSqlService.beetlSql();
        });
    }
}
