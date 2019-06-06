package com.beeasy.hzback.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.valid.ValidGroup;
import com.beeasy.tool.OkHttpUtil;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.util.C;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Table(name = "T_GATE")
public class Gate extends ValidGroup {
    @AssignID("simple")
    Long id;
    String type;
    String name;
    String config;
    String info;


    @Override
    public String onGetListSql(Map<String, Object> params) {
        return "gate.查询网关列表";
    }


    @Override
    public void onBeforeAdd(SQLManager sqlManager) {
        Assert(id == null);
        AssertNotEmpty(name, "主机名不能为空");
        AssertNotEmpty(type, "主机类型不能为空");
    }


    public Object reload(SQLManager sqlManager, JSONObject object){
        List<Gate> list = sqlManager.lambdaQuery(Gate.class).select();
        JSONObject body = new JSONObject();
        JSONArray servers = new JSONArray();
        body.put("servers", servers);
        for (Gate gate : list) {
            JSONObject config = JSON.parseObject(gate.getConfig());
            for (Object locations : config.getJSONArray("locations")) {
                JSONObject location = (JSONObject) locations;
                if (location != null) {
                    location.put("index", location.getString("index").split("\\s+"));
                }
            }
            servers.add(config);
        }

        try {
            String res = OkHttpUtil.request("post", "http://127.0.0.1:24434/api/reloadJSON", C.newMap(), C.newMap(), body.toJSONString());
            AssertEq(res, "success");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RestException();
    }
}
