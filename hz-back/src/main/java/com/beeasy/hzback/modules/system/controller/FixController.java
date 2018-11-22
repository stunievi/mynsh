package com.beeasy.hzback.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.modules.system.service.FileService;
import com.beeasy.hzback.entity.Org;
import com.beeasy.hzback.entity.User;
import com.beeasy.hzback.entity.WfModel;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Transactional
@RestController
@RequestMapping(value = "/api/fix")
public class FixController {
    @Autowired
    SQLManager  sqlManager;
    @Autowired
    FileService fileService;
    @Value("${uploads.avatar}")
    String      path;

    @RequestMapping(value = "/model")
    public String fixModel(){
        List<WfModel> modelList = sqlManager.query(WfModel.class).select();
        for (WfModel wfModel : modelList) {
            List<Map> list = sqlManager.execute(new SQLReady("select * from t_workflow_model_innate where model_id = " + wfModel.getId()),Map.class);
            List cnt = C.newList();
            for (Map map : list) {
                JSONObject object = JSON.parseObject((String) map.get("content"));
                cnt.add(object);
            }
            wfModel.setInnates(JSON.toJSONString(cnt));
            sqlManager.updateById(wfModel);
        }
        return "success";
    }

    @RequestMapping(value = "/avatar")
    public String fixUser() throws IOException {
        List<User> users = sqlManager.lambdaQuery(User.class).select();
        File face = new File(path, "face.jpg");
        for (User user : users) {
            MockMultipartFile file = new MockMultipartFile("face.jpg", new FileInputStream(face));
             fileService.uploadFace(user.getId(), file);
        }
        return "success";
    }

    @RequestMapping(value = "/cloud")
    public String fixCloud(){
        List<User> users = sqlManager.lambdaQuery(User.class).select();
        for (User user : users) {
            List<JSONObject> ps = sqlManager.execute(new SQLReady("select * from t_user_profile where user_id = " + user.getId()), JSONObject.class);
            if(0 == ps.size()){
                continue;
            }
            String uname = (String) ps.get(0).get("cloudUsername");
            String password = (String) ps.get(0).get("cloudPassword");
            if(S.empty(uname)){
                continue;
            }
            sqlManager.lambdaQuery(User.class)
                .andEq(User::getId, user.getId())
                .updateSelective(
                    C.newMap("cloudUsername", uname, "cloudPassword", password));
        }
        return "success";
    }


    @RequestMapping("/dep")
    public String fixDep(){
        sqlManager.lambdaQuery(Org.class)
            .delete();
        sqlManager.executeUpdate(new SQLReady("delete from t_org"));
        List<JSONObject> departments = sqlManager.execute(new SQLReady("select * from t_department"),JSONObject.class);
        List<String> values = C.newList();
        List<Org> list = C.newList();
        for (JSONObject department : departments) {
            Org org = $.map(department).to(Org.class);
            org.setType(Org.Type.DEPARTMENT);
            list.add(org);
        }
        //插入岗位
        List<JSONObject> quarters = sqlManager.execute(new SQLReady("select * from t_quarter"),JSONObject.class);
        for (JSONObject quarter : quarters) {
            Org org = $.map(quarter).to(Org.class);
            org.setId(org.getId() + 1000000);
            org.setType(Org.Type.QUARTERS);
            org.setParentId(quarter.getLong("departmentId"));
            list.add(org);
        }
        //插入岗位绑定
        List<JSONObject> objects = sqlManager.execute(new SQLReady("select * from t_user_quarters"), JSONObject.class);
        for (JSONObject object : objects) {
            long qid = object.getLong("quartersId");
            long uid = object.getLong("userId");
            values.add(String.format("(%d,%d)", uid,qid));
//            sqlManager.executeUpdate(new SQLReady(String.format("insert into t_user_org(uid,oid)values(%d,%d)", uid,qid + 1000000)));
        }

        //插入角色
        List<JSONObject> roles = sqlManager.execute(new SQLReady("select * from t_role"), JSONObject.class);
        for (JSONObject role : roles) {
            Org org = $.map(role).to(Org.class);
            org.setId(org.getId() + 2000000);
            org.setType(Org.Type.ROLE);
            list.add(org);
        }
        //插入角色绑定
        objects = sqlManager.execute(new SQLReady("select * from t_user_role"), JSONObject.class);
        for (JSONObject object : objects) {
            long qid = object.getLong("roleId");
            long uid = object.getLong("userId");
            values.add(String.format("(%d,%d)", uid,qid));
//            sqlManager.executeUpdate(new SQLReady(String.format("insert into t_user_org(uid,oid)values(%d,%d)", uid, qid + 2000000)));
        }
//        sqlManager.insert(list.get(0));
        sqlManager.insertBatch(Org.class, list);
        sqlManager.executeUpdate(new SQLReady(String.format("insert into t_user_org(uid,oid)values %s", S.join(",",values))));
        return "success";
    }
}
