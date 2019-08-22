package com.beeasy.hzbpm;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import com.beeasy.hzbpm.bean.Notice;
import com.beeasy.hzbpm.bpm.Bpm;
import com.github.llyb120.nami.core.Nami;
import com.github.llyb120.nami.json.Arr;
import com.github.llyb120.nami.json.Obj;
import org.beetl.sql.core.SQLBatchReady;
import org.beetl.sql.core.SQLReady;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.json.Json.o;

public class TestBpmn {

    @Test
    public void test() throws FileNotFoundException {
        String str = IoUtil.read(new FileReader("/Users/bin/work/win/hznsh/hz-Bpm/src/main/resources/diagram (14).bpmn"));
        String json = IoUtil.read(new FileReader("/Users/bin/work/win/hznsh/hz-bpm/src/main/resources/test.json"));
        ;
        Bpm bpm = new Bpm();
        Node node = bpm.getStartNode(str);
        Assert.notNull(node);
        String id = bpm.getNodeAttribute(node, "id");
        Assert.notBlank(id);
//        bpm.getUsersFromNode(node, JSON.parseObject(json, new TypeReference<List<NodeExt>>(){}));
    }


    @Test
    public void test2() throws InterruptedException {
        Nami.dev();;
        Notice.sendSystem(Arrays.asList("522"), "oh shit","");

        Thread.sleep(10000);
    }


    @Test
    public void fixDep(){
        Nami.dev();
        List<Obj> list = sqlManager.execute(new SQLReady("select * from t_org_user where otype = 'QUARTERS'"), Obj.class);
        sqlManager.executeUpdate(new SQLReady("delete from t_user_dep"));
        Obj cache = o();
        for (Obj obj : list) {
            Arr uids = cache.a(obj.s("pid"));
            if (uids == null) {
                cache.put(obj.s("pid"), uids = a());
            }
            uids.add(obj.s("uid"));
        }

        SQLBatchReady ready = new SQLBatchReady("insert into t_user_dep(did,uid)values(?,?)");
        List<Object[]> args = new ArrayList<>();
        for (Map.Entry<String, Object> entry : cache.entrySet()) {
            //distinct
            List<String> uids = (List<String>) entry.getValue();
            uids = uids.stream()
            .distinct().collect(Collectors.toList());
            for (String uid : uids) {
                args.add(new Object[]{entry.getKey(), uid});
//                ready.setArgs();
//                sqlManager.executeBatchUpdate()
            }
        }
        ready.setArgs(args);
        sqlManager.executeBatchUpdate(ready);
    }
}
