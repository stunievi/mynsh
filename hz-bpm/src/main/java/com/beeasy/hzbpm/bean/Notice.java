package com.beeasy.hzbpm.bean;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.github.llyb120.nami.core.Async;
import org.beetl.sql.core.SQLReady;

import java.util.Date;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;

public class Notice {

    public static Snowflake snowflake = IdUtil.createSnowflake(0l,0l);

    public static void sendSystem(String uid, String msg, Object ...args){
        msg = String.format(msg, args);
        String finalMsg = msg;
        Async.execute(() -> {
            sqlManager.executeUpdate(new SQLReady("insert into T_SYSTEM_NOTICE(id,add_time,content,state,type,user_id) values (?,?,?,?,?,?)", snowflake.nextId(), new Date(), finalMsg, "UNREAD", "SYSTEM", uid));
        });
    }
}
