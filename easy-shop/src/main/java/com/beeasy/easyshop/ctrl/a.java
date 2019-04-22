package com.beeasy.easyshop.ctrl;

import com.alibaba.fastjson.JSONObject;
import org.beetl.sql.core.SQLReady;

import java.util.List;
import java.util.Map;

import static com.beeasy.easyshop.core.DBService.sqlManager;
public class a {

    public static class d{
        public int c;
    }

    public String test(
        d query,
        int c,
        int[] d
    ) {
        List<JSONObject> list = sqlManager.execute(new SQLReady("select count(1) from ra_member "), JSONObject.class);
        return "source word " + new b().word;
    }
}




