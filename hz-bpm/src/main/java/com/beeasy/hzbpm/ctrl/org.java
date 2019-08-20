package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.StrUtil;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import org.beetl.sql.core.SQLReady;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.o;
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


    public Result list(String type){
        String sql;
        if(StrUtil.isBlank(type)){
            sql = "select name as text, id, parent_id, type from t_org where type in ('QUARTERS', 'DEPARTMENT')";
        } else {
            sql = String.format("select name as text, id, parent_id, type from t_org where type = '%s'", type);
        }
        List<Obj> list = sqlManager.execute(new SQLReady(sql), Obj.class);
        return Result.ok(Json.tree(list, "parent_id", "id"));
    }

    public Result one(){
        List<Obj> list = sqlManager.execute(new SQLReady("select * from t_org where id = ?", $get.s("id")), Obj.class);
        //附加关联的人
        Obj item = list.isEmpty() ? null : list.get(0);
        if (item == null) {
            return Result.ok(item);
        }
        item.put("us", sqlManager.select("workflow.查找部门人员-新版关联", Obj.class, o("id", item.s("id"))));
        return Result.ok(item);
    }

    public Object dus(String id){
        return Result.ok(
                sqlManager.select("workflow.查找部门人员", Obj.class, o("did", id))
        );
    }

    public Object rus(String id){
        return Result.ok(
                sqlManager.execute(new SQLReady("select uid,uname,utname,otype from t_org_user where oid = ?", id), Obj.class)
        );
    }

    /**
     * 乾坤大挪移
     *
     * @param id
     * @param pid
     * @return
     */
    public Object move(String id, String pid){
        int ret = sqlManager.executeUpdate(new SQLReady("update t_org set parent_id = ? where id = ?", pid, id));
        return Result.ok(ret > 0);
    }


}
