package com.beeasy.hzback.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.ann.AssertMethod;
import com.beeasy.mscommon.entity.BeetlPager;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.query.LambdaQuery;
import org.hibernate.validator.constraints.Range;
import org.osgl.util.C;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.beeasy.hzback.entity.Org.Type.DEPARTMENT;
import static com.beeasy.hzback.entity.Org.Type.QUARTERS;

@Table(name = "T_ORG")
@Getter
@Setter
public class Org extends TailBean implements ValidGroup{

    @NotNull(message = "ID不能为空", groups = ValidGroup.Edit.class)
    @AssignID("simple")
    Long id;

    @NotEmpty(groups = {ValidGroup.Add.class})
    String          name;

    Long            parentId;
    String          accCode;
    String info;

    @NotNull(message = "类型不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    Type type;

    @NotNull(message = "排序不能为空", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    @Range(min = 0, max = 255, message = "排序应在0-255之间", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    Integer sort;

    Boolean manager = false;

    public enum Type {
        DEPARTMENT, QUARTERS, ROLE
    }

    /**
     * 开放所有权限
     * @param params
     * @return
     */
    @Override
    public String onGetListSql(Map<String, Object> params) {
        if(params.containsKey("type")){
            params.put("type", U.toList((String) params.get("type"), String.class));
        }
        return "user.查询组织机构列表";
    }

    /**
     * 开放所有权限
     * @param sqlManager
     * @param id
     * @return
     */
    @Override
    public String onGetOneSql(SQLManager sqlManager, Object id) {
        return "user.查询组织机构";
    }

    @Override
    public void onBeforeAdd(SQLManager sqlManager) {
        User.AssertMethod("系统管理.组织架构.部门管理", "系统管理.组织架构.岗位管理", "系统管理.组织架构.角色管理");
    }

    @Override
    public void onBeforeEdit(SQLManager sqlManager) {
        onBeforeAdd(sqlManager);
    }

    @AssertTrue(message = "已经有同名项", groups = {ValidGroup.Add.class, ValidGroup.Edit.class})
    protected boolean isValid(){
        SQLManager sqlManager = U.getSQLManager();
        LambdaQuery<Org> query = sqlManager.lambdaQuery(Org.class)
            .andEq(Org::getType,type)
            .andEq(Org::getName, name);
        if(type.equals(Type.ROLE)){
            //edit
            if(null != id){
                return query.andNotEq(Org::getId,id).count() == 0;
            }
            else{
                return query.count() == 0;
            }
        }
        else{
            query.andEq(Org::getParentId, parentId);
            //edit
            if(null != id){
                return query.andNotEq(Org::getId,id).count() == 0;
            }
            else{
                return query.count() == 0;
            }
        }
    }



    /****/

    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        switch (action){
            /**
             *
             */
            case "getSDQList":
                return sqlManager.lambdaQuery(Org.class)
                    .andIn(Org::getType, C.newList(DEPARTMENT,QUARTERS))
                    .select(Org::getId, Org::getParentId, Org::getName, Org::getType);

            /**
             * 查询部门列表
             * 默认开放所有权限
             */
            case "getDList":
                List<JSONObject> deps = sqlManager.select("user.查询部门列表",JSONObject.class);
                Map<Long,JSONObject> map = C.newMap();
                for (JSONObject dep : deps) {
                    map.put(dep.getLong("id"), dep);
                    dep.put("children", new JSONArray());
                }
                JSONArray arr = new JSONArray();
                for (JSONObject dep : deps) {
                    Long pid = dep.getLong("parentId");
                    if(null == pid || 0 == pid){
                        arr.add(dep);
                        continue;
                    }
                    if(!map.containsKey(pid)){
                        continue;
                    }
                    map.get(pid).getJSONArray("children").add(dep);
                }
                return arr;



        }

        return null;
    }

    /**
     * 设置数据权限
     * @param sqlManager
     * @param object
     * @return
     */
    @AssertMethod(value = {"系统管理.组织架构.角色管理"})
    public Object setDataP(SQLManager sqlManager, JSONObject object){
        //设置检索范围权限
        JSONObject condition = object.getJSONObject("condition");
        sqlManager.lambdaQuery(GP.class)
            .andEq(GP::getType, GP.Type.DATA_SEARCH_CONDITION)
            .andEq(GP::getOid, condition.getLong("oid"))
            .delete();
        if(condition.getInteger("checked") == 1){
            GP gp = new GP();
            gp.setOid(condition.getLong("oid"));
            gp.setType(GP.Type.DATA_SEARCH_CONDITION);
            gp.setObjectId(0L);
            sqlManager.insert(gp);
        }
        //设置数据字段权限
        JSONObject result = object.getJSONObject("result");
        JSONArray arr = result.getJSONArray("data");
        if(arr.size() == 0){
            return null;
        }
        Long oid = result.getLong("oid");
        List<GP> gps = arr.stream()
            .map(o -> (JSONObject)o)
            .map(o -> {
                GP gp = new GP();
                gp.setType(GP.Type.DATA_SEARCH_RESULT);
                gp.setObjectId(Long.parseUnsignedLong(o.getString("ename")));
                gp.setDescription(o.getString("items"));
                gp.setOid(oid);
                return gp;
            })
            .collect(Collectors.toList());
        //删除
        sqlManager.lambdaQuery(GP.class)
            .andEq(GP::getType, GP.Type.DATA_SEARCH_RESULT)
            .andEq(GP::getOid, gps.get(0).getOid())
            .delete();
        sqlManager.insertBatch(GP.class, gps);
        return null;
    }


    /**
     * 得到数据权限
     * @param sqlManager
     * @param object
     * @return
     */
    @AssertMethod(value = {"系统管理.组织架构.角色管理"})
    public Object getDataP(SQLManager sqlManager, JSONObject object){
        return C.newMap(
            "condition", sqlManager.lambdaQuery(GP.class).andEq(GP::getOid, object.getLong("oid")).andEq(GP::getType, GP.Type.DATA_SEARCH_CONDITION).count()
            , "result", sqlManager.lambdaQuery(GP.class)
                .andEq(GP::getOid, object.getLong("oid"))
                .andEq(GP::getType, GP.Type.DATA_SEARCH_RESULT)
                .select()
        );
    }
}
