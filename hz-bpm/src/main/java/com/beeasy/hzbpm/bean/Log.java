package com.beeasy.hzbpm.bean;

import com.github.llyb120.nami.core.Async;
import org.beetl.sql.core.SQLReady;

import java.util.Date;

import static com.beeasy.hzbpm.bean.Notice.snowflake;
import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;

public class Log {

    public static void log(String uid, String msg, Object ...args){
        msg = String.format(msg, args);
        String finalMsg = msg;
        Async.execute(() -> {
            sqlManager.executeUpdate(new SQLReady("insert into T_SYSTEM_LOG(id,add_time,method,user_id) values (?,?,?,?)", snowflake.nextId(), new Date(), finalMsg, uid));
        });

    }
}
