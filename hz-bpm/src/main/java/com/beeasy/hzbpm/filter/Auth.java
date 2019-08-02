package com.beeasy.hzbpm.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.llyb120.nami.core.AopInvoke;
import com.github.llyb120.nami.core.R;
import com.github.llyb120.nami.json.Obj;
import com.github.llyb120.nami.server.Cookie;
import org.beetl.sql.core.SQLReady;

import java.util.List;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;


public class Auth {

    private static ThreadLocal<Long> uids = new ThreadLocal(){
        @Override
        protected Long initialValue() {
            return -1L;
        }
    };

    public Object around(AopInvoke invoke, Cookie cookie, Obj headers) throws Exception {
        uids.set(-1l);
        String token = cookie.get("authorization");
        if(StrUtil.isBlank(token)){
            token = headers.s("HZToken");
        }
        if (StrUtil.isBlank(token)) {
            return R.fail();
        }
        token = URLUtil.decode(token);
        List<Obj> objs = sqlManager.execute(new SQLReady(String.format("select user_id from t_user_token where token = '%s' fetch first 1 rows only", token)), Obj.class);
        if(objs.size() == 0){
            return R.fail();
        }
        uids.set((objs.get(0).l("user_id")));
        return invoke.call();
    }

    public static long getUid(){
        return uids.get();
    }
}
