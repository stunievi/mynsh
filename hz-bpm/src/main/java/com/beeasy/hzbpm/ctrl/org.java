package com.beeasy.hzbpm.ctrl;

import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import org.beetl.sql.core.SQLReady;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;

public class org {


    public Result all(String type){
        List<Obj> list;
        switch (type){
            case "q":
                list = sqlManager.execute(new SQLReady("select name as text, full_name, id, parent_id,type from t_org_ext where type = 'QUARTERS' or type = 'DEPARTMENT'" ), Obj.class);
                Json tree = Json.tree(list,"parent_id", "id");
                return Result.ok(tree);

            case "r":
                list = sqlManager.execute(new SQLReady("select name as text, id, parent_id,type from t_org where type = 'ROLE'" ), Obj.class);
                return Result.ok(list);

            case "d":
                list = sqlManager.execute(new SQLReady("select name as text, id, parent_id,type from t_org where type = 'DEPARTMENT'" ), Obj.class);
                tree = Json.tree(list, "parent_id", "id");
                return Result.ok(tree);
        }
        return null;
    }


}
