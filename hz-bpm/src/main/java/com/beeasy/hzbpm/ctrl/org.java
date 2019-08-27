package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.beeasy.hzbpm.entity.Org;
import com.beeasy.hzbpm.exception.BpmException;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import org.beetl.sql.core.SQLBatchReady;
import org.beetl.sql.core.SQLReady;
import org.bson.types.ObjectId;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.o;
import static com.github.llyb120.nami.server.Vars.$get;
import static com.github.llyb120.nami.server.Vars.$request;

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
        List<Obj> users = sqlManager.select("workflow.查找部门人员-新版关联", Obj.class, o("id", item.s("id")));
        Obj us = o();
        Obj ms = o();
        Obj tms0 = o();
        Obj tms1 = o();
        for (Obj user : users) {
            switch (user.s("type")){
                case "USER":
                    us.put(user.s("uid"), user.s("utname"));
                    break;

                case"MANAGER":
                    ms.put(user.s("uid"), user.s("utname"));
                    break;

                case "TOP_MANAGER0":
                    tms0.put(user.s("uid"), user.s("utname"));
                    break;

                case "TOP_MANAGER1":
                    tms1.put(user.s("uid"), user.s("utname"));
                    break;
            }
        }
        item.put("us",us);
        item.put("ms",ms);
        item.put("tms0",tms0);
        item.put("tms1",tms1);
        return Result.ok(item);
    }

    public Object dus(String id){
        return Result.ok(
                sqlManager.select("workflow.查找部门人员-新版关联", Obj.class, o("id", id, "type", "USER"))
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

    public Object del(String id){
        //有下级部门或者岗位的禁止删除
        List<Obj> list = sqlManager.execute(new SQLReady("select count(1) from t_org where parent_id = ?", id), Obj.class);
        int count = list.get(0).i("1");
        if(count > 0){
            return Result.error("还有下级分支，无法删除");
        }
        sqlManager.deleteById(Org.class, id);
        return Result.ok();
    }


    public Object save(Org body){
        if(StrUtil.isBlank(body.name)){
            throw new BpmException("部门名不能为空");
        }
        body.type = "DEPARTMENT";
        if (body.id == null) {
            body.id = IdUtil.createSnowflake(0,0).nextId();
            sqlManager.insert(body);
        } else {
            sqlManager.lambdaQuery(Org.class)
                    .andEq("id", body.id)
                    .updateSelective(o(
                        "name", body.name,
                        "info", body.info,
                        "sort", body.sort
                    ));
        }
        //处理保存的人
        List<Object[]> args = new ArrayList<>();
        SQLBatchReady ready = new SQLBatchReady("insert into t_user_dep(did,uid,type)values(?,?,?)");
        String[] types = {"USER", "MANAGER", "TOP_MANAGER0", "TOP_MANAGER1"};
        String[] keys = {"us", "ms", "tms0", "tms1"};
        int i = 0;
        for (String type : types) {
            String key = keys[i++];
            if($request.containsKey(key)){
                Obj o = $request.o(key);
                args.addAll(o.keySet().stream()
                        .map(e -> new Object[]{body.id, e, type})
                        .collect(Collectors.toList()));
            }
        }
        sqlManager.executeUpdate(new SQLReady("delete from t_user_dep where did = ?", body.id));
        if(!args.isEmpty()){
            ready.setArgs(args);
            sqlManager.executeBatchUpdate(ready);
        }
        return Result.ok(body);
    }

    public Object users(String keyword){
        if(StrUtil.isEmpty(keyword)){
            keyword = "";
        }
        keyword = "%" + keyword + "%";
        return Result.ok(sqlManager.execute(new SQLReady("select id as uid,true_name as utname from t_user where username like ? or true_name like ? fetch first 30 rows only", keyword, keyword), Obj.class));
    }


//    public Object getUserByDept(String[] pid, String type){
//        if(null == pid){
//            return Result.ok();
//        }
//        switch (type) {
//            case "d":
//                List<String> pids = Arrays.asList(pid);
//                String id = pids.stream().map(p -> "'" + p + "'").collect(Collectors.joining(","));
//                return Result.ok(sqlManager.execute(new SQLReady(String.format("select * from t_org_user where pid in (%s)",pids.isEmpty() ? "-1": id)),Obj.class));
//            case "r":
//                List<String> rids = Arrays.asList(pid);
//                String rid = rids.stream().map(r -> "'" + r + "'").collect(Collectors.joining(","));
//                return Result.ok(sqlManager.execute(new SQLReady(String.format("select  id as uid,true_name as utname,acc_code as uname from t_user u where 1 = 1 and exists(select 1 from t_user_org where uid = u.id and oid in (%s)) ",rids.isEmpty() ? "-1": rid)),Obj.class));
//                default:
//                    return Result.ok();
//
//        }
//    }
}
