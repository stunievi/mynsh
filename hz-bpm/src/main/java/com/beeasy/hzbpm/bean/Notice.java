package com.beeasy.hzbpm.bean;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.github.llyb120.nami.core.Async;
import com.github.llyb120.nami.json.Json;
import org.beetl.sql.core.SQLBatchReady;
import org.beetl.sql.core.SQLReady;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.o;

public class Notice {

    public static Snowflake snowflake = IdUtil.createSnowflake(0l,0l);

    public static void sendSystem(String uid, String msg, String id){
        Async.execute(() -> {
            String finalMsg = String.format(msg);
            sqlManager.executeUpdate(new SQLReady("insert into T_SYSTEM_NOTICE(id,add_time,content,state,type,user_id,bind_data) values (?,?,?,?,?,?,?)", snowflake.nextId(), new Date(), finalMsg, "UNREAD", "BPM-MESSAGE", uid,Json.stringify(o("id", id))));
        });
    }

    public static void sendSystem(Collection<String> uids, String msg, String id){
        Async.execute(() -> {
            String finalMsg = String.format(msg);
            List<Object[]> params = uids.stream()
                    .map(uid -> new Object[]{snowflake.nextId(), new Date(), finalMsg, "UNREAD", "BPM-MESSAGE", uid, Json.stringify(o("id", id))})
                    .collect(Collectors.toList());
            SQLBatchReady batch = new SQLBatchReady("insert into T_SYSTEM_NOTICE(id,add_time,content,state,type,user_id,bind_data) values (?,?,?,?,?,?,?)", params);
            sqlManager.executeBatchUpdate(batch);
        });

    }
}
