package com.beeasy.hzbpm.ctrl;

import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import org.beetl.sql.core.SQLReady;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.server.Vars.$get;

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

    public Result getUser(String oid){
        return Result.ok(sqlManager.execute(new SQLReady("SELECT u.id,u.ACC_CODE,u.PHONE,u.true_name as name FROM DB2INST1.T_USER_ORG o inner join t_user u on o.uid=u.id and o.oid="+oid),Obj.class));
    }


    public Result list(){
        List<Obj> list = sqlManager.execute(new SQLReady("select name as text, id, parent_id, type from t_org where type in ('QUARTERS', 'DEPARTMENT')"), Obj.class);
        return Result.ok(Json.tree(list, "parent_id", "id"));
    }

    public Result one(){
        return Result.ok(sqlManager.execute(new SQLReady("select * from t_org where id = ?", $get.s("id")), Obj.class));
    }


    public Object getUserByDept(String[] pid, String type){
        if(null == pid){
            return Result.ok();
        }
        switch (type) {
            case "d":
                List<String> pids = Arrays.asList(pid);
                String id = pids.stream().map(p -> "'" + p + "'").collect(Collectors.joining(","));
                return Result.ok(sqlManager.execute(new SQLReady(String.format("select * from t_org_user where pid in (%s)",pids.isEmpty() ? "-1": id)),Obj.class));
            case "r":
                List<String> rids = Arrays.asList(pid);
                String rid = rids.stream().map(r -> "'" + r + "'").collect(Collectors.joining(","));
                return Result.ok(sqlManager.execute(new SQLReady(String.format("select  id as uid,true_name as utname,acc_code as uname from t_user u where 1 = 1 and exists(select 1 from t_user_org where uid = u.id and oid in (%s)) ",rids.isEmpty() ? "-1": rid)),Obj.class));
                default:
                    return Result.ok();

        }
    }
}
