package com.beeasy.hzback.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.ext.SnowflakeIDWorker;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static com.beeasy.mscommon.valid.ValidGroup.*;

@Table(name = "T_WORKFLOW_MODEL")
@Getter
@Setter
public class WfModel extends TailBean implements ValidGroup {

    @Null(groups = ValidGroup.Add.class)
    @NotNull(groups = ValidGroup.Edit.class)
    Long id;

    @NotEmpty(message = "模型名不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    String name;

    Boolean open;
    Boolean firstOpen;
    Boolean manual;
    Boolean custom;
    Date    lastModifyTime;

    String info;

    @NotEmpty(message = "模型原型不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    String modelName;
    String innates;
    String model;
    String startNodeName;

    @AssertTrue(message = "该名字已被占用", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    protected boolean getZValidName(){
        SQLManager sqlManager = U.getSQLManager();
        LambdaQuery<WfModel> query = sqlManager.lambdaQuery(WfModel.class)
            .andEq(WfModel::getName, name);
        lastModifyTime = new Date();
        if(null == id){
            open = false;
            firstOpen = false;
            manual = false;
            custom = true;
            return query.count() == 0;
        }
        else{
            return query.andNotEq(WfModel::getId,id).count() == 0;
        }
    }

    /****************/





    @Override
    public String onGetListSql(Map<String,Object> params) {
        return "workflow.selectModels";
    }

    @Override
    public Object onAdd(SQLManager sqlManager) {
        Pattern RegexpGo = Pattern.compile("\\s*go\\([\'\"](.+?)[\'\"]\\)\\s*");
        WfModel model = this;
        ClassPathResource resource = new ClassPathResource("config/workflow.yml");
        Map object = C.newMap();
        try(
            InputStream is = resource.getInputStream();
        ){
            Yaml yaml = new Yaml();
            object = yaml.load(is);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Map m = (Map)object.get(model.getModelName());
        if(null == m){
            throw new RestException("找不到这个原型");
        }
        model.setLastModifyTime(new Date());
        //flow
        Map<String,Map> flow = (Map) m.get("flow");
        List<Map> flows = C.newList();
        List<Map> innates = C.newList();
        //start
        flow.forEach((k,v) -> {
            boolean isStart = Objects.equals(v.get("start"), true);
            if(isStart){
                model.setStartNodeName(k);
            }
            v.put("name", k);
            //fix input
            if(Objects.equals(v.get("type"), "input")){
                Map<String,Object> content = (Map<String, Object>) v.get("content");
                List<Map> fields = C.newList();
                content.forEach((kk,vvv) -> {
                    if(vvv instanceof String){
                        return;
                    }
                    Map vv = (Map)vvv;
                    vv.put("ename", vv.get("name"));
                    vv.put("cname", kk);
                    vv.remove("name");
                    if(Objects.equals(vv.get("innate"),true)){
                        innates.add(vv);
                    }
                    else{
                        fields.add(vv);
                    }
                });
                v.put("content", fields);
            }
            //fix check
            if(Objects.equals(v.get("type"), "check")){
                List<Map> states = (List<Map>) v.get("states");
                for (Map state : states) {
                    if(state.containsKey("behavior")){
                        String behavior = (String) state.get("behavior");
                        Matcher matcher = RegexpGo.matcher(behavior.trim());
                        if(matcher.find()){
                            state.put("go", matcher.group(1));
                            state.remove("behavior");
                        }
                    }
                }
            }
            flows.add(v);
        });
        //nullable
        model.setInnates("");
        model.setModel(JSON.toJSONString(C.newMap(
            "innates", innates,
            "flow", flows
        )));
        sqlManager.insert(model, true);

        //更新授权
        List<GP> gps = C.newList();
        flow.forEach((k,v) -> {
            List<String> deal = (List<String>) v.get("deal");
            if(null == deal){
                return;
            }
            String keys = deal.stream()
                .map(d -> "%" + d.replaceAll(",|，", "%") + "%")
                .map(s -> S.fmt("full_name like '%s'", s))
                .collect(joining(" or "));
            if(S.blank(keys)){
                return;
            }
            List<Long> ids = sqlManager.execute(new SQLReady("select id from t_org_ext where (" + keys + ")"), JSONObject.class).stream().map(o -> o.getLong("id")).collect(toList());
            if(ids.size() == 0){
                return;
            }
            gps.addAll(setPermissions(model.getId(), (String) v.get("name"), GP.Type.WORKFLOW_MAIN_QUARTER, ids, false));
        });

        if(gps.size() > 0){
            sqlManager.insertBatch(GP.class, gps);
        }

        //输出的时候不显示模型
        model.setModel(null);
        return model;
    }

    @Override
    public Object onEdit(SQLManager sqlManager) {
        return sqlManager.lambdaQuery(getClass())
            .andEq("id", id)
            .updateSelective(this);
    }


    /**
     *
     * @param sqlManager
     * @param action
     * @param object
     * @return
     */
    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        switch (action){

            //增加节点
            case "addCheckNode":
                createCheckNode(sqlManager, object.getLong("id"), object.getString("sname"), object.getJSONObject("node"));
                break;

            //删除节点
            case "deleteNode":
                deleteNode(sqlManager, object.getLong("id"), object.getString("name"));
                break;

            //得到可处理的模型
            case "getForPub":
                return getForPub(sqlManager, object.getString("modelName"));

            case "setOpen":
                //停止所有这个名字的任务
                List<WfModel> models = sqlManager.lambdaQuery(WfModel.class)
                    .andEq(WfModel::getId, object.getLong("id"))
                    .select("model_name");
                sqlManager.lambdaQuery(WfModel.class)
                    .andEq(WfModel::getModelName, models.get(0).getModelName())
                    .updateSelective(C.newMap("open",false));

                //停止指定任务
                sqlManager.lambdaQuery(WfModel.class)
                    .andEq(WfModel::getId, object.getLong("id"))
                    .updateSelective(C.newMap("open", object.getInteger("open"),"firstOpen", object.getInteger("open")));
                break;
        }
        return null;
    }

    private Map getForPub(SQLManager sqlManager, String modelName){
        Map map = sqlManager.lambdaQuery(WfModel.class)
            .andEq(WfModel::getModelName, modelName)
            .andEq(WfModel::getOpen,true)
            .mapSingle();
        Assert(null != map, "找不到可以发布的任务模型");
        map.put("autoId", U.getBean(SnowflakeIDWorker.class).nextId());
        map.put("dealers", sqlManager.select("workflow.查询任务发布权限(通过UID)", JSONObject.class, C.newMap("mid", map.get("id"), "nname", map.get("startNodeName"), "uid", AuthFilter.getUid())));
        return map;
    }

    /**
     *
     * @param sqlManager
     * @param id
     * @param sname
     * @param node
     */
    private void createCheckNode(SQLManager sqlManager, long id, String sname, JSONObject node){
        String name = node.getString("name");
        Assert(S.notBlank(name), "没有设置节点名字");
        WfModel wfModel = sqlManager.unique(WfModel.class, id);
        JSONObject model = JSON.parseObject(wfModel.getModel());
        JSONArray flow = model.getJSONArray("flow");
        flow.removeIf(i -> S.eq(((JSONObject)i).getString("name"), sname));
        flow.removeIf(i -> S.eq(((JSONObject)i).getString("name"), name));
        flow.add(node);
        wfModel.setModel(JSON.toJSONString(model));
        sqlManager.updateById(wfModel);
    }

    /**
     *
     * @param sqlManager
     * @param id
     * @param name
     */
    private void deleteNode(SQLManager sqlManager, Long id, String name){
        WfModel wfModel = sqlManager.unique(WfModel.class, id);
        JSONObject model = JSON.parseObject(wfModel.getModel());
        JSONArray flow = model.getJSONArray("flow");
        flow.removeIf(i -> S.eq(((JSONObject)i).getString("name"), name));
        wfModel.setModel(JSON.toJSONString(model));
        sqlManager.updateById(wfModel);
    }

    /**
     *
     * @param objectId
     * @param k1
     * @param type
     * @param oids
     * @param change
     * @return
     */
    private List<GP> setPermissions(long objectId, String k1, GP.Type type, List<Long> oids, boolean change){
        SQLManager sqlManager = U.getSQLManager();
        if(change){
            sqlManager.lambdaQuery(GP.class)
                .andEq(GP::getType, type)
                .andEq(GP::getObjectId, objectId)
                .andEq(GP::getK1, k1)
                .delete();
        }
        List<GP> list = (oids).stream()
            .map(oid -> {
                GP gp = new GP();
                gp.setK1(k1);
                gp.setObjectId(objectId);
                gp.setType(type);
                gp.setOid((oid));
                return gp;
            })
            .collect(Collectors.toList());
        if(change){
            sqlManager.insertBatch(GP.class, list);
        }
        return list;
    }

    //    @Getter
//    @Setter
//    public static class CheckNode{
//        String       name;
//        String questtion;
//        List<String> next;
//        Integer count;
//        List<CheckNodeState> state;
//    }
//
//    @Setter
//    @Getter
//    public static class CheckNodeState{
//        Integer condition;
//        String item;
//        String behavior;
//        String go;
//    }
}
