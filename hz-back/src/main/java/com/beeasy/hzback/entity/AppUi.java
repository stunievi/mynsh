package com.beeasy.hzback.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.List;

@Table(name = "T_APP_UI")
@Getter
@Setter
public class AppUi extends ValidGroup {
    @AssignID("simple")
    Long id;
    String key;
    String value;

    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        return null;
    }

    public Object set(SQLManager sqlManager, JSONObject object){
        sqlManager.lambdaQuery(AppUi.class).delete();
        JSONArray data = object.getJSONArray("data");
        List<AppUi> list = C.newList();
        for (Object datum : data) {
            list.add($.map(datum).to(AppUi.class));
        }
        sqlManager.insertBatch(getClass(), list);
        return null;
    }

    public Object get(SQLManager sqlManager, JSONObject object){
        return sqlManager.lambdaQuery(AppUi.class)
            .select();
    }

    public Object getOneByKey(SQLManager sqlManager, JSONObject object){
        String key = object.getString("key");
        Assert(S.notBlank(key), "");
        return sqlManager.lambdaQuery(AppUi.class)
            .andEq(AppUi::getKey, key)
            .select(AppUi::getValue)
            .stream()
            .findFirst()
            .map(AppUi::getValue)
            .orElse("");
    }
}
