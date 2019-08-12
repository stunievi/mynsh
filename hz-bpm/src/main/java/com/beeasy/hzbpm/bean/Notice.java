package com.beeasy.hzbpm.bean;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.github.llyb120.nami.core.Async;
import org.beetl.sql.core.SQLBatchReady;
import org.beetl.sql.core.SQLReady;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;

public class Notice {

    public static Snowflake snowflake = IdUtil.createSnowflake(0l,0l);

    public static void sendSystem(String uid, String msg, Object ...args){
        Async.execute(() -> {
            String finalMsg = String.format(msg, args);
            sqlManager.executeUpdate(new SQLReady("insert into T_SYSTEM_NOTICE(id,add_time,content,state,type,user_id) values (?,?,?,?,?,?)", snowflake.nextId(), new Date(), finalMsg, "UNREAD", "SYSTEM", uid));
        });
    }

    public static void sendSystem(Collection<String> uids, String msg, Object ...args){
        Async.execute(() -> {
            String finalMsg = String.format(msg, args);
            List<Object[]> params = uids.stream()
                    .map(uid -> new Object[]{snowflake.nextId(), new Date(), finalMsg, "UNREAD", "SYSTEM", uid})
                    .collect(Collectors.toList());
            SQLBatchReady batch = new SQLBatchReady("insert into T_SYSTEM_NOTICE(id,add_time,content,state,type,user_id) values (?,?,?,?,?,?)", params);
            sqlManager.executeBatchUpdate(batch);
        });

    }
}
